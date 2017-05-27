package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlRootElement
@Table(name = "ringback")
public class Bell implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JSONField(name = "id")
    @Column(name = "id")
    private Long Id;

    @JSONField(name = "url")
    @Column(name = "url")
    private String url;


    @JSONField(name = "starttime",format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "starttime")
    private Date startTime;

    @JSONField(name = "endtime",format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "endtime")
    private Date endTime;

    @JSONField(name = "tenantid")
    @Column(name = "tenantid")
    private long tenantId;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }
}
