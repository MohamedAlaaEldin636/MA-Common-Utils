package ma.ya.core.helperClasses

/**
 * - MUST use [init].
 */
object MACoreInitializer {

	internal var fileProviderAndroidManifestXmlAuthority = ""

	/**
	 * - MUST be called only on App startup & only once.
	 */
	fun init(
		fileProviderAndroidManifestXmlAuthority: String
	) {
		this.fileProviderAndroidManifestXmlAuthority = fileProviderAndroidManifestXmlAuthority
	}

}
