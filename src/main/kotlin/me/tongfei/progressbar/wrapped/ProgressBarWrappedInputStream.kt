package me.tongfei.progressbar.wrapped

import me.tongfei.progressbar.ProgressBar
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Any input stream whose progress is tracked by a progress bar.
 * @author Tongfei Chen
 * @since 0.7.0
 */
class ProgressBarWrappedInputStream(stream: InputStream, val progressBar: ProgressBar) : FilterInputStream(stream) {
    private var mark: Long = 0

    @Throws(IOException::class)
    override fun read(): Int {
        return super.read().also {
            if (it != -1) progressBar.step()
        }
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        return super.read(b).also {
            if (it != -1) progressBar.stepBy(it.toLong())
        }
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return super.read(b, off, len).also {
            if (it != -1) progressBar.stepBy(it.toLong())
        }
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        return super.skip(n).also {
            progressBar.stepBy(it)
        }
    }

    override fun mark(readLimit: Int) {
        super.mark(readLimit)
        mark = progressBar.current
    }

    @Throws(IOException::class)
    override fun reset() {
        super.reset()
        progressBar.stepTo(mark)
    }

    @Throws(IOException::class)
    override fun close() {
        super.close()
        progressBar.close()
    }
}