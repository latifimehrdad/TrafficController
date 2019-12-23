package com.ngra.trafficcontroller.views.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.ngra.trafficcontroller.R;

public class MLCurveFooter extends View {


    private Paint DrawPaint;
    private Paint DrawPaint2;
    private Path mPath;
    private int height;
    private int width;
    private int backColor;
    private int shadowColor;

    public MLCurveFooter(Context context) {
        super(context);
        init();
    }

    public MLCurveFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MLCurveFooter);

        backColor = ta.getColor(R.styleable.MLCurveFooter_backcolorf, Color.WHITE);
        shadowColor = ta.getColor(R.styleable.MLCurveFooter_shadowcolorf,Color.argb(60,0,0,0));

        init();
    }


    private void init() {
        mPath = new Path();
        DrawPaint = new Paint();
        DrawPaint.setColor(backColor);

        DrawPaint2 = new Paint();
        //DrawPaint2.setColor(shadowColor);



        setBackgroundColor(0);
    }



    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = getWidth();
        height = getHeight();
        int halfWidth = width / 2;
        int halfHeight = height / 2;

        DrawPaint2.setShader(new LinearGradient(0,halfHeight,0,height - 5,shadowColor,Color.argb(10,0,0,0), Shader.TileMode.CLAMP));

        int m = halfHeight * 25 / 100;
        int n = m;

        mPath.reset();
        mPath.moveTo(0, halfHeight);
        mPath.lineTo(0,height);
        mPath.lineTo(width, height);
        mPath.lineTo(width,halfHeight);

        Point mFirstCurveControlPoint1 = new Point();
        Point mFirstCurveControlPoint2 = new Point();
        mFirstCurveControlPoint1.set(width - (halfWidth * 50 / 100),halfHeight);
        mFirstCurveControlPoint2.set(width - (halfWidth / 3) , 3);

        mPath.cubicTo(mFirstCurveControlPoint1.x, mFirstCurveControlPoint1.y,
                mFirstCurveControlPoint2.x, mFirstCurveControlPoint2.y,
                halfWidth, 3);

        Point mSecondCurveControlPoint1 = new Point();
        Point mSecondCurveControlPoint2 = new Point();

        mSecondCurveControlPoint1.set((halfWidth / 3),3);
        mSecondCurveControlPoint2.set((halfWidth * 50 / 100), halfHeight);

        mPath.cubicTo(mSecondCurveControlPoint1.x, mSecondCurveControlPoint1.y,
                mSecondCurveControlPoint2.x, mSecondCurveControlPoint2.y,
                0, halfHeight);

//        Point mFirstCurveControlPoint1 = new Point();
//        Point mFirstCurveControlPoint2 = new Point();
//        mFirstCurveControlPoint1.set(width - (halfWidth * 60 / 100),halfHeight);
//        mFirstCurveControlPoint2.set(width - (halfWidth / 7) , 0);
//
//        mPath.cubicTo(mFirstCurveControlPoint1.x, mFirstCurveControlPoint1.y,
//                mFirstCurveControlPoint2.x, mFirstCurveControlPoint2.y,
//                halfWidth + (halfWidth / 5), 0);
//
//        mPath.lineTo(halfWidth - (halfWidth / 5), 0);
//
//        Point mSecondCurveControlPoint1 = new Point();
//        Point mSecondCurveControlPoint2 = new Point();
//
//        mSecondCurveControlPoint1.set((halfWidth / 7),0);
//        mSecondCurveControlPoint2.set((halfWidth * 60 / 100), halfHeight);
//
//        mPath.cubicTo(mSecondCurveControlPoint1.x, mSecondCurveControlPoint1.y,
//                mSecondCurveControlPoint2.x, mSecondCurveControlPoint2.y,
//                0, halfHeight);

        mPath.close();


    }



    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawPath(pathShadow,DrawPaint2);
        canvas.drawPath(mPath, DrawPaint);

    }
}
