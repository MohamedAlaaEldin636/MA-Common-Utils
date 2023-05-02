package ma.ya.core.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import ma.ya.core.R
import ma.ya.core.helperClasses.MALogger

fun <I> ActivityResultLauncher<I>.launchSafely(context: Context?, input: I) {
	try {
		MALogger.e("sadhiasudh onActivityPermissionsLauncherResult outer 1 -> $input")
		launch(input)
		MALogger.e("sadhiasudh onActivityPermissionsLauncherResult outer 2 -> $input")
	} catch (exception: ActivityNotFoundException) {
		context?.showError(context.getString(R.string.no_app_can_handle_this_action))
	}
}
