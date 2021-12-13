package nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.CountNumberOfPeopleByEachWorkMethodService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.NumberOfPeopleByEachWorkMethod;
import nts.uk.ctx.at.shared.app.find.workrule.shiftmaster.WorkTimeSettingDto;
import nts.uk.ctx.at.shared.app.query.worktime.GetWorkingHoursInformationQuery;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterCode;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;
import nts.uk.shr.com.context.AppContexts;


/**
 * 勤務方法ごとに人数を集計する
 * @author hoangnd
 *
 */
@Stateless 
public class ScreenQueryAggregatePeopleMethod {
	
	@Inject
	private GetWorkingHoursInformationQuery getWorkingHoursInformationQuery;
	
	@Inject
	private ShiftMasterRepository shiftMasterRepository;
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	/**
	 * 
	 * @param targetOrg
	 * @param period
	 * @param scheduleList
	 * @param actualList
	 * @param isShiftDisplay
	 * @return Map<年月日, List<勤務方法別の人数<T>>>
			        ※Tはそれぞれコードから取得した名称に置き換える
	 */
	public Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<WorkInfo>>> get(
			TargetOrgIdenInfor targetOrg,
			DatePeriod period,
			List<IntegrationOfDaily> scheduleList,
			List<IntegrationOfDaily> actualList,
			boolean isShiftDisplay
			) {
		Require require = new Require(shiftMasterRepository, workTypeRepository);
		
		String companyId = AppContexts.user().companyId();
		// 1: シフト表示か == false
		if (!isShiftDisplay) {
			// 1.1: 就業時間帯別人数を取得する(Require, 対象組織識別情報, 期間, List<日別勤怠(Work)>, List<日別勤怠(Work)>)
			Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<WorkTimeCode>>> countWork = 
						CountNumberOfPeopleByEachWorkMethodService.getByWorkTime(
								require,
								targetOrg,
								period,
								scheduleList,
								actualList);
			
			// 1.2: 就業時間帯情報リストを取得する(会社ID, List<就業時間帯コード>)
			List<WorkTimeSettingDto> workTimeSetting =
					getWorkingHoursInformationQuery.getListWorkTimeSetting(
							companyId,
							countWork.entrySet()
									 .stream()
									 .map(x -> x.getValue())
									 .flatMap(x -> x.stream())
									 .map(x -> x.getWorkMethod() == null ? null : x.getWorkMethod().toString())
									 .distinct()
									 .collect(Collectors.toList()))
					.stream()
					.map(WorkTimeSettingDto::new)
					.collect(Collectors.toList())
					;
			
			Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<WorkInfo>>> countWorkOutput = 
					countWork.entrySet()
							 .stream()
							 .collect(Collectors.toMap(
									 e -> e.getKey(),
									 
									 e -> e.getValue().stream()
													 .map(x -> new NumberOfPeopleByEachWorkMethod<WorkInfo>(
															 workTimeSetting.stream()
																	 .filter(y -> y.getWorkTimeCode().equals(x.getWorkMethod() == null ? null : x.getWorkMethod().toString()))
																	 .findFirst()
																	 .map(y -> new WorkInfo(y.getWorkTimeCode(), y.getWorkTimeName()))
																	 .orElse( new WorkInfo(x.getWorkMethod() == null ? null : x.getWorkMethod().toString(), null)),
															 x.getPlanNumber(),
															 x.getScheduleNumber(),
															 x.getActualNumber()))
													 .collect(Collectors.toList())
									 
							 ));
			return countWorkOutput;
			
		} else { // 2: シフト表示か == true
			
			// 2.1 シフト別人数を取得する(Require, 対象組織識別情報, 期間, List<日別勤怠(Work)>, List<日別勤怠(Work)>)
			Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<ShiftMasterCode>>> shift = 
					CountNumberOfPeopleByEachWorkMethodService.getByShift(
							require,
							targetOrg,
							period,
							scheduleList,
							actualList);
			
			// 2.2: *get
			List<ShiftMasterDto> shirftMasters = 
					shiftMasterRepository.getByListShiftMaterCd2(
							companyId,
							shift.entrySet()
							 .stream()
							 .map(x -> x.getValue())
							 .flatMap(x -> x.stream())
							 .map(x -> x.getWorkMethod().v())
							 .distinct()
							 .collect(Collectors.toList()))
					.stream()
					.map(ShiftMasterDto::new)
					.collect(Collectors.toList());
					
			
			Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<WorkInfo>>> shiftOutput =
				shift.entrySet()
					.stream()
					.collect(Collectors.toMap(
							e -> e.getKey(),
							e -> e.getValue().stream()
											 .map(x -> new NumberOfPeopleByEachWorkMethod<WorkInfo>(
													 shirftMasters.stream()
															 .filter(y -> y.shiftMasterCode.equals(x.getWorkMethod().v()))
															 .findFirst()
															 .map(y -> new WorkInfo(y.shiftMasterCode, y.getShiftMasterName()))
															 .orElse( new WorkInfo(x.getWorkMethod().v(), null)),
													 x.getPlanNumber(),
													 x.getScheduleNumber(),
													 x.getActualNumber()))
											 .collect(Collectors.toList())
					 ));
			
			
			return shiftOutput;
			
		}
		
				
		
	}
	
	@AllArgsConstructor
	private static class Require implements CountNumberOfPeopleByEachWorkMethodService.Require {
		
		@Inject
		private ShiftMasterRepository shiftMasterRepository;
		
		@Inject
		private WorkTypeRepository workTypeRepository;
		@Override
		public Optional<ShiftMaster> getShiftMaster(WorkInformation workInformation) {
			return shiftMasterRepository.getByWorkTypeAndWorkTime(
					AppContexts.user().companyId(),
					workInformation.getWorkTypeCode().v(),
					workInformation.getWorkTimeCodeNotNull().map(x -> x.v()).orElse(null));
		}

		@Override
		public List<WorkType> getWorkTypes(List<WorkTypeCode> workTypeCodes) {
			return workTypeRepository.getPossibleWorkType(
						AppContexts.user().companyId()
					,	workTypeCodes.stream()
									.map(c -> c.v())
									.collect(Collectors.toList())
						);
		}
		
	}
}
