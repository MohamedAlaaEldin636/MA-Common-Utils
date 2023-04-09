package ma.ya.core.extensions

import android.content.Intent

fun Intent.createChooserMA(title: CharSequence): Intent = Intent.createChooser(this, title)
