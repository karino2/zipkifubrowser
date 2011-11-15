package com.googlecode.zipkifubrowser.test;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.googlecode.zipkifubrowser.FilterCondition;

public class FilterConditionTest {
	FilterCondition filterCondition = new FilterCondition();
	
	@Test
	public void test_generateQuery_empty()
	{
		String expected = "";
		String actual = filterCondition.generateQuery();
		assertEquals(expected, actual);
	}
	
	public Date createDate(int year, int month, int day)
	{
		return KifuStreamHandlerTest.createDate(year, month, day);
	}

	@Test
	public void test_generateQuery_setFrom_butNotEnable()
	{
		Date dt = createDate(2000, 10, 4);
		String expected = "";
		
		filterCondition.setFrom(dt);
		String actual = filterCondition.generateQuery();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_generateQuery_setFrom()
	{
		Date dt = createDate(2000, 10, 4);
		String expected = "BEGIN >= " + dt.getTime();
		
		filterCondition.setFromEnabled(true);
		filterCondition.setFrom(dt);
		String actual = filterCondition.generateQuery();
		
		assertEquals(expected, actual);
	}

	@Test
	public void test_generateQuery_setTo()
	{
		Date dt = createDate(2000, 10, 4);
		String expected = "END <= " + dt.getTime();
		
		filterCondition.setToEnabled(true);
		filterCondition.setTo(dt);
		String actual = filterCondition.generateQuery();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_generateQuery_setFromTo()
	{
		Date from = createDate(2000, 10, 4);
		Date to = createDate(2001, 10, 4);
		String expected = "BEGIN >= " + from.getTime() + " AND END <= " + to.getTime();
		
		filterCondition.setFromEnabled(true);
		filterCondition.setFrom(from);
		filterCondition.setToEnabled(true);
		filterCondition.setTo(to);
		String actual = filterCondition.generateQuery();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_generateQuery_setSenkei()
	{
		String expected = "SENKEI = ?";
		
		filterCondition.setSenkeiEnabled(true);
		filterCondition.setSenkei("HOGEHOGE");
		String actual = filterCondition.generateQuery();
		
		assertEquals(expected, actual);
		
		String[] actualArgs = filterCondition.generateQueryArg();
		assertEquals(1, actualArgs.length);
		assertEquals("HOGEHOGE", actualArgs[0]);
		
	}
	
	@Test
	public void test_generateQuery_setKisi()
	{
		String expected = "(SENTE = ? OR GOTE = ?)";
		
		filterCondition.setKisiEnabled(true);
		filterCondition.setKisi("hoge");
		String actual = filterCondition.generateQuery();
		
		assertEquals(expected, actual);
		
		String[] actualArgs = filterCondition.generateQueryArg();
		assertEquals(2, actualArgs.length);
		assertEquals("hoge", actualArgs[0]);
		assertEquals("hoge", actualArgs[1]);		
	}
	
	@Test
	public void test_isSenkeiAvailable_setSenkeiEnable_butNoSenkei_meansNotAvailable()
	{
		filterCondition.setSenkeiEnabled(true);
		assertFalse(filterCondition.isSenkeiAvailable());
	}
	
	@Test
	public void test_isSenkeiAvailable_setSenkeiEnable_andSetSenkei()
	{
		filterCondition.setSenkeiEnabled(true);
		filterCondition.setSenkei("HOGE");
		assertTrue(filterCondition.isSenkeiAvailable());
	}
	
	@Test
	public void test_generateQueryArg()
	{
		assertNull(filterCondition.generateQueryArg());
	}
}
