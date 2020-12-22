package pathak.creations.sbl.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

object PreferenceFile {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    val shared_preference_key = "CollectUSER"
    val shared_preference_first = "firstLogin"
    val shared_preference_forever = "Forever"
    val shared_preference_walk = "walkthrough"


    fun storeWalkThroughKey(context: Context, key: String, value: String) {
        sharedPreferences = context.getSharedPreferences(shared_preference_walk, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun retrieveWalkThroughKey(context: Context, key: String): String? {
        sharedPreferences = context.getSharedPreferences(shared_preference_walk, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }

    fun storeForeverKey(context: Context, key: String, value: String) {
        sharedPreferences = context.getSharedPreferences(shared_preference_forever, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun retrieveForeverKey(context: Context, key: String): String? {
        sharedPreferences = context.getSharedPreferences(shared_preference_forever, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }

    fun storeKey(context: Context, key: String, value: String) {
        sharedPreferences = context.getSharedPreferences(shared_preference_key, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        editor.putString(key, value)

        editor.apply()

    }

    fun storeKeyNull(context: Context, key: String, value: String, defValue: String) {
        sharedPreferences = context.getSharedPreferences(shared_preference_key, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        if (value == "null") {
            editor.putString(key, defValue)
        } else {
            editor.putString(key, value)
        }
        editor.apply()

    }

    fun storeIntKey(context: Context, key: String, value: Int) {
        sharedPreferences = context.getSharedPreferences(shared_preference_key, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()

    }

    fun storefirstKey(context: Context, key: String, value: String) {
        sharedPreferences = context.getSharedPreferences(shared_preference_first, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()

    }

    fun retrievefirstKey(context: Context, key: String): String? {
        sharedPreferences = context.getSharedPreferences(shared_preference_first, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    fun retrieveIntKey(context: Context, key: String): String? {
        sharedPreferences = context.getSharedPreferences(shared_preference_key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    fun retrieveKey(context: Context, key: String): String? {

        Log.e("fdddfdf", "==11===$context==$key")
        sharedPreferences = context.getSharedPreferences(shared_preference_key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    fun removeAll(context: Context) {
        sharedPreferences = context.getSharedPreferences(shared_preference_key, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun removekey(context: Context, key: String) {
        sharedPreferences = context.getSharedPreferences(shared_preference_key, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun storeandparseJsonData(context: Context, jsonObject: JSONObject) {
        sharedPreferences = context.getSharedPreferences(shared_preference_key, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        try {
            editor.putString("", jsonObject.getString(""))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        editor.apply()
    }


    /*To Store Fcm Device ID*/
    fun storeFcmDeviceId(context: Context, `val`: String) {
        sharedPreferences = context.getSharedPreferences("FCM", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString("DEVICE", `val`)
        editor.apply()
    }

    fun retrieveFcmDeviceId(context: Context): String? {
        sharedPreferences = context.getSharedPreferences("FCM", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        return sharedPreferences.getString("DEVICE", null)
    }

}