package org.delcom.pam_p4_ifs23009.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23009.R
import org.delcom.pam_p4_ifs23009.helper.*
import org.delcom.pam_p4_ifs23009.helper.SuspendHelper.SnackBarType
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishData
import org.delcom.pam_p4_ifs23009.ui.components.*
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishActionUIState
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishUIState
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishViewModel

@Composable
fun FishesDetailScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    fishViewModel: FishViewModel,
    fishId: String
) {
    val uiStateFish by fishViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var isConfirmDelete by remember { mutableStateOf(false) }
    var fish by remember { mutableStateOf<ResponseFishData?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        fishViewModel.getFishById(fishId)
    }

    LaunchedEffect(uiStateFish.fish) {
        if (uiStateFish.fish !is FishUIState.Loading) {
            if (uiStateFish.fish is FishUIState.Success) {
                fish = (uiStateFish.fish as FishUIState.Success).data
                isLoading = false
            } else {
                RouteHelper.back(navController)
            }
        }
    }

    LaunchedEffect(uiStateFish.fishAction) {
        when (val state = uiStateFish.fishAction) {
            is FishActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.SUCCESS, "Berhasil menghapus data")
                fishViewModel.resetFishAction()
                RouteHelper.to(navController, ConstHelper.RouteNames.Fishes.path, true)
                isLoading = false
            }
            is FishActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.ERROR, state.message)
                isLoading = false
            }
            else -> {}
        }
    }

    if (isLoading || fish == null) {
        LoadingUI()
        return
    }

    val detailMenuItems = listOf(
        TopAppBarMenuItem(text = "Ubah Data", icon = Icons.Filled.Edit, onClick = {
            RouteHelper.to(navController, ConstHelper.RouteNames.FishesEdit.path.replace("{fishId}", fish!!.id))
        }),
        TopAppBarMenuItem(text = "Hapus Data", icon = Icons.Filled.Delete, onClick = { isConfirmDelete = true }),
    )

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(navController = navController, title = fish!!.nama ?: "", showBackButton = true, customMenuItems = detailMenuItems)
        Box(modifier = Modifier.weight(1f)) {
            FishesDetailUI(fish = fish!!)
            BottomDialog(
                type = BottomDialogType.ERROR,
                show = isConfirmDelete,
                onDismiss = { isConfirmDelete = false },
                title = "Konfirmasi Hapus",
                message = "Yakin ingin menghapus ikan ini?",
                confirmText = "Hapus",
                onConfirm = {
                    isLoading = true
                    fishViewModel.deleteFish(fishId)
                },
                cancelText = "Batal",
                destructiveAction = true
            )
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun FishesDetailUI(fish: ResponseFishData) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        AsyncImage(
            model = ToolsHelper.getFishImageUrl(fish.id),
            contentDescription = fish.nama ?: "",
            placeholder = painterResource(R.drawable.img_placeholder),
            error = painterResource(R.drawable.img_placeholder),
            modifier = Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Fit
        )
        Text(text = fish.nama ?: "", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        
        FishDetailCard(title = "Harga", content = fish.harga ?: "")
        FishDetailCard(title = "Deskripsi", content = fish.deskripsi ?: "")
        FishDetailCard(title = "Asal", content = fish.asal ?: "")
        FishDetailCard(title = "Ukuran", content = fish.ukuran ?: "")
        FishDetailCard(title = "Masa Hidup", content = fish.masaHidup ?: "")
        FishDetailCard(title = "Tingkat Kesulitan", content = fish.tingkatKesulitan ?: "")
    }
}

@Composable
fun FishDetailCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(text = content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
