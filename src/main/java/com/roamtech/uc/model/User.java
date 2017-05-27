package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlRootElement
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = {
		"username", "tenantid", "phone_type"}), @UniqueConstraint(columnNames = {
		"phone", "tenantid", "phone_type"}), @UniqueConstraint(columnNames = {
		"email", "tenantid", "phone_type"})})
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "userid")
	@Column(name = "userid")
	private Long userId;

	@Column(name = "createtime")
	private Long createTime;

	@Column(name = "createdate")
	@JSONField(name = "createdate",format="yyyy-MM-dd HH:mm:ss")
	private Date createDate;

	@Column(name = "username")
	@JSONField(name = "username")
	private String userName;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "status")
	private int status;
	
	@Column(name = "nickname")
	private String nickName;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "type")
	@JSONField(name = "type")
	private Integer type;

	@Column(name = "tenantid")
	@JSONField(name = "tenantid")
	private Long tenantId;

	@Column(name = "phone_type")
	@JSONField(name = "phone_type")
	private Integer phoneType;

//	@OneToOne(optional = false, cascade = CascadeType.REFRESH, mappedBy="user_profile")
//	@JoinColumn(name = "userid")
	@Transient
	private UserProfile userProfile;
	
	
//	@OneToOne(optional = false, cascade = CascadeType.REFRESH, mappedBy="employee_profile")
//	@JoinColumn(name = "userid")
	@Transient
	private EmployeeProfile employeeProfile;
/*	@ElementCollection(fetch= FetchType.EAGER)
	@CollectionTable(name = "phonelist", joinColumns = @JoinColumn(name = "userid"))
	private Set<Phone> phones;

	@ElementCollection(fetch= FetchType.EAGER)
	@CollectionTable(name = "touchdev", joinColumns = @JoinColumn(name = "userid"))
	private Set<Touch> touchs;*/
	
	public final static int STATUS_INACTIVE = 0;
	public final static int STATUS_ACTIVE = 1;
	public final static int STATUS_FROZEN = 2;
	public final static int NORMAL_USER = 0;
	public final static int CLERK_USER = 1;
	public final static int EMPLOYEE_USER = 2;
	public final static int OPERATOR_USER = 3;
	public final static int ADMIN_USER = 4;
	public final static long ROAM_TENANT_ID = 1L;

	@Override
	public int hashCode() {
		return (userId == null) ? super.hashCode() : userId
				.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		User entity = (User) other;
		if (userId == null && entity.userId == null) {
			return super.equals(entity);
		}
		if ((userId != null && entity.userId == null)
				|| (userId == null && entity.userId != null)) {
			return false;
		}
		return userId.equals(entity.userId);
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this,
				ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	@PrePersist
	public void updateTimeStamps() {
		if (createTime == null) {
			createDate = new Date();
			createTime = createDate.getTime();
		}
		if (tenantId == null) {
			tenantId = ROAM_TENANT_ID;
		}
		if (phoneType == null) {
			phoneType = Phone.TYPE_PHONE;
		}
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(Integer phoneType) {
		this.phoneType = phoneType;
	}

	/*	public Set<Phone> getPhones() {
		return phones;
	}

	public void setPhones(Set<Phone> phones) {
		this.phones = phones;
	}

	public Set<Touch> getTouchs() {
		return touchs;
	}

	public void setTouchs(Set<Touch> touchs) {
		this.touchs = touchs;
	}*/
	
}
