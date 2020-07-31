package com.yq.db;

import com.yq.*;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewCaseTimer
{
	@Autowired
	private NewCaseRepository newCaseRepository;

	private static final Logger logger = LoggerFactory.getLogger(NewCaseTimer.class);

	private static final String YQ_URL = "http://covidlive.com.au/report/daily-cases/";

	public static String[] STATES =
	{ "vic", "nsw", "qld", "sa", "act", "tas", "wa", "nt" };

	private static Map<String, String> SHORT_MONTH_NUM = new HashMap<String, String>()
	{
		{
			put("Jan", "01");
			put("Feb", "02");
			put("Mar", "03");
			put("Apr", "04");
			put("May", "05");
			put("Jun", "06");
			put("Jul", "07");
			put("Aug", "08");
			put("Sep", "09");
			put("Oct", "10");
			put("Nov", "11");
			put("Dec", "12");
		}
	};

	private static Map<String, String> FULL_MONTH_NUM = new HashMap<String, String>()
	{
		{
			put("January", "01");
			put("February", "02");
			put("March", "03");
			put("April", "04");
			put("May", "05");
			put("June", "06");
			put("July", "07");
			put("August", "08");
			put("September", "09");
			put("October", "10");
			put("November", "11");
			put("December", "12");
		}
	};

	// 每个小时下载最新数据，并更新数据库
	// Spring的@Scheduled在默认情况下，会运行在单独的一个线程中。因为我们只有一个@Scheduled的method，所以不会等待。而且不会影响小程序的请求调用
	// https://stackoverflow.com/questions/21993464/does-spring-scheduled-annotated-methods-runs-on-different-threads
	/**
	 * 
	 */
	// 疫情紧急，每10分钟获取最新数据
	@Scheduled(fixedDelay = 600000)
	public void downloadData()
	{
		// 异常邮件时使用
		String url = "";

		try
		{
			// 下载每个州的数据
			for (String state : STATES)
			{
				// 使用 jsoup抓取网页数据
				// https://www.jianshu.com/p/758b189a22c5
				url = YQ_URL + state;
				Document doc = Jsoup.connect(url).get();
				Elements es = doc.getElementById("content").select("div div section table tbody tr");

				Elements latestChildren = es.get(es.size() - 1).children();

				// "Wed 8 Jul"
				String[] latestDateArray = latestChildren.get(0).text().split("\\s+");
				String date = MessageFormat.format("2020-{0}-{1}", SHORT_MONTH_NUM.get(latestDateArray[2]),
											String.format("%02d", Integer.parseInt(latestDateArray[1])));

				// "118"
				String latestNumText = latestChildren.get(3).getElementsByTag("span").text();
				int num;
				try
				{
					num = Integer.parseInt(latestNumText);
				}
				catch (NumberFormatException e)
				{ // 今天数据还未更新，不处理。
					continue;
				}
				if (num < 0)
				{ // 有时会出现负数
					num = 0;
				}

				// 插入或更新数据库
				NewCase nc = new NewCase(new NewCasePK(state, date), num);
				newCaseRepository.save(nc);
				logger.info(MessageFormat.format("------------ Updated data in DB： {0} {1} {2}", state, date, num));

			}
		}
		catch (Exception ex)
		{
			logger.error(MessageFormat.format("------------ Failed to scrape data from {0}: {1}", url,
										ex.getMessage()));
			YqUtil.SendMail("抓取疫情数据失败", MessageFormat.format("{0}\n{1}", url, ex.getMessage()));
		}
	}
}
