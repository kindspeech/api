package org.kindspeech.api.ext

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import org.json.JSONObject

suspend fun ApplicationCall.respondJson(json: JSONObject) {
    respondText(ContentType.Application.Json) {
        json.toString()
    }
}

suspend fun ApplicationCall.respondJsonError(statusCode: HttpStatusCode) {
    val response = JSONObject().apply {
        put("status", statusCode.value)
        put("error", statusCode.description)
    }
    respondJson(response)
}
