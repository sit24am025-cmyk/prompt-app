package com.idchan.prompt.ui.favorites

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idchan.prompt.domain.model.AnalysisResult
import com.idchan.prompt.ui.components.IDChanTopAppBar
import com.idchan.prompt.ui.history.HistoryCard
import com.idchan.prompt.ui.theme.ErrorRed

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onOpenItem: (AnalysisResult) -> Unit
) {
    val context = LocalContext.current
    val favorites by viewModel.favorites.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        IDChanTopAppBar(title = "Favorite Prompts")

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = ErrorRed.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Favorite Prompts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the heart icon on any generated prompt to save it here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(favorites, key = { it.id }) { item ->
                    HistoryCard(
                        item = item,
                        onClick = {
                            val result = AnalysisResult(
                                id = item.id,
                                imageUri = item.imageUri,
                                masterPrompt = item.masterPrompt,
                                negativePrompt = item.negativePrompt,
                                mode = item.mode,
                                timestamp = item.timestamp,
                                isFavorite = item.isFavorite
                            )
                            onOpenItem(result)
                        },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("ID Chan Prompt", item.masterPrompt)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Prompt copied!", Toast.LENGTH_SHORT).show()
                        },
                        onFavoriteToggle = { viewModel.toggleFavorite(item.id) },
                        onDelete = { viewModel.deleteItem(item.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
