package com.googlecode.zipkifubrowser;

import java.util.ArrayList;
import java.util.Date;

public class FilterCondition {
	private Date from;
	private Date to;
	private String senkei;
	private String kisi;
	private boolean fromEnable;
	private boolean toEnable;
	private boolean senkeiEnable;
	private boolean kisiEnable;
	
	public void setFrom(Date from) {
		this.from = from;
	}
	
	
	Date getDefaultDate() {
		return new Date();
	}
	
	public Date getFromOrDefault() {
		if(from == null)
			from = getDefaultDate();
		return from;
	}
	
	public Date getToOrDefault() {
		if(to == null)
			to = getDefaultDate();
		return to;
	}
	
	public Date getFrom() {
		return from;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public Date getTo() {
		return to;
	}
	public void setSenkei(String senkei) {
		this.senkei = senkei;
	}
	public String getSenkei() {
		return senkei;
	}
	public void setKisi(String kisi) {
		this.kisi = kisi;
	}
	public String getKisi() {
		return kisi;
	}
	public void setFromEnable(boolean fromEnable) {
		this.fromEnable = fromEnable;
	}
	public boolean isFromEnable() {
		return fromEnable;
	}
	public void setToEnable(boolean toEnable) {
		this.toEnable = toEnable;
	}
	public boolean isToEnable() {
		return toEnable;
	}
	public void setSenkeiEnable(boolean senkeiEnable) {
		this.senkeiEnable = senkeiEnable;
	}
	public boolean isSenkeiEnable() {
		return senkeiEnable;
	}
	public void setKisiEnable(boolean kisiEnable) {
		this.kisiEnable = kisiEnable;
	}
	public boolean isKisiEnable() {
		return kisiEnable;
	}
	
	public String generateQuery() {
		boolean firstTime = true;
		StringBuffer sb = new StringBuffer();
		
		if(isFromEnable()) {
			firstTime = appendAndIfNecessary(firstTime, sb);
			sb.append("BEGIN >= " + getFrom().getTime());
		}

		if(isToEnable()){
			firstTime = appendAndIfNecessary(firstTime, sb);
			sb.append("END <= " + getTo().getTime());			
		}
		
		if(isSenkeiEnable()){
			firstTime = appendAndIfNecessary(firstTime, sb);
			sb.append("SENKEI = ?");
		}
		
		return sb.toString();
	}
	
	public String[] generateQueryArg() {
		ArrayList<String> list = new ArrayList<String>();
		if(isSenkeiEnable()){
			list.add(getSenkei());
		}
		if(list.size() == 0)
			return null;
		return list.toArray(new String[0]);
	}


	boolean appendAndIfNecessary(boolean firstTime, StringBuffer sb) {
		if(!firstTime)
			sb.append(" AND ");
		return false;
	}
}
