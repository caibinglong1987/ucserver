package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by roam-caochen on 2016/12/9.
 */

@Entity
@XmlRootElement
@Table(name = "application")
public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @JSONField(name = "id")
    @Column(name = "id")
    private Long Id;

    @JSONField(name = "name")
    @Column(name = "name")
    private String name;

    @JSONField(name = "secret")
    @Column(name = "secret")
    private String secret;

    @JSONField(name = "packagename")
    @Column(name = "packagename")
    private String packageName;

    @JSONField(name = "bundleid")
    @Column(name = "bundleid")
    private String bundleId;

    @JSONField(name = "developerid")
    @Column(name = "developerid")
    private Long developerId;

    @JSONField(name = "settlement")
    @Column(name = "settlement")
    private Integer settlement;

    @JSONField(name = "status")
    @Column(name = "status")
    private Integer status;

    @JSONField(name = "logo")
    @Column(name = "logo")
    private String logo;

    @JSONField(name = "description")
    @Column(name = "description")
    private String description;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    public Integer getSettlement() {
        return settlement;
    }

    public void setSettlement(Integer settlement) {
        this.settlement = settlement;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
