package com.ssoaharison.quiz.util

import com.ssoaharison.quiz.model.Result


val EXAMPLE_QUESTIONS = listOf<Result>(
    Result(
        type = "multiple",
        difficulty = "easy",
        category = "Entertainment: Television",
        question = "In the television show Breaking Bad, what is the street name of Walter and Jesse&#039;s notorious product?",
        correctAnswer = "Blue Sky",
        incorrectAnswers = listOf(
        "Baby Blue",
        "Rock Candy",
        "Pure Glass"
        )
    ),
    Result(
        type = "multiple",
        difficulty = "easy",
        category = "Entertainment: Film",
        question = "What was Bruce Campbell&#039;s iconic one-liner after getting a chainsaw hand in Evil Dead 2?",
        correctAnswer = "Groovy.",
        incorrectAnswers = listOf(
        "Gnarly.",
        "Perfect.",
        "Nice."
        )
    ),
    Result(
        type = "multiple",
        difficulty = "easy",
        category = "Entertainment: Video Games",
        question = "In &quot;Call Of Duty: Zombies&quot;, what is the name of the machine that upgrades weapons?",
        correctAnswer = "Pack-A-Punch",
        incorrectAnswers = listOf(
        "Wunderfizz",
        "Gersch Device",
        "Mule Kick"
        )
    ),
    Result(
        type = "multiple",
        difficulty =  "easy",
        category =  "Entertainment: Japanese Anime &amp; Manga",
        question =  "The two main characters of &quot;No Game No Life&quot;, Sora and Shiro, together go by what name?",
        correctAnswer =  "Blank",
        incorrectAnswers =  listOf(
        "Immanity",
        "Disboard",
        "Warbeasts"
        )
    ),
    Result(
        type = "multiple",
        difficulty = "easy",
        category = "Entertainment: Music",
        question = "Which of the following songs was not originally released by Sir Elton John? ",
        correctAnswer = "You&#039;ll Be In My Heart",
        incorrectAnswers = listOf(
        "Crocodile Rock",
        "Candle in the Wind",
        "I Don&#039;t Wanna Go On With You Like That"
        )
    ),
    Result(
        type = "boolean",
        difficulty = "easy",
        category = "Entertainment: Music",
        question = "John Williams composed the music for &quot;Star Wars&quot;.",
        correctAnswer = "True",
        incorrectAnswers = listOf(
        "False"
        )
    ),
    Result(
        type = "multiple",
        difficulty = "easy",
        category = "Entertainment: Video Games",
        question = "In what year was Garry&#039;s Mod released as a standalone title on Valve&#039;s Steam distribution service?",
        correctAnswer = "2006",
        incorrectAnswers = listOf(
        "2007",
        "2004",
        "2003"
        )
    ),
    Result(
        type =  "multiple",
        difficulty =  "easy",
        category =  "Entertainment: Film",
        question =  "In the movie &quot;Blade Runner&quot;, what is the term used for human-like androids ?",
        correctAnswer =  "Replicants",
        incorrectAnswers =  listOf(
        "Cylons",
        "Synthetics",
        "Skinjobs"
        )
    ),
    Result(
        type = "boolean",
        difficulty = "easy",
        category = "General Knowledge",
        question = "One of Donald Trump&#039;s 2016 Presidential Campaign promises was to build a border wall between the United States and Mexico.",
        correctAnswer = "True",
        incorrectAnswers = listOf(
        "False"
        )
    ),
    Result(
        type = "multiple",
        difficulty = "easy",
        category = "Entertainment: Cartoon &amp; Animations",
        question = "Which of these is NOT a catchphrase used by Rick Sanchez in the TV show &quot;Rick and Morty&quot;?",
        correctAnswer = "Slam dunk, nothing but net!",
        incorrectAnswers = listOf(
        "Hit the sack, Jack!",
        "Rikki-Tikki-Tavi, biatch!",
        "Wubba-lubba-dub-dub!"
        )
    )
)