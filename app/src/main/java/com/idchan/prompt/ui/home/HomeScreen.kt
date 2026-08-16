package com.idchan.prompt.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.idchan.prompt.domain.model.AnalysisResult
import com.idchan.prompt.ui.components.IDChanTopAppBar
import com.idchan.prompt.ui.components.ImagePickerCard
import com.idchan.prompt.ui.components.LoadingOverlay
import com.idchan.prompt.ui.components.ModeChipRow
import com.idchan.prompt.ui.theme.CyanPrimary
import com.idchan.prompt.ui.theme.ErrorRed
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToResult: (AnalysisResult) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            // Convert bitmap to temporary file URI
            val tempUri = com.idchan.prompt.core.util.ImageUtils.saveBitmapToCache(context, bitmap)
            viewModel.onImageSelected(tempUri)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            IDChanTopAppBar()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "ID Chan Prompt",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Turn any image into an exact AI prompt",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Prompt Mode Selection Chips
                Text(
                    text = "Analysis Mode",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ModeChipRow(
                    selectedMode = uiState.selectedMode,
                    onModeSelected = { viewModel.onModeSelected(it) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Image Picker / Preview Card
                ImagePickerCard(
                    selectedImageUri = uiState.selectedImageUri,
                    onChooseImage = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onTakePhoto = {
                        cameraLauncher.launch(null)
                    },
                    onAnalyzeImage = {
                        viewModel.analyzeImage { result ->
                            onNavigateToResult(result)
                        }
                    },
                    onChangeImage = {
                        viewModel.resetImage()
                    }
                )

                // Error Card if any
                uiState.errorMessage?.let { errorText ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ErrorRed,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.analyzeImage { result ->
                                        onNavigateToResult(result)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                            ) {
                                Text(text = "Try Again", color = MaterialTheme.colorScheme.surface)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp)) // Bottom spacing for navigation bar
            }
        }

        // Animated Loading Overlay
        LoadingOverlay(isLoading = uiState.isLoading)
    }
}
