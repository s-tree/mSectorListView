package mroundgroup.test.com.mroundgroup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2017/3/13.
 */

public class MySectorView extends View {
    private Paint paint;
    private int mColor;
    private int textColor = Color.WHITE;
    private RectF rectF;
    private int width,height;
    private int size;
    private boolean needCatchTouch = false;
    private TouchListener touchListener;
    private String text;
    private int textSize = 24;
    private Rect text_rect;
    private int orientation = 1;

    public MySectorView(Context context) {
        super(context);
        init();
    }

    public void setColor(int color){
        this.mColor = color;
        postInvalidate();
    }

    public void setOrientation(int orientation){
        this.orientation = orientation;
        if(size == 0){
            return;
        }
        postInvalidate();
    }

    public void setNeedCatchTouch(boolean isNeed){
        needCatchTouch = isNeed;
    }

    private void init(){
        paint = new Paint();
        paint.setAntiAlias(true);
        text_rect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        size = width > height ? height : width;
        rectF = orientation == 1 ? new RectF(0 - size,height - size ,size,height + size) : new RectF(0,height - size ,size + size,height + size);
        postInvalidate();
    }

    public void setText(String text){
        this.text = text;
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), text_rect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(rectF == null ){
            return;
        }
        paint.setColor(mColor);
        if(orientation == 1){
            canvas.drawArc(rectF,270, 360, true, paint);
        }
        else{
            canvas.drawArc(rectF,180, 270, true, paint);
        }
        if(!TextUtils.isEmpty(text)){
            paint.setColor(textColor);
            paint.setTextSize(16);
            if(orientation == 1){
                canvas.drawText(text,( width - text_rect.width() )/4, height/4*3, paint);
            }
            else{
                canvas.drawText(text,( width - text_rect.width() )/4 * 3, height/4*3, paint);
            }
        }
    }

    public void resetSize(int size){
        width = size;
        height = size;
        this.size = size;
        if(orientation == 1){
            rectF = new RectF(0 - size,0 ,size,size + size);
        }
        else{
            rectF = new RectF(0 ,0 ,size + size,size + size);
        }
        postInvalidate();
    }

    public void setTouchListener(TouchListener touchListener){
        this.touchListener = touchListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!needCatchTouch){
            return false;
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if( Math.sqrt(Math.pow(event.getX() - 0,2) + Math.pow(event.getY() - height , 2) ) >= size ){
                return false;
            }else{
                if(touchListener != null){
                    touchListener.touch();
                }
                return true;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            if(touchListener != null){
                touchListener.up();
            }
        }
        return super.onTouchEvent(event);
    }

    public interface TouchListener{
        void touch();
        void up();
    }
}
