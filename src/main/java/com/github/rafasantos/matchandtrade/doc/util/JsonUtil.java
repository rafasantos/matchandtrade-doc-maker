package com.github.rafasantos.matchandtrade.doc.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;

/**
 * Utility class for common JSON manipulations
 * 
 * @author rafael.santos.bra@gmail.com
 *
 */
public class JsonUtil {

	private static ObjectMapper objectMapper;

	/**
	 * Instantiate objectMapper with default configuration if it is null, then, return objectMapper. 
	 * @return objectMapper with default config
	 */
	private static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);		
		}
		return objectMapper;
	}
	
	/**
	 * Prettyfi a JSON string
	 * 
	 * @param json
	 * @return pretty json
	 */
	public static String prettyJson(String json) {
		String result = null;
		try {
			Object jsonObject = getObjectMapper().readValue(json, Object.class);
			result = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		} catch (IOException e) {
			throw new DocMakerException("Not able to parse string to JSON: " + json, e);
		}
		return result;
	}
	
	/**
	 * Parse an object to a JSON string.
	 * @param o
	 * @return JSON string
	 */
	public static String toJson(Object o) {
		try {
			return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new DocMakerException("Not able to parse object to string", e);
		}
	}
}
