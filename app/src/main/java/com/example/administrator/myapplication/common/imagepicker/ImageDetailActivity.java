package com.example.administrator.myapplication.common.imagepicker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.utils.StatusBarCompat;


public class ImageDetailActivity extends AppCompatActivity {
    public static final String IMAGE_URL = "image_url";
    private String url;



    protected void compatTopColor() {
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
/*        Transition explode = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            explode = TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setEnterTransition(explode);
            }
        }*/
//        initTitleLayout(getString(R.string.image_detail));
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color_trans));

        url = getIntent().getStringExtra(IMAGE_URL);
        Log.e("ii","缩放的土坯爱你："+url);
        Glide.with(this)
                .load(url)
                .into((ImageView) findViewById(R.id.iv));

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void back() {
//        getWindow().setBackgroundDrawableResource(R.color.transparent_all);
//        binding.rl.setBackgroundColor(R.color.transparent_all);
        /*Animation anim = AnimationUtils.loadAnimation(this, R.anim.photo_detail_out_anim);
        binding.iv.startAnimation(anim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageDetailActivity.super.onBackPressed();
            }
        }, 1000);*/
        ImageDetailActivity.super.onBackPressed();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.photo_detail_in_anim);
        ((ImageView) findViewById(R.id.iv)).startAnimation(anim);
    }


    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // back button
                break;
        }
        return true;
    }
}
