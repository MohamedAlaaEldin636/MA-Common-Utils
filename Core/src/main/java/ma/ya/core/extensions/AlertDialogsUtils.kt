package ma.ya.core.extensions

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import ma.ya.core.R
import ma.ya.core.helperClasses.MALogger

fun FragmentActivity.showAlertDialog(
	title: String,
	message: String,
	onDismissListener: () -> Unit = {},
	onPositiveButtonClick: (DialogInterface) -> Unit
) {
	var clickedOnPositiveClick = false

	AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(
			getString(R.string.ok)
		) { dialog, _ ->
			clickedOnPositiveClick = true
			onPositiveButtonClick(dialog)
		}
		.setOnDismissListener {
			if (clickedOnPositiveClick.not()) {
				onDismissListener()
			}
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
