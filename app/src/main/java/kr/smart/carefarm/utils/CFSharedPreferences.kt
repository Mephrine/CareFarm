package kr.smart.carefarm.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception


private const val FILENAME = "ScpSharedPreferences"
private const val PREF_USER_NAME = "userName"
private const val PREF_USER_ID = "userId"
private const val PREF_AUTO_ID = "autoId"

class CFSharedPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(FILENAME, 0)
    var userName: String
        get() = prefs.getString(PREF_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(PREF_USER_NAME, value).apply()

    var userId: String
        get() = prefs.getString(PREF_USER_ID, "") ?: ""
        set(value) = prefs.edit().putString(PREF_USER_ID, value).apply()

    var autoId: String
        get() = prefs.getString(PREF_AUTO_ID, "") ?: ""
        set(value) = prefs.edit().putString(PREF_AUTO_ID, value).apply()

//    var trainingList: List<TrainingData>
//        get() = prefs.getList(prefs, PREF_PATROL_TRAINING_LIST)
//        set(value) = prefs.edit().putList(prefs, PREF_PATROL_TRAINING_LIST, value).apply {  }
//
//    // 네트워크 오류 시, 순찰 완료에서 추가해서 보낼 데이터들.
//    var trainingFailList: List<TrainingData>
//        get() = prefs.getList(prefs, PREF_PATROL_TRAINING_FAIL_LIST)
//        set(value) = prefs.edit().putList(prefs, PREF_PATROL_TRAINING_FAIL_LIST, value).apply {  }
}

class SomeClass<T> {
    var myVar: T? = null
        set(value) {
            executeCustomFunc(value)
            field = value
        }

    private fun executeCustomFunc(v: T?) {

    }
}

private fun <T> SharedPreferences.Editor.putList(prefs: SharedPreferences, id: String, values: List<T>) {
    val editor = prefs.edit()
    val gson = Gson()
    val type = object: TypeToken<List<T>>() {
    }.type

    val json = gson.toJson(values, type)

    editor.putString(id, json)
    editor.apply()
}

//private fun <T> SharedPreferences.getList(prefs: SharedPreferences, id: String): List<T> {
//    val gson = Gson()
//    val json = prefs.getString(id, "")
//
//    val type = if (id == PREF_PATROL_LIST || id == PREF_PATROL_FAIL_LIST) {
//        object : TypeToken<List<PatrolNfcData>>() {
//        }.type
//    } else if (id == PREF_PATROL_TRAINING_LIST || id == PREF_PATROL_TRAINING_FAIL_LIST) {
//        object : TypeToken<List<TrainingData>>() {
//        }.type
//    } else {
//        object : TypeToken<List<T>>() {
//        }.type
//    }
//
//    if (json == null || json == "") {
//        return ArrayList()
//    } else {
//        try {
//            return gson.fromJson(json, type)
//        } catch (e: Exception) {
//            return ArrayList()
//        }
//    }
//}