package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public class TaskInitialSelHistTest {
	
	@Test
	public void getters() {
		TaskInitialSelHist data = TaskInitialSelHistHelper.getTaskInitialSelHistDefault();
		NtsAssert.invokeGetters(data);
	}	
	
	/*
	 * test Func deleteHistory
	 * case if $直前の履歴.isPresent = true
	 */
	@Test
	public void deleteHistory() {
		
		TaskInitialSelHist taskInitialSelHist = TaskInitialSelHistHelper.getTaskInitialSelHistDefault();
		
		TaskInitialSel param = TaskInitialSelHistHelper.getParamTestdeleteHistory();
		
		taskInitialSelHist.deleteHistory(param);
		
		List<TaskInitialSel> lstHistoryResult = taskInitialSelHist.getLstHistory();
		
		assertThat(lstHistoryResult.size()).isEqualTo(1); // sau khi xóa 1 lịch sử đi rồi thì chỉ còn 1 lịch sử mà thôi
		
		assertThat(lstHistoryResult.get(0).getDatePeriod().start()).isEqualTo(GeneralDate.ymd(2021, 06, 01));
		assertThat(lstHistoryResult.get(0).getDatePeriod().end()).isEqualTo(GeneralDate.ymd(9999, 12, 31));

	}
	
	/*
	 * test Func changeHistory
	 * case if 変更する履歴開始日 == 変更後の期間.開始日
	 */
	@Test
	public void changeHistory1() {

		TaskInitialSelHist taskInitialSelHist = TaskInitialSelHistHelper.getTaskInitialSelHistDefault();			
		
		TaskItem taskItemParam = TaskInitialSelHistHelper.getParamTaskItemChangeHistory();
		
		DatePeriod periodParam =  new DatePeriod( GeneralDate.ymd(2021, 07, 01),  GeneralDate.ymd(9999, 12, 31));
		
		GeneralDate dateParam = GeneralDate.ymd(2021, 07, 01);
		
		taskInitialSelHist.changeHistory(taskItemParam, periodParam, dateParam);
		
		assertThat(taskInitialSelHist.getLstHistory().get(1).getTaskItem()).isEqualToComparingFieldByField(taskItemParam);
		
		assertThat(taskInitialSelHist.getLstHistory().get(0).getDatePeriod().start()).isEqualTo(GeneralDate.ymd(2021, 06, 01));// check start của lịch sử 1
		assertThat(taskInitialSelHist.getLstHistory().get(0).getDatePeriod().end()).isEqualTo(GeneralDate.ymd(2021, 06, 30));// check end của lịch sử 1
		assertThat(taskInitialSelHist.getLstHistory().get(1).getDatePeriod().start()).isEqualTo(GeneralDate.ymd(2021, 07, 01));// check start của lịch sử 2
		assertThat(taskInitialSelHist.getLstHistory().get(1).getDatePeriod().end()).isEqualTo(GeneralDate.ymd(9999, 12, 31));// check end của lịch sử 2
	} 
	
	/*
	 * test Func changeHistory
	 * case if 変更する履歴開始日 <> 変更後の期間.開始日
	 */
	@Test
	public void changeHistory2() {
		// gổm 2 khoảng lịch sủ là (2021-06-01, 2021-06-30), (2021-07-01, 9999-12-31)
		TaskInitialSelHist taskInitialSelHist = TaskInitialSelHistHelper.getTaskInitialSelHistDefault();			
		// TaskItem muốn thay đổi.
		TaskItem taskItemParam = TaskInitialSelHistHelper.getParamTaskItemChangeHistory();
		// DatePeriod muốn thay đổi.
		DatePeriod periodParam =  new DatePeriod( GeneralDate.ymd(2021, 8, 01),  GeneralDate.ymd(9999, 12, 31));
		// start date của khoảng lịch sủ muốn thay đổi (Trong trường hợp này là thằng lịch sử thử 2 trong list lịch sử của domian)
		GeneralDate dateParam = GeneralDate.ymd(2021, 07, 1);

		taskInitialSelHist.changeHistory(taskItemParam, periodParam, dateParam);
		// update lại enđate của lịch sủ đầu tiên trong list lích sủ domain
		assertThat(taskInitialSelHist.getLstHistory().get(0).getDatePeriod().start()).isEqualTo(GeneralDate.ymd(2021, 06, 01));
		assertThat(taskInitialSelHist.getLstHistory().get(0).getDatePeriod().end()).isEqualTo(GeneralDate.ymd(2021, 07, 31));
		
		// update lại lịch sủ thứ 2 trong list lich sủ domain
		assertThat(taskInitialSelHist.getLstHistory().get(1).getDatePeriod().start()).isEqualTo(GeneralDate.ymd(2021, 8, 01));
		assertThat(taskInitialSelHist.getLstHistory().get(1).getDatePeriod().end()).isEqualTo(GeneralDate.ymd(9999, 12, 31));
		assertThat(taskInitialSelHist.getLstHistory().get(1).getTaskItem()).isEqualToComparingFieldByField(taskItemParam);
	}
}
