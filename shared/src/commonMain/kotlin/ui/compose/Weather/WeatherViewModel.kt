package ui.compose.Weather

import data.weather.WeatherData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.WeatherRepository
import util.flow.actionWithLabel
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog

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
                    .actionWithLabel(
                        "getFuZhouWeather/getFuZhouWeather",
                        collectAction = { label, data ->
                            _weatherDataOfFuZhou.resetWithLog(label,NetworkResult.Success(data))
                        },
                        catchAction = { label, error ->
                            _weatherDataOfFuZhou.resetWithLog(label, networkErrorWithLog(error,"获取失败"))
                        }
                    )
            }
        }
    }
}
