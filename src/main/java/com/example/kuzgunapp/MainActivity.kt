package com.example.kuzgunapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.view.drawToBitmap

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {

    lateinit var cameraButton   : Button
    lateinit var imageViewPhoto : ImageView

    lateinit var textView_Red   : TextView
    lateinit var textView_Green : TextView
    lateinit var textView_Blue  : TextView

    lateinit var textView_Hue : TextView
    lateinit var textView_Sat : TextView
    lateinit var textView_Val : TextView

    var cameraReq = 1001

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing the objects
        cameraButton   = findViewById(R.id.button_camera)
        imageViewPhoto = findViewById(R.id.imageView_photo)

        textView_Red = findViewById(R.id.textView_Red)
        textView_Green = findViewById(R.id.textView_Green)
        textView_Blue = findViewById(R.id.textView_Blue)
        textView_Hue = findViewById(R.id.textView_Hue)
        textView_Sat = findViewById(R.id.textView_Sat)
        textView_Val = findViewById(R.id.textView_Val)


        // Adding listener to the objects
        cameraButton.setOnClickListener(this)
        imageViewPhoto.setOnTouchListener(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == cameraReq)
            imageViewPhoto.setImageBitmap(data?.extras?.get("data") as Bitmap)
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.button_camera) {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, cameraReq)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(v?.id == R.id.imageView_photo) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    // x and y locations of touched point
                    val x = event.x.toInt()
                    val y = event.y.toInt()
                    // Drawing view into a bitmap to get RGB Values
                    val bitmap = v.drawToBitmap()
                    // Getting the pixel value
                    val pixel = bitmap.getPixel(x,y)
                    // RGB Values
                    val red   = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue  = Color.blue(pixel)
                    // Converting RGB values into HSV
                    val hsv = rgb2hsv(red, green, blue)
                    // HSV Values
                    val hue   = hsv[0]
                    val sat   = hsv[1]
                    val value = hsv[2]
                    // Changing textViews - RGB
                    textView_Red.text   = red.toString()
                    textView_Green.text = green.toString()
                    textView_Blue.text  = blue.toString()
                    // Changing textViews - HSV
                    textView_Hue.text = hue.toInt().toString()
                    textView_Sat.text = sat.toInt().toString()
                    textView_Val.text = value.toInt().toString()
                }
            }
        }
        return v?.onTouchEvent(event) ?: true
    }

    private fun rgb2hsv(red: Int, green: Int, blue: Int): DoubleArray {
        val rOver255 = red.toFloat()   / 255.0
        val gOver255 = green.toFloat() / 255.0
        val bOver255 = blue.toFloat()  / 255.0

        val cMax = maxOf(rOver255, gOver255, bOver255)
        val cMin = minOf(rOver255, gOver255, bOver255)
        val diff = cMax - cMin

        var hue   : Double
        var sat   : Double
        var value : Double

        // Hue calculation
        if(cMax.equals(0.0) && cMin.equals(0.0))
            hue = 0.0
        else if(cMax.equals(rOver255))
            hue = (60 * ((gOver255-bOver255)/diff) + 360) % 360
        else if(cMax.equals(gOver255))
            hue = (60 * ((bOver255-rOver255)/diff) + 120) % 360
        else
            hue = (60 * ((rOver255-gOver255)/diff) + 240) % 360


        // Saturation calculation
        if(cMax.equals(0.0))
            sat = 0.0
        else
            sat = (diff/cMax)*100

        // Value calculation
        value = cMax * 100

        return doubleArrayOf(hue, sat, value)
    }
}