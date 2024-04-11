import kotlinx.datetime.Clock
import util.encode.encode
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test

class Test {
    @Test
    fun encodeTest(){
        val time = Clock.System.now().toEpochMilliseconds()
        val randomNumber1 = Random.nextInt(10..99)
        val randomNumber2 = Random.nextInt(1..9)
        println("${randomNumber1}${randomNumber2}_${encode(randomNumber1,randomNumber2,time)}")
    }
}