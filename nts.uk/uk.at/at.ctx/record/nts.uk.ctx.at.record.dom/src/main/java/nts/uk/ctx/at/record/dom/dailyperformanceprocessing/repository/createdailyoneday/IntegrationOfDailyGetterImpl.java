package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.actualworkinghours.repository.AttendanceTimeRepository;
import nts.uk.ctx.at.record.dom.adapter.workschedule.snapshot.DailySnapshotWorkAdapter;
import nts.uk.ctx.at.record.dom.adapter.workschedule.snapshot.DailySnapshotWorkImport;
import nts.uk.ctx.at.record.dom.affiliationinformation.AffiliationInforOfDailyPerfor;
import nts.uk.ctx.at.record.dom.affiliationinformation.repository.AffiliationInforOfDailyPerforRepository;
import nts.uk.ctx.at.record.dom.breakorgoout.BreakTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.breakorgoout.OutingTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.breakorgoout.repository.BreakTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.breakorgoout.repository.OutingTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.calculationattribute.repo.CalAttrOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.AttendanceLeavingGateOfDaily;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.PCLogOnInfoOfDaily;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.repo.AttendanceLeavingGateOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.repo.PCLogOnInfoOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDaily;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.remarks.RemarksOfDailyPerform;
import nts.uk.ctx.at.record.dom.daily.remarks.RemarksOfDailyPerformRepo;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.raisesalarytime.SpecificDateAttrOfDailyPerfor;
import nts.uk.ctx.at.record.dom.raisesalarytime.repo.SpecificDateAttrOfDailyPerforRepo;
import nts.uk.ctx.at.record.dom.shorttimework.ShortTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.shorttimework.repo.ShortTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.dom.worktime.TemporaryTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.repository.TemporaryTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.worktime.repository.TimeLeavingOfDailyPerformanceRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.IntegrationOfDailyGetter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TemporaryTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.AttendanceLeavingGateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.PCLogOnInfoOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemValueOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;

@Stateless
public class IntegrationOfDailyGetterImpl implements IntegrationOfDailyGetter {

	/** ????????????????????????????????????????????? */
	@Inject
	private AttendanceTimeRepository attendanceTimeRepository;
	
	/** ????????????????????????????????????????????? */
	@Inject
	private WorkInformationRepository workInformationRepository;  
	
	/** ????????????????????????????????????????????? */
	@Inject
	private CalAttrOfDailyPerformanceRepository calAttrOfDailyPerformanceRepository;
	
	/** ????????????????????????????????????????????? */
	@Inject
	private AffiliationInforOfDailyPerforRepository affiliationInforOfDailyPerforRepository;
	
	/** ?????????????????????????????????PC?????????????????? */
	@Inject
	private PCLogOnInfoOfDailyRepo pcLogOnInfoOfDailyRepo; 
	
	/** ???????????????:???????????????????????????????????? */
	@Inject
	private EmployeeDailyPerErrorRepository employeeDailyPerErrorRepository;
	
	/** ???????????????????????????????????????????????? */
	@Inject
	private OutingTimeOfDailyPerformanceRepository outingTimeOfDailyPerformanceRepository;
	
	/** ???????????????????????????????????????????????? */
	@Inject
	private BreakTimeOfDailyPerformanceRepository breakTimeOfDailyPerformanceRepository; 
	
	/** ?????????????????????????????????????????? */
	@Inject
	private TimeLeavingOfDailyPerformanceRepository timeLeavingOfDailyPerformanceRepository;
	
	/** ????????????????????????????????????????????????????????? */
	@Inject
	private ShortTimeOfDailyPerformanceRepository shortTimeOfDailyPerformanceRepository;
	
	/** ???????????????????????????????????????????????? */
	@Inject
	private SpecificDateAttrOfDailyPerforRepo specificDateAttrOfDailyPerforRepo;
	
	/** ?????????????????????????????????????????? */
	@Inject
	private AttendanceLeavingGateOfDailyRepo attendanceLeavingGateOfDailyRepo;
	
	/** ????????????????????????????????????????????? */
	@Inject
	private AnyItemValueOfDailyRepo anyItemValueOfDailyRepo;
	
	/** ???????????????????????????????????????????????? */
	@Inject
	private EditStateOfDailyPerformanceRepository editStateOfDailyPerformanceRepository;
	
	/** ???????????????????????????????????????????????? */
	@Inject
	private TemporaryTimeOfDailyPerformanceRepository temporaryTimeOfDailyPerformanceRepository;
	
	/** ??????????????????????????????????????? */
	@Inject
	private RemarksOfDailyPerformRepo remarksRepository;
	
	@Inject
	private DailySnapshotWorkAdapter snapshotAdapter;
	
	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenSheetRepo;
	
	@Inject
	private OuenWorkTimeOfDailyRepo ouenWorkTimeOfDailyRepo;
	
	
	/**
	 * ????????????(WORK)?????????
	 * @param employeeId
	 * @param datePeriod
	 * @return
	 */
	@Override
	public List<IntegrationOfDaily> getIntegrationOfDaily(String employeeId, DatePeriod datePeriod) {
		
		val attendanceTimeList= workInformationRepository.findByPeriodOrderByYmd(employeeId, datePeriod);
		
		List<IntegrationOfDaily> returnList = new ArrayList<>();
		
		/** ????????????????????????????????????????????? */
		val workInfs = workInformationRepository.findByPeriodOrderByYmd(employeeId, datePeriod);  
		
		/** ??????????????????????????????.??????????????????????????? */
		val calAttrs = calAttrOfDailyPerformanceRepository.finds(Arrays.asList(employeeId),datePeriod);
		
		/** ????????????????????????????????????????????? */
		val affiInfos = affiliationInforOfDailyPerforRepository.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ?????????????????????????????????PC?????????????????? */
		List<PCLogOnInfoOfDaily> pCLogOnInfoOfDailys = pcLogOnInfoOfDailyRepo.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<OutingTimeOfDailyPerformance> outingTimeOfDailyPerformances = outingTimeOfDailyPerformanceRepository.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<BreakTimeOfDailyPerformance> listBreakTimeOfDailyPerformances = breakTimeOfDailyPerformanceRepository.finds(Arrays.asList(employeeId), datePeriod);
		
		List<AttendanceTimeOfDailyPerformance> attendanceTimeOfDailyPerformances = attendanceTimeRepository.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ?????????????????????????????????????????? */
		List<TimeLeavingOfDailyPerformance> timeLeavingOfDailyPerformances = timeLeavingOfDailyPerformanceRepository.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ????????????????????????????????????????????????????????? */
		List<ShortTimeOfDailyPerformance> shortTimeOfDailyPerformances = shortTimeOfDailyPerformanceRepository.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<SpecificDateAttrOfDailyPerfor> specificDateAttrOfDailyPerfor = specificDateAttrOfDailyPerforRepo.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ?????????????????????????????????????????? */
		List<AttendanceLeavingGateOfDaily> attendanceLeavingGateOfDailys = attendanceLeavingGateOfDailyRepo.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ????????????????????????????????????????????? */
		List<AnyItemValueOfDaily> anyItemValueOfDailys = anyItemValueOfDailyRepo.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<EditStateOfDailyPerformance> listEditStateOfDailyPerformances = editStateOfDailyPerformanceRepository.finds(Arrays.asList(employeeId), datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<TemporaryTimeOfDailyPerformance>  temporaryTimeOfDailyPerformances = temporaryTimeOfDailyPerformanceRepository.finds(Arrays.asList(employeeId), datePeriod);
		
		List<RemarksOfDailyPerform> listRemarksOfDailyPerforms = remarksRepository.getRemarks(Arrays.asList(employeeId), datePeriod);
		
		List<OuenWorkTimeSheetOfDaily> ouenSheets = ouenSheetRepo.find(employeeId, datePeriod);
		
		List<OuenWorkTimeOfDaily> ouenTimes = ouenWorkTimeOfDailyRepo.find(Arrays.asList(employeeId), datePeriod);
		
		List<DailySnapshotWorkImport> snapshots = snapshotAdapter.find(employeeId, datePeriod);

		for(WorkInfoOfDailyPerformance attendanceTime : attendanceTimeList) {
			
			GeneralDate ymd = attendanceTime.getYmd();
			
			Optional<WorkInfoOfDailyPerformance> workInf = workInfs.stream().filter(x-> x.getYmd().equals(ymd)).findFirst();
			
			Optional<AffiliationInforOfDailyPerfor> affiInfo = affiInfos.stream().filter(x-> x.getYmd().equals(ymd)).findFirst();

			if(!workInf.isPresent() || !affiInfo.isPresent())//calAttr == null
				continue;
			
			workInf.get().getWorkInformation().setVer(workInf.get().getVersion());
			
			Optional<PCLogOnInfoOfDailyAttd> pCLogOnInfoOfDailyAttd = pCLogOnInfoOfDailys.stream()
					.filter(x -> x.getYmd().equals(ymd)).findFirst()
					.map(x -> Optional.ofNullable(x.getTimeZone())).orElse(Optional.empty());
			
			Optional<OutingTimeOfDailyAttd> outingTimeOfDailyAttd = outingTimeOfDailyPerformances.stream()
					.filter(x -> x.getYmd().equals(ymd)).findFirst()
					.map(x -> Optional.ofNullable(x.getOutingTime())).orElse(Optional.empty());
			
			Optional<AttendanceTimeOfDailyAttendance> attendanceTimeOfDailyAttd = attendanceTimeOfDailyPerformances
					.stream().filter(x -> x.getYmd().equals(ymd)).findFirst()
					.map(x -> Optional.ofNullable(x.getTime())).orElse(Optional.empty());
			
			Optional<TimeLeavingOfDailyAttd> timeLeavingOfDailyAttd = timeLeavingOfDailyPerformances.stream()
					.filter(x -> x.getYmd().equals(ymd)).findFirst().map(x -> Optional.ofNullable(x.getAttendance()))
					.orElse(Optional.empty());
			
			Optional<ShortTimeOfDailyAttd> shortTimeOfDailyAttd = shortTimeOfDailyPerformances.stream()
					.filter(x -> x.getYmd().equals(ymd)).findFirst().map(x -> Optional.ofNullable(x.getTimeZone()))
					.orElse(Optional.empty());
			
			Optional<SpecificDateAttrOfDailyAttd> specificDateAttrOfDailyAttd = specificDateAttrOfDailyPerfor.stream()
					.filter(x -> x.getYmd().equals(ymd)).findFirst().map(x -> Optional.ofNullable(x.getSpecificDay()))
					.orElse(Optional.empty());
			
			
			Optional<AttendanceLeavingGateOfDailyAttd> attendanceLeavingGateOfDailyAttd = attendanceLeavingGateOfDailys.stream()
					.filter(x -> x.getYmd().equals(ymd)).findFirst().map(x-> Optional.ofNullable(x.getTimeZone())).orElse(Optional.empty());
			
			Optional<AnyItemValueOfDailyAttd> anyItemValueOfDailyAttd = anyItemValueOfDailys.stream()
					.filter(x -> x.getYmd().equals(ymd)).findFirst().map(x-> Optional.ofNullable(x.getAnyItem())).orElse(Optional.empty());
			
			
			Optional<TemporaryTimeOfDailyAttd> temporaryTimeOfDailyAttd = temporaryTimeOfDailyPerformances.stream()
					.filter(x -> x.getYmd().equals(ymd)).findFirst().map(x-> Optional.ofNullable(x.getAttendance())).orElse(Optional.empty());
			
			IntegrationOfDaily daily = new IntegrationOfDaily(
					attendanceTime.getEmployeeId(),
					ymd,
					workInf.get().getWorkInformation(),
					calAttrs.stream().filter(x-> x.getYmd().equals(ymd)).findFirst().map(x-> x.getCalcategory()).orElse(null),
					affiInfo.get().getAffiliationInfor(),
					pCLogOnInfoOfDailyAttd,
					employeeDailyPerErrorRepository.find(employeeId, attendanceTime.getYmd()),/** ???????????????:???????????????????????????????????? */
					outingTimeOfDailyAttd,
					listBreakTimeOfDailyPerformances.stream().filter(x-> x.getYmd().equals(ymd)).findFirst().map(x-> x.getTimeZone()).orElse(new BreakTimeOfDailyAttd()),
					attendanceTimeOfDailyAttd,
//						attendanceTimeByWorkOfDailyRepository.find(employeeId, attendanceTime.getYmd()),/** ?????????????????????????????????????????????????????? */
					timeLeavingOfDailyAttd,
					shortTimeOfDailyAttd,
					specificDateAttrOfDailyAttd,
					attendanceLeavingGateOfDailyAttd,
					anyItemValueOfDailyAttd,/** ????????????????????????????????????????????? */
					listEditStateOfDailyPerformances.stream().filter(x-> x.getYmd().equals(ymd)).map(x-> x.getEditState()).collect(Collectors.toList()),
					temporaryTimeOfDailyAttd,
					listRemarksOfDailyPerforms.stream().filter(x-> x.getYmd().equals(ymd)).map(c->c.getRemarks()).collect(Collectors.toList()),
					ouenTimes.stream().filter(x -> x.getYmd().equals(ymd)).findFirst().map(x->x.getOuenTimes()).orElse(new ArrayList<>()),
					ouenSheets.stream().filter(x -> x.getYmd().equals(ymd)).findFirst().map(x->x.getOuenTimeSheet()).orElse(new ArrayList<>()),
					snapshots.stream().filter(x-> x.getYmd().equals(ymd)).findFirst().map(c -> c.getSnapshot().toDomain()));
			

			ouenSheets.stream().filter(x -> x.getYmd().equals(ymd)).findFirst().ifPresent(x -> {
				daily.setOuenTimeSheet(x.getOuenTimeSheet());
			});
			
			Optional<OuenWorkTimeOfDaily> OuenTime = ouenWorkTimeOfDailyRepo.find(employeeId, attendanceTime.getYmd()); 

			if (OuenTime.isPresent()) {
				daily.setOuenTime(OuenTime.get().getOuenTimes());
			}

			returnList.add(daily);
		}
		return returnList;
	}


	@Override
	public List<IntegrationOfDaily> getIntegrationOfDailyClones(List<String> employeeId, DatePeriod datePeriod) {
		val attendanceTimeList= workInformationRepository.findByPeriodOrderByYmdAndEmps(employeeId, datePeriod);
		
		List<IntegrationOfDaily> returnList = new ArrayList<>();
		
		/** ????????????????????????????????????????????? */
		val workInfs = workInformationRepository.findByPeriodOrderByYmdAndEmps(employeeId, datePeriod);  
		
		/** ??????????????????????????????.??????????????????????????? */
		val calAttrs = calAttrOfDailyPerformanceRepository.finds(employeeId,datePeriod);
		
		/** ????????????????????????????????????????????? */
		val affiInfos = affiliationInforOfDailyPerforRepository.finds(employeeId, datePeriod);
		
		/** ?????????????????????????????????PC?????????????????? */
		List<PCLogOnInfoOfDaily> pCLogOnInfoOfDailys = pcLogOnInfoOfDailyRepo.finds(employeeId, datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<OutingTimeOfDailyPerformance> outingTimeOfDailyPerformances = outingTimeOfDailyPerformanceRepository.finds(employeeId, datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<BreakTimeOfDailyPerformance> listBreakTimeOfDailyPerformances = breakTimeOfDailyPerformanceRepository.finds(employeeId, datePeriod);
		
		List<AttendanceTimeOfDailyPerformance> attendanceTimeOfDailyPerformances = attendanceTimeRepository.finds(employeeId, datePeriod);
		
		/** ?????????????????????????????????????????? */
		List<TimeLeavingOfDailyPerformance> timeLeavingOfDailyPerformances = timeLeavingOfDailyPerformanceRepository.finds(employeeId, datePeriod);
		
		/** ????????????????????????????????????????????????????????? */
		List<ShortTimeOfDailyPerformance> shortTimeOfDailyPerformances = shortTimeOfDailyPerformanceRepository.finds(employeeId, datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<SpecificDateAttrOfDailyPerfor> specificDateAttrOfDailyPerfor = specificDateAttrOfDailyPerforRepo.finds(employeeId, datePeriod);
		
		/** ?????????????????????????????????????????? */
		List<AttendanceLeavingGateOfDaily> attendanceLeavingGateOfDailys = attendanceLeavingGateOfDailyRepo.finds(employeeId, datePeriod);
		
		/** ????????????????????????????????????????????? */
		List<AnyItemValueOfDaily> anyItemValueOfDailys = anyItemValueOfDailyRepo.finds(employeeId, datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<EditStateOfDailyPerformance> listEditStateOfDailyPerformances = editStateOfDailyPerformanceRepository.finds(employeeId, datePeriod);
		
		/** ???????????????????????????????????????????????? */
		List<TemporaryTimeOfDailyPerformance>  temporaryTimeOfDailyPerformances = temporaryTimeOfDailyPerformanceRepository.finds(employeeId, datePeriod);
		
		List<RemarksOfDailyPerform> listRemarksOfDailyPerforms = remarksRepository.getRemarks(employeeId, datePeriod);
		
		List<OuenWorkTimeSheetOfDaily> ouenSheets = ouenSheetRepo.find(employeeId, datePeriod);
		
		List<OuenWorkTimeOfDaily> ouenTimes = ouenWorkTimeOfDailyRepo.find(employeeId, datePeriod);
		
		List<DailySnapshotWorkImport> snapshots = snapshotAdapter.find(employeeId, datePeriod);
		
		List<EmployeeDailyPerError> employeeDailyPerError = employeeDailyPerErrorRepository.finds(employeeId, datePeriod);

		for(WorkInfoOfDailyPerformance attendanceTime : attendanceTimeList) {
			
			GeneralDate ymd = attendanceTime.getYmd();
			
			Optional<WorkInfoOfDailyPerformance> workInf = workInfs.stream().filter(x-> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst();
			
			Optional<AffiliationInforOfDailyPerfor> affiInfo = affiInfos.stream().filter(x-> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst();

			if(!workInf.isPresent() || !affiInfo.isPresent())//calAttr == null
				continue;
			
			workInf.get().getWorkInformation().setVer(workInf.get().getVersion());
			
			Optional<PCLogOnInfoOfDailyAttd> pCLogOnInfoOfDailyAttd = pCLogOnInfoOfDailys.stream()
					.filter(x -> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst()
					.map(x -> Optional.ofNullable(x.getTimeZone())).orElse(Optional.empty());
			
			Optional<OutingTimeOfDailyAttd> outingTimeOfDailyAttd = outingTimeOfDailyPerformances.stream()
					.filter(x -> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst()
					.map(x -> Optional.ofNullable(x.getOutingTime())).orElse(Optional.empty());
			
			Optional<AttendanceTimeOfDailyAttendance> attendanceTimeOfDailyAttd = attendanceTimeOfDailyPerformances
					.stream().filter(x -> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst()
					.map(x -> Optional.ofNullable(x.getTime())).orElse(Optional.empty());
			
			Optional<TimeLeavingOfDailyAttd> timeLeavingOfDailyAttd = timeLeavingOfDailyPerformances.stream()
					.filter(x -> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst().map(x -> Optional.ofNullable(x.getAttendance()))
					.orElse(Optional.empty());
			
			Optional<ShortTimeOfDailyAttd> shortTimeOfDailyAttd = shortTimeOfDailyPerformances.stream()
					.filter(x -> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst().map(x -> Optional.ofNullable(x.getTimeZone()))
					.orElse(Optional.empty());
			
			Optional<SpecificDateAttrOfDailyAttd> specificDateAttrOfDailyAttd = specificDateAttrOfDailyPerfor.stream()
					.filter(x -> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst().map(x -> Optional.ofNullable(x.getSpecificDay()))
					.orElse(Optional.empty());
			
			
			Optional<AttendanceLeavingGateOfDailyAttd> attendanceLeavingGateOfDailyAttd = attendanceLeavingGateOfDailys.stream()
					.filter(x -> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst().map(x-> Optional.ofNullable(x.getTimeZone())).orElse(Optional.empty());
			
			Optional<AnyItemValueOfDailyAttd> anyItemValueOfDailyAttd = anyItemValueOfDailys.stream()
					.filter(x -> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst().map(x-> Optional.ofNullable(x.getAnyItem())).orElse(Optional.empty());
			
			
			Optional<TemporaryTimeOfDailyAttd> temporaryTimeOfDailyAttd = temporaryTimeOfDailyPerformances.stream()
					.filter(x -> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst().map(x-> Optional.ofNullable(x.getAttendance())).orElse(Optional.empty());
			
			IntegrationOfDaily daily = new IntegrationOfDaily(
					attendanceTime.getEmployeeId(),
					ymd,
					workInf.get().getWorkInformation(),
					calAttrs.stream().filter(x-> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst().map(x-> x.getCalcategory()).orElse(null),
					affiInfo.get().getAffiliationInfor(),
					pCLogOnInfoOfDailyAttd,
					employeeDailyPerError.stream().filter(c -> c.getEmployeeID().equals(attendanceTime.getEmployeeId()) && c.getDate().equals(attendanceTime.getYmd())).collect(Collectors.toList()),/** ???????????????:???????????????????????????????????? */
					outingTimeOfDailyAttd,
					listBreakTimeOfDailyPerformances.stream().filter(x-> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).findFirst().map(x-> x.getTimeZone()).orElse(new BreakTimeOfDailyAttd()),
					attendanceTimeOfDailyAttd,
//						attendanceTimeByWorkOfDailyRepository.find(employeeId, attendanceTime.getYmd()),/** ?????????????????????????????????????????????????????? */
					timeLeavingOfDailyAttd,
					shortTimeOfDailyAttd,
					specificDateAttrOfDailyAttd,
					attendanceLeavingGateOfDailyAttd,
					anyItemValueOfDailyAttd,/** ????????????????????????????????????????????? */
					listEditStateOfDailyPerformances.stream().filter(x-> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).map(x-> x.getEditState()).collect(Collectors.toList()),
					temporaryTimeOfDailyAttd,
					listRemarksOfDailyPerforms.stream().filter(x-> x.getYmd().equals(ymd) && x.getEmployeeId().equals(attendanceTime.getEmployeeId())).map(c->c.getRemarks()).collect(Collectors.toList()),
					ouenTimes.stream().filter(x -> x.getYmd().equals(ymd) && x.getEmpId().equals(attendanceTime.getEmployeeId())).findFirst().map(x->x.getOuenTimes()).orElse(new ArrayList<>()),
					ouenSheets.stream().filter(x -> x.getYmd().equals(ymd) && x.getEmpId().equals(attendanceTime.getEmployeeId())).findFirst().map(x->x.getOuenTimeSheet()).orElse(new ArrayList<>()),
					snapshots.stream().filter(x-> x.getYmd().equals(ymd) && x.getSid().equals(attendanceTime.getEmployeeId())).findFirst().map(c -> c.getSnapshot().toDomain()));
			

			ouenSheets.stream().filter(x -> x.getYmd().equals(ymd) && x.getEmpId().equals(attendanceTime.getEmployeeId())).findFirst().ifPresent(x -> {
				daily.setOuenTimeSheet(x.getOuenTimeSheet());
			});

			returnList.add(daily);
		}
		return returnList;
	}

}
