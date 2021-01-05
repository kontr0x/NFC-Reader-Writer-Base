package de.kontr0x.nfcreaderwriterbase.nfc

import android.content.Intent
import android.nfc.*
import android.nfc.tech.Ndef
import android.util.Log
import android.widget.TextView
import java.nio.charset.Charset

class MyMifareUltralightTagTester {

    fun writeTag(tag: Tag, data: NdefMessage) {
        var currentTag: Ndef = Ndef.get(tag)
        try {
            currentTag.connect()
            currentTag.writeNdefMessage(data)
            currentTag.close()
        }catch(e: TagLostException){
            Log.e("NfcWriter", "Tag lost connection")
        }catch(e: FormatException){
            Log.e("NfcWriter", "Ndef message is malformed $data")
        }
    }

    fun readTag(intent: Intent, tag: Tag, tv: TextView) {
        val currentTag: Ndef = Ndef.get(tag)
        currentTag.connect()
        val messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (messages != null) {
            messages.map { message -> message as NdefMessage }
            val record = (messages[0] as NdefMessage).records[0]
            val payload: ByteArray = record.payload
            currentTag.close()
            tv.text = payload.toString(Charset.defaultCharset())
        }
    }
}

