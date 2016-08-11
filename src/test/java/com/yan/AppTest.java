/**
 * 
 */
package com.yan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

	/**
	 * @throws IOException
	 * @Description: Tree Model处理Json
	 * @date 2016年8月11日 上午9:16:35
	 */
	@Test
	public void serializationExampleTreeModel() throws IOException {
		// 创建一个节点工厂,为我们提供所有节点
		JsonNodeFactory factory = new JsonNodeFactory(false);
		// 创建一个json factory来写tree modle为json
		JsonFactory jsonFactory = new JsonFactory();
		// 创建一个json生成器
		JsonGenerator generator = jsonFactory.createGenerator(new FileWriter(new File("country2.json")));
		// 注意，默认情况下对象映射器不会指定根节点，下面设根节点为country
		ObjectNode country = factory.objectNode();

		country.put("countryId", "China");
		country.put("birthDate", "1949-10-01");

		// 在Java中，List和Array转化为json后对应的格式符号都是"obj:[]"
		ArrayNode nation = factory.arrayNode();
		nation.add("Han").add("Meng").add("Hui").add("WeiWuEr").add("Zang");
		country.set("nation", nation);

		ArrayNode lakes = factory.arrayNode();
		lakes.add("QingHai Lake").add("Poyang Lake").add("Dongting Lake").add("Taihu Lake");
		country.set("lakes", lakes);

		ArrayNode provinces = factory.arrayNode();
		ObjectNode province = factory.objectNode();
		ObjectNode province2 = factory.objectNode();
		province.put("name", "Shanxi");
		province.put("population", 37751200);
		province2.put("name", "ZheJiang");
		province2.put("population", 55080000);
		provinces.add(province).add(province2);
		country.set("provinces", provinces);

		ObjectNode traffic = factory.objectNode();
		traffic.put("HighWay(KM)", 4240000);
		traffic.put("Train(KM)", 112000);
		country.set("traffic", traffic);

		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.writeTree(generator, country);

	}

	/**
	 * @Description: json字符串反序列化为tree mode
	 * @date 2016年8月11日 上午9:24:03
	 * @throws IOException
	 */
	@Test
	public void deserializationExampleTreeModel1() throws IOException {
		// Jackson提供一个树节点被称为"JsonNode",ObjectMapper提供方法来读json作为树的JsonNode根节点
		JsonNode node = mapper.readTree(new File("country2.json"));
		// 看看根节点的类型
		System.out.println("node JsonNodeType:" + node.getNodeType());
		// 是不是一个容器
		System.out.println("node is container Node ? " + node.isContainerNode());
		// 得到所有node节点的子节点名称
		System.out.println("---------得到所有node节点的子节点名称-------------------------");
		Iterator<String> fieldNames = node.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			System.out.print(fieldName + " ");
		}
		System.out.println("\n-----------------------------------------------------");
		// as.Text的作用是有值返回值，无值返回空字符串
		JsonNode countryId = node.get("countryId");
		System.out.println("countryId:" + countryId.asText() + " JsonNodeType:" + countryId.getNodeType());

		JsonNode birthDate = node.get("birthDate");
		System.out.println("birthDate:" + birthDate.asText() + " JsonNodeType:" + birthDate.getNodeType());

		JsonNode nation = node.get("nation");
		System.out.println("nation:" + nation + " JsonNodeType:" + nation.getNodeType());

		JsonNode lakes = node.get("lakes");
		System.out.println("lakes:" + lakes + " JsonNodeType:" + lakes.getNodeType());

		JsonNode provinces = node.get("provinces");
		System.out.println("provinces JsonNodeType:" + provinces.getNodeType());

		boolean flag = true;
		for (JsonNode provinceElements : provinces) {
			// 为了避免provinceElements多次打印，用flag控制打印，能体现provinceElements的JsonNodeType就可以了
			if (flag) {
				System.out.println("provinceElements JsonNodeType:" + provinceElements.getNodeType());
				System.out.println("provinceElements is container node? " + provinceElements.isContainerNode());
				flag = false;
			}
			Iterator<String> provinceElementFields = provinceElements.fieldNames();
			while (provinceElementFields.hasNext()) {
				String fieldName = (String) provinceElementFields.next();
				String province;
				if ("population".equals(fieldName)) {
					province = fieldName + ":" + provinceElements.get(fieldName).asInt();
				} else {
					province = fieldName + ":" + provinceElements.get(fieldName).asText();
				}
				System.out.println(province);
			}
		}
	}

	/**
	 * @Description: json字符串反序列化为tree mode
	 * @date 2016年8月11日 上午9:28:39
	 * @throws IOException
	 */
	@Test
	public void deserializationExampleTreeModle2() throws IOException {
		JsonNode node = mapper.readTree(new File("country2.json"));
		// path方法获取JsonNode时，当对象不存在时，返回MISSING类型的JsonNode
		JsonNode missingNode = node.path("test");
		if (missingNode.isMissingNode()) {
			System.out.println("JsonNodeType : " + missingNode.getNodeType());
		}

		System.out.println("countryId:" + node.path("countryId").asText());

		JsonNode provinces = node.path("provinces");
		for (JsonNode provinceElements : provinces) {
			Iterator<String> provincesFields = provinceElements.fieldNames();
			while (provincesFields.hasNext()) {
				String fieldName = (String) provincesFields.next();
				String province;
				if ("name".equals(fieldName)) {
					province = fieldName + ":" + provinceElements.path(fieldName).asText();
				} else {
					province = fieldName + ":" + provinceElements.path(fieldName).asInt();
				}
				System.out.println(province);
			}
		}
	}

	/**
	 * @Description: stream生成json
	 * @date 2016年8月11日 上午9:38:35
	 * @throws IOException
	 */
	@Test
	public void streamGeneratorJson() throws IOException {
		JsonFactory factory = new JsonFactory();
		// 从JsonFactory创建一个JsonGenerator生成器的实例
		JsonGenerator generator = factory.createGenerator(new FileWriter(new File("country3.json")));

		generator.writeStartObject();
		generator.writeFieldName("countryId");
		generator.writeString("China");
		generator.writeFieldName("provinces");
		generator.writeStartArray();
		generator.writeStartObject();
		generator.writeStringField("name", "Shanxi");
		generator.writeNumberField("population", 33750000);
		generator.writeEndObject();
		generator.writeEndArray();
		generator.writeEndObject();

		generator.close();
	}

	@Test
	public void streamParserJson() throws Exception {
		JsonFactory factory = new JsonFactory();
		// 从JsonFactory创建JsonParser解析器的实例
		JsonParser parser = factory.createParser(new File("country3.json"));

		while (!parser.isClosed()) {
			// 得到一个token,第一次遍历时，token指向json文件中第一个符号"{"
			JsonToken token = parser.nextToken();
			if (token == null) {
				break;
			}
			// 我们只查找 country3.json中的"population"字段的值，能体现解析的流程就可以了
			// 当key是provinces时，我们进入provinces,查找population
			if (JsonToken.FIELD_NAME.equals(token) && "provinces".equals(parser.getCurrentName())) {
				token = parser.nextToken();
				if (!JsonToken.START_ARRAY.equals(token)) {
					break;
				}
				// 此时，token指向的应该是"{"
				token = parser.nextToken();
				if (!JsonToken.START_OBJECT.equals(token)) {
					break;
				}
				while (true) {
					token = parser.nextToken();
					if (token == null) {
						break;
					}
					if (JsonToken.FIELD_NAME.equals(token) && "population".equals(parser.getCurrentName())) {
						token = parser.nextToken();
						System.out.println(parser.getCurrentName() + " : " + parser.getIntValue());
					}
				}
			}
		}
	}
}
