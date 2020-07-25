package org.kindspeech.api.badge

import java.net.URL

class Resource(path: String) {

    val url: URL by lazy {
        Resource::class.java.getResource(path)
    }
}
