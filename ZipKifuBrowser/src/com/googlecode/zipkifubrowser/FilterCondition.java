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
	private boolean senkeiEnabled;
	private boolean kisiEnabled;
	
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
	public void setSenkeiEnabled(boolean senkeiEnable) {
		this.senkeiEnabled = senkeiEnable;
	}
	
	public boolean isSenkeiAvailable() {
		return isSenkeiEnabled() && getSenkei() != null;
		
	}
	public boolean isSenkeiEnabled() {
		return senkeiEnabled;
	}
	public void setKisiEnabled(boolean kisiEnable) {
		this.kisiEnabled = kisiEnable;
	}
	public boolean isKisiEnabled() {
		return kisiEnabled;
	}
	public boolean isKisiAvailable() {
		return isKisiEnabled() && getKisi() != null;
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
		
		if(isSenkeiAvailable()){
			firstTime = appendAndIfNecessary(firstTime, sb);
			sb.append("SENKEI = ?");
		}
		
		if(isKisiAvailable()) {
			firstTime = appendAndIfNecessary(firstTime, sb);
			sb.append("(SENTE = ? OR GOTE = ?)");
		}
		
		return sb.toString();
	}
	
	public String[] generateQueryArg() {
		ArrayList<String> list = new ArrayList<String>();
		if(isSenkeiAvailable()){
			list.add(getSenkei());
		}
		if(isKisiAvailable()) {
			list.add(getKisi());
			list.add(getKisi());
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
