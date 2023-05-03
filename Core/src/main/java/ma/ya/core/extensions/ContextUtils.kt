package ma.ya.core.extensions

import android.content.Context
import android.view.LayoutInflater

val Context.layoutInflater: LayoutInflater
	get() = LayoutInflater.from(this)
