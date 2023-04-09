package ma.ya.core.extensions

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import ma.ya.core.helperClasses.MALogger

fun Uri.checkLengthOfVideo(context: Context, maxDurationInSeconds: Int): Boolean {
	MALogger.e("deiwu -> pre")

	kotlin.runCatching {
		val retriever = MediaMetadataRetriever()
		// use one of overloaded setDataSource() functions to set your data source
		retriever.setDataSource(context, this)
		val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
		val timeInMilliSeconds = time?.toLongOrNull()
		val durationTimeInSecond = timeInMilliSeconds?.let { it / 1000 }

		MALogger.e("deiwu -> $durationTimeInSecond $timeInMilliSeconds")

		if (durationTimeInSecond != null && durationTimeInSecond > maxDurationInSeconds) {
			return false
		}
	}.getOrElse {
		MALogger.e("ERROR dhsaiuhsa -> $it")

		return false
	}

	/* for Size
	https://stackoverflow.com/a/63529377

	ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(imageUri, "r");
	fileLength = pfd.getStatSize();
	pfd.close();
	 */
	// todo -> https://stackoverflow.com/questions/49415012/get-file-size-using-uri-in-android
	//val fileSize = Integer.parseInt(String.valueOf((volleyFileObject.getFile().length() / 1024) / 1024));
	// https://stackoverflow.com/a/67251625

	return true
}
