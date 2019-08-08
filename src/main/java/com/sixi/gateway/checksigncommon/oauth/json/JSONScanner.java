/**
 * Project Name:json-scanner
 * File Name:JSONScanner.java
 * Package Name:com.bus.card.common.json
 *  
 * Copyright (c) 2015, www.mmaildo-soft.com All Rights Reserved.
 *
 */

package com.sixi.gateway.checksigncommon.oauth.json;

/**
 * ClassName:JSONScanner <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 *  
 * 
 * @author mmail
 * @version
 * @since JDK 1.6
 * @see
 */
public class JSONScanner extends JSONLexerBase {

	private final String text;

	public JSONScanner(char[] input, int inputLength) {
		this(input, inputLength, SingleJSON.DEFAULT_PARSER_FEATURE);
	}

	public JSONScanner(char[] input, int inputLength, int features) {
		this(new String(input, 0, inputLength), features);
	}

	public JSONScanner(String input) {
		this(input, SingleJSON.DEFAULT_PARSER_FEATURE);
	}

	public JSONScanner(String input, int features) {
		super();
		this.features = features;

		text = input;
		bp = -1;

		next();
		if (ch == 65279) {
			next();
		}
	}

	@Override
	public String numberString() {
		char chLocal = charAt(np + sp - 1);

		int sp = this.sp;
		if (chLocal == 'L' || chLocal == 'S' || chLocal == 'B' || chLocal == 'F' || chLocal == 'D') {
			sp--;
		}

		return text.substring(np, np + sp);

	}

	@Override
	public boolean isEOF() {
		return bp == text.length() || ch == EOI && bp + 1 == text.length();
	}

	@Override
	public final char charAt(int index) {
		if (index >= text.length()) {
			return EOI;
		}

		return text.charAt(index);
	}

	@Override
	public final char next() {
		return ch = charAt(++bp);
	}

	@Override
	protected void arrayCopy(int srcPos, char[] dest, int destPos, int length) {
		text.getChars(srcPos, srcPos + length, dest, destPos);
	}

	@Override
	public String addSymbol(int offset, int len, int hash, SymbolTable symbolTable) {
		return symbolTable.addSymbol(text, offset, len, hash);
	}

	/**
	 * The value of a literal token, recorded as a string. For integers, leading
	 * 0x and 'l' suffixes are suppressed.
	 */
	@Override
	public final String stringVal() {
		if (!hasSpecial) {
			return text.substring(np + 1, np + 1 + sp);
			// return this.subString(np + 1, sp);
		} else {
			return new String(sbuf, 0, sp);
		}
	}

	
	@Override
	public final String stringSubVal(){
		if (!hasSpecial) {
			return text.substring(np, np + 2 + sp);
			// return this.subString(np + 1, sp);
		} else {
			return new String(sbuf, 0, sp);
		}
	}
	
	@Override
	protected final void copyTo(int offset, int count, char[] dest) {
		text.getChars(offset, offset + count, dest, 0);
	}

	@Override
	public boolean isBlankInput() {
		for (int i = 0;; ++i) {
			char chLocal = charAt(i);
			if (chLocal == EOI) {
				break;
			}

			if (!isWhitespace(chLocal)) {
				return false;
			}
		}

		return true;
	}

}
