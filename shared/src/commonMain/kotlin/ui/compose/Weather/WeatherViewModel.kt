package ui.compose.Weather

import data.weather.WeatherData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.WeatherRepository
import util.flow.catchWithMassage
import util.flow.collectWithMassage
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.reset

class WeatherViewModel(
    val repository: WeatherRepository
):ViewModel(){
    private val _weatherDataOfFuZhou = CMutableStateFlow(MutableStateFlow<NetworkResult<WeatherData>>(
        NetworkResult.UnSend()))
    val weatherDataOfFuZhou = _weatherDataOfFuZhou.asStateFlow()

    fun getFuZhouWeather(){
        viewModelScope.launch {
            _weatherDataOfFuZhou.logicIfNotLoading {
                repository.getWeatherOfFuZhou()
                    .catchWithMassage {
                        _weatherDataOfFuZhou.reset(NetworkResult.Error(Throwable("获取失败")))
                    }.collectWithMassage {
                        _weatherDataOfFuZhou.reset(NetworkResult.Success(it))
                    }
            }
        }
    }
}