package ui.compose.Authentication

import data.LoginRepository
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class AuthenticationViewModel(val loginRepository:LoginRepository) : ViewModel() {
    private val _counter: CMutableStateFlow<Int> = CMutableStateFlow(MutableStateFlow(1))
    fun onCounterButtonPressed() {
        viewModelScope.launch {

        }
        val current = _counter.value
        _counter.value = current + 1
    }

}