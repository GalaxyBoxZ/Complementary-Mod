package com.gbz.combat.client.config

import com.gbz.combat.client.util.GbzCombatConstants.LOGGER
import java.io.Closeable
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.io.path.name

class ConfigHotReloadService(
    private val repository: AnimationConfigRepository,
    private val onReload: (ConfigState) -> Unit
) : Closeable {
    private val running = AtomicBoolean(false)
    private var watchService: WatchService? = null

    fun start() {
        if (!running.compareAndSet(false, true)) return

        val parent = repository.configPath.parent
        val watcher = parent.fileSystem.newWatchService()
        parent.register(
            watcher,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY
        )
        watchService = watcher

        thread(start = true, isDaemon = true, name = "gbzcombat-config-watch") {
            while (running.get()) {
                val key: WatchKey = try {
                    watcher.take()
                } catch (_: InterruptedException) {
                    break
                } catch (_: java.nio.file.ClosedWatchServiceException) {
                    break
                }

                var shouldReload = false
                for (event in key.pollEvents()) {
                    val changed = event.context() as? java.nio.file.Path ?: continue
                    if (changed.name == repository.configPath.fileName.name) {
                        shouldReload = true
                    }
                }

                if (shouldReload) {
                    try {
                        Thread.sleep(75L)
                        onReload(repository.reload())
                    } catch (exception: Exception) {
                        LOGGER.error("Failed to hot reload GBZ combat config", exception)
                    }
                }

                key.reset()
            }
        }
    }

    override fun close() {
        running.set(false)
        watchService?.close()
    }
}
