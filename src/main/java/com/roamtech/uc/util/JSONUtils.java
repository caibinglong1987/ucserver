package com.roamtech.uc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.*;

public class JSONUtils {
	private static SerializeConfig serializeConfig = new SerializeConfig();
	static {
		serializeConfig.put(Double.class, new DoubleSerializer("0.00"));
	}
	public static String serialize(Object object) {  
		return JSON.toJSONString(object, serializeConfig, SerializerFeature.DisableCircularReferenceDetect);
	}
	public static String serialize(Object object, SerializeFilter filter) {
		return toJSONString(object, filter, serializeConfig, SerializerFeature.DisableCircularReferenceDetect);
	}
	public static final String toJSONString(Object object, SerializeFilter filter, SerializeConfig config, SerializerFeature... features) {
		SerializeWriter out = new SerializeWriter();

		try {
			JSONSerializer serializer = new JSONSerializer(out, config);
			for (com.alibaba.fastjson.serializer.SerializerFeature feature : features) {
				serializer.config(feature, true);
			}
			if (filter != null) {
				if (filter instanceof PropertyPreFilter) {
					serializer.getPropertyPreFilters().add((PropertyPreFilter) filter);
				}

				if (filter instanceof NameFilter) {
					serializer.getNameFilters().add((NameFilter) filter);
				}

				if (filter instanceof ValueFilter) {
					serializer.getValueFilters().add((ValueFilter) filter);
				}

				if (filter instanceof PropertyFilter) {
					serializer.getPropertyFilters().add((PropertyFilter) filter);
				}

				if (filter instanceof BeforeFilter) {
					serializer.getBeforeFilters().add((BeforeFilter) filter);
				}

				if (filter instanceof AfterFilter) {
					serializer.getAfterFilters().add((AfterFilter) filter);
				}
			}
			serializer.write(object);

			return out.toString();
		} finally {
			out.close();
		}
	}
	public static <T> T  deserialize(String result, Class<T> type) {
		return (T)JSON.parseObject(result, type);
	}
}
