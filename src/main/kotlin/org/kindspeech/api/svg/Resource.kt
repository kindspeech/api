package org.kindspeech.api.svg

import java.net.URL

class Resource(path: String) {

    val url: URL by lazy {
        Resource::class.java.getResource(path)
    }
}
