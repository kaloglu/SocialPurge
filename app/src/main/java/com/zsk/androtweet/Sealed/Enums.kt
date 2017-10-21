package com.zsk.androtweet.Sealed

/**
 * Created by kaloglu on 21/10/2017.
 */
open class Enums {
    enum class FontType {
        NORMAL {
            override fun path() = ""
        },
        FONT_ICON {
            override fun path() = "assets/font/fontello.ttf"
        };

        abstract fun path(): String
    }
}
