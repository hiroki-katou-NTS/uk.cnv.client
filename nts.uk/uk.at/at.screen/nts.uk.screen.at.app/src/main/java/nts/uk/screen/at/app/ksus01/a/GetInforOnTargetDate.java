package nts.uk.screen.at.app.ksus01.a;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.RegulationInfoEmployeeAdapter;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.GetWorkTogetherEmpOnDayBySpecEmpService;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.DesiredSubmissionStatus;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailabilityDisplayInfoOfOneDay;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailabilityOfOneDay;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailabilityOfOneDayRepository;
import nts.uk.ctx.at.shared.dom.WorkInfoAndTimeZone;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.EmployeeSearchCallSystemType;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.RegulationInfoEmpQuery;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpAffiliationInforAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterCode;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.query.model.employee.EmployeeInformation;
import nts.uk.query.model.employee.EmployeeInformationQuery;
import nts.uk.query.model.employee.EmployeeInformationRepository;
import nts.uk.query.pub.employee.EmployeeSearchQueryDto;
import nts.uk.query.pub.employee.RegulationInfoEmployeeExport;
import nts.uk.query.pub.employee.RegulationInfoEmployeePub;
import nts.uk.shr.com.context.AppContexts;

/**
 *
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSUS01_スケジュール参照（スマホ）.スケジュール参照.A：スケジュール参照.メニュー別OCD.対象日の情報をを取得する
 * @author dungbn
 *
 */
@Stateless
public class GetInforOnTargetDate {

	@Inject
	private WorkScheduleRepository workScheduleRepository;

	@Inject
	private WorkplaceGroupAdapter workplaceGroupAdapter;

	@Inject
	private RegulationInfoEmployeeAdapter regulInfoEmpAdap;

	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private BasicScheduleService basicScheduleService;

	@Inject
    private RegulationInfoEmployeePub regulInfoEmpPub;

	@Inject
	private EmpAffiliationInforAdapter empAffiliationInforAdapter;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Inject
	private FixedWorkSettingRepository fixedWorkSettingRepository;

	@Inject
	private FlowWorkSettingRepository flowWorkSettingRepository;

	@Inject
	private FlexWorkSettingRepository flexWorkSettingRepository;

	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;

	@Inject
	private EmployeeInformationRepository employeeInformationRepository;

	@Inject
	private WorkAvailabilityOfOneDayRepository workAvailabilityOfOneDayRepository;

	@Inject
	private ShiftMasterRepository shiftMasterRepository;

    final static String DATE_TIME_FORMAT = "yyyy/MM/dd";

    public InforOnTargetDateDto handle(int desiredSubmissionStatus, int workHolidayAtr, String targetDate) {

    	String companyId = AppContexts.user().companyId();
    	String sid = AppContexts.user().employeeId();
    	RequireImpl require = new RequireImpl(companyId, workScheduleRepository, workplaceGroupAdapter, regulInfoEmpAdap, workTypeRepo, basicScheduleService, regulInfoEmpPub, empAffiliationInforAdapter, workTimeSettingRepository
    			, fixedWorkSettingRepository, flowWorkSettingRepository, flexWorkSettingRepository, predetemineTimeSettingRepository);

    	RequireWorkAvailabilityImpl requireWorkAvailability = new RequireWorkAvailabilityImpl(companyId, workTypeRepo, workTimeSettingRepository, basicScheduleService, fixedWorkSettingRepository
    			, flowWorkSettingRepository, flexWorkSettingRepository, predetemineTimeSettingRepository, shiftMasterRepository);

    	GeneralDate baseDate = GeneralDate.fromString(targetDate, DATE_TIME_FORMAT);

    	// dto
    	List<WorkInforAndTimeZoneByShiftMasterDto> listWorkInforAndTimeZone = new ArrayList<WorkInforAndTimeZoneByShiftMasterDto>();
    	List<AttendanceDto> listAttendanceDto = new ArrayList<AttendanceDto>();
    	List<String> listBusinessName = new ArrayList<String>();
    	String memo = "";
    	Integer type = null;


    	// [出勤休日区分＜＞休日]
    	if (workHolidayAtr != WorkStyle.ONE_DAY_REST.value) {

    		// [出勤休日区分＜＞休日]: 取得する（require, 社員ID, 基準日）：　List<社員ID>
    		List<String> listEmpId = GetWorkTogetherEmpOnDayBySpecEmpService.get(require, sid, baseDate);

    		// 2: [出勤休日区分＜＞休日]: <<create>>
    		EmployeeInformationQuery params =  EmployeeInformationQuery.builder()
								    			.employeeIds(listEmpId)
								    			.referenceDate(baseDate)
								    			.toGetWorkplace(false).toGetDepartment(false)
								    			.toGetPosition(false).toGetEmployment(false)
								    			.toGetClassification(false).toGetEmploymentCls(false).build();

    		// 3: [出勤休日区分＜＞休日]: <call>(): List＜クエリモデル「社員情報」＞
    		List<EmployeeInformation> listEmployeeInformation = employeeInformationRepository.find(params);
    		Comparator<EmployeeInformation> compareByCodes =
    				(EmployeeInformation emp1, EmployeeInformation emp2)
    				-> emp1.getEmployeeCode().compareTo(emp2.getEmployeeCode());
    		Collections.sort(listEmployeeInformation, compareByCodes);

    		listBusinessName = listEmployeeInformation.stream().map(e -> e.getBusinessName()).collect(Collectors.toList());

    		// 4: [出勤休日区分＜＞休日]:　取得する(社員ID, 年月日): 勤務予定    <<get>>
    		Optional<WorkSchedule> workSchedule = workScheduleRepository.get(sid, baseDate);

    		if (workSchedule.isPresent()) {
    			Optional<TimeLeavingOfDailyAttd> optTimeLeaving = workSchedule.get().getOptTimeLeaving();

    			if (optTimeLeaving.isPresent()) {
    				listAttendanceDto = optTimeLeaving.get().getTimeLeavingWorks().stream().map(e -> new AttendanceDto(e.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().getInDayTimeWithFormat(),
    																												e.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().getInDayTimeWithFormat())).collect(Collectors.toList());
    			}
    		}
    	}

    	// [希望提出状態<>希望なし]
    	if (desiredSubmissionStatus != DesiredSubmissionStatus.NO_HOPE.value) {

    		// 5: [希望提出状態<>希望なし]: get(社員ID,対象日): Optional<一日分の勤務希望>
    		Optional<WorkAvailabilityOfOneDay> workAvailabilityOfOneDay = workAvailabilityOfOneDayRepository.get(sid, baseDate);

    		if (workAvailabilityOfOneDay.isPresent()) {

    			memo = workAvailabilityOfOneDay.get().getMemo().v();

    			// 5.1: [希望提出状態<>希望なし]: 表示情報を返す(require): 一日分の勤務希望の表示情報  <<get>>
    			WorkAvailabilityDisplayInfoOfOneDay workAvailabilityDisplayInfoOfOneDay = workAvailabilityOfOneDay.get().getDisplayInformation(requireWorkAvailability);

    			type = workAvailabilityDisplayInfoOfOneDay.getDisplayInfo().getMethod().value;

    			List<String> shiftMaterCodes = workAvailabilityDisplayInfoOfOneDay.getDisplayInfo().getShiftList().entrySet().stream().map(e -> e.getKey().v()).collect(Collectors.toList());
    			List<ShiftMaster> listShiftMaster = new ArrayList<ShiftMaster>();

    			// 6: [一日分の勤務希望の表示情報.表示情報.種類　＝＝シフト]:  get(会社ID, List<シフトマスタコード>): List<シフトマスタ>
    			if (type == 1) {
    				listShiftMaster = shiftMasterRepository.getByListShiftMaterCd2(companyId, shiftMaterCodes);
    			}


    			listShiftMaster.forEach(e -> {

    				Optional<WorkInfoAndTimeZone> workInfoAndTimeZone = e.getWorkInfoAndTimeZone(require);

    				Optional<WorkStyle> workStyle = e.getWorkStyle(require);

    				if (workInfoAndTimeZone.isPresent()) {

    					// 6.1.1: 勤務情報と補正済み所定時間帯を取得する(require): Optional<勤務情報と補正済み所定時間帯>
    					List<TimeZoneDto> timezones = workInfoAndTimeZone.get().getTimeZones().stream().map(x -> new TimeZoneDto(x.getStart().getInDayTimeWithFormat(), x.getEnd().getInDayTimeWithFormat())).collect(Collectors.toList());

    					// 6.1.2: 出勤・休日系の判定(require): Optional<出勤休日区分>

    					if (workStyle.isPresent()) {
    						WorkInforAndTimeZoneByShiftMasterDto workInfoAndTimeZoneDto = new WorkInforAndTimeZoneByShiftMasterDto(e.getDisplayInfor().getName().v(), timezones, workStyle.get().value, e.getDisplayInfor().getColorSmartPhone().v());
        					listWorkInforAndTimeZone.add(workInfoAndTimeZoneDto);
    					}
    				}
    			});
    		}
    	}

    	return new InforOnTargetDateDto(listBusinessName, listWorkInforAndTimeZone, memo, listAttendanceDto, type);

	}

    @AllArgsConstructor
    private static class RequireWorkAvailabilityImpl implements WorkAvailabilityOfOneDay.Require {

    	private String companyId;
    	private WorkTypeRepository workTypeRepo;
    	private WorkTimeSettingRepository workTimeSettingRepository;
    	private BasicScheduleService basicScheduleService;
    	private FixedWorkSettingRepository fixedWorkSettingRepository;
    	private FlowWorkSettingRepository flowWorkSettingRepository;
    	private FlexWorkSettingRepository flexWorkSettingRepository;
    	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;
    	private ShiftMasterRepository shiftMasterRepository;

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}

		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			return basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
			Optional<FixedWorkSetting> fixedWorkSetting = fixedWorkSettingRepository.findByKey(companyId, code.v());

			if (fixedWorkSetting.isPresent()) {
				return fixedWorkSetting.get();
			}

			return new FixedWorkSetting();
		}

		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
			Optional<FlowWorkSetting> flowWorkSetting = flowWorkSettingRepository.find(companyId, code.v());

			if (flowWorkSetting.isPresent()) {
				return flowWorkSetting.get();
			}

			return new FlowWorkSetting();
		}

		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
			Optional<FlexWorkSetting> flexWorkSetting = flexWorkSettingRepository.find(companyId, code.v());

			if (flexWorkSetting.isPresent()) {
				return flexWorkSetting.get();
			}

			return new FlexWorkSetting();
		}

		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			Optional<PredetemineTimeSetting> predetemineTimeSetting = predetemineTimeSettingRepository.findByWorkTimeCode(companyId, wktmCd.v());

			if (predetemineTimeSetting.isPresent()) {
				return predetemineTimeSetting.get();
			}

			return new PredetemineTimeSetting();
		}

		@Override
		public Optional<ShiftMaster> getShiftMasterByWorkInformation(WorkTypeCode workTypeCode,
				WorkTimeCode workTimeCode) {
			return shiftMasterRepository.getByWorkTypeAndWorkTime(companyId, workTypeCode.v(), workTimeCode.v());
		}

		@Override
		public List<ShiftMaster> getShiftMaster(List<ShiftMasterCode> shiftMasterCodeList) {
			return shiftMasterRepository.getByListShiftMaterCd2(companyId, shiftMasterCodeList.stream().map(e -> e.v()).collect(Collectors.toList()));
		}

		@Override
		public boolean shiftMasterIsExist(ShiftMasterCode shiftMasterCode) {
			return false;
		}
    }

	@AllArgsConstructor
	private static class RequireImpl implements GetWorkTogetherEmpOnDayBySpecEmpService.Require {

		private String companyId;
		private WorkScheduleRepository workScheduleRepository;
		private WorkplaceGroupAdapter workplaceGroupAdapter;
		private RegulationInfoEmployeeAdapter regulInfoEmpAdap;
		private WorkTypeRepository workTypeRepo;
		private BasicScheduleService basicScheduleService;
        private RegulationInfoEmployeePub regulInfoEmpPub;
		private EmpAffiliationInforAdapter empAffiliationInforAdapter;
		private WorkTimeSettingRepository workTimeSettingRepository;
		private FixedWorkSettingRepository fixedWorkSettingRepository;
		private FlowWorkSettingRepository flowWorkSettingRepository;
		private FlexWorkSettingRepository flexWorkSettingRepository;
		private PredetemineTimeSettingRepository predetemineTimeSettingRepository;


		@Override
		public List<EmpOrganizationImport> getEmpOrganization(GeneralDate baseDate, List<String> lstEmpId) {
			return empAffiliationInforAdapter.getEmpOrganization(baseDate, lstEmpId);
		}

		@Override
		public List<String> getEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period, String workplaceGroupID) {
			List<String> data = workplaceGroupAdapter.getReferableEmp(empId, date, period, workplaceGroupID);
            return data;
		}

		@Override
		public List<String> sortEmployee(List<String> lstmployeeId, EmployeeSearchCallSystemType sysAtr, Integer sortOrderNo,
				GeneralDate referenceDate, Integer nameType) {

			GeneralDateTime time = GeneralDateTime.fromString(referenceDate.toString() + " " + "00:00:00", "yyyy/MM/dd HH:mm:ss");

			List<String> data = regulInfoEmpAdap.sortEmployee(AppContexts.user().companyId(), lstmployeeId, sysAtr.value, sortOrderNo, nameType, time);
            return data;
		}

		@Override
		public String getRoleID() {
            return AppContexts.user().roles().forAttendance();
		}

		@Override
		public List<String> searchEmployee(RegulationInfoEmpQuery q, String roleId) {
			EmployeeSearchQueryDto query = EmployeeSearchQueryDto.builder()
                    .baseDate(GeneralDateTime.fromString(q.getBaseDate().toString() + " " + "00:00:00", "yyyy/MM/dd HH:mm:ss"))
                    .referenceRange(q.getReferenceRange().value)
                    .systemType(q.getSystemType().value)
                    .filterByWorkplace(q.getFilterByWorkplace())
                    .workplaceCodes(q.getWorkplaceIds())
                    .filterByEmployment(false)
                    .employmentCodes(new ArrayList<String>())
                    .filterByDepartment(false)
                    .departmentCodes(new ArrayList<String>())
                    .filterByClassification(false)
                    .classificationCodes(new ArrayList<String>())
                    .filterByJobTitle(false)
                    .jobTitleCodes(new ArrayList<String>())
                    .filterByWorktype(false)
                    .worktypeCodes(new ArrayList<String>())
                    .filterByClosure(false)
                    .closureIds(new ArrayList<Integer>())
                    .periodStart( GeneralDateTime.fromString(q.getPeriodStart() + " 00:00", "yyyy/MM/dd HH:mm") )
                    .periodEnd( GeneralDateTime.fromString(q.getPeriodEnd() + " 00:00", "yyyy/MM/dd HH:mm") )
                    .includeIncumbents(true)
                    .includeWorkersOnLeave(true)
                    .includeOccupancy(true)
                    .includeRetirees(false)
                    .includeAreOnLoan(false)
                    .includeGoingOnLoan(false)
                    .retireStart(GeneralDateTime.now())
                    .retireEnd(GeneralDateTime.now())
                    .sortOrderNo(null)
                    .nameType(null)

                    .build();
            List<RegulationInfoEmployeeExport> data = regulInfoEmpPub.find(query);
            List<String> resultList = data.stream().map(item -> item.getEmployeeId())
                    .collect(Collectors.toList());
            return resultList;
		}

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}

		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			return basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
			Optional<FixedWorkSetting> fixedWorkSetting = fixedWorkSettingRepository.findByKey(companyId, code.v());

			if (fixedWorkSetting.isPresent()) {
				return fixedWorkSetting.get();
			}

			return new FixedWorkSetting();
		}

		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
			Optional<FlowWorkSetting> flowWorkSetting = flowWorkSettingRepository.find(companyId, code.v());

			if (flowWorkSetting.isPresent()) {
				return flowWorkSetting.get();
			}

			return new FlowWorkSetting();
		}

		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
			Optional<FlexWorkSetting> flexWorkSetting = flexWorkSettingRepository.find(companyId, code.v());

			if (flexWorkSetting.isPresent()) {
				return flexWorkSetting.get();
			}

			return new FlexWorkSetting();
		}

		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			Optional<PredetemineTimeSetting> predetemineTimeSetting = predetemineTimeSettingRepository.findByWorkTimeCode(companyId, wktmCd.v());

			if (predetemineTimeSetting.isPresent()) {
				return predetemineTimeSetting.get();
			}

			return new PredetemineTimeSetting();
		}

		@Override
		public List<WorkSchedule> getWorkSchedule(List<String> sids, GeneralDate baseDate) {
			return workScheduleRepository.getList(sids, DatePeriod.oneDay(baseDate));
		}

		@Override
		public List<String> getAllEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period) {
			// don't have to implement it
			return null;
		}

	}
}
