package com.jyckos.mails.objects;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Layout {
	private @Getter List<String> defaultMsg;
	private @Getter int fields = 0; // 0 means none
	public Layout(List<String> msg, int fields) {
		this.defaultMsg = msg;
		this.fields = fields;
	}
	public List<String> translate(String... field) {
		int size = field.length;
		ArrayList<String> msg = new ArrayList<String>();
		for (String str : defaultMsg) {
			for (int i = 0; i < size; i++) {
				/*
				 * field[0] = %f1
				 * field[1] = %f2
				 * field[2] = %f3
				 */
				StringBuilder build = new StringBuilder();
				build.append("%f");
				build.append((i + 1));
				
				str = str.replaceAll(build.toString(), field[i]);
			}
			msg.add(str);
		}
		return msg;
	}
}
