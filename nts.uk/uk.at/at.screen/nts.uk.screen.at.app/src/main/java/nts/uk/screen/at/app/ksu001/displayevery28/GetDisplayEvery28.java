package nts.uk.screen.at.app.ksu001.displayevery28;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.DateInMonth;
import nts.uk.ctx.at.record.app.find.monthly.root.common.DatePeriodDto;
import nts.uk.screen.at.app.ksu001.get28dateperiod.ScreenQuery28DayPeriod;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.FuncCtrlDisplayFormatDto;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInShift;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInShiftParam;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInShiftResult;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInWorkInfoParam;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInWorkInfoResult;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInWorkInfomation;
/**
 * 28日周期で表示する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).A：個人スケジュール修正(職場別).メニュー別OCD
 * @author hoangnd
 *
 */

@Stateless
public class GetDisplayEvery28 {

	@Inject
	private ScreenQuery28DayPeriod screenQuery28DayPeriod;
	
	@Inject
	private ChangePeriodInShift changePeriodInShift;
	
	@Inject
	private ChangePeriodInWorkInfomation changePeriodInWorkInfomation;
	
	public DisplayEvery28Dto get(DisplayEvery28Param param) {
		DisplayEvery28Dto output = new DisplayEvery28Dto();
		// 取得する(年月日)
		DatePeriodDto datePeriodDto = screenQuery28DayPeriod.get(param.getDate(), param.isNextMonth);
		if (param.getMode() == FuncCtrlDisplayFormatDto.Shift.value) { // Aa:シフト表示の場合
			ChangePeriodInShiftResult changePeriodInShiftResult_New =
					changePeriodInShift.getData(
							new ChangePeriodInShiftParam(
									datePeriodDto.getStart(),
									datePeriodDto.getEnd(),
									param.getUnit(),
									param.getWorkplaceId(),
									param.getWorkplaceGroupId(),
									param.getSids(),
									param.getListShiftMasterNotNeedGetNew(),
									param.getActualData,
									param.getPersonalCounterOp(),
									param.getWorkplaceCounterOp(),
									new DateInMonth(param.getDay(), param.isLastDay)
									)
							);
			
			output.setChangePeriodInShiftResult_New(changePeriodInShiftResult_New);
		} else { // Ab:勤務表示、Ac:略名表示の場合
			
			ChangePeriodInWorkInfoResult changePeriodInWorkInfoResult_New =
					changePeriodInWorkInfomation.getData(
							new ChangePeriodInWorkInfoParam(
									datePeriodDto.getStart(),
									datePeriodDto.getEnd(),
									param.getUnit(),
									param.getWorkplaceId(),
									param.getWorkplaceGroupId(),
									param.getSids(),
									param.getActualData,
									param.getPersonalCounterOp(),
									param.getWorkplaceCounterOp(),
									new DateInMonth(param.getDay(), param.isLastDay)
									)
							);
			
			output.setChangePeriodInWorkInfoResult_New(changePeriodInWorkInfoResult_New);
		}
		
		
		return output;
		
	}
	
}
