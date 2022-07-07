package com.scgts.sctrace.network.retrofit

import com.scgts.sctrace.base.model.ArchivedAsset
import com.scgts.sctrace.base.model.AssetRest
import com.scgts.sctrace.base.model.Identifiable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AssetService {
    @GET("/asset-upload/fetch/{projectId}")
    fun getAssetsByProject(@Path("projectId") projectId: String): Single<List<AssetRest>>

    @GET("/asset-upload/fetch/{projectId}")
    fun getAssetsByProject(
        @Path("projectId") projectId: String,
        @Query("last_sync_timestamp") lastUpdated: String,
    ): Single<List<AssetRest>>

    @GET("/asset-upload/archived-assets/{projectId}")
    fun getArchivedAssetsByProject(
        @Path("projectId") projectId: String,
        @Query("timestamp") lastUpdated: String
    ): Single<List<ArchivedAsset>>
}
