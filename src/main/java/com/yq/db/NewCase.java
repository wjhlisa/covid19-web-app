package com.yq.db;

import java.text.MessageFormat;

import javax.persistence.*;


@Entity
public class NewCase
{
	@EmbeddedId
	private NewCasePK pk;

	@Column(nullable = false)
	private int num;

	public NewCase()
	{
	}

	public NewCase(NewCasePK pk, int num)
	{
		this.pk = pk;
		this.num = num;
	}

	public NewCasePK getPk()
	{
		return pk;
	}

	public void setPk(NewCasePK pk)
	{
		this.pk = pk;
	}

	public int getNum()
	{
		return num;
	}

	public void setNum(int num)
	{
		this.num = num;
	}

	@Override
	public String toString()
	{
		return MessageFormat.format("{0} {1}", pk.toString(), getNum());
	}
}
