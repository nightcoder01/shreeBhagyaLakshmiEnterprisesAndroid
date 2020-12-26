package pathak.creations.sbl.common

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import pathak.creations.sbl.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

object CommonMethods  {

    fun isNetworkAvailable(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifi != null && wifi.isConnected) {
            return true
        }

        val mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (mobile != null && mobile.isConnected) {
            return true
        }

        val networkInfo = connectivityManager.activeNetworkInfo
        return !(networkInfo == null || !networkInfo.isConnected)
    }

    fun alertDialog(context: Context, msg: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(R.string.app_name)

        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setPositiveButton(context.getString(R.string.ok)) { arg0, _ -> arg0.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun alertDialogIntentClear(
        context: Context,
        msg: String,
        to: Class<*>
    ) {

        val alert = AlertDialog.Builder(context)
        alert.setTitle(context.getString(R.string.app_name))
        alert.setMessage(msg)
        alert.setPositiveButton(context.getString(R.string.ok)) { dialogInterface, i ->
            dialogInterface.dismiss()
            val it = Intent(context, to)
            it.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(it)

        }
        alert.setCancelable(false)
        alert.create().show()
    }

    fun getGender(string: String): String {

        return when (string) {
            "1" -> "Female"
            else -> "Male"
        }
    }


    fun hideKeyboard(view: View?) {
        if (view != null) {
            val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    fun getAge(dateOfBirth: String): Int {


        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance()

        birthDate.time = dateFormat.parse(dateOfBirth)!!
        require(!birthDate.after(today)) {
            return 0
        }

        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH)

        if (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3 || birthDate.get(
                Calendar.MONTH
            ) > today.get(Calendar.MONTH)
        ) {
            val days = birthDate.get(Calendar.DAY_OF_MONTH) - today.get(Calendar.DAY_OF_MONTH)
            age--

            Log.e("month is", month.toString() + "")
            Log.e("Days", "$days left")


        } else if (birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) && birthDate.get(
                Calendar.DAY_OF_MONTH
            ) > today.get(Calendar.DAY_OF_MONTH)
        ) {

            age--
        }

        return age
    }


    @ExperimentalTime
    fun getDateDiff(format: SimpleDateFormat, oldDate: String, newDate: String): Long {
        return try {
            DurationUnit.DAYS.convert(
                format.parse(newDate).time - format.parse(oldDate).time,
                DurationUnit.MILLISECONDS
            )
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

}