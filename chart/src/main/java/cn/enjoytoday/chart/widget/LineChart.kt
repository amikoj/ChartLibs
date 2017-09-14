package cn.enjoytoday.chart.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import cn.enjoytoday.chart.R
import cn.enjoytoday.chart.dip2px
import java.util.ArrayList

/**
 * @date 17-9-6.
 * @className LineChart
 * @serial 1.0.0
 * @author hfcai
 * 线性图
 * 
 * 
 */
class LineChart(context: Context, attrs: AttributeSet?, defStyleAttr: Int): View(context,attrs,defStyleAttr) {
     private var viewWith: Float = 0.toFloat()
     private var viewHeight: Float = 0.toFloat()

     private val brokenLineWith = 0.5f

     private var brokenLineColor = Color.parseColor("#00d9ff")
     private val straightLineColor = Color.parseColor("#aaaaaa")
     private val textNormalColor = Color.parseColor("#aaaaaa")

     var maxScore = 700f
     var minScore = 0f

     var monthCount = 7
     private var selectMonth = 7//选中的月份

     var monthText = mutableListOf("6", "7", "8", "9", "10", "11")
    var score = mutableListOf(660f, 663f, 669f, 678f, 682f, 689f)

     private var scorePoints: MutableList<Point>? = null

     private var textSize = dip2px(context,8f)

    private var brokenPaint: Paint? = null
    private var straightPaint: Paint? = null
    private var dottedPaint: Paint? = null
    private var textPaint: Paint? = null

    private var brokenPath: Path? = null



    init {
        requestFocus()
        isClickable=true
        initConfig(context, null)
        init()
    }


    constructor(context: Context, attributeset: AttributeSet):this(context,attributeset,0)

    constructor(context: Context):this(context,null,0)



    /**
     * 初始化布局配置

     * @param context
     * *
     * @param attrs
     */
    private fun initConfig(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LineChart)
        maxScore = a.getFloat(R.styleable.LineChart_max_score, 700f)
        minScore = a.getFloat(R.styleable.LineChart_min_score, 650f)
        brokenLineColor = a.getColor(R.styleable.LineChart_broken_line_color, brokenLineColor)
        a.recycle()

    }

    private fun init() {
        brokenPath = Path()

        brokenPaint = Paint()
        brokenPaint!!.isAntiAlias = true
        brokenPaint!!.style = Paint.Style.STROKE
        brokenPaint!!.strokeWidth = dip2px(context,brokenLineWith)
        brokenPaint!!.strokeCap = Paint.Cap.ROUND

        straightPaint = Paint()
        straightPaint!!.isAntiAlias = true
        straightPaint!!.style = Paint.Style.STROKE
        straightPaint!!.strokeWidth = brokenLineWith
        straightPaint!!.color = straightLineColor
        straightPaint!!.strokeCap = Paint.Cap.ROUND

        dottedPaint = Paint()
        dottedPaint!!.isAntiAlias = true
        dottedPaint!!.style = Paint.Style.STROKE
        dottedPaint!!.strokeWidth = brokenLineWith
        dottedPaint!!.color = straightLineColor
        dottedPaint!!.strokeCap = Paint.Cap.ROUND

        textPaint = Paint()
        textPaint!!.isAntiAlias = true
        textPaint!!.textAlign = Paint.Align.CENTER
        textPaint!!.style = Paint.Style.FILL
        textPaint!!.color = textNormalColor
        textPaint!!.textSize = dip2px(context,6f)


    }

    private fun initData() {
        scorePoints = ArrayList<Point>()
        val maxScoreYCoordinate = viewHeight * 0.15f
        val minScoreYCoordinate = viewHeight * 0.4f

        Log.v("ScoreTrend", "initData: " + maxScoreYCoordinate)

        val newWith = viewWith - viewWith * 0.15f * 2//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        var coordinateX: Int

        for (i in score.indices) {
            Log.v("ScoreTrend", "initData: " + score[i])
            val point = Point()
            coordinateX = (newWith * (i.toFloat() / (monthCount - 1)) + viewWith * 0.15f).toInt()
            point.x = coordinateX
            if (score[i] > maxScore) {
                score[i] = maxScore
            } else if (score[i] < minScore) {
                score[i] = minScore
            }
            point.y = ((maxScore - score[i])/ (maxScore - minScore) * (minScoreYCoordinate - maxScoreYCoordinate) + maxScoreYCoordinate).toInt()
            scorePoints!!.add(point)
        }
    }

     override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWith = w.toFloat()
        viewHeight = h.toFloat()
        initData()
    }

     override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDottedLine(canvas, viewWith * 0.15f, viewHeight * 0.15f, viewWith, viewHeight * 0.15f)
        drawDottedLine(canvas, viewWith * 0.15f, viewHeight * 0.4f, viewWith, viewHeight * 0.4f)
        drawText(canvas)
        drawMonthLine(canvas)
        drawBrokenLine(canvas)
        drawPoint(canvas)

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.parent.requestDisallowInterceptTouchEvent(true)//一旦底层View收到touch的action后调用这个方法那么父层View就不会再调用onInterceptTouchEvent了，也无法截获以后的action

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP -> {
                onActionUpEvent(event)
                this.parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_CANCEL -> this.parent.requestDisallowInterceptTouchEvent(false)
        }
        return true
    }

    private fun onActionUpEvent(event: MotionEvent) {
        val isValidTouch = validateTouch(event.x, event.y)

        if (isValidTouch) {
            invalidate()
        }
    }

    //是否是有效的触摸范围
    private fun validateTouch(x: Float, y: Float): Boolean {
        //曲线触摸区域
        for (i in scorePoints!!.indices) {
            // dip2px(8)乘以2为了适当增大触摸面积
            if (x > scorePoints!![i].x - dip2px(context,8f) * 2 && x < scorePoints!![i].x + dip2px(context,8f) * 2) {
                if (y > scorePoints!![i].y - dip2px(context,8f) * 2 && y < scorePoints!![i].y + dip2px(context,8f) * 2) {
                    selectMonth = i + 1
                    return true
                }
            }
        }

        //月份触摸区域
        //计算每个月份X坐标的中心点
        val monthTouchY = viewHeight * 0.7f - dip2px(context,3f)//减去dip2px(3)增大触摸面积

        val newWith = viewWith - viewWith * 0.15f * 2//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        val validTouchX = FloatArray(monthText.size)
        for (i in monthText.indices) {
            validTouchX[i] = newWith * (i.toFloat() / (monthCount - 1)) + viewWith * 0.15f
        }

        if (y > monthTouchY) {
            for (i in validTouchX.indices) {
                Log.v("ScoreTrend", "validateTouch: validTouchX:" + validTouchX[i])
                if (x < validTouchX[i] + dip2px(context,8f) && x > validTouchX[i] - dip2px(context,8f)) {
                    Log.v("ScoreTrend", "validateTouch: " + (i + 1))
                    selectMonth = i + 1
                    return true
                }
            }
        }

        return false
    }

    /**
     * 测量文字的宽度
     */
    private fun getStrWidth(text:String,paint: Paint):Float{
     return    paint.measureText(text)
    }





    //绘制折线穿过的点
    private fun drawPoint(canvas: Canvas) {
        if (scorePoints == null) {
            return
        }
        brokenPaint!!.strokeWidth = dip2px(context,1f)
        for (i in scorePoints!!.indices) {
            brokenPaint!!.color = brokenLineColor
            brokenPaint!!.style = Paint.Style.STROKE
            canvas.drawCircle(scorePoints!![i].x.toFloat(), scorePoints!![i].y.toFloat(), dip2px(context,3f), brokenPaint!!)
            brokenPaint!!.color = Color.WHITE
            brokenPaint!!.style = Paint.Style.FILL
            if (i == selectMonth - 1) {
                brokenPaint!!.color = 0xffd0f3f2.toInt()
                canvas.drawCircle(scorePoints!![i].x.toFloat(), scorePoints!![i].y.toFloat(), dip2px(context,8f), brokenPaint!!)
                brokenPaint!!.color = 0xff81dddb.toInt()
                canvas.drawCircle(scorePoints!![i].x.toFloat(), scorePoints!![i].y.toFloat(), dip2px(context,5f), brokenPaint!!)

                //绘制浮动文本背景框
                drawFloatTextBackground(score[i].toString(),canvas, scorePoints!![i].x, scorePoints!![i].y - dip2px(context,8f))

                textPaint!!.color = 0xffffffff.toInt()
                //绘制浮动文字
                canvas.drawText(score[i].toString(), scorePoints!![i].x.toFloat(), (scorePoints!![i].y - dip2px(context,5f) - textSize), textPaint!!)
            }
            brokenPaint!!.color = 0xffffffff.toInt()
            canvas.drawCircle(scorePoints!![i].x.toFloat(), scorePoints!![i].y.toFloat(), dip2px(context,1.5f), brokenPaint!!)
            brokenPaint!!.style = Paint.Style.STROKE
            brokenPaint!!.color = brokenLineColor
            canvas.drawCircle(scorePoints!![i].x.toFloat(), scorePoints!![i].y.toFloat(), dip2px(context,2.5f), brokenPaint!!)
        }
    }

    //绘制月份的直线(包括刻度)
    private fun drawMonthLine(canvas: Canvas) {
        straightPaint!!.strokeWidth = dip2px(context,1f).toFloat()
        canvas.drawLine(0f, viewHeight * 0.7f, viewWith, viewHeight * 0.7f, straightPaint!!)

        val newWith = viewWith - viewWith * 0.15f * 2//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        var coordinateX: Float//分隔线X坐标
        for (i in 0..monthCount - 1) {
            coordinateX = newWith * (i.toFloat() / (monthCount - 1)) + viewWith * 0.15f
            canvas.drawLine(coordinateX, viewHeight * 0.7f, coordinateX, viewHeight * 0.7f + dip2px(context,4f), straightPaint!!)
        }
    }

    //绘制折线
    private fun drawBrokenLine(canvas: Canvas) {
        brokenPath!!.reset()
        brokenPaint!!.color = brokenLineColor
        brokenPaint!!.style = Paint.Style.STROKE
        if (score.size == 0) {
            return
        }
        Log.v("ScoreTrend", "drawBrokenLine: " + scorePoints!![0])
        brokenPath!!.moveTo(scorePoints!![0].x.toFloat(), scorePoints!![0].y.toFloat())
        for (i in scorePoints!!.indices) {
            brokenPath!!.lineTo(scorePoints!![i].x.toFloat(), scorePoints!![i].y.toFloat())
        }
        canvas.drawPath(brokenPath!!, brokenPaint!!)

    }

    //绘制文本
    private fun drawText(canvas: Canvas) {
        textPaint!!.textSize = dip2px(context,12f)
        textPaint!!.color = textNormalColor

        canvas.drawText(maxScore.toString(), viewWith * 0.1f - dip2px(context,10f), viewHeight * 0.15f + textSize * 0.25f, textPaint!!)
        canvas.drawText(minScore.toString(), viewWith * 0.1f - dip2px(context,10f), viewHeight * 0.4f + textSize * 0.25f, textPaint!!)
        textPaint!!.color = 0xff7c7c7c.toInt()

        val newWith = viewWith - viewWith * 0.15f * 2//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        var coordinateX: Float//分隔线X坐标
        textPaint!!.textSize = dip2px(context,12f)
        textPaint!!.style = Paint.Style.FILL
        textPaint!!.color = textNormalColor
        textSize = textPaint!!.textSize
        for (i in monthText.indices) {
            coordinateX = newWith * (i.toFloat() / (monthCount - 1)) + viewWith * 0.15f

            if (i == selectMonth - 1) {

                textPaint!!.style = Paint.Style.STROKE
                textPaint!!.color = brokenLineColor
                val r2 = RectF()
                val textWidth=getStrWidth(monthText[i], textPaint!!)/2f
                r2.left = coordinateX - textWidth- dip2px(context,4f)
                r2.top = viewHeight * 0.7f + dip2px(context,4f) + (textSize / 2)
                r2.right = coordinateX + textWidth + dip2px(context,4f)
                r2.bottom = viewHeight * 0.7f + dip2px(context,4f) + textSize + dip2px(context,8f)
                canvas.drawRoundRect(r2, 10f, 10f, textPaint!!)
            }


            monthText[i].length
            if (Math.abs(i-selectMonth-1)%2==0) {
                canvas.drawText(monthText[i], coordinateX, viewHeight * 0.7f + dip2px(context, 4f) + textSize + dip2px(context, 5f), textPaint!!)
            }

            textPaint!!.color = textNormalColor

        }

    }

    //绘制显示浮动文字的背景
    private fun drawFloatTextBackground(text: String, canvas: Canvas, x: Int, y: Float) {
        brokenPath!!.reset()
        brokenPaint!!.color = brokenLineColor
        brokenPaint!!.style = Paint.Style.FILL
        val textWidth=getStrWidth(text,brokenPaint!!)

        //P1
        val point = Point(x, y.toInt())
        brokenPath!!.moveTo(point.x.toFloat(), point.y.toFloat())

        //P2
        point.x = (point.x + dip2px(context,5f)).toInt()
        point.y = (point.y - dip2px(context,5f)).toInt()
        brokenPath!!.lineTo(point.x.toFloat(), point.y.toFloat())

        //P3
        point.x = (point.x + dip2px(context,(textWidth-10f)/2f+4f)).toInt()
        brokenPath!!.lineTo(point.x.toFloat(), point.y.toFloat())

        //P4
        point.y = (point.y - dip2px(context,17f)).toInt()
        brokenPath!!.lineTo(point.x.toFloat(), point.y.toFloat())

        //P5
        point.x = (point.x - dip2px(context,textWidth+8f)).toInt()
        brokenPath!!.lineTo(point.x.toFloat(), point.y.toFloat())

        //P6
        point.y = (point.y + dip2px(context,17f)).toInt()
        brokenPath!!.lineTo(point.x.toFloat(), point.y.toFloat())

        //P7
        point.x = (point.x + dip2px(context,(textWidth-10f)/2f+2f)).toInt()
        brokenPath!!.lineTo(point.x.toFloat(), point.y.toFloat())

        //最后一个点连接到第一个点
        brokenPath!!.lineTo(x.toFloat(), y)

        canvas.drawPath(brokenPath!!, brokenPaint!!)
    }

    /**
     * 画虚线

     * @param canvas 画布
     * *
     * @param startX 起始点X坐标
     * *
     * @param startY 起始点Y坐标
     * *
     * @param stopX  终点X坐标
     * *
     * @param stopY  终点Y坐标
     */
    private fun drawDottedLine(canvas: Canvas, startX: Float, startY: Float, stopX: Float, stopY: Float) {
        dottedPaint!!.pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 4f)
        dottedPaint!!.strokeWidth = 1f
        // 实例化路径
        val mPath = Path()
        mPath.reset()
        // 定义路径的起点
        mPath.moveTo(startX, startY)
        mPath.lineTo(stopX, stopY)
        canvas.drawPath(mPath, dottedPaint!!)

    }




  

}