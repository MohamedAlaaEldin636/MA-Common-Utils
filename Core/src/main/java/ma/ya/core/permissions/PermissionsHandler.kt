package ma.ya.core.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Size
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import ma.ya.core.R
import ma.ya.core.extensions.*
import ma.ya.core.extensions.getActivityOrNullFromAny
import ma.ya.core.extensions.registerForActivityResultFromAny
import ma.ya.core.extensions.shouldShowRequestPermissionRationaleFromAny
import ma.ya.core.helperClasses.MALogger
import java.lang.ref.WeakReference

fun Fragment.createPermissionHandlerForSinglePermission(
	permission: String,
	onPermissionGranted: () -> Unit
) = PermissionsHandler(
	this,
	lifecycle,
	requireContext(),
	listOf(permission),
	object : PermissionsHandler.Listener {
		override fun onAllPermissionsAccepted() {
			onPermissionGranted()
		}
	}
)

class ListenerOfPermissionsHandlerWhichActOnlyIfAllGranted(
	context: Context?,
	private val onPermissionGranted: () -> Unit
) : PermissionsHandler.Listener {

	private val weakRefContext = WeakReference(context)

	override fun onAllPermissionsAccepted() {
		MALogger.e("sadhiasudh on allll+ ${weakRefContext.get()}")
		onPermissionGranted()
	}

	override fun onSubsetPermissionsAccepted(permissions: Map<String, Boolean>) {
		MALogger.e("sadhiasudh on subbbbbbbbb")
		weakRefContext.get()?.apply {
			showError(getString(R.string.not_all_permissions_are_accepted))
		}
	}
}

fun Fragment.createPermissionHandlerAndActOnlyIfAllGranted(
	vararg permissions: String,
	listener: ListenerOfPermissionsHandlerWhichActOnlyIfAllGranted
) = PermissionsHandler(
	this,
	lifecycle,
	requireContext(),
	permissions.toList(),
	listener
)

fun Fragment.createPermissionHandlerAndActOnlyIfAllGranted(
	vararg permissions: String,
	onPermissionGranted: () -> Unit
) = PermissionsHandler(
	this,
	lifecycle,
	requireContext(),
	permissions.toList(),
	ListenerOfPermissionsHandlerWhichActOnlyIfAllGranted(context, onPermissionGranted),
)

/**
 * # Info
 *
 * - Used to handle permissions with all possible cases
 * 1. if not granted where we should display a dialog due to `shouldShowRequestPermissionRationale`
 * returning `true`.
 * 2. if user pressed don't show again we redirect to the application settings.
 * 3. you can overrider the listener to act in case subset of permissions are accepted.
 *
 * - You can use extension functions to easily create an instance ex.
 * [createPermissionHandlerForSinglePermission], [createPermissionHandlerAndActOnlyIfAllGranted].
 *
 * # Usage
 *
 * - You MUST create the instance in the `fragment` or `activity` before `onCreate` method.
 *
 * - To start requesting permissions call [actOnAllPermissionsAcceptedOrRequestPermissions].
 */
class PermissionsHandler private constructor(
	lifecycle: Lifecycle,
	context: Context,
	host: Any,
	private val permissions: List<String>,
	listener: Listener,
) : DefaultLifecycleObserver {

	constructor(fragment: Fragment, lifecycle: Lifecycle, context: Context, permissions: List<String>, listener: Listener) : this(
		lifecycle, context, fragment, permissions, listener
	)

	constructor(activity: FragmentActivity, lifecycle: Lifecycle, context: Context, permissions: List<String>, listener: Listener) : this(
		lifecycle, context, activity, permissions, listener
	)

	private val weakRefLifecycle = WeakReference(lifecycle)
	private val weakRefContext = WeakReference(context)
	val weakRefHost = WeakReference(host)
	private val weakRefListener = WeakReference(listener)

	init {
		lifecycle.addObserver(this)
	}

	private val activityResultLauncherPermissions = host.registerForActivityResultFromAny(
		ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
		onActivityPermissionsLauncherResult(permissions)
	}

	private val activityResultPermissionsSystemSettings = host.registerForActivityResultFromAny(
		ActivityResultContracts.StartActivityForResult()
	) {
		checkOnPermissions(weakRefContext.get() ?: return@registerForActivityResultFromAny)
	}

	private fun onActivityPermissionsLauncherResult(permissions: Map<String, Boolean>) {
		MALogger.e("sadhiasudh onActivityPermissionsLauncherResult ${weakRefHost.get().getActivityOrNullFromAny() != null}")

		val activity = weakRefHost.get().getActivityOrNullFromAny()

		when {
			this.permissions.all { permissions[it] == true } -> {
				MALogger.e("sadhiasudh onActivityPermissionsLauncherResult 1 ${weakRefListener.get()}")
				weakRefListener.get()?.onAllPermissionsAccepted()
			}
			this.permissions.any { permissions[it] == true } -> {
				MALogger.e("sadhiasudh onActivityPermissionsLauncherResult 2")
				weakRefListener.get()?.onSubsetPermissionsAccepted(permissions)
			}
			activity != null -> {
				MALogger.e("sadhiasudh onActivityPermissionsLauncherResult 3")
				val rationaleList = this.permissions.filter {
					weakRefHost.get().shouldShowRequestPermissionRationaleFromAny(it)
				}

				if (rationaleList.isNotEmpty()) {
					weakRefListener.get()?.onShouldShowRationale(this, rationaleList)
				}else {
					weakRefListener.get()?.onDenyPermissions(this)
				}
			}
		}
	}

	fun actOnAllPermissionsAcceptedOrRequestPermissions() {
		val context = weakRefContext.get() ?: return
		MALogger.e("aaaaaaaaaa -> on all weakRefListener.get() ${weakRefListener.get()}")
		if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
			weakRefListener.get()?.onAllPermissionsAccepted()
		}else {
			activityResultLauncherPermissions.launchSafely(
				weakRefContext.get(),
				this.permissions.toTypedArray()
			)
		}
	}

	private fun checkOnPermissions(context: Context) {
		onActivityPermissionsLauncherResult(
			permissions.associateWith {
				ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
			}
		)
	}

	override fun onDestroy(owner: LifecycleOwner) {
		weakRefLifecycle.get()?.removeObserver(this)
	}

	interface Listener {

		fun onAllPermissionsAccepted()

		fun onSubsetPermissionsAccepted(permissions: Map<String, Boolean>) {
			MALogger.e("sadhiasudh on subset accepted")
		}

		/** @param list contains list of permissions which returns `true` to [shouldShowRequestPermissionRationaleFromAny] fun*/
		fun onShouldShowRationale(permissionsHandler: PermissionsHandler, @Size(min = 1) list: List<String>) {
			permissionsHandler.weakRefHost.get().getActivityOrNullFromAny()?.apply {
				showAlertDialog(
					getString(R.string.allow_permission),
					getString(R.string.to_use_this_feature_you_must_accept_this_permission),
					onDismissListener = {
						permissionsHandler.weakRefContext.get()?.also { context ->
							context.showError(context.getString(R.string.you_didn_t_accept_permission))
						}
					}
				) {
					permissionsHandler.activityResultLauncherPermissions.launchSafely(
						permissionsHandler.weakRefContext.get(),
						permissionsHandler.permissions.toTypedArray()
					)
				}
			}
		}

		fun onDenyPermissions(permissionsHandler: PermissionsHandler) {
			permissionsHandler.weakRefHost.get().getActivityOrNullFromAny()?.apply {
				showAlertDialog(
					getString(R.string.change_permission_in_settings_of_device),
					getString(R.string.to_use_this_feature_you_must_accept_this_permission),
					onDismissListener = {
						permissionsHandler.weakRefContext.get()?.also { context ->
							context.showError(context.getString(R.string.you_didn_t_accept_permission))
						}
					}
				) {
					val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
						it.data = Uri.fromParts("package", packageName, null)
					}

					permissionsHandler.activityResultPermissionsSystemSettings.launchSafely(
						permissionsHandler.weakRefContext.get(),
						intent
					)
				}
			}
		}

	}

}
