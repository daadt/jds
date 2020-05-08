package com.example.postadd.ui.main;


import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.postadd.R;
import com.example.postadd.base.BaseActivity;
import com.example.postadd.base.BaseApp;
import com.example.postadd.base.Constants;

import com.example.postadd.bean.DataBean;
import com.example.postadd.bean.ImageBean;
import com.example.postadd.db.DataBeanDao;
import com.example.postadd.net.HttpUtil;
import com.example.postadd.net.ResultSubScriber;
import com.example.postadd.net.RxUtils;
import com.example.postadd.presenter.MainPresenter;
import com.example.postadd.ui.picIdentify.PicIdentifyActivity;
import com.example.postadd.util.LogUtil;
import com.example.postadd.util.ThreadPoolManager;
import com.example.postadd.view.MainView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;


//网络回来的数据需要插入数据库
public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    @BindView(R.id.rlv)
    RecyclerView mRlv;

    private ResultSubScriber<ImageBean> mSubScriber;
    private DataBeanDao mDataBeanDao;
    ////onlyRetrieveFromCache(true),true代表只从缓存中查找
    private RequestOptions mOptions = new RequestOptions().onlyRetrieveFromCache(true);
    private PopupWindow mDownloadPop;
    private ProgressBar mPb;
    private TextView mTvProgress;
    private int mMax;
    private int mProgress;

    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void initData() {

        //全局的界面渲染完成的监听
        mRlv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                //界面定位渲染完成
                downloadPop();
                //移除监听
                mRlv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mSubScriber = HttpUtil.getInstance()
                .getApiService()
                .getImageList()
                .compose(RxUtils.<ImageBean>rxSchedulerHelper())
                .subscribeWith(new ResultSubScriber<ImageBean>() {
                    @Override
                    protected void onSuccess(ImageBean imageBean) {
                        if (imageBean.getStatus() == Constants.SUCCESS_CODE) {
                            //1.将数据插入数据库
                            saveDataLoadImage(imageBean);
                        }
                    }
                });
    }

    private void downloadPop() {
        //PopupWindow 弹出的时候依赖别的控件,所以在他依赖的控件没有在界面上展示
        //出来之前,不能弹
        //布局,宽,高
        View inflate = LayoutInflater.from(this).inflate(R.layout.download_pop, null);
        mPb = inflate.findViewById(R.id.pb);
        mTvProgress = inflate.findViewById(R.id.tv_progress);
        mDownloadPop = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.MATCH_PARENT);
        mDownloadPop.showAtLocation(mRlv, Gravity.CENTER,0,0);
    }

    private void saveDataLoadImage(ImageBean imageBean) {
        //1.将数据插入数据库
        if (imageBean.getData() != null && imageBean.getData().size() > 0) {
            List<DataBean> data = imageBean.getData();
            mDataBeanDao.insertOrReplaceInTx(data);

            //2.下载图片//3.下载语音
            loadImageMusic(data);
        }
    }
    ////2.下载图片//3.下载语音
    private void loadImageMusic(List<DataBean> data) {
        //下载图片使用glide下载,指定glide下载地址
        //这里面如果new thread 不好
        //如果有1w张图片这样做会创建1w线程
        //线程开启和销毁特别销毁系统的资源

        //线程池:不管用不用保留几个线程,如果任务多了,会有排队机制
        mMax = data.size();
        mPb.setMax(mMax);
        for (int i = 0; i < data.size(); i++) {
            String url = data.get(i).getUrl();
            ThreadPoolManager.getInstance()
                    .execute(new Runnable() {
                        @Override
                        public void run() {
                            //使用glide下载图片
                            /*if (如果图片没有下载过) {
                                downloadImage(url);
                            }*/
                            if (!isImageDownloaded(url)) {
                                downloadImage(url);
                            }
                        }
                    });
        }
    }
    /**
     * 判断图片是否下载过
     * @param url
     * @return true,下载过,false没有下载过
     */
    public boolean isImageDownloaded(String url){
        //onlyRetrieveFromCache(true),true代表只从缓存中查找
        try {
            File file = Glide.with(BaseApp.sContext).downloadOnly().apply(mOptions)
                    .load(url).submit().get();

            if (file != null){
                //代表本地缓存有图片,说明下载过
                LogUtil.print("cache path:"+file.getAbsolutePath());
                mProgress++;
                setProgress();
                return true;
            }else {
                return false;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;

    }
    public void setProgress(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPb.setProgress(mProgress);
                mTvProgress.setText(mProgress+" / "+mMax);
                if (mProgress>= mMax){
                    //下载完成了
                    mDownloadPop.dismiss();
                    showToast("下载完成");
                }
            }
        });
    }
    private void downloadImage(String url) {
        FutureTarget<File> target = Glide.with(BaseApp.sContext)
                .asFile()
                .load(url)
                .submit();
        try {
            final File imageFile = target.get();
            mProgress++;
            setProgress();
            //imageFile.getAbsolutePath():绝对路径
            LogUtil.print("download path:" + imageFile.getAbsolutePath());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubScriber.dispose();
    }

    @Override
    protected void initView() {
        mDataBeanDao = BaseApp.sContext.getDaoSession().getDataBeanDao();
        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.drawable.pictureidentification);
        list.add(R.drawable.expressivelabeling);
        list.add(R.drawable.identicalmatching);
        list.add(R.drawable.similarmatching);
        list.add(R.drawable.sorting);
        list.add(R.drawable.receptivelabeling);

        BaseQuickAdapter<Integer, BaseViewHolder> adapter = new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.item_main, list) {
                    @Override
                    protected void convert(BaseViewHolder helper, Integer item) {
                        Glide.with(mContext).load(item).into((ImageView) helper.getView(R.id.iv));
                    }
                };

        mRlv.setLayoutManager(new GridLayoutManager(this, 2));
        mRlv.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                go2Game(position);
            }
        });
    }

    private void go2Game(int position) {
        //为了复用
        Intent intent = new Intent();
        switch (position) {
            case 0:
                intent.putExtra(Constants.DATA,Constants.TYPE_PIC_IDENTIFY);
                intent.setClass(MainActivity.this, PicIdentifyActivity.class);
                break;
            case 1:
                intent.setClass(MainActivity.this, PicIdentifyActivity.class);
                break;
            case 2:
                intent.setClass(MainActivity.this, PicIdentifyActivity.class);
                break;
            case 3:
                intent.setClass(MainActivity.this, PicIdentifyActivity.class);
                break;
            case 4:
                intent.setClass(MainActivity.this, PicIdentifyActivity.class);
                break;
            case 5:
                intent.putExtra(Constants.DATA,Constants.TYPE_RECEPTIVE);
                intent.setClass(MainActivity.this, PicIdentifyActivity.class);
                break;
        }

        startActivity(intent);
    }
    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }
}
