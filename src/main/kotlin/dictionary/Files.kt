package org.example.dictionary

import java.io.File

fun main() {
    val wordsFile = File("words.txt")
//    wordsFile.createNewFile()

//    wordsFile.appendText("hello привет")
//    wordsFile.appendText("dog собака")
//    wordsFile.appendText("cat кошка")

    for (i in wordsFile.readLines()) println(i)
}