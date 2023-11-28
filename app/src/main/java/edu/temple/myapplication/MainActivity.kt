package edu.temple.myapplication

import android.content.AsyncQueryHandler
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    lateinit var timerTextView: TextView

    lateinit var timerBinder : TimerService.TimerBinder
    var isConnected = false

    val timeHandler = Handler(Looper.getMainLooper()){
        timerTextView.text = it.what.toString()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.textView)



        val serviceConnection = object : ServiceConnection{

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                timerBinder = service as TimerService.TimerBinder
                timerBinder.setHandler(timeHandler)
                isConnected = true

            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isConnected = false
            }
        }

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        findViewById<Button>(R.id.startButton).setOnClickListener {
            if(isConnected) timerBinder.start(100)
        }

        findViewById<Button>(R.id.pauseButton).setOnClickListener {
            if(isConnected) timerBinder.pause()
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if(isConnected) timerBinder.stop()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_start -> timerBinder.start(100)
            R.id.action_pause -> timerBinder.pause()
            R.id.action_stop -> timerBinder.stop()
        }

        return super.onOptionsItemSelected(item)
    }
}