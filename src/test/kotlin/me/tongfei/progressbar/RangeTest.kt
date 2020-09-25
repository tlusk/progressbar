package me.tongfei.progressbar

import org.junit.Test
import java.util.stream.IntStream

/**
 * @author Tongfei Chen
 */
class RangeTest {
    @Test
    fun parallelRangeTest() {
        ProgressBar.wrap(IntStream.range(1000, 9000).parallel(), "Test parallel").forEach {
            Thread.sleep(10)
        }
    }

    @Test
    fun sequentialRangeTest() {
        ProgressBar.wrap(IntStream.range(1000, 2000), "Test sequential").forEach {
            Thread.sleep(10)
        }
    }
}