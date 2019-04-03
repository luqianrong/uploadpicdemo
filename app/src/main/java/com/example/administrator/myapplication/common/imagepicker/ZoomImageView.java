package com.example.administrator.myapplication.common.imagepicker;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;

/**
 * 缩放图片
 * http://blog.csdn.net/lmj623565791/article/details/39474553
 */
public class ZoomImageView extends AppCompatImageView implements OnScaleGestureListener,
        OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener
  
{  
    private static final String TAG = ZoomImageView.class.getSimpleName();
      
    public static final float SCALE_MAX = 4.0f;  
    /** 
     * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0 
     */  
    private float initScale = 1.0f;  
  
    /** 
     * 用于存放矩阵的9个值 
     */  
    private final float[] matrixValues = new float[9];  
  
    private boolean once = true;  
  
    /** 
     * 缩放的手势检测 
     */  
    private ScaleGestureDetector mScaleGestureDetector = null;
  
    private final Matrix mScaleMatrix = new Matrix();
  
    public ZoomImageView(Context context)
    {  
        this(context, null);  
    }  
  
    public ZoomImageView(Context context, AttributeSet attrs)
    {  
        super(context, attrs);  
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);  
    }  
  
    @Override
    public boolean onScale(ScaleGestureDetector detector)
    {



        float scale = getScale();  
        float scaleFactor = detector.getScaleFactor();  
  
        if (getDrawable() == null)  
            return true;  
  
        /** 
         * 缩放的范围控制 
         */  
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)  
                || (scale > initScale && scaleFactor < 1.0f))  
        {  
            /** 
             * 最大值最小值判断 
             */  
            if (scaleFactor * scale < initScale)  
            {  
                scaleFactor = initScale / scale;  
            }  
            if (scaleFactor * scale > SCALE_MAX)  
            {  
                scaleFactor = SCALE_MAX / scale;  
            }  
            /** 
             * 设置缩放比例 
             */  
            mScaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2,  
                    getHeight() / 2);  
            setImageMatrix(mScaleMatrix);  
        }
        /**
         * 设置缩放比例
         */
        mScaleMatrix.postScale(scaleFactor, scaleFactor,
                detector.getFocusX(), detector.getFocusY());
        checkBorderAndCenterWhenScale();
        setImageMatrix(mScaleMatrix);

        return true;  
  
    }  
  
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector)
    {  
        return true;  
    }  
  
    @Override
    public void onScaleEnd(ScaleGestureDetector detector)
    {  
    }  
  
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {  
        return mScaleGestureDetector.onTouchEvent(event);  
  
    }  
  
      
    /** 
     * 获得当前的缩放比例 
     *  
     * @return 
     */  
    public final float getScale()  
    {  
        mScaleMatrix.getValues(matrixValues);  
        return matrixValues[Matrix.MSCALE_X];
    }  
  
    @Override
    protected void onAttachedToWindow()  
    {  
        super.onAttachedToWindow();  
        getViewTreeObserver().addOnGlobalLayoutListener(this);  
    }  
  
    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow()  
    {  
        super.onDetachedFromWindow();  
        getViewTreeObserver().removeGlobalOnLayoutListener(this);  
    }  
  
    @Override
    public void onGlobalLayout()  
    {  
        if (once)  
        {  
            Drawable d = getDrawable();
            if (d == null)  
                return;  
            Log.e(TAG, d.getIntrinsicWidth() + " , " + d.getIntrinsicHeight());
            int width = getWidth();  
            int height = getHeight();  
            // 拿到图片的宽和高  
            int dw = d.getIntrinsicWidth();  
            int dh = d.getIntrinsicHeight();  
            float scale = 1.0f;  
            // 如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高  
            if (dw > width && dh <= height)  
            {  
                scale = width * 1.0f / dw;  
            }  
            if (dh > height && dw <= width)  
            {  
                scale = height * 1.0f / dh;  
            }  
            // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小  
            if (dw > width && dh > height)  
            {  
                scale = Math.min(dw * 1.0f / width, dh * 1.0f / height);
            }  
            initScale = scale;  
            // 图片移动至屏幕中心  
                        mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);  
            mScaleMatrix  
                    .postScale(scale, scale, getWidth() / 2, getHeight() / 2);  
            setImageMatrix(mScaleMatrix);  
            once = false;  
        }  
  
    }


    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale()
    {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width)
        {
            if (rect.left > 0)
            {
                deltaX = -rect.left;
            }
            if (rect.right < width)
            {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height)
        {
            if (rect.top > 0)
            {
                deltaY = -rect.top;
            }
            if (rect.bottom < height)
            {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width)
        {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height)
        {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        Log.e(TAG, "deltaX = " + deltaX + " , deltaY = " + deltaY);

        mScaleMatrix.postTranslate(deltaX, deltaY);

    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF()
    {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d)
        {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

} 