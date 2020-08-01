package org.kindspeech.api

fun requireEnv(name: String): String {
    return checkNotNull(System.getenv(name)) {
        "Environment variable $name must be set"
    }
}
