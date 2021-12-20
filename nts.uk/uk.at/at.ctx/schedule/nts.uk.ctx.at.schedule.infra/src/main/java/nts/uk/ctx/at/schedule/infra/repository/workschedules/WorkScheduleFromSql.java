package nts.uk.ctx.at.schedule.infra.repository.workschedules;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;
@Data
@AllArgsConstructor
public class WorkScheduleFromSql {

	// KSCDT_SCH_BASIC_INFO
	String sid;
	GeneralDate ymd; 
	String cid; 
	Integer confirmedATR; 
	String empCd; 
	String jobId; 
	String wkpId; 
	String clsCd; 
	String busTypeCd; 
	String nurseLicense; 
	String wktpCd; 
	String wktmCd;
	Integer goStraightAtr;
	Integer backStraightAtr;
	Integer treatAsSubstituteAtr;
	Double treatAsSubstituteDays;
	
	// KSCDT_SCH_EDIT_STATE
	Integer atdItemId;
	Integer editState;
	
	// KSCDT_SCH_ATD_LVW_TIME
	Integer workNo;
	Integer atdClock;
	Integer atdHourlyHDTSStart;
	Integer atdHourlyHDTSEnd;
	Integer lwkClock;
	Integer lvwHourlyHDTSStart;
	Integer lvwHourlyHDTSEnd;
	
	// KSCDT_SCH_SHORTTIME_TS
	Integer childCareAtr;
	Integer frameNoShorttime;
	Integer shortTimeTsStart;
	Integer shortTimeTsEnd;
	
	// KSCDT_SCH_BREAK_TS
	Integer frameNoBreak;
	Integer breakTsStart;
	Integer breakTsEnd;
	
	// KSCDT_SCH_GOING_OUT_TS
	Integer frameNoGoingOut;
	Integer reasonAtr;
	Integer goingOutClock;
	Integer goingBackClock;
	
}
