package cn.enjoytoday.chartlibs

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import cn.enjoytoday.chart.PartModel
import cn.enjoytoday.chart.dip2px
import cn.enjoytoday.chart.widget.BarChart
import cn.enjoytoday.chart.widget.LineChart
import cn.enjoytoday.chart.widget.PieChart
import kotlinx.android.synthetic.main.activity_main.*
import java.math.MathContext

class MainActivity : AppCompatActivity() {

    private val list:MutableList<PartModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.add(PartModel(10f,"one","0"))
        list.add(PartModel(30f,"two","1"))
        list.add(PartModel(10f,"three","2"))
        list.add(PartModel(90f,"four","3"))
        list.add(PartModel(10f,"five","4"))
        list.add(PartModel(50f,"six","5"))

        view_pager.adapter=object :PagerAdapter(){
            override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
                return  view==`object`
            }

            override fun getCount(): Int {
                return 3
            }


            override fun instantiateItem(container: ViewGroup?, position: Int): Any {
                var view:View?=null
                when(position){
                   0->{
                       val pieChart=PieChart(this@MainActivity)
                       pieChart.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                       pieChart.setList(list)

                       view=pieChart
                   }

                    1->{

                        val lineChart=LineChart(this@MainActivity)
                        lineChart.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)


                        val sums= mutableListOf<Float>()


                        val lables= mutableListOf<String>()
                        list.forEach {
                            sums.add(it.value)
                            lables.add(it.tagName!!)
                        }
                        lineChart.maxScore= sums.max()!!
                        lineChart.minScore=sums.min()!!
                        lineChart.monthCount=sums.size
                        lineChart.monthText=lables
                        lineChart.score=sums
                        view=lineChart
                    }

                    2->{

                        val barChart=BarChart(this@MainActivity)
                        barChart.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

                        barChart.setList(list)

                        view=barChart
                    }
                }






                container!!.addView(view)



                return view!!
            }


            override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
                container!!.removeView(`object` as View?)
            }

        }


        nts.setViewPager(view_pager)

    }



}
