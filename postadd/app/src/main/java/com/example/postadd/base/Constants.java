package com.example.postadd.base;

import android.os.Environment;

import java.io.File;

//常量类
public interface Constants {
    //是否为debug状态,正式上线版本需要改为false
    boolean isDebug = true;

    String sBaseUrl = "http://47.110.151.50:8080/";


    String PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "codeest" + File.separator + "GeekNews";

    String FILE_PROVIDER_AUTHORITY="com.baidu.geek.fileprovider";

    //网络缓存的地址
    String PATH_DATA = BaseApp.sContext.getCacheDir().getAbsolutePath() +
            File.separator + "data";

    String PATH_CACHE = PATH_DATA + "/NetCache";
    String DATA = "data";


    String TOKEN = "token";
    String DESC = "description";
    String USERNAME = "userName";
    String GENDER = "gender";
    String EMAIL = "email";
    String PHOTO = "photo";
    String PHONE = "phone";
    String TYPE = "type";
    String VERIFY_CODE = "verifyCode";
    String MODE = "day_night_mode";
    String CURRENT_FRAG_TYPE = "current_frag_type";

    //成功的状态码
    int SUCCESS_CODE = 200;
    String ID = "id";
    String POSITION = "position";
    int TYPE_PIC_IDENTIFY = 0;//图片识别
    int TYPE_EXPRESSIVE = 1;//认知考核
    int TYPE_IDENTIFY_MATCH = 2;//完全匹配
    int TYPE_SIMILAR_MATCH = 3;//相似匹配
    int TYPE_SORT = 4;//图片分类
    int TYPE_RECEPTIVE = 5;//分类辨识
}