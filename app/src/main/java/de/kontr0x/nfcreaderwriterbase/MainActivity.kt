package de.kontr0x.nfcreaderwriterbase

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import de.kontr0x.nfcreaderwriterbase.nfc.MyMifareUltralightTagTester

class MainActivity : AppCompatActivity() {

    //Setting nfc action
    private val doWrite = false

    private lateinit var adapter: NfcAdapter
    private lateinit var myMifareRW: MyMifareUltralightTagTester
    private lateinit var resultText: TextView

    lateinit var intentFiltersArray: Array<IntentFilter>
    lateinit var pendingIntent: PendingIntent
    private val techListsArray = arrayOf(arrayOf<String>(NfcF::class.java.name))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()

        myMifareRW = MyMifareUltralightTagTester()
        adapter = NfcAdapter.getDefaultAdapter(this)

        //Checking if nfc is enabled on the device
        if (!adapter?.isEnabled!!) {
            Toast.makeText(this,"NFC disabled on this device.", Toast.LENGTH_SHORT).show()
        }

        //Prevent default nfc discover page from android to appear
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val filters = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }
        intentFiltersArray = arrayOf(filters)
    }

    private fun initViews(){
        resultText = findViewById(R.id.resultTextView)
    }

    public override fun onPause() {
        super.onPause()
        adapter.disableForegroundDispatch(this)
    }

    public override fun onResume() {
        super.onResume()
        adapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        var tagFromIntent: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if(tagFromIntent != null){
            //Since this is only a base to work with you need to hardcode if you want to read or write
            if (doWrite) {
                val data = "SomeData"
                val defRecord: NdefRecord = NdefRecord.createMime(data, data.toByteArray())
                val defMessage = NdefMessage(arrayOf(defRecord))
                myMifareRW.writeTag(tagFromIntent, defMessage)
                Toast.makeText(this, "write successful", Toast.LENGTH_SHORT).show()
            }
            else{
                myMifareRW.readTag(intent ,tagFromIntent, resultText)
            }
        }
    }

}