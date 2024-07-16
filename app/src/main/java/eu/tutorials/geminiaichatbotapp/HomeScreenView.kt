package eu.tutorials.geminiaichatbotapp

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun AppContent(viewModel: HomeViewModel = viewModel()) {
    val appUIState = viewModel.uiState.collectAsState()

    HomeScreenView(
        uiState = appUIState.value
    ) {
        inputText, selectedItems ->
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(uiState: HomeUIState = HomeUIState.Loading,
                   onSendClicked: (String, List<Uri>) -> Unit) {

    var userQuestion by rememberSaveable() {
        mutableStateOf("")
    }

    val imageUris = rememberSaveable(saver = UriCustomServer()) {
        mutableStateListOf()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Gemini AI Chat Bot") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Column {
                Row(
                    modifier = Modifier.padding(16.dp)
                ){
                    // Add image Icon
                    IconButton(onClick = { /*TODO*/ },
                        modifier = Modifier.padding(4.dp)) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add Image")
                    }

                    // Input Field
                    OutlinedTextField(value = userQuestion,
                        onValueChange = {
                            userQuestion = it
                        },
                        placeholder = { Text(text = "Upload image and ask question") }
                    )

                    // Send Button
                    IconButton(onClick = { /*TODO*/ },
                        modifier = Modifier.padding(4.dp)) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                    }

                }
            }
        }
    ){
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when(uiState) {
                is HomeUIState.Initial -> {}
                is HomeUIState.Loading -> {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUIState.Success -> {
                    Card(modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = uiState.outputText)
                    }
                }
                is HomeUIState.Error -> {
                    Card(modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(text = uiState.error)
                    }
                }
            }
        }
    }
}
