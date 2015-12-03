/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.dodola.arcwind;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.animation.LinearInterpolator;

public class SimpleTextureView extends TextureView implements TextureView.SurfaceTextureListener {


    private Paint fillPaint = new Paint();
    private Paint storkPaint = new Paint();
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private ValueAnimator valueAnimator;
    private int animatedValue;
    private float frab;
    private int fcount;

    public SimpleTextureView(Context context) {
        super(context);
        init();
    }

    public SimpleTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        setOpaque(false);
        setSurfaceTextureListener(this);
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setStrokeCap(Paint.Cap.ROUND);
        fillPaint.setStrokeWidth(20);

        storkPaint.setAntiAlias(true);
        storkPaint.setColor(0XFF007DFF);
        storkPaint.setStyle(Paint.Style.STROKE);
        storkPaint.setStrokeCap(Paint.Cap.ROUND);
        storkPaint.setStrokeWidth(20);


    }

    private void startAnimation() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofInt(60);
            valueAnimator.setDuration(1000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    frab = animation.getAnimatedFraction();
                    if (animatedValue != (int) animation.getAnimatedValue()) {
                        animatedValue = (int) animation.getAnimatedValue();
                        drawMe(getWidth(), getHeight());
                        fcount+=2;
                    }
                }
            });
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            valueAnimator.setRepeatCount(-1);
            valueAnimator.start();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startAnimation();

    }

    private void drawMe(int width, int height) {

        int space = width / 11;
        Canvas canvas = lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.translate(width >> 1, height >> 1);
        for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 4; y++) {
                canvas.save();
                canvas.rotate((float) Math.sin(Math.toRadians((y * x) * 5 + fcount )) * 60, x * space + space, y * space + space);
                storkPaint.setStrokeWidth((float) (4 + Math.sin(Math.toRadians((y * x) * 5 + fcount )) * 4));
//                storkPaint.setColor((Integer) argbEvaluator.evaluate(frab, 0x7d007dff, 0x7dff007d));
                canvas.drawCircle(x * space, y * space, 15, fillPaint);
                canvas.drawCircle(x * space, y * space, 15, storkPaint);
                canvas.restore();
            }
        }

        unlockCanvasAndPost(canvas);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}