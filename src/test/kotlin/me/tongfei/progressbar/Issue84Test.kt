package me.tongfei.progressbar

import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

private const val ITER_NUMBER = 100
private const val CRITICAL_EXTRA_MSG_LEN = 38

/**
 * @author Andrei Nakrasov
 */
class Issue84Test {
    @Test
    fun testLongExtraMessage() {

        // redirect all exception messages to a new stream
        // https://stackoverflow.com/a/8708357
        val baos = ByteArrayOutputStream()
        val ps = PrintStream(baos)
        System.setErr(ps)
        ProgressBarBuilder()
                .setTaskName("Test")
                .setInitialMax(-1)
                .build()
                .setExtraMessage("0".repeat(CRITICAL_EXTRA_MSG_LEN))
                .use { pb ->
                    repeat(ITER_NUMBER) {
                        Thread.sleep(5)
                        pb.step()
                    }
                }
        val exceptionMsgChecker = baos.toString()
        System.setErr(System.err)
        assert(!exceptionMsgChecker.contains("Exception"))
    }
}