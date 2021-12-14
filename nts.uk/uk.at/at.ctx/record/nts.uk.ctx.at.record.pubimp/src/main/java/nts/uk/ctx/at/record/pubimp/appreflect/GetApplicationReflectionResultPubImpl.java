package nts.uk.ctx.at.record.pubimp.appreflect;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.application.reflect.RCRequestSettingAdapter;
import nts.uk.ctx.at.record.dom.applicationcancel.getreflect.GetApplicationReflectionResult;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.CalculateDailyRecordServiceCenter;
import nts.uk.ctx.at.record.pub.appreflect.GetApplicationReflectionResultPub;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHAppReflectionSetting;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHApplyTimeSchedulePriority;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHClassifyScheAchieveAtr;
import nts.uk.ctx.at.shared.dom.adapter.application.reflect.SHPriorityTimeReflectAtr;
import nts.uk.ctx.at.shared.dom.calculationsetting.StampReflectionManagement;
import nts.uk.ctx.at.shared.dom.calculationsetting.repository.StampReflectionManagementRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
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

@Stateless
public class GetApplicationReflectionResultPubImpl implements GetApplicationReflectionResultPub {

	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Inject
	private WorkTimeSettingService workTimeSettingService;

	@Inject
	private BasicScheduleService basicScheduleService;

	@Inject
	private DailyRecordShareFinder dailyRecordShareFinder;

	@Inject
	private CalculateDailyRecordServiceCenter calculateDailyRecordServiceCenter;

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
	private ICorrectionAttendanceRule correctionAfterTimeChange;
	
	@Inject
	private TimeLeaveAppReflectRepository timeLeaveAppReflectRepository;
	
	@Inject
	private StampReflectionManagementRepository timePriorityRepository;

	@Inject
	private DailyRecordConverter dailyRecordConverter;

	@Inject
	private AppReflectOtHdWorkRepository appReflectOtHdWorkRepository;
	
	@Inject
	private VacationApplicationReflectRepository vacationApplicationReflectRepository;
	
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;
	
	@Inject
	private SubLeaveAppReflectRepository subLeaveAppReflectRepository;
	
	@Inject
	private SubstituteWorkAppReflectRepository substituteWorkAppReflectRepository;
	
	@Override
	public Optional<IntegrationOfDaily> getApp(String companyId, Object application, GeneralDate baseDate,
			Optional<IntegrationOfDaily> dailyData) {
		RequireImpl impl = new RequireImpl(companyId, workTypeRepo, workTimeSettingRepository, workTimeSettingService,
				basicScheduleService, dailyRecordShareFinder, calculateDailyRecordServiceCenter, requestSettingAdapter,
				flexWorkSettingRepository, predetemineTimeSettingRepository, fixedWorkSettingRepository,
				flowWorkSettingRepository, goBackReflectRepository, stampAppReflectRepository,
				lateEarlyCancelReflectRepository, reflectWorkChangeAppRepository, correctionAfterTimeChange,
				timeLeaveAppReflectRepository, dailyRecordConverter, appReflectOtHdWorkRepository,
				vacationApplicationReflectRepository, timePriorityRepository, compensLeaveComSetRepository,
				subLeaveAppReflectRepository, substituteWorkAppReflectRepository);
		return GetApplicationReflectionResult.getReflectResult(impl, companyId, (ApplicationShare) application, baseDate,
				dailyData);
	}

	@AllArgsConstructor
	public class RequireImpl implements GetApplicationReflectionResult.Require {

		private final String companyId;

		private final WorkTypeRepository workTypeRepo;

		private final WorkTimeSettingRepository workTimeSettingRepository;

		private final WorkTimeSettingService workTimeSettingService;

		private final BasicScheduleService basicScheduleService;

		private final DailyRecordShareFinder dailyRecordShareFinder;

		private final CalculateDailyRecordServiceCenter calculateDailyRecordServiceCenter;

		private final RCRequestSettingAdapter requestSettingAdapter;

		private final FlexWorkSettingRepository flexWorkSettingRepository;

		private final PredetemineTimeSettingRepository predetemineTimeSettingRepository;

		private final FixedWorkSettingRepository fixedWorkSettingRepository;

		private final FlowWorkSettingRepository flowWorkSettingRepository;

		private final GoBackReflectRepository goBackReflectRepository;

		private final StampAppReflectRepository stampAppReflectRepository;

		private final LateEarlyCancelReflectRepository lateEarlyCancelReflectRepository;

		private final ReflectWorkChangeAppRepository reflectWorkChangeAppRepository;
		
		private final ICorrectionAttendanceRule  correctionAfterTimeChange;

		private final TimeLeaveAppReflectRepository timeLeaveAppReflectRepository;

		private final DailyRecordConverter dailyRecordConverter;
		
		private final AppReflectOtHdWorkRepository appReflectOtHdWorkRepository;
		
		private final VacationApplicationReflectRepository vacationApplicationReflectRepository;
		
		private final StampReflectionManagementRepository timePriorityRepository;
		
		private final CompensLeaveComSetRepository compensLeaveComSetRepository;
		
		private final SubLeaveAppReflectRepository subLeaveAppReflectRepository;
    	
    	private final SubstituteWorkAppReflectRepository substituteWorkAppReflectRepository;
    	
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

//		@Override
//		public PredetermineTimeSetForCalc getPredeterminedTimezone(String workTypeCd, String workTimeCd,
//				Integer workNo) {
//			return workTimeSettingService.getPredeterminedTimezone(companyId, workTimeCd, workTypeCd, workNo);
//		}

		@Override
		public Optional<IntegrationOfDaily> findDaily(String employeeId, GeneralDate date) {
			return dailyRecordShareFinder.find(employeeId, date);
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
		public List<IntegrationOfDaily> calculateForRecord(CalculateOption calcOption,
				List<IntegrationOfDaily> integrationOfDaily, Optional<ManagePerCompanySet> companySet,
				ExecutionType reCalcAtr) {
			return calculateDailyRecordServiceCenter.calculatePassCompanySetting(calcOption, integrationOfDaily,
					companySet, reCalcAtr);
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
		public IntegrationOfDaily correction(IntegrationOfDaily domainDaily, ChangeDailyAttendance changeAtt) {
			return correctionAfterTimeChange.process(domainDaily, changeAtt);
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
		public DailyRecordToAttendanceItemConverter createDailyConverter() {
			return dailyRecordConverter.createDailyConverter();
		}

		@Override
		public CompensatoryLeaveComSetting findCompensatoryLeaveComSet(String companyId) {
			return compensLeaveComSetRepository.find(companyId);
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
		public Optional<WorkTimeSetting> getWorkTime(String cid, String workTimeCode) {
			return workTimeSettingRepository.findByCode(cid, workTimeCode);
		}
	}

}
