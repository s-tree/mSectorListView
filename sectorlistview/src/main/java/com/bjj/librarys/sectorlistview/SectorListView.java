package com.bjj.librarys.sectorlistview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/3/13.
 */

public class SectorListView extends FrameLayout implements SectorView.TouchListener{
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    private SectorView centerView,outSideView;
    private RoundListView myRoundListView;
    private float center_rate;
    private int center_radius;
    private int width,height;
    private ObjectAnimator closeAnimator1,closeAnimator2,
            closeAnimator3,closeAnimator4,closeAnimator5,closeAnimator6,
            closeAnimator7,closeAnimator8,closeAnimator9,closeAnimator10;
    private ObjectAnimator showAnimator1,showAnimator2,
            showAnimator3,showAnimator4,showAnimator5,showAnimator6,
            showAnimator7,showAnimator8,showAnimator9,showAnimator10;

    private int orientation = 1;

    public SectorListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SectorListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        centerView = new SectorView(getContext());
        outSideView = new SectorView(getContext());
        myRoundListView = new RoundListView(getContext());
        myRoundListView.setBackgroundColor(Color.TRANSPARENT);

        centerView.setNeedCatchTouch(true);
        centerView.setTouchListener(this);

        addView(outSideView);
        addView(myRoundListView);
        addView(centerView);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyRoundListView);
        centerView.setColor(typedArray.getColor(R.styleable.MyRoundListView_centerBG, Color.RED));
        outSideView.setColor(typedArray.getColor(R.styleable.MyRoundListView_outSideBG, Color.GREEN));
        center_rate = typedArray.getFloat(R.styleable.MyRoundListView_centerRate,0.3f);
        myRoundListView.setCenter_Rate(center_rate);
        myRoundListView.setMaxShow(typedArray.getInteger(R.styleable.MyRoundListView_maxVisisble,3));
        orientation = typedArray.getInteger(R.styleable.MyRoundListView_orientation,1);
        centerView.setOrientation(orientation);
        outSideView.setOrientation(orientation);
        myRoundListView.setOrientation(orientation);

        initAnimations(0);
    }

    private void initAnimations(int size){
        if(size == 0 || showAnimator1 != null){
            return;
        }

        int dissPost = orientation == 1 ? -size : size + size;

        showAnimator1 = ObjectAnimator.ofFloat(centerView,"scaleX", 1f, 1.5f);
        showAnimator1.setDuration(200);
        showAnimator2 = ObjectAnimator.ofFloat(centerView,"scaleY", 1f, 1.5f);
        showAnimator2.setDuration(200);
        showAnimator3 = ObjectAnimator.ofFloat(outSideView,"scaleX", center_rate, 1f);
        showAnimator3.setDuration(300);
        showAnimator4 = ObjectAnimator.ofFloat(outSideView,"scaleY", center_rate, 1f);
        showAnimator4.setDuration(300);
        showAnimator5 = ObjectAnimator.ofFloat(outSideView,"translationX", dissPost, 0);
        showAnimator5.setDuration(300);
        showAnimator6 = ObjectAnimator.ofFloat(outSideView,"translationY", size, 0);
        showAnimator6.setDuration(300);
        showAnimator7 = ObjectAnimator.ofFloat(myRoundListView,"scaleX", center_rate, 1f);
        showAnimator7.setDuration(300);
        showAnimator8 = ObjectAnimator.ofFloat(myRoundListView,"scaleY", center_rate, 1f);
        showAnimator8.setDuration(300);
        showAnimator9 = ObjectAnimator.ofFloat(myRoundListView,"translationX", dissPost, 0);
        showAnimator9.setDuration(300);
        showAnimator10 = ObjectAnimator.ofFloat(myRoundListView,"translationY", size, 0);
        showAnimator10.setDuration(300);

        closeAnimator1 = ObjectAnimator.ofFloat(centerView,"scaleX", 1.5f, 1f);
        closeAnimator1.setDuration(200);
        closeAnimator2 = ObjectAnimator.ofFloat(centerView,"scaleY", 1.5f, 1f);
        closeAnimator2.setDuration(200);
        closeAnimator3 = ObjectAnimator.ofFloat(outSideView,"scaleX", 1f,center_rate);
        closeAnimator3.setDuration(300);
        closeAnimator4 = ObjectAnimator.ofFloat(outSideView,"scaleY", 1f, center_rate);
        closeAnimator4.setDuration(300);
        closeAnimator5 = ObjectAnimator.ofFloat(outSideView,"translationX", 0, dissPost);
        closeAnimator5.setDuration(300);
        closeAnimator6 = ObjectAnimator.ofFloat(outSideView,"translationY", 0, size);
        closeAnimator6.setDuration(300);
        closeAnimator7 = ObjectAnimator.ofFloat(myRoundListView,"scaleX", 1f, center_rate);
        closeAnimator7.setDuration(300);
        closeAnimator8 = ObjectAnimator.ofFloat(myRoundListView,"scaleY", 1f, center_rate);
        closeAnimator8.setDuration(300);
        closeAnimator9 = ObjectAnimator.ofFloat(myRoundListView,"translationX", 0, dissPost);
        closeAnimator9.setDuration(300);
        closeAnimator10 = ObjectAnimator.ofFloat(myRoundListView,"translationY", 0, size);
        closeAnimator10.setDuration(300);
    }

    public void setMaxShow(int max){
        myRoundListView.setMaxShow(max);
    }

    public void setAdapter(BaseAdapter baseAdapter){
        myRoundListView.setAdapter(baseAdapter);
    }

    public void setCenterRate(float centerRate){
        this.center_rate = centerRate;
        myRoundListView.setCenter_Rate(centerRate);
        center_radius = (int) (height * center_rate);
        requestLayout();
    }

    public void setText(String text){
        centerView.setText(text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        center_radius = (int) (height * center_rate);
        initAnimations(width > height ? width : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        outSideView.layout(0,0,width,height);
        if(orientation == 1){
            centerView.layout(0,height - center_radius,center_radius,height);
        }else{
            centerView.layout(width - center_radius ,height - center_radius,width,height);
        }
        centerView.resetSize(center_radius);
        myRoundListView.layout(0,0,width,height);
    }

    boolean isShown = true;

    @Override
    public void touch() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(showAnimator1).with(showAnimator2);
        animatorSet.start();
    }

    @Override
    public void up() {
        AnimatorSet animatorSet = new AnimatorSet();
        if(isShown){
            animatorSet.play(closeAnimator1)
                    .with(closeAnimator2)
                    .with(closeAnimator3)
                    .with(closeAnimator4)
                    .with(closeAnimator5)
                    .with(closeAnimator6)
                    .with(closeAnimator7)
                    .with(closeAnimator8)
                    .with(closeAnimator9)
                    .with(closeAnimator10);
        }else{
            animatorSet.play(closeAnimator1)
                    .with(closeAnimator2)
                    .with(showAnimator3)
                    .with(showAnimator4)
                    .with(showAnimator5)
                    .with(showAnimator6)
                    .with(showAnimator7)
                    .with(showAnimator8)
                    .with(showAnimator9)
                    .with(showAnimator10);
        }
        isShown = !isShown;
        animatorSet.start();
    }
}
