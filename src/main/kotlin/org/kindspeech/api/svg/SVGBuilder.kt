package org.kindspeech.api.svg

import org.kindspeech.api.xml.XML

fun svg(init: RootSVG.() -> Unit): SVG {
    return RootSVG().apply(init)
}

class RootSVG : SVG(), SVGTitleContainer {

    init {
        attributes.putAll(
            mapOf(
                "xmlns" to "http://www.w3.org/2000/svg",
                "role" to "img"
            )
        )
    }

    override fun toString(): String = xml.toString()
}

open class SVG : SVGElement("svg"), SVGContainer, SVGDefsContainer, SVGGContainer, SVGImageContainer,
    SVGRectContainer, SVGTextContainer, SVGSizeableAttributes

class SVGDefs : SVGElement("defs"), SVGLinearGradientContainer

/**
 * Does not support the "dominant-baseline" attribute on Safari.
 */
class SVGG : SVGElement("g"), SVGTextContainer, SVGFillableAttributes, SVGFontFamilyAttribute,
    SVGFontSizeAttribute, SVGTextAnchorAttribute, SVGTextRenderingAttribute

class SVGImage : SVGElement("image"), SVGPositionableAttributes {
    var href: String by attributes
}

class SVGLinearGradient : SVGElement("linearGradient"), SVGStopContainer, SVGIdAttribute {
    var x1 by attributes
    var x2 by attributes
    var y1 by attributes
    var y2 by attributes
}

class SVGRect : SVGElement("rect"), SVGPositionableAttributes, SVGFillableAttributes {
    var stroke: String by attributes
    var rx: Number by attributes
    var ry: Number by attributes
}

class SVGStop : SVGElement("stop") {
    var offset by attributes
    var `stop-color` by attributes
    var `stop-opacity`: Number by attributes
}

class SVGText : SVGElement("text"), SVGPositionableAttributes, SVGFillableAttributes,
    SVGDominantBaselineAttribute, SVGFontFamilyAttribute, SVGFontSizeAttribute, SVGTextAnchorAttribute,
    SVGTextRenderingAttribute, SVGValueAttribute {

    enum class LengthAdjust {
        spacing, spacingAndGlyphs
    }

    var `aria-hidden`: Boolean by attributes
    var `font-weight`: Int by attributes
    var lengthAdjust: LengthAdjust by attributes
    var textLength: Number by attributes
}

class SVGTitle : SVGElement("title"), SVGValueAttribute

interface XMLWrapper {

    val xml: XML

    val attributes: MutableMap<String, Any> get() = xml.attributes
}

interface SVGContainer : XMLWrapper {
    fun svg(init: SVG.() -> Unit) {
        xml.children.add(SVG().apply(init).xml)
    }
}

interface SVGDefsContainer : XMLWrapper {
    fun defs(init: SVGDefs.() -> Unit) {
        xml.children.add(SVGDefs().apply(init).xml)
    }
}

interface SVGGContainer : XMLWrapper {
    fun g(init: SVGG.() -> Unit) {
        xml.children.add(SVGG().apply(init).xml)
    }
}

interface SVGImageContainer : XMLWrapper {
    fun image(init: SVGImage.() -> Unit) {
        xml.children.add(SVGImage().apply(init).xml)
    }
}

interface SVGLinearGradientContainer : XMLWrapper {
    fun linearGradient(init: SVGLinearGradient.() -> Unit) {
        xml.children.add(SVGLinearGradient().apply(init).xml)
    }
}

interface SVGRectContainer : XMLWrapper {
    fun rect(init: SVGRect.() -> Unit) {
        xml.children.add(SVGRect().apply(init).xml)
    }
}

interface SVGStopContainer : XMLWrapper {
    fun stop(init: SVGStop.() -> Unit) {
        xml.children.add(SVGStop().apply(init).xml)
    }
}

interface SVGTextContainer : XMLWrapper {
    fun text(init: SVGText.() -> Unit) {
        xml.children.add(SVGText().apply(init).xml)
    }
}

interface SVGTitleContainer : XMLWrapper {
    fun title(init: SVGTitle.() -> Unit) {
        xml.children.add(SVGTitle().apply(init).xml)
    }
}

abstract class SVGElement(name: String) : XMLWrapper {
    override val xml: XML = XML(name)
}

interface SVGFillableAttributes : SVGFillAttribute, SVGFillOpacityAttribute

interface SVGPositionableAttributes : SVGSizeableAttributes, SVGXAttribute, SVGYAttribute

interface SVGSizeableAttributes : SVGWidthAttribute, SVGHeightAttribute

interface SVGIdAttribute : XMLWrapper {
    var id: String
        get() = attributes["id"] as String
        set(value) {
            attributes["id"] = value
        }
}

interface SVGFillAttribute : XMLWrapper {
    var fill: String
        get() = attributes["fill"] as String
        set(value) {
            attributes["fill"] = value
        }
}

interface SVGFillOpacityAttribute : XMLWrapper {
    var `fill-opacity`: Number
        get() = attributes["fill-opacity"] as Number
        set(value) {
            attributes["fill-opacity"] = value
        }
}

interface SVGXAttribute : XMLWrapper {
    var x: Number
        get() = attributes["x"] as Number
        set(value) {
            attributes["x"] = value
        }
}

interface SVGYAttribute : XMLWrapper {
    var y: Number
        get() = attributes["y"] as Number
        set(value) {
            attributes["y"] = value
        }
}

interface SVGWidthAttribute : XMLWrapper {
    var width: Number
        get() = attributes["width"] as Number
        set(value) {
            attributes["width"] = value
        }
}

interface SVGHeightAttribute : XMLWrapper {
    var height: Number
        get() = attributes["height"] as Number
        set(value) {
            attributes["height"] = value
        }
}

interface SVGDominantBaselineAttribute : XMLWrapper {

    enum class DominantBaseline {
        auto, `text-bottom`, alphabetic, ideographic, middle, central, mathematical, hanging, `text-top`
    }

    var `dominant-baseline`: DominantBaseline
        get() = attributes["dominant-baseline"] as DominantBaseline
        set(value) {
            attributes["dominant-baseline"] = value
        }
}

interface SVGFontFamilyAttribute : XMLWrapper {
    var `font-family`: String
        get() = attributes["font-family"] as String
        set(value) {
            attributes["font-family"] = value
        }
}

interface SVGFontSizeAttribute : XMLWrapper {
    var `font-size`: String
        get() = attributes["font-size"] as String
        set(value) {
            attributes["font-size"] = value
        }
}

interface SVGTextAnchorAttribute : XMLWrapper {

    enum class TextAnchor {
        start, middle, end
    }

    var `text-anchor`: TextAnchor
        get() = attributes["text-anchor"] as TextAnchor
        set(value) {
            attributes["text-anchor"] = value
        }
}

interface SVGTextRenderingAttribute : XMLWrapper {

    enum class TextRendering {
        auto, optimizeSpeed, optimizeLegibility, geometricPrecision
    }

    var `text-rendering`: TextRendering
        get() = attributes["text-rendering"] as TextRendering
        set(value) {
            attributes["text-rendering"] = value
        }
}

interface SVGValueAttribute : XMLWrapper {
    var value: Any
        get() = xml.value as Any
        set(value) {
            xml.value = value
        }
}
