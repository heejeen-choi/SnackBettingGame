package com.portpolio.myapplication

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.lang.Math.abs
import java.util.*

class MainActivity : AppCompatActivity() {
    var p_num = 3 //초기참가지 3명
    var k = 1 //참가하는 사람 번수
    val point_list = mutableListOf<Float>() //점수리스트, 자료형:소수점float
    var isBlind = false //초가리기 블라인드


    fun start() {
        setContentView(R.layout.activity_start)
        //start화면위젯들
        val tv_pnum: TextView = findViewById(R.id.tv_pnum)
        val btn_minus: Button = findViewById(R.id.btn_minus)
        val btn_plus: Button = findViewById(R.id.btn_plus)
        val btn_start: Button = findViewById(R.id.btn_start)
        val btn_blind: Button = findViewById(R.id.btn_blind)

        //블라인드버튼 클릭마다 on과 off 교체
        btn_blind.setOnClickListener {
            isBlind = !isBlind
            if (isBlind == true) {
                btn_blind.text = "Blind 모드 ON"
            } else {
                btn_blind.text = "Blind 모드 OFF"
            }
        }

        //p_num변수 string값으로 표시
        tv_pnum.text = p_num.toString()
        //minus버튼 클릭시
        btn_minus.setOnClickListener {
            p_num -- //p_num이 바뀌고
            if (p_num == 0) { //p_num이 음수값이되면 1로 하한선 지정
                p_num = 1
            }
            tv_pnum.text = p_num.toString() //바뀐 p_num이 표시됨(plus도 동일)
        }
        //plus버튼 클릭시
        btn_plus.setOnClickListener {
            p_num ++
            tv_pnum.text = p_num.toString()
        }
        btn_start.setOnClickListener {
            main() //시작버튼 누르면 메인화면으로
        }


    }


    fun main() {
        setContentView(R.layout.activity_main)

        var timerTask: Timer? = null
        var stage = 1 //stage의 초기상태
        var sec : Int = 0
        //위젯들
        val tv: TextView = findViewById(R.id.tv_pnum)
        val tv_t: TextView = findViewById(R.id.tv_timer)
        val tv_p: TextView = findViewById(R.id.tv_point)
        val tv_people: TextView = findViewById(R.id.tv_people)
        val btn: Button = findViewById(R.id.btn_star)
        val btn_i: Button = findViewById(R.id.btn_i)
        val random_box = Random()
        //정수반환을 위해 0~10까지 .nextInt(11)이고 소수점 둘째자리까지 나타내야해서 100을 곱함
        val num = random_box.nextInt(1001) //num:랜덤한숫자(목표숫자 자동지정)
        val bg_main : ConstraintLayout = findViewById(R.id.bg_main)

        //다음 사람의 순서 때마다 배경색이 바뀌도록_최대 7가지 색상으로 설정
        val color_list = mutableListOf<String>("#FFB2A6", "#FFD32D", "#86C6F4", "#BB6464", "#CDB699", "#A1B57D", "#84DFFF")

        // -1값이 되지 않게 color_index 추가
       var color_index = k%7-1
        if (color_index == -1) {
            color_index = 6
        }

        //get(k%7-1)로 했을 때 k=7인경우 -1값이 되어 오류가 남, 자동으로 마지막수로의 인덱싱이 안됨
        val color_sel = color_list.get(color_index)
        bg_main.setBackgroundColor(Color.parseColor(color_sel))

        //소수점둘째자리까지 나타내기위해 100곱했으니 100나눔
        tv.text = ((num.toFloat())/100).toString() //랜덤한숫자 num 표시
        btn.text = "시작" //stage:1에서는 버튼글자가 '시작'으로 표시
        tv_people.text = "사람 $k"

        //홈버튼 클릭시 모든것을 초기화 하고, fun start()로
        btn_i.setOnClickListener {
            point_list.clear()
            k = 1
            start()
        }

        //시작정지버튼 눌렀을때의 이벤트트
       btn.setOnClickListener {
            stage ++ //stage1:대기상태 stage2:시간흘러가는상태 stage3:최종점수표시 클릭시마다올라감
            //최초 디폴트값 stage:1에서 처음클릭했을때 stage가 2가 됨
            if (stage == 2) {
                timerTask = kotlin.concurrent.timer(period = 10) { //시간흐름
                    sec++
                    runOnUiThread { //표시되는것
                        if (isBlind == false) {
                            tv_t.text = (sec.toFloat() / 100).toString()
                        } else if (isBlind == true && stage == 2) {
                            tv_t.text = "???"
                        }
                    }
                }
                btn.text = "정지" //시간 흐르는중(stage2)에는 버튼글자가:'정지'로 표시되도록
            } else if (stage == 3) {
                tv_t.text = (sec.toFloat() / 100).toString()
                timerTask?.cancel() //timerTask시간흐름이 중지cancel됨
                val point = abs(sec-num).toFloat()/100 //sec에서 num값 뺸것의 절댓값 abs
                point_list.add(point)
                tv_p.text = point.toString() //위의 float형태를 string형태로 변환하여 나타내기(목표값과 현재값의 오차)
                btn.text = "다음" //시간흐르는것이 멈추면(정지누르면:stage3) 나타나는 글자
                stage = 0 //stage3에서 한번더클릭하면 stage1이됨
            } else if (stage == 1) { //stage1:초기상태로 되돌아간다는뜻
                if (k < p_num) {
                    k ++
                    main() //재귀
                } else { //모든 사람이 실행완료되면 끝남
                    end()
                }
            }
        }
    }

    //종료화면
    fun end() {
        setContentView(R.layout.activity_end)
        //종료화면위젯들
        val tv_last: TextView = findViewById(R.id.tv_last)
        val tv_lpoint: TextView = findViewById(R.id.tv_lpoint) //제일큰 오차값
        val btn_init: Button = findViewById(R.id.btn_init)

        //제일 큰 오차값을 문자열string으로 변환하여 표시,불러오기
        tv_lpoint.text = (point_list.maxOrNull()).toString()
        //lpoint의 사람 불러오기
        var index_last = point_list.indexOf(point_list.maxOrNull())
        tv_last.text = "사람 "+(index_last+1).toString()

        //처음으로 가는 init버튼
        btn_init.setOnClickListener {
            point_list.clear() //모든걸초기화시키고 돌아가기
            k = 1
            start()
        }
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start()















    }
}