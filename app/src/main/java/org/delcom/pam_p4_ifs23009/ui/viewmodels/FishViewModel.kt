package org.delcom.pam_p4_ifs23009.ui.viewmodels

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishData
import org.delcom.pam_p4_ifs23009.network.fish.service.IFishRepository
import javax.inject.Inject

sealed interface FishesUIState {
    data class Success(val data: List<ResponseFishData>) : FishesUIState
    data class Error(val message: String) : FishesUIState
    object Loading : FishesUIState
}

sealed interface FishUIState {
    data class Success(val data: ResponseFishData) : FishUIState
    data class Error(val message: String) : FishUIState
    object Loading : FishUIState
}

sealed interface FishActionUIState {
    data class Success(val message: String) : FishActionUIState
    data class Error(val message: String) : FishActionUIState
    object Loading : FishActionUIState
    object Idle : FishActionUIState
}

data class UIStateFish(
    val fishes: FishesUIState = FishesUIState.Loading,
    val fish: FishUIState = FishUIState.Loading,
    val fishAction: FishActionUIState = FishActionUIState.Idle
)

@HiltViewModel
@Keep
class FishViewModel @Inject constructor(
    private val repository: IFishRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UIStateFish())
    val uiState = _uiState.asStateFlow()

    fun getAllFishes(search: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(fishes = FishesUIState.Loading) }
            val tmpState = runCatching {
                repository.getAllFishes(search)
            }.fold(
                onSuccess = {
                    if (it.status == "success") {
                        FishesUIState.Success(it.data?.fishes ?: emptyList())
                    } else {
                        FishesUIState.Error(it.message)
                    }
                },
                onFailure = {
                    FishesUIState.Error(it.message ?: "Unknown error")
                }
            )
            _uiState.update { it.copy(fishes = tmpState) }
        }
    }

    fun postFish(
        nama: RequestBody,
        deskripsi: RequestBody,
        harga: RequestBody,
        asal: RequestBody,
        ukuran: RequestBody,
        masaHidup: RequestBody,
        tingkatKesulitan: RequestBody,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(fishAction = FishActionUIState.Loading) }
            val tmpState = runCatching {
                repository.postFish(
                    nama = nama,
                    deskripsi = deskripsi,
                    harga = harga,
                    asal = asal,
                    ukuran = ukuran,
                    masaHidup = masaHidup,
                    tingkatKesulitan = tingkatKesulitan,
                    file = file
                )
            }.fold(
                onSuccess = {
                    if (it.status == "success") {
                        FishActionUIState.Success(it.data?.fishId ?: "")
                    } else {
                        FishActionUIState.Error(it.message)
                    }
                },
                onFailure = {
                    FishActionUIState.Error(it.message ?: "Unknown error")
                }
            )
            _uiState.update { it.copy(fishAction = tmpState) }
        }
    }

    fun getFishById(fishId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(fish = FishUIState.Loading) }
            val tmpState = runCatching {
                repository.getFishById(fishId)
            }.fold(
                onSuccess = {
                    if (it.status == "success") {
                        val fishData = it.data?.fish
                        if (fishData != null) {
                            FishUIState.Success(fishData)
                        } else {
                            FishUIState.Error("Fish data not found")
                        }
                    } else {
                        FishUIState.Error(it.message)
                    }
                },
                onFailure = {
                    FishUIState.Error(it.message ?: "Unknown error")
                }
            )
            _uiState.update { it.copy(fish = tmpState) }
        }
    }

    fun putFish(
        fishId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        harga: RequestBody,
        asal: RequestBody,
        ukuran: RequestBody,
        masaHidup: RequestBody,
        tingkatKesulitan: RequestBody,
        file: MultipartBody.Part? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(fishAction = FishActionUIState.Loading) }
            val tmpState = runCatching {
                repository.putFish(
                    fishId = fishId,
                    nama = nama,
                    deskripsi = deskripsi,
                    harga = harga,
                    asal = asal,
                    ukuran = ukuran,
                    masaHidup = masaHidup,
                    tingkatKesulitan = tingkatKesulitan,
                    file = file
                )
            }.fold(
                onSuccess = {
                    if (it.status == "success") {
                        FishActionUIState.Success(it.message)
                    } else {
                        FishActionUIState.Error(it.message)
                    }
                },
                onFailure = {
                    FishActionUIState.Error(it.message ?: "Unknown error")
                }
            )
            _uiState.update { it.copy(fishAction = tmpState) }
        }
    }

    fun deleteFish(fishId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(fishAction = FishActionUIState.Loading) }
            val tmpState = runCatching {
                repository.deleteFish(fishId)
            }.fold(
                onSuccess = {
                    if (it.status == "success") {
                        FishActionUIState.Success(it.message)
                    } else {
                        FishActionUIState.Error(it.message)
                    }
                },
                onFailure = {
                    FishActionUIState.Error(it.message ?: "Unknown error")
                }
            )
            _uiState.update { it.copy(fishAction = tmpState) }
        }
    }
    
    fun resetFishAction() {
        _uiState.update { it.copy(fishAction = FishActionUIState.Idle) }
    }
}
