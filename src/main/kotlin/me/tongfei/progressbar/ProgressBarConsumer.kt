package me.tongfei.progressbar

import java.util.function.Consumer

/**
 * A consumer that prints a rendered progress bar.
 * @since 0.8.0
 * @author Alex Peelman
 * @author Tongfei Chen
 */
interface ProgressBarConsumer : Consumer<String?>, Appendable, AutoCloseable {
    /**
     * Returns the maximum length allowed for the rendered form of a progress bar.
     */
    val maxRenderedLength: Int

    /**
     * Accepts a rendered form of a progress bar, e.g., prints to a specified stream.
     * @param rendered Rendered form of a progress bar, a string
     */
    override fun accept(rendered: String?)
    override fun append(csq: CharSequence): ProgressBarConsumer {
        accept(csq.toString())
        return this
    }

    override fun append(csq: CharSequence, start: Int, end: Int): ProgressBarConsumer {
        accept(csq.subSequence(start, end).toString())
        return this
    }

    override fun append(c: Char): ProgressBarConsumer {
        accept(c.toString())
        return this
    }

    override fun close()
}