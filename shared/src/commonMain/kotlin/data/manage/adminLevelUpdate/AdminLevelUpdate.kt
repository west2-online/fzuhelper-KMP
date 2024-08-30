package data.manage.adminLevelUpdate

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog
import util.network.networkSuccess

@Serializable
data class AdminLevelUpdate(val code: Int, val `data`: String?, val msg: String) {
  fun toNetworkResult(): NetworkResult<String> {
    return when (code) {
      1 -> networkErrorWithLog(code, "修改的权限不得高于或等于你自身的权限等级")
      3 -> networkErrorWithLog(code, "对方权限高于你，不得修改")
      4 -> networkSuccess("修改成功")
      else -> networkErrorWithLog(code, "修改失败")
    }
  }
}
