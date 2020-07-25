package org.kindspeech.api

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.json.JSONObject
import org.kindspeech.api.badge.badgeInterceptor
import org.kindspeech.api.ext.respondJson
import org.kindspeech.api.ext.respondJsonError
import org.kindspeech.api.ext.urlEncode
import org.kindspeech.api.text.textInterceptor
import org.slf4j.event.Level

fun main() {
    // Fixes "SSL peer shut down incorrectly" error.
    // https://stackoverflow.com/questions/28908835/ssl-peer-shut-down-incorrectly-in-java
    System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2")

    val port = System.getenv("PORT")?.toInt() ?: 8080

    val db = lazy { Database() }

    embeddedServer(Netty, port) {
        install(CallLogging) {
            level = Level.INFO
        }

        install(StatusPages) {
            exception<Throwable> { cause ->
                call.respondJsonError(HttpStatusCode.InternalServerError)
                throw cause // Throwing here logs the error.
            }

            status(HttpStatusCode.NotFound) { statusCode ->
                call.respondJsonError(statusCode)
            }
        }

        install(CORS) {
            allowNonSimpleContentTypes = true
            anyHost()
        }

        routing {
            route("v1") {
                get("text", textInterceptor(db))
                get("badge", badgeInterceptor(db))
            }
        }

    }.start(wait = true)
}
