package com.yq.db;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class NewCasePK implements Serializable
{
	//主键不能超过1000 bytes，否则表无法创建。
	//String默认为Varchar(255)，所以这里需要指定长度
	@Column(length = 50)
	private String state;

	// 不能用类型Date作为主键，否则无法查询
	@Column(length = 50)
	private String date;

	// hashCode() and eqaules()的实现
	// https://www.jianshu.com/p/7a0de63d5f99

	public NewCasePK()
	{
	}

	public NewCasePK(String state, String date)
	{
		this.setState(state);
		this.setDate(date);
	}

	@Override
	public int hashCode()
	{
		return getState().hashCode() + getDate().hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		NewCasePK np = (NewCasePK)o;

		return this.getState().equals(np.getState()) && getDate().equals(np.getDate());
	}

	@Override
	public String toString()
	{
		return MessageFormat.format("{0}, {1}", getState(), getDate());
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}
}
