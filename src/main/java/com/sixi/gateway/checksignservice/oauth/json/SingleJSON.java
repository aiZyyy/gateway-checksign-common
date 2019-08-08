/**
 * Project Name:json-scanner
 * File Name:JSON.java
 * Package Name:com.bus.card.common.json
 *  
 * Copyright (c) 2015, www.mmaildo-soft.com All Rights Reserved.
 *
 */

package com.sixi.gateway.checksignservice.oauth.json;


/**
 * ClassName:JSON <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 *  
 * 
 * @author mmail
 * @version
 * @since JDK 1.6
 * @see
 */
public class SingleJSON {

	public static final int DEFAULT_PARSER_FEATURE;

	static {
		int features = 0;
		features |= Feature.AutoCloseSource.getMask();
		features |= Feature.InternFieldNames.getMask();
		features |= Feature.UseBigDecimal.getMask();
		features |= Feature.AllowUnQuotedFieldNames.getMask();
		features |= Feature.AllowSingleQuotes.getMask();
		features |= Feature.AllowArbitraryCommas.getMask();
		features |= Feature.SortFeidFastMatch.getMask();
		features |= Feature.IgnoreNotMatch.getMask();
		DEFAULT_PARSER_FEATURE = features;
	}

	public static Object paser(String input) {
		JSONParser parser = new JSONParser(input);
		Object value =  parser.parse();
		parser.close();
		return value;
		
		
	}

}
