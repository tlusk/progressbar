package me.tongfei.progressbar

import org.junit.Test

class PauseResumeTest {
    @Test
    fun testPauseResume() {
        ProgressBarBuilder()
                .setTaskName("Test")
                .setInitialMax(10)
                .setUpdateIntervalMillis(100)
                .build().use { pb ->
                    repeat(5) {
                        Thread.sleep(100)
                        pb.step()
                        Thread.sleep(100)
                        pb.step()
                        pb.pause()
                        Thread.sleep(1000)
                        pb.resume()
                    }
                }
    }
}