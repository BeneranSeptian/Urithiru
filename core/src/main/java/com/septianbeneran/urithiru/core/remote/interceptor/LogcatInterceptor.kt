package com.septianbeneran.urithiru.core.remote.interceptor

import android.util.Log
import com.septianbeneran.urithiru.core.util.extension.asPrettyJson
import com.septianbeneran.urithiru.core.util.extension.ellipsizeCenter
import com.septianbeneran.urithiru.core.util.extension.isProbablyUtf8
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
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
        return headers.filter { it.second.isNotEmpty() }.map { "${it.first}: ${it.second.ellipsizeCenter(maxLogLength)}" }
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
        appendLine("| ${title.ellipsizeCenter(maxLogLength).padEnd(maxLogLength)} |")
        appendLine(border)
        lines.forEach { line -> appendLine("| ${line.ellipsizeCenter(maxLogLength).padEnd(maxLogLength)} |") }
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
            appendLine("| ${endPoint.ellipsizeCenter(maxLogLength).padEnd(maxLogLength)} |")
            appendSection("HEADERS", headers)
            appendSection("REQUEST", requestBody.lines())
            appendSection("RESPONSE $responseResult", responseBody.lines())
            appendLine(border)
        })
    }
}