package me.tongfei.progressbar

/**
 * Represents the display style of a progress bar.
 * @author Tongfei Chen
 * @since 0.5.1
 */
enum class ProgressBarStyle(
        val leftBracket: String,
        val rightBracket: String,
        val block: Char,
        val space: Char,
        val fractionSymbols: String) {
    COLORFUL_UNICODE_BLOCK("\u001b[33m│", "│\u001b[0m", '█', ' ', " ▏▎▍▌▋▊▉"),

    /** Use Unicode block characters to draw the progress bar.  */
    UNICODE_BLOCK("│", "│", '█', ' ', " ▏▎▍▌▋▊▉"),

    /** Use only ASCII characters to draw the progress bar.  */
    @Suppress("unused")
    ASCII("[", "]", '=', ' ', ">");
}