package pathak.creations.sbl.common

object CommonKeys {


    const val BASE_URL = "http://secondary.sbl1972.in/dsr/public/index.php/api/"
    const val BASE_IMAGE_Profile = "http://3.13.214.27:8011/uploads/users/"
    const val BASE_IMAGE_Movies = "http://3.13.214.27:8011/uploads/movies/"
    const val BASE_IMAGE_Members = "http://3.13.214.27:8011/uploads/movieMembers/"


    //http://3.13.214.27:8011/uploads/movieMembers/
    // http://3.13.214.27:8011/uploads/users/

    const val LOGIN = "v1/login"
    const val LOGIN_CODE = 1

    const val RETAILER_LIST = "v1/dists"
    const val RETAILER_LIST_CODE = 2

    const val BEAT_LIST = "v1/beats"
    const val BEAT_LIST_CODE = 3

    const val BEAT_RETAILER_LIST = "v1/beat-retailers"
    const val BEAT_RETAILER_LIST_CODE = 4


    const val CATEGORIES = "v1/sblcats"
    const val CATEGORIES_CODE = 5

/*
    const val CATEGORIES = "v1/categories"
    const val CATEGORIES_CODE = 5
*/

    const val GET_RETAILERS = "v1/get-retailers"
    const val GET_RETAILERS_CODE = 6

    const val EDIT_RETAILER = "v1/edit-retailer"
    const val EDIT_RETAILER_CODE = 7

    const val ADD_RETAILER = "v1/add-retailer"
    const val ADD_RETAILER_CODE = 8

    const val ALL_BEATS = "v1/all-beats"
    const val ALL_BEATS_CODE = 9

    const val ALL_RETAILERS = "v1/all-retailers"
    const val ALL_RETAILERS_CODE = 10

    const val ADD_CART = "v1/create-sale-order"
    const val ADD_CART_CODE = 11


    val ID = "id"
    val ROLE = "role"
    val STATUS = "status"
    val NAME = "name"
    val USERNAME = "user_name"
    val EMAIL = "email"
    val IMAGE = "image"
    val COUNTRY_CODE = "country_code"
    val PHONE = "phone"
    val COUNTRY_CODE_PHONE = "country_code_phone"
    val LOCATION = "location"
    val ADDRESS = "address"
    val LATITUDE = "latitude"
    val LONGITUDE = "longitude"
    val OTP = "Otp"
    val OTP_VERIFIED = "otp_verified"
    val TYPE = "type"
    val DEVICE_TOKEN = "device_token"
    val SOCIAL_ID = "social_id"
    val SOCIAL_TYPE = "social_type"
    val FORGOT_PASSWORD_HASH = "forgot_password_word"
    val IAT = "iat"
    val TOKEN = "token"
    val NOTIFICATION_TOKEN = "notification_token"
    val EMAIL_VERIFIED = "email_verified"



    val IS_LOCATION_CHECKED = "location_checked"
    val IS_FIRST_CHECKED = "first_checked"
    val SELECTED_DISTRIBUTOR = "selected_distributor"
    val SELECTED_DISTRIBUTOR_NAME = "selected_distributor_name"
    val CURRENT_DATE = "current_date"

}