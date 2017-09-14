package cn.enjoytoday.chart.widget

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import cn.enjoytoday.chart.PartModel
import cn.enjoytoday.chart.OnSelectedListener
import cn.enjoytoday.chart.dip2px


/**
 * @date 17-9-4.
 * @className PieChart
 * @serial 1.0.0
 *
 *
 *自定义扇形图表
 */
class PieChart(context: Context, attributeset: AttributeSet?, defStyleAttr:Int): View(context,attributeset,defStyleAttr){

    init {
        requestFocus()
        isClickable=true
    }

    constructor(context: Context, attributeset: AttributeSet):this(context,attributeset,0)

    constructor(context: Context):this(context,null,0)


    var listBar:MutableList<PartModel> = mutableListOf()


    var width=0f
    var height=0f

    var max:Float=0f
    var currentBarIndex=0
    var padding=dip2px(context,8f)
    /**
     * 圆心x轴坐标
     */
    var center_x=0f
    /**
     * 圆心y轴坐标
     */
    var center_y=0f

    /**
     * 外部圆环半径
     */
    var outer_ring_radius=0f
    /**
     * 内部圆环半径
     */
    var inter_ring_radius=0f


    /**
     * 延伸除去的半径
     */
    var offerset_radius=dip2px(context,10f)


    var onSelectedListener: OnSelectedListener?=null


    var rectF: RectF = RectF()

    val REFRESH_UI=1001
    val CALCUATE_COORIDNATE=1002
    var isSizeChanged=false

    val postHandler=object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg!!.what){
                REFRESH_UI -> invalidate()

                CALCUATE_COORIDNATE -> {
                    if (isSizeChanged){
                        calcuateCooridnate(false,null,currentBarIndex)
                    }

                }
            }

        }
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
     * 添加类别
     */
    fun  addModel(model: PartModel){
        listBar.add(model)
        max += model.value
        calcuateCooridnate(false,null,currentBarIndex)
    }


    /**
     * 添加一个列表
     */
    fun addList(list:MutableList<PartModel>){
        listBar.addAll(list)
        list.forEach { max+=it.value }
        calcuateCooridnate(false,null,currentBarIndex)

    }

    /**
     * 更新一个新的List
     */
    fun setList(list:MutableList<PartModel>){
//        log(message = "setlist,and list size:${list.size}")
        listBar.clear()
        listBar=list
        max=0f
        listBar.forEachIndexed { index, partModel ->
            if (partModel.color==-1) {
                when (index % 7) {
                    0 -> partModel.color = Color.RED
                    1 -> partModel.color = Color.parseColor("#ff6100") //橙色
                    2 -> partModel.color = Color.YELLOW
                    3 -> partModel.color = Color.GREEN
                    4 -> partModel.color = Color.parseColor("#00ffff")//青色
                    5 -> partModel.color = Color.BLUE
                    6 -> partModel.color = Color.parseColor("#ff00ff")
                }
            }
            max+=partModel.value
        }
        calcuateCooridnate(false,null,currentBarIndex)

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        center_x = width / 2f
        center_y = height / 2f
        outer_ring_radius = (Math.min(width, height) - 2f * padding)/2f
        inter_ring_radius = outer_ring_radius / 3f * 2f
        isSizeChanged=true
        postHandler.sendEmptyMessage(CALCUATE_COORIDNATE)
        super.onSizeChanged(w, h, oldw, oldh)
    }

    val paint: Paint = Paint()
    val textPaint: Paint = Paint()

    override fun onDraw(canvas: Canvas) {


        paint.isAntiAlias=true
        paint.style= Paint.Style.STROKE
        paint.strokeWidth=outer_ring_radius/3f

        textPaint.isAntiAlias=true
        textPaint.style= Paint.Style.FILL

        if (listBar.size==0){
            paint.color= Color.RED
            canvas.drawCircle(center_x,center_y,inter_ring_radius,paint)

            /**
             * 中间文字
             */
            val inner_radius=(2f*inter_ring_radius/Math.sqrt(2.toDouble())).toFloat()
            rectF.set(center_x-inner_radius,center_y-inner_radius,center_x+inner_radius,center_y+inner_radius)
            val text="No Data"
            textPaint.color= Color.parseColor("#aabbbbbb")
            textPaint.typeface= Typeface.defaultFromStyle(Typeface.ITALIC)
            textPaint.textSize=dip2px(context,25f)
            val fontMetrics=paint.fontMetrics
            val baseLine=(rectF.bottom+rectF.top-fontMetrics.bottom-fontMetrics.top)/2f
            textPaint.textAlign= Paint.Align.CENTER
            canvas.drawText(text,rectF.centerX(),baseLine,textPaint)

            onSelectedListener?.onSelectedListener(0, PartModel(0f))



        }else {
            listBar.forEachIndexed { index, partModel ->
//                            log(message = "partmodel startAngle:${partModel.startAngle} value:${partModel.value},height:${partModel.sweep}")
                paint.color = partModel.color

                val startAngle = partModel.startAngle
                val endAngle = (partModel.startAngle + partModel.sweep) % 360f
                if ((startAngle > endAngle && endAngle > 90f) || (90f in startAngle..endAngle) || partModel.sweep==360f) {
                    /**
                     * 被选中
                     */
                    val offerset_angle = partModel.startAngle + partModel.sweep / 2f
                    val offset_center_x = center_x + offerset_radius * Math.cos(Math.toRadians(offerset_angle.toDouble())).toFloat()
                    val offset_center_y = center_y + offerset_radius * Math.sin(Math.toRadians(offerset_angle.toDouble())).toFloat()
                    rectF.set(offset_center_x - inter_ring_radius, offset_center_y - inter_ring_radius,
                            offset_center_x + inter_ring_radius, offset_center_y + inter_ring_radius)
                    currentBarIndex = index

                    if (!isMoving) {
                        onSelectedListener?.onSelectedListener(index, partModel)
                    }

                } else {
                    /**
                     * 一般的未被选中的
                     */
                    rectF.set(center_x - inter_ring_radius, center_y - inter_ring_radius, center_x + inter_ring_radius, center_y + inter_ring_radius)
                }

                canvas.drawArc(rectF, partModel.startAngle, partModel.sweep, false, paint)
            }

        }

    }


    /**
     * 设置当前选择
     */
    fun setCurrentBarIndex(currentIndex: Int?){
        currentIndex?:return

        val startAngle=listBar[currentIndex].startAngle
        val endAngle= getStartAngleByCurrentIndex(currentIndex)

        var offset=endAngle-startAngle


//        log(message = "before offset is:$offset")
        if (Math.abs(offset)>180){
           if (offset<0) offset=(offset+360f)%360
           else offset= (offset-360)%360
        }

//        log(message = "after offset is:$offset")
        val times=Math.abs(offset/20f).toInt()
        if (times<=1){
            calcuateCooridnate(true,offset,currentIndex)
        }else {
            val unit=offset/times
            handler.postDelayed(object:Runnable{
                var n=0
                override fun run() {
                    if (n<times) {
                        calcuateCooridnate(true, unit, currentIndex)
                        n++
                        handler.postDelayed(this,100)
                    }else{
                        calcuateCooridnate(true, offset-unit*n, currentIndex)
                    }
                }


            }, 100)

        }

    }


    /**
     * 获取当前选中index的开始角度
     */
    fun getStartAngleByCurrentIndex(currentIndex: Int):Float{

        val bar=listBar[currentIndex]
       return (90f-bar.sweep/2f+360)%360

    }





    /**
     * 根据x,y轴坐标求相对坐标,范围(0,360)
     */
    fun getAngelByCooridnate(cooridnate_x:Float,cooridnate_y: Float):Float{


        val offerset_y=center_y-cooridnate_y
        val distance=Math.sqrt(((cooridnate_x-center_x)*(cooridnate_x-center_x)+(cooridnate_y-center_y)*(cooridnate_y-center_y)).toDouble())
        var angle=Math.asin(offerset_y/distance)/2f/Math.PI*360
        if (cooridnate_x<center_x){
            angle=180-angle
        }
        return (360-angle.toFloat())%360f
    }





    /**
     * 计算角度坐标
     */
    fun calcuateCooridnate(isMove:Boolean,moved:Float?,currentIndex:Int?){
//        log(message = "calcuateCooridnate")
        if (isMove ) {
            /**
             * 滑动时移动处理,手指未离开屏幕
             */

            if (moved!=null && moved!=0f) {
                listBar.forEachIndexed { index, partModel ->
                    val angle=(partModel.startAngle+moved+360)%360f
                    partModel.startAngle=angle
                }

                postHandler.sendEmptyMessage(REFRESH_UI)
            }
        }else{
            /**
             * 开始或者滑动结束时
             */
            var totalAngle=0f
            listBar.forEachIndexed { index, partModel ->
                var sweep=partModel.value/max*360f
                if (totalAngle+sweep>360){
                    sweep=360-totalAngle
                }
//                log(message = "sweep is:$sweep")
                partModel.sweep=sweep
                if (index==0){
                    /**
                     * 刚开始
                     */
                    partModel.startAngle=((90f-sweep/2f)+360)%360f
                }else{
                    /**
                     *
                     */
                    partModel.startAngle=((listBar[0].startAngle+totalAngle)+360)%360f
                }
                totalAngle+=partModel.sweep

            }

            invalidate()



        }


    }






    var pre_x:Float?=null
    var pre_y:Float?=null


    var isMoving=false



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
                    val offset_y = Math.abs(pre_y!! - y)
//                log(message = "actionMove offset_x:$offset_x,and offset_y:$offset_y")
                    if (pre_x != null && pre_y != null && (offset_x >= 10f || offset_y >= 10f)) {
                        val sweep = getAngelByCooridnate(x, y) - getAngelByCooridnate(pre_x!!, pre_y!!)
                        calcuateCooridnate(true, sweep, currentBarIndex)


                        if (!isMoving) isMoving = true
                        pre_x = x
                        pre_y = y
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (pre_x != null && pre_y != null) {
                        val sweep = getAngelByCooridnate(x, y) - getAngelByCooridnate(pre_x!!, pre_y!!)
                        calcuateCooridnate(true, sweep, currentBarIndex)
                        setCurrentBarIndex(currentBarIndex)
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




    override fun onDetachedFromWindow() {
//        releaseVelocityTracker()
        super.onDetachedFromWindow()
    }




}























