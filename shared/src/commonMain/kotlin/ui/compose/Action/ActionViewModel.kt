package ui.compose.Action

import com.liftric.kvault.KVault
import data.ribbon.RibbonData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.RibbonRepository
import util.flow.actionWithLabel
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.resetWithLog

class ActionViewModel(
    private val kVault: KVault,
    private val ribbonRepository: RibbonRepository
):ViewModel() {
    private val _ribbonList = CMutableStateFlow(MutableStateFlow<NetworkResult<List<RibbonData>?>>(
        NetworkResult.UnSend()))
    val ribbonList = _ribbonList.asStateFlow()

    fun getRibbonList(){
        viewModelScope.launchInDefault {
            _ribbonList.logicIfNotLoading {
                ribbonRepository.getRibbonList()
                    .actionWithLabel(
                        "getRibbonList/getRibbonList",
                        catchAction = {label, error ->
                            _ribbonList.resetWithLog(label,NetworkResult.Error(error,Throwable("获取失败")))
                        },
                        collectAction = { label, data ->
                            _ribbonList.resetWithLog(label,data.toNetworkResult())
                        }
                    )

            }
        }
    }

    fun initRibbonList(){
        viewModelScope.launchInDefault {
            if(_ribbonList.value !is NetworkResult.UnSend<*>){
                return@launchInDefault
            }
            _ribbonList.logicIfNotLoading {
                ribbonRepository.getRibbonList()
                    .actionWithLabel(
                        "initRibbonList/getRibbonList",
                        catchAction = {label, error ->
                            _ribbonList.resetWithLog(label,NetworkResult.Error(error,Throwable("获取失败")))
                        },
                        collectAction = { label, data ->
                            _ribbonList.resetWithLog(label,data.toNetworkResult())
                        }
                    )
            }
        }
    }
}