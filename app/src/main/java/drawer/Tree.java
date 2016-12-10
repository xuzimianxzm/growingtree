package drawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xuzimian.www.growingtree.MainActivity;

/**
 * Created by AW on 2016/11/24.
 */

public class Tree extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread thread; // SurfaceView通常需要自己单独的线程来播放动画
    private SurfaceHolder surfaceHolder;

    private MainActivity context;
    Paint paint = new Paint();
    Bitmap bitmap;
    private Canvas bufCanvas,canvas;  //采用双缓冲绘图技术

    int depth = 16;
    int newDepthPub;
    double newAngle, maxAngle = (float) (2 * Math.PI / 4);
    int subBranches;

    int drawCount;
    boolean isBeLock=false;
    float degrees=46f;

    public void drawSelf(float startX, float startY, int length, double angle, int depth, int branchWidth) {

        paint.setStrokeWidth(branchWidth);

        float endX, endY;
        if (depth <= 2) {
            paint.setColor(Color.rgb(0, (((int) (Math.random() * 64) + 128) >> 0), 0));
        } else {
            paint.setColor(Color.rgb((((int) (Math.random() * 64) + 64) >> 0), 50, 25));
        }

        endX = (float) (startX + length * Math.cos(angle));
        endY = (float) (startY + length * Math.sin(angle));

        int newDepth, newLength, maxBranch = 3;
        newDepth = depth - 1;
        if (newDepth == 0) return;

        if (bufCanvas == null | !isBeLock) {
            bufCanvas = new Canvas(); // 通过lockCanvas加锁并得到該SurfaceView的画布 new Rect(260,1000,768,1604)
            bufCanvas.setBitmap(bitmap);
            canvas=  this.surfaceHolder.lockCanvas();
            isBeLock=true;
        }

        //绘画
        bufCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.ADD);

        bufCanvas.drawLine(startX, startY, endX, endY, paint);
        drawCount++;

        if(drawCount > 128){
            canvas.drawBitmap(bitmap,0,0,paint);
            this.surfaceHolder.unlockCanvasAndPost(canvas);
            isBeLock=false;
            drawCount=0;
        }


        subBranches = maxBranch - 1;
        branchWidth *= .7;

        for (int i = 0; i < subBranches; i++) {
            maxAngle = (float) (2 * Math.PI / 4);
            newAngle = (float) (angle + Math.random() * maxAngle - maxAngle * .5);
            newLength = (int) (length * (.7 + Math.random() * .3));
            drawSelf(endX, endY, newLength, newAngle, newDepth, branchWidth);
            newDepthPub = newDepth;
        }
    }

    @Override
    public void run() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (true){
                    try {
                        degrees= (float) (degrees*Math.random());
                        setHue(degrees, 1f, 200);//设置色相 (float) (Math.random() * Math.random() * 4)
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        paint.setPathEffect(new CornerPathEffect(2)); //设置画笔末端线帽的样式
        paint.setDither(true);
        bitmap = Bitmap.createBitmap(~~(getMeasuredWidth() ),~~(getMeasuredHeight()) , Bitmap.Config.ARGB_8888);
        drawSelf(~~(getMeasuredWidth() / 2), ~~(int) (getMeasuredHeight() / 1.02), 60, -Math.PI / 2, depth, 12);
        if(isBeLock){
            canvas.drawBitmap(bitmap,0,0,paint);
            bufCanvas=null;
            this.surfaceHolder.unlockCanvasAndPost(canvas);
        }
        context.setFinish(true);
    }

    public void setHue(float degrees, float sat, float lum){
        // 设置色相
        ColorMatrix hueMatrix = new ColorMatrix();
        hueMatrix.setRotate(0, degrees);
        hueMatrix.setRotate(1, degrees);
        hueMatrix.setRotate(2, degrees);

        // 设置饱和度
        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(sat);

        // 设置明度
        ColorMatrix lumMatrix = new ColorMatrix();
        lumMatrix.setScale(lum, lum, lum, 1);

//        // 融合
        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(lumMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(hueMatrix);

        // 给paint设置颜色属性
        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));


    }


    public Tree(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public Tree(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Init(context);
    }

    public Tree(Context c) {
        super(c);
        Init(c);
    }

    private void Init(Context c) {
        this.surfaceHolder = this.getHolder();
        this.surfaceHolder.addCallback(this);
        this.context = (MainActivity) c;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

}
