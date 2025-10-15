package com.miapp.greenbunny.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductImage(
    @SerializedName("path") val path: String,
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("size") val size: Int?,
    @SerializedName("mime") val mime: String?,
    @SerializedName("access") val access: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("meta") val meta: ImageMeta?
) : Serializable

data class ImageMeta(
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?
) : Serializable
