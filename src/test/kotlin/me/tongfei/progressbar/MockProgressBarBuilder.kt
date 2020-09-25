package me.tongfei.progressbar

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets

class MockProgressBarBuilder : ProgressBarBuilder() {
    private val out = ByteArrayOutputStream()
    val output: String
        get() = try {
            out.toString(StandardCharsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("This should never happen!")
        }

    init {
        try {
            setConsumer(InteractiveConsoleProgressBarConsumer(PrintStream(out, true, StandardCharsets.UTF_8.name())))
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("This should never happen!")
        }
    }
}