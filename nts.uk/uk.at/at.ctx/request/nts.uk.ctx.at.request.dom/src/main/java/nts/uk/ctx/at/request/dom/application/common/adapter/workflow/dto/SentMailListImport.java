package nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto;

import java.util.List;

import lombok.Value;

@Value
public class SentMailListImport {

	/**
	 * メールアドレス
	 */
	private List<String> mailAddresses;
	
	/**
	 * 社員ID
	 */
	private String sid;
}
