package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * «Command» 作業予定を終日指定して更新する
 * @author HieuLt
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterWorkScheduleAllDayCommand {
	//List<社員ID＞
	public List<String> lstEmpId;
	//年月日
	public GeneralDate date;
	//作業コード
	public String taskCode;
	
}
