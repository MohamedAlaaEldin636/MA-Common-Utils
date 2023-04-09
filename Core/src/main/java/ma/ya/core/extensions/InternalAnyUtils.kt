package ma.ya.core.extensions

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

internal fun Any?.shouldShowRequestPermissionRationaleFromAny(permission: String): Boolean = when (this) {
	is Fragment -> shouldShowRequestPermissionRationale(permission)
	is AppCompatActivity -> ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
	else -> false
}

internal fun Any?.getActivityOrNullFromAny() = when (this) {
	is Fragment -> activity
	is AppCompatActivity -> this
	else -> null
}

internal fun <I, O> Any?.registerForActivityResultFromAny(
	contract: ActivityResultContract<I, O>,
	callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
	return when (this) {
		is Fragment -> {
			registerForActivityResult(contract, callback)
		}
		is AppCompatActivity -> {
			registerForActivityResult(contract, callback)
		}
		else -> throw RuntimeException("Unexpected host $this")
	}
}
