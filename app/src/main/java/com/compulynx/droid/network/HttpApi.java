package com.compulynx.droid.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.compulynx.droid.db.Transaction;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class HttpApi {

    public static final String BASE_URL = "";
    public static final String ENDPOINT_LOGIN = "customers/login";
    public static final String ENDPOINT_ACCOUNT_BALANCE = "accounts/balance";
    public static final String ENDPOINT_LAST_100_TXNS = "transactions/last-100-transactions";
    public static final String ENDPOINT_SEND_MONEY = "transactions/send-money";


    private static HttpApi httpApi;
    private ApiInterface apiInterface;

    public static synchronized HttpApi getInstance() {
        if (httpApi == null)
            httpApi = new HttpApi();
        return httpApi;
    }

    private HttpApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL.concat("/api/v1/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    public Observable<JsonObject> post(String path, JsonObject payload) {
        return apiInterface.post(path, payload);
    }

    public Observable<List<Transaction>> fetchLast100Txns(JsonObject payload) {
        return apiInterface.fetchLast100Txns(payload);
    }

    private interface ApiInterface {
        @POST("{path}")
        Observable<JsonObject> post(@Path("path") String path, @Body JsonObject post);

        @POST(ENDPOINT_LAST_100_TXNS)
        Observable<List<Transaction>> fetchLast100Txns(@Body JsonObject post);
    }

    public static boolean isInternetOK(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}

