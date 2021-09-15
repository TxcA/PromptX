package com.itxca.promptx

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx
 * Create Time  : 2021-09-11 0:10
 * Create By    : @author xIao
 * Version      : 1.0.0
 *
 **/

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        MMKV.initialize(this)
    }
}