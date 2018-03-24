package zao.kaloglu.com.socialpurge.helpers.responses

/**
 * Created by kaloglu on 24.03.2018.
 */

class CheckResponse(status: Boolean = false, code: String, message: String) {
    val status = status
    val code = code
    val message = message

    fun getClass():Class<*>{
        return this.getClass()
    }
}
