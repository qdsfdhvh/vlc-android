package com.seiko.danmaku.di

import com.seiko.danmaku.factory.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@InstallIn(ApplicationComponent::class)
@Module
abstract class DanmaResultModule {

    companion object {
        private const val KEY_FILE = "file"
        private const val KEY_SMB = "smb"
        private const val KEY_FTP  = "ftp"
        private const val KEY_SFTP = "sftp"
        private const val KEY_HTTP = "http"
        private const val KEY_HTTPS = "https"
    }

    @Binds
    @IntoMap
    @StringKey(KEY_FILE)
    abstract fun provideDanmaResultWithFile(result: DanmaResultWithFile): IDanmaResult

    @Binds
    @IntoMap
    @StringKey(KEY_SMB)
    abstract fun provideDanmaResultWithSmb(result: DanmaResultWithSmb): IDanmaResult

    @Binds
    @IntoMap
    @StringKey(KEY_FTP)
    abstract fun provideDanmaResultWithFtp(result: DanmaResultWithFtp): IDanmaResult

    @Binds
    @IntoMap
    @StringKey(KEY_SFTP)
    abstract fun provideDanmaResultWithSftp(result: DanmaResultWithFtp): IDanmaResult

    @Binds
    @IntoMap
    @StringKey(KEY_HTTP)
    abstract fun provideDanmaResultWithHttp(result: DanmaResultWithNet): IDanmaResult

    @Binds
    @IntoMap
    @StringKey(KEY_HTTPS)
    abstract fun provideDanmaResultWithHttps(result: DanmaResultWithNet): IDanmaResult
}