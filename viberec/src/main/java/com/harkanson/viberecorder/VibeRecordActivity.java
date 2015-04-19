package com.harkanson.viberecorder;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

public class VibeRecordActivity extends Activity {
    private final static long FOREVER = 123456789;

    private boolean firstPush;

    private long cycleStartTime;
    private long milliesDown;
    private long milliesUp;

    private Vibrator v;

    @InjectView(R.id.etPattern) EditText etPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibe_record);
        ButterKnife.inject(this);

        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        firstPush = true;
        cycleStartTime = milliesDown = milliesUp = 0;
    }

    @OnTouch(R.id.btnVibeTap)
    public boolean onTapButtonTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tapDown();
                return true;
            case MotionEvent.ACTION_UP:
                tapUp();
                return true;
        }
        return false;
    }

    @OnClick(R.id.btnGo)
    public void onGoButtonClick() {
        //Send vibrate pattern back to caller
        //Intent returnVibePattern = new Intent();
        //returnVibePattern.putExtra("Vibrate Pattern", etPattern.getText());
        //setResult(RESULT_OK);

        //Copy vibrate pattern to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Vibrate Pattern", etPattern.getText());
        clipboard.setPrimaryClip(clip);

        //Exit
        finish();
    }

    public void tapDown() {
        v.vibrate(FOREVER);

        //If it is not the first push, calculate how long the button has been up
        if(!firstPush)
            milliesUp = System.currentTimeMillis() - cycleStartTime - milliesDown;

        //Start a new cycle
        cycleStartTime = System.currentTimeMillis();
    }

    public void tapUp() {
        v.cancel();

        //Calculate how long the button has been down
        milliesDown = System.currentTimeMillis() - cycleStartTime;

        //Append the appropriate times and commas
        StringBuilder sb = new StringBuilder(etPattern.getText());
        if(!firstPush) {
            sb.append(",");
        }
        firstPush = false;
        sb.append(milliesUp).append(",").append(milliesDown);

        //Update the EditText
        etPattern.setText(sb.toString());
    }

}
