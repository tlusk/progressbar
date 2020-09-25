package me.tongfei.progressbar

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.PrintStream
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor

internal object Util {
    val executor = ScheduledThreadPoolExecutor(1) { runnable ->
        Executors.defaultThreadFactory().newThread(runnable).apply {
            name = "ProgressBar"
            isDaemon = true
        }
    }

    @JvmOverloads
    fun createConsoleConsumer(out: PrintStream = System.err): ConsoleProgressBarConsumer {
        return if (TerminalUtils.hasCursorMovementSupport)
            InteractiveConsoleProgressBarConsumer(out) else ConsoleProgressBarConsumer(out)
    }

    fun formatDuration(d: Duration): String {
        return String.format("%d:%02d:%02d", d.seconds / 3600, d.seconds % 3600 / 60, d.seconds % 60)
    }

    fun getInputStreamSize(stream: InputStream?): Long {
        return try {
            (stream as? FileInputStream)?.channel?.size() ?: -1
        } catch (e: IOException) {
            -1
        }
    }
}