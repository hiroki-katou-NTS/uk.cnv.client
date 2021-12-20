package nts.uk.ctx.at.shared.app.workrule.workinghours;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.worktime.ChangeableWorkingTimeZonePerNo.ClockAreaAtr;
import nts.uk.ctx.at.shared.dom.worktime.ChangeableWorkingTimeZonePerNo.ContainsResult;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingService;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * TODO:«Query» 時刻が不正かチェックする
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.就業時間帯.App.時刻が不正かチェックする
 * @author tutk
 *
 */
@Stateless
public class CheckTimeIsIncorrect {
	
	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Inject
	private WorkTimeSettingService workTimeSettingService;

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

	/**
	 * チェックする 
	 * @param workType 勤務種類: 勤務種類コード
	 * @param workTime  就業時間帯: 就業時間帯コード
	 * @param workTime1 , 勤務時間帯1: 時間帯(実装コードなし/使用不可)
	 * @param workTime2 勤務時間帯2: 時間帯(実装コードなし/使用不可)
	 */
	public List<ContainsResultDto> check(String workType, String workTime, TimeZoneDto workTime1,
			TimeZoneDto workTime2) {
		// 1:Create()
		WorkInformation wi = new WorkInformation(workType, workTime);
		WorkInformation.Require require = new WorkInformationImpl(workTypeRepo, workTimeSettingRepository,
				workTimeSettingService, basicScheduleService, fixedWorkSettingRepository, flowWorkSettingRepository,
				flexWorkSettingRepository, predetemineTimeSettingRepository);

		List<ContainsResultDto> listContainsResult = new ArrayList<>();
		// 2: 変更可能な勤務時間帯のチェック(Require, 対象時刻区分, 勤務NO, 時刻(日区分付き))
		ContainsResult containsResult1 = wi.containsOnChangeableWorkingTime(require, ClockAreaAtr.START, new WorkNo(1),
				new TimeWithDayAttr(workTime1.getStartTime().getTime()));
		listContainsResult.add(convertToContainsResult(containsResult1, TextResource.localize("KSU001_54"),
				convertToTime(workTime1.getStartTime().getTime()), true, InnerIndex.STARTTIME.value));

		// 3:変更可能な勤務時間帯のチェック(Require, 対象時刻区分, 勤務NO, 時刻(日区分付き))
		ContainsResult containsResult2 = wi.containsOnChangeableWorkingTime(require, ClockAreaAtr.END, new WorkNo(1),
				new TimeWithDayAttr(workTime1.getEndTime().getTime()));
		listContainsResult.add(convertToContainsResult(containsResult2, TextResource.localize("KSU001_55"),
				convertToTime(workTime1.getEndTime().getTime()), true, InnerIndex.ENDTIME.value));

		// 4:
		if (workTime2 != null) {
			// 4.1
			ContainsResult containsResult3 = wi.containsOnChangeableWorkingTime(require, ClockAreaAtr.START,
					new WorkNo(2), new TimeWithDayAttr(workTime2.getStartTime().getTime()));
			listContainsResult.add(convertToContainsResult(containsResult3, TextResource.localize("KSU001_56"),
					convertToTime(workTime2.getStartTime().getTime()), false, InnerIndex.STARTTIME.value));

			// 4.2
			ContainsResult containsResult4 = wi.containsOnChangeableWorkingTime(require, ClockAreaAtr.END,
					new WorkNo(2), new TimeWithDayAttr(workTime2.getEndTime().getTime()));
			listContainsResult.add(convertToContainsResult(containsResult4, TextResource.localize("KSU001_57"),
					convertToTime(workTime2.getEndTime().getTime()), false, InnerIndex.ENDTIME.value));
		}
		return listContainsResult;
	}
	
	private ContainsResultDto convertToContainsResult(ContainsResult containsResult,String nameError,String timeInput,boolean isWorkNo1, int innerIndex) {
		return new ContainsResultDto(containsResult.isContains(), 
				containsResult.getTimeSpan().isPresent()? 
						new TimeSpanForCalcSharedDto(containsResult.getTimeSpan().get().start(), containsResult.getTimeSpan().get().end()):null,
						nameError,timeInput,isWorkNo1,innerIndex
				);
	}
	
	private String convertToTime (Integer value) {
		if(value== null) {
			return "";
		}
		String result = value/60 +":"+(value%60 >=10?value%60:("0"+value%60));
		return result;
	}
	
	@AllArgsConstructor
	public static class WorkInformationImpl implements WorkInformation.Require {

		private final String companyId = AppContexts.user().companyId();

		@Inject
		private WorkTypeRepository workTypeRepo;

		@Inject
		private WorkTimeSettingRepository workTimeSettingRepository;

		@Inject
		private WorkTimeSettingService workTimeSettingService;

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
//		public PredetermineTimeSetForCalc getPredeterminedTimezone(String workTimeCd, String workTypeCd,
//				Integer workNo) {
//			return workTimeSettingService.getPredeterminedTimezone(companyId, workTimeCd, workTypeCd, workNo);
//		}

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

	}
}
