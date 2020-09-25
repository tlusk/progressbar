package me.tongfei.progressbar

/**
 * Represents the action that is executed for each refresh of a progress bar.
 */
internal class ProgressUpdateAction(
        val progress: ProgressState,
        private val renderer: ProgressBarRenderer,
        private val consumer: ProgressBarConsumer
) : Runnable {
    private fun refresh() {
        val rendered = renderer.render(progress, consumer.maxRenderedLength)
        consumer.accept(rendered)
    }

    override fun run() {
        if (!progress.paused) refresh()
        if (!progress.alive) {
            consumer.close()
            TerminalUtils.closeTerminal()
        }
    }
}