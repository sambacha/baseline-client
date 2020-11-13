package org.baseline.client.process


interface AbstractBaselineProcess {
    val process: Process?
    val port: Int

    fun isAlive(): Boolean

    fun stop()
}
