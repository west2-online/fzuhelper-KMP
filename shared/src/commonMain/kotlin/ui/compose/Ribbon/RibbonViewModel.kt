package ui.compose.Ribbon

import com.liftric.kvault.KVault
import data.ribbon.RibbonData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.RibbonRepository
import ui.util.flow.catchWithMassage
import ui.util.flow.collectWithMassage
import ui.util.flow.launchInDefault
import ui.util.network.NetworkResult
import ui.util.network.logicIfNotLoading
import ui.util.network.reset

class RibbonViewModel(
    private val kVault: KVault,
    private val ribbonRepository: RibbonRepository
):ViewModel() {
    private val _ribbonList = CMutableStateFlow(MutableStateFlow<NetworkResult<List<RibbonData>>>(NetworkResult.UnSend()))
    val ribbonList = _ribbonList.asStateFlow()

    fun getRibbonList(){
        viewModelScope.launchInDefault {
            _ribbonList.logicIfNotLoading {
                ribbonRepository.getRibbonList()
                    .catchWithMassage {
                        _ribbonList.reset(NetworkResult.Error(Throwable("获取失败")))
                    }.collectWithMassage {
                        _ribbonList.reset(it.toNetworkResult())
                    }
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
                    .catchWithMassage {
                        _ribbonList.reset(NetworkResult.Error(Throwable("获取失败")))
                    }.collectWithMassage {
                        _ribbonList.reset(it.toNetworkResult())
                    }
            }
        }
    }
}