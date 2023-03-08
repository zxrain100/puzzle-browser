package com.plb.bwsr

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


}