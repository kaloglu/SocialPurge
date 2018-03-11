package zao.kaloglu.com.socialpurge.components

import android.content.Context
import android.graphics.Typeface
import java.util.*

/**
 * Created by kaloglu on 21/10/2017.
 */


internal class CustomTypeFace {
    companion object {
        private val cache = Hashtable<String, Typeface>()

        fun getTypeFace(context: Context, fontType: zao.kaloglu.com.socialpurge.Sealed.Enums.FontType): Typeface? {
            val assetPath: String=fontType.path()

            synchronized(zao.kaloglu.com.socialpurge.components.CustomTypeFace.Companion.cache) {
                if (!zao.kaloglu.com.socialpurge.components.CustomTypeFace.Companion.cache.containsKey(assetPath)) {
                    try {
                        val typeFace = Typeface.createFromAsset(context.assets, assetPath)
                        zao.kaloglu.com.socialpurge.components.CustomTypeFace.Companion.cache.put(assetPath, typeFace)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return null
                    }

                }
                return zao.kaloglu.com.socialpurge.components.CustomTypeFace.Companion.cache[assetPath]
            }
        }
    }
}
