package nts.uk.screen.at.app.ksus01.a;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.app.find.application.gobackdirectly.WorkInformationDto;
import nts.uk.ctx.at.schedule.app.query.schedule.shift.management.shifttable.GetHolidaysByPeriod;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHoliday;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHolidayRepository;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.DesiredSubmissionStatus;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.GetStatusSubmissionWishes;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailabilityOfOneDay;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailabilityOfOneDayRepository;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.GetCombinationrAndWorkHolidayAtrService;
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
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSUS01_スケジュール参照（スマホ）.スケジュール参照.A：スケジュール参照.メニュー別OCD.対象期間の情報を取得する
 * @author dungbn
 *
 */
@Stateless
public class GetInforOnTargetPeriod {
	
	@Inject
	private WorkAvailabilityOfOneDayRepository workAvailabilityOfOneDayRepository;
	
	@Inject
	private WorkScheduleRepository workScheduleRepository;
	
	@Inject
	private ShiftMasterRepository shiftMasterRepo;
	
	@Inject
	private WorkTypeRepository workTypeRepo;
	
	@Inject
	private PublicHolidayRepository publicHolidayRepository;
	
	@Inject
	private ShiftMasterRepository shiftMasterRepository;
	
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	
	@Inject
	private BasicScheduleService basicScheduleService;
	
	@Inject
	private FixedWorkSettingRepository fixedWorkSettingRepository;
	
	@Inject
	private FlowWorkSettingRepository flowWorkSettingRepository;
	
	@Inject
	private FlexWorkSettingRepository flexWorkSettingRepository;
	
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;
	
	@Inject
	private GetHolidaysByPeriod getHolidaysByPeriod;
	
	final static String DATE_TIME_FORMAT = "yyyy/MM/dd";
	
	public InforOnTargetPeriodDto handle(InforOnTargetPeriodInput input) {
		
		String empId = AppContexts.user().employeeId();
		String companyId = AppContexts.user().companyId();
		GetStatusSubmissionWishesRequireImpl getStatusSubmissionWishesRequire = new GetStatusSubmissionWishesRequireImpl(companyId, workAvailabilityOfOneDayRepository, shiftMasterRepository, workTypeRepo, workTimeSettingRepository, basicScheduleService, fixedWorkSettingRepository, flowWorkSettingRepository, flexWorkSettingRepository, predetemineTimeSettingRepository);
		GetCombinationrAndWorkHolidayAtrRequireImpl getComAndWorkHolidayAtrRequire = new GetCombinationrAndWorkHolidayAtrRequireImpl(companyId, shiftMasterRepo, workTypeRepo, workTimeSettingRepository, basicScheduleService, fixedWorkSettingRepository, flowWorkSettingRepository, flexWorkSettingRepository, predetemineTimeSettingRepository);
		
		List<DesiredSubmissionStatusByDate> listDesiredSubmissionStatusByDate = new ArrayList<DesiredSubmissionStatusByDate>();
		List<WorkSchedule> listWorkSchedule = new ArrayList<WorkSchedule>();
		List<WorkInformation> lstWorkInformation = new ArrayList<WorkInformation>();
		List<String> listEmpId = new ArrayList<String>();
		
		// 1: [Optional<勤務希望期間>．isPresent]: <call>()
		if (input.getDesiredPeriodWork().getStart().length() > 0 || input.getDesiredPeriodWork().getEnd().length() > 0) {
			DatePeriod desiredPeriodWork = new DatePeriod(GeneralDate.fromString(input.getDesiredPeriodWork().getStart(), DATE_TIME_FORMAT), GeneralDate.fromString(input.getDesiredPeriodWork().getEnd(), DATE_TIME_FORMAT));
			for (GeneralDate currentStart = desiredPeriodWork.start();
					currentStart.beforeOrEquals(desiredPeriodWork.end());
					currentStart = currentStart.increase()) {
				// 1.1: 取得する(require, 社員ID, 年月日): 希望提出状況
				DesiredSubmissionStatus status = GetStatusSubmissionWishes.get(getStatusSubmissionWishesRequire, empId, currentStart);    
				DesiredSubmissionStatusByDate item = new DesiredSubmissionStatusByDate(currentStart.toString(), status.value);
				listDesiredSubmissionStatusByDate.add(item);
			}
		}
		
		// 2: [Optional<勤務予定期間>．isPresent]: <call>()
		if (input.getScheduledWorkingPeriod().getStart().length() > 0 || input.getScheduledWorkingPeriod().getEnd().length() > 0) {
			DatePeriod scheduledWorkingPeriod = new DatePeriod(GeneralDate.fromString(input.getScheduledWorkingPeriod().getStart(), DATE_TIME_FORMAT), GeneralDate.fromString(input.getScheduledWorkingPeriod().getEnd(), DATE_TIME_FORMAT));
			listEmpId.add(empId);
			
			// 2.1:  勤務予定リストを取得する(社員ID、年月日):  勤務予定
			listWorkSchedule = workScheduleRepository.getList(listEmpId, scheduledWorkingPeriod);
			lstWorkInformation = listWorkSchedule.stream().map(e -> e.getWorkInfo().getRecordInfo()).collect(Collectors.toList());
		}
		
		// 3: 勤務情報で取得する(require, 会社ID, 勤務情報リスト): Map<シフトマスタ, Optional<出勤休日区分>>
		Map<ShiftMaster, Optional<WorkStyle>> mapByWorkInfo = GetCombinationrAndWorkHolidayAtrService.getbyWorkInfo(getComAndWorkHolidayAtrRequire, companyId, lstWorkInformation);
		
		List<WorkScheduleDto> listWorkScheduleDto = new ArrayList<WorkScheduleDto>();
		
		for (WorkSchedule workSchedule : listWorkSchedule) {
			boolean check = false;
			for (Map.Entry<ShiftMaster, Optional<WorkStyle>> entry : mapByWorkInfo.entrySet()) {
				ShiftMaster k = entry.getKey();
				Optional<WorkStyle> v = entry.getValue();
			    List<AttendanceDto> listAttendaceDto = new ArrayList<AttendanceDto>();
				
    			Optional<TimeLeavingOfDailyAttd> optTimeLeaving = workSchedule.getOptTimeLeaving();
    			
    			if (optTimeLeaving.isPresent()) {
    				listAttendaceDto = optTimeLeaving.get().getTimeLeavingWorks().stream().map(e -> new AttendanceDto(e.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().getInDayTimeWithFormat(), 
    																												e.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().getInDayTimeWithFormat())).collect(Collectors.toList());
    			}
	    		
				// map by workTimeCode and workTypeCode
				if (k.getWorkTypeCode().v().equals(workSchedule.getWorkInfo().getRecordInfo().getWorkTypeCode().v())) {
					
					if (k.getWorkTimeCode() != null) {
						if (k.getWorkTimeCode().v().equals(workSchedule.getWorkInfo().getRecordInfo().getWorkTimeCode().v())) {
							check = true;
						}
					} else {
						if (workSchedule.getWorkInfo().getRecordInfo().getWorkTimeCode() == null) {
							check = true;
						}
					}
				}
				
				if (check) {
					
					WorkScheduleDto workScheduleDto = new WorkScheduleDto(workSchedule.getYmd().toString(), new ShiftMasterDto(k.getDisplayInfor().getName().v(), k.getDisplayInfor().getColorSmartPhone().v()),
													v.isPresent() ? v.get().value : null, listAttendaceDto, 
													WorkInformationDto.fromDomain(workSchedule.getWorkInfo().getRecordInfo()));
					listWorkScheduleDto.add(workScheduleDto);
					break;
				} 
			}
			if (!check) {
				WorkScheduleDto workScheduleDto = new WorkScheduleDto(workSchedule.getYmd().toString(), new ShiftMasterDto("", ""),
													null, Collections.emptyList(),
													WorkInformationDto.fromDomain(workSchedule.getWorkInfo().getRecordInfo()));
				listWorkScheduleDto.add(workScheduleDto);
			}
		}
		
		// 4: 取得する(対象期間): List<祝日>
		List<PublicHoliday> listPublicHoliday = getHolidaysByPeriod.get(
				new DatePeriod(GeneralDate.fromString(input.getTargetPeriod().getStart(), DATE_TIME_FORMAT),
								GeneralDate.fromString(input.getTargetPeriod().getEnd(), DATE_TIME_FORMAT)));
		
		return new InforOnTargetPeriodDto(listWorkScheduleDto, listDesiredSubmissionStatusByDate, listPublicHoliday.stream().map(e -> PublicHolidayDto.toDto(e)).collect(Collectors.toList()));
	}
	
	
	@AllArgsConstructor
	private static class GetCombinationrAndWorkHolidayAtrRequireImpl implements GetCombinationrAndWorkHolidayAtrService.Require {
		
		private String companyId;
		private ShiftMasterRepository shiftMasterRepo;
		private WorkTypeRepository workTypeRepo;
		private WorkTimeSettingRepository workTimeSettingRepository;
		private BasicScheduleService basicScheduleService;
		private FixedWorkSettingRepository fixedWorkSettingRepository;
		private FlowWorkSettingRepository flowWorkSettingRepository;
		private FlexWorkSettingRepository flexWorkSettingRepository;
		private PredetemineTimeSettingRepository predetemineTimeSettingRepository;
		
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
		public List<ShiftMaster> getByListEmp(String companyId, List<String> lstShiftMasterCd) {
			return shiftMasterRepo.getByListShiftMaterCd2(companyId, lstShiftMasterCd);
		}

		@Override
		public List<ShiftMaster> getByListWorkInfo(String companyId, List<WorkInformation> lstWorkInformation) {
			return shiftMasterRepo.get(companyId, lstWorkInformation);
		}
	}
	
	
	@AllArgsConstructor
	private static class GetStatusSubmissionWishesRequireImpl implements GetStatusSubmissionWishes.Require {

		private String companyId;
		private WorkAvailabilityOfOneDayRepository workAvailabilityOfOneDayRepository;
		private ShiftMasterRepository shiftMasterRepository;
		private WorkTypeRepository workTypeRepo;
		private WorkTimeSettingRepository workTimeSettingRepository;
		private BasicScheduleService basicScheduleService;
		private FixedWorkSettingRepository fixedWorkSettingRepository;
		private FlowWorkSettingRepository flowWorkSettingRepository;
		private FlexWorkSettingRepository flexWorkSettingRepository;
		private PredetemineTimeSettingRepository predetemineTimeSettingRepository;
		
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
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Optional<WorkAvailabilityOfOneDay> get(String employeeID, GeneralDate availabilityDate) {
			return workAvailabilityOfOneDayRepository.get(employeeID, availabilityDate);
		}
	}

	
}
