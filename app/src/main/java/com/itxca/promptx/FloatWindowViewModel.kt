package com.itxca.promptx

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ConvertUtils
import com.itxca.promptx.data.PromptConfig
import com.itxca.promptx.databinding.FloatTextBinding
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.anim.DefaultAnimator
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.utils.DisplayUtils
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx
 * Create Time  : 2021-09-11 1:48
 * Create By    : @author xIao
 * Version      : 1.0.0
 *
 **/

class FloatWindowViewModel : ViewModel() {

    private var viewBinding: FloatTextBinding? = null

    private var moveJob: Job? = null

    private var currentHeight: Int = 0

    private val isRunning: AtomicBoolean = AtomicBoolean(false)

    private val isMiniMode: AtomicBoolean = AtomicBoolean(false)

    private var cacheSpeed: Long = PromptConfig.scrollSpeed
        set(value) {
            PromptConfig.scrollSpeed = value
            field = value
        }

    private val floatWidth: Int
        get() = PromptConfig.floatWidth.toInt()

    private val floatHeight: Int
        get() = PromptConfig.floatHeight.toInt()

    fun createTextFloat(activity: AppCompatActivity, text: String) {
        EasyFloat.dismiss(FLOAT_TAG, true)
        EasyFloat.with(activity)
            .setLayout(R.layout.float_text) {
                viewBinding = FloatTextBinding.bind(it.findViewById(R.id.cl_float_container))

                // init config
                floatModeSwitch(false)

                viewBinding?.let { vb ->
                    vb.clFloatContainer.background = GradientDrawable().also { drawable ->
                        drawable.cornerRadius = ConvertUtils.dp2px(12f).toFloat()
                        drawable.setColor(PromptConfig.floatBackgroundColor)
                    }
                    vb.tvText.let { tvText ->
                        tvText.text = text
                        tvText.textSize = PromptConfig.textSize
                        tvText.typeface = Typeface.defaultFromStyle(
                            when {
                                PromptConfig.textBold && PromptConfig.textItalic -> Typeface.BOLD_ITALIC
                                PromptConfig.textBold -> Typeface.BOLD
                                PromptConfig.textItalic -> Typeface.ITALIC
                                else -> Typeface.NORMAL
                            }
                        )
                        tvText.setTextColor(PromptConfig.textColor)
                    }

                    // 指定拖拽
                    vb.let { v ->
                        configDrag(
                            dragView = v.ivFloatMove,
                            unDragView = mutableListOf(
                                v.tvText,
                                v.ivScrollStartPause,
                                v.ivFontSizeAdd,
                                v.ivFontSizeReduce,
                                v.ivSpeedAdd,
                                v.ivSpeedReduce,
                                v.ivFloatMini,
                                v.ivFloatClose,
                            )
                        )
                    }

                    // scroll 拖拽
                    vb.nsvContainer.let { nsvContainer ->
                        nsvContainer.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                            currentHeight = scrollY
                        }
                        nsvContainer.smoothScrollTo(0, 0)
                    }

                    // 开始暂停
                    vb.ivScrollStartPause.setOnClickListener {
                        startOrPause(!isRunning.get())
                    }

                    // 字体大小
                    vb.ivFontSizeAdd.setOnClickListener {
                        PromptConfig.textSize += 0.5f
                        vb.tvText.textSize = PromptConfig.textSize
                    }
                    vb.ivFontSizeReduce.setOnClickListener {
                        PromptConfig.textSize -= 0.5f
                        vb.tvText.textSize = PromptConfig.textSize
                    }

                    // 速度
                    vb.ivSpeedAdd.setOnClickListener {
                        if (cacheSpeed > 5L) {
                            cacheSpeed -= 5
                        }
                    }
                    vb.ivSpeedReduce.setOnClickListener {
                        if (cacheSpeed < 300L) {
                            cacheSpeed += 5
                        }
                    }

                    // 放大缩小
                    vb.ivFloatMini.setOnClickListener {
                        vb.ivFloatMini.let { ivMini ->
                            if (isMiniMode.get()) {
                                isMiniMode.set(false)
                                ivMini.setImageResource(R.mipmap.icon_float_mini)
                            } else {
                                isMiniMode.set(true)
                                startOrPause(false)
                                ivMini.setImageResource(R.mipmap.icon_float_max)
                            }
                            floatModeSwitch(isMiniMode.get())
                        }
                    }

                    // 关闭
                    vb.ivFloatClose.setOnClickListener {
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        EasyFloat.dismiss(FLOAT_TAG)
                    }
                }

            }
            // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示
            .setShowPattern(ShowPattern.BACKGROUND)
            // 设置吸附方式，共15种模式，详情参考SidePattern
            .setSidePattern(SidePattern.DEFAULT)
            // 设置浮窗的标签，用于区分多个浮窗
            .setTag(FLOAT_TAG)
            // 设置浮窗是否可拖拽
            .setDragEnable(true)
            // 浮窗是否包含EditText，默认不包含
            .hasEditText(false)
            // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
            //.setLocation(0, 200)
            // 设置浮窗的对齐方式和坐标偏移量
            .setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
            // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
            .setMatchParent(widthMatch = false, heightMatch = false)
            // 设置浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
            .setAnimator(DefaultAnimator())
            // 设置系统浮窗的不需要显示的页面
            .setFilter(MainActivity::class.java)
            // 设置系统浮窗的有效显示高度（不包含虚拟导航栏的高度），基本用不到，除非有虚拟导航栏适配问题
            .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
            // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
            .registerCallback {
                createResult { isCreated, msg, _ ->
                    if (isCreated) {
                        moveJob = activity.lifecycleScope.launch(
                            Dispatchers.Main,
                            CoroutineStart.LAZY
                        ) {
                            val textHeight = viewBinding?.tvText?.measuredHeight ?: 0
                            while (isActive) {
                                if (isRunning.get() && currentHeight < textHeight) {
                                    currentHeight += 1
                                    withContext(Dispatchers.Main) {
                                        viewBinding?.nsvContainer?.scrollTo(0, currentHeight)
                                    }
                                }
                                delay(cacheSpeed)
                            }
                        }
                    } else {
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
                    }
                }
                show {
                    if (moveJob?.isActive == true) {
                        isRunning.set(true)
                    }
                }

                hide {
                    isRunning.set(false)
                }

                dismiss {
                    moveJob?.cancel()
                    currentHeight = 0
                    isRunning.set(false)
                    viewBinding = null
                }
            }
            .show()
    }

    private fun startOrPause(toRunning: Boolean) {
        if (toRunning) {
            isRunning.set(true)
            if (moveJob?.isActive != true) {
                moveJob?.start()
            }
            viewBinding?.ivScrollStartPause?.setImageResource(R.mipmap.icon_scroll_pause)
        } else {
            isRunning.set(false)
            viewBinding?.ivScrollStartPause?.setImageResource(R.mipmap.icon_scroll_start)
        }
    }

    private fun floatModeSwitch(isMini: Boolean) {
        viewBinding?.let { vb ->
            vb.ivScrollStartPause.isGone = isMini
            vb.ivFontSizeAdd.isGone = isMini
            vb.ivFontSizeReduce.isGone = isMini
            vb.ivSpeedAdd.isGone = isMini
            vb.ivSpeedReduce.isGone = isMini
            vb.ivFloatMove.isGone = isMini
            vb.ivFloatClose.isGone = isMini
            vb.nsvContainer.isGone = isMini

            viewBinding?.clFloatContainer?.let { floatContainer ->
                floatContainer.layoutParams = if (isMini) {
                    vb.ivFloatMini.enableDrag(true)

                    floatContainer.layoutParams.also { layoutParams ->
                        layoutParams.width = ConvertUtils.dp2px(45f)
                        layoutParams.height = ConvertUtils.dp2px(45f)
                    }
                } else {
                    vb.ivFloatMini.enableDrag(false)

                    floatContainer.layoutParams.also { layoutParams ->
                        layoutParams.width = floatWidth
                        layoutParams.height = floatHeight
                    }
                }
            }
        }
    }

    private fun <T : View> configDrag(dragView: View?, unDragView: List<T?>) {
        unDragView.forEach {
            it?.enableDrag(false)
        }
        dragView?.enableDrag(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun <T : View> T?.enableDrag(isEnable: Boolean) {
        this?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL -> {
                    EasyFloat.dragEnable(isEnable, FLOAT_TAG)
                }
                else -> EasyFloat.dragEnable(false, FLOAT_TAG)
            }
            false
        }
    }

    companion object {
        private const val FLOAT_TAG = "PromptX"
    }
}