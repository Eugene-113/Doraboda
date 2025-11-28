package com.univ.doraboda.DI

import android.content.Context
import androidx.room.Room
import com.univ.doraboda.dao.EmotionDao
import com.univ.doraboda.dao.MemoDao
import com.univ.doraboda.database.DorabodaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context)
    = Room.databaseBuilder(context, DorabodaDatabase::class.java, "doraboda_db").build()

    @Provides
    fun provideEmotionDao(database: DorabodaDatabase): EmotionDao = database.emotionDao()

    @Provides
    fun provideMemoDao(database: DorabodaDatabase): MemoDao = database.memoDao()
}