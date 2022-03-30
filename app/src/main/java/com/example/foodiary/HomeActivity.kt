package com.example.foodiary

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class HomeActivity : AppCompatActivity() {
    lateinit var locationPermission: ActivityResultLauncher<Array<String>>
    val test = arrayListOf<dataDto>(
        dataDto(1, R.drawable.applogo.toString(), "20220325", 1, "마싯는 파스타 마싯는 파스타 마싯는 파스타 마싯는 파스타"),
        dataDto(2, R.drawable.applogo.toString(), "20220325", 2, "이거슨 갈B찜!!")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){ results->
            if(!results.all { it.value }){
                Toast.makeText(this, "권한 승인이 필요합니다", Toast.LENGTH_LONG).show()
            }
        }
        locationPermission.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

        val hCalendar = findViewById<MaterialCalendarView>(R.id.homeCalendar)
        val recyclerV = findViewById<RecyclerView>(R.id.foodList)

        // 달력 text 설정
        hCalendar.setDateTextAppearance(R.style.CustomDateTextAppearance)
        hCalendar.setWeekDayTextAppearance(R.style.CustomWeekDayAppearance)
        hCalendar.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance)

        // 달력 설정 및 오늘 날짜 focus
        hCalendar.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()
        hCalendar.setSelectedDate(CalendarDay.today())

        // 달력 꾸미기(토요일, 일요일 색 변경)
        val saturday = SaturdayDecorator()
        val sunday = SundayDecorator()
        hCalendar.addDecorators(saturday, sunday)

        // 날짜 변경
        var selectYear:Int
        var selectMonth:Int
        var selectDay:Int

        hCalendar.setOnDateChangedListener { widget, date, selected ->
            selectYear = date.year
            selectMonth = date.month
            selectDay = date.day
            val dateText = "${selectYear}/${selectMonth+1}/$selectDay"
            Log.d("확인!!!!!", "selectDate : $dateText")

            // RecyclerView 연결
            val datalist = DBHelper.getInstance(this, "foodata.db").select(dateText)

            for(i in datalist.indices){
                print("목록 확인 : ${datalist[i].toString()}")
            }
            recyclerV.visibility = View.VISIBLE
            val adap = Adapter(this, datalist)
            recyclerV.adapter = adap

            val layout = LinearLayoutManager(this)
            recyclerV.layoutManager = layout
        }

        // 데이터 추가창으로 이동
        val move = findViewById<Button>(R.id.insertMove)
        move.setOnClickListener {
            val itt = Intent(this, InsertActivity::class.java)
            startActivity(itt)
        }
    }
}