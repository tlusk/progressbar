package me.tongfei.progressbar

import java.util.function.Consumer

/**
 * Progress bar consumer that delegates the progress bar handling to a custom [Consumer].
 * @author Alex Peelman
 * @since 0.8.0
 */
class DelegatingProgressBarConsumer @JvmOverloads constructor(
        private val consumer: Consumer<String?>,
        override val maxRenderedLength: Int = TerminalUtils.terminalWidth) : ProgressBarConsumer {
    override fun accept(rendered: String?) {
        consumer.accept(rendered)
    }

    override fun close() {
        //NOOP
    }
}