package org.kindspeech.api

import io.ktor.application.call
import io.ktor.application.install
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
import org.json.JSONArray

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    val db by lazy { Database() }

    embeddedServer(Netty, port) {
        install(StatusPages) {
            exception<Throwable> {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        routing {
            route("v1") {
                get("text") {
                    val limit = call.request.queryParameters["limit"]?.toInt() ?: 1
                    val text = db.randomText(limit)
                    val response = JSONArray().apply {
                        text.forEach { message ->
                            put(message)
                        }
                    }
                    call.respondText(ContentType.Application.Json) {
                        response.toString()
                    }
                }
            }
        }
    }.start(wait = true)
}
