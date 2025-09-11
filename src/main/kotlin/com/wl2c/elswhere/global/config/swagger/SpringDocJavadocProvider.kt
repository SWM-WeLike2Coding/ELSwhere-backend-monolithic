package com.wl2c.elswhere.global.config.swagger

import com.github.therapi.runtimejavadoc.*
import org.apache.commons.lang3.StringUtils
import org.springdoc.core.providers.JavadocProvider
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import java.lang.reflect.Method

@Component
class SpringDocJavadocProvider : JavadocProvider {

    private val formatter = CommentFormatter()

    override fun getClassJavadoc(cl: Class<*>): String {
        val classJavadoc = RuntimeJavadoc.getJavadoc(cl)
        return formatter.format(classJavadoc.comment)
    }

    override fun getRecordClassParamJavadoc(cl: Class<*>): Map<String, String>? {
        return null
    }

    override fun getMethodJavadocDescription(method: Method): String {
        val methodJavadoc = RuntimeJavadoc.getJavadoc(method)
        return formatter.format(methodJavadoc.comment)
    }

    override fun getMethodJavadocReturn(method: Method): String {
        val methodJavadoc = RuntimeJavadoc.getJavadoc(method)
        return formatter.format(methodJavadoc.returns)
    }

    override fun getMethodJavadocThrows(method: Method): Map<String, String> {
        return RuntimeJavadoc.getJavadoc(method)
            .throws
            .associate { it.name to formatter.format(it.comment) }
    }

    override fun getParamJavadoc(method: Method, name: String): String? {
        val methodJavadoc = RuntimeJavadoc.getJavadoc(method)
        return methodJavadoc.params
            .firstOrNull { it.name == name }
            ?.let { formatter.format(it.comment) }
    }

    override fun getFieldJavadoc(field: Field): String {
        val fieldJavadoc = RuntimeJavadoc.getJavadoc(field)
        return formatter.format(fieldJavadoc.comment)
    }

    override fun getFirstSentence(text: String): String {
        if (StringUtils.isEmpty(text)) return text

        val pOpenIndex = text.indexOf("<p>")
        val pCloseIndex = text.indexOf("</p>")
        val newLineIndex = text.indexOf("\n")

        if (pOpenIndex != -1) {
            if (pOpenIndex == 0 && pCloseIndex != -1) {
                if (newLineIndex != -1) {
                    return text.substring(3, minOf(pCloseIndex, newLineIndex))
                }
                return text.substring(3, pCloseIndex)
            }
            if (newLineIndex != -1) {
                return text.substring(0, minOf(pOpenIndex, newLineIndex))
            }
            return text.substring(0, pOpenIndex)
        }

        if (newLineIndex != -1 &&
            text.length != newLineIndex + 1 &&
            Character.isWhitespace(text[newLineIndex + 1])
        ) {
            return text.substring(0, newLineIndex + 1)
        }

        return text
    }
}
