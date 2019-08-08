package com.san.app.merchant.network;

import java.util.Map;

import com.san.app.merchant.model.ActivityDetailsModel;
import com.san.app.merchant.model.BookingModel;
import com.san.app.merchant.model.CountryListModel;
import com.san.app.merchant.model.MyActivityModel;
import com.san.app.merchant.model.NotificationModel;
import com.san.app.merchant.model.SalesReportModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;


public interface ApiInterface {

    @FormUrlEncoded
    @POST("merchantlogin?language_id=1")
        // Login
    Call<ResponseBody> merchantLogin(@Field("email") String email,
                                     @Field("password") String password,
                                     @Field("device_token") String device_token,
                                     @Field("device_type") String device_type);

    @FormUrlEncoded
    @POST("forgotpassword?language_id=1")
        // Forgot password
    Call<ResponseBody> forgotPassword(@Field("email") String email);

    @POST("change_password?language_id=1")
        //change password
    Call<ResponseBody> changePassword(@Header("auth_token") String auth_token,
                                      @QueryMap Map<String, String> options);

    @FormUrlEncoded
    @POST("merchantbooking?language_id=1")
        // Booking list Main screen
    Call<BookingModel> merchantBooking(@Header("auth_token") String auth_token,
                                       @QueryMap Map<String, String> options,
                                       @Field("from_date") String from_date,
                                       @Field("to_date") String to_date,
                                       @Field("activity_name") String activity_name,
                                       @Field("category_id") String category_id,
                                       @Field("customer_name") String customer_name,
                                       @Field("booking_status") String booking_status, // 0 = Pending ,1 = Cancel, 2 = Confirm
                                       @Field("is_redeem") String is_redeem, // 0 = not Redeem , 1 = Redeem , 2 = Redeem Expire
                                       @Field("order_number") String order_number);

    @FormUrlEncoded
    @POST("merchantbookingreport?language_id=1")
        // Booking report list
    Call<SalesReportModel> merchantBookingReport(@Header("auth_token") String auth_token,
                                                 @QueryMap Map<String, String> options,
                                                 @Field("from_date") String from_date,
                                                 @Field("to_date") String to_date,
                                                 @Field("activity_name") String activity_name,
                                                 @Field("category_id") String category_id,
                                                 @Field("customer_name") String customer_name,
                                                 @Field("order_number") String order_number);

    @FormUrlEncoded
    @POST("merchantactivitylist?language_id=1")
        // My Activity list
    Call<MyActivityModel> merchantActivityList(@Header("auth_token") String auth_token,
                                               @QueryMap Map<String, String> options,
                                               @Field("searchterm") String searchterm,
                                               @Field("category") String category,
                                               @Field("location") String location,
                                               @Field("statusactivity") String statusactivity);

    @FormUrlEncoded
    @POST("add-booking-note?language_id=1")
        // send Booking note to customer
    Call<ResponseBody> sendBookingNote(@Header("auth_token") String auth_token,
                                       @Field("order_number") String order_number,
                                       @Field("title") String title,
                                       @Field("description") String description);

    @FormUrlEncoded
    @POST("voucherredeem?language_id=1")
        // voucher redeem
    Call<ResponseBody> voucherRedeem(@Header("auth_token") String auth_token,
                                     @Field("order_number") String order_number,
                                     @Field("voucher_number") String voucher_number);

    @FormUrlEncoded
    @POST("bookingstatus?language_id=1")
        // Booking Confirm or Cancel  (status = 2 for confirm and status = 1 for canceled)
    Call<ResponseBody> bookingStatus(@Header("auth_token") String auth_token,
                                     @Field("order_id") int order_id,
                                     @Field("status") int status);

    @FormUrlEncoded
    @POST("voucherinfo?language_id=1")
        //voucherinfo
    Call<ResponseBody> voucherInfo(@Header("auth_token") String auth_token,
                                   @Field("voucher_number") String voucher_number);

    @FormUrlEncoded
    @POST("reportdetail?language_id=1")
        //Booking details from Report
    Call<ResponseBody> reportDetail(@Header("auth_token") String auth_token,
                                    @Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("notes?language_id=1")
        //Note List(Booking details)
    Call<ResponseBody> notes(@Header("auth_token") String auth_token,
                             @Field("order_id") String order_id);

    @GET("logout?language_id=1")
        // For logout api
    Call<ResponseBody> logout(@Header("auth_token") String auth_token);

    @GET("categorieslist?language_id=1")
        // For logout api
    Call<ResponseBody> categoriesList(@Header("auth_token") String auth_token);

    @GET("bookingnotification?language_id=1")
        // Notification list of booking
    Call<NotificationModel> bookingNotification(@Header("auth_token") String auth_token,
                                                @QueryMap Map<String, String> options);

    //for Activity Details screen
    @GET("viewactivity?language_id=1")
    Call<ActivityDetailsModel> viewActivityDetails(@Header("auth_token") String auth_token,
                                                   @QueryMap Map<String, String> options);

    //for All Review Fetch
    @GET("activityreview?language_id=1")
    Call<ResponseBody> allActivityReview(@QueryMap Map<String, String> options);

    @GET("getcountrylist?language_id=1")
    Call<CountryListModel> getcountrylist();

    @FormUrlEncoded
    @POST("updateprofile?language_id=1")
        // UpdateProfile
    Call<ResponseBody> updateprofile(@Header("auth_token") String auth_token,
                                     @Field("first_name") String first_name,
                                     @Field("company_name") String company_name,
                                     @Field("website") String website,
                                     @Field("country_name") String country_name,
                                     @Field("city_name") String city_name,
                                     @Field("country_code") String country_code,
                                     @Field("mobile_number") String mobile_number,
                                     @Field("sst_certificate") String sst_certificate);
}
