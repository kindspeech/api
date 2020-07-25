package org.kindspeech.api.badge

import java.util.Base64

class SVG(resourcePath: String) {

    val string by lazy { Resource(resourcePath).url.readText() }

    /**
     * [string] with whitespace at the beginning of the original lines and new lines removed.
     */
    val compactString by lazy { string.lines().joinToString(separator = "") { it.trim() } }

    val dataUri by lazy {
        DATA_URI_PREFIX + Base64.getEncoder().encodeToString(compactString.toByteArray())
    }

    companion object {
        private const val DATA_URI_PREFIX = "data:image/svg+xml;base64,"
    }
}
