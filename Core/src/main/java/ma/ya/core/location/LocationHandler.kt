@file:Suppress("unused")

package ma.ya.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import ma.ya.core.R
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import ma.ya.core.extensions.launchSafely
import ma.ya.core.extensions.showAlertDialog
import ma.ya.core.extensions.showError
import ma.ya.core.helperClasses.MALogger
import ma.ya.core.permissions.PermissionsHandler
import ma.ya.core.permissions.createPermissionHandlerAndActOnlyIfAllGranted
import java.lang.Exception
import java.lang.ref.WeakReference

class LocationHandler private constructor(
    lifecycle: Lifecycle,
    context: Context,
    host: Any,
    listener: Listener,
) : DefaultLifecycleObserver, PermissionsHandler.Listener {

    constructor(fragment: Fragment, lifecycle: Lifecycle, context: Context, listener: Listener) : this(
        lifecycle, context, fragment, listener
    )

    constructor(activity: FragmentActivity, lifecycle: Lifecycle, context: Context, listener: Listener) : this(
        lifecycle, context, activity, listener
    )

    companion object {
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = 10_000L
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2L
    }

    private val weakRefLifecycle = WeakReference(lifecycle)
    private val weakRefContext = WeakReference(context)
    private val weakRefHost = WeakReference(host)
    private val weakRefListener = WeakReference(listener)

    init {
        lifecycle.addObserver(this)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private var requestCurrentLocationNotPeriodicLocation = false

    private var showProgressForCurrentLocation = false

    override fun onAllPermissionsAccepted() {
        checkGPSForLocation()
    }

    private val activityResultPermissionLocationRequest = when (host) {
        is Fragment -> PermissionsHandler(
            host,
            host.lifecycle,
            host.requireContext(),
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            this
        )
        is AppCompatActivity -> PermissionsHandler(
            host,
            host.lifecycle,
            host,
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            this
        )
        else -> throw RuntimeException("Unexpected host")
    }

    private val activityResultLocationSystemSettings = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        MALogger.e("it?.resultCode == Activity.RESULT_OK ${it?.resultCode == Activity.RESULT_OK}")

        if (it?.resultCode == Activity.RESULT_OK) {
            onGPSSuccess()
        }else {
            onGPSFailure(null)
        }
    }

    private val activityResultPermissionsSystemSettings = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkOnPermissions(weakRefContext.get() ?: return@registerForActivityResult)
    }

    override fun onCreate(owner: LifecycleOwner) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(
            weakRefContext.get() ?: return
        )

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_IN_MILLISECONDS).apply {
            this.setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.also { location ->
                    weakRefListener.get()?.onChangeLocationSuccess(location)
                }
            }
        }
    }

    fun requestCurrentLocation(showProgress: Boolean) {
        val context = weakRefContext.get() ?: return

        if (showProgress) {
            this.showProgressForCurrentLocation = true
        }

        requestCurrentLocationNotPeriodicLocation = true

        MALogger.e("LocationHandler -> on request current permission")

        checkOnPermissions(context)
    }

    fun requestLocationUpdates() {
        val context = weakRefContext.get() ?: return

        requestCurrentLocationNotPeriodicLocation = false

        checkOnPermissions(context)
    }

    fun stopLocationUpdates() {
        kotlin.runCatching {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkOnPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MALogger.e("LocationHandler -> pre check gps for location")

            checkGPSForLocation()
        }else {
            MALogger.e("LocationHandler -> pre request permissions")

            activityResultPermissionLocationRequest.actOnAllPermissionsAcceptedOrRequestPermissions()
        }
    }

    private fun onRequestCurrentLocationFailure(exception: Exception?) {
        onRequestCurrentLocationCompleted(null)

        weakRefListener.get()?.onCurrentLocationResultFailure(weakRefContext.get(), exception)
    }

    /** @param location not-null in case of success isa. */
    private fun onRequestCurrentLocationCompleted(location: Location?) {
        if (showProgressForCurrentLocation) {
		        weakRefListener.get()?.hideLoading()

            showProgressForCurrentLocation = false
        }

        MALogger.e("LocationHandler -> pre calling interface on success where location $location")

        if (location != null) {
            weakRefListener.get()?.onCurrentLocationResultSuccess(location)
        }
    }

    private fun onGPSFailure(exception: Exception?) {
        if (requestCurrentLocationNotPeriodicLocation) {
            onRequestCurrentLocationFailure(exception)
        }else {
            weakRefListener.get()?.onChangeLocationFailure(weakRefContext.get(), exception)
        }
    }

    @SuppressLint("MissingPermission")
    private fun onGPSSuccess() {
        if (requestCurrentLocationNotPeriodicLocation) {
            val cancellationToken = object : CancellationToken() {
                override fun onCanceledRequested(listener: OnTokenCanceledListener): CancellationToken = this

                override fun isCancellationRequested(): Boolean = false
            }

            kotlin.runCatching {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken
                ).addOnSuccessListener currentLocationAddOnSuccessListener@ { location: Location? ->
                    if (location == null) {
                        onRequestCurrentLocationFailure(null)

                        return@currentLocationAddOnSuccessListener
                    }

                    MALogger.e("LocationHandler -> onGPSSuccess success pre completed")

                    onRequestCurrentLocationCompleted(location)
                }.addOnFailureListener {
                    MALogger.e("LocationHandler -> onGPSSuccess error $it pre failure")

                    onRequestCurrentLocationFailure(it)
                }
            }
        }else {
            kotlin.runCatching {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper() ?: Looper.getMainLooper()
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkGPSForLocation() {
        val context = weakRefContext.get() ?: return

        if (this.showProgressForCurrentLocation) {
            weakRefListener.get()?.showLoading()
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(context)

        client.checkLocationSettings(builder.build()).addOnSuccessListener {
            MALogger.e("LocationHandler -> on check gsp success pre onGPS success")

            onGPSSuccess()
        }.addOnFailureListener { exception ->
            MALogger.e("LocationHandler -> on check gsp success failure $exception")

            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    //exception.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                    activityResultLocationSystemSettings.launchSafely(
                        weakRefContext.get(),
                        IntentSenderRequest.Builder(exception.resolution.intentSender).build()
                    )
                    MALogger.e("LocationHandler -> started activity to resolve issue of gps")
                }catch (sendEx: IntentSender.SendIntentException) {
                    MALogger.e("LocationHandler -> error in starting activity to resolve issue of gps")
                    onGPSFailure(sendEx)
                }
            }else {
                onGPSFailure(exception)
            }
        }
    }

    private fun shouldShowRequestPermissionRationale(permission: String): Boolean = when (val host = weakRefHost.get()) {
        is Fragment -> host.shouldShowRequestPermissionRationale(permission)
        is AppCompatActivity -> ActivityCompat.shouldShowRequestPermissionRationale(host, permission)
        else -> false
    }

    private fun getActivityOrNull() = when (val value = weakRefHost.get()) {
        is Fragment -> value.activity
        is AppCompatActivity -> value
        else -> null
    }

    private fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncher<I> {
        return when (val host = weakRefHost.get()) {
            is Fragment -> {
                host.registerForActivityResult(contract, callback)
            }
            is AppCompatActivity -> {
                host.registerForActivityResult(contract, callback)
            }
            else -> throw RuntimeException("Unexpected host $host")
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        weakRefLifecycle.get()?.removeObserver(this)
    }

    interface Listener {

	      fun showLoading()

	      fun hideLoading()

        fun onCurrentLocationResultSuccess(location: Location) {}

        fun onCurrentLocationResultFailure(context: Context?, exception: Exception?) {
            context?.showError(context.getString(R.string.something_went_wrong_in_getting_current_location))

            MALogger.e("error in get CURRENT location $exception")
        }

        fun onChangeLocationSuccess(location: Location) {}

        fun onChangeLocationFailure(context: Context?, exception: Exception?) {
            context?.showError(context.getString(R.string.something_went_wrong_while_getting_location))

            MALogger.e("error in get PERIODIC location $exception")
        }

        fun onDenyLocationPermissions(locationHandler: LocationHandler, context: Context?) {
            locationHandler.getActivityOrNull()?.apply {
                showAlertDialog(
                    getString(R.string.change_permission_in_settings_of_device),
                    getString(R.string.to_use_this_feature_you_must_accept_this_permission),
                    onDismissListener = {
                        context?.showError(context.getString(R.string.you_didn_t_accept_permission))
                    }
                ) { dialog ->
                    dialog.dismiss()

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                        it.data = Uri.fromParts("package", packageName, null)
                    }

                    locationHandler.activityResultPermissionsSystemSettings.launchSafely(
                        locationHandler.weakRefContext.get(),
                        intent
                    )
                }
            }
        }

    }

}
