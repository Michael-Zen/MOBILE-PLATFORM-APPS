package com.example.ch10.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import android.content.Context
import androidx.room.Room
import com.example.ch10.data.local.ArticleDao
import com.example.ch10.data.local.NewsDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NewsDatabase =
        Room.databaseBuilder(context, NewsDatabase::class.java, NewsDatabase.NAME).build()

    @Provides
    fun provideArticleDao(database: NewsDatabase): ArticleDao = database.articleDao()
}
