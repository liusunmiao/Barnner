package com.lsm.barnner.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义轮播图效果
 */
public class ImageBarnnerViewGroup extends ViewGroup {
    //获取子view的数量
    private int children;
    //子视图宽度
    private int childWidth;
    //子视图高度
    private int childHeight;
    //第一次按下的x位置  每一次移动过程中 移动之前的位置左边
    private float x;
    //每张图片的索引
    private int index = 0;
    private Scroller scroller;
    //是否自动轮播  默认情况下开启自动轮播
    private boolean isAuto = true;
    private Timer timer = new Timer();
    private TimerTask task;
    private Handler autoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int duration = 1000;
                    //实现图片的自动轮播
                    if (++index >= children) {
                        //最后一张图片  从第一张图片开始重新滑动
                        index = 0;
                        duration = 1;
                    }
                    int scrollX = getScrollX();
                    int dx = index * childWidth - scrollX;
                    scroller.startScroll(scrollX, 0, dx, 0, duration);
                    postInvalidate();
                    //通知指示器改变
                    if (pointListener != null) {
                        pointListener.selectImage(index);
                    }
                    break;
            }
        }
    };
    private ImageBannerListener listener;
    private ImageBannerViewGroupListener pointListener;
    //是否是点击事件
    private boolean isClick = false;

    /**
     * 开始播放轮播图
     */
    public void startAuto() {
        isAuto = true;
    }

    /**
     * 暂停播放轮播图
     */
    public void stopAuto() {
        isAuto = false;
    }

    /**
     * 当页面销毁的时候调用该方法
     */
    public void destoryAuto() {
        isAuto = false;
        if (autoHandler != null) {
            //移除handler消息
            autoHandler.removeCallbacksAndMessages(null);
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public ImageBarnnerViewGroup(Context context) {
        this(context, null);
    }

    public ImageBarnnerViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageBarnnerViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initObj();
    }

    /**
     * 初始化话Scroller TimerTask 实现自动轮播
     */
    private void initObj() {
        scroller = new Scroller(getContext());
        task = new TimerTask() {
            @Override
            public void run() {
                if (isAuto) {
                    //开启轮播图
                    autoHandler.sendEmptyMessage(0);
                }
            }
        };
        timer.schedule(task, 100, 2500);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取子view的数量
        children = getChildCount();
        if (0 == children) {
            setMeasuredDimension(0, 0);
        } else {
            //测量子视图的宽度和高度
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            View view = getChildAt(0);
            childWidth = view.getMeasuredWidth();
            childHeight = view.getMeasuredHeight();
            //所有子视图宽度的总和
            int width = view.getMeasuredWidth() * children;
            setMeasuredDimension(width, childHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int leftMargin = 0;
            for (int i = 0; i < children; i++) {
                View view = getChildAt(i);
                view.layout(leftMargin, 0, leftMargin + childWidth, childHeight);
                leftMargin += childWidth;
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下后停止轮播
                stopAuto();
                if (!scroller.isFinished()) {
                    //如果没有完成 结束滑动过程
                    scroller.abortAnimation();
                }
                isClick = true;
                x = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float distance = moveX - x;
                if (Math.abs(distance) > 10) {
                    isClick = false;
                }
                scrollBy((int) -distance, 0);
                x = moveX;
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                index = (scrollX + childWidth / 2) / childWidth;
                if (index < 0) {//已经滑动到最左边那张图片
                    index = 0;
                } else if (index > children - 1) {
                    index = children - 1;//滑动到最右边的图片
                }
                if (isClick) {
                    //点击事件
                    if (listener != null) {
                        listener.clickImageIndex(index);
                    }
                } else {

                }
                //计算滑动的距离
                int dx = index * childWidth - scrollX;
//                scrollTo(index * childWidth, 0);
                scroller.startScroll(scrollX, 0, dx, 0, 500);
                postInvalidate();
                //通知指示器改变
                if (pointListener != null) {
                    pointListener.selectImage(index);
                }
                //当用户松开时又重新开启图片轮播
                startAuto();
                break;
        }
        return true;
    }
    /**
     * 设置轮播图点击事件监听
     *
     * @param listener
     */
    public void setOnImageBannerListener(ImageBannerListener listener) {
        this.listener = listener;
    }

    /**
     * 轮播图点击接口
     */
    public interface ImageBannerListener {
        void clickImageIndex(int position);
    }

    /**
     * 轮播图滑动监听
     */
    public interface ImageBannerViewGroupListener {
        void selectImage(int position);
    }

    /**
     * 设置轮播图滑动监听
     *
     * @param listener
     */
    public void setPointSelectListener(ImageBannerViewGroupListener listener) {
        this.pointListener = listener;
    }
}
