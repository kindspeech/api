package org.kindspeech.api

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.json.JSONArray

fun main() {
    val port = System.getenv("PORT") ?: "8080"
    embeddedServer(Netty, port.toInt()) {
        routing {
            get("/messages") {
                val limit = call.request.queryParameters["limit"]?.toInt() ?: 1
                val response = JSONArray().apply {
                    (0 until limit).forEach {
                        put("You are wonderful.")
                    }
                }
                call.respondText(ContentType.Application.Json) {
                    response.toString()
                }
            }
        }
    }.start(wait = true)
}