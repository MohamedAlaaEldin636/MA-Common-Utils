@file:Suppress("unused")

package ma.ya.core.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ma.ya.core.R
import ma.ya.core.databinding.ToastBinding

private var toast: Toast? = null

private enum class ToastType {
	NORMAL, SUCCESS, ERROR
}

@ColorInt
private fun Int?.orTransparentColor() = this ?: Color.TRANSPARENT

/**
 * - Note from android R custom toasts won't be displayed in background, so in that case use
 * [toastDefault], & about knowing if in background you can keep track using your activities on
 * pause and on resume OR application activities callbacks as well.
 */
private fun Context.getToastView(type: ToastType, msg: CharSequence): View {
	val binding = DataBindingUtil.inflate<ToastBinding>(
		layoutInflater, R.layout.toast, null, false
	)
	binding.constraintLayout.backgroundTintList = ColorStateList.valueOf(
		when (type) {
			ToastType.NORMAL -> R.color.def_toast_bg
			ToastType.SUCCESS -> R.color.toast_success_bg
			ToastType.ERROR -> R.color.toast_error_bg
		}.let {
			ContextCompat.getColor(this, it).orTransparentColor()
		}
	)
	val color = ContextCompat.getColor(this, R.color.def_toast_text_color).orTransparentColor()
	binding.textView.text = msg
	binding.textView.setTextColor(color)
	val drawable = run {
		when (type) {
			ToastType.NORMAL -> return@run null
			ToastType.SUCCESS -> R.drawable.baseline_done_24
			ToastType.ERROR -> R.drawable.baseline_clear_24
		}.let {
			ContextCompat.getDrawable(this, it)
		}
	}?.apply {
		setTint(color)
	}
	binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)

	return binding.root
}

/**
 * - Note from android R custom toasts won't be displayed in background, so in that case use
 * [toastDefault], & about knowing if in background you can keep track using your activities on
 * pause and on resume OR application activities callbacks as well.
 *
 * - this is normal toasting, see [toastSuccess] & [toastError]
 */
fun Context.toast(
	msg: CharSequence,
	duration: Int = Toast.LENGTH_SHORT,
	modifications: (Toast) -> Unit = {
		// Note from android R custom views won't be displayed in bg.
		it.view = getToastView(ToastType.NORMAL, msg)
	}
) = showToast(msg, duration, modifications)

/**
 * - Note from android R custom toasts won't be displayed in background, so in that case use
 * [toastDefault], & about knowing if in background you can keep track using your activities on
 * pause and on resume OR application activities callbacks as well.
 *
 * - See also [toast], [toastError]
 */
fun Context.toastSuccess(
	msg: CharSequence,
	duration: Int = Toast.LENGTH_SHORT,
	modifications: (Toast) -> Unit = {
		// Note from android R custom views won't be displayed in bg.
		it.view = getToastView(ToastType.SUCCESS, msg)
	}
) = showToast(msg, duration, modifications)

/**
 * - Note from android R custom toasts won't be displayed in background, so in that case use
 * [toastDefault], & about knowing if in background you can keep track using your activities on
 * pause and on resume OR application activities callbacks as well.
 *
 * - See also [toast], [toastSuccess]
 */
fun Context.toastError(
	msg: CharSequence,
	duration: Int = Toast.LENGTH_SHORT,
	modifications: (Toast) -> Unit = {
		// Note from android R custom views won't be displayed in bg.
		it.view = getToastView(ToastType.ERROR, msg)
	}
) = showToast(msg, duration, modifications)

fun Context.toastDefault(
	msg: CharSequence,
	duration: Int = Toast.LENGTH_SHORT,
) = showToast(msg, duration) {}

@Deprecated("Better use 1 of toast, toastDefault, toastSuccess, toastError")
fun Context.toastLong(
	msg: CharSequence,
	modifications: (Toast) -> Unit = {}
) = showToast(msg, Toast.LENGTH_LONG, modifications)

@Deprecated("Use Context.toast instead")
fun Context.showError(
	msg: CharSequence,
	duration: Int = Toast.LENGTH_SHORT,
	modifications: (Toast) -> Unit = {}
) = showToast(msg, duration, modifications)

@Suppress("UnusedReceiverParameter")
fun Context.dismissToast() {
	toast?.cancel()
	toast = null
}

private fun Context.showToast(msg: CharSequence, duration: Int, modifications: (Toast) -> Unit) {
	dismissToast()
	toast = Toast.makeText(this, msg, duration).also(modifications)
	toast?.show()
}
