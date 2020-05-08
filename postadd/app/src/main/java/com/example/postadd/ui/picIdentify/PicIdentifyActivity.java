package com.example.postadd.ui.picIdentify;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.postadd.R;
import com.example.postadd.base.BaseApp;
import com.example.postadd.base.Constants;
import com.example.postadd.bean.DataBean;
import com.example.postadd.util.LogUtil;
import com.example.postadd.util.RandomImageUtil;
import com.example.postadd.util.ToastUtil;


import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PicIdentifyActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv)
    ImageView mIv;
    @BindView(R.id.iv_left)
    ImageView mIvLeft;
    @BindView(R.id.iv_right)
    ImageView mIvRight;
    @BindView(R.id.cl)
    ConstraintLayout mCl;
    private ArrayList<DataBean> mList;//随机出来的数据
    private int mCurrentPosition = 0;//当前展示的图片索引
    private int mGameType;
    private String mTitle = BaseApp.getRes().getString(R.string.what_pic_is);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_identify);
        ButterKnife.bind(this);
        mGameType = getIntent().getIntExtra(Constants.DATA, 0);
        initView();
    }

    private void initView() {
        mList = new ArrayList<>();
        addData();
    }

    private void addData() {
        DataBean dataBean = RandomImageUtil.getInstance()
                .randomData();

        Glide.with(this).load(dataBean.getUrl()).into(mIv);
        mList.add(dataBean);

        LogUtil.print("size:" + mList.size());
        if (mGameType == Constants.TYPE_PIC_IDENTIFY){
            mTvTitle.setText(mTitle);
        }else {
            mTvTitle.setText(dataBean.getTname());
        }
        voice(false);
    }

    @OnClick({R.id.iv, R.id.iv_left, R.id.iv_right})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv:
                voice(true);
                break;
            case R.id.iv_left:
                pre();
                break;
            case R.id.iv_right:
                next();
                break;
        }
    }

    /**
     *
     * @param b true 图片被点击,false不是图片被点击
     */
    private void voice(boolean b) {
        if (mGameType == Constants.TYPE_PIC_IDENTIFY){
            if (b){
                //点击图片,直接发出图片类型的语音
                voiceName();
            }else {
                //非点击图片且是第一个游戏,发出 这是什么图片 的语音
                voiceWhatIs();
            }
        }else {
            //game 6 发出图片类型的语音
            voiceName();
        }
    }

    private void voiceWhatIs() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.what_pic_is);
        mediaPlayer.start();
    }

    /**
     * 播放语音如果每次都是新建的MediaPlayer对象如果出现多次点击图片或者箭头
     * 语音会重叠播放,所以需要将MediaPlayer封装,播放语音使用全局唯一一个MediaPlayer
     * 对象
     */
    private void voiceName() {
        String music = mList.get(mCurrentPosition).getMusic();
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(music);
            //网络资源,必须异步加载
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //加载ok了之后播放
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //下一个图片
    private void next() {
        //点击下一个添加数据的时候需要判断一下,是不是点击过上一个
        if (mCurrentPosition >= mList.size() - 1) {
            mCurrentPosition++;
            addData();
        } else {
            //当前的位置,比集合的size-1要小
            // mCurrentPosition = 3,size = 6;
            mCurrentPosition++;
            setData();
        }
    }

    ///上一个图片
    private void pre() {
        if (mCurrentPosition == 0) {
            ToastUtil.showToastShort("已经是第一张图片了");
        } else {
            mCurrentPosition--;
            setData();
        }
    }

    private void setData() {
        DataBean dataBean = mList.get(mCurrentPosition);
        Glide.with(this).load(dataBean.getUrl()).into(mIv);
    }


}
