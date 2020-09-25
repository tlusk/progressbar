package me.tongfei.progressbar

import org.junit.Test

class MultiProgressBarTest {
    @Test
    fun testMultiProgressBar() {
        ProgressBar("PB1", 100).use { pb1 ->
            ProgressBar("PB2", 100).use { pb2 ->
                repeat(100) {
                    pb1.step()
                    pb2.step()
                    Thread.sleep(100)
                }
            }
        }
    }
}