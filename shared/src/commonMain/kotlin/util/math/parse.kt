package util.math

/**
 * 解析int
 * @param string String
 * @return Int
 */
fun parseInt(string: String): Int {
    val data = string.toIntOrNull()
    data?:run{
        throw Throwable("解析失败")
    }
    return data
}

fun parseIntWithNull(string: String): Int? {
    val data = string.toIntOrNull()
    data?:run{
        return null
    }
    return data
}