package com.example.ch10.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import com.example.ch10.util.Clock

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideClock(): Clock = Clock { System.currentTimeMillis() }
}
