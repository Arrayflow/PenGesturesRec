package com.example.pengesturesrec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.icu.util.Calendar;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.pengesturetest.ListenTouchEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GestureView extends View {
    private ListenTouchEvent listenTouchEvent;
    public TouchUpEvent touchUpEvent;

    private Context context;
    //X轴起点
    private float x;
    //Y轴起点
    private float y;
    //画笔
    private final Paint paint = new Paint();
    //路径
    private final Path path = new Path();
    //画布
    private Canvas canvas;
    //生成的图片
    private Bitmap bitmap;
    //画笔的宽度
    private int paintWidth = 5;
    //签名颜色
    private int paintColor = Color.BLACK;
    //背景颜色
    private int backgroundColor = Color.WHITE;
    //是否已经签名
    private boolean isTouched = false;

    private List<TouchPoint> gesturePoints = new ArrayList<>();


    //签名开始与结束
    public interface Touch {
        void OnTouch(boolean isTouch);

    }

    private Touch touch;

    public GestureView(Context context) {
        super(context);
        init(context);
    }

    public GestureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GestureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        //抗锯齿
        paint.setAntiAlias(true);
        //样式
        paint.setStyle(Paint.Style.STROKE);
        //画笔颜色
        paint.setColor(paintColor);
        //画笔宽度
        paint.setStrokeWidth(paintWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //创建于view大小一致的bitmap
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);
        isTouched = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (listenTouchEvent != null) {
            listenTouchEvent.onTouchView(event);
        }

        float xx = event.getX();
        float yy = event.getY();
        gesturePoints.add(new TouchPoint(xx, yy));

//        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
        if (touch != null) touch.OnTouch(true);
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                touchDwon(event);
                break;

            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                if (touch != null) touch.OnTouch(false);
                touchMove(event);
                break;

            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, paint);
                path.reset();

                touchUpEvent.onSuccess();
                gesturePoints.clear();
                this.clear();
                break;
        }
        // 更新绘制
        invalidate();
//        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画此次笔画之前的签名
        canvas.drawBitmap(bitmap, 0, 0, paint);
        // 通过画布绘制多点形成的图形
        canvas.drawPath(path, paint);
    }

    //Down事件方法
    private void touchDwon(MotionEvent event) {
        //重置绘制路径
        path.reset();
        float downX = event.getX();
        float downY = event.getY();
        x = downX;
        y = downY;
        //绘制起点
        path.moveTo(downX, downY);
    }

    //手指滑动的方法
    private void touchMove(MotionEvent event) {
        //当前的x,y坐标点
        final float moveX = event.getX();
        final float moveY = event.getY();
        //之前的x,y坐标点
        final float previousX = x;
        final float previousY = y;
        //获取绝对值
        final float dx = Math.abs(moveX - previousX);
        final float dy = Math.abs(moveY - previousY);
        if (dx >= 3 || dy >= 3) {
            float cX = (moveX + previousX) / 2;
            float cY = (moveY + previousY) / 2;
            path.quadTo(previousX, previousY, cX, cY);
            x = moveX;
            y = moveY;
        }
    }

    /**
     * 设置画笔颜色
     *
     * @param paintColor
     */
    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
        paint.setColor(paintColor);
    }

    public void setListenTouchEvent(ListenTouchEvent event) {
        this.listenTouchEvent = event;
    }

    public void setTouchUpEvent(TouchUpEvent event) {
        this.touchUpEvent = event;
    }

    /**
     * 设置画笔宽度
     *
     * @param paintWidth
     */
    public void setPaintWidth(int paintWidth) {
        this.paintWidth = paintWidth;
        paint.setStrokeWidth(paintWidth);
    }

    /**
     * 设置画板颜色
     *
     * @param canvasColor
     */
    public void setCanvasColor(int canvasColor) {
        this.backgroundColor = canvasColor;
    }

    /**
     * 清除画板
     */
    public void clear() {
        if (canvas != null) {
            isTouched = false;
            //更新画板
            paint.setColor(paintColor);
            paint.setStrokeWidth(paintWidth);
            canvas.drawColor(backgroundColor, PorterDuff.Mode.CLEAR);
            invalidate();
        }
    }

    /**
     * 获取画板的Bitmap
     *
     * @return
     */
    public Bitmap getBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap = getDrawingCache();
        setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 保存画板
     *
     * @param path 保存到路径
     */
    @SuppressLint("WrongThread")
    public Boolean save(String path) throws IOException {
        Rect rec = getBitmapBoundary((ArrayList<TouchPoint>) gesturePoints);
        canvas = new Canvas(bitmap);
        Bitmap bitmap = this.bitmap;

//        Bitmap _bitmap = Bitmap.createBitmap(bitmap, rec.left, rec.top, rec.right - rec.left, rec.bottom - rec.top);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
            File file = new File(path);
//            if (file.exists()) {
//                file.delete();
//            }
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
            outputStream.close();
            bos.close();
            gesturePoints.clear();
            return true;
        } else {
            gesturePoints.clear();
            return false;
        }
    }

    public Bitmap getImage() {
        Rect rec = getBitmapBoundary((ArrayList<TouchPoint>) gesturePoints);
        canvas = new Canvas(bitmap);
        Bitmap bitmap = this.bitmap;    //原始Bitmap
        //最小矩形框选Bitmap

        Bitmap _bitmap = Bitmap.createBitmap(bitmap, rec.left - 5, rec.top - 5, rec.right - rec.left + 10, rec.bottom - rec.top + 10);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        _bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return _bitmap;
    }

    public static byte[] bitmap2RGBData(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width,
                height);
        byte[] rgb = new byte[width * height * 3];
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            rgb[i * 3] = (byte) ((val >> 16) & 0xFF);//R
            rgb[i * 3 + 1] = (byte) ((val >> 8) & 0xFF);//G
            rgb[i * 3 + 2] = (byte) (val & 0xFF);//B
        }
        return rgb;
    }

    /**
     * 创建位图
     */
    public Bitmap createViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private Rect getBitmapBoundary(ArrayList<TouchPoint> gesturePoints) {

        Collections.sort(gesturePoints, new Comparator<TouchPoint>() {
            @Override
            public int compare(TouchPoint o1, TouchPoint o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return o1.a - o2.a < 0 ? -1 : (o1.a - o2.a > 0 ? 1 : 0);
            }
        });

        int left = (int) gesturePoints.get(0).a;
        int right = (int) gesturePoints.get(gesturePoints.size() - 1).a;
        Collections.sort(gesturePoints, new Comparator<TouchPoint>() {
            @Override
            public int compare(TouchPoint o1, TouchPoint o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return o1.b - o2.b < 0 ? -1 : (o1.b - o2.b > 0 ? 1 : 0);
            }
        });
        int top = (int) gesturePoints.get(0).b;
        int bottom = (int) gesturePoints.get(gesturePoints.size() - 1).b;
        return new Rect(left, top, right, bottom);
    }

}

