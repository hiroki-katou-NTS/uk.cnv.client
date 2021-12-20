package nts.uk.ctx.at.schedule.pubimp.appreflectprocess;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.adapter.appreflect.RequestSettingAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.appreflect.SCAppReflectionSetting;
import nts.uk.ctx.at.schedule.dom.appreflectprocess.change.ReflectApplicationWorkSchedule;
import nts.uk.ctx.at.schedule.dom.appreflectprocess.change.state.SCReasonNotReflect;
import nts.uk.ctx.at.schedule.dom.appreflectprocess.change.state.SCReasonNotReflectDaily;
import nts.uk.ctx.at.schedule.dom.appreflectprocess.change.state.SCReflectStatusResult;
import nts.uk.ctx.at.schedule.dom.appreflectprocess.change.state.SCReflectedState;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.snapshot.DailySnapshotWork;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.snapshot.DailySnapshotWorkRepository;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.ReflectApplicationWorkSchedulePub;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReasonNotReflectDailyExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReasonNotReflectExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReflectStatusResultExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReflectedStateExport;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHAppReflectionSetting;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHApplyTimeSchedulePriority;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHClassifyScheAchieveAtr;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHPriorityTimeReflectAtr;
import nts.uk.ctx.at.shared.dom.calculationsetting.StampReflectionManagement;
import nts.uk.ctx.at.shared.dom.calculationsetting.repository.StampReflectionManagementRepository;
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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateDailyRecordServiceCenterNew;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
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
public class ReflectAppWorkSchedulePubImpl implements ReflectApplicationWorkSchedulePub {

	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Inject
	private BasicScheduleService service;

	@Inject
	private WorkTimeSettingService workTimeSettingService;

	@Inject
	private WorkScheduleRepository workScheduleRepository;

	@Inject
	private DailyRecordConverter convertDailyRecordToAd;

	@Inject
	private ICorrectionAttendanceRule correctionAttendanceRule;

	@Inject
	private CalculateDailyRecordServiceCenterNew calculateDailyRecordServiceCenterNew;

	@Inject
	private RequestSettingAdapter requestSettingAdapter;
	
	@Inject
	private DailySnapshotWorkRepository snapshotRepo;

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

	@Override
	public Pair<SCReflectStatusResultExport, AtomTask> process(Object application, GeneralDate date, SCReflectStatusResultExport reflectStatus, int preAppWorkScheReflectAttr, String execId) {
		String companyId = AppContexts.user().companyId();
		
		RequireImpl impl = new RequireImpl(companyId, workTypeRepo, workTimeSettingRepository, service,
				workTimeSettingService, workScheduleRepository, convertDailyRecordToAd, correctionAttendanceRule,
				calculateDailyRecordServiceCenterNew, requestSettingAdapter, snapshotRepo, flexWorkSettingRepository,
				predetemineTimeSettingRepository, fixedWorkSettingRepository, flowWorkSettingRepository,
				goBackReflectRepository, stampAppReflectRepository, lateEarlyCancelReflectRepository,
				reflectWorkChangeAppRepository, timeLeaveAppReflectRepository, appReflectOtHdWorkRepository,
				vacationApplicationReflectRepository, timePriorityRepository, subLeaveAppReflectRepository, substituteWorkAppReflectRepository,
				applicationReflectHistoryRepo, compensLeaveComSetRepository);
		Pair<SCReflectStatusResult, AtomTask> result = ReflectApplicationWorkSchedule.process(impl, companyId,
				(ApplicationShare) application, date, convertToDom(reflectStatus),
				preAppWorkScheReflectAttr, execId);
		return Pair.of(convertToExport(result.getLeft()), result.getRight());
	}

	private SCReflectStatusResult convertToDom(SCReflectStatusResultExport export) {
		return new SCReflectStatusResult(EnumAdaptor.valueOf(export.getReflectStatus().value, SCReflectedState.class),
				export.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(export.getReasonNotReflectWorkRecord().value,
								SCReasonNotReflectDaily.class),
				export.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(export.getReasonNotReflectWorkSchedule().value,
								SCReasonNotReflect.class));
	}

	private SCReflectStatusResultExport convertToExport(SCReflectStatusResult dom) {
		return new SCReflectStatusResultExport(
				EnumAdaptor.valueOf(dom.getReflectStatus().value, SCReflectedStateExport.class),
				dom.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(dom.getReasonNotReflectWorkRecord().value,
								SCReasonNotReflectDailyExport.class),
				dom.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(dom.getReasonNotReflectWorkSchedule().value,
								SCReasonNotReflectExport.class));
	}
	
	@AllArgsConstructor
	public class RequireImpl implements ReflectApplicationWorkSchedule.Require {

		private final String companyId;

		private final WorkTypeRepository workTypeRepo;

		private final WorkTimeSettingRepository workTimeSettingRepository;

		private final BasicScheduleService service;

		private final WorkTimeSettingService workTimeSettingService;

		private final WorkScheduleRepository workScheduleRepository;

		private final DailyRecordConverter convertDailyRecordToAd;

		private final ICorrectionAttendanceRule correctionAttendanceRule;

		private final CalculateDailyRecordServiceCenterNew calculateDailyRecordServiceCenterNew;

		private final RequestSettingAdapter requestSettingAdapter;
		
		private DailySnapshotWorkRepository snapshotRepo;

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
		
	    private final SubLeaveAppReflectRepository subLeaveAppReflectRepository;
    	
    	private final SubstituteWorkAppReflectRepository substituteWorkAppReflectRepository;
    	
    	private final ApplicationReflectHistoryRepo applicationReflectHistoryRepo;
    	
    	private CompensLeaveComSetRepository compensLeaveComSetRepository;

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String cid, String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}
		
		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			return service.checkNeededOfWorkTimeSetting(workTypeCode);
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
		public Optional<WorkSchedule> get(String employeeID, GeneralDate ymd) {
			return workScheduleRepository.get(employeeID, ymd);
		}

		@Override
		public DailyRecordToAttendanceItemConverter createDailyConverter() {
			return convertDailyRecordToAd.createDailyConverter();
		}

		@Override
		public IntegrationOfDaily process(IntegrationOfDaily domainDaily, ChangeDailyAttendance changeAtt) {
			return correctionAttendanceRule.process(domainDaily, changeAtt);
		}

		@Override
		public void insertSchedule(WorkSchedule workSchedule) {
			workScheduleRepository.delete(workSchedule.getEmployeeID(), workSchedule.getYmd());
			workScheduleRepository.insert(workSchedule);
		}

		@Override
		public List<IntegrationOfDaily> calculateForSchedule(ExecutionType type, CalculateOption calcOption,
				List<IntegrationOfDaily> integrationOfDaily) {
			return calculateDailyRecordServiceCenterNew.calculatePassCompanySetting(calcOption, integrationOfDaily,
					type);
		}

		@Override
		public Optional<SCAppReflectionSetting> getAppReflectionSettingSc(String companyId,
				ApplicationTypeShare appType) {
			return requestSettingAdapter.getAppReflectionSetting(companyId, appType);
		}

		@Override
		public Optional<SHAppReflectionSetting> getAppReflectionSetting(String companyId,
				ApplicationTypeShare appType) {
			return requestSettingAdapter.getAppReflectionSetting(companyId, appType)
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
		public Optional<DailySnapshotWork> snapshot(String sid, GeneralDate ymd) {
			
			return snapshotRepo.find(sid, ymd);
		}
		
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			return predetemineTimeSettingRepository.findByWorkTimeCode(companyId, wktmCd.v()).get();
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
		public Optional<SubstituteWorkAppReflect> findSubWorkAppReflectByCompany(String companyId) {
			return substituteWorkAppReflectRepository.findSubWorkAppReflectByCompany(companyId);
		}

		@Override
		public Optional<SubstituteLeaveAppReflect> findSubLeaveAppReflectByCompany(String companyId) {
			return subLeaveAppReflectRepository.findSubLeaveAppReflectByCompany(companyId);
		}

		@Override
		public Optional<PredetemineTimeSetting> findByWorkTimeCode(String companyId, String workTimeCode) {
			return predetemineTimeSettingRepository.findByWorkTimeCode(companyId, workTimeCode);
		}

		@Override
		public List<IntegrationOfDaily> calculateForRecord(CalculateOption calcOption,
				List<IntegrationOfDaily> integrationOfDaily, Optional<ManagePerCompanySet> companySet,
				ExecutionType reCalcAtr) {
			return calculateForSchedule(reCalcAtr, calcOption, integrationOfDaily);
		}

		@Override
		public CompensatoryLeaveComSetting findCompensatoryLeaveComSet(String companyId) {
			return compensLeaveComSetRepository.find(companyId);
		}

	}
}
