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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.twitter.sdk.android.core.models.BindingValues
import com.twitter.sdk.android.core.models.BindingValuesAdapter
import com.twitter.sdk.android.core.models.SafeListAdapter
import com.twitter.sdk.android.core.models.SafeMapAdapter
import com.twitter.sdk.android.core.services.StatusesService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import zao.kaloglu.com.socialpurge.helpers.responses.SimpleResponses
import zao.kaloglu.com.socialpurge.helpers.services.api.SocialPurgeApi
import java.util.concurrent.ConcurrentHashMap

/**
 * A class to allow authenticated access to Twitter API endpoints.
 * Can be extended to provided additional endpoints by extending and providing Retrofit API
 * interfaces to [SocialPurgeApiClient.getService]
 */
class SocialPurgeApiClient internal constructor(client: OkHttpClient, socialPurgeApi: SocialPurgeApi) {
    internal val services: ConcurrentHashMap<Class<*>, Any>
    internal val retrofit: Retrofit

    /**
     * Constructs Guest Session based TwitterApiClient.
     */
    constructor() : this(OkHttpClient(), SocialPurgeApi())


    init {
        this.services = buildConcurrentMap()
        this.retrofit = buildRetrofit(client, socialPurgeApi)
    }

    private fun buildRetrofit(httpClient: OkHttpClient, socialPurgeApi: SocialPurgeApi): Retrofit {
        return Retrofit.Builder()
                .client(httpClient)
                .baseUrl(socialPurgeApi.baseHostUrl)
                .addConverterFactory(GsonConverterFactory.create(buildGson()))
                .build()
    }

    private fun buildGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapterFactory(SafeListAdapter())
                .registerTypeAdapterFactory(SafeMapAdapter())
                .registerTypeAdapter(BindingValues::class.java, BindingValuesAdapter())
                .create()
    }

    private fun <K, V> buildConcurrentMap(): ConcurrentHashMap<K, V> = ConcurrentHashMap()

    /**
     * @return [StatusesService] to access TwitterApi
     */
    internal fun getSimpleServices(): SimpleGetServices = getService(SimpleGetServices::class.java)

    fun <responseClass : SimpleResponses.BaseResponse> requestGET(
            lambda: Any,
            action: (Any) -> Call<responseClass>,
            callbackAction: (Class<responseClass>) -> Callback<responseClass>
    ) =
            action(lambda).enqueue(callbackAction(this))


    fun <ResponseClass> requestPOST(service: BaseServices.PostServices, action: () -> Call<ResponseClass>, cb: Callback<ResponseClass>) {
        service.apply {
            action().enqueue(cb)
        }
    }

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
