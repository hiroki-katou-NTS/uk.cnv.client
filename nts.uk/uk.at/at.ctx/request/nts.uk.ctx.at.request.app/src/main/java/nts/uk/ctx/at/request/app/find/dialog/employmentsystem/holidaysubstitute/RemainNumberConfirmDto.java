package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute;

import java.util.List;

import lombok.Data;

/**
 * 残数確認ダイアログDTO
 * @author phongtq
 *
 */

@Data
public class RemainNumberConfirmDto {

	// 1ヶ月以内期限切れ数
	private String expiredWithinMonth;
	
	// 単位
	private int unit;
	
	// 期限の一番近い日
	private String dayCloseDeadline;
	
	// 残数詳細一覧 - 残数詳細情報
	private List<RemainNumberDetailedInfo> detailRemainingNumbers;
	
	// 現時点残数
	private String currentRemainNumber;
	
	// 社員ID
	private String employeeId;
	
	// 管理する
	private int management;

	public RemainNumberConfirmDto(String expiredWithinMonth, int unit, String dayCloseDeadline,
			List<RemainNumberDetailedInfo> detailRemainingNumbers, String currentRemainNumber, String employeeId,
			int management) {
		super();
		this.expiredWithinMonth = expiredWithinMonth;
		this.unit = unit;
		this.dayCloseDeadline = dayCloseDeadline;
		this.detailRemainingNumbers = detailRemainingNumbers;
		this.currentRemainNumber = currentRemainNumber;
		this.employeeId = employeeId;
		this.management = management;
	}
}
