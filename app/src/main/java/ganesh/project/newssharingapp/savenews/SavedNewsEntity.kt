package ganesh.project.newssharingapp.savenews

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_news")
data class SavedNewsEntity(
    @PrimaryKey val newsId: String,
    val newsTitle: String,
    val newsCategory: String,
    val newsContent: String,
    val imageUrl: String,
    val place: String,
    val date: String,
    val timestamp: Long,
    val author: String
)
