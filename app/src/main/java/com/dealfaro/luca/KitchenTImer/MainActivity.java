package com.dealfaro.luca.KitchenTImer;

import android.os.CountDownTimer;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    static final private String LOG_TAG = "test2017app1";

    // Counter for the number of seconds.
    private int seconds = 0;

    // Countdown timer.
    private CountDownTimer timer = null;

    // One second.  We use Mickey Mouse time.
    private static final int ONE_SECOND_IN_MILLIS = 1000;

    // Bool var to check if plus/minus buttons have been clicked
    private boolean clicked = false;

    // Button[] containing recent time buttons
    private static final int[] buttonIds = {R.id.temp1, R.id.temp2, R.id.temp3};
    private int current = 0; // current counter cycling thru buttonIds[]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayTime();
    }

    public void onClickPlus(View v) {
        seconds += 60;
        clicked = true;
        displayTime();
    };

    public void onClickMinus(View v) {
        seconds = Math.max(0, seconds - 60);
        clicked = true;
        displayTime();
    };

    public void onReset(View v) {
        seconds = 0;
        cancelTimer();
        displayTime();
    }

    public void onClickStart(View v) {
        if (seconds == 0) {
            cancelTimer();
        }
        if (timer == null) {
            // We create a new timer.
            timer = new CountDownTimer(seconds * ONE_SECOND_IN_MILLIS, ONE_SECOND_IN_MILLIS) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //Log.d(LOG_TAG, "Tick at " + millisUntilFinished);
                    seconds = Math.max(0, seconds - 1);
                    displayTime();
                }

                @Override
                public void onFinish() {
                    seconds = 0;
                    timer = null;
                    displayTime();
                }
            };
            timer.start();

            // Check if PLUS/MINUS button has been pressed beforehand
            if (clicked){
                if (current == 3) current = 0; // counter cycles thru the 3 recent time buttons
                TextView vee = (TextView) findViewById(buttonIds[current]);
                Log.d(LOG_TAG, "isIdentical? " + isIdentical(seconds));
                if (!isIdentical(seconds)) { // if time is not identical
                    vee.setText(TimeNice(seconds));
                    current++;
                }
            }

        }

        // reset clicked bool var to prevent
        // resuming/restarting from updating the recent buttons
        clicked = false;

    }

    public void onClickStop(View v) {
        cancelTimer();
        displayTime();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // Updates the time display.
    private void displayTime() {
        //Log.d(LOG_TAG, "Displaying time " + seconds);
        TextView v = (TextView) findViewById(R.id.display);
        int m = seconds / 60;
        int s = seconds % 60;
        v.setText(String.format("%d:%02d", m, s));
        // Manages the buttons.
        Button stopButton = (Button) findViewById(R.id.button_stop);
        Button startButton = (Button) findViewById(R.id.button_start);
        startButton.setEnabled(timer == null && seconds > 0);
        stopButton.setEnabled(timer != null && seconds > 0);
    }

    // Converts (int)seconds to a nice time String format display
    public String TimeNice(int sec){
        String str = String.format("%d:%02d", sec / 60, sec % 60);
        return str;
    }

    // Converts (string)mm:ss to (int)seconds
    public int convertToSeconds(String str){
        String[] parts = str.split(":");
        String minStr = parts[0];
        String secStr = parts[1];

        // Convert to Int type minutes and seconds
        int min = Integer.parseInt(minStr);
        int sec = Integer.parseInt(secStr);

        return min * 60 + sec;
    }

    // Get time from Button
    public String getTime(View v){
        TextView vText = (TextView) v;
        String str = vText.getText().toString();
        return str;
    }

    // Set time from Button press
    public void setTime(View v){
        String str = getTime(v);

        if (!str.equals("")) {
            seconds = convertToSeconds(str);
            displayTime();
            onClickStart(v);
        }
    }

    // Check if time is already stored in one of the buttons
    public boolean isIdentical(int sec){
        boolean returnVal = false;

        // Loop through the recent time buttons
        for (int i = 0; i < buttonIds.length; i++){
            // pass in the button view
            View v = findViewById(buttonIds[i]);
            // get the time stored in the button
            String buttonTime = getTime(v);

            // if time stored in button is not empty, proceed
            if (!buttonTime.equals("")) {
                // convert time stored in button to seconds for comparison
                int buttonTimeSeconds = convertToSeconds(buttonTime);

                // compare the time being set by user with time stored in the current button
                if (sec == buttonTimeSeconds) {
                    returnVal = true;
                }
            }
        }
        return returnVal;
    }
}
