package me.tongfei.progressbar

import org.junit.Test

/**
 * @author Tongfei Chen
 */
class WrappedIterableTest {
    @Test
    fun test() {
        val sizedColl = generateSequence(1) { it + 1 }.take(10000).toMutableList()
        for (x in ProgressBar.wrap(sizedColl, "Traverse")) {
            Thread.sleep(2)
        }
    }
}