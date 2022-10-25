package com.example.cpp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.cpp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = SumApi.subtraction(4321,1234).toString()//stringFromJNI()
    }

    /**
     * A native method that is implemented by the 'cpp' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
//    external fun sum(one: Int,tow:Int):Int

    companion object {
        // Used to load the 'cpp' library on application startup.

        @JvmStatic
        external fun sum2(one: Int,tow:Int):Int

        init {
            System.loadLibrary("cpp")
            System.loadLibrary("msz")
        }
    }



}