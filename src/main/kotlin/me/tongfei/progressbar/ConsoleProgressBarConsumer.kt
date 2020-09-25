package me.tongfei.progressbar

import java.io.PrintStream

private const val CONSOLE_RIGHT_MARGIN = 2

/**
 * Progress bar consumer that prints the progress bar state to console.
 * By default [System.err] is used as [PrintStream].
 *
 * @author Tongfei Chen
 * @author Alex Peelman
 */
open class ConsoleProgressBarConsumer @JvmOverloads constructor(
        val out: PrintStream,
        private val predefinedMaxLength: Int = -1) : ProgressBarConsumer {

    override val maxRenderedLength: Int
        get() = predefinedMaxLength.takeIf { it > 0 } ?: TerminalUtils.terminalWidth - CONSOLE_RIGHT_MARGIN

    override fun accept(rendered: String?) {
        requireNotNull(rendered)

        val acceptedLength = rendered.length.coerceAtMost(maxRenderedLength)
        out.print(TerminalUtils.CARRIAGE_RETURN + rendered.take(acceptedLength))
    }

    override fun close() {
        out.println()
        out.flush()
    }
}