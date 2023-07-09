package com.yong.wesave;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import com.yong.wesave.asyntask.AsyncSendMail;
import com.yong.wesave.common.Constants;

import static com.yong.wesave.util.Validation.validateEmail;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class SendFeedbackActivity extends WeSaveActivity implements View.OnClickListener {

    public RatingBar ratingBar;
    Button submitBtn, backBtn;
    EditText feedbackText, feedbackEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_send_feedback, R.id.content_frame);
        super.replaceTitle(R.string.title_feedback);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        submitBtn = (Button) findViewById(R.id.submit);
        backBtn = (Button) findViewById(R.id.back);
        feedbackText = (EditText) findViewById(R.id.feedback);
        feedbackEmail = (EditText) findViewById(R.id.feedback_email);

        submitBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        feedbackEmail.setText(super.email);
    }

    @Override
    public void onClick(View v) {

        if (v == submitBtn) {
            if (!validateEmail(feedbackEmail.getText().toString())) {
                feedbackEmail.setError("Please enter a valid email!\n");
            } else {
                sendEmail();
            }
        } else if (v == backBtn) {
            finish();
        }
    }

    /**
     * Display rating by calling getRating() method.
     *
     * @param view
     */
    public void rateMe(View view) {

        Toast.makeText(getApplicationContext(),
                String.valueOf(ratingBar.getRating()), Toast.LENGTH_LONG).show();
    }

    public void sendEmail() {

        String emailbody = "Hi,\n\nRating value: " + ratingBar.getRating() + "\n\nSender Message: " + feedbackText.getText() + "\n\nSender email address: " + feedbackEmail.getText() + "\n\nRegards,\nWeSave Team";
        String emailsubject = "App Feedback";
        String emailsender = Constants.EMAIL;
        String emailrecipient = Constants.EMAIL;
        try {
//            AsyncTask task = new AsyncSendMail();
//            task.execute(new String[]{emailsubject, emailbody, emailsender, emailrecipient});
//            finish();

            Intent mailIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:?subject=" + emailsubject+ "&body=" + emailbody + "&to=" + emailrecipient);
            mailIntent.setData(data);
            startActivity(Intent.createChooser(mailIntent, "Send mail..."));


        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }
}
