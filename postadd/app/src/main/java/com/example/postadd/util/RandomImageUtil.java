package com.example.postadd.util;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.TextureView;


import com.example.postadd.base.BaseApp;
import com.example.postadd.bean.DataBean;
import com.example.postadd.db.DataBeanDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomImageUtil {
    //1.取出数据:list,随机一个索引出来(index< list.size),  需要: list(所有数据),随机数
    //2.随机一个类型,根据类型至少要找这个类型的一张图片,剩下的图片随机 需要:list(类型),随机数,分类型的集合
    //3.随机一个图片,底部列表中只能有一张和目标一样的图片,
    Random mRandom = new Random();
    private static volatile RandomImageUtil sRandomImageUtil;
    //分类型的所有的数据:key是类型typeid,value是某类图片的集合
    SparseArray<ArrayList<DataBean>> mAll = new SparseArray<>();
    //所有类型的集合:typeid
    ArrayList<Integer> mTypes = new ArrayList<>();
    //根据typeid取汉语的类别名称,key:typeid,value:类别名称
    SparseArray<String> mNames = new SparseArray<>();

    private RandomImageUtil() {
        //处理数据
        DataBeanDao dataBeanDao = BaseApp.sContext.getDaoSession().getDataBeanDao();
        List<DataBean> list = dataBeanDao.queryBuilder().list();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                DataBean dataBean = list.get(i);
                //根据key获取里面的分类list,因为没有添加过对应的数据,所以list可能空
                ArrayList<DataBean> typeList = mAll.get(dataBean.getTypeid());

                if (typeList == null) {
                    typeList = new ArrayList<>();
                    mAll.append(dataBean.getTypeid(), typeList);
                }
                typeList.add(dataBean);

                //所有类型的集合:typeid
                if (!mTypes.contains(dataBean.getTypeid())) {
                    mTypes.add(dataBean.getTypeid());
                }

                //根据typeid取汉语的类别名称,key:typeid,value:类别名称
                //如果根据key取出的value是空的,添加,不为空不添加
                if (TextUtils.isEmpty(mNames.get(dataBean.getTypeid()))) {
                    mNames.append(dataBean.getTypeid(), dataBean.getTname());
                }
            }
        }
    }

    public static RandomImageUtil getInstance() {
        if (sRandomImageUtil == null) {
            synchronized (RandomImageUtil.class) {
                if (sRandomImageUtil == null) {
                    sRandomImageUtil = new RandomImageUtil();
                }
            }
        }
        return sRandomImageUtil;
    }

    /**
     * 随机一个数,范围是 0 到number -1
     * @param number
     * @return
     */
    public int randomNumber(int number){
        //0到number,含0 不含number
        int i = mRandom.nextInt(number);
        return i;
    }

    /**
     * 随机一个数据出来
     * @return
     */
    public DataBean randomData(){
        //需要从SparseArray<ArrayList<DataBean>> mAll
        //分两步完成
        //1.随机一个typeid
        int i = randomNumber(mTypes.size());
        Integer typeId = mTypes.get(i);
        ArrayList<DataBean> list = mAll.get(typeId);
        //2.随机一个index
        int index = randomNumber(list.size());
        DataBean dataBean = list.get(index);
        return dataBean;
    }
}
