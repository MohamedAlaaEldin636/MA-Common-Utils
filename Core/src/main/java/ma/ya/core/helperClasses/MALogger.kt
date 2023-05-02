package ma.ya.core.helperClasses

import android.util.Log

object MALogger {

	private const val MA_LOGGER = "MA_LOGGER"

	@JvmStatic
	fun e(any: Any?) {
		Log.e(MA_LOGGER, "$MA_LOGGER -> $any")
	}

	@JvmStatic
	fun d(any: Any?) {
		Log.d(MA_LOGGER, "$MA_LOGGER -> $any")
	}

}
