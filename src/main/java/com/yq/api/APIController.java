package com.yq.api;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yq.db.*;

@RestController
public class APIController
{
	@Autowired
	private NewCaseRepository newCaseRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(NewCaseTimer.class);
	
	//小程序调用，返回json
	@RequestMapping(value = "/api/{state}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public List<Record> get(@PathVariable String state)	
	{
		//先判断state是否合法
		if (!Arrays.stream(NewCaseTimer.STATES).anyMatch(s -> s.toUpperCase().equals(state.toUpperCase()))
				&& !state.toUpperCase().equals("AU"))
		{
			return null;
		}
		
		List<Record> results;
		
		if (state.toUpperCase().equals("AU"))
		{
			//取全国数据 只取最近30天记录
			results = newCaseRepository.getForCountry().subList(0, 30);
		}
		else
		{
			results = newCaseRepository.getByState(state).subList(0, 30);
		}
		
		logger.info(MessageFormat.format("Returned WeChat request for {0}", state));
		return results;
	}
}
