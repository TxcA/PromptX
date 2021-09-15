package com.itxca.promptx.popup

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.itxca.promptx.R
import com.itxca.promptx.databinding.PopupEditHistoryTextBinding
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

class EditHistoryTextPopup : BasePopupWindow {
    constructor(context: Context) : super(context)
    constructor(fragment: Fragment) : super(fragment)
    constructor(dialog: Dialog) : super(dialog)

    private lateinit var viewBinding: PopupEditHistoryTextBinding

    private var onDeleteCallback: (() -> Unit)? = null
    private var onSaveCallback: ((String) -> Unit)? = null


    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        viewBinding = PopupEditHistoryTextBinding.bind(contentView)

        viewBinding.ivHistoryDelete.setOnClickListener {
            AskPopup(context).show("确认删除?") {
                if (it) {
                    this@EditHistoryTextPopup.dismiss()
                    onDeleteCallback?.invoke()
                }
            }
        }

        viewBinding.ivHistorySave.setOnClickListener {
            onSaveCallback?.invoke(viewBinding.etEditHistory.text.toString())
            dismiss()
        }
    }

    override fun onCreateShowAnimation(): Animation? = AnimationHelper.asAnimation()
        .withScale(ScaleConfig().from(Direction.BOTTOM).duration(200L))
        .toShow()

    override fun onCreateDismissAnimation(): Animation? = AnimationHelper.asAnimation()
        .withScale(ScaleConfig().to(Direction.BOTTOM).duration(200L))
        .toDismiss()


    fun show(
        historyText: String,
        onDeleteCallback: (() -> Unit)? = null,
        onSaveCallback: (String) -> Unit
    ) {
        viewBinding.etEditHistory.setText(historyText)
        if (onDeleteCallback == null) {
            viewBinding.ivHistoryDelete.isInvisible = true
        } else {
            this.onDeleteCallback = onDeleteCallback
        }

        this.onSaveCallback = onSaveCallback
        showPopupWindow()
    }

    init {
        contentView =  createPopupById(R.layout.popup_edit_history_text)
        setOutSideDismiss(false)
//        setAdjustInputMethod(true)
//        setAdjustInputMode(FLAG_KEYBOARD_ALIGN_TO_ROOT or FLAG_KEYBOARD_ANIMATE_ALIGN)
    }
}