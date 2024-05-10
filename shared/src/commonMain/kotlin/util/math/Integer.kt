package util.math

object Integer{
    fun parseInt(string: String): Int {
        val data = string.toIntOrNull()
        data?:run{
            throw Throwable("解析失败")
        }
        return data
    }
}