package com.roamtech.uc.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Vector;

@Entity
@XmlRootElement
@Table(name = "appreleased")
public class AppReleased implements Serializable {
	public static final int NOTRELEASED_STATUS=0;
	public static final int AUDITING_STATUS=1;
	public static final int RELEASED_STATUS=2;
	public static final int UNSHELVE_STATUS = 3;//下架
	private static final long serialVersionUID = 1L;	
	@Id
	@Column(name = "id")
	private Long Id;


	@Column(name = "type")
	private Integer type;
	
	@Column(name = "app_name")
	private String appName;	
	
	@Column(name = "version")
	private Integer version;
	
	@Column(name = "supported_min_version")
	private Integer supportedMinVersion;
	
	@Column(name = "version_name")
	private String versionName;

	@Column(name = "pkgname")
	private String pkgName;
	
	@Column(name = "url")
	private String url;	
	
	@Column(name = "releasetime")
	private Long releaseTime;	

	@Column(name = "description")
	private String description;	
	
	@Column(name = "filesize")
	private Integer fileSize;

	@Column(name = "status")
	private Integer status;
	
	@Override
	public int hashCode() {
		return (Id == null) ? super.hashCode() : Id
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
		AppReleased entity = (AppReleased) other;
		if (Id == null && entity.Id == null) {
			return super.equals(entity);
		}
		if ((Id != null && entity.Id == null)
				|| (Id == null && entity.Id != null)) {
			return false;
		}
		return Id.equals(entity.Id);
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this,
				ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Long releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}
	
	public Integer getSupportedMinVersion() {
		return supportedMinVersion;
	}

	public void setSupportedMinVersion(Integer supportedMinVersion) {
		this.supportedMinVersion = supportedMinVersion;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	static public class AppType {
		static private Vector<AppType> values = new Vector<AppType>();
		/**
		 * 络漫宝应用
		 */
		static public AppType TouchApp = new AppType(1, "络漫宝应用");
		/**
		 * 漫话android
		 */
		static public AppType RoamAppAndroid = new AppType(2, "漫话android");
		/**
		 * 漫话ios
		 */
		static public AppType RoamAppIos = new AppType(3, "漫话ios");

		public final int mValue;
		private final String mStringValue;

		private AppType(int value, String stringValue) {
			mValue = value;
			values.addElement(this);
			mStringValue = stringValue;
		}
		public static AppType fromInt(int value) {
			for (int i = 0; i < values.size(); i++) {
				AppType mstate = (AppType) values.elementAt(i);
				if (mstate.mValue == value) return mstate;
			}
			throw new RuntimeException("AppType not found [" + value + "]");
		}
		public String toString() {
			return mStringValue;
		}
	}
}

