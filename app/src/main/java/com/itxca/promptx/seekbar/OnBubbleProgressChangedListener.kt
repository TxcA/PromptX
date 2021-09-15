package com.itxca.promptx.seekbar

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx.seekbar
 * Create Time  : 2021-09-11 17:49
 * Create By    : @author xIao
 * Version      : 1.0.0
 *
 **/

open class OnBubbleProgressChangedListener : BubbleSeekBar.OnProgressChangedListener {
    override fun onProgressChanged(
        bubbleSeekBar: BubbleSeekBar?,
        progress: Int,
        progressFloat: Float,
        fromUser: Boolean
    ) =Unit

    override fun getProgressOnActionUp(
        bubbleSeekBar: BubbleSeekBar?,
        progress: Int,
        progressFloat: Float
    ) =Unit

    override fun getProgressOnFinally(
        bubbleSeekBar: BubbleSeekBar?,
        progress: Int,
        progressFloat: Float,
        fromUser: Boolean
    )  =Unit
}