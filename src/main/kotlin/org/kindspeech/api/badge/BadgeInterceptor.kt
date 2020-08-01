package org.kindspeech.api.badge

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.response.cacheControl
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineInterceptor
import org.kindspeech.api.Database

fun badgeInterceptor(db: Lazy<Database>): PipelineInterceptor<Unit, ApplicationCall> = {
    // The maximum length of the text is limited to avoid generating huge badges.
    val text = db.value.randomText(maxLength = 40)
    val badge = FlatBadge(text.text)
    call.response.cacheControl(CacheControl.MaxAge(maxAgeSeconds = 300))
    call.respondText(badge.svg.toString(), ContentType.Image.SVG)
}
