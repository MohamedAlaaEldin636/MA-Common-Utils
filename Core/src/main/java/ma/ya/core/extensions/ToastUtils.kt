@file:Suppress("unused")

package ma.ya.core.extensions

import android.content.Context
import android.widget.Toast

private var toast: Toast? = null

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
