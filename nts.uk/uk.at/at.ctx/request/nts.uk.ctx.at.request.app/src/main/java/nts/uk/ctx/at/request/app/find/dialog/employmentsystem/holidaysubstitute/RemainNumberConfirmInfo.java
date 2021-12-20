package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute;

import java.util.List;

import lombok.Data;

@Data
public class RemainNumberConfirmInfo {
	
	// List＜残数詳細情報＞
	private List<RemainNumberDetailedInfo> detailedInfos;
	
	// 1ヶ月以内期限切れ数
	private String expiredWithinMonth;
	
	// 期限の一番近い日
	private String dayCloseDeadline;

	public RemainNumberConfirmInfo(List<RemainNumberDetailedInfo> detailedInfos, String expiredWithinMonth,
			String dayCloseDeadline) {
		super();
		this.detailedInfos = detailedInfos;
		this.expiredWithinMonth = expiredWithinMonth;
		this.dayCloseDeadline = dayCloseDeadline;
	}
}
