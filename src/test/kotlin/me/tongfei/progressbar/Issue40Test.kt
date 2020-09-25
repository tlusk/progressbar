package me.tongfei.progressbar

import org.junit.Test
import java.util.*

/**
 * @author Tongfei Chen
 */
class Issue40Test {
    @Test
    fun test() {
        val input = "100 200 300".byteInputStream()

        val sc = Scanner(input)
        val x = sc.nextInt()
        ProgressBar("1", x.toLong()).use { pb ->
            repeat(x) {
                Thread.sleep(10)
                pb.step()
            }
        }
        val y = sc.nextInt()
        ProgressBar("2", y.toLong()).use { pb ->
            repeat(y) {
                Thread.sleep(10)
                pb.step()
            }
        }
        val z = sc.nextInt()
        ProgressBar("3", z.toLong()).use { pb ->
            repeat(z) {
                Thread.sleep(10)
                pb.step()
            }
        }
    }
}