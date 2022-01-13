package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeSheet;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutingTimeSheetDto {
	/*
	 * 外出枠NO
	 */
	private Integer outingFrameNo;

	// 外出: 勤怠打刻
	private WorkStampDto goOut;

	/*
	 * 外出理由
	 */
	private Integer reasonForGoOut;

	// 戻り: 勤怠打刻
	private WorkStampDto comeBack;

	public static OutingTimeSheetDto fromDomain(OutingTimeSheet domain) {
		return new OutingTimeSheetDto(
				domain.getOutingFrameNo().v(), 
				domain.getGoOut().map(go-> WorkStampDto.fromDomain(go)).orElse(null),
				domain.getReasonForGoOut().value,
				domain.getComeBack().map(go-> WorkStampDto.fromDomain(go)).orElse(null));
	}
}
