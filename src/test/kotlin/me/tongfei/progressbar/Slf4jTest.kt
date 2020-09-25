package me.tongfei.progressbar

import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * @author Alex Peelman
 */
class Slf4jTest {
    // Prints progress as new lines without carriage return
    @Test
    fun printLoggerTest() {
        val logger = LoggerFactory.getLogger("Test")
        ProgressBarBuilder()
                .setInitialMax(100)
                .setTaskName("log.test")
                .setConsumer(DelegatingProgressBarConsumer({ logger.info(it) }))
                .setUpdateIntervalMillis(100)
                .build().use { pb ->
                    repeat(100) {
                        pb.step()
                        Thread.sleep(100)
                    }
                }
    }
}