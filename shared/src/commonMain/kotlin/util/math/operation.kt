package util.math

// 取余 %
fun Int.takeover(base: Int): Int? {
  if (base == 0) {
    return null
  } else return this % base
}
