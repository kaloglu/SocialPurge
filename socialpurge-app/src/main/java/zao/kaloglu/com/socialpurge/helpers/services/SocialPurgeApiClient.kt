/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package zao.kaloglu.com.socialpurge.helpers.services

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import zao.kaloglu.com.socialpurge.BuildConfig
import zao.kaloglu.com.socialpurge.helpers.services.api.SocialPurgeApi
import java.util.concurrent.ConcurrentHashMap

/**
 * A class to allow authenticated access to Twitter API endpoints.
 * Can be extended to provided additional endpoints by extending and providing Retrofit API
 * interfaces to [SocialPurgeApiClient.getService]
 */
open class SocialPurgeApiClient internal constructor() {
    internal val services: ConcurrentHashMap<Class<*>, Any>
    private val retrofit: Retrofit

    init {
        this.services = buildConcurrentMap()
        this.retrofit = buildRetrofit()
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .addInterceptor(
                                HttpLoggingInterceptor()
                                        .setLevel(when {
                                            BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.BODY
                                            else -> HttpLoggingInterceptor.Level.BASIC
                                        })
                        )!!.build()!!)
                .baseUrl(SocialPurgeApi().baseHostUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .build()
    }

    private fun <K, V> buildConcurrentMap(): ConcurrentHashMap<K, V> = ConcurrentHashMap()

    internal fun getSimpleGetServices(): SimpleGetServices = getService(SimpleGetServices::class.java)

    internal fun getSimplePostServices(): SimplePostServices = getService(SimplePostServices::class.java)

    /**
     * Converts Retrofit style interface into instance for API access
     *
     * @param cls Retrofit style interface
     * @return instance of cls
     */
    @Suppress("UNCHECKED_CAST")
    protected fun <T> getService(cls: Class<T>): T {
        if (!services.contains(cls)) {
            services.putIfAbsent(cls, retrofit.create(cls)!!)
        }
        return services[cls] as T
    }


}
