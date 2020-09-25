package me.tongfei.progressbar

import java.io.PrintStream

/**
 * Progress bar consumer for terminals supporting moving cursor up/down.
 * @since 0.9.0
 * @author Martin Vehovsky
 */
class InteractiveConsoleProgressBarConsumer(out: PrintStream) : ConsoleProgressBarConsumer(out) {
    private var initialized = false
    private var position = 1

    override fun accept(rendered: String?) {
        if (!initialized) {
            TerminalUtils.filterActiveConsumers(InteractiveConsoleProgressBarConsumer::class.java).forEach { it.position++ }
            TerminalUtils.activeConsumers.add(this)
            out.println(TerminalUtils.CARRIAGE_RETURN + rendered)
            initialized = true
        } else
            out.print(TerminalUtils.moveCursorUp(position) + rendered + TerminalUtils.moveCursorDown(position))
    }

    override fun close() {
        out.flush()
        TerminalUtils.activeConsumers.remove(this)
    }
}