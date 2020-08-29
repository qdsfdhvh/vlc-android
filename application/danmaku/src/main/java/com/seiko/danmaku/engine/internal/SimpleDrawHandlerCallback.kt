package com.seiko.danmaku.engine.internal

import com.seiko.danmaku.engine.IDanmakuEngine
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer

open class SimpleDrawHandlerCallback : IDanmakuEngine.Callback {

    override fun drawingFinished() {}

    override fun danmakuShown(danmaku: BaseDanmaku?) {}

    override fun prepared(size: Int?) {}

    override fun updateTimer(timer: DanmakuTimer?) {}

}