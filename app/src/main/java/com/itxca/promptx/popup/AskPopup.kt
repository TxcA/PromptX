package com.itxca.promptx.popup

import android.app.Dialog
import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.itxca.promptx.R
import com.itxca.promptx.databinding.PopupAskBinding
import razerdp.basepopup.BasePopupWindow

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx.popup
 * Create Time  : 2021/9/11 11:41
 * Create By    : @author xIao
 * Version      : 1.0.0
 *
 **/

class AskPopup : BasePopupWindow {
    constructor(context: Context) : super(context)
    constructor(fragment: Fragment) : super(fragment)
    constructor(dialog: Dialog) : super(dialog)

    private lateinit var viewBinding: PopupAskBinding
    private var onClickCallback: ((Boolean) -> Unit)? = null


    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        viewBinding = PopupAskBinding.bind(contentView)
        initView()
    }

    private fun initView() {
        viewBinding.tvCancel.setOnClickListener {
            onClickCallback?.invoke(false)
            dismiss()
        }

        viewBinding.tvOk.setOnClickListener {
            onClickCallback?.invoke(true)
            dismiss()
        }
    }

    private fun updateView(content: String, showCancel: Boolean) {
        if (content.isNotBlank()) {
            viewBinding.tvContent.text = content
        } else {
            viewBinding.tvContent.isGone = true
        }

        viewBinding.tvCancel.isVisible = showCancel
    }

    fun show(
        title: SpannableStringBuilder,
        content: String = "",
        showCancel: Boolean = true,
        onClickCallback: (Boolean) -> Unit
    ) {
        this.viewBinding.tvTitle.text = title
        this.onClickCallback = onClickCallback
        updateView(content, showCancel)
        showPopupWindow()
    }

    fun show(
        title: String,
        content: String = "",
        showCancel: Boolean = true,
        onClickCallback: (Boolean) -> Unit
    ) {
        this.viewBinding.tvTitle.text = title
        this.onClickCallback = onClickCallback
        updateView(content, showCancel)
        showPopupWindow()
    }

    init {
        contentView = createPopupById(R.layout.popup_ask)
        setOutSideDismiss(false)
    }
}