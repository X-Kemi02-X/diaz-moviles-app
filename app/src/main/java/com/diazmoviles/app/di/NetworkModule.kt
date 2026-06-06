package com.diazmoviles.app.di

import com.diazmoviles.app.BuildConfig
import com.diazmoviles.app.data.local.TokenDataStore
import com.diazmoviles.app.data.remote.api.AuthApi
import com.diazmoviles.app.data.remote.api.CategoriaApi
import com.diazmoviles.app.data.remote.api.ClienteApi
import com.diazmoviles.app.data.remote.api.MarcaApi
import com.diazmoviles.app.data.remote.api.ProductoApi
import com.diazmoviles.app.data.remote.api.ProveedorApi
import com.diazmoviles.app.data.remote.api.RegisterApi
import com.diazmoviles.app.data.remote.api.VentaApi
import com.diazmoviles.app.data.remote.interceptor.BearerTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        bearerTokenInterceptor: BearerTokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(bearerTokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProductoApi(retrofit: Retrofit): ProductoApi {
        return retrofit.create(ProductoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideClienteApi(retrofit: Retrofit): ClienteApi {
        return retrofit.create(ClienteApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVentaApi(retrofit: Retrofit): VentaApi {
        return retrofit.create(VentaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMarcaApi(retrofit: Retrofit): MarcaApi {
        return retrofit.create(MarcaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoriaApi(retrofit: Retrofit): CategoriaApi {
        return retrofit.create(CategoriaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProveedorApi(retrofit: Retrofit): ProveedorApi {
        return retrofit.create(ProveedorApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRegisterApi(retrofit: Retrofit): RegisterApi {
        return retrofit.create(RegisterApi::class.java)
    }
}
