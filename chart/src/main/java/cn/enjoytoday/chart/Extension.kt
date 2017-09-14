package cn.enjoytoday.chart

import android.content.Context
import android.view.View

/**
 * @date 17-9-14.
 * @className Extension
 * @serial 1.0.0
 */

/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 */
fun View.dip2px(context: Context, dpValue: Float): Float {
    val scale = context.resources.displayMetrics.density
    return dpValue * scale + 0.5f
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
fun View.px2dip(context: Context, pxValue: Float): Float {
    val scale = context.resources.displayMetrics.density
    return pxValue / scale + 0.5f
}