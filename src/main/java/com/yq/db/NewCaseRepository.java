package com.yq.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewCaseRepository extends JpaRepository<NewCase, NewCasePK>
{
	@Query("select nc.pk.date as date, nc.num as num from NewCase nc where nc.pk.state = ?1 order by nc.pk.date desc")	
	List<Record> getByState(String state);
	
	//只取所有州都有数据的日期
	@Query("select nc.pk.date as date, sum(nc.num) as num from NewCase nc group by nc.pk.date having count(*) = 8 order by nc.pk.date desc")
	List<Record> getForCountry();
}
