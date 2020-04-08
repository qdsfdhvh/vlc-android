package org.videolan.vlc.danma

import com.seiko.danma.model.Danma

data class DanmaResultBean(
    // 弹幕评论集合
    val comments: List<Danma>,
    // 弹幕对于当前视频的偏移时间
    val shift: Long
)