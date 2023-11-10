package com.ssoaharison.quiz.util

import com.ssoaharison.quiz.model.Result


val EXAMPLE_QUESTIONS = listOf<Result>(
    Result(
        "Entertainment: Film",
        "United Kingdom",
        "easy",
        listOf("France", "Italy", "Germany"),
        "The 2002 film &quot;28 Days Later&quot; is mainly set in which European country?",
        "multiple"
    ),
    Result(
        "Entertainment: Video Games",
        "Mercy",
        "medium",
        listOf("Reaper", "Sonic", "Ana"),
        "Which Overwatch character says the line &quot;Heroes never die!&quot;?",
        "multiple"
    ),
    Result(
        "Entertainment: Television",
        "Grapok sauce",
        "hard",
        listOf("Gazorpazorp pudding", "Sweet chili sauce", "Grapork sauce"),
        "In &quot;Star Trek&quot;, what sauce is commonly used by Klingons on bregit lung?",
        "multiple"
    ),
    Result(
        "Entertainment: Cartoon & Animations",
        "10",
        "medium",
        listOf("12", "11", "13"),
        "How many episodes were in season five of Samurai Jack?",
        "multiple"
    ),
    Result(
        "General Knowledge",
        "Złoty",
        "medium",
        listOf("Ruble", "Euro", "Krone"),
        "What is the currency of Poland?",
        "multiple"
    ),
)