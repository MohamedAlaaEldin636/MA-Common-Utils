package ma.ya.macommonutils

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import ma.ya.core.media.createPickImagesOrVideoHandlerForSingleImageFromCamera
import ma.ya.core.media.createPickImagesOrVideoHandlerForSingleImageFromCameraOrGallery
import ma.ya.core.media.createPickImagesOrVideoHandlerForSingleImageFromGallery
import ma.ya.macommonutils.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding

	private val pickerOfImageFromCamera = createPickImagesOrVideoHandlerForSingleImageFromCamera(::showImage)
	private val pickerOfImageFromGallery = createPickImagesOrVideoHandlerForSingleImageFromGallery(::showImage)
	private val pickerOfImageFromCameraOrGallery = createPickImagesOrVideoHandlerForSingleImageFromCameraOrGallery(
		getAnchor = { binding.singleImageMaterialButton }, ::showImage
	)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
		binding.lifecycleOwner = this

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