package org.baseline.client.process


class EmptyProcess() : AbstractBaselineProcess {
    override val process = null
    override val port = 55000

    override fun isAlive() = false

    override fun stop() {
        // noop
    }
}