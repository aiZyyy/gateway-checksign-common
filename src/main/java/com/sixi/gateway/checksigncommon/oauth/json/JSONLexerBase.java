/**
 * Project Name:framework-webapi
 * File Name:JSONLexerBase.java
 * Package Name:com.bus.card.utils.json
 *  
 * Copyright (c) 2015, www.mmaildo-soft.com All Rights Reserved.
 *
 */

package com.sixi.gateway.checksigncommon.oauth.json;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:JSONLexerBase <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 *  
 * 
 * @author mmail
 * @version
 * @since JDK 1.6
 * @see
 */
public abstract class JSONLexerBase implements JsonLexer {

	protected boolean hasSpecial;

	protected int token;
	protected int pos;

	protected char ch;
	protected int bp;

	protected int eofPos;

	/**
	 * A character buffer for literals.
	 */
	protected char[] sbuf;
	protected int sp;

	protected final static int[] digits = new int[(int) 'f' + 1];

	/**
	 * number start position
	 */
	protected int np;

	private final static Map<String, Integer> DEFAULT_KEYWORDS;

	protected Map<String, Integer> keywods = DEFAULT_KEYWORDS;

	private final static ThreadLocal<char[]> SBUF_LOCAL         = new ThreadLocal<char[]>();
	
	public final static int TOP = EOI;

	static {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("null", JSONToken.NULL);
		map.put("new", JSONToken.NEW);
		map.put("true", JSONToken.TRUE);
		map.put("false", JSONToken.FALSE);
		map.put("undefined", JSONToken.UNDEFINED);
		DEFAULT_KEYWORDS = map;
	}
	
	public JSONLexerBase(){
		sbuf = SBUF_LOCAL.get();
		if (sbuf == null) {
	         sbuf = new char[512];
	    }
	}

	@Override
	public void close() {
		int limit_buf_size = 1024 * 8;
		if (sbuf.length <= limit_buf_size) {
	         SBUF_LOCAL.set(sbuf);
	    }
		this.sbuf = null;
	}
	
	
	protected void lexError(String key, Object... args) {
		token = JSONToken.ERROR;
	}

	@Override
	public final String scanSymbol(final SymbolTable symbolTable) {
		skipWhitespace();

		if (ch == '"') {
			return scanSymbol(symbolTable, '"');
		}

		if (!isEnabled(Feature.AllowSingleQuotes)) {
			throw new JSONException("syntax error");
		}

		if (ch == '}') {
			next();
			token = JSONToken.RBRACE;
			return null;
		}

		if (ch == ',') {
			next();
			token = JSONToken.COMMA;
			return null;
		}

		if (ch == EOI) {
			token = JSONToken.EOF;
			return null;
		}

		if (!isEnabled(Feature.AllowUnQuotedFieldNames)) {
			throw new JSONException("syntax error");
		}

		return scanSymbolUnQuoted(symbolTable);
	}

	@Override
	public final String scanSymbol(final SymbolTable symbolTable, final char quote) {
		int hash = 0;

		np = bp;
		sp = 0;
		boolean hasSpecial = false;
		char chLocal;
		for (;;) {
			chLocal = next();

			if (chLocal == quote) {
				break;
			}

			if (chLocal == EOI) {
				throw new JSONException("unclosed.str");
			}

			if (chLocal == '\\') {
				if (!hasSpecial) {
					hasSpecial = true;

					if (sp >= sbuf.length) {
						int newCapcity = sbuf.length * 2;
						if (sp > newCapcity) {
							newCapcity = sp;
						}
						char[] newsbuf = new char[newCapcity];
						System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
						sbuf = newsbuf;
					}

					// text.getChars(np + 1, np + 1 + sp, sbuf, 0);
					// System.arraycopy(this.buf, np + 1, sbuf, 0, sp);
					arrayCopy(np + 1, sbuf, 0, sp);
				}

				chLocal = next();

				switch (chLocal) {
				case '0':
					hash = 31 * hash + (int) chLocal;
					putChar('\0');
					break;
				case '1':
					hash = 31 * hash + (int) chLocal;
					putChar('\1');
					break;
				case '2':
					hash = 31 * hash + (int) chLocal;
					putChar('\2');
					break;
				case '3':
					hash = 31 * hash + (int) chLocal;
					putChar('\3');
					break;
				case '4':
					hash = 31 * hash + (int) chLocal;
					putChar('\4');
					break;
				case '5':
					hash = 31 * hash + (int) chLocal;
					putChar('\5');
					break;
				case '6':
					hash = 31 * hash + (int) chLocal;
					putChar('\6');
					break;
				case '7':
					hash = 31 * hash + (int) chLocal;
					putChar('\7');
					break;
				case 'b': // 8
					hash = 31 * hash + (int) '\b';
					putChar('\b');
					break;
				case 't': // 9
					hash = 31 * hash + (int) '\t';
					putChar('\t');
					break;
				case 'n': // 10
					hash = 31 * hash + (int) '\n';
					putChar('\n');
					break;
				case 'v': // 11
					hash = 31 * hash + (int) '\u000B';
					putChar('\u000B');
					break;
				case 'f': // 12
				case 'F':
					hash = 31 * hash + (int) '\f';
					putChar('\f');
					break;
				case 'r': // 13
					hash = 31 * hash + (int) '\r';
					putChar('\r');
					break;
				case '"': // 34
					hash = 31 * hash + (int) '"';
					putChar('"');
					break;
				case '\'': // 39
					hash = 31 * hash + (int) '\'';
					putChar('\'');
					break;
				case '/': // 47
					hash = 31 * hash + (int) '/';
					putChar('/');
					break;
				case '\\': // 92
					hash = 31 * hash + (int) '\\';
					putChar('\\');
					break;
				case 'x':
					char x1 = ch = next();
					char x2 = ch = next();

					int x_val = digits[x1] * 16 + digits[x2];
					char x_char = (char) x_val;
					hash = 31 * hash + (int) x_char;
					putChar(x_char);
					break;
				case 'u':
					char c1 = chLocal = next();
					char c2 = chLocal = next();
					char c3 = chLocal = next();
					char c4 = chLocal = next();
					int val = Integer.parseInt(new String(new char[] { c1, c2, c3, c4 }), 16);
					hash = 31 * hash + val;
					putChar((char) val);
					break;
				default:
					this.ch = chLocal;
					throw new JSONException("unclosed.str.lit");
				}
				continue;
			}

			hash = 31 * hash + chLocal;

			if (!hasSpecial) {
				sp++;
				continue;
			}

			if (sp == sbuf.length) {
				putChar(chLocal);
			} else {
				sbuf[sp++] = chLocal;
			}
		}

		token = JSONToken.LITERAL_STRING;

		String value;
		if (!hasSpecial) {
			// return this.text.substring(np + 1, np + 1 + sp).intern();
			int offset;
			if (np == -1) {
				offset = 0;
			} else {
				offset = np + 1;
			}
			value = addSymbol(offset, sp, hash, symbolTable);
		} else {
			value = symbolTable.addSymbol(sbuf, 0, sp, hash);
		}

		sp = 0;
		this.next();

		return value;
	}

	@Override
	public final String scanSymbolUnQuoted(final SymbolTable symbolTable) {
		final boolean[] firstIdentifierFlags = IOUtils.firstIdentifierFlags;
		final char first = ch;

		final boolean firstFlag = ch >= firstIdentifierFlags.length || firstIdentifierFlags[first];
		if (!firstFlag) {
			throw new JSONException("illegal identifier : " + ch);
		}

		final boolean[] identifierFlags = IOUtils.identifierFlags;

		int hash = first;

		np = bp;
		sp = 1;
		char chLocal;
		for (;;) {
			chLocal = next();

			if (chLocal < identifierFlags.length) {
				if (!identifierFlags[chLocal]) {
					break;
				}
			}

			hash = 31 * hash + chLocal;

			sp++;
			continue;
		}

		this.ch = charAt(bp);
		token = JSONToken.IDENTIFIER;

		final int NULL_HASH = 3392903;
		if (sp == 4 && hash == NULL_HASH && charAt(np) == 'n' && charAt(np + 1) == 'u' && charAt(np + 2) == 'l'
				&& charAt(np + 3) == 'l') {
			return null;
		}

		// return text.substring(np, np + sp).intern();

		return this.addSymbol(np, sp, hash, symbolTable);
		// return symbolTable.addSymbol(buf, np, sp, hash);
	}

	@Override
	public final void scanUnQuoted(char unQuoted) {
		np = bp;
		hasSpecial = false;

		char scan = unQuoted;

		cleanScanStack();

		char ch;
		for (;;) {
			ch = next();

			if (scan == ch) {
				scan = popScanStack();
				if (scan == TOP) {
					// 无元素了
					break;
				} else {
					if (!hasSpecial) {
						sp++;
						continue;
					}

					if (sp == sbuf.length) {
						putChar(ch);
					} else {
						sbuf[sp++] = ch;
					}
					continue;
				}
			}

			if (ch == EOI) {
				throw new JSONException("unclosed string : " + ch);
			}

			if (ch == '"') {
				pushScanStack(scan);
				scan = '"';
			} else if (ch == '{') {
				pushScanStack(scan);
				scan = '}';
			} else if (ch == '[') {
				pushScanStack(scan);
				scan = ']';
			} else if (ch == '\\') {
				if (!hasSpecial) {
					hasSpecial = true;

					if (sp >= sbuf.length) {
						int newCapcity = sbuf.length * 2;
						if (sp > newCapcity) {
							newCapcity = sp;
						}
						char[] newsbuf = new char[newCapcity];
						System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
						sbuf = newsbuf;
					}

					copyTo(np + 1, sp, sbuf);
					// text.getChars(np + 1, np + 1 + sp, sbuf, 0);
					// System.arraycopy(buf, np + 1, sbuf, 0, sp);
				}

				ch = next();

				switch (ch) {
				case '0':
					putChar('\0');
					break;
				case '1':
					putChar('\1');
					break;
				case '2':
					putChar('\2');
					break;
				case '3':
					putChar('\3');
					break;
				case '4':
					putChar('\4');
					break;
				case '5':
					putChar('\5');
					break;
				case '6':
					putChar('\6');
					break;
				case '7':
					putChar('\7');
					break;
				case 'b': // 8
					putChar('\b');
					break;
				case 't': // 9
					putChar('\t');
					break;
				case 'n': // 10
					putChar('\n');
					break;
				case 'v': // 11
					putChar('\u000B');
					break;
				case 'f': // 12
				case 'F':
					putChar('\f');
					break;
				case 'r': // 13
					putChar('\r');
					break;
				case '"': // 34
					putChar('"');
					break;
				case '\'': // 39
					putChar('\'');
					break;
				case '/': // 47
					putChar('/');
					break;
				case '\\': // 92
					putChar('\\');
					break;
				case 'x':
					char x1 = ch = next();
					char x2 = ch = next();

					int x_val = digits[x1] * 16 + digits[x2];
					char x_char = (char) x_val;
					putChar(x_char);
					break;
				case 'u':
					char u1 = ch = next();
					char u2 = ch = next();
					char u3 = ch = next();
					char u4 = ch = next();
					int val = Integer.parseInt(new String(new char[] { u1, u2, u3, u4 }), 16);
					putChar((char) val);
					break;
				default:
					this.ch = ch;
					throw new JSONException("unclosed string : " + ch);
				}
				continue;
			}

			if (!hasSpecial) {
				sp++;
				continue;
			}

			if (sp == sbuf.length) {
				putChar(ch);
			} else {
				sbuf[sp++] = ch;
			}
		}

		token = JSONToken.LITERAL_STRING;
		this.ch = next();
	}

	char[] stack = new char[4];
	int stp = 0;

	public final char popScanStack() {
		if (stp <= 0) {
			return TOP;
		} else {
			return stack[--stp];
		}
	}

	public final void pushScanStack(char scanChar) {
		if (stp >= stack.length) {
			char[] newsbuf = new char[stack.length * 2];
			System.arraycopy(stack, 0, newsbuf, 0, stack.length);
			stack = newsbuf;
		}
		stack[stp++] = scanChar;
	}

	public final void cleanScanStack() {
		stp = 0;
	}

	@Override
	public final void scanString() {
		np = bp;
		hasSpecial = false;
		char ch;
		for (;;) {
			ch = next();

			if (ch == '\"') {
				break;
			}

			if (ch == EOI) {
				throw new JSONException("unclosed string : " + ch);
			}

			if (ch == '\\') {
				if (!hasSpecial) {
					hasSpecial = true;

					if (sp >= sbuf.length) {
						int newCapcity = sbuf.length * 2;
						if (sp > newCapcity) {
							newCapcity = sp;
						}
						char[] newsbuf = new char[newCapcity];
						System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
						sbuf = newsbuf;
					}

					copyTo(np + 1, sp, sbuf);
					// text.getChars(np + 1, np + 1 + sp, sbuf, 0);
					// System.arraycopy(buf, np + 1, sbuf, 0, sp);
				}

				ch = next();

				switch (ch) {
				case '0':
					putChar('\0');
					break;
				case '1':
					putChar('\1');
					break;
				case '2':
					putChar('\2');
					break;
				case '3':
					putChar('\3');
					break;
				case '4':
					putChar('\4');
					break;
				case '5':
					putChar('\5');
					break;
				case '6':
					putChar('\6');
					break;
				case '7':
					putChar('\7');
					break;
				case 'b': // 8
					putChar('\b');
					break;
				case 't': // 9
					putChar('\t');
					break;
				case 'n': // 10
					putChar('\n');
					break;
				case 'v': // 11
					putChar('\u000B');
					break;
				case 'f': // 12
				case 'F':
					putChar('\f');
					break;
				case 'r': // 13
					putChar('\r');
					break;
				case '"': // 34
					putChar('"');
					break;
				case '\'': // 39
					putChar('\'');
					break;
				case '/': // 47
					putChar('/');
					break;
				case '\\': // 92
					putChar('\\');
					break;
				case 'x':
					char x1 = ch = next();
					char x2 = ch = next();

					int x_val = digits[x1] * 16 + digits[x2];
					char x_char = (char) x_val;
					putChar(x_char);
					break;
				case 'u':
					char u1 = ch = next();
					char u2 = ch = next();
					char u3 = ch = next();
					char u4 = ch = next();
					int val = Integer.parseInt(new String(new char[] { u1, u2, u3, u4 }), 16);
					putChar((char) val);
					break;
				default:
					this.ch = ch;
					throw new JSONException("unclosed string : " + ch);
				}
				continue;
			}

			if (!hasSpecial) {
				sp++;
				continue;
			}

			if (sp == sbuf.length) {
				putChar(ch);
			} else {
				sbuf[sp++] = ch;
			}
		}

		token = JSONToken.LITERAL_STRING;
		this.ch = next();
	}

	/**
	 * Append a character to sbuf.
	 */
	protected final void putChar(char ch) {
		if (sp == sbuf.length) {
			char[] newsbuf = new char[sbuf.length * 2];
			System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
			sbuf = newsbuf;
		}
		sbuf[sp++] = ch;
	}

	@Override
	public final void nextToken() {
		sp = 0;

		for (;;) {
			pos = bp;

			if (ch == '"') {
				scanString();
				return;
			}

			if (ch == ',') {
				next();
				token = JSONToken.COMMA;
				return;
			}

			if (ch >= '0' && ch <= '9') {
				scanNumber();
				return;
			}

			if (ch == '-') {
				scanNumber();
				return;
			}

			switch (ch) {
			case '\'':
				if (!isEnabled(Feature.AllowSingleQuotes)) {
					throw new JSONException("Feature.AllowSingleQuotes is false");
				}
				scanStringSingleQuote();
				return;
			case ' ':
			case '\t':
			case '\b':
			case '\f':
			case '\n':
			case '\r':
				next();
				break;
			case 't': // true
				scanTrue();
				return;
			case 'T': // true
				scanTreeSet();
				return;
			case 'S': // set
				scanSet();
				return;
			case 'f': // false
				scanFalse();
				return;
			case 'n': // new,null
				scanNullOrNew();
				return;
			case 'N': // new,null
				scanNULL();
				return;
			case 'u': // new,null
				scanUndefined();
				return;
			case '(':
				next();
				token = JSONToken.LPAREN;
				return;
			case ')':
				next();
				token = JSONToken.RPAREN;
				return;
			case '[':
				next();
				token = JSONToken.LBRACKET;
				return;
			case ']':
				next();
				token = JSONToken.RBRACKET;
				return;
			case '{':
				next();
				token = JSONToken.LBRACE;
				return;
			case '}':
				next();
				token = JSONToken.RBRACE;
				return;
			case ':':
				next();
				token = JSONToken.COLON;
				return;
			default:
				if (isEOF()) { // JLS
					if (token == JSONToken.EOF) {
						throw new JSONException("EOF error");
					}

					token = JSONToken.EOF;
					pos = bp = eofPos;
				} else {
					lexError("illegal.char", String.valueOf((int) ch));
					next();
				}

				return;
			}
		}
	}

	@Override
	public final void nextToken(int expect) {
		sp = 0;

		for (;;) {
			switch (expect) {
			case JSONToken.LBRACE:
				if (ch == '{') {
					token = JSONToken.LBRACE;
					next();
					return;
				}
				if (ch == '[') {
					token = JSONToken.LBRACKET;
					next();
					return;
				}
				break;
			case JSONToken.COMMA:
				if (ch == ',') {
					token = JSONToken.COMMA;
					next();
					return;
				}

				if (ch == '}') {
					token = JSONToken.RBRACE;
					next();
					return;
				}

				if (ch == ']') {
					token = JSONToken.RBRACKET;
					next();
					return;
				}

				if (ch == EOI) {
					token = JSONToken.EOF;
					return;
				}
				break;
			case JSONToken.LITERAL_INT:
				if (ch >= '0' && ch <= '9') {
					pos = bp;
					scanNumber();
					return;
				}

				if (ch == '"') {
					pos = bp;
					scanString();
					return;
				}

				if (ch == '[') {
					token = JSONToken.LBRACKET;
					next();
					return;
				}

				if (ch == '{') {
					token = JSONToken.LBRACE;
					next();
					return;
				}

				break;
			case JSONToken.LITERAL_STRING:
				if (ch == '"') {
					pos = bp;
					scanString();
					return;
				}

				if (ch >= '0' && ch <= '9') {
					pos = bp;
					scanNumber();
					return;
				}

				if (ch == '[') {
					token = JSONToken.LBRACKET;
					next();
					return;
				}

				if (ch == '{') {
					token = JSONToken.LBRACE;
					next();
					return;
				}
				break;
			case JSONToken.LBRACKET:
				if (ch == '[') {
					token = JSONToken.LBRACKET;
					next();
					return;
				}

				if (ch == '{') {
					token = JSONToken.LBRACE;
					next();
					return;
				}
				break;
			case JSONToken.RBRACKET:
				if (ch == ']') {
					token = JSONToken.RBRACKET;
					next();
					return;
				}
			case JSONToken.EOF:
				if (ch == EOI) {
					token = JSONToken.EOF;
					return;
				}
				break;
			case JSONToken.IDENTIFIER:
				nextIdent();
				return;
			default:
				break;
			}

			if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b') {
				next();
				continue;
			}

			nextToken();
			break;
		}
	}

	public final void scanTrue() {
		if (ch != 't') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 'r') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 'u') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 'e') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
				|| ch == '\f' || ch == '\b' || ch == ':') {
			token = JSONToken.TRUE;
		} else {
			throw new JSONException("scan true error");
		}
	}

	public final void scanTreeSet() {
		if (ch != 'T') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 'r') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 'e') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 'e') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 'S') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 'e') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 't') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b' || ch == '[' || ch == '(') {
			token = JSONToken.TREE_SET;
		} else {
			throw new JSONException("scan set error");
		}
	}

	public final void scanNullOrNew() {
		if (ch != 'n') {
			throw new JSONException("error parse null or new");
		}
		next();

		if (ch == 'u') {
			next();
			if (ch != 'l') {
				throw new JSONException("error parse l");
			}
			next();

			if (ch != 'l') {
				throw new JSONException("error parse l");
			}
			next();

			if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
					|| ch == '\f' || ch == '\b') {
				token = JSONToken.NULL;
			} else {
				throw new JSONException("scan true error");
			}
			return;
		}

		if (ch != 'e') {
			throw new JSONException("error parse e");
		}
		next();

		if (ch != 'w') {
			throw new JSONException("error parse w");
		}
		next();

		if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
				|| ch == '\f' || ch == '\b') {
			token = JSONToken.NEW;
		} else {
			throw new JSONException("scan true error");
		}
	}

	public final void scanNULL() {
		if (ch != 'N') {
			throw new JSONException("error parse NULL");
		}
		next();

		if (ch == 'U') {
			next();
			if (ch != 'L') {
				throw new JSONException("error parse U");
			}
			next();

			if (ch != 'L') {
				throw new JSONException("error parse NULL");
			}
			next();

			if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
					|| ch == '\f' || ch == '\b') {
				token = JSONToken.NULL;
			} else {
				throw new JSONException("scan NULL error");
			}
			return;
		}
	}

	public final void scanUndefined() {
		if (ch != 'u') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'n') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'd') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'e') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'f') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'i') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'n') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'e') {
			throw new JSONException("error parse false");
		}
		next();
		if (ch != 'd') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
				|| ch == '\f' || ch == '\b') {
			token = JSONToken.UNDEFINED;
		} else {
			throw new JSONException("scan false error");
		}
	}

	public final void scanFalse() {
		if (ch != 'f') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'a') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'l') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 's') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch != 'e') {
			throw new JSONException("error parse false");
		}
		next();

		if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
				|| ch == '\f' || ch == '\b' || ch == ':') {
			token = JSONToken.FALSE;
		} else {
			throw new JSONException("scan false error");
		}
	}

	private final void scanStringSingleQuote() {
		np = bp;
		hasSpecial = false;
		char chLocal;
		for (;;) {
			chLocal = next();

			if (chLocal == '\'') {
				break;
			}

			if (chLocal == EOI) {
				throw new JSONException("unclosed single-quote string");
			}

			if (chLocal == '\\') {
				if (!hasSpecial) {
					hasSpecial = true;

					if (sp > sbuf.length) {
						char[] newsbuf = new char[sp * 2];
						System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
						sbuf = newsbuf;
					}

					// text.getChars(offset, offset + count, dest, 0);
					this.copyTo(np + 1, sp, sbuf);
					// System.arraycopy(buf, np + 1, sbuf, 0, sp);
				}

				chLocal = next();

				switch (chLocal) {
				case '0':
					putChar('\0');
					break;
				case '1':
					putChar('\1');
					break;
				case '2':
					putChar('\2');
					break;
				case '3':
					putChar('\3');
					break;
				case '4':
					putChar('\4');
					break;
				case '5':
					putChar('\5');
					break;
				case '6':
					putChar('\6');
					break;
				case '7':
					putChar('\7');
					break;
				case 'b': // 8
					putChar('\b');
					break;
				case 't': // 9
					putChar('\t');
					break;
				case 'n': // 10
					putChar('\n');
					break;
				case 'v': // 11
					putChar('\u000B');
					break;
				case 'f': // 12
				case 'F':
					putChar('\f');
					break;
				case 'r': // 13
					putChar('\r');
					break;
				case '"': // 34
					putChar('"');
					break;
				case '\'': // 39
					putChar('\'');
					break;
				case '/': // 47
					putChar('/');
					break;
				case '\\': // 92
					putChar('\\');
					break;
				case 'x':
					char x1 = chLocal = next();
					char x2 = chLocal = next();

					int x_val = digits[x1] * 16 + digits[x2];
					char x_char = (char) x_val;
					putChar(x_char);
					break;
				case 'u':
					char c1 = chLocal = next();
					char c2 = chLocal = next();
					char c3 = chLocal = next();
					char c4 = chLocal = next();
					int val = Integer.parseInt(new String(new char[] { c1, c2, c3, c4 }), 16);
					putChar((char) val);
					break;
				default:
					this.ch = chLocal;
					throw new JSONException("unclosed single-quote string");
				}
				continue;
			}

			if (!hasSpecial) {
				sp++;
				continue;
			}

			if (sp == sbuf.length) {
				putChar(chLocal);
			} else {
				sbuf[sp++] = chLocal;
			}
		}

		token = JSONToken.LITERAL_STRING;
		this.next();
	}

	public final void scanSet() {
		if (ch != 'S') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 'e') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch != 't') {
			throw new JSONException("error parse true");
		}
		next();

		if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b' || ch == '[' || ch == '(') {
			token = JSONToken.SET;
		} else {
			throw new JSONException("scan set error");
		}
	}

	public final void nextIdent() {
		while (isWhitespace(ch)) {
			next();
		}
		if (ch == '_' || Character.isLetter(ch)) {
			scanIdent();
		} else {
			nextToken();
		}
	}

	public static final boolean isWhitespace(char ch) {
		return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b';
	}

	public final void scanIdent() {
		np = bp - 1;
		hasSpecial = false;

		for (;;) {
			sp++;

			next();
			if (Character.isLetterOrDigit(ch)) {
				continue;
			}

			String ident = stringVal();

			Integer tok = keywods.get(ident);
			if (tok != null) {
				token = tok;
			} else {
				token = JSONToken.IDENTIFIER;
			}
			return;
		}
	}

	@Override
	public final int token() {
		return token;
	}

	@Override
	public final String tokenName() {
		return JSONToken.name(token);
	}

	@Override
	public final int pos() {
		return pos;
	}

	@Override
	public final int getBufferPosition() {
		return bp;
	}

	public final String stringDefaultValue() {
		if (this.isEnabled(Feature.InitStringFieldAsEmpty)) {
			return "";
		}
		return null;
	}

	@Override
	public abstract String stringVal();

	@Override
	public abstract String numberString();

	public abstract boolean isEOF();

	@Override
	public final char getCurrent() {
		return ch;
	}

	public abstract char charAt(int index);

	@Override
	public abstract char next();

	protected abstract void arrayCopy(int srcPos, char[] dest, int destPos, int length);

	public abstract String addSymbol(int offset, int len, int hash, final SymbolTable symbolTable);

	protected abstract void copyTo(int offset, int count, char[] dest);

	@Override
	public final void skipWhitespace() {
		for (;;) {
			if (ch < IOUtils.whitespaceFlags.length && IOUtils.whitespaceFlags[ch]) {
				next();
				continue;
			} else {
				break;
			}
		}
	}

	public final boolean isEnabled(Feature feature) {
		return Feature.isEnabled(this.features, feature);
	}

	protected int features = SingleJSON.DEFAULT_PARSER_FEATURE;

	@Override
	public void resetStringPosition() {
		this.sp = 0;
	}

	@Override
	public void scanNumber() {
		np = bp;

		if (ch == '-') {
			sp++;
			next();
		}

		for (;;) {
			if (ch >= '0' && ch <= '9') {
				sp++;
			} else {
				break;
			}
			next();
		}

		boolean isDouble = false;

		if (ch == '.') {
			sp++;
			next();
			isDouble = true;

			for (;;) {
				if (ch >= '0' && ch <= '9') {
					sp++;
				} else {
					break;
				}
				next();
			}
		}

		if (ch == 'L') {
			sp++;
			next();
		} else if (ch == 'S') {
			sp++;
			next();
		} else if (ch == 'B') {
			sp++;
			next();
		} else if (ch == 'F') {
			sp++;
			next();
			isDouble = true;
		} else if (ch == 'D') {
			sp++;
			next();
			isDouble = true;
		} else if (ch == 'e' || ch == 'E') {
			sp++;
			next();

			if (ch == '+' || ch == '-') {
				sp++;
				next();
			}

			for (;;) {
				if (ch >= '0' && ch <= '9') {
					sp++;
				} else {
					break;
				}
				next();
			}

			if (ch == 'D' || ch == 'F') {
				sp++;
				next();
			}

			isDouble = true;
		}

		if (isDouble) {
			token = JSONToken.LITERAL_FLOAT;
		} else {
			token = JSONToken.LITERAL_INT;
		}
	}
}
