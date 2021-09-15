package com.itxca.promptx

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.TimeUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.itxca.promptx.data.HistoryText
import com.itxca.promptx.data.PromptCache
import com.itxca.promptx.databinding.ActivityMainBinding
import com.itxca.promptx.popup.AskPopup
import com.itxca.promptx.popup.EditHistoryTextPopup
import com.itxca.promptx.popup.FontConfigPopup
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx
 * Create Time  : 2021-09-11 0:11
 * Create By    : @author xIao
 * Version      : 1.0.0
 *
 **/
class MainActivity : AppCompatActivity() {

    private val floatViewModel by viewModels<FloatWindowViewModel>()
    private val viewBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val adapter by lazy {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        viewBinding.contentScrolling.rvHistory.linear(reverseLayout = true).setup {
            addType<HistoryText>(R.layout.item_history)
            onBind {
                getModel<HistoryText>().let { itemData ->
                    findView<TextView>(R.id.tv_history_text).text = itemData.text
                    findView<TextView>(R.id.tv_history_time).text =
                        TimeUtils.millis2String(itemData.editTime, sdf)
                }
            }

            onClick(R.id.iv_history_edit, R.id.iv_history_show) { ids ->
                getModel<HistoryText>().let { itemData ->
                    when (ids) {
                        R.id.iv_history_edit -> {
                            EditHistoryTextPopup(this@MainActivity).show(
                                itemData.text,
                                onDeleteCallback = {
                                    deleteHistory(itemData)
                                },
                                onSaveCallback = {
                                    if (it.isNotBlank()) {
                                        editHistory(itemData, it)
                                    }
                                })
                        }
                        R.id.iv_history_show -> {
                            floatViewModel.createTextFloat(this@MainActivity,itemData.text)
                            // FloatWindow.createTextFloat(this@MainActivity, itemData.text)
                            ActivityUtils.startHomeActivity()
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbar)
        viewBinding.toolbarLayout.title = title

        viewBinding.fabFontConfig.setOnClickListener {
            FontConfigPopup(this@MainActivity).show()
        }

        viewBinding.fabAddText.setOnClickListener {
            EditHistoryTextPopup(this@MainActivity).show("") {
                if (it.isNotBlank()) {
                    addHistory(it)
                }
            }
        }

        adapter.addModels(PromptCache.history)
    }

    override fun onResume() {
        super.onResume()
        viewBinding.root.post {
            val currentClipboardText = ClipboardUtils.getText().toString()
            if (currentClipboardText.isNotBlank()
                && PromptCache.lastClipboardText != currentClipboardText
            ) {
                PromptCache.lastClipboardText = currentClipboardText
                AskPopup(this).show(
                    "直接添加?",
                    if (currentClipboardText.length < 48) {
                        currentClipboardText
                    } else {
                        currentClipboardText.substring(0, 45) + "..."
                    }
                ) {
                    if (it) {
                        addHistory(currentClipboardText)
                    }
                }
            }
        }
    }

    private fun addHistory(text: String) {
        val history = HistoryText(text)
        PromptCache.history = PromptCache.history.toMutableList().also {
            it.add(history)
        }

        adapter.addModels(mutableListOf(history))
    }

    private fun deleteHistory(currentHistory: HistoryText) {
        val historyCache = PromptCache.history
        if (historyCache.contains(currentHistory)) {
            val newHistory = historyCache.toMutableList()
            newHistory.remove(currentHistory)

            PromptCache.history = newHistory
            adapter.models = newHistory
        }
    }

    private fun editHistory(currentHistory: HistoryText, text: String) {
        val historyCache = PromptCache.history
        if (historyCache.contains(currentHistory)) {
            val newHistory = historyCache.toMutableList()
            newHistory[historyCache.indexOf(currentHistory)] = HistoryText(text)

            PromptCache.history = newHistory
            adapter.models = newHistory
        }
    }
}