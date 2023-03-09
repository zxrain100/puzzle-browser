package com.plb.bwsr

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue

object PLBUtils {

    fun dip2px(dp: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        PLB.context.resources.displayMetrics
    )

    fun dip2px(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        PLB.context.resources.displayMetrics
    ).toInt()


    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    fun getStatusBarH(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = runCatching { context.resources.getDimensionPixelSize(resourceId) }.getOrDefault(0)
        }

        if (result == 0) {
            result = PLBUtils.dip2px(24)
        }

        return result
    }

}