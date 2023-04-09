package ma.ya.core.extensions

fun Any?.toStringOrEmpty() = this?.toString() ?: ""

fun String.minLengthZerosPrefix(requiredLength: Int): String {
	return if (length < requiredLength) {
		"${"0".repeat(requiredLength - length)}$this"
	}else {
		this
	}
}
