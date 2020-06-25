package com.kamitsoft.ecosante.nfc;

import android.content.Intent;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.os.Parcelable;

import java.io.IOException;
import java.nio.charset.Charset;

public class NfcMethod {
    public final String VENDOR = "com.kamitsoft.dmi";

    public NfcMethod()
    {

    }

    public void write(String text, Tag tag) throws IOException, FormatException {

        NdefMessage message = createNdfCode(text) ;
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }
    private NdefMessage createNdfCode(String text) {

        NdefRecord[] data = new NdefRecord[]{
                NdefRecord.createExternal(VENDOR,"uuid",text.getBytes(Charset.forName("US-ASCII"))), //createTextRecord("US-ASCII", text),
                NdefRecord.createApplicationRecord(VENDOR)
        };
       return new NdefMessage(data);


    }

    public String readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            return new String(msgs[0].getRecords()[0].getPayload());

        }
        return "";
   }



}
