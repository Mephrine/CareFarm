package kr.smart.carefarm.lib

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import kr.smart.carefarm.R
import kr.smart.carefarm.config.C
import java.util.*

class Tab1 : Activity() {
    var ip: EditText? = null
    var webport: EditText? = null
    var videoport: EditText? = null
    var channel: EditText? = null
    var id: EditText? = null
    var pw: EditText? = null
    var save: Button? = null
    var print: Button? = null
    var show: TextView? = null
    var spinner: Spinner? = null
    var pro: String? = null
    var list = ArrayList<String>()
    var text = ArrayList<Any?>()
    fun parse_response() {
        val client = AsyncHttpClient()
        client.setBasicAuth(id!!.text.toString(), pw!!.text.toString())
        val IPadress = "http://" + ip!!.text.toString() + ":" + webport!!.text.toString() + "/vb.htm?getchannels"
        client[IPadress, object : AsyncHttpResponseHandler() {
            override fun onSuccess(response: String) {
                show = findViewById<View>(R.id.show) as TextView
                show!!.movementMethod = ScrollingMovementMethod.getInstance()
                val str = response.split("=").toTypedArray()
                val str1 = str[1].split(",").toTypedArray()
                var result = String()
                for (i in str1.indices) {
                    result += (i + 1).toString() + ". " + str1[i] + "\n"
                }
                show!!.text = result
            }

            override fun onFailure(e: Throwable, respose: String) {
                Toast.makeText(applicationContext, "Fail", Toast.LENGTH_LONG).show()
            }
        }]
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var layout = R.layout.tab3
        layout = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            R.layout.tab3_2
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            R.layout.tab3
        } else {
            R.layout.tab3
        }
        setContentView(layout)
        ip = findViewById<View>(R.id.iptxt) as EditText
        webport = findViewById<View>(R.id.webporttxt) as EditText
        videoport = findViewById<View>(R.id.videoporttxt) as EditText
        id = findViewById<View>(R.id.idtxt) as EditText
        pw = findViewById<View>(R.id.pwtxt) as EditText
        channel = findViewById<View>(R.id.channeltxt) as EditText
        //test
        ip!!.setText("172.31.77.160")
        webport!!.setText("80")
        videoport!!.setText("9001")
        id!!.setText("admin")
        pw!!.setText("9999")
        channel!!.setText("1")
        save = findViewById<View>(R.id.btnsave) as Button
        print = findViewById<View>(R.id.btnprint) as Button
        spinner = findViewById<View>(R.id.protocolspin) as Spinner
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                pro = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        text.add(ip)
        text.add(webport)
        text.add(videoport)
        text.add(id)
        text.add(pw)
        text.add(channel)
        list.add(FILE_IP)
        list.add(FILE_WEBPORT)
        list.add(FILE_VIDEOPORT)
        list.add(FILE_ID)
        list.add(FILE_PW)
        list.add(FILE_CHANNEL)
        save!!.setOnClickListener {
            try {
                C.ip = ip!!.text.toString()
                C.webport = webport!!.text.toString()
                C.videoport = videoport!!.text.toString()
                C.id = id!!.text.toString()
                C.pw = pw!!.text.toString()
                C.channel = channel!!.text.toString()
            } catch (e: Exception) {
            }
        }
        print!!.setOnClickListener { parse_response() }
    }

    companion object {
        const val FILE_IP = "ip.txt"
        const val FILE_WEBPORT = "webport.txt"
        const val FILE_VIDEOPORT = "videoport.txt"
        const val FILE_ID = "id.txt"
        const val FILE_PW = "pw.txt"
        const val FILE_CHANNEL = "channel.txt"
    }
}