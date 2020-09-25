package me.tongfei.progressbar

import java.text.DecimalFormat
import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * Builder class for [ProgressBar]s.
 * @author Tongfei Chen
 * @since 0.7.0
 */
open class ProgressBarBuilder {
    private var task = ""
    private var initialMax: Long = -1
    private var updateIntervalMillis = 1000
    private var style = ProgressBarStyle.COLORFUL_UNICODE_BLOCK
    private var consumer: ProgressBarConsumer? = null
    private var unitName = ""
    private var unitSize: Long = 1
    private var showSpeed = false
    private var speedFormat: DecimalFormat = DecimalFormat("#.0")
    private var speedUnit = ChronoUnit.SECONDS
    private var processed: Long = 0
    private var elapsed = Duration.ZERO

    fun setTaskName(task: String) = apply { this.task = task }
    fun setInitialMax(initialMax: Long) = apply { this.initialMax = initialMax }
    fun setStyle(style: ProgressBarStyle) = apply { this.style = style }
    fun setUpdateIntervalMillis(updateIntervalMillis: Int) = apply { this.updateIntervalMillis = updateIntervalMillis }
    fun setConsumer(consumer: ProgressBarConsumer?) = apply { this.consumer = consumer }
    fun setUnit(unitName: String, unitSize: Long)= apply {
        this.unitName = unitName
        this.unitSize = unitSize
    }

    @JvmOverloads
    fun showSpeed(speedFormat: DecimalFormat = DecimalFormat("#.0")) = apply {
        showSpeed = true
        this.speedFormat = speedFormat
    }

    fun setSpeedUnit(speedUnit: ChronoUnit) = apply { this.speedUnit = speedUnit }

    /**
     * Sets elapsedBeforeStart duration and number of processed units.
     * @param processed amount of processed units
     * @param elapsed duration of
     */
    fun startsFrom(processed: Long, elapsed: Duration) = apply {
        this.processed = processed
        this.elapsed = elapsed
    }

    fun build(): ProgressBar {
        return ProgressBar(
                task,
                initialMax,
                updateIntervalMillis,
                processed,
                elapsed,
                DefaultProgressBarRenderer(style, unitName, unitSize, showSpeed, speedFormat, speedUnit),
                consumer ?: Util.createConsoleConsumer()
        )
    }
}