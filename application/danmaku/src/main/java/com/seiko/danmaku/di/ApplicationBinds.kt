package com.seiko.danmaku.di

import com.seiko.danmaku.DanmaService
import com.seiko.danmaku.DanmaServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class ApplicationBinds {

    @Binds
    @Singleton
    abstract fun bindDanmaService(impl: DanmaServiceImpl): DanmaService

}