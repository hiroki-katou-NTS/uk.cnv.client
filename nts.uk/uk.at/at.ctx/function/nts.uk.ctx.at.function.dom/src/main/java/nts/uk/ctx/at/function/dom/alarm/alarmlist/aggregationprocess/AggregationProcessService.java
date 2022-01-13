package nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.function.dom.adapter.WorkPlaceHistImport;
import nts.uk.ctx.at.function.dom.adapter.WorkplaceWorkRecordAdapter;
import nts.uk.ctx.at.function.dom.adapter.alarm.AlarmListPersonServiceAdapter;
import nts.uk.ctx.at.function.dom.adapter.companyRecord.StatusOfEmployeeAdapter;
import nts.uk.ctx.at.function.dom.adapter.companyRecord.SyCompanyRecordFuncAdapter;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkPlaceInforExport;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.at.function.dom.adapter.workrecord.erroralarm.recordcheck.ErAlWorkRecordCheckAdapter;
import nts.uk.ctx.at.function.dom.adapter.workrecord.erroralarm.recordcheck.RegulationInfoEmployeeResult;
import nts.uk.ctx.at.function.dom.alarm.AlarmPatternSetting;
import nts.uk.ctx.at.function.dom.alarm.AlarmPatternSettingRepository;
import nts.uk.ctx.at.function.dom.alarm.alarmdata.ValueExtractAlarm;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.AlarmExtraValueWkReDto;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.EmployeeSearchDto;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.PeriodByAlarmCategory;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess.agreementprocess.AgreementCheckService;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.annual.ScheduleAnnualAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.appapproval.AppApprovalAggregationProcessService;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.attendanceholiday.TotalProcessAnnualHoliday;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.schedaily.ScheduleDailyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.schemonthly.ScheduleMonthlyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.weekly.WeeklyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategory;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategoryRepository;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckTargetCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.CheckCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.annualholiday.AnnualHolidayAlarmCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.appapproval.AppApprovalAlarmCheckCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.daily.DailyAlarmCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.fourweekfourdayoff.AlarmCheckCondition4W4D;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.fourweekfourdayoff.FourW4DCheckCond;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.master.MasterCheckAlarmCheckCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.monthly.MonAlarmCheckCon;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.multimonth.MulMonAlarmCond;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmPatternCode;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmPatternName;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.*;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmEmployeeList;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmExtractionCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.PersistenceAlarmListExtractResult;
import nts.uk.shr.com.i18n.TextResource;
import org.apache.commons.lang3.StringUtils;

@Stateless
public class AggregationProcessService {
	@Inject
	private AlarmPatternExtractResultRepository alarmRepos;
	@Inject
	private SyCompanyRecordFuncAdapter syCompAdapter;
	@Inject
	private WorkplaceWorkRecordAdapter wpAdapter;
	@Inject
	private AlarmPatternSettingRepository alPatternSettingRepo;
	@Inject
	private AlarmListPersonServiceAdapter extractAlarmService;
	@Inject
	private ExtractAlarmForEmployeeService extractService;
	
	@Inject
	private WorkplaceAdapter workplaceAdapter;
	@Inject
	private AlarmCheckConditionByCategoryRepository checkConditionRepo;
	@Inject
	private ErAlWorkRecordCheckAdapter erCheckAdapter;
	@Inject
	private AppApprovalAggregationProcessService appApprovalAggregationProcessService;
	@Inject
	private AgreementCheckService check36Alarm;
	@Inject
	private TotalProcessAnnualHoliday annualHolidayService;
	
	/**
	 * アラーム: 集計処理
	 * @param baseDate システム日付
	 * @param companyID　ログイン会社ID
	 * @param listEmployee　List＜社員ID＞
	 * @param periodByCategory　List＜カテゴリ別期間＞
	 * @param eralCate　List＜カテゴリ別アラームチェック条件＞
	 * @param checkConList　List＜チェック条件＞
	 * @param counter
	 * @param shouldStop
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<AlarmExtraValueWkReDto> processAlarmListWorkRecordV2(GeneralDate baseDate, String companyID, List<EmployeeSearchDto> listEmployee, 
			List<PeriodByAlarmCategory> periodByCategory, List<AlarmCheckConditionByCategory> eralCate,
			List<CheckCondition> checkConList, Consumer<Integer> counter, Supplier<Boolean> shouldStop) {
		
		List<ValueExtractAlarm> valueList = new ArrayList<>();
		
		// get list workplaceId and hierarchyCode 
		List<String> listWorkplaceId = listEmployee.stream().map(e -> e.getWorkplaceId()).filter(wp -> wp != null).distinct().collect(Collectors.toList());
//		Map<String, WkpConfigAtTimeAdapterDto> hierarchyWPMap = syWorkplaceAdapter.findByWkpIdsAtTime(companyID, baseDate, listWorkplaceId)
//																			.stream().collect(Collectors.toMap(WkpConfigAtTimeAdapterDto::getWorkplaceId, x->x));
		//[No.560]職場IDから職場の情報をすべて取得する
		List<WorkPlaceInforExport> wkpExportList = this.workplaceAdapter.getWorkplaceInforByWkpIds(companyID, listWorkplaceId, baseDate);
		
		// Map employeeID to EmployeeSearchDto object
		Map<String, EmployeeSearchDto> mapEmployeeId = listEmployee.stream().collect(Collectors.toMap(EmployeeSearchDto::getId, x->x));
		
		// MAP Enum AlarmCategory 
		Map<String, AlarmCategory> mapTextResourceToEnum = this.mapTextResourceToEnum();

		valueList.addAll(extractService.processV2(companyID, checkConList, periodByCategory, 
				listEmployee, eralCate, counter, shouldStop));
		
		//Convert from ValueExtractAlarm to AlarmExtraValueWkReDto
		return valueList.stream().map(value -> {
			return new AlarmExtraValueWkReDto(value.getWorkplaceID().orElse(null),
					value.getWorkplaceID().isPresent() && wkpExportList.stream().filter(x -> value.getWorkplaceID().get().equals(x.getWorkplaceId())).findFirst().isPresent() ?
							wkpExportList.stream().filter(x -> value.getWorkplaceID().get().equals(x.getWorkplaceId())).findFirst().get().getHierarchyCode() : "",
					mapEmployeeId.get(value.getEmployeeID()).getWorkplaceName(), 
					value.getEmployeeID(),
					mapEmployeeId.get(value.getEmployeeID()).getCode(),
					mapEmployeeId.get(value.getEmployeeID()).getName(), 
					value.getAlarmValueDate(), 
					mapTextResourceToEnum.get(value.getClassification()).value,
					value.getClassification(),
					value.getAlarmItem(),
					value.getAlarmValueMessage(),
					value.getComment().orElse(null),
					value.getCheckedValue().orElse(null));
		}).collect(Collectors.toList());
	}
	
	private Map<String, AlarmCategory> mapTextResourceToEnum(){
		Map<String, AlarmCategory> map = new HashMap<String, AlarmCategory>();
		map.put(TextResource.localize("KAL010_1"), AlarmCategory.DAILY);
		map.put(TextResource.localize("KAL010_62"), AlarmCategory.SCHEDULE_4WEEK);
		map.put(TextResource.localize("KAL010_100"), AlarmCategory.MONTHLY);
		map.put(TextResource.localize("KAL010_208"), AlarmCategory.AGREEMENT);
		map.put(TextResource.localize("KAL010_250"), AlarmCategory.MULTIPLE_MONTH);
		map.put(AlarmCategory.ATTENDANCE_RATE_FOR_HOLIDAY.nameId, AlarmCategory.ATTENDANCE_RATE_FOR_HOLIDAY);
		return map;
	} 
	
	/**
	 * 集計処理
	 * @param cid 会社ID
	 * @param pattentCd　パターンコード
	 * @param lstCategoryPeriod　List<カテゴリ別期間>
	 * @param lstSid　List<従業員>
	 * @param runCode　自動実行コード　（Default：　”Z”）
	 * @param counter
	 * @param shouldStop
	 * @param alarmPattern 自動変更の場合NULLを渡す
	 * @return 
	 */
	public AlarmListResultDto processAlarmListResult(String cid, String pattentCd,
			List<PeriodByAlarmCategory> lstCategoryPeriod,List<String> lstSid, String runCode,
			AlarmPatternSetting alarmPattern, List<AlarmCheckConditionByCategory> lstCondCate,
			Consumer<Integer> counter, Supplier<Boolean> shouldStop){
		
		AlarmListResultDto result = new AlarmListResultDto();
		List<AlarmExtracResult> lstExtracResult = new ArrayList<>();
		List<CategoryCondValueDto> lstValueDto = new ArrayList<>();
		if(alarmPattern == null && !pattentCd.isEmpty()) {
			//パラメータ．パターンコードをもとにドメインモデル「アラームリストパターン設定」を取得する
			Optional<AlarmPatternSetting> findByAlarmPatternCode = alPatternSettingRepo.findByAlarmPatternCode(cid, pattentCd);
			
			if(!findByAlarmPatternCode.isPresent()) {
				throw new BusinessException("Msg_2059", pattentCd);
			}
			List<Integer> lstCategory = lstCategoryPeriod.stream().map(x -> x.getCategory()).collect(Collectors.toList());
			alarmPattern = findByAlarmPatternCode.get();
			//ドメインモデル「カテゴリ別アラームチェック条件」を取得
			alarmPattern.getCheckConList().stream().filter(a -> lstCategory.contains(a.getAlarmCategory().value)).forEach(x->{
				List<AlarmCheckConditionByCategory> lstCond = checkConditionRepo.findByCategoryAndCode(cid, 
						x.getAlarmCategory().value, 
						x.getCheckConditionList());
				lstCondCate.addAll(lstCond);
			});
			
			if(lstCondCate.isEmpty()) {
				throw new BusinessException("Msg_2038");
			}
		}

		List<AlarmEmployeeList> alarmEmployeeList = new ArrayList<>();
		List<AlarmExtractionCondition> alarmExtractConditions = new ArrayList<>();
		
		lstCondCate.stream().forEach(x ->{
			List<AlarmListCheckInfor> mapCondCdCheckNoType = new ArrayList<>();
			CategoryCondValueDto valuesDto = new CategoryCondValueDto(x.getCategory(), x.getCode().v(), mapCondCdCheckNoType);
			DatePeriod datePeriod = null;
			
			//期間条件を絞り込む
			List<PeriodByAlarmCategory> periodCheck = lstCategoryPeriod.stream()
					.filter(y -> y.getCategory() == x.getCategory().value)
					.collect(Collectors.toList());
			
			if(!periodCheck.isEmpty() && periodCheck.get(0).getStartDate() != null) {
				datePeriod = new DatePeriod(periodCheck.get(0).getStartDate(),periodCheck.get(0).getEndDate());
//				if(x.getCategory() == AlarmCategory.MONTHLY || x.getCategory() == AlarmCategory.MULTIPLE_MONTH) {
//					datePeriod = new DatePeriod(periodCheck.get(0).getStartDate(),
//							periodCheck.get(0).getEndDate().addMonths(1).addDays(-1));
//				}
			}
			
			List<String> lstSidTmp = new ArrayList<>(lstSid);
			AlarmCheckTargetCondition extractTargetCondition = x.getExtractTargetCondition();
			
			//対象者をしぼり込む(レスポンス改善)
			if(!extractTargetCondition.getLstBusinessTypeCode().isEmpty()
					|| !extractTargetCondition.getLstClassificationCode().isEmpty()
					|| !extractTargetCondition.getLstEmploymentCode().isEmpty()
					|| !extractTargetCondition.getLstJobTitleId().isEmpty()) {
				lstSidTmp.clear();				
				List<RegulationInfoEmployeeResult> listTarget = erCheckAdapter.filterEmployees(datePeriod == null || datePeriod.end() == null ? GeneralDate.today() : datePeriod.end()
						,lstSid
						,x.getExtractTargetCondition());			
				lstSidTmp.addAll(listTarget.stream().map(c -> c.getEmployeeId())
						.collect(Collectors.toList()));
			}
			
			if(!lstSidTmp.isEmpty()) {
				List<AlarmListCheckInfor> lstCheckType = new ArrayList<>();
				List<ResultOfEachCondition> lstResultCondition = new ArrayList<>();
				
				//[RQ189] 社員ID（List）と指定期間から所属職場履歴を取得
				List<WorkPlaceHistImport> getWplByListSidAndPeriod = wpAdapter.getWplByListSidAndPeriod(lstSidTmp,
						datePeriod == null || datePeriod.start() == null ? new DatePeriod(GeneralDate.today(), GeneralDate.today()) : datePeriod);
				
				//[RQ588]社員の指定期間中の所属期間を取得する
				List<StatusOfEmployeeAdapter> lstStatusEmp = syCompAdapter.getAffCompanyHistByEmployee(lstSidTmp, 
						datePeriod == null || datePeriod.start() == null ? new DatePeriod(GeneralDate.today(), GeneralDate.today()) : datePeriod);
				
				//ループ中のカテゴリ別アラームチェック条件．カテゴリをチェック
				switch (x.getCategory()) {
				
				case SCHEDULE_DAILY:
					ScheduleDailyAlarmCheckCond scheDailyAlarmCondition = (ScheduleDailyAlarmCheckCond) x.getExtractionCondition();
					extractAlarmService.extractScheDailyCheckResult(cid,
							lstSid,
							datePeriod,
							extractTargetCondition.getId(),
							scheDailyAlarmCondition,
							getWplByListSidAndPeriod,
							lstStatusEmp,
							lstResultCondition,
							lstCheckType,
							counter,
							shouldStop,
							alarmEmployeeList,
							alarmExtractConditions,
							x.getCode().v());
					break;
					
				case SCHEDULE_WEEKLY:
					break;
					
				case SCHEDULE_4WEEK:
					AlarmCheckCondition4W4D fourW4DCheckCond = (AlarmCheckCondition4W4D) x.getExtractionCondition();
					FourW4DCheckCond w4d4Cond = fourW4DCheckCond.getFourW4DCheckCond();
					extractService.lstRunW4d4CheckErAl(cid,
							lstSidTmp,
							datePeriod,
							w4d4Cond,
							getWplByListSidAndPeriod,
							lstStatusEmp,
							counter,
							shouldStop,
							alarmEmployeeList,
							x.getCode().v(),
							alarmExtractConditions);

					AlarmListCheckInfor w4d4CheckInfor = new AlarmListCheckInfor(String.valueOf(w4d4Cond.value), AlarmListCheckType.FixCheck);
					valuesDto.getMapCondCdCheckNoType().add(w4d4CheckInfor);
					break;
					
				case SCHEDULE_MONTHLY:
					ScheduleMonthlyAlarmCheckCond scheduleMonthlyAlarmCheckCond = (ScheduleMonthlyAlarmCheckCond)x.getExtractionCondition();
					extractAlarmService.extractScheMonCheckResult(
							cid, lstSid, datePeriod, extractTargetCondition.getId(), 
							scheduleMonthlyAlarmCheckCond, 
							getWplByListSidAndPeriod, lstStatusEmp, 
							lstResultCondition, lstCheckType, counter, shouldStop,
							alarmEmployeeList,
							alarmExtractConditions,
							x.getCode().v());
					break;
					
				case SCHEDULE_YEAR:
					ScheduleAnnualAlarmCheckCond scheYearAlarmCondition = (ScheduleAnnualAlarmCheckCond)x.getExtractionCondition();
					extractAlarmService.extractScheYearCheckResult(cid,
							lstSid,
							datePeriod, 
							extractTargetCondition.getId(),
							scheYearAlarmCondition,
							getWplByListSidAndPeriod,
							lstStatusEmp,
							lstResultCondition,
							lstCheckType,
							counter,
							shouldStop,
                            alarmEmployeeList,
                            alarmExtractConditions,
                            x.getCode().v());
					break;
					
				case DAILY:
					DailyAlarmCondition dailyAlarmCon = (DailyAlarmCondition) x.getExtractionCondition();
					extractAlarmService.extractDailyCheckResult(cid,
							lstSid,
							datePeriod, 
							dailyAlarmCon.getDailyAlarmConID(),
							dailyAlarmCon,
							getWplByListSidAndPeriod,
							lstStatusEmp,
							lstResultCondition,
							lstCheckType,
							counter,
							shouldStop,
							alarmEmployeeList,
							alarmExtractConditions,
							x.getCode().v());
					break;
				case WEEKLY:
					WeeklyAlarmCheckCond weeklyAlarmCheckCond = (WeeklyAlarmCheckCond)x.getExtractionCondition();
					extractAlarmService.extractWeeklyCheckResult( 
							cid, lstSid, datePeriod, getWplByListSidAndPeriod, 
							weeklyAlarmCheckCond, lstResultCondition, 
							lstCheckType, counter, shouldStop,
                            alarmEmployeeList,
                            alarmExtractConditions,
                            x.getCode().v());
					break;
					
				case MONTHLY:
					MonAlarmCheckCon monCheck = (MonAlarmCheckCon) x.getExtractionCondition();
					extractAlarmService.extractMonthCheckResult(
							cid,
							lstSidTmp,
							new YearMonthPeriod(datePeriod.start().yearMonth(), datePeriod.end().yearMonth()),
							monCheck.getMonAlarmCheckConID(),
							monCheck.getArbExtraCon(),
							getWplByListSidAndPeriod,
							lstResultCondition,
							lstCheckType,
							counter,
							shouldStop,
							alarmEmployeeList,
							alarmExtractConditions,
							x.getCode().v());
					break;
					
				case APPLICATION_APPROVAL:
					AppApprovalAlarmCheckCondition appCond = (AppApprovalAlarmCheckCondition) x.getExtractionCondition();
					appApprovalAggregationProcessService.aggregateCheck(cid, appCond.getApprovalAlarmConID(),
							datePeriod,
							lstSidTmp,
							getWplByListSidAndPeriod,
							lstResultCondition, 
							lstCheckType,
							counter,
							shouldStop,
							alarmEmployeeList,
							alarmExtractConditions,
							x.getCode().v());
					break;
					
				case MULTIPLE_MONTH:
					MulMonAlarmCond mulMonCheck = (MulMonAlarmCond) x.getExtractionCondition();
					extractAlarmService.extractMultiMonthCheckResult(cid,
							lstSidTmp, 
							new YearMonthPeriod(datePeriod.start().yearMonth(), datePeriod.end().yearMonth()),
							mulMonCheck.getErrorAlarmCondIds(), 
							getWplByListSidAndPeriod, 
							lstResultCondition,
							lstCheckType,
							alarmEmployeeList,
							alarmExtractConditions,
							x.getCode().v(),
							counter,
							shouldStop);
					break;
				case ANY_PERIOD:
					break;
					
				case ATTENDANCE_RATE_FOR_HOLIDAY:
					AnnualHolidayAlarmCondition yearHolidayCheck = (AnnualHolidayAlarmCondition) x.getExtractionCondition();
					annualHolidayService.checkAnnualHolidayAlarm(cid,
							yearHolidayCheck,
							lstSidTmp,
							counter,
							shouldStop,
							getWplByListSidAndPeriod,
							lstStatusEmp,
							lstResultCondition,
							lstCheckType,
							alarmEmployeeList,
							alarmExtractConditions,
							x.getCode().v());
					break;
					
				case AGREEMENT:
					check36Alarm.get36AlarmCheck(cid,
							x.getAlarmChkCondAgree36(),
							periodCheck,
							counter,
							shouldStop,
							getWplByListSidAndPeriod,
							lstSidTmp,
							lstResultCondition,
							lstCheckType,
							alarmEmployeeList,
							alarmExtractConditions,
							x.getCode().v());
					break;
					
				case MAN_HOUR_CHECK:
					break;
					
				case MASTER_CHECK:
					MasterCheckAlarmCheckCondition masterCheck = (MasterCheckAlarmCheckCondition) x.getExtractionCondition();
					extractAlarmService.extractMasterCheckResult(cid, lstSidTmp, 
							datePeriod, 
							masterCheck.getAlarmCheckId(), 
							getWplByListSidAndPeriod, 
							lstStatusEmp, 
							lstResultCondition,
							lstCheckType,
							counter,
							shouldStop,
							alarmEmployeeList,
							alarmExtractConditions,
							x.getCode().v());
					break;	
					
				default:
					break;
				
				}
				
				//List＜チェック種類、コード＞に追加
				CategoryCondValueDto valueDto = new CategoryCondValueDto(x.getCategory(), x.getCode().v(), lstCheckType);
				lstValueDto.add(valueDto);
				//「アラーム抽出結果」を作成してList<アラーム抽出結果＞に追加
				if(!lstResultCondition.isEmpty()) {
					AlarmExtracResult extracResult = new AlarmExtracResult(x.getCode().v(), x.getCategory(), lstResultCondition);
					lstExtracResult.add(extracResult);	
				}
				
				//TODO 抽出処理停止フラグが立っているかチェックする(check xem có flag dừng xử lý extract hay k)
				
			}
			
		});
		result.setExtracting(true);
		result.setLstAlarmResult(lstExtracResult);

		// 「永続化のアラームリスト抽出結果」を作成
		PersistenceAlarmListExtractResult persisExtractResult = new PersistenceAlarmListExtractResult(
				new AlarmPatternCode(pattentCd),
				alarmPattern != null && !StringUtils.isEmpty(alarmPattern.getAlarmPatternName().v()) ? new AlarmPatternName(alarmPattern.getAlarmPatternName().v()) : null,
				alarmEmployeeList,
				cid,
				runCode
		);
		result.setPersisAlarmExtractResult(persisExtractResult);
		result.setAlarmExtractConditions(alarmExtractConditions);

		//アラーム（トップページ）永続化の処理 TODO doi ung khi co yeu cau
		//this.createAlarmToppage(alarmResult, lstSid, lstValueDto, lstCategoryPeriod, alarmPattern.getAlarmPatternName().v());
		return result;
			
	}
	/**
	 * アラーム（トップページ）永続化の処理
	 * @param alarmResult
	 * @param lstSid
	 * @param lstCheckType
	 * @param lstCategoryPeriod
	 * @param patternName
	 */
	private void createAlarmToppage(AlarmPatternExtractResult alarmResult,//今回のアラーム結果
			List<String> lstSid, 
			List<CategoryCondValueDto> lstCheckType,
			List<PeriodByAlarmCategory> lstCategoryPeriod, String patternName) {
		String cid = alarmResult.getCID();
		String runCode = alarmResult.getRunCode();
		String pattentCd = alarmResult.getPatternCode();
		Optional<AlarmPatternExtractResult> optAlarmExtractResult = alarmRepos.optAlarmExtractResult(cid, runCode, pattentCd); //存在しているアラーム
		if(!optAlarmExtractResult.isPresent() && alarmResult.getLstExtracResult().isEmpty()) {
			return;
		}
		else if(!optAlarmExtractResult.isPresent()) {
			alarmResult.setPatternName(patternName);
			alarmRepos.addAlarmExtractResult(alarmResult);
		} else {
			List<AlarmExtracResult> lstExResultInsert = new ArrayList<>();
			List<AlarmExtracResult> lstExResultDelete = new ArrayList<>();
			AlarmPatternExtractResult alarmPattern = optAlarmExtractResult.get();
			
			//①今回のアラーム結果がデータベースに存在してない場合データベースのデータを追加
			
			//②今回のアラーム結果がないがデータベースに存在している場合データベースを削除
			//③データベースに存在しているアラームが今回のアラーム結果がある場合データがそのまま存在
			if(lstCheckType.isEmpty()) {
				return;
			}
			for (int i = 0; i < lstCheckType.size(); i++) {
				CategoryCondValueDto x = lstCheckType.get(i);
				//TODO can phai debug de xem lai dinh dang ngay thang
				List<PeriodByAlarmCategory> period = lstCategoryPeriod.stream().filter(p -> p.getCategory() == p.getCategory())
						.collect(Collectors.toList());
				//カテゴリ、アラームリストチェック条件コードを絞り込む
				List<AlarmExtracResult> lstExtracResultDB = alarmPattern.getLstExtracResult().stream()
						.filter(ex -> ex.getCategory() == x.getCategory() && ex.getCoditionCode().equals(x.getCondCode()))
						.collect(Collectors.toList());
				List<AlarmExtracResult> lstExtracResultNew = alarmResult.getLstExtracResult().stream()
						.filter(ex -> ex.getCategory() == x.getCategory() && ex.getCoditionCode().equals(x.getCondCode()))
						.collect(Collectors.toList());
				if(lstExtracResultDB.isEmpty()) {
					lstExResultInsert.addAll(lstExtracResultNew);
				} else {
					if(x.getMapCondCdCheckNoType().isEmpty()) {
						continue;
					}
					List<AlarmListCheckInfor> mapCondCdCheckNoType = x.getMapCondCdCheckNoType();
					for (int j = 0; j < mapCondCdCheckNoType.size(); j++) {
						AlarmListCheckInfor y = mapCondCdCheckNoType.get(j);
						List<ResultOfEachCondition> lstCondResultDB = new ArrayList<>();
						List<ResultOfEachCondition> lstCondResultNew = new ArrayList<>();
						//チェック種類、アラームリストチェック条件のNo・コードを絞り込む
						for (int k = 0; k < lstExtracResultDB.size(); k++) {
							AlarmExtracResult ex = lstExtracResultDB.get(k);
							List<ResultOfEachCondition> lstCondResultDBT = ex.getLstResult().stream()
									.filter(co -> co.getCheckType().value == y.getChekType().value && co.getNo().equals(y.getNo()))
									.collect(Collectors.toList());
							lstCondResultDB.addAll(lstCondResultDBT);
						}
						for (int k = 0; k < lstExtracResultNew.size(); k++) {
							AlarmExtracResult ex = lstExtracResultNew.get(k);
							List<ResultOfEachCondition> lstCondResultNewT = ex.getLstResult().stream()
									.filter(co -> co.getCheckType().equals(y.getChekType()) && co.getNo().equals(y.getNo()))
									.collect(Collectors.toList());
							lstCondResultNew.addAll(lstCondResultNewT);
						}

						if(lstCondResultNew.isEmpty() && !lstCondResultDB.isEmpty()) {
							AlarmExtracResult extracResultDelete = new AlarmExtracResult(x.getCondCode(), x.getCategory(), lstCondResultDB);
							lstExResultDelete.add(extracResultDelete);
						} else if(!lstCondResultNew.isEmpty() && lstCondResultDB.isEmpty()) {
							AlarmExtracResult extracResultInsert = new AlarmExtracResult(x.getCondCode(), x.getCategory(), lstCondResultNew);
							lstExResultInsert.add(extracResultInsert);
						} else if (!lstCondResultNew.isEmpty() && !lstCondResultDB.isEmpty()) {
							List<ExtractionResultDetail> lstResultDetailDB = new ArrayList<>();
							List<ExtractionResultDetail> lstResultDetailNew = new ArrayList<>();
							//社員ID、日付から絞り込む
							for (int k = 0; k < lstCondResultDB.size(); k++) {
								ResultOfEachCondition de = lstCondResultDB.get(k);
								List<ExtractionResultDetail> lstResultDetailDBT = de.getLstResultDetail().stream()
										.filter(dt -> lstSid.contains(dt.getSID())
												&& (!period.isEmpty() && period.get(0).getStartDate() != null && period.get(0).getEndDate() != null
														&& dt.getPeriodDate().getStartDate().get().afterOrEquals(period.get(0).getStartDate())
														&& dt.getPeriodDate().getStartDate().get().beforeOrEquals(period.get(0).getEndDate())))
										.collect(Collectors.toList());
								lstResultDetailDB.addAll(lstResultDetailDBT);
							}
							for (int k = 0; k < lstCondResultNew.size(); k++) {
								ResultOfEachCondition de = lstCondResultNew.get(k);
								List<ExtractionResultDetail> lstResultDetailNewT = de.getLstResultDetail().stream()
										.filter(dt -> lstSid.contains(dt.getSID())
												&& (!period.isEmpty() && period.get(0).getStartDate() != null && period.get(0).getEndDate() != null
														&& dt.getPeriodDate().getStartDate().get().beforeOrEquals(period.get(0).getStartDate())
														&& dt.getPeriodDate().getStartDate().get().after(period.get(0).getEndDate())))
										.collect(Collectors.toList());
								lstResultDetailNew.addAll(lstResultDetailNewT);
							}


							//追加
							if(lstResultDetailDB.isEmpty() && !lstResultDetailNew.isEmpty()) {
								ResultOfEachCondition eachCondNew = new ResultOfEachCondition(y.getChekType(), y.getNo(), lstResultDetailNew);
								AlarmExtracResult extracResultInsert = new AlarmExtracResult(x.getCondCode(), x.getCategory(), Arrays.asList(eachCondNew));
								lstExResultInsert.add(extracResultInsert);
							}
							//削除　又は　追加
							else if (!lstResultDetailDB.isEmpty() && lstResultDetailNew.isEmpty()){
								ResultOfEachCondition eachCondDel = new ResultOfEachCondition(y.getChekType(), y.getNo(), lstResultDetailDB);
								AlarmExtracResult extracResultDel = new AlarmExtracResult(x.getCondCode(), x.getCategory(), Arrays.asList(eachCondDel));
								lstExResultDelete.add(extracResultDel);
							} else if (!lstResultDetailDB.isEmpty() && !lstResultDetailNew.isEmpty()){
								List<ExtractionResultDetail> lstDetailIns = new ArrayList<>();
								List<ExtractionResultDetail> lstDetailDel = new ArrayList<>();
								//データベースがあるが抽出した結果がない　-> 削除
								lstResultDetailDB.stream().forEach(db -> {
									List<ExtractionResultDetail> lstDel = lstResultDetailNew.stream()
											.filter(news -> db.getSID().equals(news.getSID())
													&& db.getPeriodDate().getStartDate().equals(news.getPeriodDate().getStartDate()))
											.collect(Collectors.toList());
									if(lstDel.isEmpty()) {
										lstDetailDel.add(db);
									}
								});
								if(!lstDetailDel.isEmpty()) {
									ResultOfEachCondition eachCondDel = new ResultOfEachCondition(y.getChekType(), y.getNo(), lstDetailDel);
									AlarmExtracResult extracResultDel = new AlarmExtracResult(x.getCondCode(), x.getCategory(), Arrays.asList(eachCondDel));
									lstExResultDelete.add(extracResultDel);
								}

								//データベースがないが抽出した結果がある ->　追加
								lstResultDetailNew.stream().forEach(news -> {
									List<ExtractionResultDetail> lstnew = lstResultDetailDB.stream()
											.filter(db -> db.getSID().equals(db.getSID())
													&& db.getPeriodDate().getStartDate().equals(db.getPeriodDate().getStartDate()))
											.collect(Collectors.toList());
									if(lstnew.isEmpty()) {
										lstDetailIns.add(news);
									}
								});
								if(!lstDetailIns.isEmpty()) {
									ResultOfEachCondition eachCondIns = new ResultOfEachCondition(y.getChekType(), y.getNo(), lstDetailIns);
									AlarmExtracResult extracResultIns = new AlarmExtracResult(x.getCondCode(), x.getCategory(), Arrays.asList(eachCondIns));
									lstExResultInsert.add(extracResultIns);
								}
							}

						}
					}
				}
			}
			if(!lstExResultInsert.isEmpty()) {
				AlarmPatternExtractResult alarmInsert = new AlarmPatternExtractResult(cid, runCode, pattentCd, patternName, lstExResultInsert);
				alarmRepos.addAlarmExtractResult(alarmInsert);
			}
			if(!lstExResultDelete.isEmpty()) {
				AlarmPatternExtractResult alarmDelete = new AlarmPatternExtractResult(cid, runCode, pattentCd, patternName, lstExResultDelete);
				alarmRepos.deleteAlarmExtractResult(alarmDelete);
			}
		}
	}
	
	/**
	 * スケジュール日次の集計処理
	 */
	private void processWithScheduleDaily() {
		// チェックする前にデータ準備
		
	}
}
