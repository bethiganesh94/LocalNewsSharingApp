package ganesh.project.newssharingapp.savenews

import ganesh.project.newssharingapp.screens.NewsPost

class SavedNewsRepository(private val dao: SavedNewsDao) {

    suspend fun save(news: NewsPost) {
        dao.saveNews(
            SavedNewsEntity(
                newsId = news.newsId,
                newsTitle = news.newsTitle,
                newsCategory = news.newsCategory,
                newsContent = news.newsContent,
                imageUrl = news.imageUrl,
                place = news.place,
                date = news.date,
                timestamp = news.timestamp,
                author = news.author
            )
        )
    }

    suspend fun unsave(news: NewsPost) {
        dao.removeNews(
            SavedNewsEntity(
                newsId = news.newsId,
                newsTitle = news.newsTitle,
                newsCategory = news.newsCategory,
                newsContent = news.newsContent,
                imageUrl = news.imageUrl,
                place = news.place,
                date = news.date,
                timestamp = news.timestamp,
                author = news.author
            )
        )
    }

    suspend fun isSaved(id: String) = dao.isSaved(id)

    suspend fun getAllSaved() = dao.getSavedNews()
}
