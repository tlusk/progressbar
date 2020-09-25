package me.tongfei.progressbar

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author Martin Vehovsky
 * @since 1.0.0
 */
object TerminalUtils {
    const val CARRIAGE_RETURN = "\r"
    @Suppress("MemberVisibilityCanBePrivate")
    const val ESCAPE_CHAR = "\u001b"
    @Suppress("MemberVisibilityCanBePrivate")
    const val DEFAULT_TERMINAL_WIDTH = 80

    val activeConsumers: Queue<ProgressBarConsumer> = ConcurrentLinkedQueue()

    val terminalWidth: Int
        // Workaround for issue #23 under IntelliJ
        get() = terminal.width.takeIf { it >= 10 } ?: DEFAULT_TERMINAL_WIDTH

    @get:JvmName("hasCursorMovementSupport")
    val hasCursorMovementSupport: Boolean by lazy {
        terminal.getStringCapability(InfoCmp.Capability.cursor_up) != null &&
                terminal.getStringCapability(InfoCmp.Capability.cursor_down) != null
    }

    @Synchronized
    fun closeTerminal() {
        try {
            if (lazyTerminal.isInitialized()) {
                terminal.close()
            }
        } catch (ignored: IOException) { /* noop */ }
    }

    fun <T : ProgressBarConsumer> filterActiveConsumers(clazz: Class<T>): List<T> {
        return activeConsumers
                .filter(clazz::isInstance)
                .map(clazz::cast)
    }

    fun moveCursorUp(count: Int): String = ESCAPE_CHAR + "[" + count + "A" + CARRIAGE_RETURN
    fun moveCursorDown(count: Int): String = ESCAPE_CHAR + "[" + count + "B" + CARRIAGE_RETURN

    /**
     *  * Creating terminal is relatively expensive, usually takes between 5-10ms.
     *
     *  * If updateInterval is set under 10ms creating new terminal for on every re-render of progress bar could be a problem.
     *  * Especially when multiple progress bars are running in parallel.
     *
     *
     *  * Another problem with [Terminal] is that once created you can create another instance (say from different thread), but this instance will be
     * "dumb". Until previously created terminal will be closed.
     */
    private val lazyTerminal: Lazy<Terminal> = lazy {
        try {
            // Issue #42
            // Defaulting to a dumb terminal when a supported terminal can not be correctly created
            // see https://github.com/jline/jline3/issues/291
            TerminalBuilder.builder().dumb(true).build()
        } catch (e: IOException) {
            throw RuntimeException("This should never happen! Dumb terminal should have been created.")
        }
    }
    private val terminal: Terminal by lazyTerminal
}