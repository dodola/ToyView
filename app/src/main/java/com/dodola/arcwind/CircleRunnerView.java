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
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.animation.LinearInterpolator;

import java.util.Random;

public class CircleRunnerView extends TextureView implements TextureView.SurfaceTextureListener {


    private Paint fillPaint = new Paint();
    private Paint storkPaint = new Paint();
    private ValueAnimator valueAnimator;
    private int animatedValue;
    private int fcount;
    private float[] hsv;

    public CircleRunnerView(Context context) {
        super(context);
        init();
    }

    public CircleRunnerView(Context context, AttributeSet attrs) {
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
        fillPaint.setStrokeWidth(2);

        storkPaint.setAntiAlias(true);
        storkPaint.setColor(0XFF007DFF);
        storkPaint.setStyle(Paint.Style.STROKE);
        storkPaint.setStrokeCap(Paint.Cap.ROUND);
        hsv = new float[]{180, 0.9f, 1};
    }

    private void startAnimation() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofInt(60);
            valueAnimator.setDuration(1000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animatedValue != (int) animation.getAnimatedValue()) {
                        animatedValue = (int) animation.getAnimatedValue();
                        drawMe(getWidth(), getHeight());
                        n += 0.03;
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

    int div = 40;
    float r = 30;
    float n = 100;
    float widthDiv, heightDiv;

    private void drawMe(int width, int height) {
        Canvas canvas = lockCanvas();
        if (canvas != null) {

            widthDiv = width / div;
            heightDiv = height / div;

            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            for (int i = 0; i < div; i++) {
                for (int j = 0; j < div + 10; j++) {
                    float theta = (float) (Math.PI * 2 * noise(n, i * 0.1f, j * 0.02f));
                    float x = (float) ((Math.cos(theta) * r) + (j * widthDiv));
                    float y = (float) ((Math.sin(theta) * r) * 1.5 + (i * heightDiv));
                    canvas.drawPoint(x, y, fillPaint);
                }
            }
        }
        unlockCanvasAndPost(canvas);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    int perlin_TWOPI, perlin_PI;
    float[] perlin_cosTable;
    float[] perlin;

    Random perlinRandom;
    static final float DEG_TO_RAD = (float) (Math.PI / 180.0f);

    static final int PERLIN_YWRAPB = 4;
    static final int PERLIN_YWRAP = 1 << PERLIN_YWRAPB;
    static final int PERLIN_ZWRAPB = 8;
    static final int PERLIN_ZWRAP = 1 << PERLIN_ZWRAPB;
    static final int PERLIN_SIZE = 4095;
    static final protected float sinLUT[];
    static final protected float cosLUT[];
    static final protected float SINCOS_PRECISION = 0.5f;
    static final protected int SINCOS_LENGTH = (int) (360f / SINCOS_PRECISION);

    int perlin_octaves = 4; // default to medium smooth
    float perlin_amp_falloff = 0.5f; // 50% reduction/octave

    static {
        sinLUT = new float[SINCOS_LENGTH];
        cosLUT = new float[SINCOS_LENGTH];
        for (int i = 0; i < SINCOS_LENGTH; i++) {
            sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
            cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
        }
    }

    public float noise(float x, float y, float z) {
        if (perlin == null) {
            if (perlinRandom == null) {
                perlinRandom = new Random();
            }
            perlin = new float[PERLIN_SIZE + 1];
            for (int i = 0; i < PERLIN_SIZE + 1; i++) {
                perlin[i] = perlinRandom.nextFloat(); //(float)Math.random();
            }
            // [toxi 031112]
            // noise broke due to recent change of cos table in PGraphics
            // this will take care of it
            perlin_cosTable = cosLUT;
            perlin_TWOPI = perlin_PI = SINCOS_LENGTH;
            perlin_PI >>= 1;
        }

        if (x < 0) x = -x;
        if (y < 0) y = -y;
        if (z < 0) z = -z;

        int xi = (int) x, yi = (int) y, zi = (int) z;
        float xf = (float) (x - xi);
        float yf = (float) (y - yi);
        float zf = (float) (z - zi);
        float rxf, ryf;

        float r = 0;
        float ampl = 0.5f;

        float n1, n2, n3;

        for (int i = 0; i < perlin_octaves; i++) {
            int of = xi + (yi << PERLIN_YWRAPB) + (zi << PERLIN_ZWRAPB);

            rxf = noise_fsc(xf);
            ryf = noise_fsc(yf);

            n1 = perlin[of & PERLIN_SIZE];
            n1 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n1);
            n2 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n2);
            n1 += ryf * (n2 - n1);

            of += PERLIN_ZWRAP;
            n2 = perlin[of & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n2);
            n3 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n3 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n3);
            n2 += ryf * (n3 - n2);

            n1 += noise_fsc(zf) * (n2 - n1);

            r += n1 * ampl;
            ampl *= perlin_amp_falloff;
            xi <<= 1;
            xf *= 2;
            yi <<= 1;
            yf *= 2;
            zi <<= 1;
            zf *= 2;

            if (xf >= 1.0f) {
                xi++;
                xf--;
            }
            if (yf >= 1.0f) {
                yi++;
                yf--;
            }
            if (zf >= 1.0f) {
                zi++;
                zf--;
            }
        }
        return r;
    }

    // [toxi 031112]
    // now adjusts to the size of the cosLUT used via
    // the new variables, defined above
    private float noise_fsc(float i) {
        // using bagel's cosine table instead
        return 0.5f * (1.0f - perlin_cosTable[(int) (i * perlin_PI) % perlin_TWOPI]);
    }

}