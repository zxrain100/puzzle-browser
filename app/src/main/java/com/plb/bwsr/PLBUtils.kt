package com.plb.bwsr

import android.app.Activity
import android.util.TypedValue

object PLBUtils {



    fun px2dip(px: Float): Float = px / PLB.context.resources.displayMetrics.density + 0.5f

    fun px2dip(px: Int): Int = px2dip(px.toFloat()).toInt()

    fun dip2px(dp: Float): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            PLB.context.resources.displayMetrics
        ) //dp * resources.displayMetrics.density + 0.5f

    fun dip2px(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            PLB.context.resources.displayMetrics
        ).toInt() //dip2px(dp.toFloat()).toInt()

    fun sp2px(sp: Float): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            PLB.context.resources.displayMetrics
        ) //sp * resources.displayMetrics.density

    fun sp2px(sp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            PLB.context.resources.displayMetrics
        ).toInt() // sp2px(sp.toFloat()).toInt()


    fun isFinished(activity: Activity): Boolean = activity.isFinishing || activity.isDestroyed

}