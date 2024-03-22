package annotation

@Target(AnnotationTarget.CLASS)
annotation class NetworkResult(val isError :Boolean = false,val errorLog : String = "")


//标记重要函数
@Target(AnnotationTarget.FUNCTION)
annotation class ImportantFunction()