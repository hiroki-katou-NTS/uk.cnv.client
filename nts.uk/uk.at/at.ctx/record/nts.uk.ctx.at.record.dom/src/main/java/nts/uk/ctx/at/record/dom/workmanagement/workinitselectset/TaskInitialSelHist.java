package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.com.history.strategic.PersistentHistory;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.作業管理.作業初期選択設定
 * 作業初期選択履歴
 * 
 * @author HieuLt
 *
 */
@Getter
@AllArgsConstructor
public class TaskInitialSelHist extends AggregateRoot
		implements PersistentHistory<TaskInitialSel, DatePeriod, GeneralDate> {
	/** 社員ID **/
	private final String empId;

	/** 作業初期選択項目 ---履歴リスト **/
	private List<TaskInitialSel> lstHistory;

	
	/**
	 * [7] 履歴を削除する
	 * immediatelyBefore
	 */
	public void deleteHistory(TaskInitialSel taskInitialSel){
		//	$直前の履歴 = 直前の履歴の履歴項目(追加する履歴項目)	
		Optional<TaskInitialSel> data = this.immediatelyBefore(taskInitialSel);
		//		削除する(削除する履歴項目)	
		this.remove(taskInitialSel);
		//	if $直前の履歴.isPresent
		if(data.isPresent()){
			//	$最大終了日 = 年月日#年月日を指定(9999,12,31)
			GeneralDate maxEndDate =  GeneralDate.ymd(9999, 12, 31);
			// 	$変更後の期間 = 期間#期間($直前の履歴.期間.開始日,$最大終了日)		
			DatePeriod changePeriod = new DatePeriod(data.get().getDatePeriod().start(), maxEndDate);
			// 	期間を変更する($直前の履歴,$変更後の期間)																			
			this.changeSpan(data.get(), changePeriod);
		}
	}
	/**
	 * [8] 履歴を変更する	
	 */
	public void changeHistory(TaskItem taskItem, DatePeriod datePeriod, GeneralDate date) {
		
		//if 変更する履歴開始日 <> 変更後の期間.開始日		
		if(!date.equals(datePeriod.start()) ){
			//$変更する履歴 = @履歴リスト：filter $.期間.開始日 == 変更する履歴開始日	
			List<TaskInitialSel> changeHistory = this.lstHistory.stream().filter(c ->c.getDatePeriod().start().equals(date)).collect(Collectors.toList());
			//期間を変更する($変更する履歴,変更後の期間)
			if(!changeHistory.isEmpty()){
			this.changeSpan(changeHistory.get(0), datePeriod);
			}
		}
		//$対象履歴項目 = @履歴リスト：filter $.期間.開始日 == 変更後の期間.開始日
		List<TaskInitialSel> history = this.lstHistory.stream().filter(c ->c.getDatePeriod().start().equals(datePeriod.start())).collect(Collectors.toList());
		if (!history.isEmpty()) { 
			// $対象履歴項目.作業項目を変更する(変更後の作業項目)
			history.get(0).changeTaskItem(taskItem);
		}
	}
	
	@Override
	public List<TaskInitialSel> items() {
		// TODO Auto-generated method stub
		return lstHistory;
	}
}
