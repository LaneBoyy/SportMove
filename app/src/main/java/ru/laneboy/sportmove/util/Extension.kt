package ru.laneboy.sportmove.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import ru.laneboy.sportmove.databinding.ProgressBarFullScreenBinding

fun initProgressBar(layoutInflater: LayoutInflater, context: Context): AlertDialog {
    return AlertDialog.Builder(context).apply {
        setView(ProgressBarFullScreenBinding.inflate(layoutInflater).root)
        setCancelable(false)
    }.create().apply {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}


fun Fragment.showToast(message: String?) =
    Toast.makeText(this.requireActivity(), message ?: "Unknown error", Toast.LENGTH_SHORT).show()


fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun ViewBinding.getString(@StringRes id:Int) = this.root.context.getString(id)

@Px
fun View.dpToPx(@Dimension(unit = Dimension.DP) dp: Int) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
        .toInt()
@Px
fun Fragment.dpToPx(@Dimension(unit = Dimension.DP) dp: Int) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
        .toInt()