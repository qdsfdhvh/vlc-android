package com.seiko.danmaku.di

import com.seiko.danmaku.engine.internal.DanmakuEngineOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import master.flame.danmaku.danmaku.model.IDisplayer
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DanmaModule {

    @Provides
    @Singleton
    fun provideDanmakuEngineOptions(): DanmakuEngineOptions {
        return DanmakuEngineOptions {
            setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 2.5f)
//            //设置防弹幕重叠
//            .preventOverlapping()
            //合并重复弹幕
            isDuplicateMergingEnabled = false
            //弹幕滚动速度
            setScrollSpeedFactor(1.4f)
            //弹幕文字大小
            setScaleTextSize(2.4f)
//        //弹幕文字透明度
//        .setDanmakuTransparency(0.8f)
            // 是否显示滚动弹幕
            r2LDanmakuVisibility = true
            // 是否显示顶部弹幕
            ftDanmakuVisibility = true
            // 是否显示底部弹幕
            fbDanmakuVisibility = false
            // 同屏数量限制
            setMaximumVisibleSizeInScreen(100)

            setDanmakuMargin(40)
        }
    }

}