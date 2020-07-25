package org.kindspeech.api.text

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.util.pipeline.PipelineInterceptor
import org.json.JSONObject
import org.kindspeech.api.Database
import org.kindspeech.api.ext.respondJson

fun textInterceptor(db: Lazy<Database>): PipelineInterceptor<Unit, ApplicationCall> = {
    val text = db.value.randomText()
    val response = JSONObject().apply {
        put("text", text.text)
        if (text.attribution != null) {
            put("attribution", text.attribution)
        }
    }
    call.respondJson(response)
}
