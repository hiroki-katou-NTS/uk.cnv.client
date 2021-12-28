package nts.uk.ctx.at.schedule.dom.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RecordRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ScheRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubsTransferProcessMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByScheData;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.CreateWorkRecordScheduleRemain;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateDailyRecordServiceCenterNew;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.GetSubHolOccurrenceSetting;
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
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;

/**
 * 暫定データを作成する為の勤務予定を取得する
 */
@Stateless
public class RemainCreateInforByScheDataImpl implements RemainCreateInforByScheData {

	@Inject
	private WorkScheduleRepository workScheRepo;
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;
	@Inject
	private DailyRecordConverter dailyRecordConverter;
	@Inject
	private CalculateDailyRecordServiceCenterNew calculateDailyRecordServiceCenter;
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;
	@Inject
	private FlexWorkSettingRepository flexWorkSettingRepository;
	@Inject
	private FixedWorkSettingRepository fixedWorkSettingRepository;
	@Inject
	private FlowWorkSettingRepository flowWorkSettingRepository;
	@Inject
	private WorkTypeRepository workTypeRepo;
	@Inject
	private BasicScheduleService service;
	

	@Override
	public List<ScheRemainCreateInfor> createRemainInforNew(String cid, String sid, List<GeneralDate> dates) {

		DatePeriod period = new DatePeriod(dates.stream().min(Comparator.comparing(GeneralDate::date)).get(),
				dates.stream().max(Comparator.comparing(GeneralDate::date)).get());
		// 勤務予定を取得する
		List<WorkSchedule> sches = this.workScheRepo.getListBySid(sid, period);
		// (Imported)「残数作成元の勤務予定を取得する」
		// 残数作成元情報を返す
		RequireImpl impl = new RequireImpl(cid);
		
		List<ScheRemainCreateInfor> scheRemainInfor = new ArrayList<>();
		for (WorkSchedule c : sches) {
			Optional<RecordRemainCreateInfor> remainInfor = CreateWorkRecordScheduleRemain
					.createRemain(impl, cid, Arrays.asList(toDailyDomain(c)), SubsTransferProcessMode.DAILY).stream()
					.findFirst();
			if (!remainInfor.isPresent())
				continue;

			val workType = impl.getWorkType(c.getWorkInfo().getRecordInfo().getWorkTypeCode().v());
			if (!workType.isPresent() || !c.getOptAttendanceTime().isPresent()) {
				scheRemainInfor.add(ScheRemainCreateInfor.toScheRemain(remainInfor.get()));
				continue;
			}

			if (workType.get().isHolidayWork()) {
					getTranferTime(impl, cid, c, CompensatoryOccurrenceDivision.WorkDayOffTime).ifPresent(tranTime -> {
						remainInfor.get().setTransferTotal(tranTime.v());
					});
			} else {
				getTranferTime(impl, cid, c, CompensatoryOccurrenceDivision.FromOverTime).ifPresent(tranTime -> {
					remainInfor.get().setTransferOvertimesTotal(tranTime.v());
				});
			}
			scheRemainInfor.add(ScheRemainCreateInfor.toScheRemain(remainInfor.get()));
		}
		return scheRemainInfor;
	}
	
	private Optional<AttendanceTime> getTranferTime(RequireImpl impl, String cid, WorkSchedule c,
			CompensatoryOccurrenceDivision atr) {
		return GetSubHolOccurrenceSetting
				.process(impl, cid, c.getWorkInfo().getRecordInfo().getWorkTimeCodeNotNull().map(x -> x.v()), atr)
				.map(x -> x.getTransferTime(atr == CompensatoryOccurrenceDivision.FromOverTime
						? c.getOptAttendanceTime().get().getActualWorkingTimeOfDaily().getOverTime()
						: c.getOptAttendanceTime().get().getActualWorkingTimeOfDaily().getWorkHolidayTime()));
	}
	
	// 勤務予定から日別勤怠(Work)を作成する
	private IntegrationOfDaily toDailyDomain(WorkSchedule workSchedule) {
		return new IntegrationOfDaily(workSchedule.getEmployeeID(), workSchedule.getYmd(), workSchedule.getWorkInfo(),
				CalAttrOfDailyAttd.createAllCalculate(), workSchedule.getAffInfo(), 
				Optional.empty(),//pcLogOnInfo
				new ArrayList<>(),//employeeError
				workSchedule.getOutingTime(),
				workSchedule.getLstBreakTime(),
				workSchedule.getOptAttendanceTime(),
				workSchedule.getOptTimeLeaving(),//
				workSchedule.getOptSortTimeWork(), //
				Optional.empty(),//specDateAttr
				Optional.empty(),//attendanceLeavingGate
				Optional.empty(), //anyItemValue
				workSchedule.getLstEditState(), 
				Optional.empty(),//tempTime
				new ArrayList<>(),//remarks
				new ArrayList<>(),//ouenTime
				new ArrayList<>(),//ouenTimeSheet
				Optional.empty());//snapshot
	}

	@AllArgsConstructor
	public class RequireImpl implements CreateWorkRecordScheduleRemain.Require, WorkInformation.Require {
		String companyId;

		@Override
		public Optional<PredetemineTimeSetting> findByWorkTimeCode(String companyId, String workTimeCode) {
			return predetemineTimeSettingRepository.findByWorkTimeCode(companyId, workTimeCode);
		}

		@Override
		public DailyRecordToAttendanceItemConverter createDailyConverter() {
			return dailyRecordConverter.createDailyConverter();
		}

		@Override
		public List<IntegrationOfDaily> calculateForRecord(CalculateOption calcOption,
				List<IntegrationOfDaily> integrationOfDaily, Optional<ManagePerCompanySet> companySet,
				ExecutionType reCalcAtr) {
			return calculateDailyRecordServiceCenter.calculatePassCompanySetting(calcOption, integrationOfDaily, reCalcAtr);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String cid, String workTimeCode) {
			return workTimeSettingRepository.findByCode(cid, workTimeCode);
		}

		@Override
		public CompensatoryLeaveComSetting findCompensatoryLeaveComSet(String companyId) {
			return compensLeaveComSetRepository.find(companyId);
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
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			return predetemineTimeSettingRepository.findByWorkTimeCode(companyId,wktmCd.v()).get();
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}

		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			return service.checkNeededOfWorkTimeSetting(workTypeCode);
		}

	}
}
