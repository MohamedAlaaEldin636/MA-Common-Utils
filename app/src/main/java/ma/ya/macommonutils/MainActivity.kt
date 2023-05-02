package ma.ya.macommonutils

import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import ma.ya.core.extensions.dismissToast
import ma.ya.core.extensions.toastLong
import ma.ya.core.helperClasses.MALogger
import ma.ya.core.location.LocationHandler
import ma.ya.core.media.createPickImagesOrVideoHandlerForSingleImageFromCamera
import ma.ya.core.media.createPickImagesOrVideoHandlerForSingleImageFromCameraOrGallery
import ma.ya.core.media.createPickImagesOrVideoHandlerForSingleImageFromGallery
import ma.ya.macommonutils.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity(), LocationHandler.Listener {

	override fun showLoading() {
		toastLong("LOADING...")
	}

	override fun hideLoading() {
		dismissToast()
	}

	override fun onCurrentLocationResultSuccess(location: Location) {
		//toastLong("lat ${location.latitude}, lng ${location.longitude}")
		MALogger.e("lat ${location.latitude}, lng ${location.longitude}")
	}

	override fun onCurrentLocationResultFailure(context: Context?, exception: Exception?) {
		toastLong("ERROR")

		MALogger.e("exception $exception")
	}

	private lateinit var binding: ActivityMainBinding

	private val pickerOfImageFromCamera = createPickImagesOrVideoHandlerForSingleImageFromCamera(::showImage)
	private val pickerOfImageFromGallery = createPickImagesOrVideoHandlerForSingleImageFromGallery(::showImage)
	private val pickerOfImageFromCameraOrGallery = createPickImagesOrVideoHandlerForSingleImageFromCameraOrGallery(
		getAnchor = { binding.singleImageMaterialButton }, ::showImage
	)

	lateinit var locationHandler: LocationHandler
		private set
	private val listenerForLocationHandler = object : LocationHandler.Listener {
		override fun showLoading() {
			toastLong("LOADING...")
		}

		override fun hideLoading() {
			dismissToast()
		}

		override fun onCurrentLocationResultSuccess(location: Location) {
			//toastLong("lat ${location.latitude}, lng ${location.longitude}")
			MALogger.e("lat ${location.latitude}, lng ${location.longitude}")
		}

		override fun onCurrentLocationResultFailure(context: Context?, exception: Exception?) {
			toastLong("ERROR")

			MALogger.e("exception $exception")
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		locationHandler = LocationHandler(
			this,
			lifecycle,
			this,
			listenerForLocationHandler
		)

		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
		binding.lifecycleOwner = this

		binding.getCurrentLocationMaterialButton.setOnClickListener {
			locationHandler.requestCurrentLocation(true)
		}
		binding.singleImageFromCameraMaterialButton.setOnClickListener {
			pickerOfImageFromCamera.requestImageOrVideo()
		}
		binding.singleImageFromGalleryMaterialButton.setOnClickListener {
			pickerOfImageFromGallery.requestImageOrVideo()
		}
		binding.singleImageMaterialButton.setOnClickListener {
			pickerOfImageFromCameraOrGallery.requestImageOrVideo()
		}
	}

	private fun showImage(uri: Uri) {
		Glide.with(this).load(uri).into(binding.imageView)
	}

}