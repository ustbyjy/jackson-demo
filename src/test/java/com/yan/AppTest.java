/**
 * 
 */
package com.yan;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yan.bean.Country;
import com.yan.bean.Province;

/**
 * @Description: Jackson测试类
 * @author Yanjingyang
 * @date 2016年8月10日 下午5:17:09
 * @version 1.0
 */
public class AppTest {
	private ObjectMapper mapper;

	@Before
	public void setUp() throws Exception {
		System.out.println("==================Test begins=================");
		// 使用ObjectMapper来转化对象为Json
		mapper = new ObjectMapper();
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("==================Test ends=================");
	}

	/**
	 * @Description: DataBinding处理json，JavaBean转json
	 * @date 2016年8月10日 下午5:48:10
	 * @throws Exception
	 */
	@Test
	public void javaBeanSerializeToJson() throws Exception {
		// 添加功能，让时间格式更具有可读性
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		mapper.setDateFormat(dateFormat);

		Country country = new Country("China");
		country.setBirthDate(dateFormat.parse("1949-10-01"));
		country.setLakes(new String[] { "Qinghai Lake", "Poyang Lake", "Dongting Lake", "Taihu Lake" });

		List<String> nation = new ArrayList<String>();
		nation.add("Han");
		nation.add("Meng");
		nation.add("Hui");
		nation.add("WeiWuEr");
		nation.add("Zang");
		country.setNation(nation);

		Province province = new Province();
		province.name = "Shanxi";
		province.population = 37751200;
		Province province2 = new Province();
		province2.name = "ZheJiang";
		province2.population = 55080000;
		List<Province> provinces = new ArrayList<Province>();
		provinces.add(province);
		provinces.add(province2);
		country.setProvinces(provinces);

		country.addTraffic("Train(KM)", 112000);
		country.addTraffic("HighWay(KM)", 4240000);
		// 为了使JSON视觉上的可读性（缩进代码），增加一行如下代码，注意，在生产中不需要这样，因为这样会增大Json的内容
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		// 配置mapper忽略空属性
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		// 默认情况，Jackson使用Java属性字段名称作为 Json的属性名称,也可以使用Jackson annotations(注解)改变Json属性名称
		System.out.println(mapper.writeValueAsString(country));
		mapper.writeValue(new File("country.json"), country);
	}

	/**
	 * @Description: DataBinding处理json，json转JavaBean
	 * @date 2016年8月10日 下午5:48:10
	 * @throws Exception
	 */
	@Test
	public void jsonDeserializeToJava() throws Exception {
		File json = new File("country.json");
		// 当反序列化json时，未知属性会引起的反序列化被打断，这里我们禁用未知属性打断反序列化功能，
		// 因为，例如json里有10个属性，而我们的bean中只定义了2个属性，其它8个属性将被忽略
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		// 从json映射到java对象，得到country对象后就可以遍历查找,下面遍历部分内容，能说明问题就可以了
		Country country = mapper.readValue(json, Country.class);
		System.out.println("countryId:" + country.getCountryId());
		// 设置时间格式，便于阅读
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		String birthDate = dateformat.format(country.getBirthDate());
		System.out.println("birthDate:" + birthDate);

		List<Province> provinces = country.getProvinces();
		for (Province province : provinces) {
			System.out.println("province:" + province.name + "\n" + "population:" + province.population);
		}

	}

}
