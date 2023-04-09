package ma.ya.core.extensions

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import ma.ya.core.R
import ma.ya.core.helperClasses.MALogger

fun FragmentActivity.showAlertDialog(
	title: String,
	message: String,
	onDismissListener: () -> Unit = {},
	onPositiveButtonClick: () -> Unit
) {
	AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(
			getString(R.string.ok)
		) { _, _ ->
			onPositiveButtonClick()
		}
		.setOnDismissListener {
			onDismissListener()
		}
		.createAndShowSafely()
}

private fun AlertDialog.Builder.createAndShowSafely() {
	kotlin.runCatching {
		create().show()
	}.getOrElse {
		MALogger.e("createAndShowSafely error $it")
	}
}
