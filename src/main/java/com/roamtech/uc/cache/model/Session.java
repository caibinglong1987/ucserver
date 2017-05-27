package com.roamtech.uc.cache.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.model.AdminRole;
import com.roamtech.uc.model.RoleModule;

import java.io.Serializable;
import java.util.List;

public class Session implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JSONField(name = "sessionid")
	private String sessionId;
	@JSONField(name = "userid")
	private Long userId;
	@JSONField(name = "createtime")
	private Long createTime;
	@JSONField(name = "usertype")
	private Integer usertype;
	@JSONField(name = "tenantid")
	private Long tenantId;
	@JSONField(name = "packagename")
	private String packageName;
	@JSONField(name = "bundleid")
	private String bundleId;
	private List<AdminRole> adminroles;
	private List<RoleModule> rolemodules;	
	public List<AdminRole> getAdminroles() {
		return adminroles;
	}
	public void setAdminroles(List<AdminRole> adminroles) {
		this.adminroles = adminroles;
	}
	public List<RoleModule> getRolemodules() {
		return rolemodules;
	}
	public void setRolemodules(List<RoleModule> rolemodules) {
		this.rolemodules = rolemodules;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getUsertype() {
		return usertype;
	}
	public void setUsertype(Integer usertype) {
		this.usertype = usertype;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
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
}
