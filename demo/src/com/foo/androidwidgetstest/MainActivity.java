package com.foo.androidwidgetstest;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.fognl.android.widget.touchmag.TouchMagView;

public class MainActivity extends Activity {
    static final String TAG = MainActivity.class.getSimpleName();
    
    private TouchMagView mMagView;
    
    private final TouchMagView.Listener mMagListener = new TouchMagView.Listener() {
        @Override
        public void onTouchEvent(MotionEvent event) {
            // Pass the event through to the relevant views.
            findViewById(R.id.img).dispatchTouchEvent(event);
        }
        
        @Override
        public String onTextUpdate(float x, float y) {
            return String.format(Locale.getDefault(), "%.0f,%.0f", x, y);
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mMagView = (TouchMagView)findViewById(R.id.magnifier);
        mMagView.addView(findViewById(R.id.main_layout))
            .setLoupeSize(200)
            .setZoom(2f)
            .setXYOffsets(0, -200)
            .setShowText(true)
            .setShowCrosshair(true)
            .setListener(mMagListener)
            ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMagView.startMagnifying();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMagView.stopMagnifying();
    }
}
