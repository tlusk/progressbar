package me.tongfei.progressbar

import java.time.Duration
import java.time.Instant

/**
 * Encapsulates the internal states of a progress bar.
 * @author Tongfei Chen
 * @since 0.5.0
 */
class ProgressState(
        var taskName: String,
        var max: Long,
        var start: Long,
        private var elapsedBeforeStart: Duration = Duration.ZERO) {

    //  0             start     current        max
    //  [===============|=========>             ]
    var current: Long = start
    var extraMessage = ""
    var indefinite: Boolean = max < 0
    var startInstant: Instant = Instant.now()

    @Volatile
    var alive = true

    @Volatile
    var paused = false

    // The progress, normalized to range [0, 1].
    @get:Synchronized
    val normalizedProgress: Double
        get() = when {
            max <= 0 -> 0.0
            current > max -> 1.0
            else -> current.toDouble() / max
        }

    @Synchronized
    fun stepBy(n: Long) {
        current += n
        if (current > max) max = current
    }

    @Synchronized
    fun stepTo(n: Long) {
        current = n
        if (current > max) max = current
    }

    @Synchronized
    fun pause() {
        paused = true
        start = current
        elapsedBeforeStart = elapsedBeforeStart.plus(Duration.between(startInstant, Instant.now()))
    }

    @Synchronized
    fun resume() {
        paused = false
        startInstant = Instant.now()
    }

    fun kill() {
        alive = false
    }
}