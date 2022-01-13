package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.screen.at.app.kdw013.command.TimeSpanForCalcCommand;

@AllArgsConstructor
@Getter
public class TimeActualStampCommand {
	private WorkStampCommand actualStamp;
	// 打刻

	private WorkStampCommand stamp;

	// 打刻反映回数

	private Integer numberOfReflectionStamp;

	// 時間外の申告

	private OvertimeDeclarationCommand overtimeDeclaration;

	// 時間休暇時間帯

	private TimeSpanForCalcCommand timeVacation;

	public TimeActualStamp toDomain(){
		return new TimeActualStamp(
				this.getActualStamp().toDomain(),
				this.getStamp().toDomain(),
				this.getNumberOfReflectionStamp(),
				this.getOvertimeDeclaration().toDomain(),
				TimeSpanForCalcCommand.toDomain(this.getTimeVacation()));
		
	}

}
