package nts.uk.screen.at.app.ksu001.get28dateperiod;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.monthly.root.common.DatePeriodDto;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.HolidayCheckUnit;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.HolidayNumberManagement;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.TreatmentHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.TreatmentHolidayRepository;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagement;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagementRepo;
import nts.uk.shr.com.context.AppContexts;

/**
 * 28日の期間を取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).A：個人スケジュール修正(職場別).メニュー別OCD
 * @author hoangnd
 *
 */

@Stateless
public class ScreenQuery28DayPeriod {

	@Inject
	private TreatmentHolidayRepository treatmentHolidayRepository;
	
	@Inject
	private WeekRuleManagementRepo weekRuleManagementRepo;
	
	/**
	 * @param ymd 基準日: 年月日
	 * @param isNextMonth 期間を進めるか: boolean
	 * @return DatePeriodDto
	 */
	public DatePeriodDto get(GeneralDate ymd, boolean isNextMonth) {
		String companyId = AppContexts.user().companyId();
		// 1: get
		Optional<TreatmentHoliday> treatmentHolidayOp = treatmentHolidayRepository.get(companyId);
		
		// 1.1: // 管理期間の単位を取得する() : 休日チェック単位
		HolidayCheckUnit holidayUnit =  treatmentHolidayOp.get().getHolidayManagement().getUnitManagementPeriod();
		
		// 2: 休日取得数と管理期間を取得する(@Require, 年月日) : 休日取得数管理
		HolidayNumberManagement holidayNumberManagement = treatmentHolidayOp.get().getNumberHoliday(new Require(weekRuleManagementRepo), ymd);
		
		/**
		 * 【Output】 期間
		 * 
		 * ◆休日チェック単位==４週間 
		 *  期間：休日取得数管理.期間
		 * 
		 * ◆休日チェック単位==1週間
		 *  ・期間進めるか＝＝true 
		 *   期間：休日取得数管理.期間+後の3週
		 * 
		 *  ・期間進めるか＝＝false 
		 *   期間：休日取得数管理.期間+前の3週
		 */
		
		DatePeriod period = holidayNumberManagement.getPeriod();
		
		if (holidayUnit.value == HolidayCheckUnit.FOUR_WEEK.value) {
			
			return DatePeriodDto.from(period);
			
		} else if (holidayUnit.value == HolidayCheckUnit.ONE_WEEK.value) {
			
			if (isNextMonth) {
				
				return DatePeriodDto.from(period.newSpan(period.start(), period.end().addDays(21)));
				
			} else {
				
				return DatePeriodDto.from(period.newSpan(period.start().addDays(-21), period.end()));
			}
		}
		
		throw new BusinessException("Cannot get DatePeriod");
	}
	
	@AllArgsConstructor
	public class Require implements TreatmentHoliday.Require {

		@Inject
		private WeekRuleManagementRepo weekRuleManagementRepo;
		
		@Override
		public WeekRuleManagement find() {
			
			return weekRuleManagementRepo.find(AppContexts.user().companyId()).orElse(null);
		}
		
	}
}
