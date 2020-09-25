package me.tongfei.progressbar

import org.junit.Test
import java.text.DecimalFormat

class SpeedDisplayTest {
    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val bar = ProgressBarBuilder()
                .showSpeed()
                .setUnit("k", 1000)
                .setInitialMax(10000)
                .build()
        var x = 0
        while (x < 10000) {
            bar.step()
            Thread.sleep(1)
            x++
        }
        bar.close()
    }

    @Test
    @Throws(InterruptedException::class)
    fun testSpeedFormat() {
        val bar = ProgressBarBuilder()
                .showSpeed(DecimalFormat("#.##"))
                .setUnit("k", 1000)
                .setInitialMax(10000)
                .build()
        var x = 0
        while (x < 10000) {
            bar.step()
            Thread.sleep(1)
            x++
        }
        bar.close()
    }
}