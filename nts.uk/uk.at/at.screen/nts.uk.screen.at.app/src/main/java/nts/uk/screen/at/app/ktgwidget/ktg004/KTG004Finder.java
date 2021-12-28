
package nts.uk.screen.at.app.ktgwidget.ktg004;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.function.dom.employmentfunction.checksdailyerror.ChecksDailyPerformanceErrorRepository;
import nts.uk.ctx.at.record.pub.monthly.GetMonthlyRecordPub;
import nts.uk.ctx.at.record.pub.monthly.MonthlyRecordValuesExport;
import nts.uk.ctx.at.request.dom.application.appabsence.service.AbsenceServiceProcess;
import nts.uk.ctx.at.request.dom.application.appabsence.service.CheckDispHolidayType;
import nts.uk.ctx.at.request.dom.application.appabsence.service.NumberOfRemainOutput;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.sys.auth.pub.role.RoleExportRepo;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.ApproveWidgetRepository;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.DetailedWorkStatusSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.StandardWidget;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.StandardWidgetType;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.TopPageDisplayYearMonthEnum;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.WorkStatusItem;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG004_勤務状況.B：設定ダイアログ.ユースケース.起動する.起動する
 * @author thanhPV
 */
@Stateless
public class KTG004Finder {

	@Inject
	private ApproveWidgetRepository approveWidgetRepo;
	
	@Inject
	private SpecialHolidayRepository specialHolidayRepository;
	
	@Inject 
	private ClosureRepository closureRepo;
	
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;
	
	@Inject
	private RoleExportRepo roleExportRepo;
	
	@Inject
	private ChecksDailyPerformanceErrorRepository checksDailyPerformanceErrorRepo;
	
	@Inject
	private GetTheNumberOfVacationsLeft getTheNumberOfVacationsLeft;
	
	@Inject
	private GetMonthlyRecordPub getMonthlyRecordPub;
	
	@Inject
	private AbsenceServiceProcess absenceServiceProcess;
	
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;
	
	/** 起動する */
	public WorkStatusSettingDto getApprovedDataWidgetStart() {
		
		String companyId = AppContexts.user().companyId();
		
		List<ItemsSettingDto> itemsSetting = new ArrayList<>();
	
		//指定するウィジェットの設定を取得する(Get the settings of the specified widget)
		Optional<StandardWidget> standardWidget = approveWidgetRepo.findByWidgetTypeAndCompanyId(StandardWidgetType.WORK_STATUS, companyId);
		
		//ドメインモデル「特別休暇」を取得する(Get the domain model "special leave")
		List<SpecialHoliday> specialHolidays = specialHolidayRepository.findByCompanyId(companyId);
		
		if(standardWidget.isPresent()) {
			for (DetailedWorkStatusSetting item : standardWidget.get().getDetailedWorkStatusSettingList()) {
				if(item.getItem().value > WorkStatusItem.HDSP20_DISPLAY_ATR.value) {
					itemsSetting.add(new ItemsSettingDto(item, ""));
				}else {
					Optional<SpecialHoliday> sphl = specialHolidays.stream().filter(c -> item.getItem().value == c.getSpecialHolidayCode().v()).findAny();
					if(sphl.isPresent()) {
						itemsSetting.add(new ItemsSettingDto(new DetailedWorkStatusSetting(item.getDisplayType(), EnumAdaptor.valueOf(item.getItem().value, WorkStatusItem.class)), sphl.get().getSpecialHolidayName().v()));
					}
				}
			}
			return new WorkStatusSettingDto(itemsSetting, standardWidget.get().getName().v());
		}else {
//			2021/02/18　EA3960
//			設定がない場合、初期値を登録する
			for (int i = WorkStatusItem.DAY_ERR_DISPLAY_ATR.value; i <= WorkStatusItem.CARE_DISPLAY_ATR.value; i++) {
				
				NotUseAtr use = (i == WorkStatusItem.DAY_ERR_DISPLAY_ATR.value || i == WorkStatusItem.OVERTIME_DISPLAY_ATR.value || 
					i == WorkStatusItem.FLEX_DISPLAY_ATR.value || i == WorkStatusItem.HDTIME_DISPLAY_ATR.value || 
					i == WorkStatusItem.LATECOUNT_DISPLAY_ATR.value || i == WorkStatusItem.HDPAID_DISPLAY_ATR.value) ? NotUseAtr.USE : NotUseAtr.NOT_USE ;
				itemsSetting.add(new ItemsSettingDto(new DetailedWorkStatusSetting(use, EnumAdaptor.valueOf(i, WorkStatusItem.class)), ""));
			}
			for (SpecialHoliday specialHoliday : specialHolidays) {
				itemsSetting.add(new ItemsSettingDto(new DetailedWorkStatusSetting(NotUseAtr.NOT_USE, EnumAdaptor.valueOf(specialHoliday.getSpecialHolidayCode().v(), WorkStatusItem.class)), specialHoliday.getSpecialHolidayName().v()));
			}
			return new WorkStatusSettingDto(itemsSetting, I18NText.getText("KTG004_25"));
		}
	}
	
	//勤務状況を起動する
	public AcquisitionResultOfWorkStatusOutput startWorkStatus(String cid, String employeeId, KTG004DisplayYearMonthParam param) {
		TopPageDisplayYearMonthEnum topPageYearMonthEnum = EnumAdaptor.valueOf(param.getDisplayYearMonth(), TopPageDisplayYearMonthEnum.class);
		AcquisitionResultOfWorkStatusOutput result = new AcquisitionResultOfWorkStatusOutput(); 
		
		//Get the settings of the specified widget - 指定するウィジェットの設定を取得する 
		WorkStatusSettingDto setting = this.getApprovedDataWidgetStart();
		result.setItemsSetting(setting.getItemsSetting());
		result.setName(setting.getName());
		result.setClosureId(param.getClosureId());
		
		// 表示年月から期間をセットする
		Integer processingYm;
		GeneralDate startDate, endDate;
		if (topPageYearMonthEnum.equals(TopPageDisplayYearMonthEnum.THIS_MONTH_DISPLAY)) {
			processingYm = param.getCurrentProcessingYm();
			startDate = param.getCurrentProcessingYmStartDate();
			endDate = param.getCurrentProcessingYmEndDate();
		} else {
			processingYm = param.getNextProcessingYm();
			startDate = param.getNextProcessingYmStartDate();
			endDate = param.getNextProcessingYmEndDate();
		}
		CurrentClosingPeriod closingPeriod = new CurrentClosingPeriod(processingYm, startDate, endDate);
		result.setClosingThisMonth(closingPeriod);
		result.setClosingDisplay(closingPeriod);
		
		if(topPageYearMonthEnum == TopPageDisplayYearMonthEnum.THIS_MONTH_DISPLAY) {
			//Get work status data - 勤務状況のデータを取得する
			result.setAttendanceInfor(this.getWorkStatusData(cid, employeeId, result.getItemsSetting(), result.getClosingThisMonth()));
			//Get the number of vacations left - 休暇残数を取得する
			GetVacationLeftOutput vacationLeftOutput = this.getTheNumberOfVacationsLeft(cid, employeeId, result.getItemsSetting(), result.getClosingThisMonth());
			result.setRemainingNumberInfor(vacationLeftOutput.getRemainingNumberInforDto());
			result.setVacationSetting(vacationLeftOutput.getVacationSetting());
		}else {
			//Get work status data - 勤務状況のデータを取得する
			result.setAttendanceInfor(this.getWorkStatusData(cid, employeeId, result.getItemsSetting(), result.getClosingDisplay()));
			//Get the number of vacations left - 休暇残数を取得する
			GetVacationLeftOutput vacationLeftOutput = this.getTheNumberOfVacationsLeft(cid, employeeId, result.getItemsSetting(), result.getClosingDisplay());
			result.setRemainingNumberInfor(vacationLeftOutput.getRemainingNumberInforDto());
			result.setVacationSetting(vacationLeftOutput.getVacationSetting());
		}
		
		//Determine if the login person is the person in charge - ログイン者が担当者か判断する
		result.setDetailedWorkStatusSettings(roleExportRepo.getWhetherLoginerCharge().isEmployeeCharge());
		
		return result;
	}
	

	/**
	 * @name トップページの対象期間を取得する
	 * @param closureId 締めID
	 * @param currentMonth 当月の締め情報
	 * @param nextMonth 翌月の締め情報
	 * @param topPageYearMonth 表示年月
	 * @return
	 */
	public CurrentClosingPeriod getTargetPeriodOfTopPage(Integer closureId, CurrentClosingPeriod currentMonth, Optional<CurrentClosingPeriod> nextMonth, TopPageDisplayYearMonthEnum topPageYearMonth) {
		//INPUT．表示年月＝当月表示
		if(topPageYearMonth == TopPageDisplayYearMonthEnum.THIS_MONTH_DISPLAY) {
			return currentMonth;
		}
		//INPUT．表示年月＝翌月表示
		else {
			//INPUT．翌月の締め情報をチェックする = Nullじゃない
			if(nextMonth.isPresent()) {
				return nextMonth.get();
			}else {
				DatePeriod datePeriod = ClosureService.getClosurePeriod(ClosureService.createRequireM1(closureRepo, closureEmploymentRepo), closureId, new YearMonth(currentMonth.getProcessingYm()).addMonths(1));
				return new CurrentClosingPeriod(YearMonth.of(currentMonth.getProcessingYm()).addMonths(1).v(), datePeriod.start(), datePeriod.end());
			}
		}
	}
	
	
	/**
	 * @name 勤務状況のデータを取得する
	 * @param cid 会社ID
	 * @param employeeId 社員ID
	 * @param setting 勤務状況の詳細設定
	 * @param currentMonth 現在の締め期間
	 * @return 対象社員の勤怠情報
	 */
	public AttendanceInforDto getWorkStatusData(String cid, String employeeId, List<ItemsSettingDto> itemsSetting, CurrentClosingPeriod currentMonth) {
		AttendanceInforDto result = new AttendanceInforDto();
		Optional<ItemsSettingDto> dayErrDisplayAtr = itemsSetting.stream().filter(c->c.getItem() == WorkStatusItem.DAY_ERR_DISPLAY_ATR.value && c.isDisplayType()).findAny();
		if(dayErrDisplayAtr.isPresent()) {
			//sử lý 08
			//request list 192
			//アルゴリズム「08.日別実績のエラー有無表示」を実行する(Thực thi xử lý [08:hiển thị co hay không lỗi của dailyactual ])
			result.setDailyErrors(checksDailyPerformanceErrorRepo.checked(employeeId, currentMonth.getStartDate(), currentMonth.getEndDate()));
		}
		//条件：
		//INPUT．勤務状況の詳細設定．項目 IN (残業時間, フレックス時間, 休日出勤時間, 就業時間外深夜時間, 遅刻早退回数)
		/*
		 * 【パラメータ】 ・社員ID＝List＜INPUT．社員ID＞
		 * ・勤怠項目ID（List）＝List＜月別実績のトップページ表示用時間．残業合計時間(2063),
		 * 月別実績のトップページ表示用時間．フレックス合計時間(2065), フレックス繰越時間．フレックス繰越時間(19),
		 * 月別実績のトップページ表示用時間．休日出勤合計時間(2064), 月別実績の深夜時間．所定外深夜時間.時間．時間(319),
		 * 月別実績の遅刻早退．遅刻．回数(312), 月別実績の遅刻早退．早退．回数(315) の勤怠項目ID＞
		 */
		Map<String, List<MonthlyRecordValuesExport>> mapData = getMonthlyRecordPub.getRecordValues(
				Arrays.asList(employeeId),
				new YearMonthPeriod(new YearMonth(currentMonth.getProcessingYm()), new YearMonth(currentMonth.getProcessingYm())),
				Arrays.asList(2063, 2065, 19, 2064, 319, 312, 315));
		if (mapData.isEmpty()) {
			return result;
		}
		List<MonthlyRecordValuesExport> data = mapData.get(employeeId);
		if(!data.isEmpty()) {
			List<ItemValue> itemValues = data.get(0).getItemValues();
			for (ItemValue c : itemValues) {
				if(c.getItemId() == 2063) {
					result.setOverTime(c.getValue());
				}else if(c.getItemId() == 2065) {
					result.setFlexTime(c.getValue());
				}else if(c.getItemId() == 19) {
					result.setFlexCarryOverTime(c.getValue());
				}else if(c.getItemId() == 2064) {
					result.setHolidayTime(c.getValue());
				}else if(c.getItemId() == 319) {
					result.setNigthTime(c.getValue());
				}else if(c.getItemId() == 312) {
					result.setLate(c.getValue());
				}else if(c.getItemId() == 315) {
					result.setEarly(c.getValue());
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @name 休暇残数を取得する
	 * @param cid 会社ID
	 * @param employeeId 社員ID
	 * @param setting 勤務状況の詳細設定
	 * @param closingThisMonth 当月の締め情報
	 * @return 対象社員の残数情報
	 */
	public GetVacationLeftOutput getTheNumberOfVacationsLeft(String cid, String employeeId, List<ItemsSettingDto> itemsSetting, CurrentClosingPeriod closingThisMonth) {
		
		RemainingNumberInforDto remainNumber = new RemainingNumberInforDto();
		//年休管理区分
		boolean yearManage = false;  
		//代休管理区分
		boolean subHdManage = false;
		//積休管理区分
		boolean subVacaManage = false;
		//振休管理区分
		boolean retentionManage = false;
		//子看護管理区分
		boolean childNursingManagement = false;
		//介護管理区分
		boolean longTermCareManagement = false;
		for (ItemsSettingDto item: itemsSetting) {
			if(item.getItem() == WorkStatusItem.HDPAID_DISPLAY_ATR.value) {
				yearManage = item.isDisplayType();
			}else if(item.getItem() == WorkStatusItem.HDCOM_DISPLAY_ATR.value) {
				subHdManage = item.isDisplayType();
			}else if(item.getItem() == WorkStatusItem.HDSTK_DISPLAY_ATR.value) {
				subVacaManage = item.isDisplayType();
			}else if(item.getItem() == WorkStatusItem.HDSUB_DISPLAY_ATR.value) {
				retentionManage = item.isDisplayType();
			}else if(item.getItem() == WorkStatusItem.CHILD_CARE_DISPLAY_ATR.value) {
				childNursingManagement = item.isDisplayType();
			}else if(item.getItem() == WorkStatusItem.CARE_DISPLAY_ATR.value) {
				longTermCareManagement = item.isDisplayType();
			}
		}
		// 14.休暇種類表示チェック
		CheckDispHolidayType checkDispHolidayType = absenceServiceProcess.checkDisplayAppHdType(cid, employeeId, GeneralDate.today());
		
		//残数取得する
		NumberOfRemainOutput numberOfRemain = absenceServiceProcess.getNumberOfRemaining(
				cid, 
				employeeId, 
				GeneralDate.today(), 
				yearManage && checkDispHolidayType.getAnnAnualLeaveManagement().getAnnualLeaveManageDistinct().equals(ManageDistinct.YES) ? ManageDistinct.YES : ManageDistinct.NO, 
				subVacaManage && checkDispHolidayType.getAccumulatedRestManagement().getAccumulatedManage().equals(ManageDistinct.YES) ? ManageDistinct.YES : ManageDistinct.NO, 
				subHdManage && checkDispHolidayType.getSubstituteLeaveManagement().getSubstituteLeaveManagement().equals(ManageDistinct.YES) ? ManageDistinct.YES : ManageDistinct.NO, 
				retentionManage && checkDispHolidayType.getHolidayManagement().getHolidayManagement().equals(ManageDistinct.YES) ? ManageDistinct.YES : ManageDistinct.NO, 
				ManageDistinct.NO, 
				childNursingManagement && checkDispHolidayType.getNursingCareLeaveManagement().getChildNursingManagement().equals(ManageDistinct.YES) ? ManageDistinct.YES : ManageDistinct.NO, 
				longTermCareManagement && checkDispHolidayType.getNursingCareLeaveManagement().getLongTermCareManagement().equals(ManageDistinct.YES) ? ManageDistinct.YES : ManageDistinct.NO);
		if(numberOfRemain != null) {
			//積立年休残日数
		    remainNumber.setNumberOfAnnualLeaveRemain(new RemainingDaysAndTimeDto(numberOfRemain.getYearDayRemain(), new AttendanceTimeOfExistMinus(numberOfRemain.getYearHourRemain())));
			//代休残数
		    remainNumber.setNumberOfSubstituteHoliday(new RemainingDaysAndTimeDto(numberOfRemain.getSubDayRemain(), new AttendanceTimeOfExistMinus(numberOfRemain.getSubHdHourRemain())));
			//年休残数
		    remainNumber.setNumberAccumulatedAnnualLeave(numberOfRemain.getLastYearRemain());
			//振休残日数
		    remainNumber.setRemainingHolidays(numberOfRemain.getVacaRemain());
			//子の看護残数
		    remainNumber.setNursingRemainingNumberOfChildren(new RemainingDaysAndTimeDto(numberOfRemain.getChildNursingDayRemain(), new AttendanceTimeOfExistMinus(numberOfRemain.getChildNursingHourRemain())));
			//介護残数
		    remainNumber.setLongTermCareRemainingNumber(new RemainingDaysAndTimeDto(numberOfRemain.getNursingRemain(), new AttendanceTimeOfExistMinus(numberOfRemain.getNursingHourRemain())));
			// 付与年月日
		    remainNumber.setGrantDate(numberOfRemain.getGrantDate());
			// 付与日数
		    remainNumber.setGrantDays(numberOfRemain.getGrantDays());
			
		}
		
		if (subHdManage) {
		    CompensatoryLeaveComSetting compensatoryLeaveComSetting = compensLeaveComSetRepository.find(cid);
		    if (compensatoryLeaveComSetting != null) {
		        remainNumber.setSubHolidayTimeManage(compensatoryLeaveComSetting.getCompensatoryDigestiveTimeUnit().getIsManageByTime().value);
		    }
		}
		
		//アルゴリズム「23.特休残数表示」を実行する(Thực thi xử lý [23:hiển thị số phép đặc biệt còn lại])
		remainNumber.setSpecialHolidaysRemainings(getTheNumberOfVacationsLeft.remnantRepresentation(cid, employeeId, new DatePeriod(closingThisMonth.getStartDate(), closingThisMonth.getStartDate().addYears(1).addDays(-1))));
		
		/*
    		年休残数管理する　＝　年休管理.年休管理区分
    		積立年休残数管理する　＝　積休管理.積休管理区分
    		代休残数管理する　＝　代休管理.代休管理区分
    		代休時間残数管理する　＝　代休管理.時間代休管理区分
    		振休残数管理する　＝　振休管理.振休管理区分
    		公休残数管理する　＝　false
    		子の看護残数管理する　＝　介護看護休暇管理.子の看護管理区分
    		介護残数管理する　＝　介護看護休暇管理.介護管理区分
    		60H超休残数管理する　＝　60H超休管理.60H超休管理区分 
		*/
		VacationSetting setting = new VacationSetting(
		        checkDispHolidayType.getOvertime60hManagement().getOverrest60HManagement().equals(ManageDistinct.YES), 
		        checkDispHolidayType.getNursingCareLeaveManagement().getLongTermCareManagement().equals(ManageDistinct.YES), 
		        false, 
		        checkDispHolidayType.getNursingCareLeaveManagement().getChildNursingManagement().equals(ManageDistinct.YES), 
		        checkDispHolidayType.getHolidayManagement().getHolidayManagement().equals(ManageDistinct.YES), 
		        checkDispHolidayType.getAccumulatedRestManagement().getAccumulatedManage().equals(ManageDistinct.YES), 
		        checkDispHolidayType.getSubstituteLeaveManagement().getSubstituteLeaveManagement().equals(ManageDistinct.YES), 
		        checkDispHolidayType.getSubstituteLeaveManagement().getTimeAllowanceManagement().equals(ManageDistinct.YES), 
		        checkDispHolidayType.getAnnAnualLeaveManagement().getAnnualLeaveManageDistinct().equals(ManageDistinct.YES));
		
		return new GetVacationLeftOutput(remainNumber, setting);
		
	}
	
	
	
}