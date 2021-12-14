package nts.uk.ctx.at.request.dom.application.common.ovetimeholiday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.UseAtr;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SWkpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.dailyattendancetime.DailyAttendanceTimeCaculation;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.dailyattendancetime.DailyAttendanceTimeCaculationImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.shift.businesscalendar.specificdate.WpSpecificDateSettingAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.shift.businesscalendar.specificdate.dto.WpSpecificDateSettingImport;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.AgreementTimeService;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.other.Time36UpperLimitCheck;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.OverTimeWorkHoursOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CalculatedFlag;
import nts.uk.ctx.at.request.dom.application.overtime.ApplicationTime;
import nts.uk.ctx.at.request.dom.application.overtime.AttendanceType_Update;
import nts.uk.ctx.at.request.dom.application.overtime.HolidayMidNightTime;
import nts.uk.ctx.at.request.dom.application.overtime.OverTimeShiftNight;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeApplicationSetting;
import nts.uk.ctx.at.request.dom.application.overtime.service.WeekdayHolidayClassification;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.AppDateContradictionAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.Time36AgreeCheckRegister;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.employmentrules.employmenttimezone.BreakTimeZoneService;
import nts.uk.ctx.at.shared.dom.employmentrules.employmenttimezone.BreakTimeZoneSharedOutPut;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPTimeItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BonusPayTimeItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.DuplicateStateAtr;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.DuplicationStatusOfTimeZone;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.RangeOfDayTimeZoneService;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.ctx.at.shared.dom.worktype.DailyWork;
import nts.uk.ctx.at.shared.dom.worktype.HolidayAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;



@Stateless
public class CommonOvertimeHolidayImpl implements CommonOvertimeHoliday {

	final static String DATE_FORMAT = "yyyy/MM/dd";

	@Inject
	private BasicScheduleService basicService;

	@Inject
	private BreakTimeZoneService timeService;

	@Inject
	private WorkTypeRepository workTypeRepository;

	@Inject
	private AgreementTimeService agreementTimeService;

	@Inject
	private EmployeeRequestAdapter employeeAdapter;

	@Inject
	private WpSpecificDateSettingAdapter wpSpecificDateSettingAdapter;

	@Inject
	private BPTimeItemRepository bPTimeItemRepository;

	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;

	@Inject
	private Time36UpperLimitCheck time36UpperLimitCheck;
	
	@Inject
	private DailyAttendanceTimeCaculation dailyAttendanceTimeCaculation;

	@Inject
	public RangeOfDayTimeZoneService rangeOfDayTimeZoneService;

	@Override
	// 休憩時間帯を取得する
	public List<DeductionTime> getBreakTimes(String companyID, String workTypeCode, String workTimeCode, 
			Optional<TimeWithDayAttr> opStartTime, Optional<TimeWithDayAttr> opEndTime) {
		List<DeductionTime> result = new ArrayList<>();
		// 1日半日出勤・1日休日系の判定
		WorkStyle workStyle = this.basicService.checkWorkDay(companyID, workTypeCode);
		// 平日か休日か判断する
		WeekdayHolidayClassification weekDay = checkHolidayOrNot(workTypeCode);
		// 休憩時間帯の取得
		BreakTimeZoneSharedOutPut breakTimeZoneSharedOutPut = this.timeService.getBreakTimeZone(companyID, workTimeCode,
				weekDay.value, workStyle);
		Collections.sort(breakTimeZoneSharedOutPut.getLstTimezone(), new Comparator<DeductionTime>() {
			@Override
			public int compare(DeductionTime o1, DeductionTime o2) {
				return o1.getStart().v().compareTo(o2.getStart().v());
			}
		});
		// Input．開始時刻とInput．終了時刻をチェック
		if(!opStartTime.isPresent() || !opEndTime.isPresent()){
			return breakTimeZoneSharedOutPut.getLstTimezone();
		}
		for(DeductionTime deductionTime : breakTimeZoneSharedOutPut.getLstTimezone()){
			// 状態区分　＝　「重複の判断処理」を実行
			TimeWithDayAttr startTime = opStartTime.get();
			TimeWithDayAttr endTime = opEndTime.get();
			TimeSpanForCalc timeSpanFirstTime = new TimeSpanForCalc(endTime, startTime);
			TimeSpanForCalc timeSpanSecondTime = new TimeSpanForCalc(deductionTime.getEnd(), deductionTime.getStart());
			// アルゴリズム「時刻入力期間重複チェック」を実行する
			DuplicateStateAtr duplicateStateAtr = this.rangeOfDayTimeZoneService
					.checkPeriodDuplication(timeSpanFirstTime, timeSpanSecondTime);
			// 重複状態区分チェック
			DuplicationStatusOfTimeZone duplicationStatusOfTimeZone = this.rangeOfDayTimeZoneService
					.checkStateAtr(duplicateStateAtr);
			// 取得した状態区分をチェック
			if(duplicationStatusOfTimeZone != DuplicationStatusOfTimeZone.NON_OVERLAPPING){
				result.add(deductionTime);
			}
		}
		return result;
	}

	private WeekdayHolidayClassification checkHolidayOrNot(String workTypeCd) {
		String companyId = AppContexts.user().companyId();
		Optional<WorkType> WorkTypeOptional = this.workTypeRepository.findByPK(companyId, workTypeCd);
		if (!WorkTypeOptional.isPresent()) {
			return WeekdayHolidayClassification.WEEKDAY;
		}
		// check null?
		WorkType workType = WorkTypeOptional.get();
		DailyWork dailyWork = workType.getDailyWork();
		WorkTypeClassification oneDay = dailyWork.getOneDay();
		// 休日出勤
		if (oneDay.value == 11) {
			return WeekdayHolidayClassification.HOLIDAY;
		}
		return WeekdayHolidayClassification.WEEKDAY;
	}

	@Override
	public Optional<OverTimeWorkHoursOutput> getAgreementTime(
			String companyID,
			String employeeID,
			Time36AgreeCheckRegister extratimeExcessAtr,
			NotUseAtr extratimeDisplayAtr) {
		Optional<OverTimeWorkHoursOutput> opAgreeOverTimeOutput = Optional.empty();
		// ノートのif文を
		if (!(extratimeExcessAtr == Time36AgreeCheckRegister.NOT_CHECK && extratimeDisplayAtr == NotUseAtr.NOT_USE)) {
			// ３６時間の表示
			opAgreeOverTimeOutput = Optional.of(agreementTimeService.getOverTimeWorkHoursOutput(companyID, employeeID));
		}
		return opAgreeOverTimeOutput;
	}

	@Override
	public List<BonusPayTimeItem> getBonusTime(String companyID, String employeeID, GeneralDate appDate,
			UseAtr bonusTimeDisplayAtr) {
		List<BonusPayTimeItem> result = new ArrayList<>();
		WpSpecificDateSettingImport wpSpecificDateSettingImport = new WpSpecificDateSettingImport(null, null);
		if (bonusTimeDisplayAtr == UseAtr.USE) {
			// アルゴリズム「社員所属職場履歴を取得」を実行する
			SWkpHistImport sWkpHistImport = employeeAdapter.getSWkpHistByEmployeeID(employeeID, appDate);
			// アルゴリズム「職場の特定日設定を取得する」を実行する
			if (sWkpHistImport != null) {
				wpSpecificDateSettingImport = this.wpSpecificDateSettingAdapter
						.workplaceSpecificDateSettingService(companyID, sWkpHistImport.getWorkplaceId(), appDate);
			}

			List<BonusPayTimeItem> bonusPayTimeItems = this.bPTimeItemRepository
					.getListBonusPayTimeItemInUse(companyID);
			for (BonusPayTimeItem bonusItem : bonusPayTimeItems) {
				result.add(bonusItem);
			}
			if (!CollectionUtil.isEmpty(wpSpecificDateSettingImport.getNumberList())) {
				List<BonusPayTimeItem> bonusPayTimeItemSpecs = this.bPTimeItemRepository
						.getListSpecialBonusPayTimeItemInUse(companyID);
				for (BonusPayTimeItem bonusItem : bonusPayTimeItemSpecs) {
					result.add(bonusItem);
				}
			}
		}
		return result;
	}

	@Override
	public boolean displayDivergenceReasonInput(PrePostAtr prePostAtr, UseAtr divergenceReasonInputAtr) {
		// Input．乖離理由入力区分チェック
		if (divergenceReasonInputAtr == UseAtr.USE) {
			return true;
		}
		return false;
	}



	

	@Override
	public boolean getRestTime(String companyID, UseAtr timeCalUse, Boolean breakInputFieldDisp,
			ApplicationType appType) {
		// 時刻計算利用チェック
		if (timeCalUse == UseAtr.NOTUSE) {
			return false;
		}
		// 休憩入力欄を表示するをチェックする
		return breakInputFieldDisp;
	}

	@Override
	public List<ConfirmMsgOutput> inconsistencyCheck(String companyID, String employeeID, GeneralDate appDate,
			ApplicationType appType, AppDateContradictionAtr appDateContradictionAtr) {
		// Input．申請日矛盾区分をチェックする
		if (appDateContradictionAtr == AppDateContradictionAtr.NOTCHECK) {
			return Collections.emptyList();
		}
		WorkType workType = otherCommonAlgorithm.getWorkTypeScheduleSpec(companyID, employeeID, appDate);
		if (workType == null) {
			// 「申請日矛盾区分」をチェックする
			if (appDateContradictionAtr == AppDateContradictionAtr.CHECKNOTREGISTER) {
				throw new BusinessException("Msg_1519", appDate.toString("yyyy/MM/dd"));
			}
			return Arrays.asList(new ConfirmMsgOutput("Msg_1520", Arrays.asList(appDate.toString("yyyy/MM/dd"))));
		}
		boolean checked = false;
		// Input．申請種類をチェック
		if (appType == ApplicationType.OVER_TIME_APPLICATION) {
			// アルゴリズム「03-08_01 残業申請の勤務種類矛盾チェック」を実行する
			checked = this.checkOverTime(workType);
		} else {
			// 03-08_01 休日出勤の勤務種類矛盾チェック
			checked = this.workTypeInconsistencyCheck(workType);
		}
		if (!checked) {
			return Collections.emptyList();
		}
		String name = workType.getName().v();
		// 「申請日矛盾区分」をチェックする
		if (appDateContradictionAtr == AppDateContradictionAtr.CHECKNOTREGISTER) {
			throw new BusinessException("Msg_1521", appDate.toString("yyyy/MM/dd"),
					Strings.isNotBlank(name) ? name : "未登録のマスタ");
		}
		return Arrays.asList(new ConfirmMsgOutput("Msg_1522", Arrays.asList(appDate.toString("yyyy/MM/dd"), Strings.isNotBlank(name) ? name : "未登録のマスタ") ));

	}

	/**
	 * 03-08_01 残業申請の勤務種類矛盾チェック
	 * 
	 * @param workType
	 * @return
	 */
	private boolean checkOverTime(WorkType workType) {
		boolean error = false;
		// INPUT.ドメインモデル「勤務種類.勤務の単位(WORK_ATR)」をチェックする
		if (workType.getDailyWork().getWorkTypeUnit().equals(WorkTypeUnit.MonringAndAfternoon)) {
			// INPUT.ドメインモデル「勤務種類.午前の勤務分類(MORNING_CLS)」をチェックする
			int wkMorning = workType.getDailyWork().getMorning().value;
			// INPUT.ドメインモデル「勤務種類.午後の勤務分類(AFTERNOON_CLS)」をチェックする
			int wkAfternoon = workType.getDailyWork().getAfternoon().value;
			List<Integer> holidayTypes = Arrays.asList(1, 2, 3, 4, 5, 6, 8, 9, 11);
			boolean morningIsHoliday = holidayTypes.indexOf(wkMorning) != -1;
			boolean afternoonIsHoliday = holidayTypes.indexOf(wkAfternoon) != -1;
			if (morningIsHoliday && afternoonIsHoliday) {
				error = true;
			} else {
				error = false;
			}
		} else {
			// INPUT.ドメインモデル「勤務種類.1日勤務分類(ONE_DAY_CLS)」をチェックする
			WorkTypeClassification workTypeClassification = workType.getDailyWork().getOneDay();
			if (workTypeClassification.equals(WorkTypeClassification.Attendance)
					|| workTypeClassification.equals(WorkTypeClassification.Shooting)) {
				error = false;
			} else {
				error = true;
			}
		}
		return error;
	}

	/**
	 * 03-08_01 休日出勤の勤務種類矛盾チェック
	 * 
	 * @param workType
	 * @return 矛盾なし = false or 矛盾あり = true
	 */
	private boolean workTypeInconsistencyCheck(WorkType workType) {
		// INPUT.ドメインモデル「勤務種類.勤務の単位(WORK_ATR)」が１日であるかをチェックする
		if (workType.getDailyWork().getWorkTypeUnit() == WorkTypeUnit.MonringAndAfternoon) {
			return false;
		}
		// INPUT.ドメインモデル「勤務種類.1日勤務分類(ONE_DAY_CLS)」をチェックする
		WorkTypeClassification workTypeClassification = workType.getDailyWork().getOneDay();
		if (workTypeClassification == WorkTypeClassification.Holiday
				|| workTypeClassification == WorkTypeClassification.Pause
				|| workTypeClassification == WorkTypeClassification.HolidayWork) {
			return false;
		}
		return true;
	}


	/**
	 * 03-06_計算ボタンチェック
	 */
	@Override
	public void calculateButtonCheck(CalculatedFlag calculateFlg, UseAtr timeCalUse) {
		// 申請詳細設定.時刻計算利用区分=利用する
		if (timeCalUse != UseAtr.USE) {
			return;
		}
		// 計算フラグのチェック
		if (calculateFlg == CalculatedFlag.UNCALCULATED) {
			// 計算フラグ=1の場合:メッセージを表示する(Msg_750)
			throw new BusinessException("Msg_750");
		}
	}

	@Override
	public UseAtr preAppSetCheck(PrePostAtr prePostAtr, UseAtr preExcessDisplaySetting) {
		// Input．事前事後区分をチェック
		if(prePostAtr == PrePostAtr.PREDICT){
			return UseAtr.NOTUSE;
		}
		// Input．事前超過表示設定をチェック
		return preExcessDisplaySetting;
	}

	@Override
	public AppDateContradictionAtr actualSetCheck(AppDateContradictionAtr performanceExcessAtr, PrePostAtr prePostAtr) {
		// Input．事前事後区分チェック
		if(prePostAtr == PrePostAtr.PREDICT){
			return AppDateContradictionAtr.NOTCHECK;
		}
		// Input．実績超過区分チェック
		return performanceExcessAtr;
	}

	
	

	@Override
	public List<ApplicationTime> calculator(
			String companyId,
			String employeeId,
			GeneralDate date,
			Optional<String> workTypeCode,
			Optional<String> workTimeCode,
			List<TimeZone> timeZones,
			List<BreakTimeSheet> breakTimes) {
		Map<Integer, TimeZone> timeZoneMap = new HashMap<>();
		for (int i = 0; i < timeZones.size(); i++) {
			timeZoneMap.put(i + 1, timeZones.get(i));
		}
		// 1日分の勤怠時間を仮計算 (RQ23)
		List<ApplicationTime> output = new ArrayList<>();
		ApplicationTime applicationTime = new ApplicationTime();
		DailyAttendanceTimeCaculationImport dailyAttendanceTimeCaculationImport = dailyAttendanceTimeCaculation.getCalculation(
				employeeId,
				date,
				workTypeCode.orElse(null),
				workTimeCode.orElse(null),
				timeZoneMap,
				breakTimes.stream().map(x -> x.getStartTime().v()).collect(Collectors.toList()),
				breakTimes.stream().map(x -> x.getEndTime().v()).collect(Collectors.toList()),
				Collections.emptyList(),
				Collections.emptyList());
		// 「申請時間」をセットして返す
		
		List<OvertimeApplicationSetting> overtimeApplicationSetting = new ArrayList<OvertimeApplicationSetting>();
		
		List<OvertimeApplicationSetting> overTimes = dailyAttendanceTimeCaculationImport.getOverTime()
																   .entrySet()
																   .stream()
																   .map(x -> x.getValue().getCalTime() > 0  ? new OvertimeApplicationSetting(
																									   x.getKey(),
																									   AttendanceType_Update.NORMALOVERTIME,
																									   x.getValue().getCalTime())
																		   		: null )
																   .filter(y -> y != null)
																   .collect(Collectors.toList());

		overtimeApplicationSetting.addAll(overTimes);
		
		List<OvertimeApplicationSetting> holidayTimes = dailyAttendanceTimeCaculationImport.getHolidayWorkTime()
																   .entrySet()
																   .stream()
																   .map(x -> x.getValue().getCalTime() > 0 ? new OvertimeApplicationSetting(
																									   x.getKey(),
																									   AttendanceType_Update.BREAKTIME,
																									   x.getValue().getCalTime())
																		   		: null )
																   .filter(y -> y != null)
																   .collect(Collectors.toList());
		overtimeApplicationSetting.addAll(holidayTimes);
		
		
		List<OvertimeApplicationSetting> bonusPayTimes = dailyAttendanceTimeCaculationImport.getBonusPayTime()
				   .entrySet()
				   .stream()
				   .map(x -> x.getValue() > 0 ? new OvertimeApplicationSetting(
								   x.getKey(),
								   AttendanceType_Update.BONUSPAYTIME,
								   x.getValue())
						   : null
						   		)
				   .filter(y -> y != null)
				   .collect(Collectors.toList());
		
		overtimeApplicationSetting.addAll(bonusPayTimes);
		
		List<OvertimeApplicationSetting> specBonusPayTimes = dailyAttendanceTimeCaculationImport.getSpecBonusPayTime()
				   .entrySet()
				   .stream()
				   .map(x -> x.getValue() > 0 ? new OvertimeApplicationSetting(
								   x.getKey(),
								   AttendanceType_Update.BONUSSPECIALDAYTIME,
								   x.getValue())
						   : null
						   		)
				   .filter(y -> y != null)
				   .collect(Collectors.toList());
		
		overtimeApplicationSetting.addAll(specBonusPayTimes);
		
		applicationTime.setApplicationTime(overtimeApplicationSetting);
		
		
		
		applicationTime.setFlexOverTime(Optional.of(new AttendanceTimeOfExistMinus(dailyAttendanceTimeCaculationImport.getFlexTime().getCalTime())));
		
		
		OverTimeShiftNight overTimeShiftNight = new OverTimeShiftNight();
		
		overTimeShiftNight.setMidNightOutSide(dailyAttendanceTimeCaculationImport.getTimeOutSideMidnight());
		overTimeShiftNight.setOverTimeMidNight(dailyAttendanceTimeCaculationImport.getCalOvertimeMidnight());
		
		List<HolidayMidNightTime> midNightHolidayTimes = dailyAttendanceTimeCaculationImport.getCalHolidayMidnight()
										   .entrySet()
										   .stream()
										   .map(x -> new HolidayMidNightTime(
												   x.getValue(),
												   StaturoryAtrOfHolidayWork.deicisionAtrByHolidayAtr(EnumAdaptor.valueOf(x.getKey(), HolidayAtr.class))))
										   .collect(Collectors.toList());
		
		overTimeShiftNight.setMidNightHolidayTimes(midNightHolidayTimes);
		applicationTime.setOverTimeShiftNight(Optional.of(overTimeShiftNight));
		
		
		
		output.add(applicationTime);
		return output;
	}
}
