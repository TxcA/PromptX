package com.itxca.promptx.data

import kotlinx.serialization.Serializable

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx.data
 * Create Time  : 2021/9/11 13:03
 * Create By    : @author xIao
 * Version      : 1.0.0
 *
 **/

@Serializable
data class HistoryText(
    val text: String,
    val editTime: Long = System.currentTimeMillis()
)
