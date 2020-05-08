package com.example.postadd.net;




import com.example.postadd.bean.ImageBean;

import io.reactivex.Flowable;

import retrofit2.http.GET;


public interface ApiService {

    @GET("games/img/getImgList")
    Flowable<ImageBean> getImageList();
}
