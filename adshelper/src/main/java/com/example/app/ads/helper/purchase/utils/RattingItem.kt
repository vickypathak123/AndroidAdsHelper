package com.example.app.ads.helper.purchase.utils

import android.view.Gravity
import androidx.annotation.IntDef
import androidx.annotation.StringRes


data class RattingItem(
    @StringRes
    var title: Int,
    @StringRes
    var subTitle: Int,
    @StringRes
    var givenBy: Int,

    var rattingStar: Float = 5.0f,

    @TextGravityFlags
    var givenByTextGravity: Int = Gravity.END
)


@IntDef(
    flag = true, value = [
        Gravity.START,
        Gravity.END,
        Gravity.CENTER,
    ]
)
annotation class TextGravityFlags


private val words = listOf(
    "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
    "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
    "magna", "aliqua", "ut", "enim", "ad", "minim", "veniam", "quis", "nostrud",
    "exercitation", "ullamco", "laboris", "nisi", "ut", "aliquip", "ex", "ea",
    "commodo", "consequat", "duis", "aute", "irure", "dolor", "in", "reprehenderit",
    "in", "voluptate", "velit", "esse", "cillum", "dolore", "eu", "fugiat", "nulla",
    "pariatur", "excepteur", "sint", "occaecat", "cupidatat", "non", "proident",
    "sunt", "in", "culpa", "qui", "officia", "deserunt", "mollit", "anim", "id", "est",
    "laborum", "The", "app", "is", "good", "really", "need", "to", "work", "on", "sequence",
    "of", "scanned", "image", "some", "contents", "are", "missing", "and", "shuffled",
    "needs", "improvement"
)

private fun getRandomWord(): String = words.random()

fun generateRandomParagraph(wordCount: Int): String {
    return (1..wordCount)
        .joinToString(" ") { getRandomWord() }
}

//    private fun getRandomString(length: Int): String {
//        val allowedChars = " ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789 "
//        return (1..length)
//            .map { allowedChars.random() }
//            .joinToString("")
//    }