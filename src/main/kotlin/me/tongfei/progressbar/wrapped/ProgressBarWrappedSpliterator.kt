package me.tongfei.progressbar.wrapped

import me.tongfei.progressbar.ProgressBar
import java.util.*
import java.util.function.Consumer

/**
 * @author Tongfei Chen
 * @since 0.7.2
 */
class ProgressBarWrappedSpliterator<T> private constructor(
        private val underlying: Spliterator<T>,
        val progressBar: ProgressBar,
        private val openChildren: MutableSet<Spliterator<T>>)
    : Spliterator<T> by underlying, AutoCloseable by progressBar {

    constructor(underlying: Spliterator<T>, pb: ProgressBar) :
            this(underlying, pb, Collections.synchronizedSet(HashSet<Spliterator<T>>())) // has to be synchronized

    init {
        openChildren.add(this)
    }

    private fun registerChild(child: Spliterator<T>) {
        openChildren.add(child)
    }

    private fun removeThis() {
        openChildren.remove(this)
        if (openChildren.isEmpty()) close()
        // only closes the progressbar if no spliterator is working anymore
    }

    override fun tryAdvance(action: Consumer<in T>): Boolean {
        return underlying.tryAdvance(action).also { advanced ->
            if (advanced) progressBar.step()
            else removeThis()
        }
    }

    // if not overridden, may return null since that is the default Spliterator implementation
    override fun getComparator(): Comparator<in T>? = underlying.comparator

    override fun trySplit(): Spliterator<T>? {
        return underlying.trySplit()?.let { u ->
            ProgressBarWrappedSpliterator(u, progressBar, openChildren).also { registerChild(it) }
        }
    }
}