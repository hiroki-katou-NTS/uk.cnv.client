package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.GetLegalWorkTimeOfEmployeeService;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSetting;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonAndWeekStatutoryTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyFlexStatutoryLaborTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyStatutoryWorkingHours;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSet.LaborWorkTypeAttr;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeComRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeEmpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeShaRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeWkpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeComRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeEmpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeShaRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeWkpRepo;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisScheduleAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.LegalWorkTimeOfEmployeeDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.PeriodListPeriodDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.DisplayInWorkInfoInput;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author chungnt 
 * ScreenQuery: 予定・実績を取得する
 */

@Stateless
public class GetScheduleActualOfWorkInfo002 {
	@Inject
	private GetWorkActualOfWorkInfo002 getWorkRecord;

	@Inject
	private GetScheduleOfWorkInfo002 getScheduleOfWorkInfo002;
	
	@Inject
	private UsageUnitSettingRepository usageUnitSettingRepository;
	
	@Inject
	private AffWorkplaceAdapter affWorkplaceAdapter;
	
	@Inject
	private MonthlyWorkTimeSetRepo monthlyWorkTimeSet;
	
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;
	
	@Inject
	private EmploymentHisScheduleAdapter employmentHisScheduleAdapter;
	
	@Inject
	private RegularLaborTimeComRepo regularLaborTimeComRepo;

	@Inject
	private DeforLaborTimeComRepo deforLaborTimeComRepo;

	@Inject
	private RegularLaborTimeWkpRepo regularLaborTimeWkpRepo;

	@Inject
	private DeforLaborTimeWkpRepo deforLaborTimeWkpRepo;

	@Inject
	private RegularLaborTimeEmpRepo regularLaborTimeEmpRepo;

	@Inject
	private DeforLaborTimeEmpRepo deforLaborTimeEmpRepo;

	@Inject
	private RegularLaborTimeShaRepo regularLaborTimeShaRepo;

	@Inject
	private DeforLaborTimeShaRepo deforLaborTimeShaRepo;
	
	@Inject
	private KSU002Finder kSU002Finder;
	
	
	public List<WorkScheduleWorkInforDto> getDataScheduleAndAactualOfWorkInfo(DisplayInWorkInfoInput param) {
		
		PeriodListPeriodDto periodListPeriod = kSU002Finder.getPeriodList(param.getPeriod(), param.getStartWeekDate());
		
		param.startDate = periodListPeriod.datePeriod.start().toString("yyyy/MM/dd");
		param.endDate = periodListPeriod.datePeriod.end().toString("yyyy/MM/dd");
		
		// lay data Schedule
		List<WorkScheduleWorkInforDto> listDataSchedule = getScheduleOfWorkInfo002.getDataScheduleOfWorkInfo(param);

		if (param.actualData) {
			// lay data Daily
			List<WorkScheduleWorkInforDto> listDataDaily = getWorkRecord.getDataActualOfWorkInfo(param);
			
			for (WorkScheduleWorkInforDto ds : listDataSchedule) {
				listDataDaily.stream().filter(c -> c.employeeId.equals(ds.employeeId) && c.date.equals(ds.date)).findFirst()
					.ifPresent(c -> {
						WorkScheduleWorkInforDto.Achievement arch = WorkScheduleWorkInforDto
								.Achievement
								.builder()
								.workTypeCode(c.getWorkTypeCode())
								.workTypeName(c.getWorkTypeName())
								.workTimeCode(c.getWorkTimeCode())
								.workTimeName(c.getWorkTimeName())
								.startTime(c.getStartTime())
								.endTime(c.getEndTime())
								.build();
						
						ds.setAchievements(arch);
					});
			}
		}

		return listDataSchedule;
	}
	//ScreenQuery 期間に応じる基本情報を取得する  -> 3 取得する(@Require, 社員ID, 期間) Optional<社員の法定労働時間>
	public LegalWorkTimeOfEmployeeDto getlegalworkinghours(DisplayInWorkInfoInput param) {
		String companyId = AppContexts.user().companyId();
		LegalWorkTimeOfEmployeeDto result = new LegalWorkTimeOfEmployeeDto();
		if(param.listSid.isEmpty()) {
			return result;
		}
		result.setValue(GetLegalWorkTimeOfEmployeeService.get(new LegalWorkTimeRequireImpl(companyId), param.listSid.get(0), param.getPeriod()));
		return result;
	}

	@RequiredArgsConstructor
	private class LegalWorkTimeRequireImpl implements GetLegalWorkTimeOfEmployeeService.Require {
		
		private final String companyId;
		
		@Override
		public Optional<WorkingConditionItem> getHistoryItemBySidAndBaseDate(String sid, GeneralDate baseDate) {
			return workingConditionItemRepository.getBySidAndStandardDate(sid, baseDate);
		}

		@Override
		public List<EmploymentPeriodImported> getEmploymentHistories(String sid, DatePeriod datePeriod) {
			List<String> listEmpId = new ArrayList<String>();
			listEmpId.add(sid);
			return employmentHisScheduleAdapter.getEmploymentPeriod(listEmpId, datePeriod);
		}

		@Override
		public MonthlyFlexStatutoryLaborTime flexMonAndWeekStatutoryTime(YearMonth ym, String employmentCd,String employeeId, GeneralDate baseDate) {
			return MonthlyStatutoryWorkingHours.flexMonAndWeekStatutoryTime(new Require1(), new CacheCarrier(), companyId, employmentCd, employeeId, baseDate, ym);
		}

		@Override
		public Optional<MonAndWeekStatutoryTime> monAndWeekStatutoryTime(YearMonth ym, String employmentCd, String employeeId, GeneralDate baseDate, WorkingSystem workingSystem) {
			return MonthlyStatutoryWorkingHours.monAndWeekStatutoryTime(new Require2(), new CacheCarrier(), companyId, employmentCd, employeeId, baseDate, ym, workingSystem);
		}

	}
	
	@RequiredArgsConstructor
	private class Require1 implements MonthlyStatutoryWorkingHours.RequireM1 {
		
		@Override
		public Optional<UsageUnitSetting> usageUnitSetting(String companyId) {
			return usageUnitSettingRepository.findByCompany(companyId);
		}
		@Override
		public List<String> getCanUseWorkplaceForEmp(CacheCarrier cacheCarrier, String companyId, String employeeId,
				GeneralDate baseDate) {
			return affWorkplaceAdapter.findAffiliatedWorkPlaceIdsToRootRequire(cacheCarrier, companyId, employeeId, baseDate);
		}
		@Override
		public Optional<MonthlyWorkTimeSetSha> monthlyWorkTimeSetSha(String cid, String sid,
				LaborWorkTypeAttr laborAttr, YearMonth ym) {
			return monthlyWorkTimeSet.findEmployee(cid, sid, laborAttr, ym);
		}
		@Override
		public Optional<MonthlyWorkTimeSetEmp> monthlyWorkTimeSetEmp(String cid, String empCode,
				LaborWorkTypeAttr laborAttr, YearMonth ym) {
			return monthlyWorkTimeSet.findEmployment(cid, empCode, laborAttr, ym);
		}
		@Override
		public Optional<MonthlyWorkTimeSetCom> monthlyWorkTimeSetCom(String cid, LaborWorkTypeAttr laborAttr,
				YearMonth ym) {
			return monthlyWorkTimeSet.findCompany(cid, laborAttr, ym);
		}
		@Override
		public Optional<MonthlyWorkTimeSetWkp> monthlyWorkTimeSetWkp(String cid, String workplaceId,
				LaborWorkTypeAttr laborAttr, YearMonth ym) {
			return monthlyWorkTimeSet.findWorkplace(cid, workplaceId, laborAttr, ym);
		}
		
	}
	
	@RequiredArgsConstructor
	private class Require2 implements MonthlyStatutoryWorkingHours.RequireM4 {
		
		@Override
		public List<String> getCanUseWorkplaceForEmp(CacheCarrier cacheCarrier, String companyId, String employeeId,
				GeneralDate baseDate) {
			return affWorkplaceAdapter.findAffiliatedWorkPlaceIdsToRootRequire(cacheCarrier, companyId, employeeId, baseDate);
		}
		@Override
		public Optional<MonthlyWorkTimeSetSha> monthlyWorkTimeSetSha(String cid, String sid,
				LaborWorkTypeAttr laborAttr, YearMonth ym) {
			return monthlyWorkTimeSet.findEmployee(cid, sid, laborAttr, ym);
		}
		@Override
		public Optional<MonthlyWorkTimeSetEmp> monthlyWorkTimeSetEmp(String cid, String empCode,
				LaborWorkTypeAttr laborAttr, YearMonth ym) {
			return monthlyWorkTimeSet.findEmployment(cid, empCode, laborAttr, ym);
		}
		@Override
		public Optional<MonthlyWorkTimeSetCom> monthlyWorkTimeSetCom(String cid, LaborWorkTypeAttr laborAttr,
				YearMonth ym) {
			return monthlyWorkTimeSet.findCompany(cid, laborAttr, ym);
		}
		@Override
		public Optional<MonthlyWorkTimeSetWkp> monthlyWorkTimeSetWkp(String cid, String workplaceId,
				LaborWorkTypeAttr laborAttr, YearMonth ym) {
			return monthlyWorkTimeSet.findWorkplace(cid, workplaceId, laborAttr, ym);
		}

		@Override
		public Optional<UsageUnitSetting> usageUnitSetting(String companyId) {
			return usageUnitSettingRepository.findByCompany(companyId);
		}

		@Override
		public Optional<RegularLaborTimeCom> regularLaborTimeByCompany(String companyId) {
			return regularLaborTimeComRepo.find(companyId);
		}

		@Override
		public Optional<DeforLaborTimeCom> deforLaborTimeByCompany(String companyId) {
			return deforLaborTimeComRepo.find(companyId);
		}

		@Override
		public Optional<RegularLaborTimeWkp> regularLaborTimeByWorkplace(String cid, String wkpId) {
			return regularLaborTimeWkpRepo.find(cid, wkpId);
		}

		@Override
		public Optional<DeforLaborTimeWkp> deforLaborTimeByWorkplace(String cid, String wkpId) {
			return deforLaborTimeWkpRepo.find(cid, wkpId);
		}

		@Override
		public Optional<RegularLaborTimeEmp> regularLaborTimeByEmployment(String cid, String employmentCode) {
			return regularLaborTimeEmpRepo.findById(cid, employmentCode);
		}

		@Override
		public Optional<DeforLaborTimeEmp> deforLaborTimeByEmployment(String cid, String employmentCode) {
			return deforLaborTimeEmpRepo.find(cid, employmentCode);
		}

		@Override
		public Optional<RegularLaborTimeSha> regularLaborTimeByEmployee(String Cid, String EmpId) {
			return regularLaborTimeShaRepo.find(Cid, EmpId);
		}

		@Override
		public Optional<DeforLaborTimeSha> deforLaborTimeByEmployee(String cid, String empId) {
			return deforLaborTimeShaRepo.find(cid, empId);
		}
	}
}
