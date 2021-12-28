package nts.uk.ctx.at.record.dom.daily;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.actualworkinghours.daily.workrecord.AttendanceTimeByWorkOfDaily;
import nts.uk.ctx.at.record.dom.actualworkinghours.daily.workrecord.repo.AttendanceTimeByWorkOfDailyRepository;
import nts.uk.ctx.at.record.dom.actualworkinghours.repository.AttendanceTimeRepository;
import nts.uk.ctx.at.record.dom.adapter.workschedule.snapshot.DailySnapshotWorkAdapter;
import nts.uk.ctx.at.record.dom.adapter.workschedule.snapshot.DailySnapshotWorkImport;
import nts.uk.ctx.at.record.dom.affiliationinformation.AffiliationInforOfDailyPerfor;
import nts.uk.ctx.at.record.dom.affiliationinformation.repository.AffiliationInforOfDailyPerforRepository;
import nts.uk.ctx.at.record.dom.breakorgoout.BreakTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.breakorgoout.OutingTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.breakorgoout.repository.BreakTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.breakorgoout.repository.OutingTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.calculationattribute.CalAttrOfDailyPerformance;
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
import nts.uk.ctx.at.record.dom.dailyprocess.calc.AdTimeAndAnyItemAdUpService;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.raisesalarytime.SpecificDateAttrOfDailyPerfor;
import nts.uk.ctx.at.record.dom.raisesalarytime.repo.SpecificDateAttrOfDailyPerforRepo;
import nts.uk.ctx.at.record.dom.shorttimework.ShortTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.shorttimework.repo.ShortTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.approvalsituationmanagement.export.clearapprovalconfirm.ClearConfirmApprovalService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ErrorAlarmClassification;
import nts.uk.ctx.at.record.dom.worktime.TemporaryTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.repository.TemporaryTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.worktime.repository.TimeLeavingOfDailyPerformanceRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.snapshot.SnapShot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

@Stateless
public class DailyRecordAdUpServiceImpl implements DailyRecordAdUpService {

	@Inject
	private WorkInformationRepository workInfoRepo;

	@Inject
	private AffiliationInforOfDailyPerforRepository affInfoRepo;

	@Inject
	private CalAttrOfDailyPerformanceRepository calAttrRepo;

	@Inject
	private TimeLeavingOfDailyPerformanceRepository timeLeavingRepo;

	@Inject
	private BreakTimeOfDailyPerformanceRepository breakTimeRepo;

	@Inject
	private OutingTimeOfDailyPerformanceRepository outTimeRepo;

	@Inject
	private ShortTimeOfDailyPerformanceRepository shortTimeRepo;

	@Inject
	private TemporaryTimeOfDailyPerformanceRepository temporaryTimrRepo;

	@Inject
	private AttendanceLeavingGateOfDailyRepo attendanceLeavingGateRepo;

	@Inject
	private AttendanceTimeRepository attendanceTimeRepo;

	@Inject
	private SpecificDateAttrOfDailyPerforRepo specificDateAttrRepo;

	@Inject
	private EditStateOfDailyPerformanceRepository editStateRepo;

	@Inject
	private AnyItemValueOfDailyRepo anyItemRepo;

	@Inject
	private AttendanceTimeByWorkOfDailyRepository attendanceTimeWorkRepo;

	@Inject
	private PCLogOnInfoOfDailyRepo pcLogOnRepo;

	@Inject
	private RemarksOfDailyPerformRepo remarksOfDailyRepo;

	@Inject
	private AdTimeAndAnyItemAdUpService adTimeAndAnyItemAdUpService;
	
	@Inject
	private EmployeeDailyPerErrorRepository employeeErrorRepo;
	
	@Inject
	private OuenWorkTimeOfDailyRepo ouenWorkTimeOfDailyRepo;

	@Inject
	private DailySnapshotWorkAdapter snapshotAdapter;
	
	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenWorkTimeSheetOfDailyRepo;

	@Inject
	private ErrorAlarmWorkRecordRepository errorAlarmWorkRecordRepo;
	
	@Inject
	private ClearConfirmApprovalService clearConfirmApprovalService;
	@Override
	public void adUpWorkInfo(WorkInfoOfDailyPerformance workInfo) {
		workInfoRepo.updateByKey(workInfo);
	}

	@Override
	public void adUpAffilicationInfo(AffiliationInforOfDailyPerfor affiliationInfor) {
		if(affInfoRepo.findByKey(affiliationInfor.getEmployeeId(), affiliationInfor.getYmd()).isPresent()) {
			affInfoRepo.updateByKey(affiliationInfor);
		}else {
			affInfoRepo.add(affiliationInfor);
		}
	}

	@Override
	public void adUpCalAttr(CalAttrOfDailyPerformance calAttr) {
		calAttrRepo.update(calAttr);
	}

	@Override
	public void adUpTimeLeaving(Optional<TimeLeavingOfDailyPerformance> attendanceLeave) {
		if (!attendanceLeave.isPresent())
			return;
		if(timeLeavingRepo.findByKey(attendanceLeave.get().getEmployeeId(), attendanceLeave.get().getYmd()).isPresent()) {
			timeLeavingRepo.update(attendanceLeave.get());
		}else {
			timeLeavingRepo.insert(attendanceLeave.get());
		}

	}

	@Override
	public void adUpBreakTime(BreakTimeOfDailyPerformance breakTime) {
//		breakTime.ifPresent(domain -> {
		breakTimeRepo.delete(breakTime.getEmployeeId(), breakTime.getYmd());
		breakTimeRepo.update(breakTime);
//		});

	}

	@Override
	public void adUpOutTime(Optional<OutingTimeOfDailyPerformance> outingTime) {
		if (!outingTime.isPresent())
			return;
		outTimeRepo.update(outingTime.get());
	}

	@Override
	public void adUpShortTime(Optional<ShortTimeOfDailyPerformance> shortTime) {
		if (!shortTime.isPresent())
			return;
		shortTimeRepo.updateByKey(shortTime.get());
	}

	@Override
	public void adUpTemporaryTime(Optional<TemporaryTimeOfDailyPerformance> tempTime) {
		if (!tempTime.isPresent())
			return;
		if(temporaryTimrRepo.findByKey(tempTime.get().getEmployeeId(), tempTime.get().getYmd()).isPresent()) {
			temporaryTimrRepo.update(tempTime.get());
		}else {
			temporaryTimrRepo.add(tempTime.get());
		}

	}

	@Override
	public void adUpAttendanceLeavingGate(Optional<AttendanceLeavingGateOfDaily> attendanceLeavingGate) {
		if (!attendanceLeavingGate.isPresent())
			return;
		attendanceLeavingGateRepo.update(attendanceLeavingGate.get());

	}

	@Override
	public void adUpAttendanceTime(Optional<AttendanceTimeOfDailyPerformance> attendanceTimeOfDailyPerformance) {
		if (!attendanceTimeOfDailyPerformance.isPresent())
			return;
		attendanceTimeRepo.update(attendanceTimeOfDailyPerformance.get());

	}

	@Override
	public void adUpSpecificDate(Optional<SpecificDateAttrOfDailyPerfor> specDateAttr) {
		if (!specDateAttr.isPresent())
			return;
		specificDateAttrRepo.update(specDateAttr.get());

	}

	@Override
	public void adUpEditState(List<EditStateOfDailyPerformance> editState) {
		if (editState.isEmpty())
			return;
		//editState.stream().forEach(domain -> {
		editStateRepo.updateByKey(editState);
		//});

	}

	@Override
	public void clearExcludeEditState(List<EditStateOfDailyPerformance> editState) {
		if (editState.isEmpty())
			return;
		editStateRepo.deleteExclude(editState);

	}

	@Override
	public void adUpAnyItem(Optional<AnyItemValueOfDaily> anyItemValue) {
		if (!anyItemValue.isPresent())
			return;
		anyItemRepo.update(anyItemValue.get());

	}

	@Override
	public void adUpAttendanceTimeByWork(Optional<AttendanceTimeByWorkOfDaily> attendancetimeByWork) {
		if (!attendancetimeByWork.isPresent())
			return;
		//TODO:****
		attendanceTimeWorkRepo.update(attendancetimeByWork.get());
	}

	@Override
	public void adUpPCLogOn(Optional<PCLogOnInfoOfDaily> pcLogOnInfo) {
		if (!pcLogOnInfo.isPresent())
			return;
		pcLogOnRepo.update(pcLogOnInfo.get());

	}

	@Override
	public void adUpRemark(List<RemarksOfDailyPerform> remarks) {
		if (remarks.isEmpty())
			return;
		//remarks.stream().forEach(domain -> {
		remarksOfDailyRepo.update(remarks);
		//});
	}

	@Override
	public void adUpEmpError(List<EmployeeDailyPerError> errors, List<Pair<String, GeneralDate>> lstPairRemove, boolean hasRemoveError) {
		if (hasRemoveError) {
//			Map<String, List<GeneralDate>> mapError = errors.stream().collect(
//					Collectors.groupingBy(c -> c.getEmployeeID(), Collectors.collectingAndThen(Collectors.toList(),
//							c -> c.stream().map(q -> q.getDate()).collect(Collectors.toList()))));
			Map<String, List<GeneralDate>> mapEmpDateError = lstPairRemove.stream().collect(
					Collectors.groupingBy(c -> c.getLeft(), Collectors.collectingAndThen(Collectors.toList(),
					c -> c.stream().map(q -> q.getRight()).collect(Collectors.toList()))));
			employeeErrorRepo.removeNotOTK(mapEmpDateError);
		}
		employeeErrorRepo.update(errors);

	}

	@Override
	public List<IntegrationOfDaily> adTimeAndAnyItemAdUp(List<IntegrationOfDaily> dailys) {
		return adTimeAndAnyItemAdUpService.saveOnly(dailys);
	}

	@Override
	public void removeConfirmApproval(List<IntegrationOfDaily> domainDaily) {
		if(!domainDaily.isEmpty()) {
			//新しい年月日一覧を作る
			List<GeneralDate> listDate = new ArrayList<>();
			//日別勤怠（Work）一覧をループする
			for(IntegrationOfDaily integrationOfDaily : domainDaily) {
				//社員の日別実績エラー一覧を取得する
				List<EmployeeDailyPerError> listEmployeeError = integrationOfDaily.getEmployeeError();
				for(EmployeeDailyPerError error :listEmployeeError) {
					//日別実績のエラーアラームを取得する
					ErrorAlarmWorkRecordCode errorAlarmWorkRecordCode = error.getErrorAlarmWorkRecordCode();
					//日別実績のエラーアラームがエラーかを確認する
					Optional<ErrorAlarmWorkRecord> optError = errorAlarmWorkRecordRepo.findByCode(errorAlarmWorkRecordCode.v());
					if(optError.isPresent() && optError.get().getTypeAtr() == ErrorAlarmClassification.ERROR) {
						listDate.add(integrationOfDaily.getYmd());
					}
				}
			}
			listDate = listDate.stream().distinct().collect(Collectors.toList());
			if(!listDate.isEmpty()) {
				//確認、承認のクリア
				clearConfirmApprovalService.clearConfirmApproval(domainDaily.get(0).getEmployeeId(), listDate);;
			}
		}
//		Optional<IdentityProcessUseSet> iPUSOptTemp = iPUSOpt.isPresent() ? iPUSOpt : identityProcessUseRepository.findByKey(companyId);
//		Optional<ApprovalProcessingUseSetting> approvalSetTemp = approvalSet.isPresent() ? approvalSet : approvalSettingRepo.findByCompanyId(companyId);
//		
//		domainDaily.forEach(record ->{
//			divTimeSysFixedCheckService.removeconfirm(companyId, record.getEmployeeId(),
//					record.getYmd(), record.getEmployeeError(), iPUSOptTemp, approvalSetTemp);
//		});
	}

	@Override
	public void adUpSnapshot(String sid, GeneralDate ymd, SnapShot snapshot) {
		snapshotAdapter.update(DailySnapshotWorkImport.from(sid, ymd, snapshot));
	}

	@Override
	public void adUpSupportTime(String sid, GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheets) {
		OuenWorkTimeSheetOfDaily domain = new OuenWorkTimeSheetOfDaily(sid, ymd, ouenTimeSheets);
		List<OuenWorkTimeSheetOfDaily> update = new ArrayList<>();
		update.add(domain);
		ouenWorkTimeSheetOfDailyRepo.insert(update);
	}

	@Override
	public void adUpOuenWorkTime(Optional<OuenWorkTimeOfDaily> ouen) {
		if (!ouen.isPresent())
			return;
		ouenWorkTimeOfDailyRepo.update(ouen.get());
	}
}
