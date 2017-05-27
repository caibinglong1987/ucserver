package com.roamtech.uc.handler;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.DoubleSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.roamtech.uc.util.Caesar;

public class SNGenerator {
	private int nodeid;
	private int nodenum;
	private int length;
	private Random random;
	private String format;
	private Date baseDate;
	private AtomicInteger seq;
	private long lastDays;
	private ReentrantLock _lockSeqLock = new ReentrantLock();
	public SNGenerator(int nodeid, int nodenum, int length, int seq) {
		this.nodeid = nodeid;
		this.nodenum = nodenum;
		this.length = length;
		random = new Random();
		format = "%0"+(length-2)+"d";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			baseDate = sdf.parse("2016-06-01");
		} catch (ParseException e) {
			
		}
		Date now = new Date();
		lastDays = (now.getTime() - baseDate.getTime())/1000/86400;
		this.seq = new AtomicInteger(seq);
	}
	public String generateSN(int seq) {
		String table = "0123456789"; 

		String ret = null,  
	            num = String.format(format, seq);  
	        int key = nodeid,//random.nextInt(10),   
	            seed = random.nextInt(10);  
		Caesar caesar = new Caesar(table, seed);  
        num = caesar.encode(key, num);  
        ret = num   
            + String.format("%01d", key)   
            + String.format("%01d", seed);  
        return ret;
	}
	public String generateSN(int seq,int seed, int length) {
		String table = "0123456789"; 

		String ret = null,  
	            num = String.format(format, seq);  
	        int key = nodeid;//random.nextInt(10),   
	            //seed = random.nextInt(10);  
		Caesar caesar = new Caesar(table, seed);  
        num = caesar.encode(key, num);  
        ret = num   
            + String.format("%01d", key)   
            + String.format("%0"+length+"d", seed);  
        return ret;
	}	
	public String generateSEQ() {
		/*44 datetime 4位表示20160601为基准的天数*/
		Date now = new Date();
		long days = (now.getTime() - baseDate.getTime())/1000/86400;
		if(lastDays != days) {
			_lockSeqLock.lock();
			if(lastDays != days) {
				lastDays = days;
				seq.set(0);
			}
			_lockSeqLock.unlock();
		}
		String date = String.format("%04d", days+1000);
		return date + String.format("%04d", seq.getAndAdd(nodenum)+nodeid);
	}
	public static void main(String[] args) {
		//int calseq = ((0+2-2)/2)*2;
		//System.out.println(calseq);
		String isocode = "USA";
		isocode = isocode.substring(0,2);
		Set<String> isocodes = new HashSet<String>();
		isocodes.add(isocode);
		isocodes.add(isocode);
		System.out.println(isocodes.size());
		/*Double fee = 19.9;
		fee = fee*100;
		BigDecimal bdfee = new BigDecimal(fee).setScale(0, BigDecimal.ROUND_HALF_UP);
		System.out.println(bdfee.intValue());*/
		/*SimpleDateFormat sdf = new SimpleDateFormat("MMMd日");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			System.out.println(sdf.format(sdf1.parse("2016-08-10")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String temp = "[1,2,3]";
		
		if(temp.startsWith("[") && temp.endsWith("]")) {
			temp = temp.substring(1, temp.length()-1);
		}
		String[] scartIds = temp.split(",");
		System.out.println(scartIds);*/
	/*	String dbdata = "(4,1,172,171,7,'2016-07-16 18:39:21','2017-07-16 18:39:21',NULL,300,NULL,'来自订单',NULL,1,NULL),(5,1,173,172,7,'2016-07-16 18:51:13','2017-07-16 18:51:13',NULL,300,NULL,'来自订单',NULL,1,NULL),(21,1,231,203,2,'2016-07-18 00:00:00','2016-07-22 23:59:59','美国',0,'10370001','来自订单',NULL,1,'[100200300400500]'),(22,1,232,204,2,'2016-07-18 00:00:00','2016-07-23 23:59:59','马尔代夫',0,'10370001','来自订单',NULL,1,'[100200300400500]'),(23,1,233,205,2,'2016-07-18 00:00:00','2016-07-25 23:59:59','泰国',0,'10370001','来自订单',NULL,1,'[100200300400500]'),(35,1,247,219,3,'2016-07-19 14:09:11','2017-07-19 14:09:11',NULL,20,NULL,'来自订单',NULL,1,NULL),(58,2,273,245,5,'2016-07-20 00:00:00','2016-07-20 23:59:59','泰国',NULL,NULL,'来自订单',NULL,2,NULL),(67,1,283,255,2,'2016-07-21 00:00:00','2016-07-21 23:59:59','美国',NULL,'10370001','来自订单',NULL,1,'[100200300400500]'),(68,1,284,256,2,'2016-07-20 00:00:00','2016-07-25 23:59:59','泰国',NULL,NULL,'来自订单',NULL,1,NULL),(74,1,290,262,3,'2016-07-19 17:34:42','2017-07-19 17:34:42',NULL,20,NULL,'来自订单',NULL,1,NULL),(75,1,291,263,3,'2016-07-19 17:34:42','2017-07-19 17:34:42',NULL,20,NULL,'来自订单',NULL,1,NULL),(76,1,292,264,3,'2016-07-19 17:35:20','2017-07-19 17:35:20',NULL,30,NULL,'来自订单',NULL,1,NULL),(95,3,313,285,5,'2016-07-20 00:00:00','2016-07-20 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,2,NULL),(96,3,314,286,5,'2016-07-20 00:00:00','2016-07-20 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,2,NULL),(99,3,317,289,5,'2016-07-20 00:00:00','2016-07-20 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,2,NULL),(101,3,319,291,5,'2016-07-20 00:00:00','2016-07-20 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,2,NULL),(102,3,320,292,5,'2016-07-20 00:00:00','2016-07-20 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,2,NULL),(103,3,321,293,5,'0020-07-20 00:00:00','2016-07-20 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,2,NULL),(104,3,322,294,5,'2016-07-20 00:00:00','2016-07-20 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,2,NULL),(115,1,334,306,2,'2016-07-30 00:00:00','2016-07-30 23:59:59','泰国',NULL,'10370001','来自订单',NULL,1,'[100200300400500]'),(116,1,334,306,2,'2016-07-30 00:00:00','2016-07-30 23:59:59','泰国',NULL,'10370001','来自订单',NULL,1,'[100200300400500]'),(117,1,334,307,2,'2016-07-30 00:00:00','2016-07-30 23:59:59','泰国',NULL,NULL,'来自订单',NULL,1,NULL),(118,1,334,307,2,'2016-07-30 00:00:00','2016-07-30 23:59:59','泰国',NULL,NULL,'来自订单',NULL,1,NULL),(121,1,337,310,3,'2016-07-20 14:43:18','2017-07-20 14:43:18',NULL,10,NULL,'来自订单',NULL,1,NULL),(127,1,343,316,5,'2016-07-20 00:00:00','2016-07-30 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(128,1,343,316,5,'2016-07-20 00:00:00','2016-07-30 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(129,1,343,317,5,'2016-07-20 00:00:00','2016-07-30 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(130,1,343,317,5,'2016-07-20 00:00:00','2016-07-30 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(153,1,374,348,7,'2016-07-20 18:30:29','2017-07-20 18:30:29',NULL,300,NULL,'来自订单',NULL,1,NULL),(155,1,376,350,5,'2016-07-20 00:00:00','2016-07-20 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(159,1,380,354,7,'2016-07-20 18:50:49','2017-07-20 18:50:49',NULL,300,NULL,'来自订单',NULL,1,NULL),(160,1,382,356,2,'2016-07-30 00:00:00','2016-07-30 23:59:59','泰国',NULL,'10370001','来自订单',NULL,1,'[100200300400500]'),(161,1,383,357,2,'2016-07-30 00:00:00','2016-07-30 23:59:59','泰国',NULL,NULL,'来自订单',NULL,1,NULL),(162,1,384,359,7,'2016-07-20 21:23:49','2017-07-20 21:23:49',NULL,300,NULL,'来自订单',NULL,1,NULL),(169,4,395,370,5,'2016-07-21 00:00:00','2016-07-23 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(223,3,457,432,5,'2016-07-21 00:00:00','2016-07-29 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,NULL),(224,3,458,433,5,'2016-07-21 00:00:00','2016-07-29 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,NULL),(225,3,459,434,5,'2016-07-21 00:00:00','2016-07-29 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,NULL),(226,3,460,435,5,'2016-07-21 00:00:00','2016-07-29 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,NULL),(227,3,461,436,5,'2016-07-21 00:00:00','2016-07-29 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,NULL),(228,3,462,437,5,'2016-07-21 00:00:00','2016-07-29 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,NULL),(229,3,463,438,5,'2016-07-21 00:00:00','2016-07-29 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,NULL),(233,1,468,443,5,'2016-07-21 00:00:00','2016-07-22 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(236,4,473,448,3,'2016-07-21 15:44:10','2017-07-21 15:44:10',NULL,100,NULL,'来自订单',NULL,1,NULL),(237,4,474,449,3,'2016-07-21 15:44:10','2017-07-21 15:44:10',NULL,100,NULL,'来自订单',NULL,1,NULL),(240,44,477,452,3,'2016-07-21 15:45:37','2017-07-21 15:45:37',NULL,40,NULL,'来自订单',NULL,1,NULL),(246,4,484,459,5,'2016-07-21 00:00:00','2016-07-25 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(253,1,493,468,5,'2016-07-21 00:00:00','2016-08-25 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,10,NULL),(255,1,495,470,5,'2016-07-21 00:00:00','2016-08-25 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,10,NULL),(259,1,500,475,5,'2016-07-21 00:00:00','2016-08-25 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,10,NULL),(260,44,502,476,5,'2016-07-22 00:00:00','2016-07-30 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,NULL),(261,1,503,477,5,'2016-07-21 00:00:00','2016-08-25 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,10,'[\"100200300400500\"]'),(263,1,505,479,5,'2016-07-21 00:00:00','2016-08-25 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,10,NULL),(267,44,513,487,3,'2016-07-21 20:55:14','2017-07-21 20:55:14',NULL,300,NULL,'来自订单',NULL,1,NULL),(269,1,516,490,3,'2016-07-21 21:08:36','2017-07-21 21:08:36',NULL,100,NULL,'来自订单',NULL,1,NULL),(270,1,517,491,3,'2016-07-21 21:12:34','2017-07-21 21:12:34',NULL,100,NULL,'来自订单',NULL,1,NULL),(271,1,518,492,3,'2016-07-21 21:14:28','2017-07-21 21:14:28',NULL,300,NULL,'来自订单',NULL,1,NULL),(272,44,522,498,5,'2016-07-22 00:00:00','2016-07-22 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(273,44,522,498,5,'2016-07-22 00:00:00','2016-07-22 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(274,44,522,499,5,'2016-07-22 00:00:00','2016-07-22 23:59:59','美国',NULL,NULL,'来自订单',NULL,3,NULL),(275,44,522,499,5,'2016-07-22 00:00:00','2016-07-22 23:59:59','美国',NULL,NULL,'来自订单',NULL,3,NULL),(276,44,523,501,5,'2016-07-22 00:00:00','2016-07-22 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(283,44,532,510,5,'2016-07-22 00:00:00','2016-07-23 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,NULL),(298,44,547,528,5,'2016-07-22 00:00:00','2016-07-30 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,NULL),(300,44,549,534,3,'2016-07-22 18:23:19','2017-07-22 18:23:19',NULL,50,NULL,'来自订单',NULL,1,'[]'),(301,3,561,546,5,'2016-07-25 00:00:00','2016-09-25 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(302,3,562,547,5,'2016-07-25 00:00:00','2016-09-25 23:59:59','新加坡',NULL,NULL,'来自订单',NULL,1,'[]'),(306,4,568,553,2,'2016-07-26 00:00:00','2016-11-26 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[\"10500006\"]'),(307,4,569,554,2,'2016-07-26 00:00:00','2016-11-26 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[\"10500006\"]'),(309,4,571,556,2,'2016-07-26 00:00:00','2016-07-30 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[\"10500006\"]'),(326,4,592,577,5,'2016-07-26 00:00:00','2016-07-28 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(327,4,593,578,3,'2016-07-26 17:48:12','2017-07-26 17:48:12',NULL,400,NULL,'来自订单',NULL,1,'[]'),(329,1,595,580,3,'2016-07-26 19:06:16','2017-07-26 19:06:16',NULL,30,NULL,'来自订单',NULL,1,'[]'),(330,1,596,581,3,'2016-07-26 19:06:16','2017-07-26 19:06:16',NULL,30,NULL,'来自订单',NULL,1,'[]'),(335,44,601,586,2,'2016-07-26 00:00:00','2016-07-27 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(340,2,611,596,5,'2016-07-27 00:00:00','2016-07-28 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(344,44,615,600,5,'2016-07-28 00:00:00','2016-07-29 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(345,1,616,601,2,'2016-07-27 00:00:00','2016-10-27 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[\"10370001\"]'),(358,3,629,614,5,'2016-07-27 00:00:00','2016-07-30 23:59:59','泰国',NULL,NULL,'来自订单',NULL,1,'[]'),(362,2,635,620,5,'2016-07-27 00:00:00','2016-07-28 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(363,3,636,621,3,'2016-07-27 16:23:13','2017-07-27 16:23:13',NULL,40,NULL,'来自订单',NULL,1,'[]'),(364,44,637,624,5,'2016-07-26 00:00:00','2016-07-28 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(365,44,637,624,5,'2016-07-26 00:00:00','2016-07-28 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(366,44,637,625,3,'2016-07-27 17:31:17','2017-07-27 17:31:17',NULL,40,NULL,'来自订单',NULL,1,'[]'),(367,44,637,626,5,'2016-07-26 00:00:00','2016-07-26 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(368,44,637,626,5,'2016-07-26 00:00:00','2016-07-26 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(370,44,639,632,3,'2016-07-27 18:50:14','2017-07-27 18:50:14',NULL,500,NULL,'来自订单',NULL,1,'[]'),(371,2,641,634,5,'2016-07-27 00:00:00','2016-07-28 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(376,1,647,640,5,'2016-07-27 00:00:00','2016-07-27 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(377,1,647,640,5,'2016-07-27 00:00:00','2016-07-27 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(378,1,647,641,5,'2016-07-27 00:00:00','2016-07-27 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(379,1,647,641,5,'2016-07-27 00:00:00','2016-07-27 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(380,1,647,642,2,'2016-07-27 00:00:00','2016-07-29 23:59:59','马尔代夫',NULL,NULL,'来自订单',NULL,1,'[]'),(381,1,648,643,5,'2016-07-28 00:00:00','2016-07-28 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(382,1,649,644,5,'2016-07-28 00:00:00','2016-07-28 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(383,1,650,645,5,'2016-07-28 00:00:00','2016-07-28 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(387,3,654,649,5,'2016-07-28 00:00:00','2016-08-12 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(388,3,655,650,5,'2016-07-28 00:00:00','2016-08-12 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(389,3,656,651,5,'2016-07-28 00:00:00','2016-08-12 23:59:59','美国',NULL,NULL,'来自订单',NULL,1,'[]'),(390,3,657,652,3,'2016-07-28 11:11:10','2017-07-28 11:11:10',NULL,40,NULL,'来自订单',NULL,1,'[]'),(393,3,660,655,3,'2016-07-28 11:11:29','2017-07-28 11:11:29',NULL,500,NULL,'来自订单',NULL,1,'[]'),(395,3,662,657,3,'2016-07-28 11:15:43','2017-07-28 11:15:43',NULL,50,NULL,'来自订单',NULL,1,'[]'),(399,1,666,661,5,'2016-09-28 00:00:00','2016-11-28 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,'[]'),(402,1,669,664,3,'2016-07-28 11:22:19','2017-07-28 11:22:19',NULL,50,NULL,'来自订单',NULL,1,'[]'),(404,3,671,666,2,'2016-07-31 00:00:00','2016-08-25 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,'[\"10370002\"]'),(406,3,673,668,5,'2016-07-28 00:00:00','2016-08-24 23:59:59','欧洲',NULL,NULL,'来自订单',NULL,1,'[]'),(408,3,676,671,3,'2016-07-28 11:46:21','2017-07-28 11:46:21',NULL,40,NULL,'来自订单',NULL,1,'[]'),(419,3,694,689,3,'2016-07-28 14:05:07','2017-07-28 14:05:07',NULL,30,NULL,'来自订单',NULL,1,'[]'),(420,3,695,690,3,'2016-07-28 14:05:07','2017-07-28 14:05:07',NULL,30,NULL,'来自订单',NULL,1,'[]'),(429,1,705,700,3,'2016-07-28 16:29:46','2017-07-28 16:29:46',NULL,40,NULL,'来自订单',NULL,1,'[]')";
		String[] dataarr = dbdata.split("\\),\\(");
		
		for (String elem : dataarr) {	
			StringBuilder sb = new StringBuilder();
			String [] elemarr = elem.split(",");
			elemarr[13] = elemarr[13].replace(")", "");
			sb = sb.append("UPDATE `orderdetail` set ").append("userid="+elemarr[1]+",")
			.append("effect_datetime="+elemarr[5]+",").append("failure_datetime="+elemarr[6]+",")
			.append("areaname="+elemarr[7]+",").append("call_duration="+elemarr[8]+",").append("source="+elemarr[10]+",")
			.append("phone="+elemarr[11]+",").append("simids="+elemarr[13]).append(" where id="+elemarr[3]+";");			
			System.out.println(sb.toString());
		}
		System.out.println(dataarr.length);*/
/*		Double aa = 1.90000000000003;SerializeConfig sc = new SerializeConfig();
		sc.put(Double.class, new DoubleSerializer("0.00"));
		System.out.println(JSON.toJSONString(aa,sc));*/
		/*SNGenerator sngen = new SNGenerator(1,1,8,1);
		long starttime = System.currentTimeMillis();
		
		for(int i = 1; i<100;i++) {
			System.out.println(sngen.generateSN(i,i,2));
			//sngen.generateSEQ();
		}*/
		//System.out.println("spent time:"+(System.currentTimeMillis()-starttime)/1000.0);
		/*Random random;  
	    String table;  
	    random = new Random();  
        table = "0123456789"; 
        for(int i = 0; i < 100; i++) {
		String ret = null,  
	            num = String.format("%06d", 186400+i);  
	        int key = 1,//random.nextInt(10),   
	            seed = random.nextInt(10);  
		Caesar caesar = new Caesar(table, seed);  
        num = caesar.encode(key, num);  
        ret = num   
            + String.format("%01d", key)   
            + String.format("%01d", seed);  
		System.out.println(ret);
        }*/
		/*String[] areas = "江苏、浙江、上海".split("、");
		System.out.println(areas[0]);
		areas = "其它".split("、");
		System.out.println(areas[0]);*/
		/*for (int i = 0; i < 10; i++) {
			// System.out.println(generateNumber());
			System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
		}*/
	}
}
