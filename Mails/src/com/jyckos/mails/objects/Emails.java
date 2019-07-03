package com.jyckos.mails.objects;

import java.util.ArrayList;

import lombok.Getter;

public class Emails { // Mails is a set of Mail
	private @Getter ArrayList<Mail> mails = new ArrayList<Mail>();
	public Emails() {
		
	}
	public void add(Mail m) {
		if (!mails.isEmpty()) {
			ArrayList<Mail> ma = new ArrayList<Mail>();
			ma.add(m);
			for (Mail me : mails) {
			ma.add(me);
			}
			this.mails = ma;
		}
		else {
			mails.add(m);
		}
	}
	public int getUnreads() {
		int unread = 0;
		for (Mail m : mails) {
			if (!m.isRead()) {
				unread++;
				continue;
			}
		}
		return unread;
	}
	public void remove(int index) {
		mails.remove(index);
	}
}
