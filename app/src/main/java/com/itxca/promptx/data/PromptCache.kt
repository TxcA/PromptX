package com.itxca.promptx.data

import com.drake.serialize.serialize.serial
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx.data
 * Create Time  : 2021/9/11 0:07
 * Create By    : @author xIao
 * Version      : 1.0.0
 *
 **/

object PromptCache {

    private var cacheData: String by serial("")

    var history: List<HistoryText> = emptyList()
        get() = kotlin.runCatching {
            Json.decodeFromString<List<HistoryText>>(cacheData)
        }.getOrDefault(emptyList())
        set(value) {
            kotlin.runCatching { cacheData = Json.encodeToString(value) }
            field = value
        }


    var lastClipboardText: String by serial("")
}