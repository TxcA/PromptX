package com.itxca.promptx.popup

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ScreenUtils
import com.itxca.promptx.R
import com.itxca.promptx.data.PromptConfig
import com.itxca.promptx.databinding.PopupFontConfigBinding
import com.itxca.promptx.seekbar.BubbleSeekBar
import com.itxca.promptx.seekbar.OnBubbleProgressChangedListener
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.kotlin.colorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorListener
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.ScaleConfig

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx.popup
 * Create Time  : 2021/9/11 11:41
 * Create By    : @author xIao
 * Version      : 1.0.0
 *
 **/

class FontConfigPopup : BasePopupWindow {
    constructor(context: Context) : super(context)
    constructor(fragment: Fragment) : super(fragment)
    constructor(dialog: Dialog) : super(dialog)

    private lateinit var viewBinding: PopupFontConfigBinding

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        viewBinding = PopupFontConfigBinding.bind(contentView)

        updateFontColor()
        viewBinding.vConfigTextColor.setOnClickListener {
            showColorPickerDialog("修改文本颜色", PromptConfig.textColor) {
                PromptConfig.textColor = it
                updateFontColor(it)
            }
        }

        updateFloatBackgroundColor()
        viewBinding.vConfigFloatBackgroundColor.setOnClickListener {
            showColorPickerDialog("修改背景颜色", PromptConfig.floatBackgroundColor) {
                PromptConfig.floatBackgroundColor = it
                updateFloatBackgroundColor(it)
            }
        }

        viewBinding.smFontTextBold.isChecked = PromptConfig.textBold
        viewBinding.smFontTextBold.setOnCheckedChangeListener { _, isChecked ->
            PromptConfig.textBold = isChecked
        }

        viewBinding.smFontTextItalic.isChecked = PromptConfig.textItalic
        viewBinding.smFontTextItalic.setOnCheckedChangeListener { _, isChecked ->
            PromptConfig.textItalic = isChecked
        }


        viewBinding.bsbFloatWidth.let { bsbFloatWidth ->
            val screenWidth = ScreenUtils.getScreenWidth()
            bsbFloatWidth.configBuilder
                .max(screenWidth * 0.8f)
                .min(screenWidth * 0.2f)
                .progress(PromptConfig.floatWidth)
                .build()
            bsbFloatWidth.onProgressChangedListener = object : OnBubbleProgressChangedListener() {
                override fun getProgressOnActionUp(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float
                ) {
                    PromptConfig.floatWidth = progressFloat
                }
            }
        }

        viewBinding.bsbFloatHeight.let { bsbFloatHeight ->
            val screenHeight = ScreenUtils.getScreenHeight()
            bsbFloatHeight.configBuilder
                .max(screenHeight * 0.8f)
                .min(screenHeight * 0.2f)
                .progress(PromptConfig.floatHeight)
                .build()
            bsbFloatHeight.onProgressChangedListener = object : OnBubbleProgressChangedListener() {
                override fun getProgressOnActionUp(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float
                ) {
                    PromptConfig.floatHeight = progressFloat
                }
            }
        }
    }

    override fun onCreateShowAnimation(): Animation? = AnimationHelper.asAnimation()
        .withScale(ScaleConfig().from(Direction.BOTTOM).duration(200L))
        .toShow()

    override fun onCreateDismissAnimation(): Animation? = AnimationHelper.asAnimation()
        .withScale(ScaleConfig().to(Direction.BOTTOM).duration(200L))
        .toDismiss()

    /**
     * 更新文字颜色
     */
    private fun updateFontColor(color: Int = PromptConfig.textColor) {
        viewBinding.atvFontTextColor.setPaintColor(color)
    }

    /**
     * 更新float背景色
     */
    private fun updateFloatBackgroundColor(color: Int = PromptConfig.floatBackgroundColor) {
        viewBinding.atvFloatBackgroundColor.setPaintColor(color)
    }

    /**
     * 显示颜色选择Dialog
     */
    private fun showColorPickerDialog(title: String, currentColor: Int, callColor: (Int) -> Unit) {
        context.colorPickerDialog {
            setTitle(title)
            setPreferenceName("")
            setPositiveButton("确定", ColorListener { color, _ ->
                callColor.invoke(color)
            })
            setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
            attachAlphaSlideBar(true)
            attachBrightnessSlideBar(true)

            // 会导致选取view越界，超出画盘，有时间再修复
            // colorPickerView.setPadding(64, 64, 64, 64)
            colorPickerView.setInitialColor(currentColor)
            colorPickerView.flagView = BubbleFlag(context)
                .also { flag -> flag.flagMode = FlagMode.FADE }
        }.show()
    }

    fun show() {
        showPopupWindow()
    }

    init {
        contentView = createPopupById(R.layout.popup_font_config)
    }
}