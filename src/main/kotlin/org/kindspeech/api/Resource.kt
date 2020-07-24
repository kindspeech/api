package org.kindspeech.api

import java.io.File
import java.net.URL

class Resource(path: String) {

    val url: URL by lazy {
        Resource::class.java.getResource(path)
    }
}
