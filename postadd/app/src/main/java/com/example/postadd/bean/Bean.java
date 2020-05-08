package com.example.postadd.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Bean {
    @Id
    public Long id;

    @Generated(hash = 249097931)
    public Bean(Long id) {
        this.id = id;
    }

    @Generated(hash = 80546095)
    public Bean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
