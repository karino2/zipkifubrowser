package com.googlecode.zipkifubrowser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

public class KifuStreamHandler implements StreamHandlable{

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
	
	public void action(BufferedReader reader) throws IOException
	{
		parse(reader);
	}
	
	private Date begin;
	private Date end;
	private String kisen;
	private String senkei;
	private String sente;
	private String gote;
	private String kisenSyousai;

	public Date getBegin() {
		return begin;
	}

	public Date getEnd() {
		return end;
	}

	public String getKisen() {
		return kisen;
	}

	public String getKisenSyousai() {
		return kisenSyousai;
	}
	
	public String getSenkei() {
		return senkei;
	}

	public String getSente() {
		return sente;
	}

	public String getGote() {
		return gote;
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

	public void readLine(String line) {
		String val = parseField("開始日時：", line);
		if(!"".equals(val)) {
			begin = new Date(val);
			return;
		}
		val = parseField("終了日時：", line);
		if(!"".equals(val)) {
			end = new Date(val);
			return;
		}
		
		val = parseField("棋戦：", line);
		if(!"".equals(val)) {
			kisen = val;
			return;
		}
		
		val = parseField("戦型：", line);
		if(!"".equals(val)) {
			senkei = val;
			return;
		}
		
		val = parseField("先手：", line);
		if(!"".equals(val)) {
			sente = val;
			return;
		}
		
		val = parseField("後手：", line);
		if(!"".equals(val)) {
			gote = val;
			return;
		}
		
		val = parseField("*棋戦詳細：", line);
		if(!"".equals(val)) {
			kisenSyousai = val;
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
