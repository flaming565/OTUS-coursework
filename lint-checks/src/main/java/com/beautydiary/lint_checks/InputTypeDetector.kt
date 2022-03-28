package com.beautydiary.lint_checks

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element

@Suppress("UnstableApiUsage")
class InputTypeDetector : LayoutDetector() {

    companion object {
        internal val ISSUE = Issue.create(
            id = "MissingInputType",
            briefDescription = "Specify inputType attribute to get proper keyboard shown by system.",
            explanation = "You should specify an inputType for each EditText so that you can get the proper keyboard to be shown by system.",
            category = Category.USABILITY,
            priority = 1,
            severity = Severity.WARNING,
            implementation = Implementation(
                InputTypeDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        ).addMoreInfo("https://developer.android.com/training/keyboard-input/style")
    }

    override fun getApplicableElements(): Collection<String> {
        return listOf(
            SdkConstants.EDIT_TEXT,
            "androidx.appcompat.widget.AppCompatEditText",
            "android.support.v7.widget.AppCompatEditText",
            "com.google.android.material.textfield.TextInputEditText"
        )
    }

    override fun visitElement(context: XmlContext, element: Element) {
        if (!element.hasAttribute(SdkConstants.ATTR_INPUT_TYPE)) {
            context.report(
                issue = ISSUE,
                location = context.getLocation(element),
                message = ISSUE.getExplanation(TextFormat.TEXT)
            )
        }
    }
}