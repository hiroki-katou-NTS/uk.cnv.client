package nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess.daily.dailyaggregationprocess;

import java.util.ArrayList;
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
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
//import nts.arc.error.BusinessException;
import nts.arc.task.AsyncTask;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.adapter.DailyAttendanceItemAdapter;
import nts.uk.ctx.at.function.dom.adapter.DailyAttendanceItemAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.ErAlApplicationAdapter;
import nts.uk.ctx.at.function.dom.adapter.ErAlApplicationAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.ErrorAlarmWorkRecordAdapter;
import nts.uk.ctx.at.function.dom.adapter.ErrorAlarmWorkRecordAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.FixedConWorkRecordAdapter;
import nts.uk.ctx.at.function.dom.adapter.FixedConWorkRecordAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.WorkRecordExtraConAdapter;
import nts.uk.ctx.at.function.dom.adapter.WorkRecordExtraConAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.agreement.CheckedAgreementResult;
import nts.uk.ctx.at.function.dom.adapter.application.ApplicationAdapter;
import nts.uk.ctx.at.function.dom.adapter.application.ApplicationImport;
import nts.uk.ctx.at.function.dom.adapter.eralworkrecorddto.ErAlAtdItemConAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.eralworkrecorddto.ErAlConAttendanceItemAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.eralworkrecorddto.MessageWRExtraConAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.eralworkrecorddto.WorkTypeConAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.fixedcheckitem.FixedCheckItemAdapter;
import nts.uk.ctx.at.function.dom.adapter.worklocation.RecordWorkInfoFunAdapter;
import nts.uk.ctx.at.function.dom.adapter.worklocation.RecordWorkInfoFunAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.workrecord.erroralarm.recordcheck.ErAlWorkRecordCheckAdapter;
import nts.uk.ctx.at.function.dom.adapter.workrecord.erroralarm.recordcheck.ErrorRecordImport;
import nts.uk.ctx.at.function.dom.alarm.alarmdata.ValueExtractAlarm;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.EmployeeSearchDto;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.PeriodByAlarmCategory;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess.ErAlConstant;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategory;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategoryRepository;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.daily.DailyAlarmCondition;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.AttendanceItemLinking;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.repository.AttendanceItemLinkingRepository;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.repository.DailyAttendanceItemNameDomainService;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.i18n.TextResource;
import nts.arc.time.calendar.period.DatePeriod;

@Stateless
public class DailyAggregationProcessService {

	@Inject
	private AlarmCheckConditionByCategoryRepository alCheckConByCategoryRepo;

	@Inject
	private FixedConWorkRecordAdapter fixedConWorkRecordAdapter;

	@Inject
	private FixedCheckItemAdapter fixedCheckItemAdapter;

	@Inject
	private RecordWorkInfoFunAdapter recordWorkInfoFunAdapter;

	@Inject
	private DailyPerformanceService dailyPerformanceService;

	@Inject
	private ErAlWorkRecordCheckAdapter erAlWorkRecordCheckAdapter;

	@Inject
	private WorkRecordExtraConAdapter workRecordExtraConAdapter;

	// Get work type name
	@Inject
	private WorkTypeRepository workTypeRepository;

	// Get attendance name
	@Inject
	private DailyAttendanceItemNameDomainService attendanceNameService;
	// get all attendance ID
	@Inject
	private DailyAttendanceItemAdapter dailyAttendanceItemAdapter;

	@Inject
	private ManagedParallelWithContext parallelManager;

	@Inject
	private AttendanceItemLinkingRepository attendanceItemLinkingRepository;

	// 勤務実績のエラーアラーム
	@Inject
	private ErrorAlarmWorkRecordAdapter errorAlarmWorkRecordAdapter;

	@Inject
	private ErAlApplicationAdapter eralAppAdapter;

	@Inject
	private ApplicationAdapter applicationAdapter;

	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	/**
	 * 勤務実績のアラーム抽出
	 * 
	 * @param comId
	 * @param periodAlarm
	 * @param employees
	 * @param eralCate
	 * @param counter
	 * @param shouldStop
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ValueExtractAlarm> dailyAggregationProcess(String comId, PeriodByAlarmCategory periodAlarm,
			List<EmployeeSearchDto> employees, List<AlarmCheckConditionByCategory> eralCate, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop) {

		List<ValueExtractAlarm> result = Collections.synchronizedList(new ArrayList<>());
		DatePeriod datePeriod = new DatePeriod(periodAlarm.getStartDate(), periodAlarm.getEndDate());

		//
		DataHolder holder = new DataHolder(comId, eralCate);
		String KAL010_1 = TextResource.localize("KAL010_1");

		parallelManager.forEach(CollectionUtil.partitionBySize(employees, 100), emps -> {

			synchronized (this) {
				if (shouldStop.get()) {
					return;
				}
			}

			List<String> employeeIds = emps.stream().map(e -> e.getId()).collect(Collectors.toList());
			// 社員、期間に一致する申請を取得する
			List<ApplicationImport> apps = applicationAdapter.getApplicationBySID(employeeIds, datePeriod.start(),
					datePeriod.end());
			// エラーアラームチェック条件(error alarm)
			result.addAll(dailyPerformanceService.aggregationProcess(datePeriod, employeeIds, emps, holder, apps));
			// アラームチェック条件
			result.addAll(this.extractCheckCondition(datePeriod, emps, comId, employeeIds, holder, KAL010_1));
			// 「システム固定のチェック項目」で実績をチェックする
			result.addAll(
					this.extractFixedConditionV2(comId, holder.dailyAlarmCondition, periodAlarm, emps, employeeIds));

			synchronized (this) {
				counter.accept(emps.size() * eralCate.size());
			}
		});

		return result;
	}

	/**
	 * tab3: アラームチェック条件
	 * 
	 * @param period    期間
	 * @param employee  List＜社員情報＞
	 * @param companyID 会社ID
	 * @param emIds     List＜社員ID＞
	 * @param holder    日次の勤怠項目、勤務実績のエラーアラーム設定
	 * @param KAL010_1
	 * @return
	 */
	private List<ValueExtractAlarm> extractCheckCondition(DatePeriod period, List<EmployeeSearchDto> employee,
			String companyID, List<String> emIds, DataHolder holder, String KAL010_1) {
		List<ValueExtractAlarm> listValueExtractAlarm = new ArrayList<>();
		// 勤務種類でフィルタする
		Map<String, Integer> mapCheckItem = holder.mapWorkRecordExtraCon.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e-> e.getValue().getCheckItem()));
//		List<ErrorRecordImport> listErrorRecord = erAlWorkRecordCheckAdapter.check(holder.eralId, period, emIds);
		List<ErrorRecordImport> listErrorRecord = erAlWorkRecordCheckAdapter.checkV2(holder.eralId, period, emIds, mapCheckItem);
		if (listErrorRecord.isEmpty()) {
			return listValueExtractAlarm;
		}
		// Get map all name Attd Name
		Map<Integer, DailyAttendanceItem> mapAttdName = holder.listAttenDanceItem.stream()
				.collect(Collectors.toMap(DailyAttendanceItem::getAttendanceItemId, x -> x));
		Map<String, EmployeeSearchDto> empMap = employee.stream()
				.collect(Collectors.toMap(EmployeeSearchDto::getId, x -> x));
		// 実績をチェックする
		return listErrorRecord.stream().filter(er -> er.isError())
				.map(er -> this.checkConditionGenerateValue(empMap.get(er.getEmployeeId()), er.getDate(),
						holder.mapWorkRecordExtraCon.get(er.getErAlId()), companyID, getAlarmItem(holder, er),
						mapAttdName, KAL010_1, er.getCheckedValue()))
				.collect(Collectors.toList());
	}

	private String getAlarmItem(DataHolder holder, ErrorRecordImport errorRecord) {
		TypeCheckWorkRecord checkItem = EnumAdaptor.valueOf(
				holder.mapWorkRecordExtraCon.get(errorRecord.getErAlId()).getCheckItem(), TypeCheckWorkRecord.class);

		return holder.listAlarmItemName.stream().filter(ai -> ai.getCheckItem() == checkItem).findFirst()
				.orElseGet(() -> new AlarmItemName(checkItem, "")).getNameAlarm();
	}

	/**
	 * 日次の勤怠項目、勤務実績のエラーアラーム設定を返す
	 *
	 */
	public class DataHolder {
		String comId;
		List<AlarmCheckConditionByCategory> eralCate;
		List<DailyAlarmCondition> dailyAlarmCondition;
		List<String> eralCode;
		List<DailyAttendanceItemAdapterDto> dailyAttendanceItems;
		List<Integer> attendanceIds;

		List<ErrorAlarmWorkRecordAdapterDto> errorAlarmWorkRecord;
		List<ErAlApplicationAdapterDto> eralApps;
		List<AttendanceItemLinking> attendanceItemAndFrameNos;
		List<MessageWRExtraConAdapterDto> messageList;
		Map<String, WorkRecordExtraConAdapterDto> mapWorkRecordExtraCon;
		/** 抽出条件ID */
		List<String> eralId;
		List<AlarmItemName> listAlarmItemName;
		List<DailyAttendanceItem> listAttenDanceItem;

		public DataHolder(String comId, List<AlarmCheckConditionByCategory> eralCate) {
			this.comId = comId;
			this.eralCate = eralCate;
			// 日次のアラームチェック条件コード
			this.eralCode = eralCate.stream()
					.map(eral -> ((DailyAlarmCondition) eral.getExtractionCondition()).getErrorAlarmCode())
					.flatMap(List::stream).distinct().collect(Collectors.toList());
			this.dailyAlarmCondition = eralCate.stream()
					.map(eral -> (DailyAlarmCondition) eral.getExtractionCondition()).collect(Collectors.toList());
			this.eralId = this.dailyAlarmCondition.stream().map(c -> c.getExtractConditionWorkRecord())
					.flatMap(List::stream).distinct().collect(Collectors.toList());
			this.listAlarmItemName = getAlarmItemByType();
			this.getData();
		}

		private void getData() {
			// 日次の勤怠項目
			this.dailyAttendanceItems = getDailyAttendanceItems(this.comId);

			this.attendanceIds = dailyAttendanceItems.stream().map(da -> da.getAttendanceItemId())
					.collect(Collectors.toList());
			// 勤怠項目と枠の紐付け
			this.attendanceItemAndFrameNos = attendanceItemLinkingRepository
					.getFullDataByListAttdaId(this.attendanceIds);
			// 勤務実績のエラーアラーム
			this.errorAlarmWorkRecord = errorAlarmWorkRecordAdapter.getListErAlByListCode(this.comId, this.eralCode);
			// エラー発生時に呼び出す申請一覧
			this.eralApps = eralAppAdapter.getAllErAlAppByEralCode(this.comId, this.eralCode);
			// 勤務実績のエラーアラームチェックの表示メッセージ
			this.messageList = workRecordExtraConAdapter.getMessageWRExtraConByListID(this.errorAlarmWorkRecord.stream()
					.map(c -> c.getErrorAlarmCheckID()).distinct().collect(Collectors.toList()));
			// 勤務実績の抽出条件
			this.mapWorkRecordExtraCon = workRecordExtraConAdapter.getAllWorkRecordExtraConByListID(eralId).stream()
					.collect(Collectors.toMap(WorkRecordExtraConAdapterDto::getErrorAlarmCheckID, x -> x));
			// 勤怠項目に対応する名称を生成する
			this.listAttenDanceItem = attendanceNameService.getNameOfDailyAttendanceItem(this.attendanceIds);
		}
	}

	private Map<String, String> getWorkTypes(String comId, List<RecordWorkInfoFunAdapterDto> recordWorkInfo,
			List<FixedConWorkRecordAdapterDto> no1) {
		if (no1.isEmpty()) {
			return Collections.emptyMap();
		}
		List<String> workTypeCodes = recordWorkInfo.stream().filter(c -> c.getWorkTypeCode() != null)
				.map(c -> c.getWorkTypeCode()).distinct().collect(Collectors.toList());
		if (workTypeCodes.isEmpty()) {
			return Collections.emptyMap();
		}
		return workTypeRepo.getCodeNameWorkType(comId, workTypeCodes);
	}

	private Map<String, String> getWorkTimes(String comId, List<RecordWorkInfoFunAdapterDto> recordWorkInfo,
			List<FixedConWorkRecordAdapterDto> no2) {
		if (no2.isEmpty()) {
			return Collections.emptyMap();
		}
		List<String> workTimeCodes = recordWorkInfo.stream().filter(c -> c.getWorkTimeCode() != null)
				.map(c -> c.getWorkTimeCode()).distinct().collect(Collectors.toList());
		if (workTimeCodes.isEmpty()) {
			return Collections.emptyMap();
		}
		return workTimeSettingRepository.getCodeNameByListWorkTimeCd(comId, workTimeCodes);
	}

	// tab4: 「システム固定のチェック項目」で実績をチェックする
	private List<ValueExtractAlarm> extractFixedConditionV2(String comId, List<DailyAlarmCondition> dailyAlarmCondition,
			PeriodByAlarmCategory period, List<EmployeeSearchDto> employee, List<String> empIds) {
		List<ValueExtractAlarm> listValueExtractAlarm = new ArrayList<>();
		List<String> dailyAlarmIds = dailyAlarmCondition.stream().map(c -> c.getDailyAlarmConID()).distinct()
				.collect(Collectors.toList());
		// get data by dailyAlarmCondition
		// 勤務実績の固定抽出条件
		List<FixedConWorkRecordAdapterDto> listFixed = fixedConWorkRecordAdapter
				.getAllFixedConWorkRecordByID(dailyAlarmIds).stream().filter(c -> c.isUseAtr())
				.sorted((x, y) -> x.getFixConWorkRecordNo() - y.getFixConWorkRecordNo()).collect(Collectors.toList());
		DatePeriod datePeriod = new DatePeriod(period.getStartDate(), period.getEndDate());
		// 3: 本人未確認
		checkFixedNo3(period, employee, listValueExtractAlarm, listFixed, empIds);
		// 4: 管理者未承認
		checkFixedNo4(employee, listValueExtractAlarm, listFixed, empIds, datePeriod);
		// 6: 連続休暇チェック
		checkFixedNo6(employee, listValueExtractAlarm, listFixed, datePeriod);
		// 1： 勤務種類未登録、2： 就業時間帯未登録、5: データチェック
		checkFixedNo1And2AndOther(comId, period, employee, empIds, listValueExtractAlarm, listFixed);

		return listValueExtractAlarm;
	}

	/** 本人未確認 */
	private void checkFixedNo3(PeriodByAlarmCategory period, List<EmployeeSearchDto> employee,
			List<ValueExtractAlarm> listValueExtractAlarm, List<FixedConWorkRecordAdapterDto> listFixed,
			List<String> empIds) {
		List<FixedConWorkRecordAdapterDto> no3 = listFixed.stream().filter(c -> c.getFixConWorkRecordNo() == 3)
				.collect(Collectors.toList());
		if (!no3.isEmpty()) {
			Map<String, List<GeneralDate>> erals = fixedCheckItemAdapter.checkPrincipalConfirmed(empIds,
					period.getStartDate(), period.getEndDate());
			String KAL010_1 = TextResource.localize("KAL010_1");
			String KAL010_42 = TextResource.localize("KAL010_42");
			String KAL010_43 = TextResource.localize("KAL010_43");
			String KAL010_72 = TextResource.localize("KAL010_72");
			employee.stream().forEach(emp -> {
				List<GeneralDate> empEral = erals.get(emp.getId());
				period.getListDate().stream().forEach(date -> {
					if (empEral == null || !empEral.contains(date)) {
						no3.stream().forEach(c -> {
							String checkedValue = KAL010_72;
							listValueExtractAlarm.add(new ValueExtractAlarm(emp.getWorkplaceId(), emp.getId(),
									date.toString(ErAlConstant.DATE_FORMAT), KAL010_1, KAL010_42, KAL010_43,
									c.getMessage(), checkedValue));
						});
					}
				});
			});
		}
	}

	/** TODO: need response */
	/** 連続休暇チェック */
	private void checkFixedNo6(List<EmployeeSearchDto> employee, List<ValueExtractAlarm> listValueExtractAlarm,
			List<FixedConWorkRecordAdapterDto> listFixed, DatePeriod datePeriod) {
		List<FixedConWorkRecordAdapterDto> no6 = listFixed.stream().filter(c -> c.getFixConWorkRecordNo() == 6)
				.collect(Collectors.toList());
		if (!no6.isEmpty()) {
			employee.stream().forEach(emp -> {
				List<ValueExtractAlarm> erals = fixedCheckItemAdapter.checkContinuousVacation(emp.getId(), datePeriod);
				if (!erals.isEmpty()) {
					for (ValueExtractAlarm tmp : erals) {
						String checkedValue = TextResource.localize("KAL010_75",
								String.valueOf(tmp.getConsecutiveDays()));
						no6.stream().forEach(c -> {
							listValueExtractAlarm.add(new ValueExtractAlarm(emp.getWorkplaceId(), tmp.getEmployeeID(),
									TextResource.localize("KAL010_907", tmp.getAlarmValueDate()),
									tmp.getClassification(), tmp.getAlarmItem(), tmp.getAlarmValueMessage(),
									c.getMessage(), checkedValue));
						});
					}
				}
			});
		}
	}

	/** 管理者未承認 */
	private void checkFixedNo4(List<EmployeeSearchDto> employee, List<ValueExtractAlarm> listValueExtractAlarm,
			List<FixedConWorkRecordAdapterDto> listFixed, List<String> empIds, DatePeriod datePeriod) {
		List<FixedConWorkRecordAdapterDto> no4 = listFixed.stream().filter(c -> c.getFixConWorkRecordNo() == 4)
				.collect(Collectors.toList());
		if (!no4.isEmpty()) {
			String KAL010_1 = TextResource.localize("KAL010_1");
			String KAL010_44 = TextResource.localize("KAL010_44");
			String KAL010_45 = TextResource.localize("KAL010_45");
			String KAL010_73 = TextResource.localize("KAL010_73");
			Map<String, List<GeneralDate>> erals = fixedCheckItemAdapter.checkAdminUnverified(empIds, datePeriod);
			if (!erals.isEmpty()) {
				employee.stream().forEach(emp -> {
					if (erals.containsKey(emp.getId())) {
						erals.get(emp.getId()).forEach(eral -> {
							no4.stream().forEach(c -> {
								String checkedValue = KAL010_73;
								listValueExtractAlarm.add(new ValueExtractAlarm(emp.getWorkplaceId(), emp.getId(),
										eral.toString(ErAlConstant.DATE_FORMAT), KAL010_1, KAL010_44, KAL010_45,
										c.getMessage(), checkedValue));
							});
						});
					}
				});
			}
		}
	}

	/** 1： 勤務種類未登録、2： 就業時間帯未登録、5: データチェック */
	private void checkFixedNo1And2AndOther(String comId, PeriodByAlarmCategory period, List<EmployeeSearchDto> employee,
			List<String> empIds, List<ValueExtractAlarm> listValueExtractAlarm,
			List<FixedConWorkRecordAdapterDto> listFixed) {
		List<FixedConWorkRecordAdapterDto> no1 = listFixed.stream().filter(c -> c.getFixConWorkRecordNo() == 1)
				.collect(Collectors.toList());
		List<FixedConWorkRecordAdapterDto> no2 = listFixed.stream().filter(c -> c.getFixConWorkRecordNo() == 2)
				.collect(Collectors.toList());
		List<FixedConWorkRecordAdapterDto> noOthers = listFixed.stream().filter(c -> c.getFixConWorkRecordNo() == 5)
				.collect(Collectors.toList());

		if (!no1.isEmpty() || !no2.isEmpty() || !noOthers.isEmpty()) {
			List<RecordWorkInfoFunAdapterDto> recordWorkInfo = recordWorkInfoFunAdapter
					.findByPeriodOrderByYmdAndEmps(empIds, new DatePeriod(period.getStartDate(), period.getEndDate()));

			String KAL010_1 = TextResource.localize("KAL010_1");
			String KAL010_6 = TextResource.localize("KAL010_6");
			String KAL010_8 = TextResource.localize("KAL010_8");
			String KAL010_65 = TextResource.localize("KAL010_65");
			String KAL010_66 = TextResource.localize("KAL010_66");
			String KAL010_74 = TextResource.localize("KAL010_74");

			Map<String, String> workTypes = getWorkTypes(comId, recordWorkInfo, no1);
			Map<String, String> workTimes = getWorkTimes(comId, recordWorkInfo, no2);

			for (GeneralDate date : period.getListDate()) {
				employee.stream().forEach(emp -> {
					Optional<RecordWorkInfoFunAdapterDto> currentRecord = recordWorkInfo.stream()
							.filter(rw -> rw.getWorkingDate().equals(date) && rw.getEmployeeId().equals(emp.getId()))
							.findFirst();
					if (currentRecord.isPresent()) {
						RecordWorkInfoFunAdapterDto rw = currentRecord.get();
						if (!no1.isEmpty() && !workTypes.containsKey(rw.getWorkTypeCode())) {
							String KAL010_7 = TextResource.localize("KAL010_7", rw.getWorkTypeCode());
							String checkedValue = TextResource.localize("KAL010_76", rw.getWorkTypeCode());
							no1.stream().forEach(fixed -> {
								listValueExtractAlarm.add(new ValueExtractAlarm(emp.getWorkplaceId(), emp.getId(),
										date.toString(ErAlConstant.DATE_FORMAT), KAL010_1, KAL010_6, KAL010_7,
										fixed.getMessage(), checkedValue));
							});
						}
						if (!no2.isEmpty() && rw.getWorkTimeCode() != null) {
							if (!workTimes.containsKey(rw.getWorkTimeCode())) {
								String KAL010_9 = TextResource.localize("KAL010_9", rw.getWorkTimeCode());
								String checkedValue = TextResource.localize("KAL010_77", rw.getWorkTimeCode());
								no2.stream().forEach(fixed -> {
									listValueExtractAlarm.add(new ValueExtractAlarm(emp.getWorkplaceId(), emp.getId(),
											date.toString(ErAlConstant.DATE_FORMAT), KAL010_1, KAL010_8, KAL010_9,
											fixed.getMessage(), checkedValue));
								});
							}
						}
					} else {
						String checkedValue = KAL010_74;
						noOthers.stream().forEach(c -> {
							listValueExtractAlarm.add(new ValueExtractAlarm(emp.getWorkplaceId(), emp.getId(),
									date.toString(ErAlConstant.DATE_FORMAT), KAL010_1, KAL010_65, KAL010_66,
									c.getMessage(), checkedValue));
						});
					}
				});
			}
		}
	}

	public List<ValueExtractAlarm> dailyAggregationProcess(String comId, String checkConditionCode,
			PeriodByAlarmCategory period, List<EmployeeSearchDto> employees, DatePeriod datePeriod) {
		List<String> employeeIds = employees.stream().map(e -> e.getId()).collect(Collectors.toList());

		List<ValueExtractAlarm> listValueExtractAlarm = Collections.synchronizedList(new ArrayList<>());

		// ドメインモデル「カテゴリ別アラームチェック条件」を取得する
		alCheckConByCategoryRepo.find(comId, AlarmCategory.DAILY.value, checkConditionCode)
				.ifPresent(alCheckConByCategory -> {
					// カテゴリアラームチェック条件．抽出条件を元に日次データをチェックする
					DailyAlarmCondition dailyAlarmCondition = (DailyAlarmCondition) alCheckConByCategory
							.getExtractionCondition();

					// tab2: 日別実績のエラーアラーム
					listValueExtractAlarm.addAll(
							this.extractDailyRecord(dailyAlarmCondition, datePeriod, employees, comId, employeeIds));

					// tab3: チェック条件
					listValueExtractAlarm.addAll(
							this.extractCheckCondition(dailyAlarmCondition, datePeriod, employees, comId, employeeIds));

					// tab4: 「システム固定のチェック項目」で実績をチェックする
					List<ValueExtractAlarm> fixed = Collections.synchronizedList(new ArrayList<>());
//					employees.stream().map(e -> this.extractFixedCondition(dailyAlarmCondition, period, e))
//					.flatMap(List::stream).collect(Collectors.toList());
					/** 並列処理、AsyncTask */
					// Create thread pool.
					ExecutorService executorService = Executors.newFixedThreadPool(5);
					CountDownLatch countDownLatch = new CountDownLatch(employees.size());

					employees.forEach(employee -> {
						AsyncTask task = AsyncTask.builder().withContexts().keepsTrack(true)
								.threadName(this.getClass().getName()).build(() -> {
									try {
										fixed.addAll(this.extractFixedCondition(dailyAlarmCondition, period, employee));
									} catch (Exception e2) {
										e2.printStackTrace();

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
					listValueExtractAlarm.addAll(fixed);
				});

		return listValueExtractAlarm;
	}

	// tab2: 日別実績のエラーアラーム
	private List<ValueExtractAlarm> extractDailyRecord(DailyAlarmCondition dailyAlarmCondition, DatePeriod period,
			List<EmployeeSearchDto> employee, String companyID, List<String> employeeIds) {
		return dailyPerformanceService.aggregationProcess(dailyAlarmCondition, period, employeeIds, employee,
				companyID);
	}

	// tab3: チェック条件
	private List<ValueExtractAlarm> extractCheckCondition(DailyAlarmCondition dailyAlarmCondition, DatePeriod period,
			List<EmployeeSearchDto> employee, String companyID, List<String> emIds) {
		String KAL010_1 = TextResource.localize("KAL010_1");
		List<ValueExtractAlarm> listValueExtractAlarm = new ArrayList<>();
		// 勤務実績の抽出条件,
		List<WorkRecordExtraConAdapterDto> listWorkRecordExtraCon = workRecordExtraConAdapter
				.getAllWorkRecordExtraConByListID(dailyAlarmCondition.getExtractConditionWorkRecord());
		Map<String, WorkRecordExtraConAdapterDto> mapWorkRecordExtraCon = listWorkRecordExtraCon.stream()
				.collect(Collectors.toMap(WorkRecordExtraConAdapterDto::getErrorAlarmCheckID, x -> x));
		List<ErrorRecordImport> listErrorRecord = erAlWorkRecordCheckAdapter
				.check(dailyAlarmCondition.getExtractConditionWorkRecord(), period, emIds);
		/** fix response */
		// get nameAlarmItem by type
		List<AlarmItemName> listAlarmItemName = this.getAlarmItemByType();
		// get all attendance ID and all name
		List<Integer> listAttdID = dailyAttendanceItemAdapter.getDailyAttendanceItemList(companyID).stream()
				.map(c -> c.getAttendanceItemId()).collect(Collectors.toList());
		List<DailyAttendanceItem> listAttenDanceItem = attendanceNameService.getNameOfDailyAttendanceItem(listAttdID);
		// Get map all name Attd Name
		Map<Integer, DailyAttendanceItem> mapAttdName = listAttenDanceItem.stream()
				.collect(Collectors.toMap(DailyAttendanceItem::getAttendanceItemId, x -> x));
		for (ErrorRecordImport errorRecord : listErrorRecord) {
			if (errorRecord.isError()) {
				EmployeeSearchDto em = employee.stream().filter(e -> e.getId().equals(errorRecord.getEmployeeId()))
						.findFirst().get();
				String alarmItem = "";
				TypeCheckWorkRecord checkItem = EnumAdaptor.valueOf(
						mapWorkRecordExtraCon.get(errorRecord.getErAlId()).getCheckItem(), TypeCheckWorkRecord.class);
				for (AlarmItemName alarmItemName : listAlarmItemName) {
					if (alarmItemName.getCheckItem() == checkItem) {
						alarmItem = alarmItemName.getNameAlarm();
						break;
					}
				}
				listValueExtractAlarm.add(this.checkConditionGenerateValue(em, errorRecord.getDate(),
						mapWorkRecordExtraCon.get(errorRecord.getErAlId()), companyID, alarmItem, mapAttdName, KAL010_1,
						""));
			}
		}

		return listValueExtractAlarm;
	}

	// tab4: 「システム固定のチェック項目」で実績をチェックする
	private List<ValueExtractAlarm> extractFixedCondition(DailyAlarmCondition dailyAlarmCondition,
			PeriodByAlarmCategory period, EmployeeSearchDto employee) {
		List<ValueExtractAlarm> listValueExtractAlarm = new ArrayList<>();

		// get data by dailyAlarmCondition
		List<FixedConWorkRecordAdapterDto> listFixed = fixedConWorkRecordAdapter
				.getAllFixedConWorkRecordByID(dailyAlarmCondition.getDailyAlarmConID()).stream()
				.sorted((x, y) -> x.getFixConWorkRecordNo() - y.getFixConWorkRecordNo()).collect(Collectors.toList());
		for (int i = 0; i < listFixed.size(); i++) {
			if (listFixed.get(i).isUseAtr()) {
				FixedConWorkRecordAdapterDto fixedData = listFixed.get(i);
				switch (i) {
				case 0:
					for (GeneralDate date : period.getListDate()) {
						String workType = null;
						Optional<RecordWorkInfoFunAdapterDto> recordWorkInfo = recordWorkInfoFunAdapter
								.getInfoCheckNotRegister(employee.getId(), date);
						if (recordWorkInfo.isPresent()) {
							workType = recordWorkInfo.get().getWorkTypeCode();
							if (workType == null || workType.isEmpty())
								continue;
							Optional<ValueExtractAlarm> checkWorkType = fixedCheckItemAdapter.checkWorkTypeNotRegister(
									employee.getWorkplaceId(), employee.getId(), date, workType);
							if (checkWorkType.isPresent()) {
								ValueExtractAlarm tmp = checkWorkType.get();
								tmp.setComment(Optional.ofNullable(fixedData.getMessage()));
								listValueExtractAlarm.add(tmp);
							}
						}
					}
					break;
				case 1:
					for (GeneralDate date : period.getListDate()) {
						String workTime = null;
						Optional<RecordWorkInfoFunAdapterDto> recordWorkInfoFunAdapterDto = recordWorkInfoFunAdapter
								.getInfoCheckNotRegister(employee.getId(), date);
						if (recordWorkInfoFunAdapterDto.isPresent()) {
							workTime = recordWorkInfoFunAdapterDto.get().getWorkTimeCode();
							if (workTime == null || workTime.isEmpty())
								continue;
							Optional<ValueExtractAlarm> checkWorkTime = fixedCheckItemAdapter.checkWorkTimeNotRegister(
									employee.getWorkplaceId(), employee.getId(), date, workTime);
							if (checkWorkTime.isPresent()) {
								ValueExtractAlarm tmp = checkWorkTime.get();
								tmp.setComment(Optional.ofNullable(fixedData.getMessage()));
								listValueExtractAlarm.add(tmp);
							}
						}
					}
					break;
				case 2:
					List<ValueExtractAlarm> listCheckPrincipalUnconfirm = fixedCheckItemAdapter.checkPrincipalUnconfirm(
							employee.getWorkplaceId(), employee.getId(), period.getStartDate(), period.getEndDate());
					if (!listCheckPrincipalUnconfirm.isEmpty()) {
						for (ValueExtractAlarm tmp : listCheckPrincipalUnconfirm) {
							tmp.setComment(Optional.ofNullable(fixedData.getMessage()));
						}
						listValueExtractAlarm.addAll(listCheckPrincipalUnconfirm);
					}
					break;
				case 3:
					/*
					 * No 113 old List<ValueExtractAlarm> listCheckAdminUnverified =
					 * fixedCheckItemAdapter.checkAdminUnverified( employee.getWorkplaceId(),
					 * employee.getId(), period.getStartDate(), period.getEndDate());
					 */

					List<ValueExtractAlarm> listCheckAdminUnverified = fixedCheckItemAdapter.checkAdminUnverified(
							employee.getWorkplaceId(), employee.getId(),
							new DatePeriod(period.getStartDate(), period.getEndDate()));

					if (!listCheckAdminUnverified.isEmpty()) {
						for (ValueExtractAlarm tmp : listCheckAdminUnverified) {
							tmp.setComment(Optional.ofNullable(fixedData.getMessage()));
						}
						listValueExtractAlarm.addAll(listCheckAdminUnverified);
					}
					break;
				case 5:
					List<ValueExtractAlarm> listCheckVacation = fixedCheckItemAdapter.checkContinuousVacation(
							employee.getId(), new DatePeriod(period.getStartDate(), period.getEndDate()));

					if (!listCheckVacation.isEmpty()) {
						for (ValueExtractAlarm tmp : listCheckVacation) {
							tmp.setComment(Optional.ofNullable(fixedData.getMessage()));
						}
						listValueExtractAlarm.addAll(listCheckVacation);
					}
					break;
				default:
					List<ValueExtractAlarm> listCheckingData = fixedCheckItemAdapter.checkingData(
							employee.getWorkplaceId(), employee.getId(), period.getStartDate(), period.getEndDate());
					if (!listCheckingData.isEmpty()) {
						for (ValueExtractAlarm tmp : listCheckingData) {
							tmp.setComment(Optional.ofNullable(fixedData.getMessage()));
						}
						List<String> listDate = listCheckingData.stream().map(u -> u.getAlarmValueDate())
								.collect(Collectors.toList());
						listValueExtractAlarm = listValueExtractAlarm.stream()
								.filter(o -> !listDate.contains(o.getAlarmValueDate())).collect(Collectors.toList());
						listValueExtractAlarm.addAll(listCheckingData);
					}
					break;
				}// end switch
			} // end if
		} // end for
		return listValueExtractAlarm;
	}

	private List<DailyAttendanceItemAdapterDto> getDailyAttendanceItems(String comId) {
		return dailyAttendanceItemAdapter.getDailyAttendanceItemList(comId).stream().map(item -> {
			String name = item.getAttendanceName();
			if (name.indexOf("{#") >= 0) {
				int startLocation = name.indexOf("{");
				int endLocation = name.indexOf("}");
				name = name.replace(name.substring(startLocation, endLocation + 1),
						TextResource.localize(name.substring(startLocation + 2, endLocation)));
			}
			return new DailyAttendanceItemAdapterDto(item.getCompanyId(), item.getAttendanceItemId(), name,
					item.getDisplayNumber(), item.getUserCanUpdateAtr(), item.getDailyAttendanceAtr(),
					item.getNameLineFeedPosition(), item.getDisplayName());
		}).collect(Collectors.toList());
	}

	private List<AlarmItemName> getAlarmItemByType() {
		List<AlarmItemName> listAlarmItemName = new ArrayList<>();
		listAlarmItemName.add(new AlarmItemName(TypeCheckWorkRecord.TIME, TextResource.localize("KAL010_47")));
		listAlarmItemName.add(new AlarmItemName(TypeCheckWorkRecord.TIMES, TextResource.localize("KAL010_50")));
		listAlarmItemName
				.add(new AlarmItemName(TypeCheckWorkRecord.AMOUNT_OF_MONEY, TextResource.localize("KAL010_51")));
		listAlarmItemName.add(new AlarmItemName(TypeCheckWorkRecord.TIME_OF_DAY, TextResource.localize("KAL010_52")));
		listAlarmItemName
				.add(new AlarmItemName(TypeCheckWorkRecord.CONTINUOUS_TIME, TextResource.localize("KAL010_53")));
		listAlarmItemName
				.add(new AlarmItemName(TypeCheckWorkRecord.CONTINUOUS_WORK, TextResource.localize("KAL010_56")));
		listAlarmItemName
				.add(new AlarmItemName(TypeCheckWorkRecord.CONTINUOUS_TIME_ZONE, TextResource.localize("KAL010_58")));
		listAlarmItemName
				.add(new AlarmItemName(TypeCheckWorkRecord.CONTINUOUS_CONDITION, TextResource.localize("KAL010_60")));
		return listAlarmItemName;
	}

	/**
	 * 実績をチェックする
	 * 
	 * @param employee           社員情報
	 * @param date               日付
	 * @param workRecordExtraCon
	 * @param companyID
	 * @param alarmItem
	 * @param mapAtdItemName
	 * @param KAL010_1
	 * @param checkedValue
	 * @return
	 */
	private ValueExtractAlarm checkConditionGenerateValue(EmployeeSearchDto employee, GeneralDate date,
			WorkRecordExtraConAdapterDto workRecordExtraCon, String companyID, String alarmItem,
			Map<Integer, DailyAttendanceItem> mapAtdItemName, String KAL010_1, String checkedValue) {
		String checkedValueNew = checkedValue;
		String alarmContent = "";
		TypeCheckWorkRecord checkItem = EnumAdaptor.valueOf(workRecordExtraCon.getCheckItem(),
				TypeCheckWorkRecord.class);
		// アラームエラー項目の実績をチェック
		alarmContent = checkConditionGenerateAlarmContent(checkItem, workRecordExtraCon, companyID, mapAtdItemName);
		if (alarmContent.length() > 100) {
			alarmContent = alarmContent.substring(0, 100);
		}
		if (workRecordExtraCon.getCheckItem() == TypeCheckWorkRecord.CONTINUOUS_CONDITION.value) {
			checkedValueNew = TextResource.localize("KAL010_998");
		} else if (workRecordExtraCon.getCheckItem() == TypeCheckWorkRecord.TIME.value
				|| workRecordExtraCon.getCheckItem() == TypeCheckWorkRecord.TIME_OF_DAY.value) {
			checkedValueNew = this.formatHourData(String.valueOf(Double.valueOf(checkedValueNew).intValue()),
					EnumAdaptor.valueOf(workRecordExtraCon.getCheckItem(), TypeCheckWorkRecord.class));
		}
		return new ValueExtractAlarm(employee.getWorkplaceId(), employee.getId(),
				date.toString(ErAlConstant.DATE_FORMAT), KAL010_1, workRecordExtraCon.getNameWKRecord(), alarmContent,
				workRecordExtraCon.getErrorAlarmCondition().getDisplayMessage(), checkedValueNew);
	}

	/**
	 * アラームエラー項目の実績をチェック
	 * 
	 * @param checkItem
	 * @param workRecordExtraCon
	 * @param companyID
	 * @param mapAtdItemName
	 * @return
	 */
	private String checkConditionGenerateAlarmContent(TypeCheckWorkRecord checkItem,
			WorkRecordExtraConAdapterDto workRecordExtraCon, String companyID,
			Map<Integer, DailyAttendanceItem> mapAtdItemName) {

		if (workRecordExtraCon.getErrorAlarmCondition().getAtdItemCondition().getGroup1().getLstErAlAtdItemCon()
				.isEmpty())
			return "";

		String alarmContent = "";
		String wktypeText = "";
		String attendanceText = "";
		String wktimeText = "";
		// 勤怠項目に対する条件:勤怠項目の複合エラーアラーム条件
		ErAlAtdItemConAdapterDto atdItemCon = workRecordExtraCon.getErrorAlarmCondition().getAtdItemCondition()
				.getGroup1().getLstErAlAtdItemCon().get(0);
		// 単一値との比較
		CoupleOperator coupleOperator = findOperator(atdItemCon.getCompareOperator());

		switch (checkItem) {
		// 時間、回数、時刻、金額の場合
		case TIME:
		case TIMES:
		case AMOUNT_OF_MONEY:
		case TIME_OF_DAY:
			// 勤務種類
			wktypeText = calculateWktypeText(workRecordExtraCon, companyID);
			//
			attendanceText = calculateAttendanceText(atdItemCon, mapAtdItemName);

			if (!singleCompare(atdItemCon.getCompareOperator())) {

				if (betweenRange(atdItemCon.getCompareOperator())) {
					alarmContent = TextResource.localize("KAL010_49", wktypeText,
							this.formatHourData(atdItemCon.getCompareStartValue().toString(), checkItem),
							coupleOperator.getOperatorStart(), attendanceText, coupleOperator.getOperatorEnd(),
							this.formatHourData(atdItemCon.getCompareEndValue().toString(), checkItem));
				} else {
					alarmContent = TextResource.localize("KAL010_121", wktypeText, attendanceText,
							coupleOperator.getOperatorStart(),
							this.formatHourData(atdItemCon.getCompareStartValue().toString(), checkItem),
							this.formatHourData(atdItemCon.getCompareEndValue().toString(), checkItem),
							coupleOperator.getOperatorEnd(), attendanceText);
				}

			} else {
				if (atdItemCon.getConditionType() == ConditionType.FIXED_VALUE.value) {
					alarmContent = TextResource.localize("KAL010_48", wktypeText, attendanceText,
							coupleOperator.getOperatorStart(),
							this.formatHourData(atdItemCon.getCompareStartValue().toString(), checkItem));
				} else {
					alarmContent = TextResource.localize("KAL010_48", wktypeText, attendanceText,
							coupleOperator.getOperatorStart(),
							mapAtdItemName.containsKey(atdItemCon.getSingleAtdItem())
									? mapAtdItemName.get(atdItemCon.getSingleAtdItem()).getAttendanceItemName()
									: "");
				}
			}
			break;
		// 連続時間
		case CONTINUOUS_TIME:
			wktypeText = calculateWktypeText(workRecordExtraCon, companyID);
			attendanceText = calculateAttendanceText(atdItemCon, mapAtdItemName);

			if (!singleCompare(atdItemCon.getCompareOperator())) {
				if (betweenRange(atdItemCon.getCompareOperator())) {
					alarmContent = TextResource.localize("KAL010_55", wktypeText,
							this.formatHourData(atdItemCon.getCompareStartValue().toString(), checkItem),
							coupleOperator.getOperatorStart(), attendanceText, coupleOperator.getOperatorEnd(),
							this.formatHourData(atdItemCon.getCompareEndValue().toString(), checkItem),
							workRecordExtraCon.getErrorAlarmCondition().getContinuousPeriod() + "");
				} else {
					alarmContent = TextResource.localize("KAL010_122", wktypeText, attendanceText,
							coupleOperator.getOperatorStart(), atdItemCon.getCompareStartValue().toString(),
							atdItemCon.getCompareEndValue().toString(), coupleOperator.getOperatorEnd(), attendanceText,
							workRecordExtraCon.getErrorAlarmCondition().getContinuousPeriod() + "");
				}

			} else {
				if (atdItemCon.getConditionType() == ConditionType.FIXED_VALUE.value) {
					alarmContent = TextResource.localize("KAL010_54", wktypeText, attendanceText,
							coupleOperator.getOperatorStart(),
							this.formatHourData(atdItemCon.getCompareStartValue().toString(), checkItem),
							workRecordExtraCon.getErrorAlarmCondition().getContinuousPeriod() + "");
				} else {
					alarmContent = TextResource.localize("KAL010_54", wktypeText, attendanceText,
							coupleOperator.getOperatorStart(),
							mapAtdItemName.containsKey(atdItemCon.getSingleAtdItem())
									? mapAtdItemName.get(atdItemCon.getSingleAtdItem()).getAttendanceItemName()
									: "",
							workRecordExtraCon.getErrorAlarmCondition().getContinuousPeriod() + "");
				}
			}
			break;
		// 連続勤務
		case CONTINUOUS_WORK:
			wktypeText = calculateWktypeText(workRecordExtraCon, companyID);

			alarmContent = TextResource.localize("KAL010_57", wktypeText,
					workRecordExtraCon.getErrorAlarmCondition().getContinuousPeriod() + "");
			break;
		// 連続時間帯
		case CONTINUOUS_TIME_ZONE:
			wktypeText = calculateWktypeText(workRecordExtraCon, companyID);
			wktimeText = calculateWkTimeText(workRecordExtraCon, mapAtdItemName);

			alarmContent = TextResource.localize("KAL010_59", wktypeText, wktimeText,
					workRecordExtraCon.getErrorAlarmCondition().getContinuousPeriod() + "");
			break;
		// 複合条件
		case CONTINUOUS_CONDITION:
			String alarmGroup1 = "";
			String alarmGroup2 = "";
			ErAlConAttendanceItemAdapterDto group1 = workRecordExtraCon.getErrorAlarmCondition().getAtdItemCondition()
					.getGroup1();
			alarmGroup1 = generateAlarmGroup(group1, checkItem, mapAtdItemName);
			ErAlConAttendanceItemAdapterDto group2 = workRecordExtraCon.getErrorAlarmCondition().getAtdItemCondition()
					.getGroup2();
			alarmGroup2 = generateAlarmGroup(group2, checkItem, mapAtdItemName);
			if (alarmGroup1.length() != 0 && alarmGroup2.length() != 0) {
				alarmContent = alarmGroup1 + logicalOperator(
						workRecordExtraCon.getErrorAlarmCondition().getAtdItemCondition().getOperatorBetweenGroups())
						+ alarmGroup2;
			} else {
				alarmContent = alarmGroup1 + alarmGroup2;
			}
			break;
		default:
			break;
		}

		return alarmContent;
	}

	private String formatHourData(String minutes, TypeCheckWorkRecord checkItem) {
		if (checkItem == TypeCheckWorkRecord.TIME || checkItem == TypeCheckWorkRecord.TIME_OF_DAY) {
			String h = "", m = "";
			if (minutes != null && !minutes.equals("")) {
				Integer hour = Integer.parseInt(minutes);
				h = hour.intValue() / 60 + "";
				m = hour.intValue() % 60 + "";
				if (h.length() < 2)
					h = "0" + h;
				if (m.length() < 2)
					m = "0" + m;

				return h + ":" + m;
			} else {
				return "";
			}

		} else {
			return minutes;
		}

	}

	private String formatHourDataByGroup(String minutes, int conditionAtr) {
		// conditionAtr = 1 : 時間 ,conditionAtr = 2 : 時刻
		if (conditionAtr == 1 || conditionAtr == 2) {
			String h = "", m = "";
			if (minutes != null && !minutes.equals("")) {
				Integer hour = Integer.parseInt(minutes);
				h = hour.intValue() / 60 + "";
				m = hour.intValue() % 60 + "";
				if (h.length() < 2)
					h = "0" + h;
				if (m.length() < 2)
					m = "0" + m;

				return h + ":" + m;
			} else {
				return "";
			}

		} else {
			return minutes;
		}

	}

	/**
	 * 勤怠項目の複合条件をチェックする
	 * 
	 * @param group
	 * @param checkItem
	 * @param mapAtdItemName
	 * @return
	 */
	private String generateAlarmGroup(ErAlConAttendanceItemAdapterDto group, TypeCheckWorkRecord checkItem,
			Map<Integer, DailyAttendanceItem> mapAtdItemName) {
		String alarmGroup = "";

		if (!group.getLstErAlAtdItemCon().isEmpty()) {
			for (int i = 0; i < group.getLstErAlAtdItemCon().size(); i++) {
				ErAlAtdItemConAdapterDto itemCon = group.getLstErAlAtdItemCon().get(i);
				CoupleOperator coupleOperator = findOperator(itemCon.getCompareOperator());
				String alarm = "";
				if (singleCompare(itemCon.getCompareOperator())) {
					if (itemCon.getConditionType() == ConditionType.FIXED_VALUE.value) {
						alarm = "(式" + (i + 1) + " " + calculateAttendanceText(itemCon, mapAtdItemName)
								+ coupleOperator.getOperatorStart()
								+ this.formatHourDataByGroup(String.valueOf(itemCon.getCompareStartValue()),
										itemCon.getConditionAtr())
								+ ")";
					} else {
						String attendenceItemName = mapAtdItemName.containsKey(itemCon.getSingleAtdItem())
								? mapAtdItemName.get(itemCon.getSingleAtdItem()).getAttendanceItemName()
								: "";
						alarm = "(式" + (i + 1) + " " + calculateAttendanceText(itemCon, mapAtdItemName)
								+ coupleOperator.getOperatorStart() + attendenceItemName + ")";
					}

				} else {
					if (betweenRange(itemCon.getCompareOperator())) {
						alarm = "(式" + (i + 1) + " "
								+ this.formatHourDataByGroup(String.valueOf(itemCon.getCompareStartValue()),
										itemCon.getConditionAtr())
								+ coupleOperator.getOperatorStart() + calculateAttendanceText(itemCon, mapAtdItemName)
								+ coupleOperator.getOperatorEnd() + this.formatHourDataByGroup(
										String.valueOf(itemCon.getCompareEndValue()), itemCon.getConditionAtr())
								+ ")";
					} else {
						alarm = "(式" + (i + 1) + " " + calculateAttendanceText(itemCon, mapAtdItemName)
								+ coupleOperator.getOperatorStart()
								+ this.formatHourDataByGroup(
										String.valueOf(itemCon.getCompareStartValue()), itemCon.getConditionAtr())
								+ ", "
								+ this.formatHourDataByGroup(String.valueOf(itemCon.getCompareEndValue()),
										itemCon.getConditionAtr())
								+ coupleOperator.getOperatorEnd() + calculateAttendanceText(itemCon, mapAtdItemName)
								+ ")";
					}
				}

				if (alarmGroup.length() == 0) {
					alarmGroup += alarm;
				} else {
					alarmGroup += logicalOperator(group.getConditionOperator()) + alarm;
				}
			}
		}

		return alarmGroup;
	}

	/**
	 * 勤務種類をチェックする
	 * 
	 * @param workRecordExtraCon
	 * @param companyID
	 * @return
	 */
	private String calculateWktypeText(WorkRecordExtraConAdapterDto workRecordExtraCon, String companyID) {

		List<String> workTypeCodes = workRecordExtraCon.getErrorAlarmCondition().getWorkTypeCondition()
				.getPlanLstWorkType();
		if (workTypeCodes.isEmpty() || workTypeCodes == null)
			return "";
		List<String> workTypeNames = workTypeRepository.findNotDeprecatedByListCode(companyID, workTypeCodes).stream()
				.map(x -> x.getName().toString()).collect(Collectors.toList());
		return workTypeNames.isEmpty() ? "" : String.join(",", workTypeNames);
	}

	/**
	 * 勤怠項目をチェックする
	 * 
	 * @param atdItemCon
	 * @param attendanceItemMap
	 * @return
	 */
	private String calculateAttendanceText(ErAlAtdItemConAdapterDto atdItemCon,
			Map<Integer, DailyAttendanceItem> attendanceItemMap) {
		String attendanceText = "";

		if (atdItemCon.getConditionAtr() == ConditionAtr.TIME_WITH_DAY.value) {
			if (atdItemCon.getUncountableAtdItem() > 0) {
				List<DailyAttendanceItem> listAttendance = new ArrayList<>();

				DailyAttendanceItem attendanceItem = attendanceItemMap.get(atdItemCon.getUncountableAtdItem());
				if (attendanceItem != null) {
					listAttendance.add(attendanceItem);
				}
				// List<DailyAttendanceItem> listAttendance =
				// attendanceNameService.getNameOfDailyAttendanceItem(Arrays.asList(atdItemCon.getUncountableAtdItem()));
				if (!listAttendance.isEmpty())
					attendanceText = listAttendance.get(0).getAttendanceItemName();
			}

		} else {
			if (!atdItemCon.getCountableAddAtdItems().isEmpty()) {
				List<DailyAttendanceItem> listAttendanceAdd = new ArrayList<>();
				for (int attendanceID : atdItemCon.getCountableAddAtdItems()) {
					DailyAttendanceItem attendanceItem = attendanceItemMap.get(attendanceID);
					if (attendanceItem != null) {
						listAttendanceAdd.add(attendanceItem);
					}
				}
				// List<DailyAttendanceItem> listAttendanceAdd =
				// attendanceNameService.getNameOfDailyAttendanceItem(atdItemCon.getCountableAddAtdItems());
				if (!listAttendanceAdd.isEmpty()) {
					for (int i = 0; i < listAttendanceAdd.size(); i++) {
						String operator = (i == (listAttendanceAdd.size() - 1)) ? "" : " + ";
						attendanceText += listAttendanceAdd.get(i).getAttendanceItemName() + operator;
					}
				}
				if (!atdItemCon.getCountableSubAtdItems().isEmpty()) {
					List<DailyAttendanceItem> listAttendanceSub = new ArrayList<>();
					for (int attendanceID : atdItemCon.getCountableSubAtdItems()) {
						DailyAttendanceItem attendanceItem = attendanceItemMap.get(attendanceID);
						if (attendanceItem != null) {
							listAttendanceSub.add(attendanceItem);
						}
					}
					// List<DailyAttendanceItem> listAttendanceSub =
					// attendanceNameService.getNameOfDailyAttendanceItem(atdItemCon.getCountableAddAtdItems());
					if (!listAttendanceSub.isEmpty()) {
						for (int i = 0; i < listAttendanceSub.size(); i++) {
							String operator = (i == (listAttendanceSub.size() - 1)) ? "" : " - ";
							String beforeOperator = (i == 0) ? " - " : "";
							attendanceText += beforeOperator + listAttendanceSub.get(i).getAttendanceItemName()
									+ operator;
						}
					}
				}
			} else if (!atdItemCon.getCountableSubAtdItems().isEmpty()) {
				List<DailyAttendanceItem> listAttendanceSub = new ArrayList<>();
				for (int attendanceID : atdItemCon.getCountableSubAtdItems()) {
					DailyAttendanceItem attendanceItem = attendanceItemMap.get(attendanceID);
					if (attendanceItem != null) {
						listAttendanceSub.add(attendanceItem);
					}
				}
//				List<DailyAttendanceItem>  listAttendanceSub = attendanceNameService.getNameOfDailyAttendanceItem(atdItemCon.getCountableAddAtdItems());
				if (!listAttendanceSub.isEmpty()) {
					for (int i = 0; i < listAttendanceSub.size(); i++) {
						String operator = (i == (listAttendanceSub.size() - 1)) ? "" : " - ";
						String beforeOperator = (i == 0) ? " - " : "";
						attendanceText += beforeOperator + listAttendanceSub.get(i).getAttendanceItemName() + operator;
					}
				}
			}
		}
		return attendanceText;
	}

	private String calculateWkTimeText(WorkRecordExtraConAdapterDto workRecordExtraCon,
			Map<Integer, DailyAttendanceItem> attenDanceItemMap) {

		List<Integer> listWorkTimeIds = workRecordExtraCon.getErrorAlarmCondition().getWorkTimeCondition()
				.getPlanLstWorkTime().stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
		List<DailyAttendanceItem> listAttdItem = new ArrayList<>();
		for (int attendanceID : listWorkTimeIds) {
			DailyAttendanceItem dailyAttendanceItem = attenDanceItemMap.get(attendanceID);
			if (dailyAttendanceItem != null) {
				listAttdItem.add(dailyAttendanceItem);
			}
		}
		List<String> workTimeNames = listAttdItem.stream().map(e -> e.getAttendanceItemName())
				.collect(Collectors.toList());
		// List<String> workTimeNames =
		// attendanceNameService.getNameOfDailyAttendanceItem(listWorkTimeIds).stream().map(
		// e-> e.getAttendanceItemName()).collect(Collectors.toList());
		return workTimeNames.isEmpty() ? "" : String.join(",", workTimeNames);

	}

	/**
	 * 単一値との比較
	 * 
	 * @param compareOperator
	 * @return
	 */
	private CoupleOperator findOperator(int compareOperator) {
		CompareType compareType = EnumAdaptor.valueOf(compareOperator, CompareType.class);

		switch (compareType) {
		case EQUAL:
			return new CoupleOperator("＝", "");
		case NOT_EQUAL:
			return new CoupleOperator("≠", "");
		case GREATER_THAN:
			return new CoupleOperator("＞", "");
		case GREATER_OR_EQUAL:
			return new CoupleOperator("≧", "");
		case LESS_THAN:
			return new CoupleOperator("＜", "");
		case LESS_OR_EQUAL:
			return new CoupleOperator("≦", "");
		case BETWEEN_RANGE_OPEN:
			return new CoupleOperator("＜", "＜");
		case BETWEEN_RANGE_CLOSED:
			return new CoupleOperator("≦", "≦");
		case OUTSIDE_RANGE_OPEN:
			return new CoupleOperator("＜", "＜");
		case OUTSIDE_RANGE_CLOSED:
			return new CoupleOperator("≦", "≦");
		}

		return null;
	}

	private boolean singleCompare(int compareOperator) {
		return compareOperator <= CompareType.GREATER_THAN.value;
	}

	private boolean betweenRange(int compareOperator) {
		return compareOperator == CompareType.BETWEEN_RANGE_OPEN.value
				|| compareOperator == CompareType.BETWEEN_RANGE_CLOSED.value;
	}

	private String logicalOperator(int logicalOperator) {
		if (logicalOperator == LogicalOperator.AND.value)
			return " AND ";
		else
			return " OR ";
	}
}
