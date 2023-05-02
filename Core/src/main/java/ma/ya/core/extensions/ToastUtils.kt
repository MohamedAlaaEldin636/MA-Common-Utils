@file:Suppress("unused")

package ma.ya.core.extensions

import android.content.Context
import android.widget.Toast

private var toast: Toast? = null


fun Context.toast(
	msg: CharSequence,
	duration: Int = Toast.LENGTH_SHORT,
	modifications: (Toast) -> Unit = {}
) = showToast(msg, duration, modifications)

fun Context.toastLong(
	msg: CharSequence,
	modifications: (Toast) -> Unit = {}
) = showToast(msg, Toast.LENGTH_LONG, modifications)

// todo rename to toastError and for normal toast create toast and for success toastSuccess isa.
@Deprecated("Use Context.toast instead")
fun Context.showError(
	msg: CharSequence,
	duration: Int = Toast.LENGTH_SHORT,
	modifications: (Toast) -> Unit = {}
) = showToast(msg, duration, modifications)

fun Context.dismissToast() {
	toast?.cancel()
	toast = null
}

private fun Context.showToast(msg: CharSequence, duration: Int, modifications: (Toast) -> Unit) {
	toast = Toast.makeText(this, msg, duration).also(modifications)
	toast?.show()
}
