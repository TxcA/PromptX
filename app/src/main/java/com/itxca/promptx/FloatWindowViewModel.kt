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

                    // ????????????
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

                    // scroll ??????
                    vb.nsvContainer.let { nsvContainer ->
                        nsvContainer.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                            currentHeight = scrollY
                        }
                        nsvContainer.smoothScrollTo(0, 0)
                    }

                    // ????????????
                    vb.ivScrollStartPause.setOnClickListener {
                        startOrPause(!isRunning.get())
                    }

                    // ????????????
                    vb.ivFontSizeAdd.setOnClickListener {
                        PromptConfig.textSize += 0.5f
                        vb.tvText.textSize = PromptConfig.textSize
                    }
                    vb.ivFontSizeReduce.setOnClickListener {
                        PromptConfig.textSize -= 0.5f
                        vb.tvText.textSize = PromptConfig.textSize
                    }

                    // ??????
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

                    // ????????????
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

                    // ??????
                    vb.ivFloatClose.setOnClickListener {
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        EasyFloat.dismiss(FLOAT_TAG)
                    }
                }

            }
            // ?????????????????????????????????????????????Activity?????????????????????????????????????????????
            .setShowPattern(ShowPattern.BACKGROUND)
            // ????????????????????????15????????????????????????SidePattern
            .setSidePattern(SidePattern.DEFAULT)
            // ????????????????????????????????????????????????
            .setTag(FLOAT_TAG)
            // ???????????????????????????
            .setDragEnable(true)
            // ??????????????????EditText??????????????????
            .hasEditText(false)
            // ???????????????????????????ps????????????????????????Gravity?????????offset???????????????
            //.setLocation(0, 200)
            // ?????????????????????????????????????????????
            .setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
            // ?????????????????????????????????????????????xml??????match_parent????????????
            .setMatchParent(widthMatch = false, heightMatch = false)
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????null
            .setAnimator(DefaultAnimator())
            // ?????????????????????????????????????????????
            .setFilter(MainActivity::class.java)
            // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            // ??????????????????????????????????????????????????????????????????????????????touchEvent?????????????????????????????????
            // ps?????????Kotlin DSL??????????????????????????????????????????????????????????????????
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