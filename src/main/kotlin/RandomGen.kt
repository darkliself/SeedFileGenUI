import java.lang.Math.random
import kotlin.math.roundToInt


class RandomGen(private val numbers: List<Int>) {
    var index: Int = 0
    fun showRandomNumber(): Int {
        index = (random() * (numbers.count() - 1)).roundToInt()
        println(numbers[index])
        return numbers[index]
    }
}

abstract class Tz() {
    fun show(): String {
        println("123213")
        return ""
    }
}

class Tz2() {

}