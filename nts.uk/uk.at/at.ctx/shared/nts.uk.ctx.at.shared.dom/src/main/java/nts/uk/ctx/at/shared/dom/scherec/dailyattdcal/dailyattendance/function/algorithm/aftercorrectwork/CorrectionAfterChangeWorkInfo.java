package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.aftercorrectwork;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.adapter.workplace.affiliate.SharedAffWorkplaceHistoryItemAdapter;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.BonusPaySettingCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.WorkingTimesheetCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPUnitUseSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.CPBonusPaySettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.WPBonusPaySettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.WTBonusPaySettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BPUnitUseSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.CompanyBonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.WorkingTimesheetBonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.WorkplaceBonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.aftercorrectatt.CorrectAddSalaryCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.aftercorrectwork.startendwork.CorrectStartEndWorkForWorkInfo;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
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
 * @author ThanhNX
 *
 *         勤務情報変更後の補正
 */
@Stateless
public class CorrectionAfterChangeWorkInfo {

	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepo;
	@Inject
	private FixedWorkSettingRepository fixedWorkSettingRepo;
	@Inject
	private FlowWorkSettingRepository flowWorkSettingRepo;
	@Inject
	private FlexWorkSettingRepository flexWorkSettingRepo;
	@Inject
	private WorkTypeRepository workTypeRepo;
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepo;
	@Inject
	private BasicScheduleService basicScheduleService;
	@Inject
	private TimeCorrectionProcess timeCorrectionProcess;
	@Inject
	private AttendanceItemConvertFactory attendanceItemConvertFactory;
	@Inject
	private BPUnitUseSettingRepository bpUnitUseSettingRepository;
	@Inject
	private WTBonusPaySettingRepository wtBonusPaySettingRepository;
	@Inject
	private BPSettingRepository bpSettingRepository;
	@Inject
	private SharedAffWorkplaceHistoryItemAdapter sharedAffWorkplaceHistoryItemAdapter;
	@Inject
	private WPBonusPaySettingRepository wpBonusPaySettingRepository;
	@Inject
	private CPBonusPaySettingRepository cpBonusPaySettingRepository;
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepo;
	@Inject
	private WorkingConditionRepository workingConditionRepo;

	public IntegrationOfDaily correction(String companyId, IntegrationOfDaily domainDaily,
			Optional<WorkingConditionItem> workCondition, ChangeDailyAttendance changeDailyAttendance) {
		
		val require = createRequire(companyId);
		
		/** 日別勤怠の何が変更されたか.勤務情報=true　＆＆　日別勤怠の何が変更されたか。勤務予定から移送した値も補正する＝True */
		if (changeDailyAttendance.workInfo && changeDailyAttendance.correctValCopyFromSche) {
			/** 始業終業時刻の補正 */
			domainDaily.setWorkInformation(CorrectStartEndWorkForWorkInfo.correctStartEndWork(require, domainDaily.getWorkInformation(), domainDaily.getEditState()));
		}
		
		//時刻の補正
		timeCorrectionProcess.process(companyId, workCondition, domainDaily, changeDailyAttendance.getClassification());
		
		/** 勤務回数の補正 */
		AttendanceTimesCorrector.correct(require, domainDaily);
		
		// fix 111738
		// remove TODO: ドメインモデル「予実反映」を取得 - mock new domain
		//  remove 予実反映処理の補正
		
		if(changeDailyAttendance.getClassification() == ScheduleRecordClassifi.RECORD) {
			CorrectAddSalaryCode.correct(require, companyId, domainDaily);
		}
		
		return domainDaily;
	}
	
	private Require createRequire(String companyId) {
		
		return new Require() {
			
			@Override
			public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
				return predetemineTimeSettingRepo.findByWorkTimeCode(companyId, wktmCd.v()).get();
			}
			
			@Override
			public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
				return flowWorkSettingRepo.find(companyId, code.v()).get();
			}
			
			@Override
			public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
				return flexWorkSettingRepo.find(companyId, code.v()).get();
			}
			
			@Override
			public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
				return fixedWorkSettingRepo.findByKey(companyId, code.v()).get();
			}
			
			@Override
			public Optional<WorkType> getWorkType(String workTypeCd) {
				return workTypeRepo.findByPK(companyId, workTypeCd);
			}
			
			@Override
			public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
				return workTimeSettingRepo.findByCode(companyId, workTimeCode);
			}
			
			@Override
			public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
				return basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
			}

			@Override
			public DailyRecordToAttendanceItemConverter createDailyConverter() {
				return attendanceItemConvertFactory.createDailyConverter();
			}

			@Override
			public Optional<BPUnitUseSetting> getSetting(String companyId) {
				return bpUnitUseSettingRepository.getSetting(companyId);
			}

			@Override
			public Optional<WorkingTimesheetBonusPaySetting> getWTBPSetting(String companyId,
					WorkingTimesheetCode workingTimesheetCode) {
				return wtBonusPaySettingRepository.getWTBPSetting(companyId, workingTimesheetCode);
			}

			@Override
			public Optional<BonusPaySetting> getBonusPaySetting(String companyId,
					BonusPaySettingCode bonusPaySettingCode) {
				return bpSettingRepository.getBonusPaySetting(companyId, bonusPaySettingCode);
			}

			@Override
			public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId) {
				return sharedAffWorkplaceHistoryItemAdapter.getWorkplaceIdAndUpper(companyId, baseDate, workplaceId);
			}

			@Override
			public Optional<CompanyBonusPaySetting> getSettingCom(String companyId) {
				return cpBonusPaySettingRepository.getSetting(companyId);
			}

			@Override
			public Optional<WorkingConditionItem> workingConditionItem(String historyId) {
				return workingConditionItemRepo.getByHistoryId(historyId);
			}

			@Override
			public Optional<WorkingCondition> workingCondition(String companyId, String employeeId,
					GeneralDate baseDate) {
				return workingConditionRepo.getBySidAndStandardDate(companyId, employeeId, baseDate);
			}

			@Override
			public Optional<WorkplaceBonusPaySetting> getWPBPSetting(String companyId, WorkplaceId wpl) {
				return wpBonusPaySettingRepository.getWPBPSetting(companyId, wpl);
			}

		};
	}

	public static interface Require extends CorrectStartEndWorkForWorkInfo.Require, AttendanceTimesCorrector.Require, CorrectAddSalaryCode.Require {
		
	}
}
