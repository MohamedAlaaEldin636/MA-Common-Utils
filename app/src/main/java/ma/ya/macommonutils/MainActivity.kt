package ma.ya.macommonutils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import ma.ya.core.media.createPickImagesOrVideoHandlerForSingleImageFromCamera
import ma.ya.macommonutils.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding

	private val pickerOfImageFromCamera = createPickImagesOrVideoHandlerForSingleImageFromCamera {
		Glide.with(this).load(it).into(binding.imageView)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
		binding.lifecycleOwner = this

		binding.materialButton.setOnClickListener {
			pickerOfImageFromCamera.requestImageOrVideo()
		}
	}

}