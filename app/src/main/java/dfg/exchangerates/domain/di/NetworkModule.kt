package dfg.exchangerates.domain.di

import android.provider.SyncStateContract
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.exchangerates.data.network.ExchangeRatesAPI
import dfg.exchangerates.data.network.ExchangeRatesApiService
import dfg.exchangerates.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {


    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        val httpBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpBuilder.addInterceptor(interceptor)

        return httpBuilder.build()
    }

    @Singleton
    @Provides
    fun provideExchangeRatesApi(okHttpClient: OkHttpClient): ExchangeRatesAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        return retrofit.create(ExchangeRatesAPI::class.java)
    }

//    @Singleton
//    @Provides
//    fun provideExchangeRatesApiService() : ExchangeRatesApiService {
//        return ExchangeRatesApiService()
//    }

    @Singleton
    @Provides
    fun provideGson(): Gson{
        return GsonBuilder().create()
    }
}