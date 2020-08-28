package com.seiko.danmaku.data.model

import com.seiko.danmaku.engine.Danma
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DanmaCommentBean(
    override var cid: Int = 0,
    override var p: String = "",
    override var m: String = ""
) : Danma