package nts.uk.ctx.at.schedule.infra.repository.workschedules;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;
@Data
@AllArgsConstructor
public class ScheduleTimeFromSql {
	// KSCDT_SCH_TIME
	public String sid ;
	public GeneralDate ymd ; 
	public String cid ; 
	public Integer countTbl1; 
	public Integer totalTimeTbl1 ; 
	public Integer totalTimeAct;
	public Integer prsWorkTime  ;
	public Integer prsWorkTimeAct ;
	public Integer prsPrimeTime  ;
	public Integer prsMidniteTime  ;
	public Integer extBindTimeOtw ;
	public Integer extBindTimeHw  ;
	public Integer extVarwkOtwTimeLegal  ;
	public Integer extFlexTime ;
	public Integer extFlexTimePreApp ; 
	public Integer extMidNiteOtwTime  ;
	public Integer extMidNiteHdwTimeLghd  ;
	public Integer extMidNiteHdwTimeIlghd  ;
	public Integer extMidNiteHdwTimePubhd  ;
	public Integer extMidNiteTotal  ;
	public Integer extMidNiteTotalPreApp  ;
	public Integer IntegerervalAtdClock ;
	public Integer IntegerervalTime ;
	public Integer brkTotalTime  ;
	public Integer hdPaidTime ;
	public Integer hdPaidHourlyTime; 
	public Integer hdComTime ;
	public Integer hdComHourlyTime; 
	public Integer hd60hTime ;
	public Integer hd60hHourlyTime ; 
	public Integer hdspTime  ;
	public Integer hdspHourlyTime ;
	public Integer hdstkTime ;
	public Integer hdHourlyTime ;
	public Integer hdHourlyShortageTime ;
	public Integer absenceTime ;
	public Integer vacationAddTime  ;
	public Integer staggeredWhTime ;
	
	// KSCDT_SCH_OVERTIME_WORK
	public Integer frameNoTbl2;
	public Integer overtimeWorkTime;
	public Integer overtimeWorkTimeTrans ;
	public Integer overtimeWorkTimePreApp;
	
	// KSCDT_SCH_HOLIDAY_WORK
	public Integer frameNoTbl3;
	public Integer holidayWorkTsStart;
	public Integer holidayWorkTsEnd;
	public Integer holidayWorkTime;
	public Integer holidayWorkTimeTrans;
	public Integer holidayWorkTimePreApp;
	
	// KSCDT_SCH_BONUSPAY
	public Integer bonuspayType;
	public Integer frameNoTbl4;
	public Integer premiumTimeTbl4;
	public Integer premiumTimeWithIn;
	public Integer premiumTimeWithOut;
	
	// KSCDT_SCH_PREMIUM
	public Integer frameNoTbl5;
	public Integer premiumTimeTbl5;
	
	// KSCDT_SCH_SHORTTIME
	public Integer childCareAtr;
	public Integer countTbl6; 
	public Integer totalTimeTbl16; 
	public Integer totalTimeWithIn; 
	public Integer totalTimeWithOut;
	
	// KSCDT_SCH_COME_LATE
	public Integer workNoTbl7;
	public Integer useHourlyHdPaidTbl7; 
	public Integer useHourlyHdComTbl7; 
	public Integer useHourlyHd60hTbl7; 
	public Integer useHourlyHdSpNOTbl7;
	public Integer useHourlyHdSpTimeTbl7;
	public Integer useHourlyHdChildCareTbl7; 
	public Integer useHourlyHdNurseCareTbl7; 
	
	// KSCDT_SCH_GOING_OUT
	public Integer reasonAtr;
	public Integer useHourlyHdPaidTbl8; 
	public Integer useHourlyHdComTbl8; 
	public Integer useHourlyHd60hTbl8; 
	public Integer useHourlyHdSpNOTbl8;
	public Integer useHourlyHdSpTimeTbl8;
	public Integer useHourlyHdChildCareTbl8; 
	public Integer useHourlyHdNurseCareTbl8;
	
	// KSCDT_SCH_LEAVE_EARLY
	public Integer workNoTbl9;
	public Integer useHourlyHdPaidTbl9; 
	public Integer useHourlyHdComTbl9; 
	public Integer useHourlyHd60hTbl9; 
	public Integer useHourlyHdSpNOTbl9;
	public Integer useHourlyHdSpTimeTbl9;
	public Integer useHourlyHdChildCareTbl9; 
	public Integer useHourlyHdNurseCareTbl9;
	
}
