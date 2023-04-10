package ma.ya.macommonutils

import android.app.Application
import ma.ya.core.helperClasses.MACoreInitializer

class MyApplication : Application() {

	override fun onCreate() {
		super.onCreate()

		MACoreInitializer.init(BuildConfig.fileProviderAndroidManifestXmlAuthority)
	}

}
