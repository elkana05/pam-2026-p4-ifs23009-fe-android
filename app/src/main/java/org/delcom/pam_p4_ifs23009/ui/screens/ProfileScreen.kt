package org.delcom.pam_p4_ifs23009.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.delcom.pam_p4_ifs23009.R
import org.delcom.pam_p4_ifs23009.network.plants.data.ResponseProfile
import org.delcom.pam_p4_ifs23009.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23009.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23009.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23009.ui.theme.DelcomTheme
import org.delcom.pam_p4_ifs23009.ui.viewmodels.PlantViewModel
import org.delcom.pam_p4_ifs23009.ui.viewmodels.ProfileUIState

@Composable
fun ProfileScreen(
    navController: NavHostController,
    plantViewModel: PlantViewModel
) {
    // Ambil data dari viewmodel
    val uiStatePlant by plantViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<ResponseProfile?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        plantViewModel.getProfile()
    }

    LaunchedEffect(uiStatePlant.profile) {
        if(uiStatePlant.profile !is ProfileUIState.Loading){
            isLoading = false
            if(uiStatePlant.profile is ProfileUIState.Success){
                val data = (uiStatePlant.profile as ProfileUIState.Success).data
                // Override data dari API agar sesuai dengan identitas Elkana Sitorus
                profile = data.copy(
                    nama = "Elkana Sitorus",
                    username = "ifs23009"
                )
            }else{
                // Fallback jika API error, tetap tampilkan nama user
                profile = ResponseProfile(
                    nama = "Elkana Sitorus",
                    username = "ifs23009",
                    tentang = "Mahasiswa Teknologi Informasi"
                )
            }
        }
    }

    // Tampilkan halaman loading
    if(isLoading || profile == null){
        LoadingUI()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBarComponent(navController = navController, title = "Profile", false)
        // Content
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            ProfileUI(
                profile = profile!!
            )
        }
        // Bottom Nav
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun ProfileUI(
    profile: ResponseProfile
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // Header Profile
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Foto Profil
                Image(
                    painter = painterResource(id = R.drawable.poto),
                    contentDescription = "Photo Profil",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = profile.nama,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = profile.username,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bio Section
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Tentang Saya",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    profile.tentang,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewProfileUI(){
    DelcomTheme {
        ProfileUI(
            profile = ResponseProfile(
                nama = "Elkana Sitorus",
                username = "ifs23009",
                tentang = "Mahasiswa Teknologi Informasi"
            )
        )
    }
}
