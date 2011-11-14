package com.googlecode.zipkifubrowser;

import java.util.Date;

public class KifuSummary {
	private Date begin;
	private Date end;
	private String kisen;
	private String senkei;
	private String sente;
	private String gote;
	private String kisenSyousai;
	private String path;
	
	public String getPath() { return path; }
	
	public KifuSummary(String path) {
		this.path = path; 
	}
	
	public void setBegin(Date begin) {
		this.begin = begin;
	}
	public Date getBegin() {
		return begin;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public Date getEnd() {
		if(end == null)
			return begin; // use begin in this case.
		return getRawEnd();
	}
	public void setKisen(String kisen) {
		this.kisen = kisen;
	}
	public String getKisen() {
		return kisen;
	}
	public void setSenkei(String senkei) {
		this.senkei = senkei;
	}
	public String getSenkei() {
		return senkei;
	}
	public void setSente(String sente) {
		this.sente = sente;
	}
	public String getSente() {
		return sente;
	}
	public void setGote(String gote) {
		this.gote = gote;
	}
	public String getGote() {
		return gote;
	}
	public void setKisenSyousai(String kisenSyousai) {
		this.kisenSyousai = kisenSyousai;
	}
	public String getKisenSyousai() {
		return kisenSyousai;
	}

	public Date getRawEnd() {
		return end;
	}

}
