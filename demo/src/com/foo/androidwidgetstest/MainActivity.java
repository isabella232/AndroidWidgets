package com.foo.androidwidgetstest;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.fognl.android.widget.touchmag.TouchMagView;

public class MainActivity extends Activity {
    static final String TAG = MainActivity.class.getSimpleName();
    
    private TouchMagView mMagView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mMagView = (TouchMagView)findViewById(R.id.magnifier);
        mMagView.addView(findViewById(R.id.img))
            .setLoupeSize(200)
            .setZoom(2f)
            .setXYOffsets(0, -200)
            .setListener(new TouchMagView.Listener() {
                @Override
                public void onTouchEvent(MotionEvent event) {
                    // Pass the event through to the relevant views.
                    findViewById(R.id.img).dispatchTouchEvent(event);
                }
            });
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
