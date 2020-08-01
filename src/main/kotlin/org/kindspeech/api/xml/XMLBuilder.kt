package org.kindspeech.api.xml

import org.apache.commons.text.StringEscapeUtils
import org.kindspeech.api.xml.XML.TraversalAction.ENTER
import org.kindspeech.api.xml.XML.TraversalAction.EXIT
import java.util.Stack

fun xml(name: String, vararg attributes: Pair<String, Any>, init: XML.() -> Unit): XML {
    return XML(name, attributes.toMap()).apply(init)
}

class XML(val name: String, attributes: Map<String, Any> = emptyMap()) {

    private enum class TraversalAction {
        ENTER, EXIT
    }

    val attributes = attributes.toMutableMap()

    val children = mutableListOf<XML>()

    var value: Any? = null

    operator fun String.invoke(vararg attributes: Pair<String, Any>, init: (XML.() -> Unit)? = null) {
        require(value == null) {
            "You cannot add children to an XML element which has a value"
        }

        val child = XML(this, attributes.toMap())
        init?.invoke(child)
        children.add(child)
    }

    fun isEmpty() = value == null && children.isEmpty()

    override fun toString(): String = toString(2)

    fun toString(indentationStep: Int): String {
        val stringBuilder = StringBuilder()
        val stack = Stack<Pair<TraversalAction, XML>>()

        stack.push(ENTER to this)

        var indentation = 0

        while (stack.isNotEmpty()) {
            val (action, element) = stack.pop()
            when (action) {
                ENTER -> {
                    stack.add(EXIT to element)

                    stringBuilder.enterElement(element, indentation)

                    if (element.value != null) {
                        stringBuilder.indent(indentation + indentationStep)
                            .append(StringEscapeUtils.escapeXml11(element.value.toString()))
                            .append('\n')
                    } else if (element.children.isNotEmpty()) {
                        stack.addAll(element.children.asReversed().map { ENTER to it })
                    }

                    indentation += indentationStep
                }
                EXIT -> {
                    indentation -= indentationStep

                    stringBuilder.exitElement(element, indentation)
                }
            }
        }

        return stringBuilder.toString()
    }

    private fun StringBuilder.indent(indentation: Int): StringBuilder = apply {
        append(" ".repeat(indentation))
    }

    private fun StringBuilder.enterElement(element: XML, indentation: Int): StringBuilder = apply {
        append(" ".repeat(indentation))
        append("<${element.name}")
        element.attributes.forEach { attribute ->
            append(" ${attribute.key}=\"${attribute.value}\"")
        }
        if (element.isEmpty()) append(" /")
        append(">\n")
    }

    private fun StringBuilder.exitElement(element: XML, indentation: Int): StringBuilder = apply {
        if (!element.isEmpty()) {
            append(" ".repeat(indentation))
            append("</${element.name}>\n")
        }
    }
}
