package com.lsm.barnner.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lsm.barnner.R;

import java.util.List;

/**
 * 自定义轮播图
 */
public class ImageBannerFramLayout extends FrameLayout {
    //图片轮播
    private ImageBarnnerViewGroup imageBarnnerViewGroup;
    //屏幕的宽度
    private int screenWidth;
    //底部指示器容器
    private LinearLayout linearLayout;
    //点击监听回调接口
    private ImageBannerListener listener;

    public ImageBannerFramLayout(Context context) {
        this(context, null);
    }

    public ImageBannerFramLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageBannerFramLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        screenWidth = getScreenWidth();
        initImageBannerViewGroup();
        initDotLinearLayout();
    }

    /**
     * 加载图片轮播
     */
    private void initImageBannerViewGroup() {
        imageBarnnerViewGroup = new ImageBarnnerViewGroup(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        imageBarnnerViewGroup.setLayoutParams(lp);
        addView(imageBarnnerViewGroup);
        //设置轮播图切换监听
        imageBarnnerViewGroup.setPointSelectListener(new ImageBarnnerViewGroup.ImageBannerViewGroupListener() {
            @Override
            public void selectImage(int position) {
                int count = linearLayout.getChildCount();
                for (int i = 0; i < count; i++) {
                    ImageView childAt = (ImageView) linearLayout.getChildAt(i);
                    if (position == i) {
                        childAt.setImageResource(R.drawable.dot_select);
                    } else {
                        childAt.setImageResource(R.drawable.dot_normal);
                    }
                }
            }
        });
        //设置点击轮播图点击事件监听
        imageBarnnerViewGroup.setOnImageBannerListener(new ImageBarnnerViewGroup.ImageBannerListener() {
            @Override
            public void clickImageIndex(int position) {
                if (listener != null) {
                    listener.clickImageIndex(position);
                }
            }
        });
    }

    /**
     * 加载底部指示器
     */
    private void initDotLinearLayout() {
        linearLayout = new LinearLayout(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(40));
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        addView(linearLayout);

        FrameLayout.LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM;
        linearLayout.setLayoutParams(layoutParams);

    }

    /**
     * 调用该方法设置轮播图的数量
     * @param list 传入轮播的数据源
     */
    public void addPoint(List<Integer> list) {
        for (Integer integer : list) {
            addBitmapToImageBannerViewGroup(integer);
            addDotToLinearLayout();
        }
    }

    /**
     * 添加轮播的指示器
     */
    private void addDotToLinearLayout() {
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = dip2px(5);
        lp.leftMargin = dip2px(5);
        imageView.setLayoutParams(lp);
        imageView.setImageResource(R.drawable.dot_normal);
        linearLayout.addView(imageView);
    }

    /**
     * 添加轮播的图片
     * @param resId
     */
    private void addBitmapToImageBannerViewGroup(int resId) {
        ImageView iv = new ImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setImageResource(resId);
        iv.setLayoutParams(lp);
        imageBarnnerViewGroup.addView(iv);
    }

    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    /**
     * 获取手机屏幕的宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void setOnImageBannerListener(ImageBannerListener listener) {
        this.listener = listener;
    }

    public interface ImageBannerListener {
        void clickImageIndex(int position);
    }
    /**
     * 开始播放轮播图
     */
    public void startAuto(){
        imageBarnnerViewGroup.startAuto();
    }

    /**
     * 暂停播放轮播图
     */
    public void stopAuto(){
        imageBarnnerViewGroup.stopAuto();
    }

    /**
     * 当页面销毁的时候调用该方法
     */
    public void destoryAuto(){
        imageBarnnerViewGroup.destoryAuto();
    }
}
