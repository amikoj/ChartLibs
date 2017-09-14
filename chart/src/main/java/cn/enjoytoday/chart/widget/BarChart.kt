/**
 *
 */
package cn.enjoytoday.chart.widget

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import cn.enjoytoday.chart.dip2px
import cn.enjoytoday.chart.PartModel
import cn.enjoytoday.chart.OnSelectedListener
import cn.enjoytoday.chart.log

/**
 *
 *
 * @date 17-9-5.
 * @className BarChart
 * @serial 1.0.0
 * @author hfcai
 */
class BarChart(context: Context, attributeset: AttributeSet?, defStyleAttr:Int): View(context,attributeset,defStyleAttr){
    init {
        requestFocus()
        isClickable=true
    }

    constructor(context: Context, attributeset: AttributeSet):this(context,attributeset,0)

    constructor(context: Context):this(context,null,0)






    private var listBar:MutableList<PartModel> = mutableListOf()


    var width=0f
    var height=0f

    private var padding=dip2px(context,8f)

    /**
     * bar的宽度
     */
    private var unitWidth=0f


    /**
     * 正常状况下的坐标字体颜色
     */
    private var normalCooridnateColor= Color.GRAY


    /**
     * 选中情况下的坐标字体颜色
     */
    private var selectedCooridnateColor= Color.WHITE


    /**
     * View背景色
     */
    private var bgColor = Color.parseColor("#00d9ff")


    /**
     * 正常状态下的bar的颜色
     */
    private var normalBarColor = Color.parseColor("#aaffffff")


    /**
     * 选中后的bar的颜色
     */
    private var selectedBarColor= Color.parseColor("#ffffffff")



    private var currentIndex=0


    private var textbgColor= Color.parseColor("#dddddd")

    private var textHeight=dip2px(context,20f)

    /**
     * 坐标轴text字体大小
     */
    private var cooridnateTextSize=dip2px(context,8f)


    /**
     * bar paint
     */
    private var paint: Paint = Paint()

    /**
     * 绘制坐标轴
     */
    var textPaint: Paint = Paint()


    var gap=0f



    val REFRESH_UI=1001




    val CALCUATE_COORIDNATE=1002

    var isSizeChanged=false

    var onSelectedListener: OnSelectedListener?=null



    private val postHandler=object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg!!.what){
               REFRESH_UI -> invalidate()

                CALCUATE_COORIDNATE -> {
                    if (isSizeChanged){
                        calculateCoordinate(false)
                    }

                }
            }

        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        unitWidth=width/14f
        gap=unitWidth
        isSizeChanged=true
        postHandler.sendEmptyMessage(CALCUATE_COORIDNATE)
//        log(message = "onSizeChanged, and unitWidth:$unitWidth,and gap:$gap")
        super.onSizeChanged(w, h, oldw, oldh)
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getMeasureWidth(widthMeasureSpec), getMeasureHeight(heightMeasureSpec))

    }


    override fun onFinishInflate() {
        super.onFinishInflate()



    }





    /**

     * @param widthMeasureSpec
     * *
     * @return
     * * 测量 width
     */
    private fun getMeasureWidth(widthMeasureSpec: Int): Int {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var width = 0
        when (widthMode) {
            MeasureSpec.AT_MOST -> width = this.width.toInt()
            MeasureSpec.EXACTLY -> {
                this.width = widthSize.toFloat()
                width = widthSize
            }
            MeasureSpec.UNSPECIFIED -> width = this.width.toInt()
        }
        return width
    }

    /**

     * @param heightMeasureSpec
     * *
     * @return
     * * 测量 height
     */
    private fun getMeasureHeight(heightMeasureSpec: Int): Int {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val height: Int
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
            this.height = height.toFloat()
        } else {
            this.height = getHeight().toFloat()
            height = this.height.toInt()
        }
        return height
    }


    /**
     * 设置s数据源
     */
    fun setList(list:MutableList<PartModel>){
        /**
         * 对list进行排序,按降序排列
         */
        log(message = "setlist,and list size:${list.size}")
        listBar = list
        currentIndex=listBar.size/2




        if (isSizeChanged){
            calculateCoordinate(false)
        }


    }

    var max=0f


    /**
     * 计算坐标
     * @param isMoved 是否属于移动操作
     * @param moved 移动操作的移动大小(方向定为从做到右为正方向)
     */
    fun calculateCoordinate(isMoving:Boolean,moved:Float=0f){
        if (listBar.size==0) return
        log(message = "calcuateCooridnate")
        if (isMoving){
            /**
             * 移动操作
             */
            if ( listBar[0].startAngle+moved<width/2f+unitWidth && listBar.last().startAngle+moved>width/2f-unitWidth) {
                /**
                 * 拖动范围不可超过一个bar的范围
                 */
                listBar.forEach { bar ->
                    bar.startAngle += moved
                }
                postHandler.sendEmptyMessage(REFRESH_UI)
            }

        }else{
            /**
             * 确认坐标
             */
            listBar.forEachIndexed { index, partModel ->

                partModel.startAngle=gap+index*(unitWidth+gap)
                partModel.sweep=partModel.value*(height-3*padding-textHeight)/max

                log(message = "sweep:${partModel.sweep}")
            }


            val startAngle=  listBar[currentIndex].startAngle
            val endAngle=width/2f-unitWidth/2f
            val offset=endAngle-startAngle
            if (offset!=0f) {
                listBar.forEach { partModel ->
                    partModel.startAngle += offset
                }
            }

            invalidate()
        }


    }





private var rectF: RectF = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.isAntiAlias=true
        paint.style= Paint.Style.FILL

        textPaint.isAntiAlias=true
        textPaint.style= Paint.Style.FILL
        textPaint.textSize=cooridnateTextSize

        /**
         *展示部分
         */
        rectF.set(0f,padding,width,height-textHeight-padding)
        paint.color=bgColor
        canvas.drawRect(rectF,paint)

        log(message = "height:$height,and width:$width")

        /**
         * 坐标轴
         */
        rectF.set(0f,height-textHeight-padding,width,height-padding)
        paint.color=textbgColor
        canvas.drawRect(rectF,paint)

        if (listBar.size>0) {
            listBar.forEachIndexed { index, partModel ->
                if (partModel.startAngle>=0-unitWidth && partModel.startAngle<=width+unitWidth){
                    log(message = "partmodel startAngle:${partModel.startAngle} value:${partModel.value},sweep:${partModel.sweep}")
                    /**
                     * 范围内绘制
                     */
                    if (width/2f in partModel.startAngle..(partModel.startAngle+unitWidth)){
                        /**
                         * 被选中
                         */
                        paint.color=selectedBarColor
                        textPaint.color=selectedCooridnateColor
                        currentIndex=index
                        if (!isMoving){
                            onSelectedListener?.onSelectedListener(index,partModel)
                        }

                    }else{
                        /**
                         * 普通
                         */
                        paint.color=normalBarColor
                        textPaint.color=normalCooridnateColor
                    }

                    /**
                     * 绘制bar
                     */
                    val left=partModel.startAngle
                    val top=height-textHeight-padding-partModel.sweep
                    val right=left+unitWidth
                    val bottom=height-textHeight-padding
                    rectF.set(left,top,right,bottom)
                    canvas.drawRect(rectF,paint)


                    /**
                     * 绘制座标轴
                     */
                    rectF.set(left-unitWidth/2f,bottom,right+unitWidth/2f,height-padding)
                    val text=partModel.tagName
                    val fontMetrics=textPaint.fontMetrics
                    val baseLine=(rectF.bottom+rectF.top-fontMetrics.bottom-fontMetrics.top)/2f
                    textPaint.textAlign= Paint.Align.CENTER
                    canvas.drawText(text,rectF.centerX(),baseLine,textPaint)


                }
            }

        }else{

            /**
             * No Data
             */
            rectF.set(0f,padding,width,height-textHeight-padding)
            val text="No Data"
            textPaint.color= Color.parseColor("#aabbbbbb")
            textPaint.typeface= Typeface.defaultFromStyle(Typeface.ITALIC)
            textPaint.textSize=dip2px(context,35f)
            val fontMetrics=textPaint.fontMetrics
            val baseLine=(rectF.bottom+rectF.top-fontMetrics.bottom-fontMetrics.top)/2f
            textPaint.textAlign= Paint.Align.CENTER
            canvas.drawText(text,rectF.centerX(),baseLine,textPaint)
            onSelectedListener?.onSelectedListener(0, PartModel(0f))

        }
    }


    private var pre_x:Float?=null
    private var pre_y:Float?=null

    private var isMoving=false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (listBar.size>0) {
            parent.requestDisallowInterceptTouchEvent(true)
            val x = event!!.x
            val y = event.y

            if (pre_x == null) {
                pre_x = x
            }
            if (pre_y == null) {
                pre_y = y
            }


            when (event.action) {

                MotionEvent.ACTION_MOVE -> {
                    val offset_x = Math.abs(pre_x!! - x)
                    if (pre_x != null && pre_y != null && offset_x >= 10f) {
                        if (!isMoving) {
                            isMoving = true
                        }
                        calculateCoordinate(true, x - pre_x!!)
                        pre_x = x
                        pre_y = y

                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (pre_x != null && pre_y != null) {
                        calculateCoordinate(false)
                    }
                    pre_x = null
                    pre_y = null
                    isMoving = false
                }


                MotionEvent.ACTION_DOWN -> {


                }
            }


        }

        return super.onTouchEvent(event)



    }




}