package nts.uk.screen.at.app.kdw013.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;

@AllArgsConstructor
@Getter
public class WorkContentCommand {

	/** 勤務先: 応援別勤務の勤務先 */
	private WorkplaceOfWorkEachOuenCommand workplace;

	/** 作業: 作業グループ */
	private WorkGroupCommand work;

	/** 備考: 作業入力備考 */
//	@Setter
//	private Optional<WorkinputRemarks> workRemarks;

	/** 作業補足情報 */
	private WorkSuppInfoCommand workSuppInfo;

	public WorkContent toDomain() {

		return WorkContent.create(this.getWorkplace().toDomain(),
				Optional.ofNullable(this.getWork() == null ? null : this.getWork().toDomain()),
				Optional.ofNullable(this.getWorkSuppInfo() == null ? null : this.getWorkSuppInfo().toDomain()));
	}

}
