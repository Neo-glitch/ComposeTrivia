package com.neo.composetrivia.repository

import android.util.Log
import com.neo.composetrivia.data.DataOrException
import com.neo.composetrivia.model.QuestionItem
import com.neo.composetrivia.network.QuestionApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val api: QuestionApi) {

    private val dataOrException =
        DataOrException<ArrayList<QuestionItem>, Boolean, Exception>()

    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>, Boolean, java.lang.Exception>{
        try{
            dataOrException.loading = true                // data is loading
            dataOrException.data = api.getAllQuestions()  // gets the result from backend

            if(dataOrException.data.toString().isNotEmpty()) dataOrException.loading = false // stop loading
        }catch (e: Exception){
            dataOrException.error = e
            Log.d("Exc", "getAllQuestions: ${dataOrException.error!!.localizedMessage}")

        }

        return dataOrException
    }
}