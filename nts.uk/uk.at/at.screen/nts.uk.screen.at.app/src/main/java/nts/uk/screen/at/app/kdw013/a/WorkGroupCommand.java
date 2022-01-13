package nts.uk.screen.at.app.kdw013.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@Getter
public class WorkGroupCommand {
	/** 作業CD1 */
	private String workCD1;

	/** 作業CD2 */
	private String workCD2;

	/** 作業CD3 */
	private String workCD3;

	/** 作業CD4 */
	private String workCD4;

	/** 作業CD5 */
	private String workCD5;

	public WorkGroup toDomain() {

		return new WorkGroup(new WorkCode(this.getWorkCD1()), Optional.ofNullable(new WorkCode(this.getWorkCD2())),
				Optional.ofNullable(new WorkCode(this.getWorkCD3())),
				Optional.ofNullable(new WorkCode(this.getWorkCD4())),
				Optional.ofNullable(new WorkCode(this.getWorkCD5())));
	}

}
