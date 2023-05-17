@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ma.ya.core.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object GsonUtils {
	fun getLibGson(): Gson = gson

	/**
	 * For this to work you better call it before any usage of [toJsonOrNull], [fromJsonOrNull]
	 * or [getLibGson]
	 */
	fun addAdditionalGsonSetups(setups: GsonBuilder.() -> GsonBuilder) {
		additionalGsonSetups = setups
	}
}

private var additionalGsonSetups: (GsonBuilder.() -> GsonBuilder)? = null

@PublishedApi
internal val gson by lazy {
	GsonBuilder()
		.disableHtmlEscaping()
		.setLenient()
		.serializeNulls()
		.let {
			val setups = additionalGsonSetups

			if (setups == null) it else it.setups()
		}
		.create()
}

inline fun <reified T> T?.toJsonOrNull(): String? = kotlin.runCatching {
	gson.toJson(this, object : TypeToken<T>() {}.type)
}.getOrNull()

inline fun <reified T> String?.fromJsonOrNull(): T? = kotlin.runCatching {
	gson.fromJson<T>(this, object : TypeToken<T>() {}.type)
}.getOrNull()
