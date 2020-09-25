package me.tongfei.progressbar

import org.junit.Test

class Issue50Test {
    @Test
    fun testCloseSpeed() {
        val tenSecondsInMS = 10 * 1000
        val startTime = System.currentTimeMillis()
        ProgressBarBuilder()
                .setTaskName("Foo")
                .setInitialMax(100)
                .setUpdateIntervalMillis(tenSecondsInMS)
                .build().use {
                    Thread.sleep(5)
                }
        val endTime = System.currentTimeMillis()
        assert(endTime - startTime < tenSecondsInMS)
    }
}