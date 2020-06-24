package com.kamitsoft.ecosante.nfcPackage;

import android.content.Intent;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class NfcMethod {

    public NfcMethod()
    {

    }

    public void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }
    private NdefRecord createRecord(String text) {

        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA ,
                "application/vnd.com.kamisoft.dmi".getBytes(Charset.forName("US-ASCII")),
                new byte[0], text.getBytes(Charset.forName("US-ASCII")));

        return mimeRecord;
    }

    public String readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Parcelable rawMsgs = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage[] msgs = null;
        /*    if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }*/
            }
            return "";//buildTagViews(msgs);
        }
        //return "";
   // }
    private String buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return "";

        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        return text;
    }
}
