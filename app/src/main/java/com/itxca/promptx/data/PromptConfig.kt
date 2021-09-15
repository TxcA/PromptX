package com.itxca.promptx.data

import android.graphics.Color
import com.blankj.utilcode.util.ScreenUtils
import com.drake.serialize.serialize.serial
import com.drake.serialize.serialize.serialLazy

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx.float
 * Create Time  : 2021/9/11 0:05
 * Create By    : @author xIao
 * Version      : 1.0.0
 *
 **/

object PromptConfig {

    /**
     * 默认悬浮窗背景色
     */
    private const val DEFAULT_FLOAT_BACKGROUND: Int = 0x10FFFFFF


    private val DEFAULT_FLOAT_WIDTH by lazy { ScreenUtils.getScreenWidth() * 0.8f }
    private val DEFAULT_FLOAT_HEIGHT by lazy { ScreenUtils.getScreenHeight() * 0.6f }

    /**
     * 悬浮窗宽度
     */
    var floatWidth: Float by serialLazy(DEFAULT_FLOAT_WIDTH)

    /**
     * 悬浮窗高度
     */
    var floatHeight: Float by serialLazy(DEFAULT_FLOAT_HEIGHT)

    /**
     * 悬浮窗背景
     */
    var floatBackgroundColor: Int by serial(DEFAULT_FLOAT_BACKGROUND)

    /**
     * 文字大小
     */
    var textSize: Float by serial(20.0f)

    /**
     * 文字颜色
     */
    var textColor: Int by serial(Color.WHITE)

    /**
     *  文字加粗
     */
    var textBold: Boolean by serial(true)

    /**
     * 文字斜体
     */
    var textItalic: Boolean by serial(false)

    /**
     * 滚动速度
     */
    var scrollSpeed: Long by serial(50L)
}