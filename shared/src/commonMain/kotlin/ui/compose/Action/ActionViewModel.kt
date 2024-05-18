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

/**
 * 功能页的相关逻辑
 * @property kVault KVault
 * @property ribbonRepository RibbonRepository
 * @property _ribbonList CMutableStateFlow<NetworkResult<List<RibbonData>?>>
 * @property ribbonList StateFlow<NetworkResult<List<RibbonData>?>> 用于ui使用的轮播图数据
 * @constructor
 */
class ActionViewModel(
    private val kVault: KVault,
    private val ribbonRepository: RibbonRepository
):ViewModel() {
    private val _ribbonList = CMutableStateFlow(MutableStateFlow<NetworkResult<List<RibbonData>?>>(
        NetworkResult.UnSend()))
    val ribbonList = _ribbonList.asStateFlow()

    /**
     * Get ribbon list
     * 刷新轮播图
     */
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

    /**
     * Init ribbon list
     * 初始化轮播图
     */
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