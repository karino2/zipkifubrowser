package com.googlecode.zipkifubrowser.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.zipkifubrowser.KifuStreamHandler;
import com.googlecode.zipkifubrowser.KifuSummary;


public class KifuStreamHandlerTest {
	KifuStreamHandler target = new KifuStreamHandler();
	
	@Before
	public void setUp()
	{
		target.newSummary("dummyZipEntryName");
	}
	
	@Test
	public void test_readLine() throws IOException
	{
		String testData = "開始日時：2003/09/08\n"
+ "終了日時：2003/09/09\n"
+ "棋戦：王位戦\n"
+ "戦型：その他の戦型\n"
+ "先手：羽生善治\n"
+ "後手：谷川浩司\n"
+ "\n"
+ "場所：徳島県徳島市「渭水苑」\n"
+ "持ち時間：8時間\n"
+ "*棋戦詳細：第44期王位戦七番勝負第5局\n"
;
		callParse(testData);
		
		assertEqualsDate(2003, 9, 8, target.getBegin());
		assertEqualsDate(2003, 9, 9, target.getEnd());
		assertEquals("王位戦", target.getKisen());
		assertEquals("その他の戦型", target.getSenkei());
		assertEquals("羽生善治", target.getSente());
		assertEquals("谷川浩司", target.getGote());
		assertEquals("第44期王位戦七番勝負第5局", target.getKisenSyousai());
		assertTrue(target.isHeaderEnd());
	}

	@Test
	public void test_defaultField_allNull()
	{
		assertNull(target.getBegin());
		assertNull(target.getEnd());
		assertNull(target.getKisen());
		assertNull(target.getSenkei());
		assertNull(target.getSente());
		assertNull(target.getGote());
		assertFalse(target.isHeaderEnd());
	}
	
	void assertEqualsDate(int expectedYear, int expectedMonth, int expectedDay, Date actual)
	{
		Date expect = createDate(expectedYear, expectedMonth, expectedDay);
		assertEquals(expect, actual);
	}

	static Date createDate(int expectedYear, int expectedMonth, int expectedDay) {
		Date expect = new Date(expectedYear-1900, expectedMonth-1, expectedDay);
		return expect;
	}
	
	@Test
	public void test_readLine_begin()
	{
		target.readLine("開始日時：2003/09/08");
		Date actual = target.getBegin();

		assertEqualsDate(2003, 9, 8, actual);
	}
	
	// 04708.KI2
	@Test
	public void test_readLine_begin_illegal()
	{
		target.readLine("開始日時：1995/01/23s");
		Date actual = target.getBegin();

		assertEqualsDate(1995, 1, 23, actual);
	}
	
	
	@Test
	public void test_readLine_end()
	{
		target.readLine("終了日時：2003/09/09");
		Date actual = target.getEnd();

		assertEqualsDate(2003, 9, 9, actual);
	}
		
	@Test
	public void test_readLine_kisen()
	{
		String expected = "王位戦";
		
		target.readLine("棋戦：王位戦");
		String actual = target.getKisen();
		
		assertEquals(expected, actual);
	}
	
	
	@Test
	public void test_readLine_senkei()
	{
		String expected = "その他の戦型";
		
		target.readLine("戦型：その他の戦型");
		String actual = target.getSenkei();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_readLine_sente()
	{
		String expected = "羽生善治";
		
		target.readLine("先手：羽生善治");
		String actual = target.getSente();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_readLine_gote()
	{
		String expected = "谷川浩司";
		
		target.readLine("後手：谷川浩司");
		String actual = target.getGote();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_readLine_kisenSyousai()
	{
		String expected = "第44期王位戦七番勝負第5局";
		
		target.readLine("*棋戦詳細：第44期王位戦七番勝負第5局");
		String actual = target.getKisenSyousai();
		
		assertEquals(expected, actual);
		assertTrue(target.isHeaderEnd());
	}
	
	@Test
	public void test_kifuSummary_endDateNotExist_useBeginDate()
	{
		Date expected = createDate(2003, 5, 4);
				
		KifuSummary summary = new KifuSummary("dummyEntryName");
		summary.setBegin(expected);
		
		Date actual = summary.getEnd();
		assertEquals(expected, actual);
	}
		
	void callParse(String testData) throws IOException {
		BufferedReader br = createReader(testData);
		target.parse(br);
	}

	BufferedReader createReader(String testData) {
		BufferedReader br = new BufferedReader(new StringReader(testData));
		return br;
	}
}
