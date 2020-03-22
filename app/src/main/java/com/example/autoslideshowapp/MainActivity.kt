package com.example.autoslideshowapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.util.Log
import android.os.Build
import android.provider.MediaStore
import java.util.*
import android.os.Handler
import android.support.design.widget.Snackbar

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 100
    private var cursor : Cursor? = null
    private var mTimer : Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //パーミッション取得
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Log.d("testtest", "許可されている")
                getCursor() //許可されていたらカーソル取得

            }else{
                Log.d("testtest", "許可されていない")
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
                //許可されていなかったらパーミッションをリクエスト
            }
        }else{
            getCursor() //許可されていたらカーソル取得
        }

        //次へボタン
        next_button.setOnClickListener {
            nextImage()
        }

        //戻るボタン
        back_button.setOnClickListener{
            if(cursor!!.moveToPrevious()) {
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView1.setImageURI(imageUri)
                Log.d("testtest", "URI : " + imageUri.toString())
            }else if(cursor!!.moveToLast()){
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView1.setImageURI(imageUri)
                Log.d("testtest", "URI : " + imageUri.toString())
            }
        }

        //再生ボタン、停止ボタン
        startstop_button.setOnClickListener{
            if(mTimer == null){
                startstop_button.text = "停止"
                next_button.isClickable = false
                back_button.isClickable = false

                mTimer = Timer()

                mTimer!!.schedule(object : TimerTask(){
                    override fun run(){
                        mHandler.post{
                            nextImage()
                        }

                    }
                },2000, 2000)
            }else{
                startstop_button.text = "再生"
                next_button.isClickable = true
                back_button.isClickable = true

                mTimer!!.cancel()
                mTimer = null
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSIONS_REQUEST_CODE ->
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("testtest", "許可された")
                    //許可されたら、Granted
                    getCursor()
                }else{
                    Log.d("testtest", "許可されなかった")
                    //許可されなかった場合Snackbarで警告を出し、再度パーミッションをとる

                    next_button.isClickable = false
                    back_button.isClickable = false
                    startstop_button.isClickable = false

                    activity_main.setOnClickListener{ view ->
                        Snackbar.make(view, "ストレージへのアクセスを許可してください", Snackbar.LENGTH_INDEFINITE)
                            .setAction("許可する") {
                                Log.d("testtest", "Snackbarをタップした")
                                recreate() //再描画してふりだしに戻る
                            }.show()
                    }

                    }
        }
    }

    //カーソルを取得
    private fun getCursor() {
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        Log.d("testtest", "カーソルは " + cursor.toString())

        //カーソルの先頭を取得して画像を描画
        if(cursor!!.moveToFirst()){
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView1.setImageURI(imageUri)
            Log.d("testtest", "URI : " + imageUri.toString())
        }
    }

    //進む
    private fun nextImage(){
        if(cursor!!.moveToNext()) {
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView1.setImageURI(imageUri)
            Log.d("testtest", "URI : " + imageUri.toString())

        }else if(cursor!!.moveToFirst()){
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView1.setImageURI(imageUri)
            Log.d("testtest", "URI : " + imageUri.toString())
        }
    }
}
