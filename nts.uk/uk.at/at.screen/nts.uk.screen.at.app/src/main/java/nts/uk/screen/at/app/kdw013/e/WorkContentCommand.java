package nts.uk.screen.at.app.kdw013.e;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.kdw013.a.WorkGroupCommand;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

/**
 * 
 * @author tutt
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkContentCommand {

	/** 作業: 作業グループ */
	public WorkGroupCommand work;

	public WorkContent toDomain() {
		return WorkContent.create(null,
				Optional.of(
						this.work == null ? null
								: WorkGroup.create(this.work.getWorkCD1(), this.work.getWorkCD2(),
										this.work.getWorkCD3(), this.work.getWorkCD4(), this.work.getWorkCD5())),
				Optional.empty());
	}
}
