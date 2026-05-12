package com.gbz.combat.shared.packet

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

internal object PacketBufferIO {
    fun <T> write(writer: DataOutputStream.() -> T): ByteArray {
        val out = ByteArrayOutputStream()
        DataOutputStream(out).use { stream ->
            stream.writer()
        }
        return out.toByteArray()
    }

    fun <T> read(bytes: ByteArray, reader: DataInputStream.() -> T): T {
        return DataInputStream(ByteArrayInputStream(bytes)).use { stream ->
            stream.reader()
        }
    }
}
