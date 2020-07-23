package org.kindspeech.api

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.json.JSONObject
import org.kindspeech.api.ext.respondJsonError
import org.kindspeech.api.ext.respondJson
import org.slf4j.event.Level

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    val db by lazy { Database() }

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
                get("text") {
                    val text = db.randomText()
                    val response = JSONObject().apply {
                        put("text", text.text)
                        if (text.attribution != null) {
                            put("attribution", text.attribution)
                        }
                    }
                    call.respondJson(response)
                }
            }
        }
    }.start(wait = true)
}
