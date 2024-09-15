package util

import getStringMd5_32

object CipherUtil {
  fun md5(string: String, bit: Int = 32): String {
    val encodeStr = getStringMd5_32(string)
    return if (bit == 16) {
      encodeStr.substring(8, 24)
    } else {
      encodeStr
    }
  }

}
