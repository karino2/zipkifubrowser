package com.googlecode.zipkifubrowser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

public class KifuStreamHandler implements StreamHandlable{
	
	KifuSummary summary;
	KifuSummaryStorable storable;

	public KifuStreamHandler()
	{
		this(new KifuSummaryStorable() {
			@Override
			public void save(KifuSummary summary) {
				// do nothing
			}
		});
	}
	
	public KifuStreamHandler(KifuSummaryStorable store)
	{
		storable = store;
	}

	public void parse(BufferedReader reader) throws IOException
	{
		while(!isHeaderEnd())
		{
			String line = reader.readLine();
			if(line == null)
				return;
			readLine(line);
		}
	}
	
	public void action(String entryName, BufferedReader reader) throws IOException
	{
		newSummary(entryName);
		parse(reader);
		storable.save(getSummary());
	}
	
	public KifuSummary getSummary()
	{
		return summary;
	}
	
	public void newSummary(String entryName)
	{
		summary = new KifuSummary(entryName);
	}
	
	public Date getBegin() {
		return summary.getBegin();
	}

	public Date getEnd() {
		return summary.getEnd();
	}

	public String getKisen() {
		return summary.getKisen();
	}

	public String getKisenSyousai() {
		if(summary == null)
			return ""; // if prev action was zip, it might happen.
		return summary.getKisenSyousai();
	}
	
	public String getSenkei() {
		return summary.getSenkei();
	}

	public String getSente() {
		return summary.getSente();
	}

	public String getGote() {
		return summary.getGote();
	}

	public boolean isHeaderEnd() {
		return getKisenSyousai() != null;
	}

	private String parseField(String fieldHeader, String line)
	{
		if(line.startsWith(fieldHeader))
			return line.substring(fieldHeader.length());
		return "";
	}
	
	private Date createDate(String val) {
		if(val.length() > 10)
			val = val.substring(0, 10);
		return new Date(val);
	}
	
	public void readLine(String line) {
		String val = parseField("開始日時：", line);
		if(!"".equals(val)) {
			summary.setBegin(createDate(val));
			return;
		}
		val = parseField("終了日時：", line);
		if(!"".equals(val)) {
			// for 03057.KI2, end field twice and second one is illegal.
			if(summary.getRawEnd() == null)
				summary.setEnd(createDate(val));
			return;
		}
		
		val = parseField("棋戦：", line);
		if(!"".equals(val)) {
			summary.setKisen(val);
			return;
		}
		
		val = parseField("戦型：", line);
		if(!"".equals(val)) {
			summary.setSenkei(val);
			return;
		}
		
		val = parseField("先手：", line);
		if(!"".equals(val)) {
			summary.setSente(val);
			return;
		}
		
		val = parseField("後手：", line);
		if(!"".equals(val)) {
			summary.setGote(val);
			return;
		}
		
		val = parseField("*棋戦詳細：", line);
		if(!"".equals(val)) {
			summary.setKisenSyousai(val);
			return;
		}

		/*
		 * KisenSyousai is more important. use KisenSyousai as header end marker.
		if(val.equals("")) {
			headerEnd = true;
			return;
		}
		*/
	}
}
