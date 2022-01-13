package nts.uk.screen.at.app.kdl045.query;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.WorkTimezoneCommonSetDto;
import nts.uk.ctx.at.shared.app.find.worktime.flexset.dto.FlexWorkSettingDto;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.workrule.BreakTimeZone;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.getcommonset.GetCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeForm;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingService;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.internal.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * «ScreenQuery» 詳細情報を取得する
 * @author tutk
 *
 */
@Stateless
public class GetMoreInformation {
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	
	@Inject 
	private RecordDomRequireService requireService;
	
	@Inject
	private WorkTypeRepository workTypeRepo;

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
	
	public GetMoreInformationOutput getMoreInformation(String employeeId,String workType,String workTimeCode) {
		
		String companyId = AppContexts.user().companyId();
		GetMoreInformationOutput data =new GetMoreInformationOutput();
		val require = requireService.createRequire();
		WorkInformation.Require requireWorkInfo = new WorkInformationImpl(workTypeRepo, workTimeSettingRepository,
				workTimeSettingService, basicScheduleService,fixedWorkSettingRepository,flowWorkSettingRepository,flexWorkSettingRepository,predetemineTimeSettingRepository);
		
		WorkInformation wi = new WorkInformation(workType, workTimeCode);
		//1 
		Optional<WorkStyle> workStyle =  wi.getWorkStyle(requireWorkInfo);
		Integer style = workStyle.isPresent()?workStyle.get().value: null;
		data.setWorkStyle(style);
		if(workStyle.isPresent() && workStyle.get() != WorkStyle.ONE_DAY_REST) {
			//2.1 :休憩時間帯を取得する (require: Require): Optional<休憩時間>
			Optional<BreakTimeZone> optBreakTimeZone = wi.getBreakTimeZone(requireWorkInfo);
			if(optBreakTimeZone.isPresent()) {
				data.setBreakTime(BreakTimeKdl045Dto.convertToBreakTimeZone(optBreakTimeZone.get()));
			}
			
			//2.2 : 共通設定の取得
			Optional<WorkTimezoneCommonSet> optWorktimezone = GetCommonSet.workTimezoneCommonSet(require, companyId, workTimeCode);
			WorkTimezoneCommonSetDto workTimezoneCommonSetDto = new WorkTimezoneCommonSetDto(); 
			if(optWorktimezone.isPresent()) {
				optWorktimezone.get().saveToMemento(workTimezoneCommonSetDto);
				data.setWorkTimezoneCommonSet(workTimezoneCommonSetDto);
			}
		}
		
		//就業時間帯コード<>NULL
		if(workTimeCode != null) {
			//3.1:就業時間帯の設定を取得する
			//emptyの運用はあり得ないため - QA : 112354
			Optional<WorkTimeSetting> workTimeSetting = workTimeSettingRepository.findByCode(companyId, workTimeCode);
			if(workTimeSetting.isPresent()) {
				//3.2:call()
				data.setWorkTimeForm(workTimeSetting.get().getWorkTimeDivision().getWorkTimeForm().value);
				data.setWorkTimeSettingName(new WorkTimeSettingNameDto(
						workTimeSetting.get().getWorkTimeDisplayName().getWorkTimeName().v(),
						workTimeSetting.get().getWorkTimeDisplayName().getWorkTimeAbName().v()));
				if(workTimeSetting.get().getWorkTimeDivision().getWorkTimeForm() == WorkTimeForm.FLEX) {
					//3.3
					Optional<FlexWorkSetting> dataFlex = flexWorkSettingRepository.find(companyId, workTimeCode);
					if (dataFlex.isPresent()) {
						FlexWorkSettingDto dto = new FlexWorkSettingDto();
						dataFlex.get().saveToMemento(dto);
						data.setCoreTimeFlexSetting(dto.getCoreTimeSetting());
					}
					
				}
			}
		}

		//4 : 勤務種類を取得する
		Optional<WorkType> workTypeOpt = workTypeRepo.findByPK(companyId, workType);
		data.setWorkTypeSettingName(new WorkTypeSettingNameDto(workTypeOpt.get().getName().v(), workTypeOpt.get().getAbbreviationName().v()));
		
		return data;
		
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
