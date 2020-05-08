package com.example.postadd.widget;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //缓存和下载的图片都在这个路径下面
        //sdcard/Android/data/包名/cache/images
        //diskCacheSize:单位byte,1024(1kb) *1024 = 1M * 1024 = 1G
        //1080*1920
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(
                context,"images",1024*1024*1024));
    }
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
    }
}