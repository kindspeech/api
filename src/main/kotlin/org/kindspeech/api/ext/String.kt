package org.kindspeech.api.ext

import java.net.URLEncoder

fun String.urlEncode(): String {
    return URLEncoder.encode(this, "utf-8")
}
