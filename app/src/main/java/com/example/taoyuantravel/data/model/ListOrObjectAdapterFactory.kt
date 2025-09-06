package com.example.taoyuantravel.data.model

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

// --- 基礎資料模型 ---

data class Image(
    @SerializedName("Src") val src: String = "",
    @SerializedName("Subject") val subject: String = "",
    @SerializedName("Ext") val ext: String = ""
)

data class Link(
    @SerializedName("Src") val src: String = "",
    @SerializedName("Subject") val subject: String = ""
)


// --- 包裝模型 (用於處理不一致的 API 回應) ---

data class ImageWrapper(val items: List<Image> = emptyList())
data class LinkWrapper(val items: List<Link> = emptyList())


// --- 自訂 Gson Type Adapter ---

/**
 * 這是一個 TypeAdapterFactory，用於創建一個能處理不一致 JSON 結構的 TypeAdapter。
 * API 中的 "Image" 和 "Link" 欄位有時回傳一個物件，有時回傳一個物件陣列。
 * 這個 Adapter 會將這兩種情況都統一轉換成一個 List。
 */
class ListOrObjectAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
        // 如果目標類型不是我們的包裝類型，則不使用此 Adapter
        if (typeToken.rawType != ImageWrapper::class.java && typeToken.rawType != LinkWrapper::class.java) {
            return null
        }

        val elementAdapter = gson.getAdapter(JsonElement::class.java)
        val delegate = gson.getDelegateAdapter(this, typeToken)

        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T) {
                delegate.write(out, value)
            }

            override fun read(`in`: JsonReader): T {
                val jsonElement = elementAdapter.read(`in`)
                if (jsonElement == null || jsonElement.isJsonNull || !jsonElement.isJsonObject) {
                    // 如果不是物件，則回傳空的包裝
                    return when (typeToken.rawType) {
                        ImageWrapper::class.java -> ImageWrapper() as T
                        LinkWrapper::class.java -> LinkWrapper() as T
                        else -> delegate.fromJsonTree(jsonElement)
                    }
                }

                val jsonObject = jsonElement.asJsonObject
                val itemType: Class<*>
                val dataKey: String

                // 判斷是哪種包裝類型，並設定對應的資料模型和 JSON key
                when (typeToken.rawType) {
                    ImageWrapper::class.java -> {
                        itemType = Image::class.java
                        dataKey = "Image"
                    }
                    LinkWrapper::class.java -> {
                        itemType = Link::class.java
                        dataKey = "Link"
                    }
                    else -> return delegate.fromJsonTree(jsonElement)
                }

                val dataElement = jsonObject.get(dataKey)
                val itemList = mutableListOf<Any>()

                if (dataElement != null && !dataElement.isJsonNull) {
                    if (dataElement.isJsonArray) {
                        // 如果是陣列，遍歷並解析每一個物件
                        dataElement.asJsonArray.forEach {
                            itemList.add(gson.fromJson(it, itemType))
                        }
                    } else if (dataElement.isJsonObject) {
                        // 如果是單一物件，直接解析
                        itemList.add(gson.fromJson(dataElement, itemType))
                    }
                }

                // 根據類型創建並回傳最終的包裝物件
                return when (typeToken.rawType) {
                    ImageWrapper::class.java -> ImageWrapper(itemList as List<Image>) as T
                    LinkWrapper::class.java -> LinkWrapper(itemList as List<Link>) as T
                    else -> delegate.fromJsonTree(jsonElement)
                }
            }
        }.nullSafe()
    }
}
