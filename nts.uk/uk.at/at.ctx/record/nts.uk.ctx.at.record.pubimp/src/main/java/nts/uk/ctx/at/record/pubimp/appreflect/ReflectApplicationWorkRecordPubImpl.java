package nts.uk.ctx.at.record.pubimp.appreflect;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.adapter.application.reflect.RCRequestSettingAdapter;
import nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal.EmpDataImport;
import nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal.GetMngInfoFromEmpIDListAdapter;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReasonNotReflect;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReasonNotReflectDaily;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReflectStatusResult;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReflectedState;
import nts.uk.ctx.at.record.dom.applicationcancel.ReflectApplicationWorkRecord;
import nts.uk.ctx.at.record.dom.daily.DailyRecordAdUpService;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.createdailyresults.CreateDailyResults;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.OutputCreateDailyOneDay;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.CalculateDailyRecordServiceCenter;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.attendancetime.reflectleavingwork.CheckRangeReflectLeavingWork;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.attendancetime.reflectwork.CheckRangeReflectAttd;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.attendancetime.reflectwork.OutputCheckRangeReflectAttd;
import nts.uk.ctx.at.record.dom.dailyresultcreationprocess.creationprocess.creationclass.dailywork.TemporarilyReflectStampDailyAttd;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.pub.appreflect.ReflectApplicationWorkRecordPub;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReasonNotReflectDailyExport;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReasonNotReflectExport;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReflectStatusResultExport;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReflectedStateExport;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHAppReflectionSetting;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHApplyTimeSchedulePriority;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHClassifyScheAchieveAtr;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHPriorityTimeReflectAtr;
import nts.uk.ctx.at.shared.dom.calculationsetting.StampReflectionManagement;
import nts.uk.ctx.at.shared.dom.calculationsetting.repository.StampReflectionManagementRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailywork.worktime.empwork.EmployeeWorkDataSetting;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.businesstrip.ReflectBusinessTripApp;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.GoBackReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.GoBackReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.lateearlycancellation.LateEarlyCancelReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.lateearlycancellation.LateEarlyCancelReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.AppReflectOtHdWork;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.AppReflectOtHdWorkRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistoryRepo;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveApplicationReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.leaveapplication.VacationApplicationReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.leaveapplication.VacationApplicationReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.subleaveapp.SubLeaveAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.subleaveapp.SubstituteLeaveAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.workchangeapp.ReflectWorkChangeApp;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.workchangeapp.ReflectWorkChangeAppRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ICorrectionAttendanceRule;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.OutputTimeReflectForWorkinfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.StampReflectRangeOutput;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.TimeReflectFromWorkinfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
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
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingService;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class ReflectApplicationWorkRecordPubImpl implements ReflectApplicationWorkRecordPub {

	@Inject
	private StampCardRepository stampCardRepository;

	@Inject
	private ICorrectionAttendanceRule correctionAttendanceRule;

	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Inject
	private WorkTimeSettingService workTimeSettingService;

	@Inject
	private BasicScheduleService basicScheduleService;

	@Inject
	private TimeReflectFromWorkinfo timeReflectFromWorkinfo;

	@Inject
	private CheckRangeReflectAttd checkRangeReflectAttd;

	@Inject
	private CheckRangeReflectLeavingWork checkRangeReflectLeavingWork;

	@Inject
	private TemporarilyReflectStampDailyAttd temporarilyReflectStampDailyAttd;

	@Inject
	private DailyRecordShareFinder dailyRecordShareFinder;

	@Inject
	private DailyRecordConverter convertDailyRecordToAd;

	@Inject
	private CalculateDailyRecordServiceCenter calculateDailyRecordServiceCenter;

	@Inject
	private DailyRecordAdUpService dailyRecordAdUpService;

	@Inject
	private RCRequestSettingAdapter requestSettingAdapter;

	@Inject
	private FlexWorkSettingRepository flexWorkSettingRepository;

	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;

	@Inject
	private FixedWorkSettingRepository fixedWorkSettingRepository;

	@Inject
	private FlowWorkSettingRepository flowWorkSettingRepository;
	
	@Inject
	private GoBackReflectRepository goBackReflectRepository;
	
	@Inject
	private StampAppReflectRepository stampAppReflectRepository;
	
	@Inject
    private LateEarlyCancelReflectRepository lateEarlyCancelReflectRepository;
    
	@Inject
    private ReflectWorkChangeAppRepository reflectWorkChangeAppRepository;
	
	@Inject
	private TimeLeaveAppReflectRepository timeLeaveAppReflectRepository;
	
	@Inject
	private AppReflectOtHdWorkRepository appReflectOtHdWorkRepository;
	
	@Inject
	private VacationApplicationReflectRepository vacationApplicationReflectRepository;
	
	@Inject
	private SubLeaveAppReflectRepository subLeaveAppReflectRepository;
	
	@Inject
	private SubstituteWorkAppReflectRepository substituteWorkAppReflectRepository;
	
	@Inject
	private ApplicationReflectHistoryRepo applicationReflectHistoryRepo;

	@Inject
	private StampReflectionManagementRepository timePriorityRepository;
	
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;
	
	@Inject
	private GetMngInfoFromEmpIDListAdapter getMngInfoFromEmpIDListAdapter;
	
	@Inject
	private CreateDailyResults createDailyResults;
	
	@Override
	public Pair<RCReflectStatusResultExport, Optional<AtomTask>> process(Object application, GeneralDate date,
			RCReflectStatusResultExport reflectStatus, GeneralDateTime reflectTime, String execId) {

		RequireImpl impl = new RequireImpl(AppContexts.user().companyId(), AppContexts.user().contractCode(),
				stampCardRepository, correctionAttendanceRule, workTypeRepo, workTimeSettingRepository,
				workTimeSettingService, basicScheduleService, timeReflectFromWorkinfo, checkRangeReflectAttd,
				checkRangeReflectLeavingWork, temporarilyReflectStampDailyAttd, dailyRecordShareFinder,
				convertDailyRecordToAd, calculateDailyRecordServiceCenter, dailyRecordAdUpService,
				requestSettingAdapter, flexWorkSettingRepository, predetemineTimeSettingRepository,
				fixedWorkSettingRepository, flowWorkSettingRepository, goBackReflectRepository,
				stampAppReflectRepository, lateEarlyCancelReflectRepository, reflectWorkChangeAppRepository,
				timeLeaveAppReflectRepository, appReflectOtHdWorkRepository, vacationApplicationReflectRepository, timePriorityRepository,
				compensLeaveComSetRepository, subLeaveAppReflectRepository, substituteWorkAppReflectRepository,
				applicationReflectHistoryRepo, getMngInfoFromEmpIDListAdapter, createDailyResults);
		val result = ReflectApplicationWorkRecord.process(impl , AppContexts.user().companyId(), (ApplicationShare) application, date, convertToDom(reflectStatus), reflectTime, execId);
		return Pair.of(convertToExport(result.getLeft()), result.getRight());
	}

	private RCReflectStatusResult convertToDom(RCReflectStatusResultExport export) {
		return new RCReflectStatusResult(EnumAdaptor.valueOf(export.getReflectStatus().value, RCReflectedState.class),
				export.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(export.getReasonNotReflectWorkRecord().value,
								RCReasonNotReflectDaily.class),
				export.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(export.getReasonNotReflectWorkSchedule().value,
								RCReasonNotReflect.class));
	}

	private RCReflectStatusResultExport convertToExport(RCReflectStatusResult dom) {
		return new RCReflectStatusResultExport(
				EnumAdaptor.valueOf(dom.getReflectStatus().value, RCReflectedStateExport.class),
				dom.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(dom.getReasonNotReflectWorkRecord().value,
								RCReasonNotReflectDailyExport.class),
				dom.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(dom.getReasonNotReflectWorkSchedule().value,
								RCReasonNotReflectExport.class));
	}
	
	@AllArgsConstructor
	public class RequireImpl implements ReflectApplicationWorkRecord.Require {

		private final String companyId;

		private final String contractCode;

		private final StampCardRepository stampCardRepository;

		private final ICorrectionAttendanceRule correctionAttendanceRule;

		private final WorkTypeRepository workTypeRepo;

		private final WorkTimeSettingRepository workTimeSettingRepository;

		private final WorkTimeSettingService workTimeSettingService;

		private final BasicScheduleService basicScheduleService;

		private final TimeReflectFromWorkinfo timeReflectFromWorkinfo;

		private final CheckRangeReflectAttd checkRangeReflectAttd;

		private final CheckRangeReflectLeavingWork checkRangeReflectLeavingWork;

		private final TemporarilyReflectStampDailyAttd temporarilyReflectStampDailyAttd;

		private final DailyRecordShareFinder dailyRecordShareFinder;

		private final DailyRecordConverter convertDailyRecordToAd;

		private final CalculateDailyRecordServiceCenter calculateDailyRecordServiceCenter;

		private final DailyRecordAdUpService dailyRecordAdUpService;

		private final RCRequestSettingAdapter requestSettingAdapter;

		private final FlexWorkSettingRepository flexWorkSettingRepository;

		private final PredetemineTimeSettingRepository predetemineTimeSettingRepository;

		private final FixedWorkSettingRepository fixedWorkSettingRepository;

		private final FlowWorkSettingRepository flowWorkSettingRepository;
		
		private final GoBackReflectRepository goBackReflectRepository;
		
		private final StampAppReflectRepository stampAppReflectRepository;
		
        private final LateEarlyCancelReflectRepository lateEarlyCancelReflectRepository;
        
        private final ReflectWorkChangeAppRepository reflectWorkChangeAppRepository;
        
        private final TimeLeaveAppReflectRepository timeLeaveAppReflectRepository;
        
        private final AppReflectOtHdWorkRepository appReflectOtHdWorkRepository;
        
    	private final VacationApplicationReflectRepository vacationApplicationReflectRepository;
    	
        private final StampReflectionManagementRepository timePriorityRepository;
        
    	private final CompensLeaveComSetRepository compensLeaveComSetRepository;
    	
	    private final SubLeaveAppReflectRepository subLeaveAppReflectRepository;
    	
    	private final SubstituteWorkAppReflectRepository substituteWorkAppReflectRepository;
    	
    	private final ApplicationReflectHistoryRepo applicationReflectHistoryRepo;
    	
    	private final GetMngInfoFromEmpIDListAdapter getMngInfoFromEmpIDListAdapter;
    	
    	private final CreateDailyResults createDailyResults;

		@Override
		public List<StampCard> getLstStampCardBySidAndContractCd(String sid) {
			return stampCardRepository.getLstStampCardBySidAndContractCd(contractCode, sid);
		}

		@Override
		public IntegrationOfDaily process(IntegrationOfDaily domainDaily, ChangeDailyAttendance changeAtt) {
			return correctionAttendanceRule.process(domainDaily, changeAtt);
		}

		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			return basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}

		// fix bug 113211
//		@Override
//		public PredetermineTimeSetForCalc getPredeterminedTimezone(String workTypeCd, String workTimeCd, Integer workNo) {
//			return workTimeSettingService.getPredeterminedTimezone(companyId, workTimeCd, workTypeCd, workNo);
//		}

		@Override
		public void insertAppReflectHist(ApplicationReflectHistory hist) {
			applicationReflectHistoryRepo.insertAppReflectHist(companyId, hist);
		}

		@Override
		public OutputTimeReflectForWorkinfo getTimeReflect(String employeeId, GeneralDate ymd,
				WorkInfoOfDailyAttendance workInformation) {
			return timeReflectFromWorkinfo.get(companyId, employeeId, ymd, workInformation);
		}

		@Override
		public OutputCheckRangeReflectAttd checkRangeReflectAttd(Stamp stamp,
				StampReflectRangeOutput stampReflectRangeOutput, IntegrationOfDaily integrationOfDaily) {
			return checkRangeReflectAttd.checkRangeReflectAttd(stamp, stampReflectRangeOutput, integrationOfDaily);
		}

		@Override
		public OutputCheckRangeReflectAttd checkRangeReflectOut(Stamp stamp, StampReflectRangeOutput s,
				IntegrationOfDaily integrationOfDaily) {
			return checkRangeReflectLeavingWork.checkRangeReflectAttd(stamp, s, integrationOfDaily);
		}

		@Override
		public List<ErrorMessageInfo> reflectStamp(String companyId, Stamp stamp, StampReflectRangeOutput stampReflectRangeOutput,
				IntegrationOfDaily integrationOfDaily, ChangeDailyAttendance changeDailyAtt) {
			return temporarilyReflectStampDailyAttd.reflectStamp(companyId, stamp, stampReflectRangeOutput,
					integrationOfDaily, changeDailyAtt);
		}

		@Override
		public Optional<EmployeeWorkDataSetting> getEmpWorkDataSetting(String employeeId) {
			// TODO: ??? repo
			return Optional.empty();
		}

		@Override
		public Optional<IntegrationOfDaily> findDaily(String employeeId, GeneralDate date) {
			return dailyRecordShareFinder.find(employeeId, date);
		}

		@Override
		public DailyRecordToAttendanceItemConverter createDailyConverter() {
			return convertDailyRecordToAd.createDailyConverter();
		}

		@Override
		public List<IntegrationOfDaily> calculateForSchedule(CalculateOption calcOption,
				List<IntegrationOfDaily> integrationOfDaily, Optional<ManagePerCompanySet> companySet, ExecutionType reCalcAtr) {
			return calculateDailyRecordServiceCenter.calculatePassCompanySetting(calcOption, integrationOfDaily, companySet, reCalcAtr);
		}

		@Override
		public void addAllDomain(IntegrationOfDaily domain, boolean removeError) {
			dailyRecordAdUpService.addAllDomain(domain);
		}

		@Override
		public Optional<SHAppReflectionSetting> getAppReflectionSetting(String companyId,
				ApplicationTypeShare appType) {
			return requestSettingAdapter.getAppReflectionSetting(companyId)
					.map(x -> new SHAppReflectionSetting(x.getScheReflectFlg(),
							EnumAdaptor.valueOf(x.getPriorityTimeReflectFlag().value, SHPriorityTimeReflectAtr.class),
							x.getAttendentTimeReflectFlg(),
							EnumAdaptor.valueOf(x.getClassScheAchi().value, SHClassifyScheAchieveAtr.class),
							EnumAdaptor.valueOf(x.getReflecTimeofSche().value, SHApplyTimeSchedulePriority.class)));
		}

		@Override
		public Optional<ReflectBusinessTripApp> findReflectBusinessTripApp(String companyId) {
			return Optional.of(new ReflectBusinessTripApp(companyId));
		}

		@Override
		public Optional<ReflectWorkChangeApp> findReflectWorkCg(String companyId) {
			return reflectWorkChangeAppRepository.findByCompanyIdReflect(companyId);
		}

		@Override
		public Optional<GoBackReflect> findReflectGoBack(String companyId) {
			return goBackReflectRepository.findByCompany(companyId);
		}

		@Override
		public Optional<StampAppReflect> findReflectAppStamp(String companyId) {
			return stampAppReflectRepository.findReflectByCompanyId(companyId);
		}

		@Override
		public Optional<LateEarlyCancelReflect> findReflectArrivedLateLeaveEarly(String companyId) {
			return Optional.ofNullable(lateEarlyCancelReflectRepository.getByCompanyId(companyId));
		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
			return fixedWorkSettingRepository.findByKey(companyId, code.v()).get();
		}

		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
			return flowWorkSettingRepository.find(companyId, code.v()).get();
		}

		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
			return flexWorkSettingRepository.find(companyId, code.v()).get();
		}

		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			return predetemineTimeSettingRepository.findByWorkTimeCode(companyId, wktmCd.v()).get();
		}

		@Override
		public Optional<TimeLeaveApplicationReflect> findReflectTimeLeav(String companyId) {
			return timeLeaveAppReflectRepository.findByCompany(companyId);
		}

		@Override
		public Optional<AppReflectOtHdWork> findOvertime(String companyId) {
			return appReflectOtHdWorkRepository.findByCompanyId(companyId);
		}

		@Override
		public Optional<VacationApplicationReflect> findVacationApp(String companyId) {
			return vacationApplicationReflectRepository.findReflectByCompanyId(companyId);
		}
		
		@Override
		public Optional<StampReflectionManagement> findByCid(String companyId) {
			return timePriorityRepository.findByCid(companyId);
		}

		@Override
		public Optional<WorkType> findByPK(String companyId, String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<PredetemineTimeSetting> findByWorkTimeCode(String companyId, String workTimeCode) {
			return predetemineTimeSettingRepository.findByWorkTimeCode(companyId, workTimeCode);
		}

		@Override
		public List<IntegrationOfDaily> calculateForRecord(CalculateOption calcOption,
				List<IntegrationOfDaily> integrationOfDaily, Optional<ManagePerCompanySet> companySet,
				ExecutionType reCalcAtr) {
			return calculateForSchedule(calcOption, integrationOfDaily, companySet, reCalcAtr);
		}

		@Override
		public CompensatoryLeaveComSetting findCompensatoryLeaveComSet(String companyId) {
			return compensLeaveComSetRepository.find(companyId);
		}

		@Override
		public void removeConfirmApproval(List<IntegrationOfDaily> domainDaily) {
			dailyRecordAdUpService.removeConfirmApproval(domainDaily);
		}

		@Override
		public Optional<SubstituteWorkAppReflect> findSubWorkAppReflectByCompany(String companyId) {
			return substituteWorkAppReflectRepository.findSubWorkAppReflectByCompany(companyId);
		}

		@Override
		public Optional<SubstituteLeaveAppReflect> findSubLeaveAppReflectByCompany(String companyId) {
			return subLeaveAppReflectRepository.findSubLeaveAppReflectByCompany(companyId);
		}

		@Override
		public Optional<StampCard> getByCardNoAndContractCode(ContractCode contractCode, StampNumber stampNumber) {
			return stampCardRepository.getByCardNoAndContractCode(stampNumber.v(), contractCode.v());
		}

		@Override
		public List<EmpDataImport> getEmpData(List<String> empIDList) {
			return getMngInfoFromEmpIDListAdapter.getEmpData(empIDList);
		}

		@Override
		public Optional<IntegrationOfDaily> find(String employeeId, GeneralDate date) {
			return dailyRecordShareFinder.find(employeeId, date);
		}

		@Override
		public Optional<OutputCreateDailyOneDay> createDailyResult(String companyId, String employeeId, GeneralDate ymd,
				ExecutionTypeDaily executionType) {
			return createDailyResults.createDailyResult(companyId, employeeId, ymd, executionType);
		}

		@Override
		public OutputTimeReflectForWorkinfo get(String companyId, String employeeId, GeneralDate ymd,
				WorkInfoOfDailyAttendance workInformation) {
			return timeReflectFromWorkinfo.get(companyId, employeeId, ymd, workInformation);
		}
		
		@Override
		public Optional<WorkTimeSetting> getWorkTime(String cid, String workTimeCode) {
			return workTimeSettingRepository.findByCode(cid, workTimeCode);
		}
	}
}
