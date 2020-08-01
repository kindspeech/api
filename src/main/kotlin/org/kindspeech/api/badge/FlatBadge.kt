package org.kindspeech.api.badge

import org.kindspeech.api.svg.SVG
import org.kindspeech.api.svg.SVGDominantBaselineAttribute.DominantBaseline
import org.kindspeech.api.svg.SVGRect
import org.kindspeech.api.svg.SVGText
import org.kindspeech.api.svg.SVGText.LengthAdjust
import org.kindspeech.api.svg.SVGTextAnchorAttribute.TextAnchor
import org.kindspeech.api.svg.SVGTextRenderingAttribute.TextRendering
import org.kindspeech.api.svg.svg
import java.awt.Font
import java.awt.font.FontRenderContext

class FlatBadge(text: String, color: String) {

    val svg: SVG

    init {
        val textWidthEstimate = text.size11WidthEstimate()

        svg = svg {
            width = HORIZONTAL_SPACING_PX + textWidthEstimate + HORIZONTAL_SPACING_PX
            height = HEIGHT

            title { value = text }

            defs {
                linearGradient {
                    id = "gradient"
                    x2 = 0
                    y2 = "100%"
                    stop {
                        offset = 0
                        `stop-color` = "#bbb"
                        `stop-opacity` = .1f
                    }
                    stop {
                        offset = 1
                        `stop-opacity` = .1f
                    }
                }
            }

            val sharedRectAttributes: SVGRect.() -> Unit = {
                x = 0
                y = 0
                width = HORIZONTAL_SPACING_PX + textWidthEstimate + HORIZONTAL_SPACING_PX
                height = HEIGHT
                rx = 3
            }

            // Main background.
            rect {
                sharedRectAttributes()
                fill = color
            }

            // Gradient overlay.
            rect {
                sharedRectAttributes()
                fill = "url(#gradient)"
            }

            // Text group.
            g {
                `dominant-baseline` = DominantBaseline.middle
                `font-family` = "Verdana,Geneva,DejaVu Sans,sans-serif"
                `font-size` = "11px"
                `text-anchor` = TextAnchor.start
                `text-rendering` = TextRendering.optimizeLegibility

                val sharedTextAttributes: SVGText.() -> Unit = {
                    x = HORIZONTAL_SPACING_PX
                    lengthAdjust = LengthAdjust.spacingAndGlyphs
                    textLength = textWidthEstimate
                    value = text
                }

                // Text shadow.
                text {
                    sharedTextAttributes()
                    y = (HEIGHT / 2) + 1
                    `aria-hidden` = true
                    fill = "#010101"
                    `fill-opacity` = .3f
                }

                // Text.
                text {
                    sharedTextAttributes()
                    y = HEIGHT / 2
                    fill = "#fff"
                }
            }
        }
    }

    companion object {
        private const val HEIGHT = 20
        private const val HORIZONTAL_SPACING_PX = 6
        private val FONT = Font(Font.SANS_SERIF, Font.PLAIN, 11)
        private val FONT_RENDER_CONTEXT = FontRenderContext(null, false, true)

        private fun String.size11WidthEstimate(): Double {
            return FONT.getStringBounds(this, FONT_RENDER_CONTEXT).width
        }
    }
}
