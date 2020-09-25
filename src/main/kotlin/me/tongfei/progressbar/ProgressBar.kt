package me.tongfei.progressbar

import me.tongfei.progressbar.wrapped.ProgressBarWrappedInputStream
import me.tongfei.progressbar.wrapped.ProgressBarWrappedIterable
import me.tongfei.progressbar.wrapped.ProgressBarWrappedIterator
import me.tongfei.progressbar.wrapped.ProgressBarWrappedSpliterator
import java.io.InputStream
import java.io.PrintStream
import java.text.DecimalFormat
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.stream.BaseStream
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * A console-based progress bar with minimal runtime overhead.
 * @author Tongfei Chen
 */
class ProgressBar(
        task: String,
        initialMax: Long,
        updateIntervalMillis: Int,
        processed: Long,
        elapsed: Duration,
        renderer: ProgressBarRenderer,
        consumer: ProgressBarConsumer
) : AutoCloseable {
    private val progress: ProgressState = ProgressState(task, initialMax, processed, elapsed)
    private val action: ProgressUpdateAction = ProgressUpdateAction(progress, renderer, consumer)
    private val scheduledTask: ScheduledFuture<*> = Util.executor.scheduleAtFixedRate(
            action, 0, updateIntervalMillis.toLong(), TimeUnit.MILLISECONDS
    )

    /**
     * Creates a progress bar with the specific taskName name, initial maximum value,
     * customized update interval (default 1000 ms), the PrintStream to be used, and output style.
     * @param task Task name
     * @param initialMax Initial maximum value
     * @param updateIntervalMillis Update interval (default value 1000 ms)
     * @param style Output style (default value ProgressBarStyle.UNICODE_BLOCK)
     * @param showSpeed Should the calculated speed be displayed
     * @param speedFormat Speed number format
     */
    @JvmOverloads
    constructor(
            task: String,
            initialMax: Long,
            updateIntervalMillis: Int = 1000,
            os: PrintStream = System.err,
            style: ProgressBarStyle = ProgressBarStyle.COLORFUL_UNICODE_BLOCK,
            unitName: String = "",
            unitSize: Long = 1,
            showSpeed: Boolean = false,
            speedFormat: DecimalFormat = DecimalFormat("#.0"),
            speedUnit: ChronoUnit = ChronoUnit.SECONDS,
            processed: Long = 0L,
            elapsed: Duration = Duration.ZERO
    ) : this(task, initialMax, updateIntervalMillis, processed, elapsed,
            DefaultProgressBarRenderer(style, unitName, unitSize, showSpeed, speedFormat, speedUnit),
            Util.createConsoleConsumer(os)
    )

    /**
     * Advances this progress bar by a specific amount.
     * @param n Step size
     */
    fun stepBy(n: Long): ProgressBar = apply {
        progress.stepBy(n)
    }

    /**
     * Advances this progress bar to the specific progress value.
     * @param n New progress value
     */
    fun stepTo(n: Long): ProgressBar = apply {
        progress.stepTo(n)
    }

    /**
     * Advances this progress bar by one step.
     */
    fun step(): ProgressBar = apply {
        progress.stepBy(1)
    }

    /**
     * Gives a hint to the maximum value of the progress bar.
     * @param n Hint of the maximum value
     */
    fun maxHint(n: Long): ProgressBar = apply {
        if (n < 0)
            progress.indefinite = true
        else {
            progress.indefinite = false
            progress.max = n
        }
    }

    /**
     * Pauses this current progress.
     */
    fun pause(): ProgressBar = apply {
        progress.pause()
    }

    /**
     * Resumes this current progress.
     */
    fun resume(): ProgressBar = apply {
        progress.resume()
    }

    /**
     *
     * Stops this progress bar, effectively stops tracking the underlying process.
     *
     * Implements the [AutoCloseable] interface which enables the try-with-resource
     * pattern with progress bars.
     * @since 0.7.0
     */
    override fun close() {
        scheduledTask.cancel(false)
        progress.kill()
        try {
            Util.executor.schedule(action, 0, TimeUnit.NANOSECONDS).get()
        } catch (e: InterruptedException) { /* NOOP */
        } catch (e: ExecutionException) {
        }
    }

    /**
     * Sets the extra message at the end of the progress bar.
     * @param msg New message
     */
    fun setExtraMessage(msg: String): ProgressBar = apply {
        progress.extraMessage = msg
    }

    /**
     * Returns the current progress.
     */
    val current: Long
        get() = progress.current

    /**
     * Returns the maximum value of this progress bar.
     */
    @Suppress("unused")
    val max: Long
        get() = progress.max

    /**
     * Returns the name of this task.
     */
    @Suppress("unused")
    val taskName: String
        get() = progress.taskName

    /**
     * Returns the extra message at the end of the progress bar.
     */
    @Suppress("unused")
    val extraMessage: String
        get() = progress.extraMessage

    // STATIC WRAPPER METHODS
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    companion object {
        /**
         * Wraps an iterator so that when iterated, a progress bar is shown to track the traversal progress.
         * @param it Underlying iterator
         * @param task Task name
         */
        fun <T> wrap(it: MutableIterator<T>, task: String): Iterator<T> {
            return wrap(it,
                    ProgressBarBuilder().setTaskName(task).setInitialMax(-1)
            ) // indefinite progress bar
        }

        /**
         * Wraps an iterator so that when iterated, a progress bar is shown to track the traversal progress.
         * @param it Underlying iterator
         * @param pbb Progress bar builder
         */
        fun <T> wrap(it: MutableIterator<T>, pbb: ProgressBarBuilder): Iterator<T> {
            return ProgressBarWrappedIterator(it, pbb.build())
        }

        /**
         * Wraps an [Iterable] so that when iterated, a progress bar is shown to track the traversal progress.
         *
         *
         * Sample usage: `for (T x : ProgressBar.wrap(collection, "Traversal")) { ... }
        ` *
         *
         * @param ts Underlying iterable
         * @param task Task name
         */
        fun <T> wrap(ts: MutableIterable<T>, task: String): Iterable<T> {
            return wrap(ts, ProgressBarBuilder().setTaskName(task))
        }

        /**
         * Wraps an [Iterable] so that when iterated, a progress bar is shown to track the traversal progress.
         * For this function the progress bar can be fully customized by using a [ProgressBarBuilder].
         * @param ts Underlying iterable
         * @param pbb An instance of a [ProgressBarBuilder]
         */
        fun <T> wrap(ts: MutableIterable<T>, pbb: ProgressBarBuilder): Iterable<T> {
            val size = ts.spliterator().exactSizeIfKnown
            if (size != -1L) pbb.setInitialMax(size)
            return ProgressBarWrappedIterable(ts, pbb)
        }

        /**
         * Wraps an [InputStream] so that when read, a progress bar is shown to track the reading progress.
         * @param is Input stream to be wrapped
         * @param task Name of the progress
         */
        fun wrap(`is`: InputStream, task: String): InputStream {
            val pbb = ProgressBarBuilder().setTaskName(task).setInitialMax(Util.getInputStreamSize(`is`))
            return wrap(`is`, pbb)
        }

        /**
         * Wraps an [InputStream] so that when read, a progress bar is shown to track the reading progress.
         * For this function the progress bar can be fully customized by using a [ProgressBarBuilder].
         * @param is Input stream to be wrapped
         * @param pbb An instance of a [ProgressBarBuilder]
         */
        fun wrap(`is`: InputStream, pbb: ProgressBarBuilder): InputStream {
            val size = Util.getInputStreamSize(`is`)
            if (size != -1L) pbb.setInitialMax(size)
            return ProgressBarWrappedInputStream(`is`, pbb.build())
        }

        /**
         * Wraps a [Spliterator] so that when iterated, a progress bar is shown to track the traversal progress.
         * @param sp Underlying spliterator
         * @param task Task name
         */
        fun <T> wrap(sp: Spliterator<T>, task: String): Spliterator<T> {
            val pbb = ProgressBarBuilder().setTaskName(task)
            return wrap(sp, pbb)
        }

        /**
         * Wraps a [Spliterator] so that when iterated, a progress bar is shown to track the traversal progress.
         * For this function the progress bar can be fully customized by using a [ProgressBarBuilder].
         * @param sp Underlying spliterator
         * @param pbb An instance of a [ProgressBarBuilder]
         */
        fun <T> wrap(sp: Spliterator<T>, pbb: ProgressBarBuilder): Spliterator<T> {
            val size = sp.exactSizeIfKnown
            if (size != -1L) pbb.setInitialMax(size)
            return ProgressBarWrappedSpliterator(sp, pbb.build())
        }

        /**
         * Wraps a [Stream] so that when iterated, a progress bar is shown to track the traversal progress.
         * @param stream Underlying stream (can be sequential or parallel)
         * @param task Task name
         */
        fun <T, S : BaseStream<T, S>> wrap(stream: S, task: String): Stream<T> {
            val pbb = ProgressBarBuilder().setTaskName(task)
            return wrap(stream, pbb)
        }

        /**
         * Wraps a [Stream] so that when iterated, a progress bar is shown to track the traversal progress.
         * For this function the progress bar can be fully customized by using a [ProgressBarBuilder].
         * @param stream Underlying stream (can be sequential or parallel)
         * @param pbb An instance of a [ProgressBarBuilder]
         */
        fun <T, S : BaseStream<T, S>> wrap(stream: S, pbb: ProgressBarBuilder): Stream<T> {
            val sp = wrap(stream.spliterator(), pbb)
            return StreamSupport.stream(sp, stream.isParallel)
        }

        /**
         * Wraps an array so that when iterated, a progress bar is shown to track the traversal progress.
         * @param array Array to be wrapped
         * @param task Task name
         * @return Wrapped array, of type [Stream].
         */
        fun <T> wrap(array: Array<T>, task: String): Stream<T> {
            val pbb = ProgressBarBuilder().setTaskName(task).setInitialMax(array.size.toLong())
            return wrap(array, pbb)
        }

        /**
         * Wraps an array so that when iterated, a progress bar is shown to track the traversal progress.
         * For this function the progress bar can be fully customized by using a [ProgressBarBuilder].
         * @param array Array to be wrapped
         * @param pbb An instance of a [ProgressBarBuilder]
         * @return Wrapped array, of type [Stream].
         */
        fun <T> wrap(array: Array<T>?, pbb: ProgressBarBuilder): Stream<T> {
            return wrap(Arrays.stream(array), pbb)
        }
    }
}