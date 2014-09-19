Android Widgets
==============

Various Android widgets
--------------


This is a library I hope to add to over time, a place to keep custom views that turn out useful.



TouchMagView
============
This view is intended to allow touch magnification of a view under it. To use it, I usually put it at the top of the z-order in a RelativeLayout, like this:

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        >
        <ImageView
            android:id="@+id/some_img"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:src="@drawable/some_image"
            />
            
        <com.fognl.android.widget.TouchMagView
            android:id="@+id/mag"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            />
    </RelativeLayout>

Then, in onCreate() of the Activity, do something like this:

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

You can specify when to start and stop magnifying. For example:

    public void onStartMagClick(View v) {
        mMagView.startMagnifying();
    }
    
    public void onStopMagClick(View v) {
        mMagView.stopMagnifying();
    }
    
There are several options for controlling loupe size, magnification, various colors (rim, text, crosshair), whether or not to show a crosshair or text, etc.

Project Structure

The library/ directory contains the Android library you'll (perhaps) use in your project. The demo/ directory contains an app that shows it in action.

Have fun.


