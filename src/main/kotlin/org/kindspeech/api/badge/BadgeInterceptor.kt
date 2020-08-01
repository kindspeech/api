package org.kindspeech.api.badge

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.response.cacheControl
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineInterceptor
import org.kindspeech.api.Database
import org.kindspeech.api.svg.SVGColor

private const val DEFAULT_COLOR = "#0f59c6"

private val HEX_COLOR_REGEX = "[a-fA-F0-9]{3}([a-fA-F0-9]{3})?".toRegex()

fun badgeInterceptor(db: Lazy<Database>): PipelineInterceptor<Unit, ApplicationCall> = {

    val userColor = call.parameters["color"]

    val (color, isUserColorValid) = when {
        userColor == null -> DEFAULT_COLOR to true
        userColor in SVGColor.NAMES -> userColor to true
        HEX_COLOR_REGEX.matches(userColor) -> "#$userColor" to true
        else -> "darkred" to false
    }

    val badge = if (isUserColorValid) {
        // The maximum length of the text is limited to avoid generating huge badges.
        val text = db.value.randomText(maxLength = 40)
        FlatBadge(text.text, color)
    } else {
        FlatBadge("Invalid color", color)
    }

    call.response.cacheControl(CacheControl.MaxAge(maxAgeSeconds = 300))

    call.respondText(badge.svg.toString(), ContentType.Image.SVG)
}
