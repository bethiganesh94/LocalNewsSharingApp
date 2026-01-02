package s3359881.ganeshbethi.newssharingapp.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import s3359881.ganeshbethi.newssharingapp.Article
import s3359881.ganeshbethi.newssharingapp.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class NewsViewModel : ViewModel() {

    var newsList by mutableStateOf<List<Article>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        getNews()
    }

    private fun getNews() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.api.getTopHeadlines(apiKey = "ecb1729e2f2c4920bb7d2a45c67f4c2d")
                newsList = response.articles.filter { it.urlToImage != null }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isLoading = false
        }
    }
}

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String
    ): NewsResponse
}

object ApiClient {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(NewsApiService::class.java)
}
