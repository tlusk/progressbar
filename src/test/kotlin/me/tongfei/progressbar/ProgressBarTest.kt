package me.tongfei.progressbar

import org.junit.Test
import java.text.DecimalFormat
import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * @author Tongfei Chen
 */
class ProgressBarTest {
    @Test
    fun test() {
        ProgressBarBuilder()
                .setTaskName("Test").setInitialMax(5).setUpdateIntervalMillis(50)
                .setStyle(ProgressBarStyle.UNICODE_BLOCK).setUnit("K", 1024).build().use { pb ->
                    val l = mutableListOf<Int>()
                    println("\n\n\n\n\n")
                    repeat(10000) { i ->
                        var sum = 0
                        for (j in 0 until i * 2000) sum += j
                        l.add(sum)
                        pb.step()
                        if (pb.current > 8000) pb.maxHint(10000)
                    }
                }
        println("Hello")
    }

    @Test
    fun testSpeedFormat() {
        val bar = ProgressBarBuilder()
                .showSpeed(DecimalFormat("#.##"))
                .setUnit("k", 1000)
                .setInitialMax(10000)
                .build()
        repeat(10000) {
            bar.step()
            Thread.sleep(1)
        }
        bar.close()
    }

    @Test
    fun testSpeedUnit() {
        val bar = ProgressBarBuilder()
                .showSpeed(DecimalFormat("#.####"))
                .setUnit("k", 1000)
                .setInitialMax(10000)
                .setSpeedUnit(ChronoUnit.MINUTES)
                .build()
        repeat(10000) {
            bar.step()
            Thread.sleep(1)
        }
        bar.close()
    }

    @Test
    fun testSpeedStartFrom() {
        val bar = ProgressBarBuilder()
                .showSpeed(DecimalFormat("#.##"))
                .setUnit("k", 1000)
                .setInitialMax(10000)
                .startsFrom(5000, Duration.ZERO)
                .setUpdateIntervalMillis(10)
                .build()
        repeat(5000) {
            bar.step()
            Thread.sleep(1)
        }
        bar.close()
    }
}