package pathak.creations.sbl.retrofit

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



class RetrofitService {

    private var TAG: String = "RetrofitService"

    var mContext: Context
    var mRes: RetrofitResponse
    var mUrl: String
    var mCode: Int  = 0
    var mValue: Int  = 0
    lateinit var mJson: JSONObject
    lateinit var coroutineEx: CoroutineExceptionHandler
    private lateinit var   response : Response<ResponseBody>
    lateinit var mMap: HashMap<String, RequestBody>
    lateinit var mFiles: ArrayList<MultipartBody.Part>

    /**
     * get
     */
    constructor(context:Context, res: RetrofitResponse, url: String, code:Int, value:Int)
    {
        mContext = context
        mRes = res
        mUrl = url
        mCode = code
        mValue = value
    }

    /**
     * post
     */
    constructor(context:Context, res: RetrofitResponse, url: String, code:Int, json:JSONObject, value:Int)
    {
        mContext = context
        mRes = res
        mUrl = url
        mCode = code
        mValue = value
        mJson = json
    }
    /**
     * multipart List
     */
    constructor(context: Context, res: RetrofitResponse, url: String, code: Int, value: Int, map: HashMap<String, RequestBody>,
                files: ArrayList<MultipartBody.Part>?) {
        mContext = context
        mRes = res
        mUrl = url
        mCode = code
        mValue = value
        mMap = map
        mFiles = files!!
    }

    /**
     * Dialog Box
     */

    lateinit var dialogBuilderMain  : AlertDialog
    lateinit var tvRetryDialog  : TextView
    lateinit var tvCancelDialog  : TextView
    lateinit var clConnectionLost  : ConstraintLayout
    lateinit var pbDialog  : ProgressBar
    lateinit var retrofit: Retrofit

    fun callService(dialog:Boolean, token: String)
    {

        val dialogBuilder = AlertDialog.Builder(mContext)
        val layout = AlertDialogLayout.inflate(mContext, R.layout.dialog_loading,null)
        dialogBuilder.setView(layout)

        pbDialog = layout.findViewById(R.id.pbDialog)
        clConnectionLost = layout.findViewById(R.id.clConnectionLost)
        tvRetryDialog = layout.findViewById(R.id.tvRetryDialog)
        tvCancelDialog = layout.findViewById(R.id.tvCancelDialog)

        dialogBuilderMain = dialogBuilder.create()
        dialogBuilderMain.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuilderMain.setCancelable(false)
        dialogBuilderMain.setCanceledOnTouchOutside(false)


        if(dialog)
        {
            dialogBuilderMain.show()
        }

        if (token == "")
        {
            val okHttpClient = OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)

            okHttpClient.addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader("Accept", "application/json").build()
                chain.proceed(request)
            }

            retrofit = Retrofit.Builder()
                    .baseUrl(CommonKeys.BASE_URL)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
        }
        else {
            val httpClient = OkHttpClient.Builder().callTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)

            httpClient.addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader("Accept", "application/json").addHeader("Authorization", "Bearer $token").build()
                chain.proceed(request)
            }

            retrofit = Retrofit.Builder()
                    .baseUrl(CommonKeys.BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
        }


        val retrofitApi = retrofit.create<RetrofitApi>(RetrofitApi::class.java)

        val coroutineExceptionHandler = CoroutineExceptionHandler{ _, t->
            t.printStackTrace()

            Log.e(
                    TAG, "coroutineExceptionHandler: Something else went wrong" + t.message
                    + " ... " + t.cause + " >>> " + t.localizedMessage
            )
            if (dialogBuilderMain.isShowing) {

                GlobalScope.launch(Dispatchers.Main) {  reconnectService(dialogBuilderMain,token) }
            }
        }

        coroutineEx = coroutineExceptionHandler

        callCoroutine(retrofitApi)
    }

    private fun callCoroutine(
            retrofitApi: RetrofitApi
    )
    {
        CoroutineScope(Dispatchers.IO+coroutineEx).launch {
            Log.e("url ", mUrl)
            when(mValue)
            {
                1->{response = retrofitApi.callGet(mUrl) }
                2->{ response = mUrl.let { retrofitApi.callPost(it, RequestBody.create(MediaType.parse("application/json"),mJson.toString())) } }
                3->{ response = mUrl.let { retrofitApi.callMultipart(it, mMap,mFiles) } }
                4 -> {
                    response = retrofitApi.callPut(mUrl)
                }
            }

            CoroutineScope(Dispatchers.Main).launch {

                if(response.isSuccessful){
                    try {
                        dialogBuilderMain.dismiss()
                        val res = response.body()?.string().toString()
                        mRes.response(mCode,res)
                        Log.e("RetrofitService", "onRes==success: " + response.code())
                        Log.e("dataa", "onRes==success: " + response.message())
                        Log.e("dataa", "Responseee:=== $res")
                    }
                    catch (e:Exception)
                    {e.printStackTrace()}
                }
                else{
                    dialogBuilderMain.cancel()
                    val res = response.errorBody()?.string().toString()
                    mRes.response(mCode,res)
                    Log.e("RetrofitService", "onRes==Failure: " + response.code())
                    Log.e("reeeesss", "onRes==Failure: $res")
                }
            }
        }
    }

    private fun reconnectService(
            dialogBuilderMain: AlertDialog,
            token: String
    ) {
        try {

            dialogBuilderMain.window!!.setBackgroundDrawable(mContext.getDrawable(R.drawable.round_border_green))
            pbDialog.visibility = View.GONE
            clConnectionLost.visibility = View.VISIBLE
            tvRetryDialog.setOnClickListener {
                dialogBuilderMain.dismiss()
                callService(true,token)
            }
            tvCancelDialog.setOnClickListener {
                dialogBuilderMain.dismiss()
            }
        }

        catch (e: Exception) {
            e.printStackTrace()
        }
    }

}