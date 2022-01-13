package nts.uk.screen.at.app.kdw013.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.screen.at.app.kdw013.e.WorkTimeInformationCommand;

@AllArgsConstructor
@Getter
public class WorkStampCommand {
	/*
	 * 時刻
	 */
	private WorkTimeInformationCommand timeDay;

	/*
	 * 場所コード
	 */
	private String locationCode;

	public WorkStamp toDomain() {

		return new WorkStamp(
				WorkTimeInformationCommand.toDomain(this.getTimeDay()),
				Optional.ofNullable(new WorkLocationCD(this.getLocationCode())));
	}
}
