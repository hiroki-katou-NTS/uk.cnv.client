package nts.uk.screen.at.app.ksus01.b;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.common.DailyAttendanceGettingService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.EstimatedSalary;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.EstimatedSalaryAggregationService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.WorkingTimeCounterService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompany;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompanyRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmployment;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmploymentRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSetting;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSettingRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountRepository;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AttendanceTimesForAggregation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisScheduleAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSUS01_スケジュール参照（スマホ）.スケジュール参照.B:補足情報.メニュー別OCD.初期起動時
 * @author huylq
 *
 */
@Stateless
public class GetInforInitialStartupKsus01B {
	
	@Inject
	private EmploymentHisScheduleAdapter employmentHisScheduleAdapter;
	@Inject
	private WorkScheduleRepository workScheduleRepository;
	@Inject
	private DailyRecordShareFinder dailyRecordShareFinder;
	@Inject
	private CriterionAmountUsageSettingRepository criterionAmountUsageSettingRepository;
	@Inject
	private CriterionAmountForCompanyRepository criterionAmountForCompanyRepository;
	@Inject
	private CriterionAmountForEmploymentRepository criterionAmountForEmploymentRepository;
	@Inject
	private HandlingOfCriterionAmountRepository handlingOfCriterionAmountRepository;
	
	final static String DATE_TIME_FORMAT = "yyyy/MM/dd";

	public InforInitialDto handle(InforInitialInput input) {
		String employeeId = AppContexts.user().employeeId();
		String companyId = AppContexts.user().companyId();
		
		DatePeriod targetPeriod = new DatePeriod(GeneralDate.fromString(input.getStartDate(), DATE_TIME_FORMAT), 
				GeneralDate.fromString(input.getEndDate(), DATE_TIME_FORMAT));
		YearMonth baseYM = new YearMonth(input.getBaseYM());
		DateInMonth closingDay = DateInMonth.of(input.getClosingDay()); //対象期間.終了日
		
		EstimatedSalaryAggregationServiceRequireImpl estimatedSalaryAggregationServiceRequire = new EstimatedSalaryAggregationServiceRequireImpl(companyId, workScheduleRepository, dailyRecordShareFinder, criterionAmountUsageSettingRepository, criterionAmountForCompanyRepository, criterionAmountForEmploymentRepository, employmentHisScheduleAdapter, handlingOfCriterionAmountRepository);
		DailyAttendanceGettingServiceRequireImpl dailyAttendanceGettingServiceRequire = new DailyAttendanceGettingServiceRequireImpl(workScheduleRepository, dailyRecordShareFinder);
		
		List<EmployeeId> empIds = new ArrayList<EmployeeId>();
		EmployeeId empId = new EmployeeId(employeeId);
		empIds.add(empId);
		
		//	1: 予定を取得する(Require, List<社員ID>, 基準年月): List<日別勤怠(Work)>
		List<IntegrationOfDaily> integrationOfDailyList = DailyAttendanceGettingService.getSchedule(
				dailyAttendanceGettingServiceRequire, empIds, targetPeriod);
		
		//	2: 月間想定給与額を集計する(基準年月, 対象期間.終了日, List<日別勤怠（Work）>): Map<社員ID, 想定給与額>
		Map<EmployeeId, EstimatedSalary> estimatedSalaryMonthlyMap = EstimatedSalaryAggregationService.aggregateByMonthly(
				estimatedSalaryAggregationServiceRequire, 
				baseYM, 
				closingDay, 
				integrationOfDailyList);
		
		//	3: 累計想定給与額を集計する(require, 基準年月, 対象期間.終了日, 社員IDリスト): Map<社員ID, 想定給与額>
		
		Map<EmployeeId, EstimatedSalary> estimatedSalaryCumulativeMap = EstimatedSalaryAggregationService.aggregateByCumulatively(
				estimatedSalaryAggregationServiceRequire, 
				baseYM, 
				closingDay, 
				empIds);
		
		//	4: 集計する(日別勤怠リスト): Map<社員ID, Map<集計対象の勤怠時間, BigDecimal>>
		Map<EmployeeId, Map<AttendanceTimesForAggregation, BigDecimal>> totalWorkingTimeMap = WorkingTimeCounterService.get(integrationOfDailyList);

		AttendanceTimesForAggregation enum0 = null;
		for(AttendanceTimesForAggregation enumConstant: AttendanceTimesForAggregation.class.getEnumConstants()) {
			if(enumConstant.getValue() == 0) {
				enum0 = enumConstant;
			}
		}
		return new InforInitialDto(
				totalWorkingTimeMap != null && totalWorkingTimeMap.get(empId) != null ? 
						totalWorkingTimeMap.get(empId).get(enum0) : new BigDecimal(0),
				estimatedSalaryMonthlyMap != null && estimatedSalaryMonthlyMap.get(empId) != null ? 
						estimatedSalaryMonthlyMap.get(empId).getSalary() : new BigDecimal(0), 
				estimatedSalaryCumulativeMap != null && estimatedSalaryCumulativeMap.get(empId) != null ? 
						estimatedSalaryCumulativeMap.get(empId).getSalary() : new BigDecimal(0));
	}
	
	@AllArgsConstructor
	private static class DailyAttendanceGettingServiceRequireImpl implements DailyAttendanceGettingService.Require {
		
		private WorkScheduleRepository workScheduleRepository;
		private DailyRecordShareFinder dailyRecordShareFinder;

		@Override
		public List<IntegrationOfDaily> getSchduleList(List<EmployeeId> empIds, DatePeriod period) {
			return workScheduleRepository
					.getList(empIds.stream().map(el -> el.v()).collect(Collectors.toList()), period)
					.stream().map(el -> el.convertToIntegrationOfDaily()).collect(Collectors.toList());
		}

		@Override
		public List<IntegrationOfDaily> getRecordList(List<EmployeeId> empIds, DatePeriod period) {
			return dailyRecordShareFinder.findByListEmployeeId(empIds.stream().map(el -> el.v()).collect(Collectors.toList()), period);
		}

	}
	
	@AllArgsConstructor
	private static class EstimatedSalaryAggregationServiceRequireImpl implements EstimatedSalaryAggregationService.Require {
		
		private String companyId;
		private WorkScheduleRepository workScheduleRepository;
		private DailyRecordShareFinder dailyRecordShareFinder;
		private CriterionAmountUsageSettingRepository criterionAmountUsageSettingRepository;
		private CriterionAmountForCompanyRepository criterionAmountForCompanyRepository;
		private CriterionAmountForEmploymentRepository criterionAmountForEmploymentRepository;
		private EmploymentHisScheduleAdapter employmentHisScheduleAdapter;
		private HandlingOfCriterionAmountRepository handlingOfCriterionAmountRepository;
		
		@Override
		public List<IntegrationOfDaily> getSchduleList(List<EmployeeId> empIds, DatePeriod period) {
			return workScheduleRepository
					.getList(empIds.stream().map(el -> el.v()).collect(Collectors.toList()), period)
					.stream().map(el -> el.convertToIntegrationOfDaily()).collect(Collectors.toList());
		}

		@Override
		public List<IntegrationOfDaily> getRecordList(List<EmployeeId> empIds, DatePeriod period) {
			return dailyRecordShareFinder.findByListEmployeeId(empIds.stream().map(el -> el.v()).collect(Collectors.toList()), period);
		}

		@Override
		public Optional<CriterionAmountUsageSetting> getUsageSetting() {
			return criterionAmountUsageSettingRepository.get(companyId);
		}

		@Override
		public Optional<CriterionAmountForCompany> getCriterionAmountForCompany() {
			return criterionAmountForCompanyRepository.get(companyId);
		}

		@Override
		public Optional<CriterionAmountForEmployment> getCriterionAmountForEmployment(EmploymentCode employmentCode) {
			return criterionAmountForEmploymentRepository.get(companyId, employmentCode);
		}

		@Override
		public Optional<EmploymentPeriodImported> getEmploymentHistory(EmployeeId empId, GeneralDate date) {
			val result = employmentHisScheduleAdapter.getEmploymentPeriod(Arrays.asList(empId.v()), new DatePeriod(date, date));
			if (result.isEmpty())
				return Optional.empty();
			return Optional.of(result.get(0));
		}

		@Override
		public Optional<HandlingOfCriterionAmount> getHandling() {
			return handlingOfCriterionAmountRepository.get(companyId);
		}
		
	}
}
