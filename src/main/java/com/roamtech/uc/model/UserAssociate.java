package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by roam-caochen on 2017/3/2.
 */

@Entity
@XmlRootElement
@Table(name = "user_associate", uniqueConstraints = {@UniqueConstraint(columnNames = {
        "userid"})})
public class UserAssociate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @JSONField(name = "id")
    @Column(name = "id")
    private Long id;

    @JSONField(name = "userid")
    @Column(name = "userid")
    private Long userId;

    @JSONField(name = "parent_userid")
    @Column(name = "parent_userid")
    private Long parentUserId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(Long parentUserId) {
        this.parentUserId = parentUserId;
    }
}
