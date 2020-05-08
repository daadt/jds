package com.example.postadd.ui.expressive;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.postadd.R;
import com.example.postadd.base.BaseApp;
import com.example.postadd.bean.DataBean;
import com.example.postadd.util.LogUtil;
import com.example.postadd.util.MediaPlayerUtil;
import com.example.postadd.util.RandomImageUtil;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class ExpressiveActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.rlv)
    RecyclerView mRlv;
    @BindView(R.id.tv_skip)
    TextView mTvSkip;
    @BindView(R.id.cl)
    ConstraintLayout mCl;
    String mFind = BaseApp.getRes().getString(R.string.find);
    private ArrayList<DataBean> mList;
    private BaseQuickAdapter<DataBean, BaseViewHolder> mAdapter;
    private DataBean mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expressive);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mList = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<DataBean, BaseViewHolder>(R.layout.item_expressive, mList) {
            @Override
            protected void convert(BaseViewHolder helper, DataBean item) {
                ImageView iv = (ImageView) helper.getView(R.id.iv);
                Glide.with(mContext).load(item.getUrl()).into(iv);
            }
        };
        mRlv.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter.bindToRecyclerView(mRlv);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DataBean item = mAdapter.getData().get(position);
                TextView tv = (TextView) adapter.getViewByPosition(position,R.id.tv);
                //if (item == mTarget)
                //只要类型一致就算对
                //类型判断后要做的操作
                //1.显示textview
                //2.语音
                //3.隔段时间后textview要消失
                //4.如果对的,textview消失后进行下一个游戏
                if (item.getTypeid() == mTarget.getTypeid()) {
                    //对
                    rightOrWrong(true,tv);
                } else {
                    //不对
                    rightOrWrong(false,tv);
                }
            }

        });

        initGame();

    }

    /**
     *
     * @param right true 正确
     * @param tv
     */
    @SuppressLint("CheckResult")
    private void rightOrWrong(boolean right, TextView tv) {
        //1.显示textview
        tv.setVisibility(View.VISIBLE);
        if (right){
            tv.setBackgroundResource(R.color.green);
            tv.setText(R.string.right);
        }else {
            tv.setBackgroundResource(R.color.red);
            tv.setText(R.string.wrong);
        }
        //2.语音
        voice(right);
        //3.隔段时间后textview要消失
        //计时器:Timer+TimerTask,Handler,Rxjava,CountDownTimer
        //period:时间间隔,
        //unit:时间单位
        //每隔一定的时间发送一个事件,0,1,2,3....
        Flowable.interval(1, TimeUnit.SECONDS)
                .take(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ResourceSubscriber<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        LogUtil.print("along:"+aLong);
                        if (aLong == 1){
                            tv.setVisibility(View.GONE);
                            dispose();
                            //4.如果对的,textview消失后进行下一个游戏
                            if (right){
                                initGame();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    //2.语音
    private void voice(boolean right) {
        int resid;
        if (right){
            resid = R.raw.right;
        }else {
            resid = R.raw.wrong;
        }
        MediaPlayerUtil.getInstance()
                .setData(resid);
    }

    //初始化游戏
    private void initGame() {
        mList.clear();
        //随机一个目标图片
        mTarget = RandomImageUtil.getInstance().randomData();
        mList.add(mTarget);
        //为了避免添加了重复的图片
        DataBean dataBean = RandomImageUtil.getInstance().randomData();
        while (mList.size() < 4) {
            if (mList.contains(dataBean)) {
                //包含,说明list中已经有这张图片了
                dataBean = RandomImageUtil.getInstance().randomData();
            } else {
                //不包含,添加
                mList.add(dataBean);
            }
        }

        //打乱list中的数据
        Collections.shuffle(mList);
        mAdapter.notifyDataSetChanged();
        mTvTitle.setText(mFind + " " + mTarget.getTname());
    }

    @OnClick(R.id.tv_skip)
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_skip:
                //跳过
                skip();
                break;
        }
    }

    private void skip() {
        initGame();
    }
}
