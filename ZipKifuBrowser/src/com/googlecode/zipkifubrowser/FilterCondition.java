package com.googlecode.zipkifubrowser;

import java.util.ArrayList;
import java.util.Date;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FilterCondition {
	private Date from;
	private Date to;
	private String senkei;
	private String kisi;
	private boolean fromEnabled;
	private boolean toEnabled;
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
	public void setFromEnabled(boolean fromEnable) {
		this.fromEnabled = fromEnable;
	}
	public boolean isFromEnabled() {
		return fromEnabled;
	}
	public void setToEnabled(boolean toEnable) {
		this.toEnabled = toEnable;
	}
	public boolean isToEnabled() {
		return toEnabled;
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
		
		if(isFromEnabled()) {
			firstTime = appendAndIfNecessary(firstTime, sb);
			sb.append("BEGIN >= " + getFrom().getTime());
		}

		if(isToEnabled()){
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
	
	public void saveTo(SharedPreferences pref)
	{
		Editor ed = pref.edit();
		ed.putBoolean("FILTER_FROM_ENABLED", isFromEnabled());
		ed.putBoolean("FILTER_TO_ENABLED", isToEnabled());
		ed.putBoolean("FILTER_SENKEI_ENABLED", isSenkeiEnabled());
		ed.putBoolean("FILTER_KISI_ENABLED", isKisiEnabled());
		putDate(ed, "FILTER_FROM", getFrom());
		putDate(ed, "FILTER_TO", getTo());
		ed.putString("FILTER_SENKEI", getSenkei());
		ed.putString("FILTER_KISI", getKisi());		
		ed.commit();
	}
	
	static Date getDateFromPref(SharedPreferences pref, String key) {
		if(pref.contains(key))
			return new Date(pref.getLong(key, 0 ));
		return null;
	}
	
	public static FilterCondition loadFrom(SharedPreferences pref) {
		FilterCondition filter = new FilterCondition();
		filter.setFromEnabled(pref.getBoolean("FILTER_FROM_ENABLED", false));
		filter.setToEnabled(pref.getBoolean("FILTER_TO_ENABLED", false));
		filter.setSenkeiEnabled(pref.getBoolean("FILTER_SENKEI_ENABLED", false));
		filter.setKisiEnabled( pref.getBoolean("FILTER_KISI_ENABLED", false));
		
		filter.setFrom(getDateFromPref(pref, "FILTER_FROM"));
		filter.setTo(getDateFromPref(pref,"FILTER_TO"));
		filter.setSenkei(pref.getString("FILTER_SENKEI", null));
		filter.setKisi(pref.getString("FILTER_KISI", null));		
		return filter;
	}


	void putDate(Editor ed, String key, Date dt) {
		if(dt != null)
			ed.putLong(key, dt.getTime());
		else
			ed.remove(key);
	}
}
