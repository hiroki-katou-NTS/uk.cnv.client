package nts.uk.ctx.at.request.dom.application.appabsence.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;

@Getter
@AllArgsConstructor
public class NumberOfRemainOutput {
	//年休残数
	private int yearDayRemain;

	// 年休残時間
	private int yearHourRemain;
	
	// 積休残数
	private int lastYearRemain;
	
	// 代休残数
	private int subDayRemain;
	
	// 代休残時間
	private int subHdHourRemain;
	
	// 振休残数
	private int vacaRemain;	
	
	// 60H超休残時間
	private int over60HHourRemain;
	
	// 子看護残数
	private int childNursingDayRemain;
	
	// 子看護残時間
	private int childNursingHourRemain;
	
	// 介護残数
	private int nursingRemain;
	
	// 介護残時間
	private int nursingHourRemain;
	
	// 付与年月日
	private GeneralDate grantDate;
	
	// 付与日数
	private int grantDays;
}
