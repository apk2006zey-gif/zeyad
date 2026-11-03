package com.example.data.local

import androidx.room.*
import com.example.data.model.LoveLetter
import com.example.data.model.MemoryItem
import com.example.data.model.StoryMilestone
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryItem>>

    @Query("SELECT * FROM memories WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteMemories(): Flow<List<MemoryItem>>

    @Query("SELECT * FROM memories WHERE mediaType = :type ORDER BY timestamp DESC")
    fun getMemoriesByType(type: String): Flow<List<MemoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryItem): Long

    @Update
    suspend fun updateMemory(memory: MemoryItem)

    @Delete
    suspend fun deleteMemory(memory: MemoryItem)

    @Query("SELECT COUNT(*) FROM memories")
    suspend fun getCount(): Int

    @Query("DELETE FROM memories")
    suspend fun clearAllMemories()
}

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestones ORDER BY timestamp ASC")
    fun getAllMilestones(): Flow<List<StoryMilestone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: StoryMilestone): Long

    @Delete
    suspend fun deleteMilestone(milestone: StoryMilestone)

    @Query("SELECT COUNT(*) FROM milestones")
    suspend fun getCount(): Int

    @Query("DELETE FROM milestones")
    suspend fun clearAllMilestones()
}

@Dao
interface LoveLetterDao {
    @Query("SELECT * FROM love_letters ORDER BY timestamp DESC")
    fun getAllLetters(): Flow<List<LoveLetter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: LoveLetter): Long

    @Delete
    suspend fun deleteLetter(letter: LoveLetter)

    @Query("SELECT COUNT(*) FROM love_letters")
    suspend fun getCount(): Int

    @Query("DELETE FROM love_letters")
    suspend fun clearAllLetters()
}

@Database(
    entities = [MemoryItem::class, StoryMilestone::class, LoveLetter::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun loveLetterDao(): LoveLetterDao
}
