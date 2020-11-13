package org.baseline.client

import com.google.gson.Gson
import com.intellij.openapi.diagnostic.Logger
import org.baseline.client.process.AbstractBaselineProcess
import org.baseline.common.IoStreams.toByteArray
import org.baseline.env.Environment
import org.baseline.errors.ErrorList
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset.forName


object BaselineClient {
    val LOG = Logger.getInstance(BaselineClient::class.java)

    val gson = Gson()
    var server: AbstractBaselineProcess? = null

    fun startServer(projectDir: String): BaselineClient =
        if (server == null || !server!!.isAlive()) {
            server = Environment.BaselineServer(projectDir)
            this
        } else {
            this
        }

    fun fileErrors(projectDir: String, filePath: String): ErrorList =
        startServer(projectDir)
            .verifyFile(filePath)
            .toErrorList()

    fun verifyFile(filePath: String) =
        try {
            URL("http", "127.0.0.1", server!!.port, "?filePath=${urlEncode(filePath)}")
                .openConnection()
                .inputStream
                .use {
                    String(toByteArray(it), forName("utf-8"))
                }
        } catch (e: Exception) {
            LOG.warn("Can not connect to Baseline Validation server", e)

            "[]"
        }

    fun stopServer() =
        server?.stop()

    fun String.toErrorList(): ErrorList =
        gson.fromJson<ErrorList>(this, ErrorList::class.java)

    private fun urlEncode(value: String) =
        URLEncoder.encode(value, "utf-8")
}