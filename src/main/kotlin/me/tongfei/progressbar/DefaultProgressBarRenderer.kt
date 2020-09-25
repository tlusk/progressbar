package me.tongfei.progressbar

import java.text.DecimalFormat
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.floor
import kotlin.math.max

/**
 * Default progress bar renderer (see [ProgressBarRenderer]).
 * @author Tongfei Chen
 * @author Muhammet Sakarya
 * @since 0.8.0
 */
@Suppress("MemberVisibilityCanBePrivate")
open class DefaultProgressBarRenderer(
        private val style: ProgressBarStyle,
        private val unitName: String,
        private val unitSize: Long,
        private val isSpeedShown: Boolean,
        private val speedFormat: DecimalFormat,
        private val speedUnit: ChronoUnit
) : ProgressBarRenderer {
    // Number of full blocks
    protected fun progressIntegralPart(progress: ProgressState, length: Int): Int {
        return (progress.normalizedProgress * length).toInt()
    }

    protected fun progressFractionalPart(progress: ProgressState, length: Int): Int {
        val p = progress.normalizedProgress * length
        val fraction = (p - floor(p)) * style.fractionSymbols.length
        return floor(fraction).toInt()
    }

    protected fun eta(progress: ProgressState, elapsed: Duration): String {
        return if (progress.max <= 0 || progress.indefinite) "?" else if (progress.current - progress.start == 0L) "?" else Util.formatDuration(
                elapsed.dividedBy(progress.current - progress.start).multipliedBy(progress.max - progress.current)
        )
    }

    protected fun percentage(progress: ProgressState): String {
        val res = if (progress.max <= 0 || progress.indefinite) "? %" else (floor(100.0 * progress.current / progress.max).toString() + "%")
        return " ".repeat(4 - res.length) + res
    }

    protected fun ratio(progress: ProgressState): String {
        val m = if (progress.indefinite) "?" else (progress.max / unitSize).toString()
        val c = (progress.current / unitSize).toString()
        return " ".repeat(m.length - c.length) + c + "/" + m + unitName
    }

    protected fun speed(progress: ProgressState, elapsed: Duration): String {
        var suffix = "/s"
        val elapsedSeconds = elapsed.seconds.toDouble()
        var elapsedInUnit = elapsedSeconds
        when (speedUnit) {
            ChronoUnit.MINUTES -> {
                suffix = "/min"
                elapsedInUnit /= 60.0
            }
            ChronoUnit.HOURS -> {
                suffix = "/h"
                elapsedInUnit /= (60 * 60).toDouble()
            }
            ChronoUnit.DAYS -> {
                suffix = "/d"
                elapsedInUnit /= (60 * 60 * 24).toDouble()
            }
            else -> {}
        }
        if (elapsedSeconds == 0.0) return "?$unitName$suffix"
        val speed = (progress.current - progress.start).toDouble() / elapsedInUnit
        val speedWithUnit = speed / unitSize
        return speedFormat.format(speedWithUnit) + unitName + suffix
    }

    @Suppress("MemberVisibilityCanBePrivate", "MemberVisibilityCanBePrivate")
    override fun render(progress: ProgressState, maxLength: Int): String {
        val currTime = Instant.now()
        val elapsed = Duration.between(progress.startInstant, currTime)
        val prefix = "${progress.taskName} ${percentage(progress)} ${style.leftBracket}".take(maxLength - 1)

        // length of progress should be at least 1
        val maxSuffixLength = max(maxLength - prefix.length - 1, 0)
        val speedString = if (isSpeedShown) speed(progress, elapsed) else ""
        val suffix = ("${style.rightBracket} ${ratio(progress)} (" +
                "${Util.formatDuration(elapsed)} / ${eta(progress, elapsed)}) " +
                speedString + progress.extraMessage).take(maxSuffixLength)

        val length = maxLength - prefix.length - suffix.length
        val sb = StringBuilder(prefix)

        // case of indefinite progress bars
        if (progress.indefinite) {
            val pos = (progress.current % length).toInt()
            sb.append(style.space.toString().repeat(pos))
            sb.append(style.block)
            sb.append(style.space.toString().repeat(length - pos - 1))
        } else {
            sb.append(style.block.toString().repeat(progressIntegralPart(progress, length)))
            if (progress.current < progress.max) {
                sb.append(style.fractionSymbols[progressFractionalPart(progress, length)])
                sb.append(style.space.toString().repeat(length - progressIntegralPart(progress, length) - 1))
            }
        }
        sb.append(suffix)
        return sb.toString()
    }
}