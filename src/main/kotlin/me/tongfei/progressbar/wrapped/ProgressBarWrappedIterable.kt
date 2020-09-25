package me.tongfei.progressbar.wrapped

import me.tongfei.progressbar.ProgressBarBuilder

/**
 * @author Tongfei Chen
 * @since 0.6.0
 */
class ProgressBarWrappedIterable<T>(
        private val underlying: MutableIterable<T>,
        val progressBarBuilder: ProgressBarBuilder) : Iterable<T> {

    override fun iterator(): MutableIterator<T> {
        return ProgressBarWrappedIterator(
                underlying.iterator(),
                progressBarBuilder.setInitialMax(underlying.spliterator().exactSizeIfKnown).build()
                // exactSizeIfKnown return -1 if not known, then indefinite progress bar naturally
        )
    }
}