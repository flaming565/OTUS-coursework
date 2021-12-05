package com.amalkina.beautydiary.ui.home.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.amalkina.beautydiary.ui.common.models.BaseModel

const val MAX_COLLAPSE_QUOTE_SIZE = 100

internal open class QuoteModel(
    val quote: String = "",
    var author: String = ""
) : BaseModel() {

    val isAuthorShown = author.isNotEmpty()

    init {
        if (isAuthorShown) author = "— $author"
    }

    private val isCardCollapse = MutableLiveData(true)
    val quoteText = isCardCollapse.map {
        if (it && quote.length > MAX_COLLAPSE_QUOTE_SIZE)
            "${quote.substring(0, MAX_COLLAPSE_QUOTE_SIZE).trim()}..."
        else quote
    }
    val maxLines = isCardCollapse.map { if (it) 2 else 10 }

    fun changeCardCollapseMode() {
        isCardCollapse.value?.let {
            isCardCollapse.postValue(!it)
        }
    }
}