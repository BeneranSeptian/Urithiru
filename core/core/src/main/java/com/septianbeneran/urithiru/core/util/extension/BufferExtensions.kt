package com.septianbeneran.urithiru.core.util.extension

import okio.Buffer
import java.io.EOFException

/** True when the start of the buffer looks like UTF-8 text rather than binary data. */
fun Buffer.isProbablyUtf8(): Boolean {
    return try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64L)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) break
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) return false
        }
        true
    } catch (_: EOFException) {
        false
    }
}
