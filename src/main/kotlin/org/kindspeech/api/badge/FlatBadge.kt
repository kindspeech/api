package org.kindspeech.api.badge

import org.kindspeech.api.svg.SVG
import org.kindspeech.api.svg.SVGDominantBaselineAttribute
import org.kindspeech.api.svg.SVGRect
import org.kindspeech.api.svg.SVGText
import org.kindspeech.api.svg.SVGTextAnchorAttribute
import org.kindspeech.api.svg.SVGTextRenderingAttribute
import org.kindspeech.api.svg.svg
import java.awt.Font
import java.awt.Toolkit

class FlatBadge(private val text: String) {

    val svg: SVG

    init {
        val textWidthEstimate = FONT_METRICS.stringWidth(text)

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
                fill = "#0f59c6"
            }

            // Gradient overlay.
            rect {
                sharedRectAttributes()
                fill = "url(#gradient)"
            }

            // Text group.
            g {
                `dominant-baseline` = SVGDominantBaselineAttribute.DominantBaseline.middle
                `font-family` = "Verdana,Geneva,DejaVu Sans,sans-serif"
                `font-size` = "11px"
                `text-anchor` = SVGTextAnchorAttribute.TextAnchor.start
                `text-rendering` = SVGTextRenderingAttribute.TextRendering.optimizeLegibility

                val sharedTextAttributes: SVGText.() -> Unit = {
                    x = HORIZONTAL_SPACING_PX
                    lengthAdjust = SVGText.LengthAdjust.spacingAndGlyphs
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
        @Suppress("DEPRECATION")
        private val FONT_METRICS = Toolkit.getDefaultToolkit().getFontMetrics(FONT)
    }
}
