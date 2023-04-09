package ma.ya.core.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import ma.ya.core.R

fun <I> ActivityResultLauncher<I>.launchSafely(context: Context?, input: I) {
	try {
		launch(input)
	} catch (exception: ActivityNotFoundException) {
		context?.showError(context.getString(R.string.no_app_can_handle_this_action))
	}
}
