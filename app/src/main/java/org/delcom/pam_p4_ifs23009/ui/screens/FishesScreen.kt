package org.delcom.pam_p4_ifs23009.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23009.R
import org.delcom.pam_p4_ifs23009.helper.ConstHelper
import org.delcom.pam_p4_ifs23009.helper.RouteHelper
import org.delcom.pam_p4_ifs23009.helper.ToolsHelper
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishData
import org.delcom.pam_p4_ifs23009.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23009.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23009.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishViewModel
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishesUIState

@Composable
fun FishesScreen(
    navController: NavHostController,
    fishViewModel: FishViewModel
) {
    val uiStateFish by fishViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var fishes by remember { mutableStateOf<List<ResponseFishData>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun fetchFishesData() {
        isLoading = true
        errorMessage = null
        val query = if (searchQuery.text.isEmpty()) null else searchQuery.text
        fishViewModel.getAllFishes(query)
    }

    LaunchedEffect(Unit) {
        fetchFishesData()
    }

    LaunchedEffect(uiStateFish.fishes) {
        when (val state = uiStateFish.fishes) {
            is FishesUIState.Success -> {
                isLoading = false
                fishes = state.data
                errorMessage = null
            }
            is FishesUIState.Error -> {
                isLoading = false
                fishes = emptyList()
                errorMessage = state.message
            }
            is FishesUIState.Loading -> {
                // Jangan set isLoading false di sini karena fetchFishesData sudah set true
            }
        }
    }

    if (isLoading) {
        LoadingUI()
        return
    }

    fun onOpen(fishId: String) {
        RouteHelper.to(
            navController = navController,
            destination = "fishes/${fishId}"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBarComponent(
            navController = navController,
            title = "Fishes",
            showBackButton = false,
            withSearch = true,
            searchQuery = searchQuery,
            onSearchQueryChange = { query -> searchQuery = query },
            onSearchAction = { fetchFishesData() }
        )
        
        Box(modifier = Modifier.weight(1f)) {
            FishesUI(fishes = fishes, errorMessage = errorMessage, onOpen = ::onOpen)

            FloatingActionButton(
                onClick = {
                    RouteHelper.to(navController, ConstHelper.RouteNames.FishesAdd.path)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Ikan")
            }
        }
        
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun FishesUI(fishes: List<ResponseFishData>, errorMessage: String?, onOpen: (String) -> Unit) {
    if (errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
        }
    } else if (fishes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Tidak ada data!", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(fishes) { fish ->
                FishItemUI(fish, onOpen)
            }
        }
    }
}

@Composable
fun FishItemUI(fish: ResponseFishData, onOpen: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onOpen(fish.id) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            AsyncImage(
                model = ToolsHelper.getFishImageUrl(fish.id),
                contentDescription = fish.nama ?: "Gambar Ikan",
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                modifier = Modifier.size(70.dp).clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fish.nama ?: "Tanpa Nama",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = fish.deskripsi ?: "Tidak ada deskripsi",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Price: ${fish.harga ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
