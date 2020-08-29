package com.seiko.danmaku.engine

import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.controller.IDanmakuView

interface IDanmakuEngine {

    /**
     * 绑定播放器
     * @param danmaView 弹幕视图
     */
    fun bindDanmakuView(danmaView: IDanmakuView)

    /**
     * 弹幕集合
     * @param danma 弹幕集合
     * @param shift 偏移时间
     */
    fun setDanmaList(danma: List<Danma>, shift: Long)

    /**
     * 开始
     */
    fun play()

    /**
     * 停止
     */
    fun pause()

    /**
     * 注销
     */
    fun release()

    /**
     * 显示弹幕
     */
    fun show()

    /**
     * 影藏弹幕
     */
    fun hide()

    /**
     * 播放速度
     */
    fun setRate(rate: Float)

    /**
     * 跳转
     * @param position 位置
     */
    fun seekTo(position: Long)

    /**
     * 烈焰弹幕回调
     */
    fun setCallback(callback: Callback?)

    interface Callback : DrawHandler.Callback {
        fun prepared(size: Int?)

        override fun prepared() {
            prepared(null)
        }
    }
}