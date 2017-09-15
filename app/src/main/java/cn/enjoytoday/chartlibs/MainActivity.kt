package cn.enjoytoday.chartlibs

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import cn.enjoytoday.chart.OnSelectedListener
import cn.enjoytoday.chart.PartModel
import cn.enjoytoday.chart.dip2px
import cn.enjoytoday.chart.widget.BarChart
import cn.enjoytoday.chart.widget.LineChart
import cn.enjoytoday.chart.widget.PieChart
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_layout.*
import kotlinx.android.synthetic.main.item_layout.view.*
import java.math.MathContext

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        view_pager.adapter=object :PagerAdapter(){
            override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
                return  view==`object`
            }

            override fun getCount(): Int {
                return 3
            }


            override fun instantiateItem(container: ViewGroup?, position: Int): Any {
                var view:View=LayoutInflater.from(this@MainActivity).inflate(R.layout.item_layout,null)

                view.pieChart.visibility=View.GONE
                view.lineChart.visibility=View.GONE
                view.barChart.visibility=View.GONE

                val list:MutableList<PartModel> = mutableListOf()
                list.add(PartModel(10f,"one","0"))
                list.add(PartModel(30f,"two","1"))
                list.add(PartModel(10f,"three","2"))
                list.add(PartModel(90f,"four","3"))
                list.add(PartModel(10f,"five","4"))
                list.add(PartModel(50f,"six","5"))

                when(position){
                   0->{
                      view.pieChart.visibility=View.VISIBLE
                       view.pieChart.setList(list)
                       view.pieChart.onSelectedListener=object : OnSelectedListener{
                           override fun onSelectedListener(index: Int, partModel: PartModel) {
                               view.text_view.text="${partModel.value}"
                           }

                       }

                   }

                    1->{

                        view.lineChart.visibility=View.VISIBLE
                        val sums= mutableListOf<Float>()
                        val lables= mutableListOf<String>()
                        list.forEach {
                            sums.add(it.value)
                            lables.add(it.tagName!!)
                        }
                        view.lineChart.maxScore= sums.max()!!
                        view.lineChart.minScore=sums.min()!!
                        view.lineChart.monthCount=sums.size
                        view.lineChart.monthText=lables
                        view.lineChart.score=sums

                        view.lineChart.onSelectedListener=object :OnSelectedListener{
                            override fun onSelectedListener(index: Int, partModel: PartModel) {
                                view.text_view.text="${partModel.value}"
                            }

                        }
                    }

                    2->{
                        view.barChart.visibility=View.VISIBLE
                        view.barChart.setList(list)
                        view.barChart.onSelectedListener=object : OnSelectedListener{
                            override fun onSelectedListener(index: Int, partModel: PartModel) {
                                view.text_view.text="${partModel.value}"
                            }

                        }
                    }
                }

                container!!.addView(view)
                return view
            }


            override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
                container!!.removeView(`object` as View?)
            }

        }


        nts.setViewPager(view_pager)

    }



}
