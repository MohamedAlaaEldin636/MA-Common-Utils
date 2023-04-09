package ma.ya.core.permissions

/*import android.content.Context
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
import ma.ya.cometchatintegration.R
import ma.ya.cometchatintegration.extensions.*
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
		MyLogger.e("sadhiasudh on allll+ ${weakRefContext.get()}")
		onPermissionGranted()
	}

	override fun onSubsetPermissionsAccepted(permissions: Map<String, Boolean>) {
		MyLogger.e("sadhiasudh on subbbbbbbbb")
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
	ListenerOfPermissionsHandlerWhichActOnlyIfAllGranted(context, onPermissionGranted)
	*//*object : PermissionsHandler.Listener {
		override fun onAllPermissionsAccepted() {
			MyLogger.e("sadhiasudh on allll+")
			onPermissionGranted()
		}

		override fun onSubsetPermissionsAccepted(permissions: Map<String, Boolean>) {
			MyLogger.e("sadhiasudh on subbbbbbbbb")
			context?.showError(getString(R.string.not_all_permissions_are_accepted))
		}
	}*//*
)

abstract class ListenerOfPermissionsHandler : PermissionsHandler.Listener

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

	private val activityResultLauncherPermissions = host.registerForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
		onActivityPermissionsLauncherResult(permissions)
	}

	private val activityResultPermissionsSystemSettings = host.registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) {
		checkOnPermissions(weakRefContext.get() ?: return@registerForActivityResult)
	}

	private fun onActivityPermissionsLauncherResult(permissions: Map<String, Boolean>) {
		MyLogger.e("sadhiasudh onActivityPermissionsLauncherResult ${weakRefHost.get().getActivityOrNull() != null}")

		val activity = weakRefHost.get().getActivityOrNull()

		when {
			this.permissions.all { permissions[it] == true } -> {
				MyLogger.e("sadhiasudh onActivityPermissionsLauncherResult 1 ${weakRefListener.get()}")
				weakRefListener.get()?.onAllPermissionsAccepted()
			}
			this.permissions.any { permissions[it] == true } -> {
				MyLogger.e("sadhiasudh onActivityPermissionsLauncherResult 2")
				weakRefListener.get()?.onSubsetPermissionsAccepted(permissions)
			}
			activity != null -> {
				MyLogger.e("sadhiasudh onActivityPermissionsLauncherResult 3")
				val rationaleList = this.permissions.filter {
					weakRefHost.get().shouldShowRequestPermissionRationale(it)
				}

				if (rationaleList.isNotEmpty()) {
					weakRefListener.get()?.onShouldShowRationale(this, rationaleList)
				}else {
					weakRefListener.get()?.onDenyPermissions(this)
				}
			}
		}
	}

	fun requestPermissions() {
		MyLogger.e("sadhiasudh request permission in permissions handler with ${this.permissions}")
		activityResultLauncherPermissions.launchSafely(
			weakRefContext.get(),
			this.permissions.toTypedArray()
		)
	}

	fun actOnAllPermissionsAcceptedOrRequestPermissions() {
		val context = weakRefContext.get() ?: return
		MyLogger.e("aaaaaaaaaa -> on all weakRefListener.get() ${weakRefListener.get()}")
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
			MyLogger.e("sadhiasudh on subset accepted")
		}

		*//** @param list contains list of permissions which returns `true` to [shouldShowRequestPermissionRationale] fun *//*
		fun onShouldShowRationale(permissionsHandler: PermissionsHandler, @Size(min = 1) list: List<String>) {
			permissionsHandler.weakRefHost.get().getActivityOrNull()?.apply {
				showAlertDialog(
					getString(R.string.allow_location_permission),
					getString(R.string.this_app_need_allow_location),
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
			permissionsHandler.weakRefHost.get().getActivityOrNull()?.apply {
				showAlertDialog(
					getString(R.string.change_permission_in_settings_of_device),
					getString(R.string.this_app_need_allow_location),
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

}*/
