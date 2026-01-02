package s3359881.ganeshbethi.newssharingapp.savenews

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import s3359881.ganeshbethi.newssharingapp.screens.NewsPost
import kotlinx.coroutines.launch

class SavedNewsViewModel(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).savedNewsDao()
    private val repo = SavedNewsRepository(dao)

    var savedList by mutableStateOf<List<SavedNewsEntity>>(emptyList())
    var savedMap = mutableStateMapOf<String, Boolean>()

    init {
        loadSaved()
    }

    private fun loadSaved() {
        viewModelScope.launch {
            savedList = repo.getAllSaved()
            savedMap.clear()
            savedList.forEach { savedMap[it.newsId] = true }
        }
    }

    fun toggleSave(news: NewsPost) {
        viewModelScope.launch {
            val isSaved = repo.isSaved(news.newsId)

            if (isSaved) {
                repo.unsave(news)
                savedMap[news.newsId] = false
            } else {
                repo.save(news)
                savedMap[news.newsId] = true
            }

            loadSaved()
        }
    }
}
