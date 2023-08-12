package com.fuadhev.musicplayer.di

import android.content.Context
import androidx.room.Room
import com.fuadhev.musicplayer.entity.Music
import com.fuadhev.musicplayer.repo.Repository
import com.fuadhev.musicplayer.room.MusicDao
import com.fuadhev.musicplayer.room.MyRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): MyRoomDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MyRoomDatabase::class.java,
            "last_played_musics"
        ).build()
    }

    @Provides
    @Singleton
    fun providesRepository(musicDao: MusicDao): Repository {
        return Repository(musicDao)
    }

    @Provides
    @Singleton
    fun provideMusicDao(db:MyRoomDatabase): MusicDao {
        return db.musicDao()
    }


}