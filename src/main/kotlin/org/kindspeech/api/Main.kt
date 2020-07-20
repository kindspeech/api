package org.kindspeech.api

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val target = System.getenv("TARGET") ?: "World"
    val port = System.getenv("PORT") ?: "8080"
    embeddedServer(Netty, port.toInt()) {
        routing {
            get("/") {
                call.respondText("Hello $target", ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}