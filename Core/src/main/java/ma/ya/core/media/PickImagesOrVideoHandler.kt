@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ma.ya.core.media

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ma.ya.core.R
import ma.ya.core.extensions.*
import ma.ya.core.helperClasses.MACoreInitializer
import ma.ya.core.helperClasses.MALogger
import ma.ya.core.media.PickImagesOrVideoHandler.SourceOfData
import ma.ya.core.media.PickImagesOrVideoHandler.SupportedMediaType
import ma.ya.core.permissions.PermissionsHandler
import java.io.File

fun FragmentActivity.createPickImagesOrVideoHandlerForSingleImageFromCamera(
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.CAMERA,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun FragmentActivity.createPickImagesOrVideoHandlerForSingleImageFromGallery(
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.GALLERY,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun FragmentActivity.createPickImagesOrVideoHandlerForSingleImageFromCameraOrGallery(
	getAnchor: () -> View,
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.BOTH,
	getAnchor = { getAnchor() },
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun FragmentActivity.createPickImagesOrVideoHandlerForMultiImageFromGallery(
	onReceive: (uris: List<Uri>) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.GALLERY,
	requestMultipleImages = true,
	onReceive = { uris, _, _ ->
		onReceive(uris)
	}
)
fun FragmentActivity.createPickImagesOrVideoHandlerForMultiImageFromEitherCameraOrGallery(
	getAnchor: (tag: Bundle) -> View?,
	onReceive: (uris: List<Uri>, fromCamera: Boolean) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.BOTH,
	requestMultipleImages = true,
	getAnchor = getAnchor,
	onReceive = { uris, fromCamera, _ ->
		onReceive(uris, fromCamera)
	}
)
fun FragmentActivity.createPickImagesOrVideoHandlerForVideoFromCamera(
	maxVideoLengthInSeconds: Int = 5 * 60,
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.VIDEO,
	SourceOfData.CAMERA,
	maxVideoLengthInSeconds = maxVideoLengthInSeconds,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun FragmentActivity.createPickImagesOrVideoHandlerForVideoFromGallery(
	maxVideoLengthInSeconds: Int = 5 * 60,
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.VIDEO,
	SourceOfData.GALLERY,
	maxVideoLengthInSeconds = maxVideoLengthInSeconds,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun FragmentActivity.createPickImagesOrVideoHandlerForVideoFromEitherCameraOrGallery(
	maxVideoLengthInSeconds: Int = 5 * 60,
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.VIDEO,
	SourceOfData.BOTH,
	maxVideoLengthInSeconds = maxVideoLengthInSeconds,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun FragmentActivity.createPickImagesOrVideoHandler(
	supportedMediaType: SupportedMediaType,
	sourceOfData: SourceOfData = SourceOfData.BOTH,
	maxVideoLengthInSeconds: Int = 5 * 60,
	requestMultipleImages: Boolean = false,
	getAnchor: (tag: Bundle) -> View? = { null },
	onReceive: (uris: List<Uri>, fromCamera: Boolean, isImageNotVideo: Boolean) -> Unit,
) = PickImagesOrVideoHandler(
	this,
	supportedMediaType,
	sourceOfData,
	maxVideoLengthInSeconds,
	requestMultipleImages,
	getAnchor,
	onReceive
)

fun Fragment.createPickImagesOrVideoHandlerForSingleImageFromEitherCameraOrGallery (
	getAnchor: (tag: Bundle) -> View?,
	onReceive: (uri: Uri, fromCamera: Boolean) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.BOTH,
	requestMultipleImages = false,
	getAnchor = getAnchor,
	onReceive = { uris, fromCamera, _ ->
		uris.firstOrNull()?.also { onReceive(it, fromCamera) }
	}
)
fun Fragment.createPickImagesOrVideoHandlerForMultiImageFromEitherCameraOrGallery(
	getAnchor: (tag: Bundle) -> View?,
	onReceive: (uris: List<Uri>, fromCamera: Boolean) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.BOTH,
	requestMultipleImages = true,
	getAnchor = getAnchor,
	onReceive = { uris, fromCamera, _ ->
		onReceive(uris, fromCamera)
	}
)
fun Fragment.createPickImagesOrVideoHandlerForSingleImageFromCamera(
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.CAMERA,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun Fragment.createPickImagesOrVideoHandlerForMultiImageFromGallery(
	onReceive: (uris: List<Uri>) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.GALLERY,
	requestMultipleImages = true,
	onReceive = { uris, _, _ ->
		onReceive(uris)
	}
)
fun Fragment.createPickImagesOrVideoHandlerForSingleImageFromGallery(
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.IMAGE,
	SourceOfData.GALLERY,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun Fragment.createPickImagesOrVideoHandlerForVideoFromCamera(
	maxVideoLengthInSeconds: Int = 5 * 60,
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.VIDEO,
	SourceOfData.CAMERA,
	maxVideoLengthInSeconds = maxVideoLengthInSeconds,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun Fragment.createPickImagesOrVideoHandlerForVideoFromGallery(
	maxVideoLengthInSeconds: Int = 5 * 60,
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.VIDEO,
	SourceOfData.GALLERY,
	maxVideoLengthInSeconds = maxVideoLengthInSeconds,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun Fragment.createPickImagesOrVideoHandlerForVideoFromEitherCameraOrGallery(
	maxVideoLengthInSeconds: Int = 5 * 60,
	onReceive: (uri: Uri) -> Unit
) = PickImagesOrVideoHandler(
	this,
	SupportedMediaType.VIDEO,
	SourceOfData.BOTH,
	maxVideoLengthInSeconds = maxVideoLengthInSeconds,
	requestMultipleImages = false,
	onReceive = { uris, _, _ ->
		uris.firstOrNull()?.also { onReceive(it) }
	}
)
fun Fragment.createPickImagesOrVideoHandler(
	supportedMediaType: SupportedMediaType,
	sourceOfData: SourceOfData = SourceOfData.BOTH,
	maxVideoLengthInSeconds: Int = 5 * 60,
	requestMultipleImages: Boolean = false,
	getAnchor: (tag: Bundle) -> View? = { null },
	onReceive: (uris: List<Uri>, fromCamera: Boolean, isImageNotVideo: Boolean) -> Unit,
) = PickImagesOrVideoHandler(
	this,
	supportedMediaType,
	sourceOfData,
	maxVideoLengthInSeconds,
	requestMultipleImages,
	getAnchor,
	onReceive
)

/**
 * # Info
 *
 * - Used to handle appropriate permissions required to get image and/or video from current Android
 * API version.
 *
 * - Shows popup on view provided in case wants to get image and/or video from several options.
 *
 * - Can get from camera or from gallery and can get image or video or all of these things.
 *
 * # Usage
 *
 * - You MUST create the instance in the `fragment` or `activity` before `onCreate` method.
 *
 * - Use [requestImageOrVideo] and permissions will be auto handled then you can use constructor
 * params to do whatever you want.
 */
class PickImagesOrVideoHandler(
	eitherFragmentOrFragmentActivity: Any,
	private val supportedMediaType: SupportedMediaType,
	private val sourceOfData: SourceOfData = SourceOfData.BOTH,
	/** Ex. 3 minutes -> 3 * 60 */
	private val maxVideoLengthInSeconds: Int = 5 * 60,
	private val requestMultipleImages: Boolean = false,
	/**
	 * - MUST be used IF will have more than 1 option
	 *
	 * - Ex. where it is NOT needed [SupportedMediaType.IMAGE] && [SourceOfData.CAMERA].
	 */
	private val getAnchor: (tag: Bundle) -> View? = { null },
	private val onReceive: (uris: List<Uri>, fromCamera: Boolean, isImageNotVideo: Boolean) -> Unit,
) : PermissionsHandler.Listener {

	private var imageUri: Uri? = null

	private val galleryPermissions = buildList {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			when (supportedMediaType) {
				SupportedMediaType.IMAGE -> add(Manifest.permission.READ_MEDIA_IMAGES)
				SupportedMediaType.VIDEO -> add(Manifest.permission.READ_MEDIA_VIDEO)
				SupportedMediaType.BOTH -> {
					add(Manifest.permission.READ_MEDIA_IMAGES)
					add(Manifest.permission.READ_MEDIA_VIDEO)
				}
			}
		}else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
			add(Manifest.permission.READ_EXTERNAL_STORAGE)
		}
	}
	private val cameraPermissions = listOf(Manifest.permission.CAMERA)

	private val handler = when (eitherFragmentOrFragmentActivity) {
		is Fragment -> PermissionsHandler(
			eitherFragmentOrFragmentActivity,
			eitherFragmentOrFragmentActivity.lifecycle,
			eitherFragmentOrFragmentActivity.requireContext(),
			when (sourceOfData) {
				SourceOfData.GALLERY -> galleryPermissions
				SourceOfData.CAMERA -> cameraPermissions
				SourceOfData.BOTH -> galleryPermissions + cameraPermissions
			},
			this
		)
		is FragmentActivity -> PermissionsHandler(
			eitherFragmentOrFragmentActivity,
			eitherFragmentOrFragmentActivity.lifecycle,
			eitherFragmentOrFragmentActivity,
			when (sourceOfData) {
				SourceOfData.GALLERY -> galleryPermissions
				SourceOfData.CAMERA -> cameraPermissions
				SourceOfData.BOTH -> galleryPermissions + cameraPermissions
			},
			this
		)
		else -> throw RuntimeException("You must either provider Fragment or FragmentActivity")
	}

	var tag: Bundle = Bundle.EMPTY
		private set

	private val activityResultVideoCamera = if (supportedMediaType == SupportedMediaType.IMAGE) null else eitherFragmentOrFragmentActivity.registerForActivityResultFromAny(
		ActivityResultContracts.StartActivityForResult()
	) {
		if (it.resultCode == Activity.RESULT_OK) {
			val uri = it.data?.data ?: return@registerForActivityResultFromAny

			val context = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return@registerForActivityResultFromAny

			if (!uri.checkLengthOfVideo(context, maxVideoLengthInSeconds)) {
				context.showError(context.getString(R.string.max_length_of_video_exceeded))

				return@registerForActivityResultFromAny
			}

			onReceive(listOf(uri), true, false)
		}
	}

	private val activityResultVideoGallery = if (supportedMediaType == SupportedMediaType.IMAGE) null else eitherFragmentOrFragmentActivity.registerForActivityResultFromAny(
		ActivityResultContracts.StartActivityForResult()
	) {
		if (it.resultCode == Activity.RESULT_OK) {
			val uri = it.data?.data ?: return@registerForActivityResultFromAny

			val context = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return@registerForActivityResultFromAny

			if (!uri.checkLengthOfVideo(context, maxVideoLengthInSeconds)) {
				context.showError(context.getString(R.string.max_length_of_video_exceeded))

				return@registerForActivityResultFromAny
			}

			onReceive(listOf(uri), false, false)
		}
	}

	private val activityResultImageCameraFile = if (supportedMediaType == SupportedMediaType.VIDEO) null else eitherFragmentOrFragmentActivity.registerForActivityResultFromAny(
		ActivityResultContracts.TakePicture()
	) { result ->
		if (result != null && result) {
			imageUri?.also { imageUri ->
				onReceive(listOf(imageUri), true, true)
			}
		}
	}

	private val activityResultImageGallery = if (supportedMediaType == SupportedMediaType.VIDEO) null else eitherFragmentOrFragmentActivity.registerForActivityResultFromAny(
		ActivityResultContracts.StartActivityForResult()
	) {
		MALogger.e("on result -> it.resultCode == Activity.RESULT_OK ${it.resultCode} ${Activity.RESULT_OK}")
		if (it.resultCode == Activity.RESULT_OK) {
			val list = if (requestMultipleImages) {
				it.data?.clipData?.let { clipData ->
					List(clipData.itemCount) { index ->
						clipData.getItemAt(index).uri
					}.filterNotNull()
				}.orIfNullOrEmpty {
					listOfNotNull(it.data?.data)
				}
			}else {
				listOfNotNull(it.data?.data)
			}

			onReceive(list, false, true)
		}
	}

	fun requestImageOrVideo(tag: Bundle = Bundle.EMPTY) {
		this.tag = tag

		handler.actOnAllPermissionsAcceptedOrRequestPermissions()
	}

	private fun pickImageFromCamera() {
		val activity = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return

		activityResultImageCameraFile?.launchSafely(
			activity,
			createImageUri(activity) ?: return
		)
	}

	private fun pickImageFromGallery() {
		val activity = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return

		if (requestMultipleImages) {
			val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
				it.type = "image/*"
				it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
			}

			activityResultImageGallery?.launchSafely(
				activity,
				intent.createChooserMA(activity.getString(R.string.pick_image))
			)
		}else {
			val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

			activityResultImageGallery?.launchSafely(
				activity,
				intent.createChooserMA(activity.getString(R.string.pick_image))
			)
		}
	}

	private fun showPickerForImageOrVideoFromGallery() {
		val activity = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return

		val galleryImage = "${activity.getString(R.string.gallery)} (${activity.getString(R.string.image)})"
		val galleryVideo = "${activity.getString(R.string.gallery)} (${activity.getString(R.string.video)})"

		getAnchor(tag)?.showPopup(
			listOf(
				galleryImage, galleryVideo
			)
		) {
			when (it.title.toStringOrEmpty()) {
				galleryImage -> pickImageFromGallery()
				galleryVideo -> pickVideoFromGallery()
			}
		}
	}
	private fun showPickerForImageOrVideoFromCamera() {
		val activity = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return

		val cameraImage = "${activity.getString(R.string.camera)} (${activity.getString(R.string.image)})"
		val cameraVideo = "${activity.getString(R.string.camera)} (${activity.getString(R.string.video)})"

		getAnchor(tag)?.showPopup(
			listOf(
				cameraImage, cameraVideo
			)
		) {
			when (it.title.toStringOrEmpty()) {
				cameraImage -> pickImageFromCamera()
				cameraVideo -> pickVideoFromCamera()
			}
		}
	}
	private fun showPickerForVideoFromCameraOrGallery() {
		val activity = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return

		val videoCamera = "${activity.getString(R.string.video)} (${activity.getString(R.string.camera)})"
		val videoGallery = "${activity.getString(R.string.video)} (${activity.getString(R.string.gallery)})"

		getAnchor(tag)?.showPopup(
			listOf(
				videoCamera, videoGallery
			)
		) {
			when (it.title.toStringOrEmpty()) {
				videoCamera -> pickVideoFromCamera()
				videoGallery -> pickVideoFromGallery()
			}
		}
	}
	private fun showPickerForImageFromCameraOrGallery() {
		val activity = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return

		val imageCamera = "${activity.getString(R.string.image)} (${activity.getString(R.string.camera)})"
		val imageGallery = "${activity.getString(R.string.image)} (${activity.getString(R.string.gallery)})"

		getAnchor(tag)?.showPopup(
			listOf(
				imageCamera, imageGallery
			)
		) {
			when (it.title.toStringOrEmpty()) {
				imageCamera -> pickImageFromCamera()
				imageGallery -> pickImageFromGallery()
			}
		}
	}
	private fun showPickerForImageOrVideoFromCameraOrGallery() {
		val activity = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return

		val imageCamera = "${activity.getString(R.string.image)} (${activity.getString(R.string.camera)})"
		val imageGallery = "${activity.getString(R.string.image)} (${activity.getString(R.string.gallery)})"

		val videoCamera = "${activity.getString(R.string.video)} (${activity.getString(R.string.camera)})"
		val videoGallery = "${activity.getString(R.string.video)} (${activity.getString(R.string.gallery)})"

		getAnchor(tag)?.showPopup(
			listOf(
				imageCamera, imageGallery, videoCamera, videoGallery
			)
		) {
			when (val title = it.title.toStringOrEmpty()) {
				imageCamera, imageGallery -> pickImage(title == imageCamera)
				videoCamera, videoGallery -> pickVideo(title == videoCamera)
			}
		}
	}

	override fun onAllPermissionsAccepted() {
		when (sourceOfData) {
			SourceOfData.GALLERY -> when (supportedMediaType) {
				SupportedMediaType.VIDEO -> pickVideoFromGallery()
				SupportedMediaType.IMAGE -> pickImageFromGallery()
				SupportedMediaType.BOTH -> showPickerForImageOrVideoFromGallery()
			}
			SourceOfData.CAMERA -> when (supportedMediaType) {
				SupportedMediaType.VIDEO -> pickVideoFromCamera()
				SupportedMediaType.IMAGE -> pickImageFromCamera()
				SupportedMediaType.BOTH -> showPickerForImageOrVideoFromCamera()
			}
			SourceOfData.BOTH -> when (supportedMediaType) {
				SupportedMediaType.VIDEO -> showPickerForVideoFromCameraOrGallery()
				SupportedMediaType.IMAGE -> showPickerForImageFromCameraOrGallery()
				SupportedMediaType.BOTH -> showPickerForImageOrVideoFromCameraOrGallery()
			}
		}
	}

	private fun pickImage(fromCamera: Boolean) {
		if (fromCamera) {
			pickImageFromCamera()
		}else {
			pickImageFromGallery()
		}
	}

	private fun pickVideo(fromCamera: Boolean) {
		if (fromCamera) {
			pickVideoFromCamera()
		}else {
			pickVideoFromGallery()
		}
	}

	private fun pickVideoFromCamera() {
		activityResultVideoCamera?.launchSafely(
			null,
			Intent(MediaStore.ACTION_VIDEO_CAPTURE).also {
				it.putExtra(MediaStore.EXTRA_DURATION_LIMIT,maxVideoLengthInSeconds)
			}
		)
	}

	private fun pickVideoFromGallery() {
		activityResultVideoGallery?.launchSafely(null, Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI))
	}

	private fun pickCamera() {
		when (supportedMediaType) {
			SupportedMediaType.VIDEO -> pickVideoFromCamera()
			SupportedMediaType.IMAGE -> pickImageFromCamera()
			SupportedMediaType.BOTH -> {
				val activity = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return

				val imageString = activity.getString(R.string.image)
				val videoString = activity.getString(R.string.video)

				getAnchor(tag)?.showPopup(
					listOf(imageString, videoString),
					onMenuItemClickListener = {
						when (it.title.toStringOrEmpty()) {
							imageString -> pickImageFromCamera()
							videoString -> pickVideoFromCamera()
						}
					}
				)
			}
		}
	}

	private fun pickGallery() {
		when (supportedMediaType) {
			SupportedMediaType.VIDEO -> pickVideoFromGallery()
			SupportedMediaType.IMAGE -> pickImageFromGallery()
			SupportedMediaType.BOTH -> {
				val activity = handler.weakRefHost.get()?.getActivityOrNullFromAny() ?: return

				val imageString = activity.getString(R.string.image)
				val videoString = activity.getString(R.string.video)

				getAnchor(tag)?.showPopup(
					listOf(imageString, videoString),
					onMenuItemClickListener = {
						when (it.title.toStringOrEmpty()) {
							imageString -> pickImageFromGallery()
							videoString -> pickVideoFromGallery()
						}
					}
				)
			}
		}
	}

	private fun createImageUri(context: Context): Uri? {
		val fileCameraCapture = File(
			context.applicationContext.filesDir,
			"camera_photo_${System.currentTimeMillis()}.png"
		)

		imageUri = context.applicationContext.let {
			FileProvider.getUriForFile(
				it,
				MACoreInitializer.fileProviderAndroidManifestXmlAuthority,
				fileCameraCapture
			)
		}

		return imageUri
	}

	override fun onSubsetPermissionsAccepted(permissions: Map<String, Boolean>) {
		MALogger.e("aaaaaaaaaa -> on subset $permissions")

		if (permissions[Manifest.permission.CAMERA] == true) {
			pickCamera()
		}else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
			(permissions[Manifest.permission.READ_MEDIA_IMAGES] == true || permissions[Manifest.permission.READ_MEDIA_VIDEO] == true)) {
			pickGallery()
		}else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
			permissions[Manifest.permission.READ_MEDIA_IMAGES] == true) {
			pickGallery()
		}
	}

	enum class SupportedMediaType {
		VIDEO, IMAGE, BOTH
	}

	enum class SourceOfData {
		GALLERY, CAMERA, BOTH
	}

}
