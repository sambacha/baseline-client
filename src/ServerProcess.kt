package org.baseline.client.process

import org.baseline.common.IoStreams.copy
import java.io.File
import java.io.InputStream
import java.util.*


class ServerProcess(val nodePath: String, val baselinePath: String, val baseDir: String) : AbstractbaselineProcess {
    override val port = 55000 + Random().nextInt(1000)
    override val process = start()

    fun start(): Process? =
        ProcessBuilder()
            .directory(File(baseDir))
            .command(nodePath, serverCodeFile(), baselinePath, port.toString())
            .start()
            .killOnShutdown()

    override fun stop() {
        process!!.destroyForcibly()
    }

    override fun isAlive() =
        process != null && process.isAlive

    private fun serverCodeFile() =
        File
            .createTempFile("baseline-server", ".js")
            .writeFrom(javaClass.getResourceAsStream("baseline-server.js"))
            .trashOnExit()
            .absolutePath

    private fun File.writeFrom(inputStream: InputStream): File {
        copy(inputStream, this.outputStream())
        return this
    }

    private fun File.trashOnExit(): File {
        this.deleteOnExit()
        return this
    }

    private fun Process.killOnShutdown(): Process {
        Runtime.getRuntime().addShutdownHook(Thread({ this.destroyForcibly() }))
        return this
    }
}