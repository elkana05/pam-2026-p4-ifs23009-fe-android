package org.delcom.pam_p4_ifs23009.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23009.R
import org.delcom.pam_p4_ifs23009.helper.*
import org.delcom.pam_p4_ifs23009.helper.SuspendHelper.SnackBarType
import org.delcom.pam_p4_ifs23009.helper.ToolsHelper.toRequestBodyText
import org.delcom.pam_p4_ifs23009.helper.ToolsHelper.uriToMultipart
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishData
import org.delcom.pam_p4_ifs23009.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23009.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23009.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishActionUIState
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishUIState
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishViewModel

@Composable
fun FishesEditScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    fishViewModel: FishViewModel,
    fishId: String
) {
    val uiStateFish by fishViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var fish by remember { mutableStateOf<ResponseFishData?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        fishViewModel.resetFishAction()
        fishViewModel.getFishById(fishId)
    }

    LaunchedEffect(uiStateFish.fish) {
        if (uiStateFish.fish !is FishUIState.Loading) {
            if (uiStateFish.fish is FishUIState.Success) {
                fish = (uiStateFish.fish as FishUIState.Success).data
                isLoading = false
            } else {
                RouteHelper.back(navController)
                isLoading = false
            }
        }
    }

    fun onSave(
        context: Context,
        name: String,
        price: String,
        description: String,
        origin: String,
        size: String,
        lifespan: String,
        difficulty: String,
        file: Uri?
    ) {
        isLoading = true
        var filePart: MultipartBody.Part? = null
        if (file != null) {
            filePart = uriToMultipart(context, file, "file")
        }
        
        fishViewModel.putFish(
            fishId = fishId,
            nama = name.toRequestBodyText(),
            deskripsi = description.toRequestBodyText(),
            harga = price.toRequestBodyText(),
            asal = origin.toRequestBodyText(),
            ukuran = size.toRequestBodyText(),
            masaHidup = lifespan.toRequestBodyText(),
            tingkatKesulitan = difficulty.toRequestBodyText(),
            file = filePart
        )
    }

    LaunchedEffect(uiStateFish.fishAction) {
        when (val state = uiStateFish.fishAction) {
            is FishActionUIState.Success -> {
                isLoading = false
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SnackBarType.SUCCESS,
                    message = "Berhasil mengubah data"
                )
                fishViewModel.resetFishAction()
                RouteHelper.to(
                    navController = navController,
                    destination = ConstHelper.RouteNames.FishesDetail.path.replace("{fishId}", fishId),
                    popUpTo = ConstHelper.RouteNames.FishesDetail.path.replace("{fishId}", fishId),
                    removeBackStack = true
                )
            }
            is FishActionUIState.Error -> {
                isLoading = false
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SnackBarType.ERROR,
                    message = state.message
                )
            }
            else -> {}
        }
    }

    if (isLoading || fish == null) {
        LoadingUI()
        return
    }

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(navController = navController, title = "Ubah Ikan", showBackButton = true)
        Box(modifier = Modifier.weight(1f)) {
            FishesEditUI(fish = fish!!, onSave = ::onSave)
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun FishesEditUI(
    fish: ResponseFishData,
    onSave: (Context, String, String, String, String, String, String, String, Uri?) -> Unit
) {
    val alertState = remember { mutableStateOf(AlertState()) }
    var dataFile by remember { mutableStateOf<Uri?>(null) }
    var dataName by remember { mutableStateOf(fish.nama ?: "") }
    var dataPrice by remember { mutableStateOf(fish.harga ?: "") }
    var dataDescription by remember { mutableStateOf(fish.deskripsi ?: "") }
    var dataOrigin by remember { mutableStateOf(fish.asal ?: "") }
    var dataSize by remember { mutableStateOf(fish.ukuran ?: "") }
    var dataLifespan by remember { mutableStateOf(fish.masaHidup ?: "") }
    var dataDifficulty by remember { mutableStateOf(fish.tingkatKesulitan ?: "") }
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    val priceFocus = remember { FocusRequester() }
    val descFocus = remember { FocusRequester() }
    val originFocus = remember { FocusRequester() }
    val sizeFocus = remember { FocusRequester() }
    val lifespanFocus = remember { FocusRequester() }
    val difficultyFocus = remember { FocusRequester() }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> dataFile = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (dataFile != null) {
                    AsyncImage(
                        model = dataFile,
                        contentDescription = "Pratinjau Gambar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(R.drawable.img_placeholder),
                        error = painterResource(R.drawable.img_placeholder)
                    )
                } else {
                    AsyncImage(
                        model = ToolsHelper.getFishImageUrl(fish.id),
                        contentDescription = "Pratinjau Gambar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(R.drawable.img_placeholder),
                        error = painterResource(R.drawable.img_placeholder)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tap untuk mengganti gambar", style = MaterialTheme.typography.bodySmall)
        }

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            cursorColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        OutlinedTextField(
            value = dataName,
            onValueChange = { dataName = it },
            label = { Text("Nama", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { priceFocus.requestFocus() })
        )
        OutlinedTextField(
            value = dataPrice,
            onValueChange = { dataPrice = it },
            label = { Text("Harga", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().focusRequester(priceFocus),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { descFocus.requestFocus() })
        )
        OutlinedTextField(
            value = dataDescription,
            onValueChange = { dataDescription = it },
            label = { Text("Deskripsi", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().height(120.dp).focusRequester(descFocus),
            colors = textFieldColors,
            maxLines = 5,
            minLines = 3,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { originFocus.requestFocus() })
        )
        OutlinedTextField(
            value = dataOrigin,
            onValueChange = { dataOrigin = it },
            label = { Text("Asal", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().focusRequester(originFocus),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { sizeFocus.requestFocus() })
        )
        OutlinedTextField(
            value = dataSize,
            onValueChange = { dataSize = it },
            label = { Text("Ukuran", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().focusRequester(sizeFocus),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { lifespanFocus.requestFocus() })
        )
        OutlinedTextField(
            value = dataLifespan,
            onValueChange = { dataLifespan = it },
            label = { Text("Masa Hidup", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().focusRequester(lifespanFocus),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { difficultyFocus.requestFocus() })
        )
        OutlinedTextField(
            value = dataDifficulty,
            onValueChange = { dataDifficulty = it },
            label = { Text("Tingkat Kesulitan", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().focusRequester(difficultyFocus),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Button(
            onClick = {
                onSave(
                    context,
                    dataName,
                    dataPrice,
                    dataDescription,
                    dataOrigin,
                    dataSize,
                    dataLifespan,
                    dataDifficulty,
                    dataFile
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan Perubahan")
        }
    }
}
