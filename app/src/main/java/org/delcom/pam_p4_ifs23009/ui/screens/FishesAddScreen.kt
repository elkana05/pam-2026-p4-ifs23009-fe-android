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
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishViewModel

@Composable
fun FishesAddScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    fishViewModel: FishViewModel
) {
    val uiStateFish by fishViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var tmpFish by remember { mutableStateOf<ResponseFishData?>(null) }

    LaunchedEffect(Unit) {
        fishViewModel.resetFishAction()
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
        file: Uri
    ) {
        isLoading = true
        
        tmpFish = ResponseFishData(
            id = "",
            nama = name,
            deskripsi = description,
            harga = price,
            asal = origin,
            ukuran = size,
            masaHidup = lifespan,
            tingkatKesulitan = difficulty
        )
        
        val namaBody = name.toRequestBodyText()
        val deskripsiBody = description.toRequestBodyText()
        val hargaBody = price.toRequestBodyText()
        val asalBody = origin.toRequestBodyText()
        val ukuranBody = size.toRequestBodyText()
        val masaHidupBody = lifespan.toRequestBodyText()
        val tingkatKesulitanBody = difficulty.toRequestBodyText()
        
        val filePart = uriToMultipart(context, file, "file")

        fishViewModel.postFish(
            nama = namaBody,
            deskripsi = deskripsiBody,
            harga = hargaBody,
            asal = asalBody,
            ukuran = ukuranBody,
            masaHidup = masaHidupBody,
            tingkatKesulitan = tingkatKesulitanBody,
            file = filePart
        )
    }

    LaunchedEffect(uiStateFish.fishAction) {
        when (val state = uiStateFish.fishAction) {
            is FishActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.SUCCESS, "Berhasil menambah data ikan")
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

    if (isLoading) {
        LoadingUI()
        return
    }

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(navController = navController, title = "Tambah Ikan", showBackButton = true)
        Box(modifier = Modifier.weight(1f)) {
            FishesAddUI(
                tmpFish = tmpFish,
                onSave = ::onSave
            )
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun FishesAddUI(
    tmpFish: ResponseFishData?,
    onSave: (Context, String, String, String, String, String, String, String, Uri) -> Unit
) {
    val alertState = remember { mutableStateOf(AlertState()) }
    var dataFile by remember { mutableStateOf<Uri?>(null) }
    var dataName by remember { mutableStateOf(tmpFish?.nama ?: "") }
    var dataPrice by remember { mutableStateOf(tmpFish?.harga ?: "") }
    var dataDescription by remember { mutableStateOf(tmpFish?.deskripsi ?: "") }
    var dataOrigin by remember { mutableStateOf(tmpFish?.asal ?: "") }
    var dataSize by remember { mutableStateOf(tmpFish?.ukuran ?: "") }
    var dataLifespan by remember { mutableStateOf(tmpFish?.masaHidup ?: "") }
    var dataDifficulty by remember { mutableStateOf(tmpFish?.tingkatKesulitan ?: "") }
    
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
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.size(150.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (dataFile != null) {
                    AsyncImage(model = dataFile, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Text("Pilih Gambar", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tap untuk memilih gambar", style = MaterialTheme.typography.bodySmall)
        }

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            cursorColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        OutlinedTextField(value = dataName, onValueChange = { dataName = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { priceFocus.requestFocus() }))
        OutlinedTextField(value = dataPrice, onValueChange = { dataPrice = it }, label = { Text("Harga") }, modifier = Modifier.fillMaxWidth().focusRequester(priceFocus), colors = textFieldColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { descFocus.requestFocus() }))
        OutlinedTextField(value = dataDescription, onValueChange = { dataDescription = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth().height(120.dp).focusRequester(descFocus), colors = textFieldColors, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { originFocus.requestFocus() }), maxLines = 5, minLines = 3)
        OutlinedTextField(value = dataOrigin, onValueChange = { dataOrigin = it }, label = { Text("Asal") }, modifier = Modifier.fillMaxWidth().focusRequester(originFocus), colors = textFieldColors, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { sizeFocus.requestFocus() }))
        OutlinedTextField(value = dataSize, onValueChange = { dataSize = it }, label = { Text("Ukuran") }, modifier = Modifier.fillMaxWidth().focusRequester(sizeFocus), colors = textFieldColors, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { lifespanFocus.requestFocus() }))
        OutlinedTextField(value = dataLifespan, onValueChange = { dataLifespan = it }, label = { Text("Masa Hidup") }, modifier = Modifier.fillMaxWidth().focusRequester(lifespanFocus), colors = textFieldColors, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { difficultyFocus.requestFocus() }))
        OutlinedTextField(value = dataDifficulty, onValueChange = { dataDifficulty = it }, label = { Text("Tingkat Kesulitan") }, modifier = Modifier.fillMaxWidth().focusRequester(difficultyFocus), colors = textFieldColors, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
        
        Spacer(modifier = Modifier.height(64.dp))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                if (dataFile == null) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Pilih gambar terlebih dahulu!")
                } else if (dataName.isEmpty() || dataPrice.isEmpty() || dataDescription.isEmpty() || dataOrigin.isEmpty() || dataSize.isEmpty() || dataLifespan.isEmpty() || dataDifficulty.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Semua field wajib diisi!")
                } else {
                    onSave(context, dataName, dataPrice, dataDescription, dataOrigin, dataSize, dataLifespan, dataDifficulty, dataFile!!)
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Save, contentDescription = "Simpan")
        }
    }

    if (alertState.value.isVisible) {
        AlertDialog(onDismissRequest = { AlertHelper.dismiss(alertState) }, title = { Text(alertState.value.type.title) }, text = { Text(alertState.value.message) },
            confirmButton = { TextButton(onClick = { AlertHelper.dismiss(alertState) }) { Text("OK") } })
    }
}
