package nts.uk.ctx.at.record.dom.daily.ouen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

/**
 * @author thanhpv
 * @name 作業利用頻度
 */
@AllArgsConstructor
@Getter
public class WorkUsageFrequency {

	//作業グループ
	private WorkGroup workGroup;
	
	//回数
	private int number;
	
	public void addOne() {
		this.number++;
	};
}
