@file:Suppress("unused")

package ma.ya.core.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object GsonUtils {
	fun getLibGson(): Gson = gson
}

@PublishedApi
internal val gson by lazy {
	GsonBuilder()
		.disableHtmlEscaping()
		.setLenient()
		.serializeNulls()
		.create()
}

inline fun <reified T> T?.toJsonOrNull(): String? = kotlin.runCatching {
	gson.toJson(this, object : TypeToken<T>() {}.type)
}.getOrNull()

inline fun <reified T> String?.fromJsonOrNull(): T? = kotlin.runCatching {
	gson.fromJson<T>(this, object : TypeToken<T>() {}.type)
}.getOrNull()
