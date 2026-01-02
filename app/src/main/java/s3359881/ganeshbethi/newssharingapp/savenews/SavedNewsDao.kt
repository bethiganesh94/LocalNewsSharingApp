package s3359881.ganeshbethi.newssharingapp.savenews

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNews(news: SavedNewsEntity)

    @Delete
    suspend fun removeNews(news: SavedNewsEntity)

    @Query("SELECT * FROM saved_news ORDER BY timestamp DESC")
    suspend fun getSavedNews(): List<SavedNewsEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_news WHERE newsId = :id)")
    suspend fun isSaved(id: String): Boolean
}
