package zao.kaloglu.com.socialpurge.models

/**
 * Created by kaloglu on 12/11/2017.
 */
data class TwitterConsumer(
        val ownerId: Long = 0,
        val owner: String = "",
        val api_key: String? = "",
        val api_secret: String? = ""
)

