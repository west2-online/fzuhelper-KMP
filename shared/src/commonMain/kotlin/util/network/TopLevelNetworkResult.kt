package util.network

abstract class TopLevelNetworkResult {
    abstract fun <T>toNetworkResult():NetworkResult<T>
}