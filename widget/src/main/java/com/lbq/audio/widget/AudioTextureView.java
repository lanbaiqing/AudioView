package com.lbq.audio.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

import java.util.Random;

public class AudioTextureView extends TextureView implements TextureView.SurfaceTextureListener
{
    private Paint paint = new Paint();
    private LinearGradient gradient;
    private Random random = new Random();
    private boolean auto;
    private float rx;
    private float ry;
    private float left;
    private float right;
    private int mode;
    private int min;
    private int max;
    private int total;
    private float width;
    private float height;
    private float drawOffset;
    private float drawHeight;
    private int position;

    private int[] arrays = new int[]{0xff00ff00, 0xffffff00, 0xffff0000};

    public AudioTextureView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setOpaque(false);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AudioTextureView);
        auto = array.getBoolean(R.styleable.AudioTextureView_auto, false);
        mode = array.getInt(R.styleable.AudioTextureView_mode, 0);
        total = array.getInt(R.styleable.AudioTextureView_total, 25);
        min = array.getInt(R.styleable.AudioTextureView_min, 5);
        max = array.getInt(R.styleable.AudioTextureView_max, 30);
        drawHeight = array.getDimension(R.styleable.AudioTextureView_drawHeight, 0);
        drawOffset = array.getDimension(R.styleable.AudioTextureView_drawOffset, 0);
        String content = array.getString(R.styleable.AudioTextureView_colors);
        if (content != null && content.trim().length() > 0)
        {
            String[] list = content.trim().split("\\|");
            this.arrays = new int[list.length];
            for (int i = 0 ; i < list.length ; i ++ )
            {
                arrays[i] = Color.parseColor(list[i]);
            }
        }
        array.recycle();
        setSurfaceTextureListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        left = 0;
        right = width;
        if(drawOffset == 0)
            drawOffset = height / total / (total / 5);
        if(drawHeight == 0)
            drawHeight = height / total - drawOffset;
        rx = drawHeight / 2;
        ry = drawHeight / 2;
        if (gradient == null)
        {
            initPaint();
        }
        if (auto)
            startDance();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
    {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface)
    {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        stopDance();
        return false;
    }

    public boolean getAuto(boolean auto)
    {
        return auto;
    }

    public void setAuto(boolean auto)
    {
        this.auto = auto;
    }

    public int getMode()
    {
        return mode;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }

    public float getDrawOffset()
    {
        return drawOffset;
    }

    public void setDrawOffset(int drawOffset)
    {
        this.drawOffset = drawOffset;
    }

    public float getDrawHeight()
    {
        return drawHeight;
    }

    public void setDrawHeight(int drawHeight)
    {
        this.drawHeight = drawHeight;
    }

    private void initPaint()
    {
        int[] colors = new int[arrays.length];
        for (int i = 0 ; i < colors.length ; i ++ )
        {
            colors[i] = arrays[arrays.length -1 - i];
        }
        gradient = new LinearGradient(0, 0, 0, height, colors, null, LinearGradient.TileMode.CLAMP);
        paint.setShader(gradient);
    }

    public int[] getColors()
    {
        return arrays;
    }

    public void setColors(int... colors)
    {
        this.arrays = colors;
        this.initPaint();
    }

    public int getMin()
    {
        return min;
    }

    public void setMin(int min)
    {
        this.min = min;
    }

    public int getMax()
    {
        return max;
    }

    public void setMax(int max)
    {
        this.max = max;
    }

    public int getRandom(int n, int m)
    {
        return random.nextInt(m) % (m - n) + n;
    }

    public void startDance()
    {
        if (games != null)
        {
            games.run = false;
            games.interrupt();
            games = null;
        }
        games = new Games();
        games.run = true;
        games.start();
    }

    public void stopDance()
    {
        if (games != null)
        {
            games.run = false;
            games.interrupt();
            games = null;
            position = 0;
        }
    }

    private void drawTop(int index)
    {
        float top;
        float bottom;
        switch (getMode())
        {
            case 0:
                bottom = height - (drawOffset + drawHeight) * index;
                top = bottom - drawHeight;
                break;
            case 1:
                bottom = height - (drawOffset + drawHeight) * index;
                top = bottom - drawHeight;
                break;
            default:
                bottom = height - (drawOffset + drawHeight) * index;
                top = bottom - (drawOffset + drawHeight);
                break;
        }
        Rect rect = new Rect((int)left, (int)top, (int)right, (int)bottom);
        Canvas canvas = lockCanvas(rect);
        switch(getMode())
        {
            case 0:
                canvas.drawRoundRect(new RectF(left, top, right, bottom), rx, ry, paint);
                break;
            case 1:
                canvas.drawRect(left, top, right, bottom, paint);
                break;
            default:
                canvas.drawRect(left, top, right, bottom, paint);
                break;
        }
        unlockCanvasAndPost(canvas);
    }

    private void drawDown(int index)
    {
        float top;
        float bottom;
        switch (getMode())
        {
            case 0:
                bottom = height - (drawOffset + drawHeight) * index;
                top = bottom - drawHeight;
                break;
            case 1:
                bottom = height - (drawOffset + drawHeight) * index;
                top = bottom - drawHeight;
                break;
            default:
                bottom = height - (drawOffset + drawHeight) * index;
                top = bottom - (drawOffset + drawHeight);
                break;
        }
        Rect rect = new Rect((int)left, (int)top, (int)right, (int)bottom);

        Canvas canvas = lockCanvas(rect);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        unlockCanvasAndPost(canvas);
    }

    public void update(int index)
    {
        if (position != index)
        {
            if (index - position > 0)
            {
                drawTop(index);
            }
            else
            {
                drawDown(index);
            }
            position = index;
        }
    }

    private Games games;
    private class Games extends Thread
    {
        private boolean run;
        @Override
        public void run()
        {
            try
            {
                while(run)
                {
                    int r = random.nextInt(total);
                    if(r != position)
                    {
                        int millis = getRandom(min, max);
                        if(r - position > 0)
                        {
                            for(int i = position ; i < r ; i ++ )
                            {
                                drawTop(i);
                                Thread.sleep(millis);
                            }
                        }
                        else
                        {
                            for(int i = position +1 ; i > r; i -- )
                            {
                                drawDown(i -1);
                                Thread.sleep(millis);
                            }
                        }
                        position = r;
                    }
                }
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}

