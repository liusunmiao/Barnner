package com.lsm.barnner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.lsm.barnner.view.ImageBannerFramLayout;
import com.lsm.barnner.view.ImageBarnnerViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Integer> ids;
    private ImageBannerFramLayout bannerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bannerLayout = findViewById(R.id.banner_layout);
        ids = new ArrayList<>();
        ids.add(R.mipmap.banner);
        ids.add(R.mipmap.baner01);
        ids.add(R.mipmap.banner02);
        ids.add(R.mipmap.baner04);
        ids.add(R.mipmap.banner05);
        ids.add(R.mipmap.banner06);
        bannerLayout.addPoint(ids);

        bannerLayout.setOnImageBannerListener(new ImageBannerFramLayout.ImageBannerListener() {
            @Override
            public void clickImageIndex(int position) {
                Toast.makeText(MainActivity.this, "点击了" + position, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        bannerLayout.stopAuto();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bannerLayout.startAuto();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bannerLayout.destoryAuto();
    }
}
