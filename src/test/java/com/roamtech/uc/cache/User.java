package com.roamtech.uc.cache;

import java.io.Serializable;

public class User implements Serializable {
	private String name;
	private Integer age;
	public User() {
		
	}
	public User(String name, Integer age) {
		setName(name);
		setAge(age);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	@Override
	public boolean equals(Object other) {
		User ouser = (User)other;
		if(ouser.name.equals(name) && ouser.age.equals(age)) {
			return true;
		}
		return false;
	}
}
