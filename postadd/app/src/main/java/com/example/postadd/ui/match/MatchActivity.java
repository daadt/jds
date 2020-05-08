package com.example.postadd.ui.match;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.postadd.R;
import com.example.postadd.bean.DataBean;
import com.example.postadd.bean.LocationBean;
import com.example.postadd.util.RandomImageUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


//1.可以滑动的图片
//2.图片重叠判断
public class MatchActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_result)
    TextView mTvResult;
    @BindView(R.id.rlv)
    RecyclerView mRlv;
    @BindView(R.id.iv_target)
    CustomImageView mIvTarget;
    private ArrayList<DataBean> mList;
    private BaseQuickAdapter<DataBean, BaseViewHolder> mAdapter;
    private DataBean mTarget;
    private ArrayList<LocationBean> mLocationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mList = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<DataBean, BaseViewHolder>(R.layout.item_main, mList) {
            @Override
            protected void convert(BaseViewHolder helper, DataBean item) {
                ImageView iv = (ImageView) helper.getView(R.id.iv);
                Glide.with(mContext).load(item.getUrl()).into(iv);
            }
        };
        mRlv.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter.bindToRecyclerView(mRlv);

        initGame();

        mIvTarget.setOnMoveListener(new CustomImageView.OnMoveListener() {
            @Override
            public void onMove(int l, int t, int r, int b) {
                //目标图片的上下左右,
                //在这个方法里面比较目标图片和列表图片是否重合

                /**
                 * a.判断两张图片某个角间距小于某个值，例如图片B的左上角和图片A左上角距
                 * 离小于某个值（r），我们就说它们重合了
                 * b.图片A比较大，可以完全把图片B给包裹起来
                 * c.图片B比较大，可以完全把A包裹起来
                 */

                //像素
                int dSpace = 15;
                for (int i = 0; i < mLocationList.size(); i++) {
                    LocationBean locationBean = mLocationList.get(i);
                    //a.判断两张图片某个角间距小于某个值，例如图片B的左上角和图片A左上角距
                    // 离小于某个值（r），我们就说它们重合了
                    //左上角
                    int dl = Math.abs(l - locationBean.l);
                    int dt = Math.abs(t - locationBean.t);
                    int dr = Math.abs(r - locationBean.r);
                    int db = Math.abs(b - locationBean.b);

                    boolean b1 = dl<dSpace && dt<dSpace|| //左上角重叠
                            dr<dSpace && dt<dSpace ||//右上角
                            dl<dSpace && db<dSpace ||//左下角
                            dr<dSpace && db<dSpace;//右下角;

                    //b.列表图片比较大，可以完全把目标给包裹起来
                    boolean b2 = locationBean.l<= l &&
                            locationBean.t <= t &&
                            locationBean.r >= r &&
                            locationBean.b >= b;

                    //c.目标图片比较大，可以完全把列表图片包裹起来
                    boolean b3 = locationBean.l >= l &&
                            locationBean.t >= t &&
                            locationBean.r <= r &&
                            locationBean.b <= b;

                    if (b1 || b2 || b3){
                        //重叠了
                        checkResult(locationBean.index);
                        //结束循环
                        break;
                    }
                }

            }
        });

        //定位渲染完成的监听
        mRlv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for (int i = 0; i < mList.size(); i++) {
                    //根据索引获取某个孩子
                    View childAt = mRlv.getChildAt(i);
                    Rect rect = new Rect();
                    ////获取控件在屏幕上的可视视图区域
                    childAt.getGlobalVisibleRect(rect);
                    mLocationList.add(new LocationBean(i,rect.left,rect.top,rect.right,rect.bottom));
                }
                mRlv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    //判断重合的图片和目标图片是否是同一张
    private void checkResult(int index) {
        if (mTarget.getImageid() == mList.get(index).getImageid()){
            //做对了
        }else {
            //错了
        }
    }

    private void initGame() {
        mList.clear();
        //初始化游戏
        //完全匹配,列表中有且只有一张图片和目标图片完全一样
        mTarget = RandomImageUtil.getInstance()
                .randomData();
        Glide.with(this).load(mTarget.getUrl()).into(mIvTarget);

        mList.add(mTarget);

        //其他5张图片不能和目标图片一样
        addData(5);

        mAdapter.notifyDataSetChanged();
    }

    private void addData(int number) {
        for (int i = 0; i < number; i++) {
            DataBean dataBean = RandomImageUtil.getInstance()
                    .randomDataType(mTarget.getTypeid());
            mList.add(dataBean);
        }
    }

    private class CustomImageView {
    }
}
