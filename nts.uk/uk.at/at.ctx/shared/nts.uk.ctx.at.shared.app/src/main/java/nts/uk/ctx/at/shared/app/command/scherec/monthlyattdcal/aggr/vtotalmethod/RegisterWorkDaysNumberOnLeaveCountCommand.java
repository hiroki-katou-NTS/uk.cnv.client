package nts.uk.ctx.at.shared.app.command.scherec.monthlyattdcal.aggr.vtotalmethod;

import lombok.Value;

@Value
public class RegisterWorkDaysNumberOnLeaveCountCommand {

	/**
	 * 出勤日数をカウントするか
	 */
	private boolean isCounting;
	
	/**
	 * 休暇の種類
	 */
	private int leaveType;
}
