package cn.enjoytoday.chart


/**
 * @date 17-9-5.
 * @className OnSelectedListener
 * @serial 1.0.0
 * 图表选中回调
 */
interface OnSelectedListener{

    /**
     * 被选中的model内容
     */
    fun onSelectedListener(index:Int,partModel: PartModel)
}