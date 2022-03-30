package com.example.foodiary

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.FileOutputStream

class ListDetailActivity : AppCompatActivity() {
    // storage 권한 처리에 필요한 변수
    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99
    var path:String = ""

    @SuppressLint("WrongThread", "CutPasteId")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_detail)

        // 카메라 접속
        val camera = findViewById<Button>(R.id.detailCamera)
        camera.setOnClickListener{
            Toast.makeText(this, "카메라", Toast.LENGTH_LONG).show()
            GetCamera()
        }
        // 앨범 접속
        val album = findViewById<Button>(R.id.detailAlbum)
        album.setOnClickListener{
            Toast.makeText(this, "앨범", Toast.LENGTH_LONG).show()
            GetAlbum()
        }
        // 데이터 불러오기
        val dto = intent.getParcelableExtra<dataDto>("data")
        println("dto:${dto?.path}")

        val dImage = findViewById<ImageView>(R.id.detailImg)
        val dDateView = findViewById<TextView>(R.id.detailDateView)
        val dRadio = findViewById<RadioGroup>(R.id.detailRG)
        val dMenu = findViewById<TextView>(R.id.detailMenu)

        dImage.setImageBitmap(BitmapFactory.decodeFile(dto?.path))
        dDateView.text = dto?.date
        dMenu.text = dto?.menu

        var cate:Int = 0
        when(dto?.category){
            1 -> {
                dRadio.check(R.id.detailRadio1)
                cate = 1
            }
            2 -> {
                dRadio.check(R.id.detailRadio2)
                cate = 2
            }
            3 -> {
                dRadio.check(R.id.detailRadio3)
                cate = 3
            }
            4 -> {
                dRadio.check(R.id.detailRadio4)
                cate = 4
            }
        }

        // 데이터 삭제
        val delete = findViewById<Button>(R.id.deleteBtn)
        delete.setOnClickListener {
            val data = dataDto(0, path.toString(), dDateView.text.toString(), cate, dMenu.text.toString())
            println("삭제확인!!!!!!!!!! ${data.path} ${data.date}  ${data.category}  ${data.menu}")

            val dbh =  DBHelper.getInstance(this, "foodata.db")
            dbh.delete(data)

            Toast.makeText(this.applicationContext, "삭제완료", Toast.LENGTH_SHORT).show()
            val itt = Intent(this, HomeActivity::class.java)
            startActivity(itt)
        }
        // 데이터 수정
        val update = findViewById<Button>(R.id.updateBtn)
        update.setOnClickListener {
            val data = dataDto(0, path.toString(), dDateView.text.toString(), 0, dMenu.text.toString())
            println("수정확인!!!!!!!!!! ${data.path} ${data.date}  ${data.category}  ${data.menu}")

            val dbh =  DBHelper.getInstance(this, "foodata.db")
            dbh.update(data)

            Toast.makeText(this.applicationContext, "수정완료", Toast.LENGTH_SHORT).show()
            val itt = Intent(this, HomeActivity::class.java)
            startActivity(itt)
        }
    }
    // 카메라, 저장소 권한 요청
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_CODE -> {
                for (grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "카메라 권한 승인 요청", Toast.LENGTH_LONG).show()
                    }
                }
            }
            STORAGE_CODE -> {
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "저장소 권한 승인 요청", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    // 다른 권한 확인
    fun checkPermission(permissions: Array<out String>, type:Int):Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (permission in permissions){
                if(ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }
        return true
    }
    // 카메라 접속 -> 사진 촬영
    fun GetCamera(){
        if(checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)){
            val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intentCamera, CAMERA_CODE)
        }
    }
    // 파일명 날짜로 저장
    @RequiresApi(Build.VERSION_CODES.N)
    fun fileName() : String{
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
        return fileName
    }
    // 사진 저장
    fun saveFile(fileName:String, mimeType:String, bitmap: Bitmap): Uri?{
        var CV = ContentValues()
        // MediaStore에 파일명, mimeType 지정
        CV.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        // 안정성 검사
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        // MediaStore에 파일 저장
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)
        if(uri != null){
            var scriptor = contentResolver.openFileDescriptor(uri, "w")
            val fos = FileOutputStream(scriptor?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                CV.clear()
                // IS_PENDING 0으로 초기화
                CV.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, CV, null, null)
            }
        }
        return uri
    }
    // 실제 경로로 변경
    fun getPath(uri: Uri?): String {
        val projection = arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        startManagingCursor(cursor)
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }
    // 갤러리 접속
    fun GetAlbum(){
        if(checkPermission(STORAGE, STORAGE_CODE)){
            val intentAlbum = Intent(Intent.ACTION_PICK)
            intentAlbum.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intentAlbum, STORAGE_CODE)
        }
    }
    // 최종
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // xml 요소 변수
        val img = findViewById<ImageView>(R.id.insertImg)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                CAMERA_CODE -> {
                    if(data?.extras?.get("data") != null){
                        val imgData = data?.extras?.get("data") as Bitmap
                        val uri = saveFile(fileName(), "image/jpg", imgData)
                        img.setImageURI(uri)
                        path = getPath(uri)
                        Log.d("확인!!!!!", "이미지 경로 : " + path)
                    }
                }
                STORAGE_CODE -> {
                    val uri = data?.data
                    img.setImageURI(uri)
                    path = getPath(uri)
                    Log.d("확인!!!!!", "이미지 경로 : " + path)
                }
            }
        }
    }
}