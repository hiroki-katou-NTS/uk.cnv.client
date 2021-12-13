package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.TimezoneToUseHourlyHoliday;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.GettingTimeVacactionService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimeVacation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.ClassificationCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
@RunWith(JMockit.class)
public class IntegrationOfDailyTest {
	
	@Injectable 
	TimeLeavingOfDailyAttd timeLeaving;
	
	@Injectable 
	AttendanceTimeOfDailyAttendance attendanceTime;
	
	@Injectable
	OutingTimeOfDailyAttd outingTime;
	
	@Test
	public void testGetTimeVacation_success(
			@Injectable Map<TimezoneToUseHourlyHoliday, TimeVacation> timeVacations) {
		
		new MockUp<GettingTimeVacactionService>() {
			
			@Mock
			public Map<TimezoneToUseHourlyHoliday, TimeVacation> get(Optional<TimeLeavingOfDailyAttd> optTimeLeaving,
					Optional<AttendanceTimeOfDailyAttendance> optAttendanceTime,
					Optional<OutingTimeOfDailyAttd> outingTime) {

				return timeVacations;
				
			}
			
		};
		
		// Arrange
		IntegrationOfDaily  target = Helper.createWithParams(
				Optional.of(timeLeaving), 
				Optional.of(attendanceTime),
				Optional.of(outingTime));
		
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = target.getTimeVacation();
		
		// Assert
		assertThat(result).isEqualTo(timeVacations);
	}

	public static class Helper{
		
		private static WorkInfoOfDailyAttendance defaultWorkInfo = new WorkInfoOfDailyAttendance(
				new WorkInformation(new WorkTypeCode("001"), new WorkTimeCode("001")), 
				CalculationState.No_Calculated, 
				NotUseAttribute.Not_use, 
				NotUseAttribute.Not_use, 
				DayOfWeek.MONDAY, 
				Collections.emptyList(), 
				Optional.empty());
		
		private static AffiliationInforOfDailyAttd defaultAffInfo = new AffiliationInforOfDailyAttd(
				new EmploymentCode("EmpCode-001"),
				"JobTitle-Id-001", 
				"Wpl-Id-001", 
				new ClassificationCode("class-001"), 
				Optional.empty(), 
				Optional.empty());
		
		public static IntegrationOfDaily createWithParams(
				Optional<TimeLeavingOfDailyAttd> optTimeLeaving,
				Optional<AttendanceTimeOfDailyAttendance> optAttendanceTime,
				Optional<OutingTimeOfDailyAttd> outingTime) {
			return new IntegrationOfDaily(
					  "sid"
					, GeneralDate.today()
					, defaultWorkInfo
					, CalAttrOfDailyAttd.createAllCalculate()
					, defaultAffInfo
					, Optional.empty()
					, Collections.emptyList()
					, outingTime
					, new BreakTimeOfDailyAttd()
					, optAttendanceTime
					, optTimeLeaving
					, Optional.empty()
					, Optional.empty()
					, Optional.empty()
					, Optional.empty()
					, Collections.emptyList()
					, Optional.empty()
					, Collections.emptyList()
					, Optional.empty());
		}

	}
}
