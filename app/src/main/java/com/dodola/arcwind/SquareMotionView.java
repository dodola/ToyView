
package com.dodola.arcwind;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

public class SquareMotionView extends SurfaceView implements SurfaceHolder.Callback {
    Paint linePaint = new Paint();
    ValueAnimator valueAnimator;
    private float sweepAngle = 90;
    SurfaceHolder mHolder;

    public void setSweepAngle(float angle) {
        sweepAngle = angle;
        invalidate();
    }


    public SquareMotionView(Context context) {
        super(context);
        initPaint();
    }

    public SquareMotionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SquareMotionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private void initPaint() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);

        linePaint.setAntiAlias(true);
        linePaint.setColor(0xff05225C);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeWidth(20);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        startAnimation();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    int animatedValue;

    private void startAnimation() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofInt(360);
            valueAnimator.setDuration(6000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    if (animatedValue != (int) animation.getAnimatedValue()) {
                        animatedValue = (int) animation.getAnimatedValue();
                        drawMe();
                    }
                }
            });
            valueAnimator.setRepeatCount(-1);
            valueAnimator.start();
        }
    }

    RectF rectF = new RectF();

    private void drawMe() {
        int centerX = getWidth() >> 1;
        int centerY = getHeight() >> 1;
        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas(null);//锁整个画布
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕

            for (int r = 10; r < centerX; r += 30) {
                canvas.rotate(animatedValue, centerX, centerY);
                linePaint.setStrokeWidth(r / 20);
                rectF.top = centerX - r;
                rectF.left = centerY - r;
                rectF.right = centerX + r;
                rectF.bottom = centerY + r;
                canvas.drawArc(rectF, 0, sweepAngle, false, linePaint);
            }
        } catch (Exception ex) {
        } finally {
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }


}
