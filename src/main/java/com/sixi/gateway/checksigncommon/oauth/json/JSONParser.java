/**
 * Project Name:json-scanner
 * File Name:JSONParser.java
 * Package Name:com.bus.card.common.json
 *  
 * Copyright (c) 2015, www.mmaildo-soft.com All Rights Reserved.
 *
 */

package com.sixi.gateway.checksigncommon.oauth.json;

import java.util.HashMap;
import java.util.Map;


/**
 * ClassName:JSONParser <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 *  
 * 
 * @author mmail
 * @version
 * @since JDK 1.6
 * @see
 */
public class JSONParser {

	protected final SymbolTable symbolTable;

	protected final Object input;

	protected final JsonLexer lexer;

	public JSONParser(String input) {
		this(input, SingleJSON.DEFAULT_PARSER_FEATURE);
	}

	public JSONParser(String input, int features) {
		this(input, SymbolTable.getGlobalInstance(), features);
	}

	public JSONParser(final String input, final SymbolTable config, int features) {
		this(input, new JSONScanner(input, features), config);
	}

	public JSONParser(final Object input, final JsonLexer lexer, final SymbolTable symbolTable) {
		this.lexer = lexer;
		this.input = input;
		this.symbolTable = symbolTable;
		lexer.nextToken(JSONToken.LBRACE); // prime the pump
	}
	
	public void close(){
		 lexer.close();
	}

	public Object parse() {
		final JsonLexer lexer = getLexer();
		switch (lexer.token()) {
		case JSONToken.LBRACE:
			Map<String, Object> map = new HashMap<String, Object>();
			return parseMap(map);
		case JSONToken.LITERAL_INT:
			String intValue = lexer.numberString();
			lexer.nextToken();
			return intValue;
		case JSONToken.LITERAL_FLOAT:
			String floatValue = lexer.numberString();
			lexer.nextToken();
			return floatValue;
		case JSONToken.LITERAL_STRING:
			String stringLiteral = lexer.stringVal();
			lexer.nextToken(JSONToken.COMMA);
			return stringLiteral;
		case JSONToken.NULL:
			lexer.nextToken();
			return null;
		case JSONToken.UNDEFINED:
			lexer.nextToken();
			return null;
		case JSONToken.TRUE:
			lexer.nextToken();
			return Boolean.TRUE;
		case JSONToken.FALSE:
			lexer.nextToken();
			return Boolean.FALSE;
		case JSONToken.NEW:
			lexer.nextToken(JSONToken.IDENTIFIER);

			if (lexer.token() != JSONToken.IDENTIFIER) {
				throw new JSONException("syntax error");
			}
			lexer.nextToken(JSONToken.LPAREN);

			String t = lexer.stringVal();
			return t;
		case JSONToken.EOF:
			if (lexer.isBlankInput()) {
				return null;
			}
			throw new JSONException("unterminated json string, pos " + lexer.getBufferPosition());
		case JSONToken.ERROR:
		default:
			throw new JSONException("syntax error, pos " + lexer.getBufferPosition());

		}
	}

	public final Object parseMap(final Map<String, Object> object) {
		final JsonLexer lexer = this.lexer;

		if (lexer.token() == JSONToken.NULL) {
			lexer.next();
			return null;
		}

		if (lexer.token() != JSONToken.LBRACE && lexer.token() != JSONToken.COMMA) {
			throw new JSONException("syntax error, expect {, actual " + lexer.tokenName());
		}

		for (;;) {
			lexer.skipWhitespace();
			char ch = lexer.getCurrent();
			while (ch == ',') {
				lexer.next();
				lexer.skipWhitespace();
				ch = lexer.getCurrent();
			}

			boolean isObjectKey = false;
			String key;
			if (ch == '"') {
				key = lexer.scanSymbol(symbolTable, '"');
				lexer.skipWhitespace();
				ch = lexer.getCurrent();
				if (ch != ':') {
					throw new JSONException("expect ':' at " + lexer.pos() + ", name " + key);
				}
			} else if (ch == '}') {
				lexer.next();
				lexer.resetStringPosition();
				lexer.nextToken();
				return object;
			} else if (ch == '\'') {
				key = lexer.scanSymbol(symbolTable, '\'');
				lexer.skipWhitespace();
				ch = lexer.getCurrent();
				if (ch != ':') {
					throw new JSONException("expect ':' at " + lexer.pos());
				}
			} else if (ch == ',') {
				throw new JSONException("syntax error");
			} else if ((ch >= '0' && ch <= '9') || ch == '-') {
				lexer.resetStringPosition();
				lexer.scanNumber();
				key = lexer.numberString();
				ch = lexer.getCurrent();
				if (ch != ':') {
					throw new JSONException("expect ':' at " + lexer.pos() + ", name " + key);
				}
			} else {
				key = lexer.scanSymbolUnQuoted(symbolTable);
				lexer.skipWhitespace();
				ch = lexer.getCurrent();
				if (ch != ':') {
					throw new JSONException("expect ':' at " + lexer.pos() + ", actual " + ch);
				}
			}

			if (!isObjectKey) {
				lexer.next();
				lexer.skipWhitespace();
			}

			ch = lexer.getCurrent();

			lexer.resetStringPosition();

			Object value;
			if (ch == '"') {
				lexer.scanString();
				value = lexer.stringVal();
				object.put(key, value);
			} else if (ch >= '0' && ch <= '9' || ch == '-') {
				lexer.scanNumber();
				value = lexer.numberString();
				object.put(key, value);
			} else if (ch == '[') { // 减少嵌套，兼容android
				
				lexer.scanUnQuoted(']');
				value = lexer.stringSubVal();
				object.put(key, value);

			} else if (ch == '{') { // 减少嵌套，兼容android
				lexer.scanUnQuoted('}');
				value = lexer.stringSubVal();
				object.put(key, value);
			} else {
				lexer.nextToken();
				value = parse();
				object.put(key, value);

				if (lexer.token() == JSONToken.RBRACE) {
					lexer.nextToken();
					return object;
				} else if (lexer.token() == JSONToken.COMMA) {
					continue;
				} else {
					throw new JSONException("syntax error, position at " + lexer.pos() + ", name " + key);
				}
			}

			lexer.skipWhitespace();
			ch = lexer.getCurrent();
			if (ch == ',') {
				lexer.next();
				continue;
			} else if (ch == '}') {
				lexer.next();
				lexer.resetStringPosition();
				lexer.nextToken();
				return object;
			} else {
				throw new JSONException("syntax error, position at " + lexer.pos() + ", name " + key);
			}

		}

	}

	public JsonLexer getLexer() {
		return lexer;
	}
}
