package me.tongfei.progressbar.wrapped

import me.tongfei.progressbar.ProgressBar

/**
 * @author Tongfei Chen
 * @since 0.6.0
 */
class ProgressBarWrappedIterator<T>(private val iterator: MutableIterator<T>, val progressBar: ProgressBar) :
        MutableIterator<T> by iterator,
        AutoCloseable by progressBar {
    override fun hasNext(): Boolean {
        return iterator.hasNext().also {
            if (!it) progressBar.close()
        }
    }

    override fun next(): T {
        return iterator.next().also {
            progressBar.step()
        }
    }
}