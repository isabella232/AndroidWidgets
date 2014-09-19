package com.fognl.android.widget.touchmag;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchMagView extends View {
    static final String TAG = TouchMagView.class.getSimpleName();
    
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int CROSSHAIR_COLOR = 0xFFAAAAAA;
    private static final int SHADOW_COLOR = 0xAA000000;
    private static final int RIM_COLOR = 0xFF777777;
    
    public static interface Listener {
        void onTouchEvent(MotionEvent event);
    }
    
    private final ArrayList<View> mViews = new ArrayList<View>();
    private PointF mMagPoint = null;
    private Shader mMagShader = null;
    private Paint mMagPaint = null;
    private Paint mRimPaint = null;
    private Paint mCrosshairPaint = null;
    private Paint mTextPaint = null;
    private Bitmap mMagBitmap = null;
    private Matrix mMatrix = new Matrix();
    private boolean mEchoEvents = false;
    private float mZoom = 2f;
    private int mXOffset = 0;
    private int mYOffset = 0;
    private int mLoupeSize = 200;
    private boolean mCrosshair = false;
    private boolean mShowText = false;
    
    private Listener mListener;
    private boolean mEnabled = true;
    private boolean mPendingStart = false;

    public TouchMagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TouchMagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TouchMagView(Context context) {
        super(context);
        init(context);
    }
    
    public TouchMagView setListener(Listener listener) {
        mListener = listener;
        return this;
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        if(w > 0 && h > 0 && mPendingStart) {
            startMagnifier();
            mPendingStart = false;
        }
    }
    
    public TouchMagView addView(View view) {
        mViews.add(view);
        return this;
    }
    
    public TouchMagView clearViews() {
        mViews.clear();
        return this;
    }
    
    public void startMagnifying() {
        if(mMagBitmap != null) {
            try {
                mMagBitmap.recycle();
            }
            catch(Throwable ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
        
        int width = getWidth();
        int height = getHeight();
        
        if(width == 0 || height == 0) {
            mPendingStart = true;
        }
        else {
            startMagnifier();
        }
    }
    
    public void stopMagnifying() {
        if(mMagBitmap != null) {
            try {
                mMagBitmap.recycle();
            }
            catch(Throwable ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
  
        mMagBitmap = null;
        mMagShader = null;
        mMagPaint = null;
    }
    
    public boolean isMagnifying() {
        return mMagBitmap != null;
    }
    
    public float getZoom() { return mZoom; }
    public TouchMagView setZoom(float z) { mZoom = z; return this; }
    
    public int getLoupeSize() { return mLoupeSize; }
    public TouchMagView setLoupeSize(int size) { mLoupeSize = size; return this; }
    
    public boolean showsText() { return mShowText; }
    public TouchMagView setShowText(boolean b) { mShowText = b; return this; }
    
    public boolean hasCrosshair() { return mCrosshair; }
    public TouchMagView setCrosshair(boolean c) { mCrosshair = c; return this; }
    
    public boolean echoesEvents() { return mEchoEvents; }
    public TouchMagView echoEvents(boolean b) { mEchoEvents = b; return this; }
    
    public int getRimColor() { return mRimPaint.getColor(); }
    public TouchMagView setRimColor(int clr) { 
        mRimPaint.setColor(clr); 
        return this; 
    }
    
    public int getCrosshairColor() { return mCrosshairPaint.getColor(); }
    public TouchMagView setCrosshairColor(int clr) { 
        mCrosshairPaint.setColor(clr); 
        return this; 
    }
    
    public int getTextColor() { return mTextPaint.getColor(); }
    public TouchMagView setTextColor(int clr) { 
        mTextPaint.setColor(clr); 
        return this; 
    }
    
    public int getXOffset() { return mXOffset; }
    public int getYOffset() { return mYOffset; }
    public TouchMagView setXYOffsets(int x, int y) {
        mXOffset = x;
        mYOffset = y;
        return this;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
        super.setEnabled(enabled);
    }
    
    @SuppressLint("DefaultLocale")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if(mMagPoint != null) {
            Shader shader = mMagShader;
            if(shader != null) {
                mMatrix.reset();
                mMatrix.postScale(mZoom, mZoom, mMagPoint.x, mMagPoint.y);
                mMatrix.postTranslate(mXOffset, mYOffset);
                shader.setLocalMatrix(mMatrix);
                
                float x = (mMagPoint.x + mXOffset);
                float y = (mMagPoint.y + mYOffset);
                
                canvas.drawCircle(x, y, mLoupeSize, mMagPaint);
                canvas.drawCircle(x, y, mLoupeSize, mRimPaint);
                
                if(mShowText) {
                    String text = String.format("%.0f,%.0f", mMagPoint.x, mMagPoint.y);
                    float tw = mTextPaint.measureText(text);
                    canvas.drawText(text, x - (tw / 2), y - (mLoupeSize / 4), mTextPaint);
                }
                
                if(mCrosshair) {
                    canvas.drawCircle(x, y, 20f, mCrosshairPaint);
                    canvas.drawLine(x-10, y, x+10, y, mCrosshairPaint);
                    canvas.drawLine(x, y-10, x, y+10, mCrosshairPaint);
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        if(mEnabled) {
            float x = event.getX();
            float y = event.getY();
            
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mMagPoint = new PointF(x, y);
                    break;
                }
                
                case MotionEvent.ACTION_MOVE: {
                    
                    if(mMagPoint != null) {
                        mMagPoint.set(x, y);
                    }
                    
                    postInvalidate();
                    break;
                }
                
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    mMagPoint = null;
                    postInvalidate();
                    break;
                }
            }
            
            if(mEchoEvents && mListener != null) {
                mListener.onTouchEvent(event);
            }
            
            return true;
        }
        else {
            return super.onTouchEvent(event);
        }
    }

    private void init(Context c) {
        this.setLayerType(LAYER_TYPE_HARDWARE, null);
        
        Paint p = new Paint();
        p.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        p.setColor(RIM_COLOR);
        p.setShadowLayer(8f, 4f, 4f, SHADOW_COLOR);
        p.setStrokeWidth(6f);
        p.setStyle(Paint.Style.STROKE);
        mRimPaint = p;
        
        p = new Paint();
        p.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        p.setColor(CROSSHAIR_COLOR);
        p.setShadowLayer(8f, 4f, 4f, 0xAA000000);
        p.setStrokeWidth(6f);
        p.setStyle(Paint.Style.STROKE);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        mCrosshairPaint = p;
        
        p = new Paint();
        p.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        p.setColor(TEXT_COLOR);
        p.setStrokeWidth(1f);
        p.setTextSize(26f);
        p.setStyle(Paint.Style.STROKE);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        mTextPaint = p;
    }
    
    private void startMagnifier() {
        mMagBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mMagBitmap);
        
        for(View v: mViews) {
            v.draw(canvas);
        }
        
        mMagShader = new BitmapShader(mMagBitmap, TileMode.CLAMP, TileMode.CLAMP);
        mMagPaint = new Paint();
        mMagPaint.setShader(mMagShader);
    }
}
