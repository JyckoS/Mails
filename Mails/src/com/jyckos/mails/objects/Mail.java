package com.jyckos.mails.objects;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Builder
public class Mail {
	private @Getter  String sender;
	private @Getter String date;
	private @Getter  List<String> messages;
	private @Getter @Builder.Default UUID uniqueID = UUID.randomUUID();
	private @Getter List<String> originalmessage;
	public static Mail getMail(String sender, List<String> msg, List<String> original, String date) {
		Mail m = Mail.builder().sender(sender).messages(msg).date(date).originalmessage(original).build();
		return m;
	}
	private @Builder.Default @Getter @Setter boolean read = false;
}
