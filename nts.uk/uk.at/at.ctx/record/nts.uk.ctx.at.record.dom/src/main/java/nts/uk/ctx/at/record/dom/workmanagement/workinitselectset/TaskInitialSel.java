package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.com.history.HistoryItem;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.作業管理.作業初期選択設定
 * 作業初期選択項目
 * @author HieuLt
 */
@Getter
@AllArgsConstructor
public class TaskInitialSel extends  HistoryItem<DatePeriod, GeneralDate> {
	
	/** 社員ID **/ 
	private final String empID;
	
	/** 期間 **/
	private DatePeriod datePeriod;
	
	/** 作業項目 **/
	@Setter
	private TaskItem taskItem;
	
	//	[1] 作業項目を変更する
	public void changeTaskItem(TaskItem changetaskItem){
	//	@作業項目 = 変更後の作業項目	
	setTaskItem(changetaskItem);
	}

	@Override
	public DatePeriod span() {
		return datePeriod;
	}


	@Override
	public void changeSpan(DatePeriod newSpan) {
		datePeriod = newSpan;
		
	}

	@Override
	public String identifier() {
		return this.empID;
	}
	
	@Override
	public boolean equals(HistoryItem<DatePeriod, GeneralDate> other) {
		// TODO Auto-generated method stub
		if (other == null) {
			return false;
		}
		return other.start().equals(datePeriod.start()) && other.end().equals(datePeriod.end());
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((empID == null) ? 0 : empID.hashCode());
		result = prime * result + ((datePeriod == null) ? 0 : datePeriod.start().hashCode());
		result = prime * result + ((datePeriod == null) ? 0 : datePeriod.end().hashCode());
		return result;
	}
}
