package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.createdailyresults;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.CopyWorkTypeWorkTime;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.workschedulereflected.WorkScheduleReflected;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.CreateDailyResultDomainServiceNew;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.EmployeeGeneralAndPeriodMaster;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.OutputCreateDailyOneDay;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.EmployeeGeneralInfoImport;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.AffiliationInforState;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ReflectWorkInforDomainService;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.output.PeriodInMasterList;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workingcondition.ManageAtr;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 日別実績を作成する
 * 
 * @author tutk
 *
 */
@Stateless
public class CreateDailyResults {

	@Inject
	private DailyRecordConverter dailyRecordConverter;

	@Inject
	private ReflectWorkInforDomainService reflectWorkInforDomainService;

	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;

	@Inject
	private ReflectWorkInforDomainService reflectWorkInforDomainServiceImpl;

	@Inject
	private CopyWorkTypeWorkTime copyWorkTypeWorkTime;

	@Inject
	private WorkScheduleReflected workScheduleReflected;
	
	@Inject
	private CreateDailyResultDomainServiceNew createDailyResultDomainServiceNew;
	
	//マスターデータを取得して、日別実績を作成する
	public Optional<OutputCreateDailyOneDay> createDailyResult(String companyId, String employeeId, GeneralDate ymd,
			ExecutionTypeDaily executionType) {
		    DatePeriod period = new DatePeriod(ymd, ymd);
		    Optional<EmployeeGeneralAndPeriodMaster> masterData = createDailyResultDomainServiceNew.getMasterData(companyId, employeeId, period);
		if (!masterData.isPresent()) {
			return Optional.empty();
		}
		OutputCreateDailyOneDay result = createDailyResult(companyId, employeeId, ymd, executionType,
				masterData.get().getEmployeeGeneralInfoImport(),
				masterData.get().getPeriodInMasterList(), Optional.empty());
	    if(!result.getListErrorMessageInfo().isEmpty()) {
	    	return Optional.empty();
	    }
	    return Optional.of(result);
	}
	
	/**
	 * @param companyId                 会社ID
	 * @param employeeId                社員ID
	 * @param ymd                       年月日
	 * @param executionType             実行タイプ（作成する、打刻反映する、実績削除する）
	 * @param employeeGeneralInfoImport 特定期間の社員情報(optional)
	 * @param periodInMasterList        期間内マスタ一覧(optional)
	 * @param empCalAndSumExecLogID
	 * @return
	 */
	public OutputCreateDailyOneDay createDailyResult(String companyId, String employeeId, GeneralDate ymd,
			ExecutionTypeDaily executionType, EmployeeGeneralInfoImport employeeGeneralInfoImport,
			PeriodInMasterList periodInMasterList, Optional<IntegrationOfDaily> integrationOfDailyOpt) {
		List<ErrorMessageInfo> listErrorMessageInfo = new ArrayList<>();

		IntegrationOfDaily integrationOfDaily = integrationOfDailyOpt.orElse(createDefault(employeeId, ymd));
		// 日別実績の「情報系」のドメインを取得する
		List<Integer> attendanceItemIdList = integrationOfDaily.getEditState().stream()
				.map(editState -> editState.getAttendanceItemId()).distinct().collect(Collectors.toList());
		
		// 日別実績のコンバーターを作成する
		// 日別実績のデータをコンバーターに入れる
		DailyRecordToAttendanceItemConverter converter = dailyRecordConverter.createDailyConverter()
				.setData(integrationOfDaily).completed();
		List<ItemValue> listItemValue = converter.convert(attendanceItemIdList);
		
		if(integrationOfDaily.getWorkInformation() == null) {
			integrationOfDaily.setWorkInformation(new WorkInfoOfDailyAttendance());
		}
		// 曜日を求める - 日別実績の勤務情報．曜日 ← 処理日の曜日
		integrationOfDaily.getWorkInformation().setDayOfWeek(DayOfWeek.valueOf(ymd.dayOfWeekEnum().value-1));

		// 所属情報を反映する
		AffiliationInforState affiliationInforState = reflectWorkInforDomainService.createAffiliationInforState(
				companyId, employeeId, ymd, employeeGeneralInfoImport);
		// エラーあるかを確認する
		if (!affiliationInforState.getErrorNotExecLogID().isEmpty()) {
			listErrorMessageInfo.addAll(affiliationInforState.getErrorNotExecLogID());
			return new OutputCreateDailyOneDay(listErrorMessageInfo, integrationOfDaily, new ArrayList<>()) ;
		}
		if(affiliationInforState.getAffiliationInforOfDailyPerfor().isPresent()) {
			integrationOfDaily.setAffiliationInfor(affiliationInforState.getAffiliationInforOfDailyPerfor().get());
		}
		// ドメインモデル「労働条件項目」を取得する
		Optional<WorkingConditionItem> optWorkingConditionItem = this.workingConditionItemRepository
				.getBySidAndStandardDate(employeeId, ymd);
		if (!optWorkingConditionItem.isPresent()) {
			listErrorMessageInfo.add(new ErrorMessageInfo(companyId, employeeId, ymd, ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("005"), new ErrMessageContent(TextResource.localize("Msg_430"))));
			return new OutputCreateDailyOneDay(listErrorMessageInfo, integrationOfDaily, new ArrayList<>()) ;
		}
		if (optWorkingConditionItem.get().getScheduleManagementAtr() == ManageAtr.USE) {
			//勤務予定反映
			listErrorMessageInfo.addAll(workScheduleReflected.workScheduleReflected(companyId, integrationOfDaily));
		} else {
			// 個人情報から勤務種類と就業時間帯を写す
			listErrorMessageInfo.addAll(copyWorkTypeWorkTime.copyWorkTypeWorkTime(companyId, integrationOfDaily));
		}
		if (!listErrorMessageInfo.isEmpty()) {
			return new OutputCreateDailyOneDay(listErrorMessageInfo, integrationOfDaily, new ArrayList<>()) ;
		}
		// 特定日を日別実績に反映する
		integrationOfDaily.setSpecDateAttr(Optional.of(reflectWorkInforDomainService.reflectSpecificDate(companyId, employeeId, ymd,
				integrationOfDaily.getAffiliationInfor().getWplID(), periodInMasterList)));
		// 加給設定を日別実績に反映する
		Optional<BonusPaySetting> optBonusPaySetting = reflectWorkInforDomainServiceImpl.reflectBonusSettingDailyPer(companyId, employeeId, ymd,
				integrationOfDaily.getWorkInformation(), integrationOfDaily.getAffiliationInfor(), periodInMasterList);
		if(optBonusPaySetting.isPresent()) {
			integrationOfDaily.getAffiliationInfor().setBonusPaySettingCode(Optional.ofNullable(optBonusPaySetting.get().getCode()));
		}
		// 計算区分を日別実績に反映する
		integrationOfDaily.setCalAttr(reflectWorkInforDomainServiceImpl.reflectCalAttOfDaiPer(companyId, employeeId, ymd,
				integrationOfDaily.getAffiliationInfor(), periodInMasterList));
		if(!integrationOfDaily.getEditState().isEmpty()) {
			//手修正項目取り戻す
			integrationOfDaily = restoreData(converter, integrationOfDaily, listItemValue);
		}
		return new OutputCreateDailyOneDay(listErrorMessageInfo, integrationOfDaily, new ArrayList<>()) ;
	}
	/**
	 * 手修正項目のデータを元に戻す
	 * @param converter
	 * @param integrationOfDaily
	 * @param listItemValue
	 */
	public IntegrationOfDaily restoreData(DailyRecordToAttendanceItemConverter converter,IntegrationOfDaily integrationOfDaily,List<ItemValue> listItemValue) {
		converter.setData(integrationOfDaily);
		converter.merge(listItemValue);
		return converter.toDomain();
	}

	//日別実績のディフォルトを作成する
		private IntegrationOfDaily createDefault(String sid, GeneralDate dateData) {
			return new IntegrationOfDaily(
					sid,
					dateData,
					null, 
					null, 
					null,
					Optional.empty(), 
					new ArrayList<>(), 
					Optional.empty(), 
					new BreakTimeOfDailyAttd(), 
					Optional.empty(), 
					Optional.empty(), 
					Optional.empty(), 
					Optional.empty(), 
					Optional.empty(), 
					Optional.empty(), 
					new ArrayList<>(),
					Optional.empty(),
					new ArrayList<>(),
					new ArrayList<>(),
					new ArrayList<>(),
					Optional.empty());
		}
}
