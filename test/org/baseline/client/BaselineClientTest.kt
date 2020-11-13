package org.baseline.client

import org.junit.Test
import java.io.File
import java.nio.file.Files


class BaselineClientTest {

    @Test
    fun `should correctly start baseline server` () {
        BaselineClient.startServer(".")

        assert(BaselineClient.server!!.isAlive())
        BaselineClient.stopServer()
    }

    @Test
    fun `should correctly stop baseline server` () {
        BaselineClient.startServer(".")
        val process = BaselineClient.server!!

        BaselineClient.stopServer()

        waitToStop(process.process)
        assert(!process.isAlive())
    }

    @Test
    fun `should correctly verify solidity file` () {
        val client = BaselineClient
        client.startServer(".")
        val file = File.createTempFile("test", ".sol")
        Files.write(file.toPath(), arrayListOf("pragma solidity ^4.1.1"))

        Thread.sleep(3000)
        val result = client.fileErrors(".", file.absolutePath)

        assert(result.size == 1)
        client.stopServer()
    }

    fun waitToStop(process: Process?) {
        while (process != null && process.isAlive) {
            // wait
        }
    }

}