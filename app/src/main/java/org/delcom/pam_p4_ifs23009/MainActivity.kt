package org.delcom.pam_p4_ifs23009

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.delcom.pam_p4_ifs23009.ui.UIApp
import org.delcom.pam_p4_ifs23009.ui.theme.DelcomTheme
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishViewModel
import org.delcom.pam_p4_ifs23009.ui.viewmodels.PlantViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val plantViewModel: PlantViewModel by viewModels<PlantViewModel>()
    private val fishViewModel: FishViewModel by viewModels<FishViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelcomTheme {
                UIApp(
                    plantViewModel = plantViewModel,
                    fishViewModel = fishViewModel
                )
            }
        }
    }
}
