package cn.enjoytoday.chart

/**
 * @date 17-9-5.
 * @className PartModel
 * @serial 1.0.0
 *
 * 图表数据类
 */
class PartModel(var value:Float){


    constructor(value: Float,tagName:String,tagId:String):this(value){
        this.value=value
        this.tagName=tagName
        this.tagId=tagId
    }
    var tagId:String?=null
    var color:Int=-1

    /**
     * 饼图对应开始角度,柱状图对应bar左下角x轴坐标
     */
    var startAngle:Float=0f

    /**
     * 饼图对应扫过角度,柱状图对应bar的高度
     */
    var sweep:Float=0f

    var tagName:String?=null


}