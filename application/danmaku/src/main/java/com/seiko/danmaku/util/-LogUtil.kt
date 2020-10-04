package com.seiko.danmaku.util

import android.util.Log

var LOG_ENABLE = true

const val LOG_TAG = "Danmaku"

internal fun log(msg: String) {
    if (LOG_ENABLE) {
        Log.d(LOG_TAG, msg)
    }
}

internal fun log(tag: String, msg: String) {
    if (LOG_ENABLE) {
        Log.d(tag, msg)
    }
}

internal fun loge(e: Throwable?) {
    if (LOG_ENABLE) {
        Log.e(LOG_TAG, e?.message, e)
    }
}

internal fun loge(tag: String, msg: String, e: Throwable?) {
    if (LOG_ENABLE) {
        Log.e(tag, msg, e)
    }
}
