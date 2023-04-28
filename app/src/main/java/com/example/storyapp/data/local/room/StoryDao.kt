package com.example.storyapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.remote.response.Story

@Dao
interface StoryDao {

    @Query("SELECT * FROM Story")
    fun getStories(): List<Story>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStory(story: Story): Long

    @Query("DELETE FROM Story")
    suspend fun clearStory()

}