package pathak.creations.sbl.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface RetrofitApi {


    @GET
    suspend fun callGet(@Url url: String): Response<ResponseBody>

    @PUT
    suspend fun callPut(@Url url: String): Response<ResponseBody>

    @POST
    suspend fun callPost(@Url url: String, @Body body: RequestBody) : Response<ResponseBody>

    @Multipart
    @POST
    suspend fun callMultipart(@Url url: String,@PartMap map: HashMap<String,RequestBody>,
                              @Part part: ArrayList<MultipartBody.Part>) : Response<ResponseBody>

}