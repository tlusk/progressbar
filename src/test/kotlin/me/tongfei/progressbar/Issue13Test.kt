package me.tongfei.progressbar

import org.junit.Test

private const val NBR_ELEMENTS = 100
private const val PROGRESSBAR_GRACE_PERIOD = 1000

/**
 * @author bwittwer
 */
class Issue13Test {
    @Test
    fun testOk() {
        ProgressBar("Test", NBR_ELEMENTS.toLong()).use { pb ->
            Thread.sleep(PROGRESSBAR_GRACE_PERIOD.toLong())
            repeat(NBR_ELEMENTS) {
                pb.step()
            }
        }
    }

    @Test
    fun testKo() {
        ProgressBar("Test", NBR_ELEMENTS.toLong()).use { pb ->
            repeat(NBR_ELEMENTS) {
                pb.step()
            }
        }
    }
}