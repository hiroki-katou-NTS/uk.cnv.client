package nts.uk.ctx.at.record.dom.workrecord.erroralarm.daily.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.adapter.specificdatesetting.RecSpecificDateSettingAdapter;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.dtos.ApproveRootStatusForEmpImport;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.enums.ApprovalStatusForEmployee;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.AttendanceLeavingGateOfDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.finddata.IFindDataDCRecord;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.errorcheck.DailyRecordCreateErrorAlermService;
import nts.uk.ctx.at.record.dom.divergence.time.service.DivTimeSysFixedCheckService;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmConditionRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.FixedConditionData;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.FixedConditionDataRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.FixedConditionWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.FixedConditionWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.WorkRecordFixedCheckItem;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.algorithm.DataCheckAlarmListService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.WorkRecordExtraConRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.WorkRecordExtractingCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.ErAlAttendanceItemCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.service.ErAlWorkRecordCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.service.ErAlWorkRecordCheckService.ErrorRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktime.PlanActualWorkTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktime.SingleWorkTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktype.PlanActualWorkType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktype.SingleWorkType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.daily.DailyCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.CompareOperatorText;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConditionAtr;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConvertCompareTypeToText;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.FilterByCompare;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.TypeCheckWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.StatusOfEmployeeAdapterAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceHistImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceIdAndPeriodImportAl;
import nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm.ExitStampIncorrectOrderCheck;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.Identification;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository.IdentificationRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcessRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcessRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.adapter.attendanceitemname.AttendanceItemNameAdapter;
import nts.uk.ctx.at.shared.dom.adapter.attendanceitemname.MonthlyAttendanceItemNameDto;
import nts.uk.ctx.at.shared.dom.adapter.specificdatesetting.RecSpecificDateSettingImport;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckType;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ExtractionAlarmPeriodDate;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmCheckConditionCode;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmEmployeeList;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmExtractInfoResult;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmExtractionCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.ExtractResultDetail;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrame;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrameRepository;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculationMinusExist;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.AttendanceLeavingGate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.SystemFixedErrorAlarm;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.CheckExcessAtr;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrame;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrameRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.LaborContractTime;
import nts.uk.ctx.at.shared.dom.workingcondition.PersonalWorkCategory;
import nts.uk.ctx.at.shared.dom.workingcondition.SingleDaySchedule;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class DailyCheckServiceImpl implements DailyCheckService{
	
	@Inject
	private AttendanceItemNameAdapter attendanceAdap;
	
	@Inject
	private WorkTypeRepository workTypeRep;
	
	@Inject
	private DailyRecordShareFinder dailyRecordShareFinder;
	
	@Inject
	private WorkRecordExtraConRepository workRep;
	
	@Inject
	private ErrorAlarmWorkRecordRepository errorAlarmRep;
	
	@Inject
	private FixedConditionWorkRecordRepository fixCondReRep;
	
	@Inject
	private FixedConditionDataRepository fixConRep;
	
	@Inject
	private IdentificationRepository indentifiRep;
	
	@Inject
	private IFindDataDCRecord approotAdap;
	
	@Inject
	private WorkTimeSettingRepository workTimeRep;
	
	@Inject
	private ErrorAlarmConditionRepository errorConRep;
	
	@Inject
	private StampCardRepository stampCardRep;
	
	@Inject
	private WorkingConditionRepository workingConditionRepository;
	
	@Inject
	private DailyRecordCreateErrorAlermService dailyAlermService;
	
	@Inject
	private ManagedParallelWithContext parallelManager;
	@Inject
	private IdentityProcessRepository indentiryRepo;
	@Inject
	private ApprovalProcessRepository appProcessRepo;
	@Inject
	private ErAlWorkRecordCheckService erAlCheckService;
	@Inject
	private DataCheckAlarmListService dataCheckSevice;
	@Inject
	private ConvertCompareTypeToText convertComparaToText;
	@Inject
	private AttendanceItemConvertFactory convertFactory;
	@Inject
	private WorkingConditionItemRepository workConditionItemRepo;
	@Inject
	private DivTimeSysFixedCheckService divTimeCheckService;
	@Inject
	private ExitStampIncorrectOrderCheck exitStampIncorrectOrderCheck;
	@Inject
	private OvertimeWorkFrameRepository overtimeWorkFrameRepository;
	@Inject
	private WorkdayoffFrameRepository workDayOffFrameRepo;
	@Inject
	private RecSpecificDateSettingAdapter specificDateSettingAdapter;
	@Inject
	private StampDakokuRepository stampDakokuRepo;
	
	@Override
	public void extractDailyCheck(String cid, List<String> lstSid, DatePeriod dPeriod,
								  String errorDailyCheckId, List<String> extractConditionWorkRecord,
								  List<String> errorDailyCheckCd, List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
								  List<StatusOfEmployeeAdapterAl> lstStatusEmp,
								  List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter,
								  Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeLists,
								  List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		
		// チェックする前にデータを準備
		PrepareData prepareData = this.getDataBeforeChecking(cid, lstSid, dPeriod, errorDailyCheckId,
																extractConditionWorkRecord, errorDailyCheckCd);
		
		parallelManager.forEach(CollectionUtil.partitionBySize(lstSid, 100), emps -> {

			synchronized (this) {
				if (shouldStop.get()) {
					return;
				}
			}
			// get work place id
			for(String sid : lstSid) {
				List<AlarmExtractInfoResult> lstExtractInfoResult = new ArrayList<>();
				List<GeneralDate> listDate = dPeriod.datesBetween();
				for(WorkRecordExtractingCondition extCond : prepareData.getWorkRecordCond()) {
					// 日次のチェック条件のアラーム値を生成する
					this.extractAlarmConditionTab3(extCond, 
							prepareData.getListErrorAlarmCon(),
							prepareData.getListIntegrationDai(),
							sid,
							dPeriod,
							getWplByListSidAndPeriod,
							prepareData.getLstItemDay(),
							extractConditionWorkRecord, 
							prepareData.getListWorkType(),
							lstStatusEmp, prepareData.getListWorktime(),
							alarmExtractConditions, alarmCheckConditionCode, lstExtractInfoResult);

				}
				for(int day = 0; day < listDate.size(); day++) {
					GeneralDate exDate = listDate.get(day);
					// 社員の会社所属状況をチェック
					List<StatusOfEmployeeAdapterAl> statusOfEmp = lstStatusEmp.stream()
							.filter(x -> x.getEmployeeId().equals(sid) 
									&& !x.getListPeriod().stream()
										.filter(y -> y.start().beforeOrEquals(exDate) && y.end().afterOrEquals(exDate)).collect(Collectors.toList()).isEmpty())
							.collect(Collectors.toList());
					if(statusOfEmp.isEmpty()) continue;
					
					// 日別実績を絞り込む
					List<IntegrationOfDaily> lstDaily = prepareData.getListIntegrationDai().stream()
							.filter(x -> x.getEmployeeId().equals(sid) && x.getYmd().equals(exDate))
							.collect(Collectors.toList());			
					IntegrationOfDaily integrationDaily = null;
					if(!lstDaily.isEmpty()) {
						integrationDaily = lstDaily.get(0);
					}
					if(integrationDaily != null) {
						// 日別実績のエラーアラームのアラーム値を生成する
						this.extractAlarmDailyTab2(prepareData.getListError(),
								prepareData.getListErrorAlarmCon(),
								integrationDaily,
								sid,
								exDate,
								prepareData.getLstItemDay(),
								getWplByListSidAndPeriod,
								alarmExtractConditions,
								alarmCheckConditionCode,
								lstExtractInfoResult);
					}
					
					// 日次の固定抽出条件のアラーム値を生成する
					this.extractAlarmFixTab4(prepareData,
							integrationDaily,
							sid,
							exDate,
							getWplByListSidAndPeriod,
							alarmExtractConditions,
							alarmCheckConditionCode,
							lstExtractInfoResult);
				}
                if (!lstExtractInfoResult.isEmpty()) {
					if (alarmEmployeeLists.stream().anyMatch(i -> i.getEmployeeID().equals(sid))) {
						for (AlarmEmployeeList i : alarmEmployeeLists) {
							if (i.getEmployeeID().equals(sid)) {
								List<AlarmExtractInfoResult> temp = new ArrayList<>(i.getAlarmExtractInfoResults());
								temp.addAll(lstExtractInfoResult);

								i.setAlarmExtractInfoResults(temp);
								break;
							}
						}
					} else {
						alarmEmployeeLists.add(new AlarmEmployeeList(lstExtractInfoResult, sid));
					}

                }
			}
			synchronized (this) {
				counter.accept(emps.size());
			}
		});
		
	}

	/**
	 * チェックする前にデータを準備
	 * @param cid
	 * @param lstSid
	 * @param dPeriod
	 * @param errorDailyCheckId tab4
	 * @param extractConditionWorkRecord tab3
	 * @param errorDailyCheckCd tab2 日別実績のエラーアラームコード
	 * @return
	 */
	private PrepareData getDataBeforeChecking(String cid, List<String> lstSid, DatePeriod dPeriod, 
			String errorDailyCheckId, List<String> extractConditionWorkRecord, List<String> errorDailyCheckCd) {
		// ドメインモデル「勤務実績のエラーアラームチェック」を取得する。
		List<ErrorAlarmCondition> listErrorAlarmCon = new ArrayList<>();
		//ドメインモデル「日別実績のエラーアラーム」を取得する
		List<ErrorAlarmWorkRecord> listError = new ArrayList<>();
		if(!errorDailyCheckCd.isEmpty()) {
			listError = errorAlarmRep.getListErAlByListCode(cid, errorDailyCheckCd).stream()
					.filter(x -> x.getUseAtr())
					.collect(Collectors.toList());
		}
		if(!extractConditionWorkRecord.isEmpty()) {
			List<ErrorAlarmCondition> lstErrorAlarmCon = errorConRep.findConditionByListErrorAlamCheckId(extractConditionWorkRecord);
			listErrorAlarmCon.addAll(lstErrorAlarmCon);
		}
		//日次の固定抽出条件のデータを取得する
		DataFixExtracCon dataforDailyFix = this.getDataForDailyFix(lstSid, dPeriod, errorDailyCheckId, cid);
		List<WorkType> listWorkType = new ArrayList<>();	
		//ドメインモデル「勤務実績の抽出条件」を取得する
		List<WorkRecordExtractingCondition> workRecordCond = workRep.getAllWorkRecordExtraConByIdAndUse(extractConditionWorkRecord, 1);
		List<WorkTimeSetting> listWorktime  = new ArrayList<>();
		//社員と期間を条件に日別実績を取得する
		List<IntegrationOfDaily> listIntegrationDai = dailyRecordShareFinder.findByListEmployeeId(lstSid, dPeriod);
		//画面で利用できる勤怠項目一覧を取得する
		List<MonthlyAttendanceItemNameDto> lstItemDay = attendanceAdap.getMonthlyAttendanceItem(1);
		if(!listErrorAlarmCon.isEmpty() || !dataforDailyFix.getListFixConWork().isEmpty()) {
			//<<Public>> 勤務種類をすべて取得する
			listWorkType = workTypeRep.findByCompanyId(cid);
			// 会社で使用できる就業時間帯を全件を取得する
			listWorktime = workTimeRep.findByCompanyId(cid);	
		}	
		
		PrepareData prepareData = new PrepareData(listWorkType, listIntegrationDai, listErrorAlarmCon, 
													workRecordCond, listError, dataforDailyFix, listWorktime, lstItemDay);
		return prepareData;
	}

	/**
	 * 日次の固定抽出条件のデータを取得する
	 * @param lstSid
	 * @param dPeriod
	 * @param errorDailyCheckId
	 * @param cid
	 * @return
	 */
	private DataFixExtracCon getDataForDailyFix(List<String> lstSid,
			DatePeriod dPeriod,  String errorDailyCheckId, String cid) {
		DataFixExtracCon result = new DataFixExtracCon();
		List<FixedConditionData> fixExtrac = new ArrayList<>();
		List<Identification> listIdentity = new ArrayList<>();
		List<StampCard> listStampCard = new ArrayList<>();
		List<WorkingCondition> listWkConItem = new ArrayList<>();
		List<WorkingConditionItem> lstWorkCondItem  = new ArrayList<>();
		// ドメインモデル「勤務実績の固定抽出条件」を取得する
		List<FixedConditionWorkRecord> workRecordExtract = fixCondReRep.getFixConWorkRecordByIdUse(errorDailyCheckId, 1);
		result.setListFixConWork(workRecordExtract);
		if(!workRecordExtract.isEmpty()) {
			fixExtrac = fixConRep.getAllFixedConditionData();
			result.setLstFixConWorkItem(fixExtrac);
		}
			
		for (FixedConditionWorkRecord itemFix : workRecordExtract) {
			
			switch (itemFix.getFixConWorkRecordNo()) {
				case UNIDENTIFIED_PERSON: 
					// 社員一覧本人が確認しているかチェック
					// ドメインモデル「日の本人確認」を取得する
					listIdentity = indentifiRep.findByListEmployeeID(lstSid, dPeriod.start(), dPeriod.end());
					result.setIdentityVerifyStatus(listIdentity);
					//ドメインモデル「本人確認処理の利用設定」を取得
					Optional<IdentityProcess> getIdentityProcessById = indentiryRepo.getIdentityProcessById(cid);
					boolean personConfirm = false;
					if(getIdentityProcessById.isPresent()) personConfirm = getIdentityProcessById.get().getUseDailySelfCk() == 1 ? true : false;
					result.setPersonConfirm(personConfirm);
					break;
				case DATA_CHECK:	
					// 管理者未確認チェック
					// 上司が確認しているかチェックする
					List<ApproveRootStatusForEmpImport> request = approotAdap.getApprovalByListEmplAndListApprovalRecordDateNew(dPeriod.datesBetween(), lstSid, 1);
					result.setAdminUnconfirm(request);
					//ドメインモデル「承認処理の利用設定」を取得
					Optional<ApprovalProcess> getApprovalProcessById = appProcessRepo.getApprovalProcessById(cid);
					boolean approverConfirm = false;
					if(getApprovalProcessById.isPresent()) approverConfirm = getApprovalProcessById.get().getUseDailyBossChk() == 1 ? true : false;
					result.setApproverConfirm(approverConfirm);
					break;
				case CONTRACT_TIME_EXCEEDED:
				case LESS_THAN_CONTRACT_TIME:
				case VIOLATION_DAY_OF_WEEK:
				case ILL_WORK_TIME_DAY_THE_WEEK:
					if(listWkConItem.isEmpty()) {
						listWkConItem = workingConditionRepository.getBySidsAndDatePeriodNew(lstSid, dPeriod);
						lstWorkCondItem = workConditionItemRepo.getBySidsAndDatePeriodNew(lstSid, dPeriod);
						result.setListWkConItem(listWkConItem);
						result.setLstWorkCondItem(lstWorkCondItem);
					}
					break;
				case UNREFLECTED_STAMP:
					//List<社員ID＞から打刻カードを全て取得する
					listStampCard = stampCardRep.getLstStampCardByLstSid(lstSid);
					result.setListStampCard(listStampCard);
					break;
				default:
			}
		}
		return result;
	}
		

	 /**
	  * アラームチェック条件Tab3
	  * 日次のチェック条件のアラーム値を生成する 
	  */
	private OutputCheckResult extractAlarmConditionTab3(WorkRecordExtractingCondition extCond,
			List<ErrorAlarmCondition> listErrorAlarmCon,
			List<IntegrationOfDaily> listIntegrationDai,
			String sid,
			DatePeriod datePeriod,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<MonthlyAttendanceItemNameDto> lstItemDay,
			List<String> extractConditionWorkRecord,
			List<WorkType> lstWkType,
			List<StatusOfEmployeeAdapterAl> lstStatusEmp, List<WorkTimeSetting> listWorktime,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode,
			List<AlarmExtractInfoResult> lstExtractInfoResult) {
		OutputCheckResult result = new OutputCheckResult(new ArrayList<>(), new ArrayList<>(),new ArrayList<>(), new ArrayList<>());
		//「アラーム抽出条件」を作成してInput．List＜アラーム抽出条件＞を追加
		val extractionCond = alarmExtractConditions.stream()
				.filter(x -> x.getAlarmListCheckType() == AlarmListCheckType.FreeCheck && x.getAlarmCheckConditionNo().equals(String.valueOf(extCond.getSortOrderBy())))
				.findAny();
		if (!extractionCond.isPresent()) {
			alarmExtractConditions.add(new AlarmExtractionCondition(
					String.valueOf(extCond.getSortOrderBy()),
					new AlarmCheckConditionCode(alarmCheckConditionCode),
					AlarmCategory.DAILY,
					AlarmListCheckType.FreeCheck
			));
		}
		int renzoku = 0;
		Optional<ErrorAlarmCondition> optErrorAlarm = listErrorAlarmCon.stream()
				.filter(x -> x.getErrorAlarmCheckID().equals(extCond.getErrorAlarmCheckID()))
				.findFirst();
		if(!optErrorAlarm.isPresent()) return result;
		ErrorAlarmCondition errorAlarm = optErrorAlarm.get();
		AlarmMessageValues alMes = new AlarmMessageValues("","","","");
		String alarmMessage = "";
		GeneralDate renzokuDate = datePeriod.start();
		List<WorkTypeCode> lstWorkTypeCond = new ArrayList<>();
		List<WorkTimeCode> lstWorkTimeCond = new ArrayList<>();
		FilterByCompare wTypeCom = FilterByCompare.ALL;
		FilterByCompare wTimeCom = FilterByCompare.ALL;
		for(GeneralDate exDate : datePeriod.datesBetween()) {
			// 社員の会社所属状況をチェック
			List<StatusOfEmployeeAdapterAl> statusOfEmp = lstStatusEmp.stream()
					.filter(x -> x.getEmployeeId().equals(sid) 
							&& !x.getListPeriod().stream()
								.filter(y -> y.start().beforeOrEquals(exDate) && y.end().afterOrEquals(exDate)).collect(Collectors.toList()).isEmpty())
					.collect(Collectors.toList());
			if(statusOfEmp.isEmpty()) continue;
			
			// 日別実績を絞り込む
			List<IntegrationOfDaily> lstDaily = listIntegrationDai.stream()
					.filter(x -> x.getEmployeeId().equals(sid) && x.getYmd().equals(exDate))
					.collect(Collectors.toList());			
			if(lstDaily.isEmpty()) continue;
			
			IntegrationOfDaily integrationDaily = lstDaily.get(0);
			
			if(!optErrorAlarm.isPresent()) continue;
			boolean isWorkTypeChk = false;
			switch (extCond.getCheckItem()) {
			
			case TIME:
			case TIMES:
			case AMOUNT_OF_MONEY:
			case TIME_OF_DAY:
			case CONTINUOUS_CONDITION:
			case CONTINUOUS_TIME:				
				List<ErrorRecord> mapCheck = erAlCheckService.checkWithRecord(exDate, Arrays.asList(sid),
						Arrays.asList(extCond.getErrorAlarmCheckID()),
						Arrays.asList(integrationDaily));
				if(mapCheck.isEmpty() || !mapCheck.get(0).isError()) {
					if(renzoku != 0 && renzoku >= errorAlarm.getContinuousPeriod().v()) {
						//Group 1
						alMes = new AlarmMessageValues("","","","");
						List<ErAlAttendanceItemCondition<?>> lstErCondition = errorAlarm.getAtdItemCondition().getGroup1().getLstErAlAtdItemCon();
						createMess(lstItemDay, lstWkType, extCond, errorAlarm, mapCheck, lstErCondition, alMes);
						this.createExtractAlarmRenzoku(sid,
								lstExtractInfoResult,
								alarmCheckConditionCode,
								new DatePeriod(exDate.addDays(-renzoku), exDate),
								result.getLstResultCondition(),
								extCond.getNameWKRecord().v(),
								alMes,
								Optional.ofNullable(errorAlarm.getDisplayMessage().v()),
								renzoku,
								String.valueOf(extCond.getSortOrderBy()),
								AlarmListCheckType.FreeCheck,
								getWplByListSidAndPeriod,
								errorAlarm.getContinuousPeriod().v(),
								extCond.getCheckItem(),
								lstWorkTypeCond, lstWkType, wTypeCom,
								lstWorkTimeCond, listWorktime, wTimeCom);
					}
					renzoku = 0;
					continue;	
				}
				if(mapCheck.get(0).isError() == true) {
					
					if(extCond.getCheckItem() != TypeCheckWorkRecord.CONTINUOUS_TIME) {
						Optional<AlarmListCheckInfor> optCheckInfor = result.getLstCheckType().stream()
								.filter(x -> x.getChekType() == AlarmListCheckType.FreeCheck && x.getNo().equals(String.valueOf(extCond.getSortOrderBy())))
								.findFirst();
						if(!optCheckInfor.isPresent()) {
							result.getLstCheckType().add(new AlarmListCheckInfor(String.valueOf(extCond.getSortOrderBy()), AlarmListCheckType.FreeCheck));
						}
						alMes = new AlarmMessageValues("","","","");
						//Group 1
						List<ErAlAttendanceItemCondition<?>> lstErCondition = errorAlarm.getAtdItemCondition().getGroup1().getLstErAlAtdItemCon();
						createMess(lstItemDay, lstWkType, extCond, errorAlarm, mapCheck, lstErCondition, alMes);
						//Group 2
						if(errorAlarm.getAtdItemCondition().isUseGroup2()) {
							List<ErAlAttendanceItemCondition<?>> lstErCondition2 = errorAlarm.getAtdItemCondition().getGroup2().getLstErAlAtdItemCon();	
							createMess(lstItemDay, lstWkType, extCond, errorAlarm, mapCheck, lstErCondition2, alMes);
						}
						
						alarmMessage = TextResource.localize("KAL010_78",
								alMes.getWtName().isEmpty() ? "" : alMes.getWorkTypeName(),
								alMes.getWtName().isEmpty() ?  alMes.getWorkTypeName() : alMes.getWtName(),
								alMes.getAttendentName());
						this.createExtractAlarm(sid,
								exDate,
								result.getLstResultCondition(),
								extCond.getNameWKRecord().v(),
								alarmMessage,
								Optional.ofNullable(errorAlarm.getDisplayMessage().v()),
								alMes.getAlarmTarget(),
								String.valueOf(extCond.getSortOrderBy()),
								AlarmListCheckType.FreeCheck,
								getWplByListSidAndPeriod,
								lstExtractInfoResult,
								alarmCheckConditionCode);
					} else {
						renzoku += 1;
						renzokuDate = exDate;
					}
					
				} else {
					if(renzoku != 0  && renzoku >= errorAlarm.getContinuousPeriod().v()) {
						alMes = new AlarmMessageValues("","","","");
						List<ErAlAttendanceItemCondition<?>> lstErCondition = errorAlarm.getAtdItemCondition().getGroup1().getLstErAlAtdItemCon();
						createMess(lstItemDay, lstWkType, extCond, errorAlarm, mapCheck, lstErCondition, alMes);
						this.createExtractAlarmRenzoku(sid,
								lstExtractInfoResult,
								alarmCheckConditionCode,
								new DatePeriod(exDate.addDays(-renzoku), exDate),
								result.getLstResultCondition(),
								extCond.getNameWKRecord().v(),
								alMes,
								Optional.ofNullable(errorAlarm.getDisplayMessage().v()),
								renzoku,
								String.valueOf(extCond.getSortOrderBy()),
								AlarmListCheckType.FreeCheck,
								getWplByListSidAndPeriod,
								errorAlarm.getContinuousPeriod().v(),
								extCond.getCheckItem(),
								lstWorkTypeCond, lstWkType, wTypeCom,
								lstWorkTimeCond, listWorktime, wTimeCom);
					}
					renzoku = 0;
				}
				break;
			case CONTINUOUS_TIME_ZONE:
				wTimeCom = errorAlarm.getWorkTimeCondition().getComparePlanAndActual();
				if(wTimeCom != FilterByCompare.SELECTED) {
					PlanActualWorkTime wtimeCondition = (PlanActualWorkTime) errorAlarm.getWorkTimeCondition();
					lstWorkTimeCond = wtimeCondition.getWorkTimePlan().getLstWorkTime(); 
				} else {
					SingleWorkTime workTimeConCheck = (SingleWorkTime)errorAlarm.getWorkTimeCondition();
					lstWorkTimeCond = workTimeConCheck.getTargetWorkTime().getLstWorkTime();
				}
				
				wTypeCom = errorAlarm.getWorkTypeCondition().getComparePlanAndActual();
				if(wTypeCom == FilterByCompare.NOT_SELECTED) {
					PlanActualWorkType wtypeCondition = (PlanActualWorkType) errorAlarm.getWorkTypeCondition();
					lstWorkTypeCond = wtypeCondition.getWorkTypePlan().getLstWorkType();					
				} else if(wTypeCom == FilterByCompare.SELECTED){
					SingleWorkType wtConCheck = (SingleWorkType) errorAlarm.getWorkTypeCondition();
					lstWorkTypeCond = wtConCheck.getTargetWorkType().getLstWorkType();
				}
					
				
				WorkTypeCode workTypeCd = integrationDaily.getWorkInformation().getRecordInfo().getWorkTypeCode();
				switch (wTypeCom) {
				case ALL:
					isWorkTypeChk = true;
					break;
				case SELECTED:
					if(lstWorkTypeCond.contains(workTypeCd)) {
						isWorkTypeChk = true;
					} else {
						isWorkTypeChk = false;
					}
					break;
				case NOT_SELECTED:
					if(!lstWorkTypeCond.contains(workTypeCd)) {
						isWorkTypeChk = true;
					} else {
						isWorkTypeChk = false;
					}
					break;
				default:
					break;
				}
				WorkTimeCode workTimeCd = integrationDaily.getWorkInformation().getRecordInfo().getWorkTimeCode();
				if(isWorkTypeChk && workTimeCd != null) {
					switch (wTimeCom) {
					case SELECTED:
						if(lstWorkTimeCond.contains(workTimeCd)) {
							isWorkTypeChk = true;
						} else {
							isWorkTypeChk = false;
						}
						break;
					case NOT_SELECTED:
						if(lstWorkTimeCond.contains(workTimeCd)) {
							isWorkTypeChk = false;
						} else {
							isWorkTypeChk = true;
						}
						break;
					default:
						break;
					}
					if(isWorkTypeChk) {
						renzoku += 1;
						renzokuDate = exDate;
					}
				}
				if(!isWorkTypeChk) {
					if(renzoku > 1 && renzoku >= errorAlarm.getContinuousPeriod().v()) {
						this.createExtractAlarmRenzoku(sid,
								lstExtractInfoResult,
								alarmCheckConditionCode,
								new DatePeriod(exDate.addDays(-renzoku), exDate),
								result.getLstResultCondition(),
								extCond.getNameWKRecord().v(),
								alMes,
								Optional.ofNullable(errorAlarm.getDisplayMessage().v()),
								renzoku,
								String.valueOf(extCond.getSortOrderBy()),
								AlarmListCheckType.FreeCheck,
								getWplByListSidAndPeriod,
								errorAlarm.getContinuousPeriod().v(),									
								extCond.getCheckItem(),
								lstWorkTypeCond, lstWkType, wTypeCom,
								lstWorkTimeCond, listWorktime, wTimeCom);
					}
					renzoku = 0;
					renzokuDate = exDate;
				} 				
				break;
			case CONTINUOUS_WORK:
				wTypeCom = errorAlarm.getWorkTypeCondition().getComparePlanAndActual();
				if(wTypeCom != FilterByCompare.SELECTED) {
					PlanActualWorkType wtypeCondition = (PlanActualWorkType) errorAlarm.getWorkTypeCondition();
					lstWorkTypeCond = wtypeCondition.getWorkTypePlan().getLstWorkType();					
				} else {
					SingleWorkType wtConCheck = (SingleWorkType) errorAlarm.getWorkTypeCondition();
					lstWorkTypeCond = wtConCheck.getTargetWorkType().getLstWorkType();
				}						
				WorkTypeCode workTypeCode = integrationDaily.getWorkInformation().getRecordInfo().getWorkTypeCode();
				switch (wTypeCom) {
				case ALL:
					isWorkTypeChk = true;
					break;
				case SELECTED:
					if(lstWorkTypeCond.contains(workTypeCode)) {
						isWorkTypeChk = true;
					}
					break;
				case NOT_SELECTED:
					if(!lstWorkTypeCond.contains(workTypeCode)) {
						isWorkTypeChk = true;
					}
				default:
					break;
				}
				if(isWorkTypeChk) {
					renzoku += 1;
					renzokuDate = exDate;
				} else {
					if(renzoku > 0 && renzoku >= errorAlarm.getContinuousPeriod().v()) {
						this.createExtractAlarmRenzoku(sid,
								lstExtractInfoResult,
								alarmCheckConditionCode,
								new DatePeriod(exDate.addDays(-renzoku), exDate),
								result.getLstResultCondition(),
								extCond.getNameWKRecord().v(),
								alMes,
								Optional.ofNullable(errorAlarm.getDisplayMessage().v()),
								renzoku,
								String.valueOf(extCond.getSortOrderBy()),
								AlarmListCheckType.FreeCheck,
								getWplByListSidAndPeriod,
								errorAlarm.getContinuousPeriod().v(),									
								extCond.getCheckItem(),
								lstWorkTypeCond, lstWkType, wTypeCom,
								lstWorkTimeCond, listWorktime, wTimeCom);
					}
					renzoku = 0;
					renzokuDate = exDate;
				}
				break;
			default:
				break;
			}
		}
		if(renzoku != 0 && renzoku >= errorAlarm.getContinuousPeriod().v()) {
			alMes = new AlarmMessageValues("","","","");
			List<ErAlAttendanceItemCondition<?>> lstErCondition = errorAlarm.getAtdItemCondition().getGroup1().getLstErAlAtdItemCon();
			createMess(lstItemDay, lstWkType, extCond, errorAlarm, new ArrayList<>(), lstErCondition, alMes);
			this.createExtractAlarmRenzoku(sid,
					lstExtractInfoResult,
					alarmCheckConditionCode,
					new DatePeriod(renzokuDate.addDays(-(renzoku-1)), renzokuDate),
					result.getLstResultCondition(),
					extCond.getNameWKRecord().v(),
					alMes,
					Optional.ofNullable(errorAlarm.getDisplayMessage().v()),
					renzoku,
					String.valueOf(extCond.getSortOrderBy()),
					AlarmListCheckType.FreeCheck,
					getWplByListSidAndPeriod,
					errorAlarm.getContinuousPeriod().v(),
					extCond.getCheckItem(),
					lstWorkTypeCond, lstWkType, wTypeCom,
					lstWorkTimeCond, listWorktime, wTimeCom);
		}
		return result;
	}
	
	private void createExtractAlarmRenzoku(String sid,
			List<AlarmExtractInfoResult> alarmExtractInfoResults,
			String alarmCheckConditionCode,
			DatePeriod dateP,
			List<ResultOfEachCondition> listResultCond,
			String alarmName,
			AlarmMessageValues alMes,
			Optional<String> alarmMess,
			int dayRenzoku,
			String alarmCode, AlarmListCheckType checkType,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			int continuousPeriod, TypeCheckWorkRecord typeCheck, 
			List<WorkTypeCode> lstWorkTypeCond, List<WorkType> lstWkType, FilterByCompare wTypeCom,
			List<WorkTimeCode> lstWorkTimeCond, List<WorkTimeSetting> lstWorkTime, FilterByCompare wTimeCom) {
		String wplId = "";
		Optional<WorkPlaceHistImportAl> optWorkPlaceHistImportAl = getWplByListSidAndPeriod.stream().filter(x -> x.getEmployeeId().equals(sid)).findFirst();
		if(optWorkPlaceHistImportAl.isPresent()) {
			Optional<WorkPlaceIdAndPeriodImportAl> optWorkPlaceIdAndPeriodImportAl = optWorkPlaceHistImportAl.get().getLstWkpIdAndPeriod().stream()
					.filter(x -> x.getDatePeriod().start()
							.beforeOrEquals(dateP.start()) 
							&& x.getDatePeriod().end()
							.afterOrEquals(dateP.start())).findFirst();
			if(optWorkPlaceIdAndPeriodImportAl.isPresent()) {
				wplId = optWorkPlaceIdAndPeriodImportAl.get().getWorkplaceId();
			}
		}
		String alarmContent = "";
		if(typeCheck == TypeCheckWorkRecord.CONTINUOUS_TIME) {
			alarmContent = TextResource.localize("KAL010_626", alMes.getWtName().isEmpty() ? "" : alMes.getWorkTypeName(),
					alMes.getWtName().isEmpty() ?  alMes.getWorkTypeName() : alMes.getWtName(),
					alMes.getAttendentName(), String.valueOf(continuousPeriod));	
		} else if (typeCheck == TypeCheckWorkRecord.CONTINUOUS_WORK) {
			String strWt = lstWkType.stream().filter(x -> lstWorkTypeCond.contains(x.getWorkTypeCode()))
					.collect(Collectors.toList())
					.stream().map(a -> a.getWorkTypeCode().v()+ ' ' + a.getName().v())
					.collect(Collectors.joining(","));
			alarmContent = TextResource.localize("KAL010_627",
					wTypeCom == FilterByCompare.SELECTED ? TextResource.localize("KAL010_134") : TextResource.localize("KAL010_135"),
					strWt,
					String.valueOf(continuousPeriod));
		} else if (typeCheck == TypeCheckWorkRecord.CONTINUOUS_TIME_ZONE) {
			String strWt = lstWkType.stream().filter(x -> lstWorkTypeCond.contains(x.getWorkTypeCode()))
					.collect(Collectors.toList())
					.stream().map(a -> a.getWorkTypeCode().v()+ ' ' + a.getName().v()).collect(Collectors.joining(","));
			 
			String strWtime = lstWorkTime.stream().filter(x -> lstWorkTimeCond.contains(x.getWorktimeCode()))
					.collect(Collectors.toList())
					.stream().map(x -> x.getWorktimeCode().v() + ' ' + x.getWorkTimeDisplayName().getWorkTimeName().v())
					.collect(Collectors.joining(","));
			
			alarmContent = TextResource.localize("KAL010_628",
					wTypeCom == FilterByCompare.ALL ? "" : wTypeCom == FilterByCompare.SELECTED ? TextResource.localize("KAL010_134") : TextResource.localize("KAL010_135"),
					wTypeCom == FilterByCompare.ALL ? "全て" : strWt,
					wTimeCom == FilterByCompare.ALL ? "" : wTimeCom == FilterByCompare.SELECTED ? TextResource.localize("KAL010_134") : TextResource.localize("KAL010_135"),
					strWtime,
					String.valueOf(continuousPeriod));
		}

		ExtractResultDetail detail = new ExtractResultDetail(
				new ExtractionAlarmPeriodDate(
						Optional.ofNullable(dateP.start()),
						Optional.ofNullable(dateP.end())
				),
				alarmName,
				alarmContent,
				GeneralDateTime.now(),
				Optional.ofNullable(wplId),
				alarmMess,
				Optional.ofNullable(TextResource.localize("KAL010_625", String.valueOf(dayRenzoku)))
		);

		//「アラーム抽出情報結果」を作
		if (alarmExtractInfoResults.stream()
				.anyMatch(x -> x.getAlarmListCheckType().value == checkType.value
						&& x.getAlarmCheckConditionNo().equals(alarmCode)
						&& x.getAlarmCheckConditionCode().v().equals(alarmCheckConditionCode)
						&& x.getAlarmCategory().value == AlarmCategory.DAILY.value)) {
			for (AlarmExtractInfoResult x : alarmExtractInfoResults) {
				if (x.getAlarmListCheckType().value == checkType.value
						&& x.getAlarmCheckConditionNo().equals(alarmCode)
						&& x.getAlarmCheckConditionCode().v().equals(alarmCheckConditionCode)
						&& x.getAlarmCategory().value == AlarmCategory.DAILY.value) {
					List<ExtractResultDetail> tmp = new ArrayList<>(x.getExtractionResultDetails());
					tmp.add(detail);
					x.setExtractionResultDetails(tmp);
					break;
				}
			}
		} else {
			alarmExtractInfoResults.add(new AlarmExtractInfoResult(
					alarmCode,
					new AlarmCheckConditionCode(alarmCheckConditionCode),
					AlarmCategory.DAILY,
					checkType,
					new ArrayList<>(Arrays.asList(detail))
			));
		}
	}

	private void createMess(List<MonthlyAttendanceItemNameDto> lstItemDay, List<WorkType> lstWkType,
			WorkRecordExtractingCondition extCond, ErrorAlarmCondition errorAlarm, List<ErrorRecord> mapCheck,
			List<ErAlAttendanceItemCondition<?>> lstErCondition, AlarmMessageValues alMes) {
		
		for (ErAlAttendanceItemCondition<?> erCondition : lstErCondition) {
			//比較演算子
			int compare = erCondition.getCompareSingleValue() == null ?
					erCondition.getCompareRange().getCompareOperator().value :
						erCondition.getCompareSingleValue().getCompareOpertor().value;
			List<Integer> lstAddSub = erCondition.getCountableTarget() != null ? erCondition.getCountableTarget().getAddSubAttendanceItems().getAdditionAttendanceItems()
					: Arrays.asList(erCondition.getUncountableTarget().getAttendanceItem());
			List<Integer> lstSubStr = erCondition.getCountableTarget() != null ? erCondition.getCountableTarget().getAddSubAttendanceItems().getSubstractionAttendanceItems()
					: new ArrayList<>();
						
			List<MonthlyAttendanceItemNameDto> addSubName = lstItemDay.stream().filter(x -> lstAddSub.contains(x.getAttendanceItemId()))
					.collect(Collectors.toList());
			List<MonthlyAttendanceItemNameDto> subStrName = lstItemDay.stream().filter(x -> lstSubStr.contains(x.getAttendanceItemId()))
					.collect(Collectors.toList());
			String nameItem = dataCheckSevice.getNameErrorAlarm(addSubName,	0, "");
			nameItem = dataCheckSevice.getNameErrorAlarm(subStrName, 1, nameItem);
			CompareOperatorText compareOperatorText = convertComparaToText.convertCompareType(
					erCondition.getCompareSingleValue() != null 
							? erCondition.getCompareSingleValue().getCompareOpertor().value
							: erCondition.getCompareRange().getCompareOperator().value);
			String startValue = erCondition.getCompareSingleValue() == null ?
					erCondition.getCompareRange().getStartValue().toString() : erCondition.getCompareSingleValue().getValue().toString();
			String endValue = erCondition.getCompareRange() == null ? null : erCondition.getCompareRange().getEndValue().toString();
			if(erCondition.getConditionAtr() == ConditionAtr.TIME_DURATION ) {
				startValue = dataCheckSevice.timeToString((Double.valueOf(startValue).intValue()));
				endValue = endValue == null ? null : dataCheckSevice.timeToString((Double.valueOf(endValue).intValue()));
			}
			if(!mapCheck.isEmpty()) {
				if(erCondition.getConditionAtr() == ConditionAtr.TIME_DURATION ) {
					alMes.setAlarmTarget(alMes.getAlarmTarget() + "," + nameItem + ": " + dataCheckSevice.timeToString((Double.valueOf(mapCheck.get(0).getCheckedValue()).intValue())));
				} else if (erCondition.getConditionAtr() == ConditionAtr.TIME_WITH_DAY) {
					TimeWithDayAttr startTimeDay = new TimeWithDayAttr(Double.valueOf(startValue).intValue());
					startValue = startTimeDay.getFullText();
					TimeWithDayAttr endTimeDay = endValue == null ? null : new TimeWithDayAttr(Double.valueOf(endValue).intValue());
					endValue = endValue == null ? null : endTimeDay.getFullText();
					TimeWithDayAttr targetV =  new TimeWithDayAttr(Double.valueOf(mapCheck.get(0).getCheckedValue()).intValue());
					alMes.setAlarmTarget(alMes.getAlarmTarget() + "," + nameItem + ": " + targetV.getFullText());
				} else {
					alMes.setAlarmTarget(alMes.getAlarmTarget() + "," + nameItem + ": " + mapCheck.get(0).getCheckedValue());
				}
				if(alMes.getAlarmTarget().substring(0,1).equals(",")) {
					alMes.setAlarmTarget(alMes.getAlarmTarget().substring(1));	
				}
			}
			
			
			if(compare <= 5) {
				alMes.setAttendentName(alMes.getAttendentName()  + "," + nameItem + compareOperatorText.getCompareLeft() + startValue);
			} else {
				if (compare > 5 && compare <= 7) {
					alMes.setAttendentName(alMes.getAttendentName()  + "," +   startValue + compareOperatorText.getCompareLeft() + nameItem
							+ compareOperatorText.getCompareright() + endValue);
				} else {
					alMes.setAttendentName(alMes.getAttendentName()  + "," +   nameItem+ compareOperatorText.getCompareright()+ startValue 
							+ "又は " + nameItem + compareOperatorText.getCompareLeft() + endValue);
				}
			}
			if(alMes.getAttendentName().substring(0,1).equals(",")) {
				alMes.setAttendentName(alMes.getAttendentName().substring(1));	
			}
			List<WorkTypeCode> lstWorkType = new ArrayList<>();
			if(errorAlarm.getWorkTypeCondition().getComparePlanAndActual() == FilterByCompare.NOT_SELECTED) {
				PlanActualWorkType plan = (PlanActualWorkType) errorAlarm.getWorkTypeCondition();
				lstWorkType = plan.getWorkTypePlan().getLstWorkType();
			} else if(errorAlarm.getWorkTypeCondition().getComparePlanAndActual() == FilterByCompare.SELECTED) {
				SingleWorkType wtypeConditionDomain = (SingleWorkType) errorAlarm.getWorkTypeCondition();
				lstWorkType = wtypeConditionDomain.getTargetWorkType().getLstWorkType();
			}
			
			
			if(errorAlarm.getWorkTypeCondition().getComparePlanAndActual() == FilterByCompare.ALL) {
				alMes.setWorkTypeName(TextResource.localize("KAL010_133"));
			} else if (errorAlarm.getWorkTypeCondition().getComparePlanAndActual() == FilterByCompare.NOT_SELECTED) {
				alMes.setWorkTypeName(TextResource.localize("KAL010_135"));
			} else {
				alMes.setWorkTypeName(TextResource.localize("KAL010_134"));
			}
			
			if(!lstWorkType.isEmpty()) {
				for(int i = 0; i < lstWorkType.size(); i++) {
					WorkTypeCode wt = lstWorkType.get(i);
					Optional<WorkType> optWt = lstWkType.stream().filter(x -> x.getWorkTypeCode().equals(wt))
							.findFirst();
					if(optWt.isPresent()) alMes.setWtName(alMes.getWtName() + ", " + optWt.get().getWorkTypeCode().v()  + ' ' + optWt.get().getName().v());
				}
				alMes.setWtName(alMes.getWtName().substring(2));
			} else {
				alMes.setWorkTypeName(TextResource.localize("KAL010_133"));
			}
			
		}
	}
	
	/**
	 * 日別実績のエラーアラームのアラーム値を生成する
	 */
	private OutputCheckResult extractAlarmDailyTab2(List<ErrorAlarmWorkRecord> listError,
			List<ErrorAlarmCondition> listErrorAlarmCon,
			IntegrationOfDaily integra,
			String sid,
			GeneralDate day,
			List<MonthlyAttendanceItemNameDto> lstItemDay,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<AlarmExtractionCondition> alarmExtractConditions,
			String alarmCheckConditionCode, List<AlarmExtractInfoResult> alarmExtractInfoResults) {
		List<AlarmListCheckInfor> listAlarmChk = new ArrayList<>();
		List<ResultOfEachCondition> listResultCond = new ArrayList<>();
		OutputCheckResult result = new OutputCheckResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		// 社員の日別実績エラー一覧
		List<EmployeeDailyPerError> listErrorEmp = integra.getEmployeeError();
		if(listError.isEmpty() || listErrorEmp.isEmpty()) return result;
		for(ErrorAlarmWorkRecord item: listError) {

			//「アラーム抽出条件」を作成してInput．List＜アラーム抽出条件＞を追加
			val extractionCond = alarmExtractConditions.stream()
					.filter(x -> x.getAlarmListCheckType() == AlarmListCheckType.FreeCheck && x.getAlarmCheckConditionNo().equals(String.valueOf(item.getCode())))
					.findAny();
			if (!extractionCond.isPresent()) {
				alarmExtractConditions.add(new AlarmExtractionCondition(
						item.getCode().v(),
						new AlarmCheckConditionCode(alarmCheckConditionCode),
						AlarmCategory.DAILY,
						AlarmListCheckType.FreeCheck
				));
			}
			// Input．日別勤怠のエラー一覧を探す
			List<EmployeeDailyPerError> afterFilter = listErrorEmp.stream().filter(x -> x.getErrorAlarmWorkRecordCode().equals(item.getCode()))
																			.collect(Collectors.toList());
			if(listAlarmChk.isEmpty()) {
				listAlarmChk.add(new AlarmListCheckInfor(item.getCode().v(), AlarmListCheckType.FreeCheck));	
			}		
			if(afterFilter.isEmpty()) continue;
			
			List<Integer> attendanceItemList = new ArrayList<>();
			afterFilter.stream().forEach(x -> {
				attendanceItemList.addAll(x.getAttendanceItemList());
			});
			String atttendanceName = "";
			List<MonthlyAttendanceItemNameDto> lstItemName = lstItemDay.stream().filter(x -> attendanceItemList.contains(x.getAttendanceItemId()))
					.collect(Collectors.toList());
					
			for (MonthlyAttendanceItemNameDto dto : lstItemName) {
				atttendanceName += ", " + dto.getAttendanceItemName();
				
			}
			String alarmContent = afterFilter.get(0).getErrorAlarmMessage().isPresent() 
					? afterFilter.get(0).getErrorAlarmMessage().get().v() : "";
			String alarmMess = "";
			List<ErrorAlarmCondition> lstTemp =  listErrorAlarmCon.stream().filter(x -> x.getErrorAlarmCheckID().equals(item.getErrorAlarmCheckID())).collect(Collectors.toList());
			if(!lstTemp.isEmpty()) {
				alarmMess = lstTemp.get(0).getDisplayMessage() == null ? "" : lstTemp.get(0).getDisplayMessage().v();
			}
			createExtractAlarm(sid,
					day,
					listResultCond,
					item.getName().v(),
					alarmContent,
					Optional.ofNullable(alarmMess),
					atttendanceName.isEmpty() ? "" : atttendanceName.substring(2),
					item.getCode().v(),
					AlarmListCheckType.FreeCheck,
					getWplByListSidAndPeriod,
					alarmExtractInfoResults,
					alarmCheckConditionCode);
		}
		return new OutputCheckResult(listResultCond, listAlarmChk, alarmExtractInfoResults, new ArrayList<>());
	}

	private void createExtractAlarm(String sid,
			GeneralDate day,
			List<ResultOfEachCondition> listResultCond,
			String alarmName,
			String alarmContent,
			Optional<String> alarmMess,
			String checkValue,
			String alarmCode, AlarmListCheckType checkType,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<AlarmExtractInfoResult> alarmExtractInfoResults,
			String alarmCheckConditionCode) {
		
		String wplId = "";
		Optional<WorkPlaceHistImportAl> optWorkPlaceHistImportAl = getWplByListSidAndPeriod.stream().filter(x -> x.getEmployeeId().equals(sid)).findFirst();
		if(optWorkPlaceHistImportAl.isPresent()) {
			Optional<WorkPlaceIdAndPeriodImportAl> optWorkPlaceIdAndPeriodImportAl = optWorkPlaceHistImportAl.get().getLstWkpIdAndPeriod().stream()
					.filter(x -> x.getDatePeriod().start()
							.beforeOrEquals(day) 
							&& x.getDatePeriod().end()
							.afterOrEquals(day)).findFirst();
			if(optWorkPlaceIdAndPeriodImportAl.isPresent()) {
				wplId = optWorkPlaceIdAndPeriodImportAl.get().getWorkplaceId();
			}
		}
		ExtractResultDetail detail = new ExtractResultDetail(
				new ExtractionAlarmPeriodDate(
						Optional.ofNullable(day),
						Optional.empty()
				),
				alarmName, 
				alarmContent, 
				GeneralDateTime.now(), 
				Optional.ofNullable(wplId), 
				alarmMess, 
				Optional.ofNullable(checkValue)
		);

		if (alarmExtractInfoResults.stream()
				.anyMatch(x -> x.getAlarmListCheckType().value == checkType.value
						&& x.getAlarmCheckConditionNo().equals(alarmCode)
						&& x.getAlarmCheckConditionCode().v().equals(alarmCheckConditionCode)
						&& x.getAlarmCategory().value == AlarmCategory.DAILY.value)) {
			for (AlarmExtractInfoResult x : alarmExtractInfoResults) {
				if (x.getAlarmCategory() == AlarmCategory.DAILY
						&& x.getAlarmCheckConditionNo().equals(alarmCode)
						&& x.getAlarmCheckConditionCode().v().equals(alarmCheckConditionCode)
						&& x.getAlarmListCheckType().value == checkType.value) {
					List<ExtractResultDetail> tmp = new ArrayList<>(x.getExtractionResultDetails());
					tmp.add(detail);
					x.setExtractionResultDetails(tmp);
					break;
				}
			}
		} else {
			alarmExtractInfoResults.add(new AlarmExtractInfoResult(
					alarmCode,
					new AlarmCheckConditionCode(alarmCheckConditionCode),
					AlarmCategory.DAILY,
					checkType,
					new ArrayList<>(Arrays.asList(detail))
			));
		}
	}
	
	/**
	 * 日次の固定抽出条件のアラーム値を生成する
	 */
	private OutputCheckResult extractAlarmFixTab4(PrepareData prepareData,
			IntegrationOfDaily integra,
			String sid,
			GeneralDate baseDate,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<AlarmExtractionCondition> alarmExtractConditions,
			String alarmCheckConditionCode, List<AlarmExtractInfoResult> alarmExtractInfoResults) {
		DataFixExtracCon dataforDailyFix = prepareData.getDataforDailyFix();
		List<WorkType> listWorkType = prepareData.getListWorkType();
		List<WorkTimeSetting> listWorktime = prepareData.getListWorktime();
		List<MonthlyAttendanceItemNameDto> lstItemDay = prepareData.getLstItemDay();
		List<ResultOfEachCondition> listResultCond = new ArrayList<>();
		List<AlarmListCheckInfor> listAlarmChk = new ArrayList<>();		
		
		List<FixedConditionWorkRecord> listFixedConWk = dataforDailyFix.getListFixConWork();
		if(listFixedConWk.isEmpty()) return new OutputCheckResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		
		for(FixedConditionWorkRecord item : listFixedConWk) {
			String alarmMessage = new String();
			String alarmTarget = new String();

			//「アラーム抽出条件」を作成してInput．List＜アラーム抽出条件＞を追加
			val extractionCond = alarmExtractConditions.stream()
					.filter(x -> x.getAlarmListCheckType() == AlarmListCheckType.FixCheck && x.getAlarmCheckConditionNo().equals(String.valueOf(item.getFixConWorkRecordNo().value)))
					.findAny();
			if (!extractionCond.isPresent()) {
				alarmExtractConditions.add(new AlarmExtractionCondition(
						String.valueOf(item.getFixConWorkRecordNo().value),
						new AlarmCheckConditionCode(alarmCheckConditionCode),
						AlarmCategory.DAILY,
						AlarmListCheckType.FixCheck
				));
			}

			List<AlarmListCheckInfor> listAlarmChkTmp = listAlarmChk.stream()
					.filter(x -> x.getChekType() == AlarmListCheckType.FixCheck && x.getNo().equals(String.valueOf(item.getFixConWorkRecordNo().value)))
					.collect(Collectors.toList());			
			if(listAlarmChkTmp.isEmpty()) {
				listAlarmChk.add(new AlarmListCheckInfor(String.valueOf(item.getFixConWorkRecordNo().value), 
						AlarmListCheckType.FixCheck));	
			}		
			FixedConditionData itemData = dataforDailyFix.getLstFixConWorkItem().stream().filter(x -> x.getFixConWorkRecordNo().value == item.getFixConWorkRecordNo().value)
					.collect(Collectors.toList()).get(0);
			if(item.getFixConWorkRecordNo() == WorkRecordFixedCheckItem.ADMINISTRATOR_NOT_CONFIRMED && integra == null) {
				alarmMessage = TextResource.localize("KAL010_66");
				alarmTarget = TextResource.localize("KAL010_74");
				this.createExtractAlarm(sid,
						baseDate,
						listResultCond,
						itemData.getFixConWorkRecordName().v(),
						alarmMessage,
						Optional.ofNullable(item.getMessage().v()),
						alarmTarget,
						String.valueOf(item.getFixConWorkRecordNo().value),
						AlarmListCheckType.FixCheck,
						getWplByListSidAndPeriod,
						alarmExtractInfoResults,
						alarmCheckConditionCode);
				continue;
			}
			if(integra == null) continue;
			
			List<EmployeeDailyPerError> lstDailyError = new ArrayList<>();
			String itemName = "";
			String companyId = AppContexts.user().companyId();
			ErrorInfo errorInfo = new ErrorInfo("", "");
			switch(item.getFixConWorkRecordNo()) {
			
				// NO=1:勤務種類未登録
				case WORK_TYPE_NOT_REGISTERED:
					// 勤務種類コード
					WorkTypeCode wkType = integra.getWorkInformation().getRecordInfo().getWorkTypeCode();
					Optional<WorkType> listWk = listWorkType.stream()
							.filter(x -> x.getWorkTypeCode().equals(wkType)).findFirst();
					if(!listWk.isPresent()) {
						alarmMessage = TextResource.localize("KAL010_7", wkType.v());
						alarmTarget = TextResource.localize("KAL010_76", wkType.v());
					}
					break;
				// NO=2:就業時間帯未登録
				case WORKING_HOURS_NOT_REGISTERED:
					// 就業時間帯コード
					WorkTimeCode wkTime = integra.getWorkInformation().getRecordInfo().getWorkTimeCode();
					if(wkTime == null) break;
					Optional<WorkTimeSetting> optWtime = listWorktime.stream()
							.filter(x -> x.getWorktimeCode().equals(wkTime)).findFirst();
					if(!optWtime.isPresent()) {
						alarmMessage = TextResource.localize("KAL010_9", wkTime.v());
						alarmTarget = TextResource.localize("KAL010_77", wkTime.v());
					}
					break;
				// NO=3:本人未確認チェック
				case UNIDENTIFIED_PERSON:
					if(!dataforDailyFix.isPersonConfirm()) break;
					
					List<Identification> identityVerifyStatus = dataforDailyFix.getIdentityVerifyStatus().stream()
								.filter(x -> x.getEmployeeId().equals(sid) && x.getProcessingYmd().equals(baseDate))
								.collect(Collectors.toList());
					
					if(identityVerifyStatus.isEmpty()) {
						alarmMessage = TextResource.localize("KAL010_43");
						alarmTarget = TextResource.localize("KAL010_72");
					}
					break;
				// NO=4:管理者未確認チェック
				case DATA_CHECK:
					if(!dataforDailyFix.isApproverConfirm()) break;
					
					List<ApproveRootStatusForEmpImport> adminUnconfirm = dataforDailyFix.getAdminUnconfirm()
							.stream().filter(x -> x.getAppDate().equals(baseDate) 
									&& x.getEmployeeID().equals(sid) 
									&& x.getApprovalStatus() == ApprovalStatusForEmployee.UNAPPROVED)
							.collect(Collectors.toList());
					
					if(!adminUnconfirm.isEmpty()) {
						alarmMessage = TextResource.localize("KAL010_45");
						alarmTarget = TextResource.localize("KAL010_73");
					}
					break;
				// NO = 6： 打刻漏れ
				case CONTINUOUS_VATATION_CHECK:
					// 打刻漏れ
					lstDailyError = dailyAlermService.lackOfTimeLeavingStamping(integra);
					if(lstDailyError.isEmpty()) break;
					 
					alarmMessage = TextResource.localize("KAL010_79", getItemName(lstDailyError, lstItemDay));
					alarmTarget = getItemName(lstDailyError, lstItemDay);
					break;
				// NO =7:打刻漏れ(入退門)
				case GATE_MISS_STAMP:
					// 入退門打刻漏れ
					lstDailyError = dailyAlermService.lackOfAttendanceGateStamping(integra);
					if(lstDailyError.isEmpty()) break;
					itemName = getItemName(lstDailyError, lstItemDay);
					if(itemName.isEmpty()) break;
					alarmMessage = TextResource.localize("KAL010_80",  getItemName(lstDailyError, lstItemDay));
					alarmTarget = TextResource.localize(getItemName(lstDailyError, lstItemDay));
					break;
				// NO =8： 打刻順序不正
				case MISS_ORDER_STAMP:
					lstDailyError = dailyAlermService.stampIncorrectOrderAlgorithm(integra);
					if(lstDailyError.isEmpty()) break;
					itemName = getItemNameWithValue(lstDailyError, integra, lstItemDay);
					if(itemName.isEmpty()) break;
					alarmMessage = TextResource.localize("KAL010_81", itemName);
					alarmTarget = TextResource.localize(itemName);
				case GATE_MISS_ORDER_STAMP:
					errorInfo = this.getCheckFix9(integra, companyId,lstItemDay);
					break;
					
				case MISS_HOLIDAY_STAMP:
					errorInfo = this.getCheckFix10(integra,  listWorkType,lstItemDay);
					if(errorInfo.alarmMessage.isEmpty()) break;
					
					alarmMessage = errorInfo.alarmMessage;
					alarmTarget = errorInfo.alarmTarget;				
					break;
					
				case GATE_MISS_HOLIDAY_STAMP:
					break;
					
				case ADDITION_NOT_REGISTERED:
					break;
					
				case CONTRACT_TIME_EXCEEDED:
					errorInfo = this.checkContracTime(dataforDailyFix,
							integra,
							true);
					break;
					
				case LESS_THAN_CONTRACT_TIME:
					errorInfo = this.checkContracTime(dataforDailyFix,
							integra,
							false);
					break;
				case VIOLATION_DAY_OF_WEEK:
					errorInfo = this.getCheckFix15(dataforDailyFix,
							integra,
							listWorkType);
					break;
				case ILL_WORK_TIME_DAY_THE_WEEK:
					errorInfo = this.getCheckFix16(dataforDailyFix,
							integra,
							listWorkType,
							listWorktime);
					break;
					
				case DISSOCIATION_ERROR:
					errorInfo = this.getCheckFix17(integra, companyId, lstItemDay);
					break;
					
				case MANUAL_INPUT:
					errorInfo = this.getCheckFix18(integra, lstItemDay);
					break;
					
				case DOUBLE_STAMP:
					errorInfo = this.getCheckFix19(integra, lstItemDay);
					break;
					
				case UNCALCULATED:
					CalculationState calculationState = integra.getWorkInformation().getCalculationState();
					if(calculationState == CalculationState.No_Calculated) {
						alarmMessage = TextResource.localize("KAL010_20");
						alarmTarget =  TextResource.localize("KAL010_20");
					}
					
					break;
					
				case OVER_APP_INPUT:
					List<ErrorInfo> lstErrorInfo = new ArrayList<>();
					//残業時間実績超過
					ErrorInfo errorOt = this.checkOtTimeOver(integra, lstItemDay);
					if(!errorOt.alarmMessage.isEmpty()) {
						lstErrorInfo.add(errorOt);
					}
					//休出時間実績超過
					ErrorInfo errorHw = this.checkHolidayWorkTimeOver(integra);
					if(!errorHw.alarmMessage.isEmpty()) {
						lstErrorInfo.add(errorHw);
					}
					//深夜時間実績超過
					ErrorInfo errorMidNight = this.checkMidnight(integra, lstItemDay);
					if(!errorMidNight.alarmMessage.isEmpty()) {
						lstErrorInfo.add(errorMidNight);
					}					
					//フレックス時間超過チェック
					ErrorInfo errorFlex = this.checkFlex(integra, lstItemDay);
					if(!errorFlex.alarmMessage.isEmpty()) {
						lstErrorInfo.add(errorFlex);
					}
					for (ErrorInfo x : lstErrorInfo) {
						this.createExtractAlarm(sid,
								baseDate,
								listResultCond,
								itemData.getFixConWorkRecordName().v(),
								x.alarmMessage,
								Optional.ofNullable(item.getMessage().v()),
								x.alarmTarget,
								String.valueOf(item.getFixConWorkRecordNo().value),
								AlarmListCheckType.FixCheck,
								getWplByListSidAndPeriod,
								alarmExtractInfoResults,
								alarmCheckConditionCode);
					}
					break;
					
				case MULTI_WORK_TIMES:
					errorInfo = this.getCheckFix22(integra);
					break;
					
				case TEMPORARY_WORK:
					errorInfo = this.getCheckFix23(integra);
					break;
					
				case SPEC_DAY_WORK:
					errorInfo = this.getCheckFix24(integra, listWorkType, companyId, getWplByListSidAndPeriod);
					break;
					
				case UNREFLECTED_STAMP:
					errorInfo = this.getCheckFix25(prepareData, sid, baseDate);
					break;
					
				case ACTUAL_STAMP_OVER:
					errorInfo = this.getCheckFix26(integra);
					break;
					
				case GATE_DOUBLE_STAMP:
					errorInfo = this.getCheckFix27(integra);
					break;
					
				case DISSOCIATION_ALARM:
					errorInfo = this.getCheckFix28(integra, companyId, lstItemDay);
					
					break;
				
				default:
					break;
			}
			if(!errorInfo.alarmMessage.isEmpty()) {
				alarmMessage = errorInfo.alarmMessage;
				alarmTarget = errorInfo.alarmTarget;	
			}
			
			if(!alarmMessage.isEmpty()) {
				this.createExtractAlarm(sid,
						baseDate,
						listResultCond,
						itemData.getFixConWorkRecordName().v(),
						alarmMessage,
						Optional.ofNullable(item.getMessage().v()),
						alarmTarget,
						String.valueOf(item.getFixConWorkRecordNo().value),
						AlarmListCheckType.FixCheck,
						getWplByListSidAndPeriod,
						alarmExtractInfoResults,
						alarmCheckConditionCode);
			}
		}
		return new OutputCheckResult(listResultCond, listAlarmChk, alarmExtractInfoResults, new ArrayList<>());
	}
	/**
	 * 28.乖離アラーム
	 * @param integra
	 * @param companyId
	 * @param lstItemDay
	 * @param alarmMessage
	 * @param alarmTarget
	 */
	private ErrorInfo getCheckFix28(IntegrationOfDaily integra,
			String companyId,
			List<MonthlyAttendanceItemNameDto> lstItemDay) {
		ErrorInfo result = new ErrorInfo("", "");	
		if(!integra.getAttendanceTimeOfDailyPerformance().isPresent()) return result;
		
		List<EmployeeDailyPerError> employeeAlarm = divTimeCheckService.divergenceTimeCheckBySystemFixed(companyId,
				integra.getEmployeeId(),
				integra.getYmd(),
				integra.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getDivTime().getDivergenceTime(),
				Optional.empty());
		
		String[] lstKairiAlarm= {SystemFixedErrorAlarm.DIVERGENCE_ALARM_10.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ALARM_1.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ALARM_2.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ALARM_3.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ALARM_4.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ALARM_5.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ALARM_6.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ALARM_7.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ALARM_8.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ALARM_9.name()};
		List<String> lstDivergenceAlarm = Arrays.asList(lstKairiAlarm);
		if(employeeAlarm.isEmpty()) return result;
		
		//取得した社員の日別実績エラー一覧を探す
		List<EmployeeDailyPerError> lstErrorAlarmDivergence = employeeAlarm
				.stream().filter(a -> lstDivergenceAlarm.contains(a.getErrorAlarmWorkRecordCode().v()))
				.collect(Collectors.toList());
		if(lstErrorAlarmDivergence.isEmpty()) return result;
		
		List<Integer> attendanceItemList = new ArrayList<>();
		lstErrorAlarmDivergence.stream().forEach(x -> attendanceItemList.addAll(x.getAttendanceItemList()));
		
		List<DivergenceTime> divergenceTime = integra.getAttendanceTimeOfDailyPerformance().get()
				.getActualWorkingTimeOfDaily().getDivTime().getDivergenceTime()
				.stream().filter(x -> attendanceItemList.contains(x.getDivTimeId()))
				.collect(Collectors.toList());					
		if(divergenceTime.isEmpty()) return result;
		for(DivergenceTime x : divergenceTime) {
			Optional<MonthlyAttendanceItemNameDto> optAttItem = lstItemDay.stream()
					.filter(a -> a.getAttendanceItemId() == x.getDivTimeId()).findFirst();
			result.alarmMessage += "/" + optAttItem.get().getAttendanceItemName() 
					+ " " + converAttendanceTimeOfExistMinusToStr(x.getDivTime());
			
		}
		result.alarmMessage = result.alarmMessage.substring(1);
		result.alarmTarget = result.alarmTarget;
		return result;
	}
	/**
	 * 27:二重打刻(入退門)チェック
	 * @param integra
	 * @param alarmMessage
	 * @param alarmTarget
	 */
	private ErrorInfo getCheckFix27(IntegrationOfDaily integra) {
		ErrorInfo result = new ErrorInfo("", "");	
		if(!integra.getAttendanceLeavingGate().isPresent()
				|| !integra.getAttendanceLeavingGate().get().getAttendanceLeavingGate(new WorkNo(2)).isPresent()) {
			return result;
		}
		AttendanceLeavingGate attendanceLeavingGates = integra.getAttendanceLeavingGate().get().getAttendanceLeavingGate(new WorkNo(2)).get();
		result.alarmMessage = TextResource.localize("KAL010_605");
		String startTime = "";
		String endTime = "";
		if(attendanceLeavingGates.getAttendance().isPresent()
				&& attendanceLeavingGates.getAttendance().get().getTimeDay().getTimeWithDay().isPresent()) {
			TimeWithDayAttr timeWithDayStr = attendanceLeavingGates.getAttendance().get().getTimeDay().getTimeWithDay().get();
			startTime = timeWithDayStr.getFullText();
		}
		if(attendanceLeavingGates.getLeaving().isPresent()
				&& attendanceLeavingGates.getLeaving().get().getTimeDay().getTimeWithDay().isPresent()) {
			TimeWithDayAttr timeWithDayStr = attendanceLeavingGates.getLeaving().get().getTimeDay().getTimeWithDay().get();
			endTime = timeWithDayStr.getFullText();
		}
		result.alarmTarget = TextResource.localize("KAL010_624", startTime + '～' + endTime);
		return result;
	}
	/**
	 * 26.実打刻オーバー
	 */
	private ErrorInfo getCheckFix26(IntegrationOfDaily integra) {
		ErrorInfo result = new ErrorInfo("", "");	
		Optional<TimeLeavingOfDailyAttd> attendanceLeave = integra.getAttendanceLeave();
		if(!attendanceLeave.isPresent()
				|| attendanceLeave.get().getTimeLeavingWorks().isEmpty()) return result;
		String attendanceActual = "";
		String attendance = "";
		String leaveActual = "";
		String strLeave = "";
		for(TimeLeavingWork timeLeavingW : attendanceLeave.get().getTimeLeavingWorks()) {
			
			//出勤時刻をチェック
			if(timeLeavingW.getAttendanceStamp().isPresent()
					&& timeLeavingW.getAttendanceStamp().get().getActualStamp().isPresent()
					&& timeLeavingW.getAttendanceStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().isPresent()
					&& (!timeLeavingW.getAttendanceStamp().get().getStamp().isPresent()
							|| !timeLeavingW.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
							|| timeLeavingW.getAttendanceStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().get().v()
								> timeLeavingW.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v()
							)
				) {
				attendanceActual += "/" + timeLeavingW.getAttendanceStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().get().v();
				attendance += "/" + timeLeavingW.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
			}
			//退勤時刻をチェック
			if(timeLeavingW.getLeaveStamp().isPresent()
					&& timeLeavingW.getLeaveStamp().get().getActualStamp().isPresent()
					&& timeLeavingW.getLeaveStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().isPresent()
					&& (!timeLeavingW.getLeaveStamp().get().getStamp().isPresent()
							|| !timeLeavingW.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
							|| timeLeavingW.getLeaveStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().get().v()
								> timeLeavingW.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v()
							)
				) {
				leaveActual += "/" + timeLeavingW.getLeaveStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().get().v();
				strLeave += "/" + timeLeavingW.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
			}
		}
		
		if(!attendanceActual.isEmpty()) {
			result.alarmMessage = TextResource.localize("KAL010_629", attendanceActual.substring(1), attendance.substring(1));
		}
		if(!leaveActual.isEmpty()) {
			result.alarmMessage += "\n" + TextResource.localize("KAL010_630", leaveActual.substring(1), strLeave.substring(1));
		}
		result.alarmTarget = result.alarmMessage;
		return result;
	}
	/**
	 * 25.未反映打刻
	 * @param prepareData
	 * @param sid
	 * @param baseDate
	 * @param alarmMessage
	 * @param alarmTarget
	 */
	private ErrorInfo getCheckFix25(PrepareData prepareData,
			String sid,
			GeneralDate baseDate) {
		ErrorInfo result = new ErrorInfo("", "");	
		//打刻カード番号
		List<StampCard> lstStampCard = prepareData.getDataforDailyFix().getListStampCard().stream()
			.filter(x -> x.getEmployeeId().equals(sid)).collect(Collectors.toList());
		if(lstStampCard.isEmpty()) return result;
		
		List<StampNumber> stampNumber = lstStampCard.stream().map(x-> x.getStampNumber()).collect(Collectors.toList());
		//ドメインモデル「打刻」を取得する
		List<Stamp> lstStampDakoku = stampDakokuRepo.get(AppContexts.user().contractCode(), stampNumber, baseDate)
				.stream().filter(a -> a.getImprintReflectionStatus().isReflectedCategory() && a.getStampDateTime().toDate().equals(baseDate))
				.collect(Collectors.toList());
		if(lstStampDakoku.isEmpty()) return result;
		String strClockArt = lstStampDakoku.stream().map(x -> x.getType().getChangeClockArt().nameId).collect(Collectors.joining(","));
		result.alarmMessage = TextResource.localize("KAL010_623", strClockArt);
		result.alarmTarget =  lstStampDakoku.stream().map(x -> TextResource.localize("KAL010_35", x.getType().getChangeClockArt().nameId, lstStampDakoku.get(0).getStampDateTime().toString()))
				.collect(Collectors.joining(","));
		return result;
	}
	
	/**
	 * 24.特定日出勤
	 */
	private ErrorInfo getCheckFix24(IntegrationOfDaily integra,
			List<WorkType> lstWorkType,
			String companyId,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod) {
		ErrorInfo result = new ErrorInfo("", "");	
		//1日半日出勤・1日休日系の判定
		WorkTypeCode wtypeCode = integra.getWorkInformation().getRecordInfo().getWorkTypeCode();
		if(wtypeCode == null) return result;
		
		Optional<WorkType> optWtype = lstWorkType.stream().filter(x -> x.getWorkTypeCode().equals(wtypeCode)).findFirst();
		if(!optWtype.isPresent()) return result;
		WorkType wType = optWtype.get();
		//1日休日の場合
		if(wType.chechAttendanceDay().isHoliday()) return result;
		
		String wplId = "";
		Optional<WorkPlaceHistImportAl> optWorkPlaceHistImportAl = getWplByListSidAndPeriod.stream().filter(x -> x.getEmployeeId().equals(integra.getEmployeeId())).findFirst();
		if(optWorkPlaceHistImportAl.isPresent()) {
			Optional<WorkPlaceIdAndPeriodImportAl> optWorkPlaceIdAndPeriodImportAl = optWorkPlaceHistImportAl.get().getLstWkpIdAndPeriod().stream()
					.filter(x -> x.getDatePeriod().start()
							.beforeOrEquals(integra.getYmd()) 
							&& x.getDatePeriod().end()
							.afterOrEquals(integra.getYmd())).findFirst();
			if(optWorkPlaceIdAndPeriodImportAl.isPresent()) {
				wplId = optWorkPlaceIdAndPeriodImportAl.get().getWorkplaceId();
			}
		}
		//1日休日じゃないの場合
		// 職場の特定日設定を取得する (Acquire specific day setting of the workplace)
		RecSpecificDateSettingImport specificDateSetting = specificDateSettingAdapter.specificDateSettingService(companyId, wplId, integra.getYmd());
		//取得した「特定日」をチェック
		if(specificDateSetting == null || specificDateSetting.getNumberList().isEmpty()) return result;
		
		//特定日項目NO（List）から特定日を取得
		// アラーム表示値を生成する
		String speName = specificDateSettingAdapter.getSpecifiDateItem(companyId, specificDateSetting.getNumberList()).stream()
				.map(x -> x.getSpecificName()).collect(Collectors.joining(","));
		result.alarmMessage = TextResource.localize("KAL010_33", speName, wType.getName().v());
		result.alarmTarget =  TextResource.localize("KAL010_622", wType.getName().v());
		
		return result;
	}
	/**
	 * 23.臨時勤務チェック
	 * @param integra
	 * @param alarmMessage
	 * @param alarmTarget
	 */
	private ErrorInfo getCheckFix23(IntegrationOfDaily integra) {
		ErrorInfo result = new ErrorInfo("", "");				
		Optional<TimeLeavingOfDailyAttd> optAttendanceLeave3 = integra.getAttendanceLeave();	
		if(!optAttendanceLeave3.isPresent() 
				|| optAttendanceLeave3.get().getTimeLeavingWorks().size() < 3) return result;
		
		Optional<TimeLeavingWork> timeLeavingWork3 = optAttendanceLeave3.get().getAttendanceLeavingWork(2);
		String strAttendanceSt3 = "";
		if(timeLeavingWork3.isPresent() 
				&& timeLeavingWork3.get().getAttendanceStamp().isPresent()
				&& timeLeavingWork3.get().getAttendanceStamp().get().getActualStamp().isPresent()
				&& timeLeavingWork3.get().getAttendanceStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
			TimeWithDayAttr timeWithDay = timeLeavingWork3.get()
					.getAttendanceStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().get();
			strAttendanceSt3 = timeWithDay.getRawTimeWithFormat();
		}
		String strleaveStamp3 = "";
		if(timeLeavingWork3.isPresent() 
				&& timeLeavingWork3.get().getLeaveStamp().isPresent()
				&& timeLeavingWork3.get().getLeaveStamp().get().getActualStamp().isPresent()
				&& timeLeavingWork3.get().getLeaveStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
			TimeWithDayAttr timeWithDay = timeLeavingWork3.get()
					.getLeaveStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().get();
			strleaveStamp3 = timeWithDay.getRawTimeWithFormat();
		}
		if(!strAttendanceSt3.isEmpty() || !strleaveStamp3.isEmpty()) {
			result.alarmMessage = TextResource.localize("KAL010_25", strAttendanceSt3 + '～' + strleaveStamp3);
			result.alarmTarget =  TextResource.localize("KAL010_621", strAttendanceSt3 + '～' + strleaveStamp3);	
		}
		return result;
	}
	/**
	 *  22.複数回勤務
	 * @param integra
	 * @param alarmMessage
	 * @param alarmTarget
	 */
	private ErrorInfo getCheckFix22(IntegrationOfDaily integra) {
		ErrorInfo result = new ErrorInfo("", "");		
		Optional<TimeLeavingOfDailyAttd> optAttendanceLeave = integra.getAttendanceLeave();					
		if(!optAttendanceLeave.isPresent() 
				|| optAttendanceLeave.get().getTimeLeavingWorks().size() < 2) return result;
		
		Optional<TimeLeavingWork> timeLeavingWork2 = optAttendanceLeave.get().getAttendanceLeavingWork(1);
		String strAttendanceSt = "";
		if(timeLeavingWork2.isPresent() 
				&& timeLeavingWork2.get().getAttendanceStamp().isPresent()) {
			TimeWithDayAttr timeWithDay = timeLeavingWork2.get().getAttendanceStamp().get().getActualStamp().isPresent() 
					? timeLeavingWork2.get().getAttendanceStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().get()
							: (timeLeavingWork2.get().getAttendanceStamp().get().getStamp().isPresent() ? timeLeavingWork2.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get() : null);
			strAttendanceSt = timeWithDay.getFullText();
		}
		String strleaveStamp = "";
		if(timeLeavingWork2.isPresent() 
				&& timeLeavingWork2.get().getLeaveStamp().isPresent()) {
			TimeWithDayAttr timeWithDay = timeLeavingWork2.get().getLeaveStamp().get().getActualStamp().isPresent() 
					? timeLeavingWork2.get().getLeaveStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay().get()
							: (timeLeavingWork2.get().getLeaveStamp().get().getStamp().isPresent() ? timeLeavingWork2.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get() : null);
			strleaveStamp = timeWithDay.getFullText();
		}
		if(!strAttendanceSt.isEmpty() || !strleaveStamp.isEmpty()) {
			result.alarmMessage = TextResource.localize("KAL010_25", strAttendanceSt , strleaveStamp);
			result.alarmTarget =  TextResource.localize("KAL010_621", strAttendanceSt, strleaveStamp);	
		}
		return result;
	}
	/**
	 * 19.二重打刻チェック
	 * @param integra
	 * @param lstItemDay
	 * @param alarmMessage
	 * @param alarmTarget
	 */
	private ErrorInfo getCheckFix19(IntegrationOfDaily integra,
			List<MonthlyAttendanceItemNameDto> lstItemDay) {
		
		ErrorInfo result = new ErrorInfo("", "");		
		List<EmployeeDailyPerError> lstDoubleStamp = dailyAlermService.doubleStampAlgorithm(integra);
		if(lstDoubleStamp.isEmpty()) return result;
		List<Integer> lstAttItemId = new ArrayList<Integer>();
		for(EmployeeDailyPerError err : lstDoubleStamp) {
			lstAttItemId.addAll(err.getAttendanceItemList());
		}
		
		String strItemNameD = lstItemDay.stream()
				.filter(x -> lstAttItemId.contains(x.getAttendanceItemId()))
				.map(a -> a.getAttendanceItemName()).collect(Collectors.joining(","));
		
		result.alarmMessage = TextResource.localize("KAL010_16");
		result.alarmTarget =  TextResource.localize("KAL010_617", strItemNameD);
		return result;
	}
	/**
	 * 18.手入力の値を抽出する
	 * @param integra
	 * @param lstItemDay
	 * @param alarmMessage
	 * @param alarmTarget
	 */
	private ErrorInfo getCheckFix18(IntegrationOfDaily integra,
			List<MonthlyAttendanceItemNameDto> lstItemDay) {
		ErrorInfo result = new ErrorInfo("", "");
		List<EditStateOfDailyAttd> lstEditState = integra.getEditState().stream()
				.filter(x -> x.getEditStateSetting() == EditStateSetting.HAND_CORRECTION_MYSELF 
					|| x.getEditStateSetting() == EditStateSetting.HAND_CORRECTION_OTHER)
				.collect(Collectors.toList());
		if(lstEditState.isEmpty()) return result;
		List<Integer> lstAttItemId = lstEditState.stream().map(x -> x.getAttendanceItemId()).collect(Collectors.toList());
		String strItemName = lstItemDay.stream().filter(x -> lstAttItemId.contains(x.getAttendanceItemId())).map(a -> a.getAttendanceItemName()).collect(Collectors.joining(","));
		result.alarmMessage = TextResource.localize("KAL010_16");
		result.alarmTarget =  TextResource.localize("KAL010_617", strItemName);
		return result;
	}
	/**
	 * 17.乖離エラーチェック
	 */
	private ErrorInfo getCheckFix17(IntegrationOfDaily integra,
			String companyId,
			List<MonthlyAttendanceItemNameDto> lstItemDay) {
		
		ErrorInfo result = new ErrorInfo("", "");
		
		if(!integra.getAttendanceTimeOfDailyPerformance().isPresent()) return result;
		
		List<EmployeeDailyPerError> employeeError = divTimeCheckService.divergenceTimeCheckBySystemFixed(companyId,
				integra.getEmployeeId(),
				integra.getYmd(),
				integra.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getDivTime().getDivergenceTime(),
				Optional.empty());
		
		String[] lstKairiError= {SystemFixedErrorAlarm.DIVERGENCE_ERROR_1.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ERROR_2.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ERROR_3.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ERROR_4.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ERROR_5.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ERROR_6.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ERROR_7.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ERROR_8.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ERROR_9.name(),
				SystemFixedErrorAlarm.DIVERGENCE_ERROR_10.name()};
		List<String> lstDivergenceError = Arrays.asList(lstKairiError);
		if(employeeError.isEmpty()) return result;
		
		//取得した社員の日別実績エラー一覧を探す
		List<EmployeeDailyPerError> lstErrorAlarmDivergence = employeeError
				.stream().filter(a -> lstDivergenceError.contains(a.getErrorAlarmWorkRecordCode().v()))
				.collect(Collectors.toList());
		
		if(!lstErrorAlarmDivergence.isEmpty()) {
			List<Integer> attendanceItemList = new ArrayList<>();
			lstErrorAlarmDivergence.stream().forEach(x -> attendanceItemList.addAll(x.getAttendanceItemList()));
			
			List<DivergenceTime> divergenceTime = integra.getAttendanceTimeOfDailyPerformance().get()
					.getActualWorkingTimeOfDaily().getDivTime().getDivergenceTime()
					.stream().filter(x -> attendanceItemList.contains(x.getDivTimeId()))
					.collect(Collectors.toList());
			if(divergenceTime.isEmpty()) return result;
			for(DivergenceTime x : divergenceTime) {
				Optional<MonthlyAttendanceItemNameDto> optAttItem = lstItemDay.stream()
						.filter(a -> a.getAttendanceItemId() == x.getDivTimeId()).findFirst();
				result.alarmMessage += "/" + optAttItem.get().getAttendanceItemName() 
						+ " " + converAttendanceTimeOfExistMinusToStr(x.getDivTime());
				
			}
			result.alarmMessage = result.alarmMessage.substring(1);
			result.alarmTarget = result.alarmMessage;
		}
		return result;
	}
	/**
	 * 10.休日打刻チェック
	 */
	private ErrorInfo getCheckFix10(IntegrationOfDaily integra,
			List<WorkType> lstWorkType, List<MonthlyAttendanceItemNameDto> lstItemDay) {
		Optional<EmployeeDailyPerError> optDailyError = dailyAlermService.checkHolidayStamp(integra);
		ErrorInfo result = new ErrorInfo("", "");
		if(!optDailyError.isPresent()) return result;
		
		WorkTypeCode wtCode = integra.getWorkInformation().getRecordInfo().getWorkTypeCode();
		String wtName = lstWorkType.stream().filter(x -> x.getWorkTypeCode().equals(wtCode)).map(x -> x.getName().v()).findFirst().get();
		List<TimeLeavingWork> lstTimeLeavingWork = integra.getAttendanceLeave().get().getTimeLeavingWorks();
		if(lstTimeLeavingWork.isEmpty()) return result;
		String time = "";
		for(TimeLeavingWork x : lstTimeLeavingWork) {
			String startTime = "";
			String endTime = "";
			String itemName = "";
			if(x.getAttendanceStamp().isPresent()) {
				Optional<TimeWithDayAttr> optStamp = x.getAttendanceStamp().get().getActualStamp().isPresent() ? x.getAttendanceStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay()
						: (x.getAttendanceStamp().get().getStamp().isPresent() ? x.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay() : Optional.empty());
				if(optStamp.isPresent()) {
					if(x.getWorkNo().v() == 1) {
						itemName = getItemName(lstItemDay, 31);
					} else if(x.getWorkNo().v() == 2) {
						itemName = getItemName(lstItemDay, 41);
					}
				} 
				startTime = itemName + ':' +  optStamp.get().getFullText();
			}
			
			if(x.getLeaveStamp().isPresent()) {
				Optional<TimeWithDayAttr> optStamp = x.getLeaveStamp().get().getActualStamp().isPresent() ? x.getLeaveStamp().get().getActualStamp().get().getTimeDay().getTimeWithDay()
						: (x.getLeaveStamp().get().getStamp().isPresent() ? x.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay() : Optional.empty());
				if(optStamp.isPresent()) {
					if(x.getWorkNo().v() == 1) {
						itemName = getItemName(lstItemDay, 34);
					} else if (x.getWorkNo().v() == 2) {
						itemName = getItemName(lstItemDay, 44);
					}
					endTime = itemName + ":" + optStamp.get().getFullText();
				}
			}
			
			time = "," + startTime + (endTime.isEmpty() ? endTime : "/" + endTime);
		}
		result.alarmMessage = TextResource.localize("KAL010_5",
				wtName,
				time.substring(1));
		result.alarmTarget = TextResource.localize("KAL010_613", time.substring(1));
		return result;
	}

	private String getItemName(List<MonthlyAttendanceItemNameDto> lstItemDay, int itemId) {
		Optional<MonthlyAttendanceItemNameDto> optItemName = lstItemDay.stream().filter(a -> a.getAttendanceItemId() == itemId).findFirst();
		return optItemName.isPresent() ? optItemName.get().getAttendanceItemName() : "未登録";
	}
	/**
	 * 9.打刻順序不正（入退門）チェック
	 * @param integra
	 * @param companyId
	 * @param baseDate
	 * @param sid
	 * @param lstItemDay
	 * @param alarmMessage
	 * @param alarmTarget
	 */
	private ErrorInfo getCheckFix9(IntegrationOfDaily integra,
			String companyId,
			List<MonthlyAttendanceItemNameDto> lstItemDay) {
		ErrorInfo result = new ErrorInfo("", "");
		AttendanceLeavingGateOfDaily attendanceLeavingGateOfDaily = integra.getAttendanceLeavingGate()
				.isPresent()
						? new AttendanceLeavingGateOfDaily(integra.getEmployeeId(),
								integra.getYmd(), integra.getAttendanceLeavingGate().get())
						: null;
		TimeLeavingOfDailyPerformance timeLeavingOfDailyPerformance = new TimeLeavingOfDailyPerformance(integra.getEmployeeId(),
				integra.getYmd(), integra.getAttendanceLeave().orElse(null));
		List<EmployeeDailyPerError>  lstDailyError = exitStampIncorrectOrderCheck.exitStampIncorrectOrderCheck(companyId, integra.getEmployeeId(), integra.getYmd(),
				attendanceLeavingGateOfDaily, timeLeavingOfDailyPerformance);
		if(lstDailyError.isEmpty()) return result;				
		String itemName = getItemNameWithValue(lstDailyError, integra, lstItemDay);
		if(itemName.isEmpty()) return result;
		result.alarmMessage = TextResource.localize("KAL010_82", itemName);
		result.alarmTarget = TextResource.localize(itemName);
		return result;
	}
	/**
	 * フレックス時間超過チェック
	 * @param integra
	 * @param lstItemDay
	 * @return
	 */
	private ErrorInfo checkFlex(IntegrationOfDaily integra,
			List<MonthlyAttendanceItemNameDto> lstItemDay) {
		String alarmMessage = "";
		String alarmTarget = "";
		List<EmployeeDailyPerError>  lstFlexError =  integra.getErrorList(integra.getEmployeeId(), 
				integra.getYmd(),
				SystemFixedErrorAlarm.FLEX_OVER_TIME,
				CheckExcessAtr.FLEX_OVER_TIME);
		
		if(lstFlexError.isEmpty()) return new ErrorInfo(alarmMessage, alarmTarget);
		
		TimeDivergenceWithCalculationMinusExist flexTimeData = integra.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
				.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getFlexTime();
		String strNameItem = "";
		for(EmployeeDailyPerError flexError : lstFlexError) {
			strNameItem = lstItemDay.stream().filter(x -> flexError.getAttendanceItemList().get(0) == x.getAttendanceItemId()).findFirst().isPresent() ?
					lstItemDay.stream().filter(x -> flexError.getAttendanceItemList().get(0) == x.getAttendanceItemId()).findFirst().get().getAttendanceItemName() : "未登録";					
		}
		AttendanceTimeOfExistMinus flexTime = flexTimeData.getTime();
		String strFlectTime = converAttendanceTimeOfExistMinusToStr(flexTime);
		AttendanceTimeOfExistMinus calFlexTime = flexTimeData.getCalcTime();
		String strCalFlexTime = converAttendanceTimeOfExistMinusToStr(calFlexTime);
		
		alarmMessage = TextResource.localize("KAL010_603", strNameItem, strCalFlexTime, strFlectTime);
		alarmTarget = TextResource.localize("KAL010_620", strNameItem, strFlectTime);
				
		 return new ErrorInfo(alarmMessage, alarmTarget);
	}

	private String converAttendanceTimeOfExistMinusToStr(AttendanceTimeOfExistMinus flexTime) {
		return flexTime.hour() + ":" + (flexTime.minute() < 10 ? "0" + flexTime.minute() : flexTime.minute());
	}
	
	/**
	 * 深夜時間実績超過
	 * @param integra
	 * @param lstItemDay
	 * @return
	 */
	private ErrorInfo checkMidnight(IntegrationOfDaily integra,
			List<MonthlyAttendanceItemNameDto> lstItemDay) {
		String alarmMessage = "";
		String alarmTarget = "";
		List<EmployeeDailyPerError> lstMidnightError = integra.getErrorList(integra.getEmployeeId(), 
				integra.getYmd(),
				SystemFixedErrorAlarm.MIDNIGHT_EXCESS,
				CheckExcessAtr.MIDNIGHT_EXCESS);
		if(lstMidnightError.isEmpty()) return new ErrorInfo(alarmMessage, alarmTarget);
		
		for(EmployeeDailyPerError midnighError : lstMidnightError) {
			for(int midnighItemID : midnighError.getAttendanceItemList()) {
				AttendanceTime midnighData = null; 
				AttendanceTime calMidnighData  = null;
				
				//外深夜時間
				if(midnighItemID == 563) {
					midnighData = integra.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
							.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getExcessOfStatutoryMidNightTime().getTime().getTime();
					calMidnighData =  integra.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
							.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getExcessOfStatutoryMidNightTime().getTime().getCalcTime();
					
				} else if (midnighItemID == 561) { //内深夜時間
					midnighData = integra.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
							.getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getWithinStatutoryMidNightTime().getTime().getTime();
					calMidnighData =  integra.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
							.getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getWithinStatutoryMidNightTime().getTime().getCalcTime();
					
				}				
				String strMidnighData = converAttendanceTimeToString(midnighData);
				String strCalMidnighData = converAttendanceTimeToString(calMidnighData);
				String itemName = lstItemDay.stream().filter(x -> x.getAttendanceItemId() == midnighItemID).findFirst().isPresent() ?
						lstItemDay.stream().filter(x -> x.getAttendanceItemId() == midnighItemID).findFirst().get().getAttendanceItemName() : "未登録";
				
				alarmMessage += "\n" + TextResource.localize("KAL010_603", itemName, strCalMidnighData, strMidnighData);
				alarmTarget += "\n" +  TextResource.localize("KAL010_620", itemName, strCalMidnighData);
			}
		}
		
		return new ErrorInfo(alarmMessage, alarmTarget);
	}
	
	/**
	 * 休出時間実績超過
	 * @param integra
	 * @param integra
	 * @return
	 */
	private ErrorInfo checkHolidayWorkTimeOver(IntegrationOfDaily integra) {
		String alarmMessage = "";
		String alarmTarget = "";
		List<EmployeeDailyPerError> lstHwError =  integra.getErrorList(integra.getEmployeeId(), 
				integra.getYmd(),
				SystemFixedErrorAlarm.REST_TIME_EXCESS,
				CheckExcessAtr.REST_TIME_EXCESS);
		if(lstHwError.isEmpty()) return new ErrorInfo(alarmMessage, alarmTarget);
		
		List<HolidayWorkFrameTime> lstHolidayWorkTimeData = integra.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
				.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().getHolidayWorkFrameTime();
		List<WorkdayoffFrame> lstHWtimeFrameName = workDayOffFrameRepo.findByUseAtr(AppContexts.user().companyId(), NotUseAtr.USE.value);
		//List<Integer> lstHolidayWorkItemId = Arrays.asList(266, 271, 276, 281, 286, 291, 296, 301, 306, 311);
		//List<Integer> lstTranferTimeItemId = Arrays.asList(267, 272, 277, 282, 287, 292, 297, 302, 307, 312);
		for(EmployeeDailyPerError hwError: lstHwError) {
			for(int itemId : hwError.getAttendanceItemList()) {
				HolidayWorkFrameTime hwFrameData = null;
				String strHwFrameName = "";
				switch (itemId) {
				case 266:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 1).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 1).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 1).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				case 271:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 2).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 2).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 2).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				case 276:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 3).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 3).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 3).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				case 281:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 4).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 4).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 4).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				case 286:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 5).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 5).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 5).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				case 291:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 6).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 6).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 6).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				case 296:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 7).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 7).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 7).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				case 301:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 8).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 8).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 8).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				case 306:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 9).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 9).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 9).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				case 311:
					hwFrameData = lstHolidayWorkTimeData.stream().filter(x -> x.getHolidayFrameNo().v() == 10).findFirst().get();
					strHwFrameName = lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 10).findFirst().isPresent() ?
							lstHWtimeFrameName.stream().filter(x -> x.getWorkdayoffFrNo().v().intValue() == 10).findFirst().get().getWorkdayoffFrName().v() : "未登録";
					break;
				default:
					break;
				}
				//休出枠時間．休出時間．時間
				AttendanceTime hwTime = hwFrameData.getHolidayWorkTime().get().getTime();
				String strHwTime = converAttendanceTimeToString(hwTime);
				//休出枠時間．休出時間．計算時間
				AttendanceTime calHwtime = hwFrameData.getHolidayWorkTime().get().getCalcTime();
				String strCalHwTime =  converAttendanceTimeToString(calHwtime);
				//休出枠時間．振替時間．時間
				AttendanceTime tranferTime = hwFrameData.getTransferTime().get().getTime();
				String strTranferTime = converAttendanceTimeToString(tranferTime);
				//休出枠時間．振替時間．計算時間
				AttendanceTime calTranferTime = hwFrameData.getTransferTime().get().getCalcTime();
				String strCalTranferTime = converAttendanceTimeToString(calTranferTime);
				alarmMessage += "\n" + TextResource.localize("KAL010_602", 
						strHwFrameName,
						strCalHwTime,
						strCalTranferTime,
						strHwTime,
						strTranferTime);
				alarmTarget +=  "\n" + TextResource.localize("KAL010_619",
						strHwFrameName,
						strHwTime,
						strTranferTime);
			}
		}
		
		return new ErrorInfo(alarmMessage, alarmTarget);
	}
	
	/**
	 * 残業時間実績超過
	 * @param integra
	 * @param lstItemDay
	 * @return
	 */
	private ErrorInfo checkOtTimeOver(IntegrationOfDaily integra,
			List<MonthlyAttendanceItemNameDto> lstItemDay) {
		List<EmployeeDailyPerError> lstOtError = integra.getErrorList(integra.getEmployeeId(), 
				integra.getYmd(),
				SystemFixedErrorAlarm.OVER_TIME_EXCESS,
				CheckExcessAtr.OVER_TIME_EXCESS);
		ErrorInfo resutl =  new ErrorInfo("", "");
		if(lstOtError.isEmpty()) return resutl;
		
		List<OverTimeFrameTime> lstOtTime = integra.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
				.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime();
		//List<Integer> lstOvetTimeID = Arrays.asList(216, 221, 226, 231, 236, 241, 246, 251, 256, 261);
		//List<Integer> lstTranferID = Arrays.asList(217, 222, 227, 232, 237, 242, 247, 252, 257, 262);
		List<OvertimeWorkFrame> lstOtNameFrame = overtimeWorkFrameRepository.getOvertimeWorkFrameByFrameByCom(AppContexts.user().companyId(), NotUseAtr.USE.value);
		
		for(EmployeeDailyPerError otError : lstOtError) {
			List<MonthlyAttendanceItemNameDto> lstOtItem = lstItemDay.stream()
					.filter(x -> otError.getAttendanceItemList().contains(x.getAttendanceItemId())).collect(Collectors.toList());
			if(lstOtItem.isEmpty()) continue;
			for(MonthlyAttendanceItemNameDto otItem : lstOtItem) {
				OverTimeFrameTime otFrameTime = null;
				String otNameFrame = "";
				switch (otItem.getAttendanceItemId()) {
				case 216:
					otFrameTime = lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 1).findFirst().get();									
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 1).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 1).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
										 
					break;
				case 221:
					otFrameTime =  lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 2).findFirst().get();
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 2).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 2).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
					break;
				case 226:
					otFrameTime = lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 3).findFirst().get();
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 3).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 3).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
					break;
				case 231:
					otFrameTime = lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 4).findFirst().get();
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 4).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 4).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
					break;
				case 236:
					otFrameTime = lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 5).findFirst().get();
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 5).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 5).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
					break;
				case 241:
					otFrameTime = lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 6).findFirst().get();
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 6).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 6).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
					break;
				case 246:
					otFrameTime = lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 7).findFirst().get();
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 7).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 7).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
					break;
				case 251:
					otFrameTime =  lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 8).findFirst().get();
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 8).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 8).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
					break;
				case 256:
					otFrameTime = lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 9).findFirst().get();
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 9).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 9).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
					break;
				case 261:
					otFrameTime = lstOtTime.stream().filter(x -> x.getOverWorkFrameNo().v() == 10).findFirst().get();
					otNameFrame = lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 10).findFirst().isPresent() ? 
							 lstOtNameFrame.stream().filter(x -> x.getOvertimeWorkFrNo().v().intValue() == 10).findFirst().get().getOvertimeWorkFrName().v() : "未登録";
					break;
				default:
					break;
				}
				//残業枠時間．残業時間．時間
				AttendanceTime otAtTime =  otFrameTime.getOverTimeWork().getTime();
				String strOtAtTime = converAttendanceTimeToString(otAtTime);
				//残業枠時間．残業時間．計算時間
				AttendanceTime calOtTime = otFrameTime.getOverTimeWork().getCalcTime();
				String strCalOtTime = converAttendanceTimeToString(calOtTime);
				//残業枠時間．振替時間．時間
				AttendanceTime transferTime = otFrameTime.getTransferTime().getTime();
				String strTransferTime = converAttendanceTimeToString(transferTime);
				//残業枠時間．振替時間．計算時間
				AttendanceTime calTransferTime = otFrameTime.getTransferTime().getCalcTime();
				String strCalTransferTime = converAttendanceTimeToString(calTransferTime);
				resutl.alarmMessage += "\n" + TextResource.localize("KAL010_602", 
						otNameFrame,
						strCalOtTime,
						strCalTransferTime,
						strOtAtTime,
						strTransferTime);
				resutl.alarmTarget +=  "\n" + TextResource.localize("KAL010_619",
						otNameFrame,
						strOtAtTime,
						strTransferTime);
			}
		}
		return resutl;
	}

	private String converAttendanceTimeToString(AttendanceTime otAtTime) {
		return otAtTime.hour() + ":" + (otAtTime.minute() < 10 ? "0" : "") + otAtTime.minute();
	}
	/**
	 * 16.曜日別の就業時間帯不正チェック
	 * @return
	 */
	private ErrorInfo getCheckFix16(DataFixExtracCon dataforDailyFix,
			IntegrationOfDaily integra,
			List<WorkType> lstWorkType,
			List<WorkTimeSetting> lstWorktime) {
		ErrorInfo result =  new ErrorInfo("", "");
		/*
		 * WorkTypeCode workTypeCode =
		 * integra.getWorkInformation().getRecordInfo().getWorkTypeCode();
		 * if(workTypeCode == null) return result;
		 * 
		 * Optional<WorkType> optWorkType = lstWorkType.stream().filter(x ->
		 * x.getWorkTypeCode().equals(workTypeCode)).findFirst();
		 * if(!optWorkType.isPresent()) return result;
		 * 
		 * WorkType workTypeRecord = optWorkType.get();
		 * 
		 * Optional<WorkingConditionItem> optWorkingConditionItem =
		 * this.getWorkingConditionItem(integra.getEmployeeId(), integra.getYmd(),
		 * dataforDailyFix); if(!optWorkingConditionItem.isPresent() ||
		 * (!optWorkingConditionItem.get().getWorkDayOfWeek().getMonday().isPresent() &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getTuesday().isPresent() &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getWednesday().isPresent()
		 * &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getThursday().isPresent()
		 * && !optWorkingConditionItem.get().getWorkDayOfWeek().getFriday().isPresent()
		 * &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getSaturday().isPresent()
		 * &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getSunday().isPresent()))
		 * return result; WorkingConditionItem workingConditionItem =
		 * optWorkingConditionItem.get(); //該当の単一日勤務予定を探す Optional<SingleDaySchedule>
		 * optSingleDaySchedule =
		 * workingConditionItem.getWorkDayOfWeek().getSingleDaySchedule(integra.getYmd()
		 * );
		 * 
		 * PersonalWorkCategory workCategory = workingConditionItem.getWorkCategory();
		 * 
		 * AlarmMessageValues wTimeCheck = new AlarmMessageValues();
		 * 
		 * //勤務実績の勤務情報．勤務情報．勤務種類コードの休出かどうかの判断() if(workTypeRecord.isHolidayWork()) {
		 * wTimeCheck = this.checkWorktimeHoliday(workTypeRecord,
		 * integra.getWorkInformation().getRecordInfo().getWorkTimeCode(),
		 * workingConditionItem.getWorkCategory(), optSingleDaySchedule, lstWorktime); }
		 * else { wTimeCheck = this.checkWorktimeNotHoliday(workTypeRecord,
		 * integra.getWorkInformation().getRecordInfo().getWorkTimeCode(),
		 * optSingleDaySchedule, workCategory.getWeekdayTime(), lstWorktime,
		 * integra.getYmd()); } if(wTimeCheck.getAlarmTarget() != null) {
		 * result.alarmMessage = TextResource.localize("KAL010_93",
		 * wTimeCheck.getWorkTypeName(), wTimeCheck.getWtName(),
		 * wTimeCheck.getAlarmTarget()); result.alarmTarget = wTimeCheck.getWtName() +
		 * " " + wTimeCheck.getWorkTypeName(); }
		 */
		return result;
	}
	/**
	 * 日次の勤務種類が休出以外のチェック
	 * @param workTypeRecord
	 * @param worktimeCodeRecord
	 * @param optSingleDaySchedule
	 * @param lstWorktime
	 * @param baseDate
	 * @return
	 */
	private AlarmMessageValues checkWorktimeNotHoliday(WorkType workTypeRecord,
			WorkTimeCode worktimeCodeRecord,
			Optional<SingleDaySchedule> optSingleDaySchedule,
			SingleDaySchedule weekdayTime,
			List<WorkTimeSetting> lstWorktime,
			GeneralDate baseDate) {
		AlarmMessageValues result = new AlarmMessageValues();
		/*
		 * if(worktimeCodeRecord == null) return result;
		 * 
		 * //Input．個人曜日別勤務の単一日勤務予定をチェック if(optSingleDaySchedule.isPresent() &&
		 * optSingleDaySchedule.get().getWorkTimeCode().isPresent()) { //曜日の就業時間帯を比較
		 * WorkTimeCode workTimeCodeDay =
		 * optSingleDaySchedule.get().getWorkTimeCode().get();
		 * if(!workTimeCodeDay.equals(worktimeCodeRecord)) { Optional<WorkTimeSetting>
		 * optWorkTimeSettingRecord = lstWorktime.stream() .filter(x ->
		 * x.getWorktimeCode().equals(worktimeCodeRecord)) .findFirst(); //実績又はスケの勤務種類情報
		 * result.setWorkTypeName(optWorkTimeSettingRecord.isPresent() ?
		 * optWorkTimeSettingRecord.get().getWorktimeCode().v() + " " +
		 * optWorkTimeSettingRecord.get().getWorkTimeDisplayName().getWorkTimeName().v()
		 * : "未登録"); //項目エラ int day = baseDate.localDate().getDayOfWeek().getValue();
		 * String description = EnumAdaptor.valueOf(day - 1,
		 * nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.
		 * DayOfWeek.class).description; result.setWtName(description); //個人の就業時間帯情報
		 * Optional<WorkTimeSetting> optWorkTimeSettingDay = lstWorktime.stream()
		 * .filter(x -> x.getWorktimeCode().equals(workTimeCodeDay)) .findFirst();
		 * result.setAlarmTarget(optWorkTimeSettingDay.isPresent() ?
		 * (optWorkTimeSettingDay.get().getWorktimeCode().v() + " " +
		 * optWorkTimeSettingDay.get().getWorkTimeDisplayName().getWorkTimeName().v()) :
		 * "未登録"); } } else { //Input．平日時の単一日勤務予定．Optional＜就業時間帯コード＞をチェック
		 * if(weekdayTime.getWorkTimeCode().isPresent() &&
		 * !weekdayTime.getWorkTimeCode().get().equals(worktimeCodeRecord)) {
		 * Optional<WorkTimeSetting> optWorkTimeSettingRecord = lstWorktime.stream()
		 * .filter(x -> x.getWorktimeCode().equals(worktimeCodeRecord)) .findFirst();
		 * //実績又はスケの勤務種類情報 result.setWorkTypeName(optWorkTimeSettingRecord.isPresent() ?
		 * optWorkTimeSettingRecord.get().getWorktimeCode().v() + " " +
		 * optWorkTimeSettingRecord.get().getWorkTimeDisplayName().getWorkTimeName().v()
		 * : "未登録"); //項目エラ result.setWtName(TextResource.localize("KAL010_600"));
		 * //個人の就業時間帯情報 Optional<WorkTimeSetting> optWorkTimeSettingDay =
		 * lstWorktime.stream() .filter(x ->
		 * x.getWorktimeCode().equals(weekdayTime.getWorkTimeCode().get()))
		 * .findFirst(); result.setAlarmTarget(optWorkTimeSettingDay.isPresent() ?
		 * (optWorkTimeSettingDay.get().getWorktimeCode().v() + " " +
		 * optWorkTimeSettingDay.get().getWorkTimeDisplayName().getWorkTimeName().v()) :
		 * "未登録"); } }
		 */
		
		return result;
	}
	
	/**
	 * 日次の勤務種類が休出のチェック
	 * @param workTypeRecord
	 * @param worktimeCodeRecord
	 * @param workCategory
	 * @param optSingleDaySchedule
	 * @param lstWorktime
	 * @return
	 */
	private AlarmMessageValues checkWorktimeHoliday(WorkType workTypeRecord,
			WorkTimeCode worktimeCodeRecord,
			PersonalWorkCategory workCategory,
			Optional<SingleDaySchedule> optSingleDaySchedule,
			List<WorkTimeSetting> lstWorktime) {
		AlarmMessageValues result = new AlarmMessageValues();
		/*
		 * if(worktimeCodeRecord == null) return result; String str1 = ""; String str2 =
		 * "";
		 * 
		 * //Input．個人勤務日区分別勤務．公休出勤時をチェック
		 * if(!workCategory.getPublicHolidayWork().isPresent() ||
		 * !workCategory.getPublicHolidayWork().get().getWorkTimeCode().isPresent() ||
		 * !workCategory.getPublicHolidayWork().get().getWorkTypeCode().isPresent() ||
		 * !workTypeRecord.getWorkTypeCode().equals(workCategory.getPublicHolidayWork().
		 * get().getWorkTypeCode().get())) { HolidayAtr holidayAtr =
		 * workTypeRecord.getWorkTypeSetList().get(0).getHolidayAtr();
		 * //Input．勤務種類の休日区分をチェック if(holidayAtr == HolidayAtr.STATUTORY_HOLIDAYS) {
		 * //Input．個人勤務日区分別勤務．法内休出時をチェック
		 * if(!workCategory.getInLawBreakTime().isPresent() ||
		 * !workCategory.getInLawBreakTime().get().getWorkTimeCode().isPresent()) {
		 * ErrorInfo getErrorInfo = this.getErrorInfo(workCategory.getHolidayWork(),
		 * worktimeCodeRecord, lstWorktime); str1 = getErrorInfo.alarmMessage; str2 =
		 * getErrorInfo.alarmTarget; } else { //法内休出時の就業時間帯を比較
		 * if(!workCategory.getInLawBreakTime().get().getWorkTimeCode().get().equals(
		 * worktimeCodeRecord)) { str1 = TextResource.localize("KAL010_95");
		 * Optional<WorkTimeSetting> optWorkTimeSetting = lstWorktime.stream() .filter(x
		 * -> x.getWorktimeCode().equals(workCategory.getInLawBreakTime().get().
		 * getWorkTimeCode().get())) .findFirst(); str2 = optWorkTimeSetting.isPresent()
		 * ? optWorkTimeSetting.get().getWorktimeCode().v() + " " +
		 * optWorkTimeSetting.get().getWorkTimeDisplayName().getWorkTimeName().v() :
		 * "未登録"; } } } else if (holidayAtr == HolidayAtr.NON_STATUTORY_HOLIDAYS) {
		 * //Input．個人勤務日区分別勤務．法外休出時をチェック
		 * if(!workCategory.getOutsideLawBreakTime().isPresent() ||
		 * !workCategory.getOutsideLawBreakTime().get().getWorkTimeCode().isPresent()) {
		 * ErrorInfo getErrorInfo = this.getErrorInfo(workCategory.getHolidayWork(),
		 * worktimeCodeRecord, lstWorktime); str1 = getErrorInfo.alarmMessage; str2 =
		 * getErrorInfo.alarmTarget; } else { //法外休出時の就業時間帯を比較
		 * if(!workCategory.getOutsideLawBreakTime().get().getWorkTimeCode().get().
		 * equals(worktimeCodeRecord)) { str1 = TextResource.localize("KAL010_96");
		 * Optional<WorkTimeSetting> optWorkTimeSetting = lstWorktime.stream() .filter(x
		 * -> x.getWorktimeCode().equals(workCategory.getOutsideLawBreakTime().get().
		 * getWorkTimeCode().get())) .findFirst(); str2 = optWorkTimeSetting.isPresent()
		 * ? optWorkTimeSetting.get().getWorktimeCode().v() + " " +
		 * optWorkTimeSetting.get().getWorkTimeDisplayName().getWorkTimeName().v() :
		 * "未登録"; } } } else { //Input．個人勤務日区分別勤務．祝日休出時をチェック
		 * if(!workCategory.getHolidayAttendanceTime().isPresent() ||
		 * !workCategory.getHolidayAttendanceTime().get().getWorkTimeCode().isPresent())
		 * { ErrorInfo getErrorInfo = this.getErrorInfo(workCategory.getHolidayWork(),
		 * worktimeCodeRecord, lstWorktime); str1 = getErrorInfo.alarmMessage; str2 =
		 * getErrorInfo.alarmTarget; } else { //祝日休出時の就業時間帯を比較する
		 * if(!workCategory.getHolidayAttendanceTime().get().getWorkTimeCode().get().
		 * equals(worktimeCodeRecord)) { str1 = TextResource.localize("KAL010_97");
		 * Optional<WorkTimeSetting> optWorkTimeSetting = lstWorktime.stream() .filter(x
		 * -> x.getWorktimeCode().equals(workCategory.getHolidayAttendanceTime().get().
		 * getWorkTimeCode().get())) .findFirst(); str2 = optWorkTimeSetting.isPresent()
		 * ? optWorkTimeSetting.get().getWorktimeCode().v() + " " +
		 * optWorkTimeSetting.get().getWorkTimeDisplayName().getWorkTimeName().v() :
		 * "未登録"; } } }
		 * 
		 * } else { //公休出勤時の就業時間帯を比較
		 * if(workCategory.getPublicHolidayWork().get().getWorkTimeCode().isPresent() &&
		 * !workCategory.getPublicHolidayWork().get().getWorkTimeCode().get().equals(
		 * worktimeCodeRecord)) { str1 = TextResource.localize("KAL010_94");
		 * Optional<WorkTimeSetting> optWorkTimeSetting = lstWorktime.stream() .filter(x
		 * -> x.getWorktimeCode().equals(workCategory.getPublicHolidayWork().get().
		 * getWorkTimeCode().get())) .findFirst(); str2 = optWorkTimeSetting.isPresent()
		 * ? (optWorkTimeSetting.get().getWorktimeCode().v() + " " +
		 * optWorkTimeSetting.get().getWorkTimeDisplayName().getWorkTimeName().v()) :
		 * "未登録"; } } if(!str1.isEmpty()) { //実績又はスケの勤務種類情報
		 * result.setWorkTypeName(workTypeRecord.getWorkTypeCode().v() + " " +
		 * workTypeRecord.getName().v()); //項目エラ result.setWtName(str1); //個人の就業時間帯情報
		 * result.setAlarmTarget(str2); }
		 */
		return result;
	}
	/**
	 * Input．個人勤務日区分別勤務．休日出勤時をチェック
	 * @param holidayWork
	 * @return
	 */
	private ErrorInfo getErrorInfo(SingleDaySchedule holidayWork,
			WorkTimeCode worktimeCodeRecord,
			List<WorkTimeSetting> lstWorktime) {
		ErrorInfo result = new ErrorInfo("", "");
		
		/*
		 * if(holidayWork == null || !holidayWork.getWorkTimeCode().isPresent()) {
		 * return result; } //休日出勤時の就業時間帯と比較
		 * if(!holidayWork.getWorkTimeCode().get().equals(worktimeCodeRecord)) {
		 * result.alarmMessage = TextResource.localize("KAL010_98");
		 * Optional<WorkTimeSetting> optWorkTimeSetting = lstWorktime.stream().filter(a
		 * -> a.getWorktimeCode().equals(holidayWork.getWorkTimeCode().get()))
		 * .findFirst(); result.alarmTarget = optWorkTimeSetting.isPresent() ?
		 * (optWorkTimeSetting.get().getWorktimeCode().v() + " " +
		 * optWorkTimeSetting.get().getWorkTimeDisplayName().getWorkTimeName().v()) :
		 * "未登録"; }
		 */
		return result;
	}
	
	/**
	 * 15.曜日別の違反チェック
	 * @param sid
	 * @param baseDate
	 * @param dataforDailyFix
	 * @param integra
	 * @return
	 */
	private ErrorInfo getCheckFix15(DataFixExtracCon dataforDailyFix,
			IntegrationOfDaily integra,
			List<WorkType> lstWorkType) {
		ErrorInfo result = new ErrorInfo("","");
		/*
		 * Optional<WorkingConditionItem> optWorkingConditionItem =
		 * this.getWorkingConditionItem(integra.getEmployeeId(), integra.getYmd(),
		 * dataforDailyFix); if(!optWorkingConditionItem.isPresent() ||
		 * (!optWorkingConditionItem.get().getWorkDayOfWeek().getMonday().isPresent() &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getTuesday().isPresent() &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getWednesday().isPresent()
		 * &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getThursday().isPresent()
		 * && !optWorkingConditionItem.get().getWorkDayOfWeek().getFriday().isPresent()
		 * &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getSaturday().isPresent()
		 * &&
		 * !optWorkingConditionItem.get().getWorkDayOfWeek().getSunday().isPresent()))
		 * return result; //該当の単一日勤務予定を探す Optional<SingleDaySchedule>
		 * optSingleDaySchedule =
		 * optWorkingConditionItem.get().getWorkDayOfWeek().getSingleDaySchedule(integra
		 * .getYmd()); boolean isError = false; //単一日勤務予定．勤務種類コード != Null と
		 * 単一日勤務予定．就業時間帯コード == Null チェック Optional<WorkType> optWorkTypePerson =
		 * Optional.empty(); if(optSingleDaySchedule.isPresent() &&
		 * optSingleDaySchedule.get().getWorkTypeCode().isPresent()) {
		 * //個人曜日別勤務の勤務種類の出勤休日区分を取得する optWorkTypePerson = lstWorkType.stream() .filter(x
		 * ->
		 * x.getWorkTypeCode().equals(optSingleDaySchedule.get().getWorkTypeCode().get()
		 * )) .findFirst(); } //日次の勤務種類コードの出勤休日区分を取得する Optional<WorkType>
		 * optWorkTypeRecord = lstWorkType.stream() .filter(x ->
		 * x.getWorkTypeCode().equals(integra.getWorkInformation().getRecordInfo().
		 * getWorkTypeCode())).findFirst(); if(optSingleDaySchedule.isPresent() &&
		 * optSingleDaySchedule.get().getWorkTimeCode().isPresent() &&
		 * !optSingleDaySchedule.get().getWorkTypeCode().isPresent()) {
		 * if(optWorkTypePerson.isPresent() && optWorkTypeRecord.isPresent()) {
		 * AttendanceHolidayAttr wtPersonAtr =
		 * optWorkTypePerson.get().getAttendanceHolidayAttr(); AttendanceHolidayAttr
		 * wtRecordAtr = optWorkTypeRecord.get().getAttendanceHolidayAttr();
		 * if(wtPersonAtr.equals(wtRecordAtr)) { isError = true; } } } else {
		 * if(optWorkTypeRecord.isPresent()) { AttendanceHolidayAttr wtRecordAtr =
		 * optWorkTypeRecord.get().getAttendanceHolidayAttr(); //探した単一日勤務予定 ＝＝ Null OR （
		 * 単一日勤務予定.勤務種類コード ＝＝ Null AND 単一日勤務予定．就業時間帯コード ＝ Null) AND 取得した出勤休日区分 ！＝１日休日系
		 * //OR （探した単一日勤務予定 ！＝ Null AND 取得した出勤休日区分 ＝＝１日休日系）
		 * if(((!optSingleDaySchedule.isPresent() ||
		 * (!optSingleDaySchedule.get().getWorkTimeCode().isPresent() &&
		 * !optSingleDaySchedule.get().getWorkTypeCode().isPresent())) &&
		 * !wtRecordAtr.isHoliday()) || (optSingleDaySchedule.isPresent() &&
		 * wtRecordAtr.isHoliday())) { isError = true; } } } if(!isError) return result;
		 * 
		 * Optional<TimeLeavingOfDailyAttd> optAttendanceLeave =
		 * integra.getAttendanceLeave(); String strActualStamp = ""; String
		 * strLeaveStamp = ""; if(!optAttendanceLeave.isPresent() ||
		 * !optAttendanceLeave.get().getAttendanceLeavingWork(new WorkNo(1)).isPresent()
		 * || (optWorkTypeRecord.isPresent() &&
		 * optWorkTypeRecord.get().getAttendanceHolidayAttr().isHoliday())) {
		 * strActualStamp = "休日"; } else { TimeLeavingWork timeLeavingWork =
		 * optAttendanceLeave.get().getAttendanceLeavingWork(new WorkNo(1)).get();
		 * 
		 * if(timeLeavingWork.getAttendanceStamp().isPresent()) { strActualStamp =
		 * timeLeavingWork.getAttendanceStamp().get().getActualStamp().isPresent() ?
		 * timeLeavingWork.getAttendanceStamp().get().getActualStamp().get().getTimeDay(
		 * ).getTimeWithDay().get().getFullText() :
		 * timeLeavingWork.getAttendanceStamp().get().getStamp().isPresent() ?
		 * timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().
		 * getTimeWithDay().get().getFullText() : TextResource.localize("KAL010_1027");
		 * } else { strActualStamp = TextResource.localize("KAL010_1027"); }
		 * 
		 * if(timeLeavingWork.getLeaveStamp().isPresent()) { strLeaveStamp =
		 * timeLeavingWork.getLeaveStamp().get().getActualStamp().isPresent() ?
		 * timeLeavingWork.getLeaveStamp().get().getActualStamp().get().getTimeDay().
		 * getTimeWithDay().get().getFullText() :
		 * timeLeavingWork.getLeaveStamp().get().getStamp().isPresent() ?
		 * timeLeavingWork.getLeaveStamp().get().getStamp().get().getTimeDay().
		 * getTimeWithDay().get().getFullText() : TextResource.localize("KAL010_1027");
		 * } else { strLeaveStamp = TextResource.localize("KAL010_1027"); } } int day =
		 * integra.getYmd().localDate().getDayOfWeek().getValue(); String description =
		 * EnumAdaptor.valueOf(day - 1,
		 * nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.
		 * DayOfWeek.class).description; String strScheApp = description + ' ' +
		 * (!optWorkTypePerson.isPresent() ? "未設定" :
		 * optWorkTypePerson.get().getWorkTypeCode().v() + " " +
		 * optWorkTypePerson.get().getName().v());
		 * 
		 * result.alarmMessage = TextResource.localize("KAL010_90",
		 * strLeaveStamp.isEmpty() ? strActualStamp : strActualStamp + "～" +
		 * strLeaveStamp, strScheApp); result.alarmTarget =
		 * TextResource.localize("KAL010_616", strActualStamp, strLeaveStamp);
		 */
		return result;
	}
	
	/**
	 * 契約時間超過か未満かチェック
	 * @param sid
	 * @param baseDate
	 * @param dataforDailyFix
	 * @param isOver True: 超過, False: 未満
	 * @return
	 */
	private ErrorInfo checkContracTime(DataFixExtracCon dataforDailyFix,
			IntegrationOfDaily integra,
			boolean isOver) {
		ErrorInfo result = new ErrorInfo("", "");
		Optional<AttendanceTimeOfDailyAttendance> optAttendanceTimeOfDailyPerformance = integra.getAttendanceTimeOfDailyPerformance();
		if(!optAttendanceTimeOfDailyPerformance.isPresent()) return result;
		
		AttendanceTime totalTime = optAttendanceTimeOfDailyPerformance.get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getActualTime();
		int intTotalTime = totalTime.v();
		Optional<WorkingConditionItem> optWorkingConditionItem = this.getWorkingConditionItem(integra.getEmployeeId(), integra.getYmd(), dataforDailyFix);
		if(!optWorkingConditionItem.isPresent()) return result;
		
		WorkingConditionItem workingConditionItem = optWorkingConditionItem.get();
		LaborContractTime contractTime = workingConditionItem.getContractTime();
		int intContractTime = contractTime.v();
		
		if((isOver && intTotalTime > intContractTime)
				|| (!isOver && intTotalTime < intContractTime)) {
			String strTotalTime = converAttendanceTimeToString(totalTime);
			String strContractTime = contractTime.hour() + ":" + (contractTime.minute() < 10 ? "0" : "") + totalTime.minute();
			if(isOver) {
				result.alarmMessage = TextResource.localize("KAL010_86",
						strTotalTime,
						strContractTime);
				result.alarmTarget = TextResource.localize("KAL010_615", strTotalTime);
			} else {
				result.alarmMessage = TextResource.localize("KAL010_88",
						strTotalTime,
						strContractTime);
				result.alarmTarget = TextResource.localize("KAL010_615", strTotalTime);
			}
		}
		return result;		
	}
	
	private Optional<WorkingConditionItem> getWorkingConditionItem(String sid,
			GeneralDate baseDate,
			DataFixExtracCon dataforDailyFix){
		Optional<WorkingCondition> optWorkCond = dataforDailyFix.getListWkConItem().stream()
				.filter(x -> x.getEmployeeId().equals(sid) 
						&& x.getDateHistoryItem().stream()
						.filter(a -> a.start().beforeOrEquals(baseDate) && a.end().afterOrEquals(baseDate)).findFirst().isPresent())
				.findFirst();
		if(!optWorkCond.isPresent()) return Optional.empty();
		WorkingCondition workCond = optWorkCond.get();
		
		Optional<WorkingConditionItem> optWorkingConditionItem = dataforDailyFix.getLstWorkCondItem().stream()
				.filter(x -> x.getEmployeeId().equals(sid) && x.getHistoryId().equals(workCond.getDateHistoryItem().get(0).identifier()))
				.findFirst();
		
		return optWorkingConditionItem;
	}
	
 	private String getItemNameWithValue(List<EmployeeDailyPerError> lstDailyError,
			IntegrationOfDaily integra,
			List<MonthlyAttendanceItemNameDto> lstItemDay) {
		List<Integer> lstItemErr = new ArrayList<>();
		String itemNames = "";
		for(EmployeeDailyPerError x : lstDailyError) {
			if(x != null) {
				lstItemErr.addAll(x.getAttendanceItemList());
			}
		}
		if(lstItemErr.isEmpty()) return itemNames;
		val converter = convertFactory.createDailyConverter();
		converter.setData(integra);
		List<ItemValue> lstItemValue = converter.convert(lstItemErr);
		for (int i = 0; i < lstItemErr.size(); i++) {
			Integer x = lstItemErr.get(i);
			String itemName = lstItemDay.stream()
					.filter(a -> a.getAttendanceItemId() == x)
					.map(y -> y.getAttendanceItemName())
					.collect(Collectors.toList()).get(0);
			List<String> value = lstItemValue.stream().filter(a -> a.getItemId() == x).map(b -> b.getValue()).collect(Collectors.toList());
			if(!value.isEmpty()) {
				TimeWithDayAttr timeDay = new TimeWithDayAttr(Integer.valueOf(value.get(0)));
				itemName = itemName + ":" + timeDay.getFullText();
			}
			itemNames += "/" +  itemName;
		}
		
		return itemNames.substring(1);
	}
	private String getItemName(List<EmployeeDailyPerError> lstDailyError, List<MonthlyAttendanceItemNameDto> lstItemDay) {
		List<Integer> lstItemErr = new ArrayList<>();
		String strItemName = "";
		for(EmployeeDailyPerError x : lstDailyError) {
			if(x != null) {
				lstItemErr.addAll(x.getAttendanceItemList());
			}
		}
		if(lstItemErr.isEmpty()) return strItemName;
		
		strItemName =  lstItemDay.stream()
				.filter(x -> lstItemErr.contains(x.getAttendanceItemId()))
				.map(y -> y.getAttendanceItemName())
				.collect(Collectors.joining("/"));
				
		return strItemName;
	}
	
	public class ErrorInfo {
		String alarmMessage;
		String alarmTarget;
		public ErrorInfo(String alarmMessage, String alarmTarget) {
			this.alarmMessage = alarmMessage;
			this.alarmTarget = alarmTarget;
		}
	}
}