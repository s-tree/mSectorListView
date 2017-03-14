package com.bjj.librarys.sectorlistview;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Administrator on 2017/3/12.
 */

public class RoundListView extends ViewGroup {
    private int width,height , radius;
    //宽高中的短边
    private int size;
    //初始角度
    private float angle = 0;
    //最大的旋转角度(负值)
    private float maxAngle = 75;
    //小圆的高度比例
    private float center_Rate = 0.3f;
    //小圆的高度
    private int center_size;
    //最多展示的item个数
    private int maxVisibleItemCount = 3;
    //每个item间距的角度
    private float item_angle = 30;
    private BaseAdapter baseAdapter;
    private int orientation = 1;

    private DataSetObserver dataSetObservable = new DataSetObserver(){
        @Override
        public void onChanged() {
            super.onChanged();
            resetChilds();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    public RoundListView(Context context){
        this(context, null);
    }

    public RoundListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAdapter(BaseAdapter baseAdapter){
        if(this.baseAdapter != null){
            this.baseAdapter.unregisterDataSetObserver(dataSetObservable);
        }
        this.baseAdapter = baseAdapter;
        this.baseAdapter.registerDataSetObserver(dataSetObservable);
        maxAngle = (baseAdapter.getCount() - maxVisibleItemCount) * item_angle + item_angle;
        resetChilds();
    }

    private void resetChilds(){
        angle = item_angle-5;
        removeAllViews();
        for(int i = 0 ; i < maxVisibleItemCount +2 && i < baseAdapter.getCount(); i++){
            addView(baseAdapter.getView(i,null,this));
        }
        requestLayout();
    }

    public void setMaxShow(int number){
        this.maxVisibleItemCount = number;
        item_angle = 90 / (number - 1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        size = width > height ? height : width;
        center_size = (int) (size * center_Rate);
        radius = (size - center_size) / 2 + center_size;
    }

    public void setOrientation(int orientation){
        this.orientation = orientation;
    }

    private void init(){
        item_angle = 90 / (maxVisibleItemCount - 1);
    }

    public void setCenter_Rate(float center_rate){
        this.center_Rate = center_rate;
        center_size = (int) (size * center_Rate);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        requestMLayout();
    }

    private void requestMLayout(){
        if(baseAdapter == null || baseAdapter.getCount() == 0){
            return;
        }
        float nowAgnle = angle;
        float trueAngle = Math.abs(nowAgnle);
        int firstVis = (int) ( trueAngle / item_angle);
        nowAgnle = nowAgnle % item_angle ;
        for(int i = 0 ; i < getChildCount() ; i++){
            View v = getChildAt(i);
            if(firstVis + i >= baseAdapter.getCount()){
                v.setVisibility(GONE);
            }else{
                v.setVisibility(VISIBLE);
                v = baseAdapter.getView(firstVis + i , getChildAt(i) ,this);
            }
            double mAngle = Math.toRadians(nowAgnle);
            v.measure(0,0);
            int mWidth = v.getMeasuredWidth()/2;
            int mHeight = v.getMeasuredHeight()/2;
            int x = orientation == 1 ? (int) (radius * Math.sin(mAngle)) : width - (int) (radius * Math.sin(mAngle));
            int y = height - (int) (radius * Math.cos(mAngle));
            v.layout(x - mWidth, y - mHeight , x + mWidth , y + mHeight);
            nowAgnle += item_angle;
        }
    }

    //#####################################################################################
    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 检测按下到抬起时使用的时间
     */
    private long mDownTime;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isFling;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private static final int FLINGABLE_VALUE = 200;

    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private static final int NOCLICK_VALUE = 3;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private int mFlingableValue = FLINGABLE_VALUE;

    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;

    /**
     * 如果每秒旋转角度到达该值，则认为是自动滚动
     *
     * @param mFlingableValue
     */
    public void setFlingableValue(int mFlingableValue){
        this.mFlingableValue = mFlingableValue;
    }


    /**
     * 自动滚动的Runnable
     */
    private AutoFlingRunnable mFlingRunnable;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if(baseAdapter == null || baseAdapter.getCount() <= 1){
            return super.dispatchTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if( Math.sqrt(Math.pow(event.getX() - 0,2) + Math.pow(event.getY() - height , 2) ) >= size ){
                        return false;
                    }
                }

                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;

                // 如果当前已经在快速滚动
                if (isFling){
                    // 移除快速滚动的回调
                    removeCallbacks(mFlingRunnable);
                    isFling = false;
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE:

                /**
                 * 获得开始的角度
                 */
                float start = getAngle(mLastX, mLastY);
                /**
                 * 获得当前的角度
                 */
                float end = getAngle(x, y);
                float cha = end - start;
                angle += cha;
                if(angle >= item_angle - 5){
                    angle = item_angle - 5;
                }
                else if(angle <= -maxAngle){
                    angle = -maxAngle;
                }
                mTmpAngle += cha;
                // 重新布局
                requestLayout();
                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:

                // 计算，每秒移动的角度
                float anglePerSecond = mTmpAngle * 1000
                        / (System.currentTimeMillis() - mDownTime);

                // 如果达到该值认为是快速移动
                if (Math.abs(anglePerSecond) > mFlingableValue && !isFling){
                    // post一个任务，去自动滚动
                    post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));

                    return true;
                }

                // 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击
                if (Math.abs(mTmpAngle) > NOCLICK_VALUE){
                    return true;
                }

                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch){
        double x = xTouch ;
        double y = yTouch - height;
        if(orientation == 1)
            return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
        return (float) (Math.asin(y / Math.hypot(width - x, y)) * 180 / Math.PI);
    }

    /**
     * 自动滚动的任务
     *
     * @author zhy
     *
     */
    private class AutoFlingRunnable implements Runnable{

        private float angelPerSecond;

        public AutoFlingRunnable(float velocity)
        {
            this.angelPerSecond = velocity;
        }

        public void run(){
            // 如果小于20,则停止
            if ((int) Math.abs(angelPerSecond) < 20){
                isFling = false;
                return;
            }
            isFling = true;
            // 不断改变mStartAngle，让其滚动，/30为了避免滚动太快
            angle += (angelPerSecond / 30);
            if(angle >= item_angle - 5){
                angle = item_angle - 5;
            }
            else if(angle <= -maxAngle){
                angle = -maxAngle;
            }
            // 逐渐减小这个值
            angelPerSecond /= 1.0666F;
            postDelayed(this, 30);
            // 重新布局
            requestLayout();
        }
    }

}
