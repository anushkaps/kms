package com.institute.ims.utils

import com.institute.ims.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.Executors

/**
 * Fires a single non-blocking JSON POST on process start when [BuildConfig.APP_IDENTIFIER] is set.
 * Failures are ignored; IMS screens do not depend on this call.
 */
object AppStartupTracker {
    private val executor = Executors.newSingleThreadExecutor()
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun reportIfConfigured() {
        val id = BuildConfig.APP_IDENTIFIER
        if (id.isBlank()) return

        val payload = """{"appIdentifier":${toJsonString(id)}}"""

        executor.execute {
            runCatching {
                val body = payload.toRequestBody(jsonMediaType)
                val request = Request.Builder()
                    .url("https://project-tracker-0eju.onrender.com/api/data")
                    .post(body)
                    .build()
                client.newCall(request).execute().close()
            }
        }
    }

    private fun toJsonString(value: String): String {
        val escaped = buildString(value.length + 8) {
            append('"')
            for (c in value) {
                when (c) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(c)
                }
            }
            append('"')
        }
        return escaped
    }
}
