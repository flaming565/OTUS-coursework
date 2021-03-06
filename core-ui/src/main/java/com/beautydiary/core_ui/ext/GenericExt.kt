package com.beautydiary.core_ui.ext

inline fun <reified T> tryCast(instance: Any?, block: T.() -> Unit) {
    if (instance is T) {
        block(instance)
    }
}

inline fun <reified T> cast(instance: Any?): T? {
    return if (instance is T) instance
    else null
}