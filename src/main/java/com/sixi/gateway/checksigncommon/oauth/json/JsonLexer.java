

package com.sixi.gateway.checksigncommon.oauth.json;

/**
 * ClassName: JsonLexer <br/>
 * Function: JsonLexer. <br/>
 * Reason:	 JsonLexer 扫码接口类. <br/>
 * Date:     2019/6/11 4:18 PM <br/>
 * @author luoyi
 * @version 1.0
 * @since JDK 1.6
 */
public interface JsonLexer {

	static byte EOI = 0x1A;
	static int NOT_MATCH = -1;
	static int NOT_MATCH_NAME = -2;
	static int UNKOWN = 0;
	static int OBJECT = 1;
	static int ARRAY = 2;
	static int VALUE = 3;
	static int END = 4;


	/**
	 * token
	 * @return token
	 */
	int token();

	/**
	 * token name
	 * @return token name
	 */
	String tokenName();

	/**
	 * 跟过空白
	 */
	void skipWhitespace();

	/**
	 * 扫描下一个token
	 */
	void nextToken();

	/**
	 * 扫描到下一个token
	 * @param expect 期望扫描到的字符
	 */
	void nextToken(int expect);

	/**
	 * 返回当前字符
	 * @return 当前字符
	 */
	char getCurrent();

	/**
	 * 扫描器向下移动一个
	 * @return char
	 */
	char next();

	/**
	 * 扫描一个单词
	 */
	void scanString();

	/**
	 * 扫描一个字符并返回
	 * @param symbolTable 字符表
	 * @return	扫描结果
	 */
	String scanSymbol(final SymbolTable symbolTable);

    /**
     * 一直扫描到指定括号
     * @param symbolTable   字符表
     * @param quote  括号符号
     * @return  扫描结果
     */
	String scanSymbol(final SymbolTable symbolTable, final char quote);

    /**
     * 一直扫描到括号
     * @param symbolTable   字符表
     * @return 扫描结果
     */
	String scanSymbolUnQuoted(final SymbolTable symbolTable);

    /**
     * 重置String 移偏位置
     */
	void resetStringPosition();

    /**
     * 扫描一个数字串
     */
	void scanNumber();

    /**
     * 返回当前位置
     * @return 当前位置
     */
	int pos();

    /**
     * 返回当前扫描到数字字串
     * @return 数字字串
     */
    String numberString();

    /**
     * 返回当前扫描变量名
     * @return 变量名
     */
    String stringVal();

    /**
     * 返回当前扫描到
     * @return 变量名
     */
    String stringSubVal();

    /**
     * buf 位置
     * @return buf 位置
     */
	int getBufferPosition();

    /**
     * 判断是否空行
     * @return 结果
     */
	boolean isBlankInput();

    /**
     * 一直扫描到括符
     * @param unQuoted 指定括符
     */
	void scanUnQuoted(char unQuoted);

    /**
     * 关闭
     */
	void close();
}
