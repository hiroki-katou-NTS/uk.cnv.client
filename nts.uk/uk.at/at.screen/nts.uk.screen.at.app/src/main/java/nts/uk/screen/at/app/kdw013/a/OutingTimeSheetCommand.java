package nts.uk.screen.at.app.kdw013.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeSheet;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;

@AllArgsConstructor
@Getter
public class OutingTimeSheetCommand {
	/*
	 * 外出枠NO
	 */
	private Integer outingFrameNo;

	// 外出: 勤怠打刻
	private WorkStampCommand goOut;
	/*
	 * 外出理由
	 */
	private Integer reasonForGoOut;

	// 戻り: 勤怠打刻
	private WorkStampCommand comeBack;

	public OutingTimeSheet toDomain() {

		return new OutingTimeSheet(new OutingFrameNo(this.getOutingFrameNo()),
				Optional.ofNullable(this.getGoOut().toDomain()),
				EnumAdaptor.valueOf(this.getReasonForGoOut(), GoingOutReason.class), 
				Optional.ofNullable(this.getComeBack().toDomain()));
	}
}
