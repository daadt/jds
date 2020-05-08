package com.example.postadd.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DataBean {
    /**
     * imageid : 127
     * typeid : 15
     * mname : ic_floor_006
     * url : http://47.110.151.50:8080/images/15-花/ic_floor_006.jpg
     * imgtime : 2020-04-08 00:41:14
     * tname : 花
     * music : null
     */

    @Id
    private String url;
    private int imageid;
    private int typeid;
    private String mname;
    private String imgtime;
    private String tname;
    private String music;
    @Generated(hash = 1763080484)
    public DataBean(String url, int imageid, int typeid, String mname,
            String imgtime, String tname, String music) {
        this.url = url;
        this.imageid = imageid;
        this.typeid = typeid;
        this.mname = mname;
        this.imgtime = imgtime;
        this.tname = tname;
        this.music = music;
    }
    @Generated(hash = 908697775)
    public DataBean() {
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getImageid() {
        return this.imageid;
    }
    public void setImageid(int imageid) {
        this.imageid = imageid;
    }
    public int getTypeid() {
        return this.typeid;
    }
    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }
    public String getMname() {
        return this.mname;
    }
    public void setMname(String mname) {
        this.mname = mname;
    }
    public String getImgtime() {
        return this.imgtime;
    }
    public void setImgtime(String imgtime) {
        this.imgtime = imgtime;
    }
    public String getTname() {
        return this.tname;
    }
    public void setTname(String tname) {
        this.tname = tname;
    }
    public String getMusic() {
        return this.music;
    }
    public void setMusic(String music) {
        this.music = music;
    }
}