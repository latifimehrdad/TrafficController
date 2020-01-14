package com.ngra.trafficcontroller.dagger.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.ngra.trafficcontroller.dagger.DaggerScope;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ngra.trafficcontroller.dagger.retrofit.RetrofitApis.Host;

@Module
public class RetrofitModule {

    private Context context;
    public static boolean isCancel = false;

    public RetrofitModule(Context context) {
        this.context = context;
    }

    @Provides
    @DaggerScope
    public RetrofitApiInterface getRetrofitApiInterface(retrofit2.Retrofit retrofit) {
        return retrofit.create(RetrofitApiInterface.class);
    }

    @Provides
    @DaggerScope
    public retrofit2.Retrofit getRetrofit(OkHttpClient okHttpClient) {

        Gson gson = new GsonBuilder()
                .setLenient().create();

        return new retrofit2.Retrofit.Builder()
                .baseUrl(Host)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    @Provides
    @DaggerScope
    public OkHttpClient getOkHttpClient(Cache cache, HttpLoggingInterceptor interceptor, JavaNetCookieJar javaNetCookieJar) {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .cookieJar(javaNetCookieJar)
                .cache(cache)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }


    @Provides
    @DaggerScope
    public JavaNetCookieJar getJavaNetCookieJar(CookieHandler cookieHandler) {
        return new JavaNetCookieJar(cookieHandler);
    }


    @Provides
    @DaggerScope
    public CookieHandler getCookieHandler() {
        return new CookieManager();
    }

    @Provides
    @DaggerScope
    public HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Provides
    @DaggerScope
    public Cache getCache(File file) {
        return new Cache(file, 5 * 1000 * 1000);
    }

    @Provides
    @DaggerScope
    public File getFile() {
        return new File(context.getCacheDir(), "Okhttp_cache");
    }
}
