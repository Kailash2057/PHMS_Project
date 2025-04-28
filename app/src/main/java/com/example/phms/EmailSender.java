package com.example.phms;

import android.content.Context;
import android.content.Intent;

public class EmailSender {

    private Context context;

    public EmailSender(Context context) {
        this.context = context;
    }

    public void sendEmail(String emailAddress, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, "Send Email"));
    }
}
