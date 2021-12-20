package nts.uk.screen.at.app.dailymodify.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.task.AsyncTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.gul.util.value.MutableValue;
import nts.uk.ctx.at.record.app.command.dailyperform.DailyCorrectEventServiceCenter;
import nts.uk.ctx.at.record.app.command.dailyperform.DailyRecordWorkCommand;
import nts.uk.ctx.at.record.app.command.dailyperform.audittrail.DPAttendanceItemRC;
import nts.uk.ctx.at.record.app.command.dailyperform.checkdata.DailyModifyRCResult;
import nts.uk.ctx.at.record.app.command.dailyperform.checkdata.RCDailyCorrectionResult;
import nts.uk.ctx.at.record.app.command.dailyperform.correctevent.EventCorrectResult;
import nts.uk.ctx.at.record.app.command.dailyperform.month.UpdateMonthDailyParam;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.dom.daily.itemvalue.DailyItemValue;
import nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem.RegisterPastMonthTotalResult;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.CorrectDailyAttendanceService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.screen.at.app.dailymodify.CorrectDaiAttRequireImpl;
import nts.uk.screen.at.app.dailymodify.command.DailyModifyRCommandFacade;
import nts.uk.screen.at.app.dailymodify.command.InsertAllData;
import nts.uk.screen.at.app.dailymodify.command.common.DailyCalcParam;
import nts.uk.screen.at.app.dailymodify.command.common.DailyCalcResult;
import nts.uk.screen.at.app.dailymodify.command.common.ProcessCommonCalc;
import nts.uk.screen.at.app.dailymodify.command.common.ProcessDailyCalc;
import nts.uk.screen.at.app.dailymodify.command.common.ProcessMonthlyCalc;
import nts.uk.screen.at.app.dailymodify.mobile.dto.DPMobileAdUpParam;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyQuery;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyResult;
import nts.uk.screen.at.app.dailyperformance.correction.calctime.DailyCorrectCalcTimeService;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.ValidatorDataDailyRes;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.dto.ErrorAfterCalcDaily;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DataResultAfterIU;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DatePeriodInfo;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DisplayFormat;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ResultReturnDCUpdateData;
import nts.uk.screen.at.app.dailyperformance.correction.dto.TypeError;
import nts.uk.screen.at.app.dailyperformance.correction.dto.cache.AggrPeriodClosure;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class DailyModifyMobileCommandFacade {

	@Inject
	private OptionalItemRepository optionalMasterRepo;

	@Inject
	private ValidatorDataDailyRes validatorDataDaily;

	@Inject
	private InsertAllData insertAllData;

	/** 並列処理用 */
	@Resource
	private ManagedExecutorService executerService;

	@Inject
	private ProcessDailyCalc processDailyCalc;

	@Inject
	private ProcessMonthlyCalc processMonthlyCalc;

	@Inject
	private DailyModifyRCommandFacade dailyRCommandFacade;

	@Inject
	private DailyCorrectCalcTimeService dCCalcTimeService;

	@Inject
	private CorrectDaiAttRequireImpl correctDaiAttRequireImpl;

	@Inject
	private DailyCorrectEventServiceCenter dailyCorrectEventServiceCenter;
	
	@Inject
	private RegisterPastMonthTotalResult registerPastMonthTotalResult;

	public DataResultAfterIU insertItemDomain(DPMobileAdUpParam dataParent) {
		// Map<Integer, List<DPItemValue>> resultError = new HashMap<>();
		Map<Integer, List<DPItemValue>> resultErrorMonth = new HashMap<>();
		DataResultAfterIU dataResultAfterIU = new DataResultAfterIU();
		Map<Pair<String, GeneralDate>, ResultReturnDCUpdateData> lstResultReturnDailyError = new HashMap<>();
		boolean hasErrorRow = false;
		boolean errorMonthAfterCalc = false;
		boolean editFlex = (dataParent.getMode() == 0 && dataParent.getMonthValue() != null
				&& !CollectionUtil.isEmpty(dataParent.getMonthValue().getItems()));
		dataParent.setCheckDailyChange(true);
		// insert flex
		UpdateMonthDailyParam monthParam = null;
		if (dataParent.getStateParam() != null && dataParent.getStateParam().getDateInfo() != null) {
			DatePeriodInfo paramCommon = dataParent.getStateParam().getDateInfo();
			AggrPeriodClosure aggrClosure = paramCommon.getLstClosureCache().stream()
					.filter(x -> x.getClosureId().value == paramCommon.getClosureId().value).findFirst().orElse(null);
			Optional<IntegrationOfMonthly> domainMonthOpt = Optional.empty();
			if (aggrClosure != null)
				monthParam = new UpdateMonthDailyParam(aggrClosure.getYearMonth(), dataParent.getEmployeeId(),
						aggrClosure.getClosureId().value, ClosureDateDto.from(aggrClosure.getClosureDate()),
						domainMonthOpt, new DatePeriod(dataParent.getDateRange().getStartDate(),
								dataParent.getDateRange().getEndDate()),
						"", true, true, 0L);
		}

		Map<Pair<String, GeneralDate>, List<DPItemValue>> mapSidDate = dataParent.getItemValues().stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.getEmployeeId(), x.getDate())));

		Map<Pair<String, GeneralDate>, List<DPItemValue>> mapSidDateNotChange = dataParent.getItemValues().stream()
				//.filter(x -> !DPText.ITEM_CHANGE_MOBI.contains(x.getItemId()))
				.collect(Collectors.groupingBy(x -> Pair.of(x.getEmployeeId(), x.getDate())));

		dCCalcTimeService.getWplPosId(dataParent.getItemValues());
		Map<Integer, OptionalItem> optionalMaster = optionalMasterRepo
				.findAll(AppContexts.user().companyId()).stream()
				.collect(Collectors.toMap(c -> c.getOptionalItemNo().v(), c -> c));
		
		List<DailyModifyQuery> querys = dailyRCommandFacade.createQuerys(mapSidDate);
		List<DailyModifyQuery> queryNotChanges = dailyRCommandFacade.createQuerys(mapSidDateNotChange);
		// map to list result -> check error;
		List<DailyRecordDto> dailyOlds = new ArrayList<>(), dailyEdits = new ArrayList<>();

		Set<Pair<String, GeneralDate>> pairSidDateCheck = new HashSet<>();
		dataParent.getDataCheckSign().stream().forEach(x -> {
			pairSidDateCheck.add(Pair.of(x.getEmployeeId(), x.getDate()));
		});

		dataParent.getDataCheckApproval().stream().forEach(x -> {
			pairSidDateCheck.add(Pair.of(x.getEmployeeId(), x.getDate()));
		});

		processDto(dailyOlds, dailyEdits, dataParent, querys, mapSidDate, pairSidDateCheck, queryNotChanges);
		// row data will insert
		Set<Pair<String, GeneralDate>> rowWillInsert = dailyEdits.stream()
				.map(x -> Pair.of(x.getEmployeeId(), x.getDate())).collect(Collectors.toSet());

		List<DailyModifyResult> resultOlds = AttendanceItemUtil.toItemValues(dailyOlds).entrySet().stream()
				.map(dto -> DailyModifyResult.builder().items(dto.getValue()).employeeId(dto.getKey().getEmployeeId())
						.workingDate(dto.getKey().getDate()).completed())
				.collect(Collectors.toList());

		RCDailyCorrectionResult resultIU = new RCDailyCorrectionResult();
		List<DailyItemValue> dailyItems = resultOlds.stream().map(
				x -> DailyItemValue.build().createEmpAndDate(x.getEmployeeId(), x.getDate()).createItems(x.getItems()))
				.collect(Collectors.toList());
		List<IntegrationOfDaily> domainDailyNew = dailyEdits.stream()
				.map(x -> x.toDomain(x.getEmployeeId(), x.getDate())).collect(Collectors.toList());
		Set<Pair<String, GeneralDate>> updated = new HashSet<>();
		Map<Integer, DPAttendanceItemRC> itemAtr = dataParent.getLstAttendanceItem().entrySet().stream()
				.collect(Collectors.toMap(x -> x.getKey(), x -> dailyRCommandFacade.convertItemAtr(x.getValue())));

		// 日別実績の修正からの計算
		String sid = AppContexts.user().employeeId();
		List<DailyRecordWorkCommand> commandNew = ProcessCommonCalc.createCommands(sid, dailyEdits, querys);

		List<DailyRecordWorkCommand> commandOld = ProcessCommonCalc.createCommands(sid, dailyOlds, querys);

		// 日別実績の修正からの計算
		// resultIU = processDailyCalc.calcDaily(dailyEdits, commandNew, commandOld,
		// dailyItems, monthParam);

		List<EmployeeMonthlyPerError> errorMonthHoliday = new ArrayList<>();
		if (dataParent.isCheckDailyChange()) {
			//勤怠ルールの補正処理
			//2021/03/19 - 日別修正から補正処理を実行する対応
			List<DailyRecordDto> dtoOldTemp = dailyOlds;
			dailyEdits = dailyEdits.stream().map(x -> {
				val changeSetting = ChangeDailyAttendance.createChangeDailyAtt(dataParent.getItemValues().stream()
						.filter(y -> y.getEmployeeId().equals(x.getEmployeeId()) && y.getDate().equals(x.getDate()))
						.map(y -> y.getItemId()).collect(Collectors.toList()), ScheduleRecordClassifi.RECORD);
				val domDaily = CorrectDailyAttendanceService.processAttendanceRule(
						correctDaiAttRequireImpl.createRequire(), x.toDomain(x.getEmployeeId(), x.getDate()),
						changeSetting);
				//振休振出として扱う日数を補正する
				val dailyOldSameDate = dtoOldTemp.stream().filter(
						old -> old.getEmployeeId().equals(x.getEmployeeId()) && old.getDate().equals(x.getDate()))
						.findFirst().orElse(null);
				CorrectDailyAttendanceService.correctFurikyu(correctDaiAttRequireImpl.createRequire(),
						dailyOldSameDate.getWorkInfo().toDomain(x.getEmployeeId(), x.getDate()), domDaily.getWorkInformation());
				//ootsuka mode
				if (AppContexts.optionLicense().customize().ootsuka()) {
					 List<DPItemValue> lstItemValue = mapSidDateNotChange.get(Pair.of(x.getEmployeeId(), x.getDate()));
					 if(lstItemValue.isEmpty()) {
						 return  DailyRecordDto.from(domDaily, optionalMaster);
					 }
					 val itemValues = lstItemValue.stream()
								.map(it -> new ItemValue(it.getValue(),
										it.getValueType() == null ? ValueType.UNKNOWN : ValueType.valueOf(it.getValueType()),
										it.getLayoutCode(), it.getItemId()))
								.collect(Collectors.toList());

					DailyModifyRCResult updatedOoTsuka = DailyModifyRCResult.builder().employeeId(x.getEmployeeId())
							.workingDate(x.getDate()).items(itemValues).completed();
					EventCorrectResult result = dailyCorrectEventServiceCenter.correctRunTime(
							DailyRecordDto.from(domDaily, optionalMaster),
							updatedOoTsuka, AppContexts.user().companyId());
					return result.getCorrected();
				}
				return DailyRecordDto.from(domDaily, optionalMaster);
			}).collect(Collectors.toList());
			DailyCalcResult daiCalcResult = processDailyCalc.processDailyCalc(
					new DailyCalcParam(mapSidDate, dataParent.getLstNotFoundWorkType(), resultOlds,
							dataParent.getDateRange(), dataParent.getDailyEdits(), dataParent.getItemValues()),
					dailyEdits, dailyOlds, dailyItems, querys, monthParam, true,
					ExecutionType.NORMAL_EXECUTION,null); //bên mobile chưa đối ứng nên trên check unlock == null
			if (daiCalcResult.getResultUI() == null) {
				return daiCalcResult.getDataResultAfterIU();
			}
			resultIU = daiCalcResult.getResultUI();
			ErrorAfterCalcDaily errorCheck = daiCalcResult.getErrorAfterCheck();
			errorMonthHoliday.addAll(errorCheck.getErrorMonth());
			boolean hasError = errorCheck.getHasError();
			if (hasError || !daiCalcResult.getLstResultDaiRowError().isEmpty()) {
				resultErrorMonth = errorCheck.getResultErrorMonth();
				lstResultReturnDailyError.putAll(daiCalcResult.getLstResultDaiRowError());
				lstResultReturnDailyError.putAll(errorCheck.getResultError());
				dataResultAfterIU.setErrorMap(resultErrorMonth);
				dailyEdits = dailyEdits.stream()
						.filter(x -> !lstResultReturnDailyError.containsKey(Pair.of(x.getEmployeeId(), x.getDate()))
								&& !errorCheck.getLstErrorEmpMonth().contains(Pair.of(x.getEmployeeId(), x.getDate())))
						.collect(Collectors.toList());
				dailyOlds = dailyOlds.stream()
						.filter(x -> !lstResultReturnDailyError.containsKey(Pair.of(x.getEmployeeId(), x.getDate()))
								&& !errorCheck.getLstErrorEmpMonth().contains(Pair.of(x.getEmployeeId(), x.getDate())))
						.collect(Collectors.toList());

				resultIU.filterDataError(
						lstResultReturnDailyError.keySet().stream().map(x -> x.getLeft() + "|" + x.getRight())
								.collect(Collectors.toSet()),
						errorCheck.getLstErrorEmpMonth().stream().map(x -> x.getLeft() + "|" + x.getRight())
								.collect(Collectors.toSet()));
				if (dailyEdits.isEmpty()) {
					dataResultAfterIU.setErrorMap(
							ProcessCommonCalc.convertErrorToType(lstResultReturnDailyError, resultErrorMonth));
					dataResultAfterIU.setMessageAlert("Msg_1489");
					dataResultAfterIU.setErrorAllSidDate(true);
					dataResultAfterIU.setShowErrorDialog(false);
					return dataResultAfterIU;
				}
			}
		}

		validatorDataDaily.checkVerConfirmApproval(dataParent.getApprovalConfirmCache(), dataParent.getDataCheckSign(),
				dataParent.getDataCheckApproval(), dataParent.getItemValues());

		// 日次登録処理
		dailyItems = dailyItems.stream()
				.filter(x -> !lstResultReturnDailyError.containsKey(Pair.of(x.getEmployeeId(), x.getDate())))
				.collect(Collectors.toList());

		{
			CountDownLatch cdlDaily = new CountDownLatch(1);
			MutableValue<RuntimeException> excepDaily = new MutableValue<>();
			RCDailyCorrectionResult resultIUTemp = resultIU;
			UpdateMonthDailyParam monthParamTemp = monthParam;
			List<DailyRecordDto> dailyEditsTemp = dailyEdits;
			Runnable asyncDaily = () -> {
				try {
					if (dataParent.isCheckDailyChange()) {
						List<DailyItemValue> dailyItemForLog = AttendanceItemUtil
								.toItemValues(dataParent.getDailyOldForLog()).entrySet().stream()
								.map(c -> DailyItemValue.build()
										.createEmpAndDate(c.getKey().employeeId(), c.getKey().workingDate())
										.createItems(c.getValue()))
								.collect(Collectors.toList());

						this.insertAllData.handlerInsertAllDaily(resultIUTemp.getCommandNew(),
								resultIUTemp.getLstDailyDomain(), resultIUTemp.getCommandOld(), dailyItemForLog,
								resultIUTemp.isUpdate(), monthParamTemp, itemAtr);
						// 暫定データを登録する - Register provisional data
						List<DailyModifyResult> resultNews = AttendanceItemUtil.toItemValues(dailyEditsTemp).entrySet()
								.stream()
								.map(dto -> DailyModifyResult.builder().items(dto.getValue())
										.employeeId(dto.getKey().getEmployeeId()).workingDate(dto.getKey().getDate())
										.completed())
								.collect(Collectors.toList());
						// 暫定データを登録する
						dailyRCommandFacade.registerTempData(dataParent.getMode(), resultOlds, resultNews);
					}
				} catch (RuntimeException ex) {
					excepDaily.set(ex);
				} finally {
					cdlDaily.countDown();
				}
			};

			if (Thread.currentThread().getName().indexOf("REQUEST:") == 0) {
				this.executerService.submit(AsyncTask.builder().withContexts().threadName("Daily").build(asyncDaily));
			} else {
				asyncDaily.run();
			}

			// processCalcMonth
			if (dataParent.isCheckDailyChange()) {
				domainDailyNew = resultIU.getLstDailyDomain();
			}

			if (dataParent.getMode() == DisplayFormat.Individual.value) {
				//// 月別実績の集計
				DailyCalcResult resultCalcMonth = processMonthlyCalc.processMonthCalc(commandNew, commandOld,
						domainDailyNew, dailyItems, monthParam, dataParent.getMonthValue(), errorMonthHoliday,
						dataParent.getDateRange(), dataParent.getMode(), editFlex,null);
				RCDailyCorrectionResult resultMonth = resultCalcMonth.getResultUI();
				
				if(resultCalcMonth.getErrorAfterCheck() !=null && resultCalcMonth.getListAggregatePastMonthResult().isEmpty()) {
					ErrorAfterCalcDaily errorMonth = resultCalcMonth.getErrorAfterCheck();
					// map error holiday into result
					List<DPItemValue> lstItemErrorMonth = errorMonth.getResultErrorMonth().get(TypeError.ERROR_MONTH.value);
					if (lstItemErrorMonth != null) {
						List<DPItemValue> itemErrorMonth = dataResultAfterIU.getErrorMap().get(TypeError.ERROR_MONTH.value);
						if (itemErrorMonth == null) {
							// dataResultAfterIU.getErrorMap().put(TypeError.ERROR_MONTH.value,
							// lstItemErrorMonth);
							resultErrorMonth.put(TypeError.ERROR_MONTH.value, lstItemErrorMonth);
						} else {
							lstItemErrorMonth.addAll(itemErrorMonth);
							// dataResultAfterIU.getErrorMap().put(TypeError.ERROR_MONTH.value,
							// lstItemErrorMonth);
							resultErrorMonth.put(TypeError.ERROR_MONTH.value, lstItemErrorMonth);
						}
					}
					// 月次登録処理
					errorMonthAfterCalc = errorMonth.getHasError();
					if (!errorMonthAfterCalc) {
						this.insertAllData.handlerInsertAllMonth(resultMonth.getLstMonthDomain(), monthParam);
						
						dataResultAfterIU.setDomainMonthOpt(resultMonth.getLstMonthDomain().isEmpty() ? Optional.empty()
								: resultMonth.getLstMonthDomain().stream()
										.map(x -> MonthlyRecordWorkDto.fromDtoWithOptional(x, optionalMaster)).findFirst());
						// }
						// dataResultAfterIU.setErrorMap(errorMonth.getResultError());
						dataResultAfterIU.setFlexShortage(errorMonth.getFlexShortage());
					}
				}
				//過去月集計結果を登録する
				if(resultCalcMonth.getErrorAfterCheck() ==null && !resultCalcMonth.getListAggregatePastMonthResult().isEmpty()) {
					registerPastMonthTotalResult.register(resultCalcMonth.getListAggregatePastMonthResult());
				}
			}

			try {
				cdlDaily.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			// 集計処理で例外が起きていたらここでthrow
			if (excepDaily.optional().isPresent()) {
				throw excepDaily.get();
			}

			Set<Pair<String, GeneralDate>> rowAfterCheck = dailyEdits.stream()
					.map(x -> Pair.of(x.getEmployeeId(), x.getDate())).collect(Collectors.toSet());
			Set<Pair<String, GeneralDate>> rowRemoveInsert = rowWillInsert.stream()
					.filter(x -> !rowAfterCheck.contains(x)).collect(Collectors.toSet());
			dataParent.setDataCheckSign(dataParent.getDataCheckSign().stream()
					.filter(x -> !rowRemoveInsert.contains(Pair.of(x.getEmployeeId(), x.getDate())))
					.collect(Collectors.toList()));
			dataParent.setDataCheckApproval(dataParent.getDataCheckApproval().stream()
					.filter(x -> !rowRemoveInsert.contains(Pair.of(x.getEmployeeId(), x.getDate())))
					.collect(Collectors.toList()));
			// insert sign
			// 日の本人確認を登録する
			// TODO: neu ko goi xu ly ngay thi ko co domain loi cho ngay
			dailyRCommandFacade.insertSignD(dataParent.getDataCheckSign(), domainDailyNew, dataParent.getDailyOlds(),
					updated);
			// insert approval
			// 日の承認を登録する
			dailyRCommandFacade.insertApproval(dataParent.getDataCheckApproval(), updated);

			// SPR連携時の確認承認解除
			dataResultAfterIU.setShowErrorDialog(dailyRCommandFacade.showError(domainDailyNew, new ArrayList<>()));

		}

		dataResultAfterIU
				.setErrorMap(ProcessCommonCalc.convertErrorToType(lstResultReturnDailyError, resultErrorMonth));

		// 登録確認メッセージ
		if ((dataResultAfterIU.getErrorMap().isEmpty() && dataResultAfterIU.getErrorMap().values().isEmpty()
				&& !hasErrorRow
				&& (dataResultAfterIU.getFlexShortage() == null || (!dataResultAfterIU.getFlexShortage().isError()
						|| (dataResultAfterIU.getFlexShortage().isError() && !editFlex))))) {
			dataResultAfterIU.setMessageAlert("Msg_15");
		} else {
			Map<Integer, List<DPItemValue>> errorMapTemp = dataResultAfterIU.getErrorMap().entrySet().stream().filter(
					x -> x.getKey() != TypeError.CONTINUOUS.value && x.getKey() != TypeError.RELEASE_CHECKBOX.value)
					.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue(), (x, y) -> x));
			if (errorMapTemp.values().isEmpty()
					&& (dataResultAfterIU.getFlexShortage() == null || (!dataResultAfterIU.getFlexShortage().isError()
							|| (dataResultAfterIU.getFlexShortage().isError() && !editFlex)))) {
				dataResultAfterIU.setMessageAlert("Msg_15");
			} else {
				dataResultAfterIU.setMessageAlert("Msg_1489");
			}
		}

		val empSidUpdate = dailyEdits.stream().map(x -> Pair.of(x.getEmployeeId(), x.getDate()))
				.collect(Collectors.toSet());
		empSidUpdate.addAll(updated);
		dataResultAfterIU.setLstSidDateDomainError(new ArrayList<>(empSidUpdate));
		return dataResultAfterIU;
	}

	public void processDto(List<DailyRecordDto> dailyOlds, List<DailyRecordDto> dailyEdits,
			DPMobileAdUpParam dataParent, List<DailyModifyQuery> querys,
			Map<Pair<String, GeneralDate>, List<DPItemValue>> mapSidDate,
			Set<Pair<String, GeneralDate>> pairSidDateCheck, List<DailyModifyQuery> queryNotChanges) {
		// list cell change by checkbox
		if (!querys.isEmpty()) {
			dailyOlds.addAll(dataParent.getDailyOlds().stream()
					.filter(x -> mapSidDate.containsKey(Pair.of(x.getEmployeeId(), x.getDate()))
							|| pairSidDateCheck.contains(Pair.of(x.getEmployeeId(), x.getDate())))
					.collect(Collectors.toList()));
			List<DailyRecordDto> temp = dataParent.getDailyEdits().stream()
					.filter(x -> mapSidDate.containsKey(Pair.of(x.getEmployeeId(), x.getDate()))
							|| pairSidDateCheck.contains(Pair.of(x.getEmployeeId(), x.getDate())))
					.collect(Collectors.toList());
			dailyEdits.addAll(queryNotChanges.isEmpty() ? temp.stream().map(x -> {
				dailyRCommandFacade.createStampSourceInfo(x, querys);
				return x;
			}).collect(Collectors.toList()) : dailyRCommandFacade.toDto(queryNotChanges, temp, false));
		} else {
			dailyOlds.addAll(dataParent.getDailyOlds());
			dailyEdits.addAll(dataParent.getDailyEdits());
		}
		Map<Integer, OptionalItemAtr> optionalMaster = optionalMasterRepo.findAll(AppContexts.user().companyId())
				.stream().collect(Collectors.toMap(c -> c.getOptionalItemNo().v(), c -> c.getOptionalItemAtr()));

		dailyOlds.stream().forEach(o -> {
			o.getOptionalItem().ifPresent(optional -> {
				optional.correctItemsWith(optionalMaster);
			});
		});

		dailyEdits.stream().forEach(o -> {
			o.getOptionalItem().ifPresent(optional -> {
				optional.correctItemsWith(optionalMaster);
			});
		});
	}
}
