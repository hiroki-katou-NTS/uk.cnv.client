package nts.uk.ctx.at.record.app.command.kdp.kdp001.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.command.stamp.card.stampcard.management.personalengraving.WorkGroupCommand;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.WorkInformationStamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.SupportCardNumber;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
public class RefectActualResultCommand {
	@Getter
	private String cardNumberSupport;

	@Getter
	private String workPlaceId;

	/**
	 * 打刻場所コード 勤務場所コード old
	 */
	@Getter
	private String workLocationCD;

	/**
	 * 就業時間帯コード
	 */
	@Getter
	private String workTimeCode;

	/**
	 * 時間外の申告
	 */
	@Getter
	private OvertimeDeclarationComamnd overtimeDeclaration;

	/**
	 * 	作業グループ
	 */
	@Getter
	private WorkGroupCommand workGroup;

	public RefectActualResult toDomainValue() {

		WorkGroup workGroupResult = null;

		if (workGroup != null) {
			if (workGroup.getWorkCode1() != null) {
				workGroupResult = new WorkGroup(new WorkCode(workGroup.getWorkCode1()),
						Optional.ofNullable(workGroup.getWorkCode2() == null ?  null : new WorkCode(workGroup.getWorkCode2())),
						Optional.ofNullable(workGroup.getWorkCode3() == null ?  null : new WorkCode(workGroup.getWorkCode3())),
						Optional.ofNullable(workGroup.getWorkCode4() == null ?  null : new WorkCode(workGroup.getWorkCode4())),
						Optional.ofNullable(workGroup.getWorkCode5() == null ?  null : new WorkCode(workGroup.getWorkCode5())));
			}
		}

		WorkInformationStamp workInformationStamp = new WorkInformationStamp(
				this.workPlaceId == null ? Optional.empty() : Optional.of(this.workPlaceId), Optional.empty(),
				this.workLocationCD == null ? Optional.empty() : Optional.of(new WorkLocationCD(this.workLocationCD)),
				this.cardNumberSupport == null ? Optional.empty()
						: Optional.of(new SupportCardNumber(Integer.valueOf(this.cardNumberSupport))));
		return new RefectActualResult(workInformationStamp,
				workTimeCode != null ? new WorkTimeCode(workTimeCode) : null,
				overtimeDeclaration != null ? overtimeDeclaration.toDomainValue() : null,
				workGroupResult);
	}
}
