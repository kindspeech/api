package org.kindspeech.api.badge

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.response.respondRedirect
import io.ktor.util.pipeline.PipelineInterceptor
import org.kindspeech.api.Database
import org.kindspeech.api.ext.urlEncode

val svgBadgeLogo by lazy { SVG("/images/badge_logo.svg") }

fun badgeInterceptor(db: Lazy<Database>): PipelineInterceptor<Unit, ApplicationCall> = {
    // The maximum length of the text is limited to avoid generating huge badges. However the shields.io
    // API has no documented limit.
    val text = db.value.randomText(maxLength = 30)

    val shieldsUrl = "http://img.shields.io/static/v1" +
            "?label=" +
            "&message=${text.text.urlEncode()}" +
            "&style=social" +
            "&logoWidth=28" +
            "&logo=${svgBadgeLogo.dataUri}"

    call.respondRedirect(shieldsUrl, permanent = false)
}
