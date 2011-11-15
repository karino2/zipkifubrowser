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
		
		filterCondition.setFromEnable(true);
		filterCondition.setFrom(dt);
		String actual = filterCondition.generateQuery();
		
		assertEquals(expected, actual);
	}

	@Test
	public void test_generateQuery_setTo()
	{
		Date dt = createDate(2000, 10, 4);
		String expected = "END <= " + dt.getTime();
		
		filterCondition.setToEnable(true);
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
		
		filterCondition.setFromEnable(true);
		filterCondition.setFrom(from);
		filterCondition.setToEnable(true);
		filterCondition.setTo(to);
		String actual = filterCondition.generateQuery();
		
		assertEquals(expected, actual);
	}
}
