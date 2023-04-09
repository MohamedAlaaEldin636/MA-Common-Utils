package ma.ya.core.extensions

fun <E> List<E>?.orIfNullOrEmpty(fallback: () -> List<E>): List<E> {
	return if (isNullOrEmpty()) fallback() else this
}
