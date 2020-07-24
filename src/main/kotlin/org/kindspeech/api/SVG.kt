package org.kindspeech.api

class SVG(resourcePath: String) {

    val string by lazy { Resource(resourcePath).url.readText() }

    /**
     * [string] with whitespace at the beginning of the original lines and new lines removed.
     */
    val compactString by lazy { string.lines().joinToString(separator = "") { it.trim() } }
}
