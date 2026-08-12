package com.example.farmastudy.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CLASSIFICATION = "classification"
    const val RANDOM_STUDY = "random_study"
    const val QUIZ_INTRO = "quiz_intro/{quizPart}"
    const val QUIZ_QUESTION = "quiz_question/{quizPart}/{questionIndex}"
    const val QUIZ_RESULT = "quiz_result"
    const val STUDY_BY_CATEGORY = "study_by_category/{category}"

    fun quizIntro(quizPart: Int) = "quiz_intro/$quizPart"
    fun quizQuestion(quizPart: Int, questionIndex: Int) = "quiz_question/$quizPart/$questionIndex"
    fun studyByCategory(category: String) = "study_by_category/$category"
}