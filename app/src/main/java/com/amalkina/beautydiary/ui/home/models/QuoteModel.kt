package com.amalkina.beautydiary.ui.home.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.amalkina.beautydiary.ui.common.models.BaseModel

const val MAX_COLLAPSE_QUOTE_SIZE = 60

internal open class QuoteModel(
    val quote: String = "",
    var author: String = ""
) : BaseModel() {

    init {
        if (author.isNotEmpty())
            author = "— $author"
    }

    private val isCardCollapse = MutableLiveData(true)
    val isAuthorShown = isCardCollapse.map { !it && author.isNotEmpty() }
    val quoteText = isCardCollapse.map {
        if (it && quote.length > MAX_COLLAPSE_QUOTE_SIZE)
            "${quote.substring(0, MAX_COLLAPSE_QUOTE_SIZE).trim()}..."
        else quote
    }

    fun changeCardCollapseMode() {
        isCardCollapse.value?.let {
            isCardCollapse.postValue(!it)
        }
    }
}