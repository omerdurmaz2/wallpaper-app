package com.example.wallpaperapp.di

import com.example.wallpaperapp.service.RestControllerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun getRestController(): RestControllerFactory = RestControllerFactory()
}