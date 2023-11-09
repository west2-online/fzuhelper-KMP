package config

object Debug {
    const val isDebug:Boolean = true
    val debugLevel = DebugLevel.DeBug
}

enum class DebugLevel{
    DeBug
}