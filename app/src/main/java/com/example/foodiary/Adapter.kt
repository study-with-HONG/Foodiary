package com.example.foodiary

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class Adapter(private val context: Context, private val dataList: ArrayList<dataDto>)
    : RecyclerView.Adapter<Adapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val img = itemView.findViewById<ImageView>(R.id.recyImg)
        private val cate = itemView.findViewById<TextView>(R.id.recyCate)
        private val menu = itemView.findViewById<TextView>(R.id.recyMenu)

        fun bind(dto: dataDto, context: Context){
            // test image연결
//            val resourceId = context.resources.getIdentifier(dto.path, "drawable", context.packageName)
//            img.setImageResource(resourceId)

            // 사진 연결
            if (dto.path != "") {
//                val resourceId = context.resources.getIdentifier(dto.path, "drawable", context.packageName)
//                if (resourceId > 0) {
//                    img.setImageResource(resourceId)
//                } else {
                    val file:File = File(dto.path)
                    val fExist = file.exists()
                    if(fExist){
                        println("확인!!!!!!!!!! 이미지 존재!!!!!!!!!!")
                        val bitmap = BitmapFactory.decodeFile(dto.path)
                        img.setImageBitmap(bitmap)
                    }else{
                        println("확인!!!!!!!!!! 이미지 없음!!!!!!!!!!")
                        img.setImageResource(R.drawable.applogo)
                    }
//                }
            } else { // 저장된 이미지 없으면 로고 보여주기
                img.setImageResource(R.drawable.applogo)
            }

            // 카테고리, 메뉴 연결
            when(dto.category){
                1 -> cate.text = "아침"
                2 -> cate.text = "점심"
                3 -> cate.text = "저녁"
                4 -> cate.text = "간식"
            }
            menu.text = dto.menu

            // 수정, 삭제창으로 이동
            itemView.setOnClickListener{
                Intent(context, ListDetailActivity::class.java).apply {
                    putExtra("data", dto)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_layout, parent, false)
        return ItemViewHolder(view)
    }
    override fun onBindViewHolder(holder: Adapter.ItemViewHolder, position: Int) {
        holder.bind(dataList[position], context)
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
}