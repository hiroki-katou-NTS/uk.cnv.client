package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author HieuLt
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
 
public class AddScheduleByDisplaySettingCommand {

	// 表示範囲
	public int dispRange;
	// 開始時刻
	public int startTime;
	// 初期表示の開始時刻
	public int initDispStart;
	// 対象組織の単位
	public int targetUnit;
	// 組織ID
	public String organizationID;

}
