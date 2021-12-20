package nts.uk.ctx.at.function.dom.alarm.w4d4alarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
//import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.task.AsyncTask;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.adapter.WorkPlaceHistImport;
import nts.uk.ctx.at.function.dom.adapter.WorkPlaceIdAndPeriodImport;
import nts.uk.ctx.at.function.dom.adapter.companyRecord.StatusOfEmployeeAdapter;
import nts.uk.ctx.at.function.dom.adapter.worklocation.RecordWorkInfoFunAdapter;
import nts.uk.ctx.at.function.dom.adapter.worklocation.RecordWorkInfoFunAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.workrecord.alarmlist.fourweekfourdayoff.W4D4CheckAdapter;
import nts.uk.ctx.at.function.dom.adapter.workrecord.erroralarm.recordcheck.ErAlWorkRecordCheckAdapter;
import nts.uk.ctx.at.function.dom.adapter.workrecord.erroralarm.recordcheck.RegulationInfoEmployeeResult;
import nts.uk.ctx.at.function.dom.adapter.workschedule.WorkScheduleBasicInforFunctionImport;
import nts.uk.ctx.at.function.dom.adapter.workschedule.WorkScheduleFunctionAdapter;
import nts.uk.ctx.at.function.dom.alarm.alarmdata.ValueExtractAlarm;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.EmployeeSearchDto;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategory;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategoryRepository;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckTargetCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.fourweekfourdayoff.AlarmCheckCondition4W4D;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.fourweekfourdayoff.FourW4DCheckCond;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.*;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckType;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ExtractionAlarmPeriodDate;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.ExtractResultDetail;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.HolidayNumberManagement;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.TreatmentHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday.TreatmentHolidayRepository;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagement;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagementRepo;
import nts.uk.ctx.at.shared.dom.worktype.DeprecateClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
public class W4D4AlarmService {

	@Inject
	private AlarmCheckConditionByCategoryRepository alarmCheckConditionByCategoryRepository;

	@Inject
	private W4D4CheckAdapter w4D4CheckAdapter;

	@Inject
	private ErAlWorkRecordCheckAdapter erAlWorkRecordCheckAdapter;

	@Inject
	private ManagedParallelWithContext parallelManager;

	@Inject
	private WorkScheduleFunctionAdapter wScheduleAdapter;
	@Inject
	private TreatmentHolidayRepository treatHolidayRepos;
	@Inject
	private WeekRuleManagementRepo weekRuleManagementRepo;
	@Inject
	private WorkTypeRepository workTypeRepo;
	
	public List<ValueExtractAlarm> calculateTotal4W4D(EmployeeSearchDto employee, DatePeriod period,
			String checkConditionCode) {
		String companyID = AppContexts.user().companyId();
		List<ValueExtractAlarm> result = new ArrayList<ValueExtractAlarm>();

		Optional<AlarmCheckConditionByCategory> optAlarmCheckConditionByCategory = alarmCheckConditionByCategoryRepository
				.find(companyID, AlarmCategory.SCHEDULE_4WEEK.value, checkConditionCode);
		if (!optAlarmCheckConditionByCategory.isPresent())
			throw new RuntimeException(
					"Can't find AlarmCheckConditionByCategory with category: 4W4D and code: " + checkConditionCode);

		// TODO: Narrow down the target audience
		List<RegulationInfoEmployeeResult> listTarget = erAlWorkRecordCheckAdapter.filterEmployees(period.end(),
				Arrays.asList(employee.getId()), optAlarmCheckConditionByCategory.get().getExtractTargetCondition());
		if (!listTarget.isEmpty()) {
			List<String> listEmps = new ArrayList<>();
			listEmps.add(employee.getId());
			List<RecordWorkInfoFunAdapterDto> listWorkInfoOfDailyPerformance = recordWorkInfoFunAdapter
					.findByPeriodOrderByYmdAndEmps(listEmps, period);

			val listHolidayWorkType = workTypeRepository.findWorkOneDay(companyID,
					DeprecateClassification.NotDeprecated.value, WorkTypeUnit.OneDay.value,
					WorkTypeClassification.Holiday.value);
			List<String> listHolidayWorkTypeCode = listHolidayWorkType.stream().map(c -> c.getWorkTypeCode().v())
					.collect(Collectors.toList());

			for (RegulationInfoEmployeeResult target : listTarget) {
				if (target.getEmployeeId().equals(employee.getId())) {
					AlarmCheckConditionByCategory alarmCheckConditionByCategory = optAlarmCheckConditionByCategory
							.get();
					AlarmCheckCondition4W4D fourW4DCheckCond = (AlarmCheckCondition4W4D) alarmCheckConditionByCategory
							.getExtractionCondition();

					if (fourW4DCheckCond.isForActualResultsOnly()) {
						Optional<ValueExtractAlarm> optAlarm = this.checkWithActualResults(employee, period,
								listHolidayWorkTypeCode, listWorkInfoOfDailyPerformance);
						if (optAlarm.isPresent())
							result.add(optAlarm.get());
					}
					break;
				}
			}
		}

		return result;

	}

	@Inject
	private WorkTypeRepository workTypeRepository;

	@Inject
	private RecordWorkInfoFunAdapter recordWorkInfoFunAdapter;

	public List<ValueExtractAlarm> calculateTotal4W4D(List<EmployeeSearchDto> employees, DatePeriod period,
			List<String> checkConditionCodes) {

		List<String> empIds = employees.stream().map(e -> e.getId()).collect(Collectors.toList());

		String companyID = AppContexts.user().companyId();
		List<ValueExtractAlarm> result = Collections.synchronizedList(new ArrayList<ValueExtractAlarm>());

		List<AlarmCheckConditionByCategory> optAlarmCheckConditionByCategory = alarmCheckConditionByCategoryRepository
				.findByCategoryAndCode(companyID, AlarmCategory.SCHEDULE_4WEEK.value, checkConditionCodes);
		if (optAlarmCheckConditionByCategory.isEmpty())
			throw new RuntimeException("Can't find AlarmCheckConditionByCategory with category: 4W4D and code");
		List<AlarmCheckTargetCondition> listExtractionCondition = optAlarmCheckConditionByCategory.stream()
				.map(c -> c.getExtractTargetCondition()).collect(Collectors.toList());

		// List<RegulationInfoEmployeeResult> listTarget =
		// erAlWorkRecordCheckAdapter.filterEmployees(period.end(), empIds,
		// optAlarmCheckConditionByCategory.get().getExtractTargetCondition());

		val listHolidayWorkType = workTypeRepository.findWorkOneDay(companyID,
				DeprecateClassification.NotDeprecated.value, WorkTypeUnit.OneDay.value,
				WorkTypeClassification.Holiday.value);
		List<String> listHolidayWorkTypeCode = listHolidayWorkType.stream().map(c -> c.getWorkTypeCode().v())
				.collect(Collectors.toList());

		List<RecordWorkInfoFunAdapterDto> listWorkInfoOfDailyPerformance = recordWorkInfoFunAdapter
				.findByPeriodOrderByYmdAndEmps(empIds, period);

		// TODO: Narrow down the target audience
		Map<String, List<RegulationInfoEmployeeResult>> listTargetMap = erAlWorkRecordCheckAdapter
				.filterEmployees(period, empIds, listExtractionCondition);
		optAlarmCheckConditionByCategory.forEach(c -> {
			List<RegulationInfoEmployeeResult> value = listTargetMap.get(c.getExtractTargetCondition().getId());
			AlarmCheckCondition4W4D fourW4DCheckCond = (AlarmCheckCondition4W4D) c.getExtractionCondition();
			if (!value.isEmpty()) {

				/** 並列処理、AsyncTask */
				// Create thread pool.
				Map<String, List<RegulationInfoEmployeeResult>> valueMap = value.stream()
						.collect(Collectors.groupingBy(v -> v.getEmployeeId(), Collectors.toList()));
				ExecutorService executorService = Executors.newFixedThreadPool(5);
				CountDownLatch countDownLatch = new CountDownLatch(valueMap.keySet().size());
				valueMap.entrySet().forEach(v -> {
					AsyncTask task = AsyncTask.builder().withContexts().keepsTrack(false)
							.threadName(this.getClass().getName()).build(() -> {
								Optional<EmployeeSearchDto> emOp = employees.stream()
										.filter(e -> e.getId().equals(v.getKey())).findFirst();
								if (emOp.isPresent()) {

									List<RecordWorkInfoFunAdapterDto> listWorkInfoOfDailyPerByID = new ArrayList<>();
									for (RecordWorkInfoFunAdapterDto recordWorkInfoFunAdapterDto : listWorkInfoOfDailyPerformance) {
										if (recordWorkInfoFunAdapterDto.getEmployeeId().equals(emOp.get().getId())) {
											listWorkInfoOfDailyPerByID.add(recordWorkInfoFunAdapterDto);
										}
									}

									if (fourW4DCheckCond.isForActualResultsOnly()) {
										Optional<ValueExtractAlarm> optAlarm = this.checkWithActualResults(emOp.get(),
												period, listHolidayWorkTypeCode, listWorkInfoOfDailyPerByID);
										if (optAlarm.isPresent())
											result.add(optAlarm.get());
									}

								}
								// Count down latch.
								countDownLatch.countDown();
							});
					executorService.submit(task);
				});
				// Wait for latch until finish.
				try {
					countDownLatch.await();
				} catch (InterruptedException ie) {
					throw new RuntimeException(ie);
				} finally {
					// Force shut down executor services.
					executorService.shutdown();
				}
				// value.stream().collect(Collectors.groupingBy(v ->
				// v.getEmployeeId(), Collectors.toList())).entrySet().stream()
				// .forEach(v -> {
				// Optional<EmployeeSearchDto> emOp =
				// employees.stream().filter(e ->
				// e.getId().equals(v.getKey())).findFirst();
				// if(emOp.isPresent()) {
				//
				// List<RecordWorkInfoFunAdapterDto> listWorkInfoOfDailyPerByID
				// = new ArrayList<>();
				// for(RecordWorkInfoFunAdapterDto recordWorkInfoFunAdapterDto
				// :listWorkInfoOfDailyPerformance) {
				// if(recordWorkInfoFunAdapterDto.getEmployeeId().equals(emOp.get().getId()))
				// {
				// listWorkInfoOfDailyPerByID.add(recordWorkInfoFunAdapterDto);
				// }
				// }
				//
				// if (fourW4DCheckCond.isForActualResultsOnly()) {
				// Optional<ValueExtractAlarm> optAlarm =
				// this.checkWithActualResults(emOp.get(), period,
				// listHolidayWorkTypeCode,listWorkInfoOfDailyPerByID);
				// if (optAlarm.isPresent())
				// result.add(optAlarm.get());
				// }
				//
				// }
				// });

			}
		});

		return result;

	}

	/**
	 * 4週4休の集計処理（２次）
	 * @param cid
	 * @param lstSid
	 * @param dPeriod
	 * @param w4dCheckCond
	 * @param getWplByListSidAndPeriod
	 * @param lstStatusEmp
	 * @return
	 */
	public List<ExtractResultDetail> extractCheck4W4d(String cid, List<String> lstSid,
			DatePeriod dPeriod,
			FourW4DCheckCond w4dCheckCond,
			List<WorkPlaceHistImport> getWplByListSidAndPeriod,
			List<StatusOfEmployeeAdapter> lstStatusEmp, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop,
			List<AlarmEmployeeList> alarmEmployeeList, String alarmCheckConditionCode,
			List<AlarmExtractionCondition> alarmExtractConditions){
		List<ExtractResultDetail> lstResult = new ArrayList<>();
		// 「アラーム抽出条件」を作成してInput．List＜アラーム抽出条件＞に追加
		val extractionCon = alarmExtractConditions.stream()
				.filter(x -> x.getAlarmListCheckType() == AlarmListCheckType.FixCheck
						&& x.getAlarmCheckConditionNo().equals(String.valueOf(w4dCheckCond.value)))
				.findAny();
		if (!extractionCon.isPresent()) {
			alarmExtractConditions.add(new AlarmExtractionCondition(
					String.valueOf(w4dCheckCond.value),
					new AlarmCheckConditionCode(alarmCheckConditionCode),
					AlarmCategory.SCHEDULE_4WEEK,
					AlarmListCheckType.FixCheck
			));
		}

		//ドメインモデル「休日の扱い」を取得
		Optional<TreatmentHoliday> optTreatHolidaySet = treatHolidayRepos.get(cid);
		if(!optTreatHolidaySet.isPresent()) return lstResult;
		TreatmentHoliday treatHolidaySet = optTreatHolidaySet.get();
		

		// 日別実績の勤務情報を取得する
		List<RecordWorkInfoFunAdapterDto> workInfos = recordWorkInfoFunAdapter.findByPeriodOrderByYmdAndEmps(lstSid,
				dPeriod);
		RequireImpl require = new RequireImpl(weekRuleManagementRepo, workTypeRepo);
		HolidayNumberManagement holidayNumberMana = treatHolidaySet.getNumberHoliday(require, dPeriod.start());
		parallelManager.forEach(CollectionUtil.partitionBySize(lstSid, 100), emps -> {

			synchronized (this) {
				if (shouldStop.get()) {
					return;
				}
			}
			//勤務予定
			List<WorkScheduleBasicInforFunctionImport> basicScheduleImports = new ArrayList<>();
			if(w4dCheckCond == FourW4DCheckCond.WithScheduleAndActualResults) {
				basicScheduleImports = wScheduleAdapter.getScheBySids(emps, dPeriod);	
			}
			for(int ss = 0; ss < emps.size(); ss++) {
				String sid = lstSid.get(ss);
				String workplaceId = "";
				List<WorkPlaceHistImport> getWpl = getWplByListSidAndPeriod.stream()
						.filter(x -> x.getEmployeeId().equals(sid)).collect(Collectors.toList());
				if(!getWpl.isEmpty()) {
					WorkPlaceIdAndPeriodImport wpPeriod = !getWpl.get(0).getLstWkpIdAndPeriod().isEmpty()
							? getWpl.get(0).getLstWkpIdAndPeriod().get(0) : null;
					if (wpPeriod != null)
						workplaceId = wpPeriod.getWorkplaceId();
				}
				List<RecordWorkInfoFunAdapterDto> workInfoSid = workInfos.stream()
						.filter(w -> w.getEmployeeId().equals(sid))
						.collect(Collectors.toList());
				List<WorkScheduleBasicInforFunctionImport> basicScheduleImportSid = basicScheduleImports.stream()
						.filter(w -> w.getEmployeeID().equals(sid))
						.collect(Collectors.toList());
				Map<GeneralDate, WorkInformation> mapWorkInfor = new HashMap<>();
				for (int i = 0; dPeriod.start().daysTo(dPeriod.end()) - i >= 0 ; i++) {
					List<StatusOfEmployeeAdapter> lstStatusOfE = lstStatusEmp.stream()
							.filter(x -> x.getEmployeeId().equals(sid)).collect(Collectors.toList());
					GeneralDate date = dPeriod.start().addDays(i);
					if(lstStatusOfE.isEmpty()) {
						continue;
					}
					List<DatePeriod> lstStatusE = lstStatusOfE.get(0).getListPeriod();
					
					for (int j = 0; j < lstStatusE.size(); j++) {
						DatePeriod ePeriod = lstStatusE.get(j);
						if(ePeriod.start().beforeOrEquals(date) && ePeriod.end().afterOrEquals(date)) {
							WorkInformation workInfor = null;
							List<RecordWorkInfoFunAdapterDto> workInfo = workInfoSid.stream()
									.filter(w -> w.getWorkingDate().equals(date))
									.collect(Collectors.toList());
							if(w4dCheckCond == FourW4DCheckCond.WithScheduleAndActualResults 
									&& workInfo.isEmpty() 
									&& !basicScheduleImports.isEmpty()) {
								List<WorkScheduleBasicInforFunctionImport> basicScheduleImport = basicScheduleImportSid.stream()
										.filter(w -> w.getYmd().equals(date))
										.collect(Collectors.toList());
								if(!basicScheduleImport.isEmpty()) {
									workInfor =  new WorkInformation(basicScheduleImport.get(0).getWorkTypeCd(),
											basicScheduleImport.get(0).getWorkTimeCd().isPresent() ? basicScheduleImport.get(0).getWorkTimeCd().get() : null);
								}
							}
							
							if(!workInfo.isEmpty()) {
								workInfor = new WorkInformation(workInfo.get(0).getWorkTypeCode(),
										workInfo.get(0).getWorkTimeCode());
							}
							if(workInfor != null) {
								mapWorkInfor.put(date, workInfor);	
							}
						}
					}
					
				}
				List<ExtractResultDetail> lstDetail = new ArrayList<>();
				if(mapWorkInfor.isEmpty()) continue;
				
				int holidays = holidayNumberMana.countNumberHolidays(require, mapWorkInfor);
				if(holidays < holidayNumberMana.getHolidayDays().v()) {
					ExtractionAlarmPeriodDate alarmDate = new ExtractionAlarmPeriodDate(Optional.ofNullable(dPeriod.start()), Optional.ofNullable(dPeriod.end()));
					ExtractResultDetail alarmDetail = new ExtractResultDetail(
							alarmDate, 
							w4dCheckCond.nameId,
							TextResource.localize("KAL010_64"), 
							GeneralDateTime.now(), 
							Optional.ofNullable(workplaceId),
							Optional.ofNullable(""),
							Optional.ofNullable(TextResource.localize("KAL010_63", String.valueOf(holidays))));
					lstDetail.add(alarmDetail);
				}

				if (!lstDetail.isEmpty()) {
					List<AlarmExtractInfoResult> alarmExtractInfoResults = new ArrayList<>(Arrays.asList(
							new AlarmExtractInfoResult(
									String.valueOf(w4dCheckCond.value),
									new AlarmCheckConditionCode(alarmCheckConditionCode),
									AlarmCategory.SCHEDULE_4WEEK,
									AlarmListCheckType.FixCheck,
									lstDetail
							)
					));

					if (alarmEmployeeList.stream().noneMatch(i -> i.getEmployeeID().equals(sid))) {
						alarmEmployeeList.add(new AlarmEmployeeList(alarmExtractInfoResults, sid));
					} else {
						for (AlarmEmployeeList emp : alarmEmployeeList) {
							if(emp.getEmployeeID().equals(sid)){
								List<AlarmExtractInfoResult> temp = new ArrayList<>(emp.getAlarmExtractInfoResults());
								temp.addAll(alarmExtractInfoResults);
								emp.setAlarmExtractInfoResults(temp);
								break;
							}
						}
					}
				}
			}
			synchronized (this) {
				counter.accept(emps.size());
			}
		});
		return lstResult;
	}
	/**
	 * 4週4休の集計処理
	 * 
	 * @param companyID
	 * @param employees
	 * @param period
	 * @param w4d4ErAl
	 * @param counter
	 * @param shouldStop
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ValueExtractAlarm> calculateTotal4W4D(String companyID, List<EmployeeSearchDto> employees,
			DatePeriod period, List<AlarmCheckConditionByCategory> w4d4ErAl, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop) {
		List<ValueExtractAlarm> result = Collections.synchronizedList(new ArrayList<ValueExtractAlarm>());
		List<String> empIds = employees.stream().map(e -> e.getId()).collect(Collectors.toList());

		List<AlarmCheckTargetCondition> listExtractionCondition = w4d4ErAl.stream()
				.map(c -> c.getExtractTargetCondition()).collect(Collectors.toList());

		// ドメインモデル「勤務種類」を取得
		List<String> workTypes = workTypeRepository.findWorkTypeCodeOneDay(companyID,
				DeprecateClassification.NotDeprecated.value, WorkTypeUnit.OneDay.value,
				WorkTypeClassification.Holiday.value);

		// ドメインモデル「勤務種類」を取得 ↓
		// 【法定内休日】勤務種類<List>を取得する
		List<WorkType> legalHLDWorkTypes = workTypeRepository.findHolidayWorkType(companyID,
				DeprecateClassification.NotDeprecated.value, WorkTypeUnit.OneDay.value,
				WorkTypeClassification.Holiday.value, 0);

		// ドメインモデル「勤務種類」を取得 ↓
		// 【法定外休日】勤務種類<List>を取得する
		List<WorkType> nonLegalHLDWorkTypes = workTypeRepository.findHolidayWorkType(companyID,
				DeprecateClassification.NotDeprecated.value, WorkTypeUnit.OneDay.value,
				WorkTypeClassification.Holiday.value, 1);

		List<WorkScheduleBasicInforFunctionImport> basicScheduleImports = wScheduleAdapter.getScheBySids(empIds, period);

		// 日別実績の勤務情報を取得する
		List<RecordWorkInfoFunAdapterDto> workInfos = recordWorkInfoFunAdapter.findByPeriodOrderByYmdAndEmps(empIds,
				period);
		// 対象者をしぼり込む
		Map<String, List<RegulationInfoEmployeeResult>> listTargetMap = erAlWorkRecordCheckAdapter
				.filterEmployees(period, empIds, listExtractionCondition);

		w4d4ErAl.forEach(c -> {
			AlarmCheckCondition4W4D fourW4DCheckCond = (AlarmCheckCondition4W4D) c.getExtractionCondition();
			String alarmValueMessage = fourW4DCheckCond.getFourW4DCheckCond().nameId;
			if (fourW4DCheckCond.isForActualResultsOnly()) {
				List<RegulationInfoEmployeeResult> targetEmps = listTargetMap
						.get(c.getExtractTargetCondition().getId());
				if (!targetEmps.isEmpty()) {
					Map<String, List<RegulationInfoEmployeeResult>> valueMap = targetEmps.stream()
							.collect(Collectors.groupingBy(v -> v.getEmployeeId(), Collectors.toList()));
					parallelManager.forEach(CollectionUtil.partitionBySize(employees, 100), emps -> {

						synchronized (this) {
							if (shouldStop.get()) {
								return;
							}
						}
						emps.stream().forEach(emp -> {
							if (valueMap.containsKey(emp.getId())) {
								List<RecordWorkInfoFunAdapterDto> currentWorkInfos = workInfos.stream()
										.filter(wi -> emp.getId().equals(wi.getEmployeeId()))
										.collect(Collectors.toList());
								this.checkWithActualResults(emp, period, workTypes, currentWorkInfos).ifPresent(er -> {
									er.setAlarmValueMessage(alarmValueMessage);
									result.add(er);
								});
							}
						});
						synchronized (this) {
							counter.accept(emps.size());
						}
					});
				}
			} else if (fourW4DCheckCond.isWithScheduleAndActualResults()) { // 実績とスケジュールでチェックする
				List<RegulationInfoEmployeeResult> targetEmps = listTargetMap
						.get(c.getExtractTargetCondition().getId());
				if (!targetEmps.isEmpty()) {
					Map<String, List<RegulationInfoEmployeeResult>> valueMap = targetEmps.stream()
							.collect(Collectors.groupingBy(v -> v.getEmployeeId(), Collectors.toList()));
					parallelManager.forEach(CollectionUtil.partitionBySize(employees, 100), emps -> {
						synchronized (this) {
							if (shouldStop.get()) {
								return;
							}
						}
						emps.stream().forEach(emp -> {
							if (valueMap.containsKey(emp.getId())) {
								List<RecordWorkInfoFunAdapterDto> currentWorkInfos = workInfos.stream()
										.filter(wi -> emp.getId().equals(wi.getEmployeeId()))
										.collect(Collectors.toList());
								List<WorkScheduleBasicInforFunctionImport> basicSchedules = basicScheduleImports.stream()
										.filter(b -> emp.getId().equals(b.getEmployeeID()))
										.collect(Collectors.toList());
								List<ValueExtractAlarm> alarms = this.checkWithScheduleAndActualResults(emp, period,
										legalHLDWorkTypes, nonLegalHLDWorkTypes, currentWorkInfos, basicSchedules);
								result.addAll(alarms);
							}
						});
						synchronized (this) {
							counter.accept(emps.size());
						}
					});
				}
			}

		});

		return result;

	}

	public Optional<ValueExtractAlarm> checkWithActualResults(EmployeeSearchDto employee, DatePeriod period,
			List<String> listHolidayWorkTypeCode, List<RecordWorkInfoFunAdapterDto> listWorkInfoOfDailyPerByID) {
		return w4D4CheckAdapter.checkHoliday(employee.getWorkplaceId(), employee.getId(), period,
				listHolidayWorkTypeCode, listWorkInfoOfDailyPerByID);
	}

	public List<ValueExtractAlarm> checkWithScheduleAndActualResults(EmployeeSearchDto employee, DatePeriod period,
			List<WorkType> legalHolidayWorkTypeCodes, List<WorkType> illegalHolidayWorkTypeCodes,
			List<RecordWorkInfoFunAdapterDto> listWorkInfoOfDailyPerByID, List<WorkScheduleBasicInforFunctionImport> basicSchedules) {
		return w4D4CheckAdapter.checkHolidayWithSchedule(employee.getWorkplaceId(), employee.getId(), period,
				legalHolidayWorkTypeCodes, illegalHolidayWorkTypeCodes, listWorkInfoOfDailyPerByID, basicSchedules);
	}
	
	@AllArgsConstructor
	private static class RequireImpl implements TreatmentHoliday.Require, HolidayNumberManagement.Require{
		
		private WeekRuleManagementRepo weekRuleManagementRepo;
		
		private WorkTypeRepository workTypeRepo;
		
		@Override
		public WeekRuleManagement find() {
			String companyID = AppContexts.user().companyId();
			return weekRuleManagementRepo.find(companyID).get();
		}
		@Override
		public Optional<WorkType> findByPK(String workTypeCd) {
			String companyID = AppContexts.user().companyId();
			return workTypeRepo.findByPK(companyID, workTypeCd);
		}
		
	}

}