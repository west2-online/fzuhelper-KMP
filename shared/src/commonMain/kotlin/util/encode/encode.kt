package util.encode

fun encode(multiplication: Int, multiplication2: Int, time: Long): String {
  return time
    .toString()
    .map { it.toString().toInt() * multiplication * multiplication2 }
    .joinToString(separator = "_")
}
