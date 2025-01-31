package com.unitip.mobile.features.setting.commons

import com.unitip.mobile.features.setting.data.sources.AccountApi
import com.unitip.mobile.features.setting.data.sources.AuthApi
import com.unitip.mobile.shared.commons.configs.ApiConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object Modules {
    @Provides
    fun provideAuthApi(): AuthApi = ApiConfig.retrofit.create(AuthApi::class.java)

    @Provides
    fun provideAccountApi(): AccountApi = ApiConfig.retrofit.create((AccountApi::class.java))
}