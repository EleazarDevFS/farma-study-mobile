package com.example.farmastudy.data.datasource

import android.content.Context
import com.example.farmastudy.data.local.FarmaDatabase
import com.example.farmastudy.data.local.entity.MedicationEntity
import com.example.farmastudy.data.local.entity.QuestionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object SeedData {

    suspend fun seedIfEmpty(context: Context, db: FarmaDatabase) = withContext(Dispatchers.IO) {
        if (db.questionDao().count() > 0) return@withContext

        val medications = parseMedications(readAsset(context, "medications.json"))
        val questions = parseQuestions(readAsset(context, "questions.json"))

        db.medicationDao().insertAll(medications)
        db.questionDao().insertAll(questions)
    }

    private fun readAsset(context: Context, fileName: String): String =
        context.assets.open(fileName).bufferedReader().use { it.readText() }

    private fun parseMedications(json: String): List<MedicationEntity> {
        val arr = JSONObject(json).getJSONArray("medications")
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            MedicationEntity(
                id = i + 1,
                name = o.getString("name"),
                therapeuticUse = o.optString("therapeuticUse").ifEmpty { null },
                mechanism = o.optString("mechanism").ifEmpty { null },
                chemicalStructure = o.optString("chemicalStructure").ifEmpty { null },
                organicSystem = o.optString("organicSystem").ifEmpty { null }
            )
        }
    }

    private fun parseQuestions(json: String): List<QuestionEntity> {
        val arr = JSONObject(json).getJSONArray("questions")
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            QuestionEntity(
                id = o.getInt("id"),
                question = o.getString("question"),
                optionA = o.getString("optionA"),
                optionB = o.getString("optionB"),
                optionC = o.getString("optionC"),
                optionD = o.getString("optionD"),
                correctOption = o.getString("correctOption"),
                quizPart = o.getInt("quizPart")
            )
        }
    }
}