package com.septianbeneran.urithiru.core.remote.interceptor

import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.EOFException
import java.nio.charset.StandardCharsets.UTF_8

class LogcatInterceptor : Interceptor {

    private val maxLogLength = 100
    private val maxLogcatEntryLength = 4000
    private val border = "-".repeat(maxLogLength + 4)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (t: Throwable) {
            logPretty(
                endPoint = "${request.method} ${request.url}",
                headers = logHeaders(request.headers),
                requestBody = logRequest(request),
                responseBody = "FAILED: ${t.stackTraceToString()}"
            )
            throw t
        }

        logPretty(
            endPoint = "${request.method} ${request.url}",
            headers = logHeaders(request.headers),
            requestBody = logRequest(request),
            responseBody = logResponse(response),
            responseResult = "${response.code} ${response.message}"
        )

        return response
    }

    private fun logHeaders(headers: Headers): List<String> {
        return headers.filter { it.second.isNotEmpty() }.map { "${it.first}: ${it.second.ellipsizeCenter()}" }
    }

    private fun logRequest(request: Request): String {
        val reqBody = request.body

        return if (reqBody == null) "empty request body"
        // Writing these to a buffer would consume them, so the real request would be sent without a body
        else if (reqBody.isOneShot() || reqBody.isDuplex()) "one-shot or duplex request body omitted"
        else {
            val buffer = Buffer()
            reqBody.writeTo(buffer)

            val contentType = reqBody.contentType()
            val charset = contentType?.charset(UTF_8) ?: UTF_8

            if (buffer.isProbablyUtf8()) buffer.clone().readString(charset).asPrettyJson()
            else "request body is not UTF-8"
        }
    }

    // OkHttp 5 never returns a null body; a response without one gets an empty body instead
    private fun logResponse(response: Response): String {
        val resBody = response.body
        val source = resBody.source()
        source.request(Long.MAX_VALUE)
        val buffer = source.buffer
        val charset = resBody.contentType()?.charset(UTF_8) ?: UTF_8

        return if (buffer.size == 0L) "empty response body"
        else if (buffer.isProbablyUtf8().not()) "binary ${buffer.size}-byte body omitted"
        else buffer.clone().readString(charset).asPrettyJson()
    }

    private fun Buffer.isProbablyUtf8(): Boolean {
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

    private fun String.ellipsizeCenter(): String {
        if (this.length <= maxLogLength) return this

        val halfLength = (maxLogLength - 5) / 2
        val start = this.substring(0, halfLength)
        val end = this.substring(this.length - halfLength)

        return "$start ... $end"
    }

    private fun String.asPrettyJson(): String {
        val trimmed = trim()
        return try {
            when {
                trimmed.startsWith("{") -> JSONObject(trimmed).toString(2)
                trimmed.startsWith("[") -> JSONArray(trimmed).toString(2)
                else -> this
            }
        } catch (_: JSONException) {
            this
        }
    }

    private fun log(text: String) {
        if (text.isBlank()) return

        val chunk = StringBuilder()
        text.lineSequence().forEach { line ->
            if (chunk.length + line.length + 1 > maxLogcatEntryLength && chunk.isNotEmpty()) {
                Log.d("LogcatInterceptor", chunk.toString())
                chunk.clear()
            }
            chunk.appendLine(line)
        }
        if (chunk.isNotBlank()) Log.d("LogcatInterceptor", chunk.toString())
    }

    private fun StringBuilder.appendSection(title: String, lines: List<String>) {
        appendLine(border)
        appendLine("| ${title.ellipsizeCenter().padEnd(maxLogLength)} |")
        appendLine(border)
        lines.forEach { line -> appendLine("| ${line.ellipsizeCenter().padEnd(maxLogLength)} |") }
    }

    private fun logPretty(
        endPoint: String,
        headers: List<String>,
        requestBody: String,
        responseBody: String,
        responseResult: String = ""
    ) {
        log(buildString {
            appendLine(border)
            appendLine("| ${endPoint.ellipsizeCenter().padEnd(maxLogLength)} |")
            appendSection("HEADERS", headers)
            appendSection("REQUEST", requestBody.lines())
            appendSection("RESPONSE $responseResult", responseBody.lines())
            appendLine(border)
        })
    }
}