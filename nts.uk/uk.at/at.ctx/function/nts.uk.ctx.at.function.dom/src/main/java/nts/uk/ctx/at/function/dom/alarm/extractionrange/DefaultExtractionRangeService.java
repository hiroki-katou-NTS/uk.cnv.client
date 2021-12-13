package nts.uk.ctx.at.function.dom.alarm.extractionrange;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.standardtime.AgreementOperationSettingAdapter;
import nts.uk.ctx.at.function.dom.adapter.standardtime.AgreementOperationSettingImport;
import nts.uk.ctx.at.function.dom.alarm.AlarmPatternSettingRepository;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.CheckCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.Period;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.EndSpecify;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.ExtractionPeriodDaily;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.StartSpecify;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.ExtractionPeriodMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.FixedMonthly;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.SpecifyEndMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.SpecifyStartMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.YearSpecifiedType;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.mutilmonth.AverageMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.periodunit.ExtractionPeriodUnit;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.year.AYear;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.HolidayNumberManagement;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.TreatmentHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.TreatmentHolidayRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagement;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagementRepo;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
public class DefaultExtractionRangeService implements ExtractionRangeService {

	@Inject
	private AlarmPatternSettingRepository alarmRepo;
	@Inject
	private ClosureRepository closureRepo;
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;
	@Inject
	private AgreementOperationSettingAdapter agreementOperationSettingAdapter;
	@Inject
	private TreatmentHolidayRepository treatHolidayRepo;
	@Inject
	private WeekRuleManagementRepo weekRuleManagementRepo;
	@Override
	public List<CheckConditionTimeDto> getPeriodByCategory(String alarmCode, String companyId, int closureId,
			Integer processingYm) {
		List<CheckConditionTimeDto> result = new ArrayList<CheckConditionTimeDto>();
		List<CheckCondition> checkConList = alarmRepo.getCheckCondition(companyId, alarmCode);

		checkConList.forEach(c -> {
			if (c.isDaily() || c.isApplication() || c.isScheduleDaily() || c.isWeekly()) {
				CheckConditionTimeDto daily = this.getDailyTime(c, closureId, new YearMonth(processingYm));
				result.add(daily);
				
			} else if (c.is4W4D()) {
				CheckConditionTimeDto schedual_4week = this.get4WeekTime(c, closureId, new YearMonth(processingYm), companyId);
				result.add(schedual_4week);
				
			} else if(c.isMonthly() || c.isMultipleMonth() || c.isScheduleMonthly()) {
				CheckConditionTimeDto other = this.getMonthlyTime(c,closureId,new YearMonth(processingYm));
				result.add(other);
				
			} else if(c.isAgrrement()) {
				result.addAll(this.getAgreementTime(c, closureId, new YearMonth(processingYm), companyId));
			} else if(c.isAttHoliday() || c.isMasterChk()) {
				CheckConditionTimeDto attHoliday = new CheckConditionTimeDto(c.getAlarmCategory().value, c.getAlarmCategory().nameId, 0);
				attHoliday.setTabOrder(7);
				result.add(attHoliday);
			} else if (c.isScheduleYear()) {
				CheckConditionTimeDto other = this.getScheYearTime(c,closureId,new YearMonth(processingYm));
				result.add(other);
			}

		});
		
        Collections.sort(result, Comparator.comparing(CheckConditionTimeDto::getCategory)
                .thenComparing(CheckConditionTimeDto::getTabOrder));
		return result;
	}
	
	//36協定の期間を算出する
	private List<CheckConditionTimeDto> getAgreementTime(CheckCondition c, int closureId, YearMonth yearMonth, String cid){
		List<CheckConditionTimeDto> result = new ArrayList<CheckConditionTimeDto>();
		
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = null;
		Date endDate = null;
//		String startMonth =null;
//		String endMonth = null;
		YearMonth startMonth = yearMonth;
		YearMonth endMonth = yearMonth;
		int year = 0;
		//int checkMonth = 1;
		for(ExtractionRangeBase extractBase : c.getExtractPeriodList()) {
			
			if(extractBase instanceof ExtractionPeriodDaily) {
				ExtractionPeriodDaily extraction = (ExtractionPeriodDaily) extractBase;				
				CheckConditionPeriod period  = this.getPeriodDaily(extraction, closureId, yearMonth, c);
				startDate = period.getStartDate();
				endDate = period.getEndDate();
				CheckConditionTimeDto dailyDto = new CheckConditionTimeDto(c.getAlarmCategory().value, 
						textAgreementTime(1), formatter.format(startDate), formatter.format(endDate), null, null);
				dailyDto.setTabOrder(1);
				dailyDto.setPeriod36Agreement(Period.One_Week.value);
				result.add(dailyDto);
				
			}else if(extractBase instanceof ExtractionPeriodMonth) {
				ExtractionPeriodMonth extraction = (ExtractionPeriodMonth) extractBase;
				
				if(extraction.getStartMonth().isDesignateMonth()) {
					startMonth = yearMonth.addMonths((-1)*extraction.getStartMonth().getStrMonthNo().get().getMonthNo());
				}else {
					Calendar calendar = Calendar.getInstance();
					int currentYear = calendar.get(Calendar.YEAR);
					startMonth = YearMonth.of(currentYear, extraction.getStartMonth().getFixedMonthly().get().getDesignatedMonth());
				}
				
				if(extraction.getEndMonth().isSpecifyClose()) {
					endMonth = yearMonth.addMonths((-1)*extraction.getEndMonth().getEndMonthNo().get().getMonthNo());
				}else {
					endMonth = yearMonth.addMonths((-1)*extraction.getEndMonth().getEndMonthNo().get().getMonthNo());
				}
				CheckConditionTimeDto monthDto = new CheckConditionTimeDto(c.getAlarmCategory().value, 
						textAgreementTime(extraction.getNumberOfMonth().value +2), null, null, startMonth.toString(), endMonth.toString());
				monthDto.setTabOrder(extraction.getNumberOfMonth().value +2);
				if (extraction.getNumberOfMonth() == NumberOfMonth.ONE_MONTH) {
					monthDto.setPeriod36Agreement(Period.One_Month.value);
				} else if (extraction.getNumberOfMonth() == NumberOfMonth.TWO_MONTH) {
					monthDto.setPeriod36Agreement(Period.Two_Month.value);
				} else if (extraction.getNumberOfMonth() == NumberOfMonth.THREE_MONTH) {
					monthDto.setPeriod36Agreement(Period.Three_Month.value);
				}
				//checkMonth++;
				result.add(monthDto);
			//基準月の期間を算出する
			}else if(extractBase instanceof AverageMonth) {
				AverageMonth extraction = (AverageMonth) extractBase;
				CheckConditionTimeDto monthDto = new CheckConditionTimeDto(c.getAlarmCategory().value, 
						TextResource.localize("KAL010_208") + "　" + TextResource.localize("KAL004_120"), null, null, 
						yearMonth.toString(), yearMonth.toString());
				if(extraction.getStrMonth() != StandardMonth.CURRENT_MONTH ) {
					monthDto.setStartMonth(yearMonth.addMonths(-extraction.getStrMonth().value).toString());
					monthDto.setEndMonth(yearMonth.addMonths(-extraction.getStrMonth().value).toString());
				}
				monthDto.setTabOrder(6);
				monthDto.setPeriod36Agreement(Period.Months_Average.value);
				result.add(monthDto);
				
			}else if(extractBase instanceof AYear) {
				AYear extraction = (AYear) extractBase;
				Optional<AgreementOperationSettingImport> agreementOperationSettingImport =  agreementOperationSettingAdapter.findForAlarm(cid);
				int firstMonth = 0;
				int yearMonthYear = yearMonth.year();
				if (agreementOperationSettingImport.isPresent()){
					firstMonth = agreementOperationSettingImport.get().getStartingMonth().value + 1;
					if(extraction.isToBeThisYear()){
						if(firstMonth <= yearMonth.month()) {
							year = yearMonthYear;
						}else {
							year = yearMonthYear - 1;
						}
						if (firstMonth == 1) {
							startMonth = YearMonth.of(year, firstMonth);
							endMonth = YearMonth.of(year, 12);
						} else {
							startMonth = YearMonth.of(year, firstMonth);
							firstMonth = firstMonth - 1;
							endMonth = YearMonth.of(year + 1, firstMonth);
						}
						
					}else {
						year = extraction.getYear();
						if (firstMonth == 1) {
							startMonth = YearMonth.of(year, firstMonth);
							endMonth = YearMonth.of(year, 12);
						}else{
							startMonth = YearMonth.of(year, firstMonth);
							firstMonth = firstMonth - 1;
							endMonth = YearMonth.of(year + 1, firstMonth);
						}
					}
				} else {
					startMonth = YearMonth.of(GeneralDate.today().year(), 1);
					endMonth = YearMonth.of(GeneralDate.today().year(), 12);
				}
				CheckConditionTimeDto yearDto = new CheckConditionTimeDto(c.getAlarmCategory().value,
						textAgreementTime(5), null, null, startMonth.toString(), endMonth.toString(), year );
				yearDto.setTabOrder(5);
				yearDto.setPeriod36Agreement(Period.Yearly.value);
				result.add(yearDto);
			}
		}
		
		result.sort((a, b) ->  a.getCategoryName().compareTo(b.getCategoryName()));
		return result;
	}
	
	private String textAgreementTime(int tabOrder) {
		switch(tabOrder) {
			case 1 : return TextResource.localize("KAL010_208") + "　" + TextResource.localize("KAL004_69");
			case 2 : return TextResource.localize("KAL010_208") + "　" + TextResource.localize("KAL004_70"); 
			case 3 : return TextResource.localize("KAL010_208") + "　" + TextResource.localize("KAL004_71"); 
			case 4 : return TextResource.localize("KAL010_208") + "　" + TextResource.localize("KAL004_72"); 
			case 5 : return TextResource.localize("KAL010_208") + "　" + TextResource.localize("KAL004_73"); 
			default : return "";
		}
		
	}
	
	//日次単位の期間の算出する
	public CheckConditionTimeDto getDailyTime(CheckCondition c, int closureId, YearMonth yearMonth) {
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = null;
		Date endDate = null;
	
		ExtractionPeriodDaily extraction = (ExtractionPeriodDaily) c.getExtractPeriodList().get(0);
		
		CheckConditionPeriod period = this.getPeriodDaily(extraction, closureId, yearMonth, c);
		startDate = period.getStartDate();
		endDate = period.getEndDate();
		
		return new CheckConditionTimeDto(c.getAlarmCategory().value,
				EnumAdaptor.convertToValueName(c.getAlarmCategory()).getLocalizedName(), formatter.format(startDate),
				formatter.format(endDate), null, null);
	}
	
	/**
	 * 月次単位の期間を算出する
	 * @param c
	 * @param closureId
	 * @param yearMonth
	 * @return
	 */
	public CheckConditionTimeDto getMonthlyTime(CheckCondition c, int closureId, YearMonth yearMonth) {
		//DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		YearMonth startMonthly = yearMonth;
		YearMonth endMonthly = yearMonth;
		ExtractionPeriodMonth extractionPeriodMonth =  (ExtractionPeriodMonth) c.getExtractPeriodList().get(0);
		if(extractionPeriodMonth.getStartMonth().getSpecifyStartMonth().value == SpecifyStartMonth.DESIGNATE_CLOSE_START_MONTH.value) {
			if (c.isScheduleMonthly()) {
				startMonthly = yearMonth.addMonths(extractionPeriodMonth.getStartMonth().getStrMonthNo().get().getMonthNo());
			} else {
				startMonthly = yearMonth.addMonths((-1)*extractionPeriodMonth.getStartMonth().getStrMonthNo().get().getMonthNo());
			}
		}
		if(extractionPeriodMonth.getEndMonth().getSpecifyEndMonth().value == SpecifyEndMonth.SPECIFY_CLOSE_END_MONTH.value ) {
			if (c.isScheduleMonthly()) {
				endMonthly = yearMonth.addMonths(extractionPeriodMonth.getEndMonth().getEndMonthNo().get().getMonthNo());
			} else {
				endMonthly = yearMonth.addMonths((-1)*extractionPeriodMonth.getEndMonth().getEndMonthNo().get().getMonthNo());
			}
		}
		
		return new CheckConditionTimeDto(c.getAlarmCategory().value,
				EnumAdaptor.convertToValueName(c.getAlarmCategory()).getLocalizedName(), null,
				null, startMonthly.toString(), endMonthly.toString());
	}
	
	/**
	 * 月次単位の期間を算出する
	 * @param c
	 * @param closureId
	 * @param yearMonth
	 * @return
	 */
	public CheckConditionTimeDto getScheYearTime(CheckCondition c, int closureId, YearMonth yearMonth) {
		GeneralDate sysDate = GeneralDate.today();
		YearMonth startMonthly = yearMonth;
		YearMonth endMonthly = yearMonth;
		ExtractionPeriodMonth extractionPeriodMonth =  (ExtractionPeriodMonth) c.getExtractPeriodList().get(0);
		
		// 固定の月度を指定する
		if (SpecifyStartMonth.SPECIFY_FIXED_MOON_DEGREE == extractionPeriodMonth.getStartMonth().getSpecifyStartMonth()) {
			if (extractionPeriodMonth.getStartMonth().getFixedMonthly().isPresent()) {
				FixedMonthly fixedMonthly = extractionPeriodMonth.getStartMonth().getFixedMonthly().get();
				int fixMonNo = fixedMonthly.getDesignatedMonth();
				YearMonth targetYearMonth = YearMonth.of(sysDate.year(), fixMonNo);
						
				// 固定月度．年の種類　＝　本年の場合
				if (YearSpecifiedType.FISCAL_YEAR == fixedMonthly.getYearSpecifiedType()) {
					// 開始月の年　＝　システム日付．年
					startMonthly = targetYearMonth;
				} else {
					// 固定月度．年の種類　＝　本年度の場合
					// 開始月の年　＝　Input．処理月．年　　＃117102
					startMonthly = YearMonth.of(yearMonth.year(), fixMonNo);
				}
			}
		} else {
			// 締め開始月を指定する
			int startMonthNo = extractionPeriodMonth.getStartMonth().getStrMonthNo().get().getMonthNo();
			startMonthly = yearMonth.addMonths((-1) * startMonthNo);
		}
		
		// 開始から期間を指定する
		// 終了月　=　「パラメータ．処理月」　-　「月数指定．月数」とする 
		if(extractionPeriodMonth.getEndMonth().getSpecifyEndMonth().value == SpecifyEndMonth.SPECIFY_PERIOD_FROM_START_MONTH.value ) {
			endMonthly = startMonthly.addMonths(extractionPeriodMonth.getEndMonth().getExtractFromStartMonth().value - 1);
		} else {
			// 締め終了月を指定する
			int endMonthNo = extractionPeriodMonth.getEndMonth().getEndMonthNo().get().getMonthNo();
			endMonthly = yearMonth.addMonths(endMonthNo);
		}
		
		return new CheckConditionTimeDto(c.getAlarmCategory().value,
				EnumAdaptor.convertToValueName(c.getAlarmCategory()).getLocalizedName(), null,
				null, startMonthly.toString(), endMonthly.toString());
	}
	
	/**
	 * 日次単位の期間の算出する
	 * @param extraction
	 * @param closureId
	 * @param yearMonth
	 * @return
	 */
	private CheckConditionPeriod getPeriodDaily(ExtractionPeriodDaily extraction, int closureId, YearMonth yearMonth, CheckCondition c) {
		val require = ClosureService.createRequireM1(closureRepo, closureEmploymentRepo);
		Date startDate =null;
		Date endDate =null;
		YearMonth startMonthly = yearMonth;
		YearMonth endMonthly = yearMonth;
		// Calculate start date
		//ドメインモデル「チェック条件」．抽出期間から開始日の指定方法をチェックする
		if (extraction.getStartDate().getStartSpecify() == StartSpecify.DAYS) {
			Calendar calendar = Calendar.getInstance();
			if (extraction.getStartDate().getStrDays().get().getDayPrevious() == PreviousClassification.BEFORE)
				calendar.add(Calendar.DAY_OF_YEAR, -extraction.getStartDate().getStrDays().get().getDay().v());
			else
				calendar.add(Calendar.DAY_OF_YEAR, extraction.getStartDate().getStrDays().get().getDay().v());
				startDate = calendar.getTime();

		} else {
			YearMonth extractStartMon = null;
			// QA#115836
			if (c.isScheduleDaily()) {
				extractStartMon = startMonthly.addMonths(extraction.getStartDate().getStrMonth().get().getMonth());
			} else {
				extractStartMon = startMonthly.addMonths((-1)*extraction.getStartDate().getStrMonth().get().getMonth());
			}
			
			DatePeriod datePeriod = ClosureService.getClosurePeriod(require, closureId, extractStartMon);
			if(datePeriod == null) {
				return new CheckConditionPeriod(GeneralDate.ymd(startMonthly, 1).date(), GeneralDate.ymd(endMonthly.addMonths(1), 1).addDays(-1).date());
			}
			startDate = datePeriod.start().date();
		}

		// Calculate endDate
		//メインモデル「チェック条件」．抽出期間から終了日の指定方法をチェックする
		if (extraction.getEndDate().getEndSpecify() == EndSpecify.DAYS) {
			Calendar calendar = Calendar.getInstance();
			if (extraction.getEndDate().getEndDays().get().getDayPrevious() == PreviousClassification.BEFORE)
				calendar.add(Calendar.DAY_OF_YEAR, -extraction.getEndDate().getEndDays().get().getDay().v());
			else
				calendar.add(Calendar.DAY_OF_YEAR, extraction.getEndDate().getEndDays().get().getDay().v());

			endDate = calendar.getTime();
		} else {
			YearMonth extractEndMon = null;
			if (c.isScheduleDaily()) {
				extractEndMon = endMonthly.addMonths(extraction.getEndDate().getEndMonth().get().getMonth());
			} else {
				extractEndMon = endMonthly.addMonths((-1)*extraction.getEndDate().getEndMonth().get().getMonth());
			}
			
			DatePeriod datePeriod = ClosureService.getClosurePeriod(require, closureId, extractEndMon);
			if(datePeriod == null) {
				return new CheckConditionPeriod(GeneralDate.ymd(startMonthly, 1).date(), GeneralDate.ymd(endMonthly.addMonths(1), 1).addDays(-1).date());
			}
			endDate = datePeriod.end().date();
		}
		
		  
		return new CheckConditionPeriod(startDate, endDate);
		
	}
	
	//http://192.168.50.4:3000/issues/112749
	/**
	 * 周期単位の期間を算出する
	 * @param c
	 * @param closureId
	 * @param yearMonth
	 * @param companyId
	 * @return
	 */
	public CheckConditionTimeDto get4WeekTime(CheckCondition c, int closureId, YearMonth yearMonth, String companyId) {

		ExtractionPeriodUnit periodUnit = (ExtractionPeriodUnit) c.getExtractPeriodList().get(0);
		//ドメインモデル「休日の扱い」を取得
		Optional<TreatmentHoliday> optTreatHolidaySet = treatHolidayRepo.get(companyId);
		if(!optTreatHolidaySet.isPresent()) {
			return new CheckConditionTimeDto(c.getAlarmCategory().value,
					EnumAdaptor.convertToValueName(c.getAlarmCategory()).getLocalizedName(), null, null, null, null);
		}
		TreatmentHoliday treatHolidaySet = optTreatHolidaySet.get();
		//休日取得数と管理期間を取得する
		RequireImpl require = new RequireImpl(weekRuleManagementRepo);
		HolidayNumberManagement holidays = treatHolidaySet.getNumberHoliday(require, GeneralDate.today());
		
		switch (periodUnit.getSegmentationOfCycle()) {
		case TheNextCycle:
			GeneralDate date = holidays.getPeriod().end().addDays(1);
			holidays = treatHolidaySet.getNumberHoliday(require, date);
			break;
		case ThePreviousCycle:
			GeneralDate datePre = holidays.getPeriod().start().addDays(-1);
			holidays = treatHolidaySet.getNumberHoliday(require, datePre);
			break;
		default:
			break;
		}
		DatePeriod period = holidays.getPeriod();
		
		return new CheckConditionTimeDto(c.getAlarmCategory().value,
				EnumAdaptor.convertToValueName(c.getAlarmCategory()).getLocalizedName(),
				period.start().toString(),
				period.end().toString(), null, null);
	}
	@AllArgsConstructor
	private static class RequireImpl implements TreatmentHoliday.Require{
		private WeekRuleManagementRepo weekRuleManagementRepo;
		@Override
		public WeekRuleManagement find() {
			String companyID = AppContexts.user().companyId();
			return weekRuleManagementRepo.find(companyID).get();
		}
		
	}
}
