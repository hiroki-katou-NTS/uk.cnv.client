package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;


import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;

@Getter
/** 日別勤怠の応援作業時間帯 */
public class OuenWorkTimeSheetOfDailyAttendance implements DomainObject {

	/** 応援勤務枠No: 応援勤務枠No supportNo*/
	private SupportFrameNo workNo;

	/** 作業内容: 作業内容 */
	private WorkContent workContent;
	
	/** 時間帯: 時間帯別勤怠の時間帯 */
	private TimeSheetOfAttendanceEachOuenSheet timeSheet;
	
	/** 作業時間入力フラグ*/
	private Optional<Boolean> inputFlag;

	public OuenWorkTimeSheetOfDailyAttendance(SupportFrameNo workNo, WorkContent workContent, 
			TimeSheetOfAttendanceEachOuenSheet timeSheet, Optional<Boolean> inputFlag) {
		super();
		this.workNo = workNo;
		this.workContent = workContent;
		this.timeSheet = timeSheet;
		this.inputFlag = inputFlag;
	}
	
	public static OuenWorkTimeSheetOfDailyAttendance create(SupportFrameNo workNo, WorkContent workContent, 
			TimeSheetOfAttendanceEachOuenSheet timeSheet, Optional<Boolean> inputFlag) {
		
		return new OuenWorkTimeSheetOfDailyAttendance(workNo, workContent, timeSheet, inputFlag);
	}

	public void setWorkNo(int workNo) {
		this.workNo = SupportFrameNo.of(workNo);
	}

	public void update(OuenWorkTimeSheetOfDailyAttendance inputSheet) {
		this.workContent = inputSheet.getWorkContent();
		this.timeSheet = inputSheet.getTimeSheet();
	}
}
