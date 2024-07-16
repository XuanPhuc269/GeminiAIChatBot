package eu.tutorials.geminiaichatbotapp

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val _uiState: MutableStateFlow<HomeUIState> = MutableStateFlow(HomeUIState.Initial)
    val uiState = _uiState.asStateFlow()

    private lateinit var generativeModel: GenerativeModel

    init {
        val config = generationConfig {
            temperature = 0.70f // 0 to 1
        }
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash-latest",
            apiKey = BuildConfig.apiKey,
            generationConfig = config
        )
    }

    fun questioning(
        userInput: String,
        selectedImages: List<Bitmap>
    ) {
        _uiState.value = HomeUIState.Loading
        val prompt = "Take a look at images, and then answer the following question: $userInput"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val content = content {
                    for (bitmap in selectedImages) {
                        image(bitmap)
                    }
                    text(prompt)
                }
                var output = ""
                generativeModel.generateContentStream(content).collect {
                    output += it.text
                    _uiState.value = HomeUIState.Success(output)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUIState.Error(e.localizedMessage ?: "Error in Generating Content")
            }
        }
    }

}


sealed interface HomeUIState {
    object Initial: HomeUIState
    object Loading: HomeUIState
    data class Success(
        val outputText: String
    ): HomeUIState
    data class Error(val error: String): HomeUIState
}