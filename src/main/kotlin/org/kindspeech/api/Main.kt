package org.kindspeech.api

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.json.JSONObject

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    val db by lazy { Database() }

    suspend fun ApplicationCall.respondJson(json: JSONObject) {
        respondText(ContentType.Application.Json) {
            json.toString()
        }
    }

    suspend fun ApplicationCall.respondError(statusCode: HttpStatusCode) {
        val response = JSONObject().apply {
            put("status", statusCode.value)
            put("error", statusCode.description)
        }
        respondJson(response)
    }

    embeddedServer(Netty, port) {
        install(StatusPages) {
            exception<Throwable> { cause ->
                call.respondError(HttpStatusCode.InternalServerError)
                throw cause // Throwing here logs the error.
            }

            status(HttpStatusCode.NotFound) { statusCode ->
                call.respondError(statusCode)
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
