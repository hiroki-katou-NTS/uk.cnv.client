package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;

/**
 * «Command» 時間帯を指定して作業予定を追加する
 * @author HieuLt
 *
 */
public class AddWorkScheduleByTimezoneCommand {
	
	//List＜社員ID＞
	public List<String> lstEmpId;
	//年月日
	public GeneralDate date;
	//対象時間帯
	public TimeSpanForCalc timeSpanForCalc;
	//作業コード
	public String taskCode;
	
	
}
