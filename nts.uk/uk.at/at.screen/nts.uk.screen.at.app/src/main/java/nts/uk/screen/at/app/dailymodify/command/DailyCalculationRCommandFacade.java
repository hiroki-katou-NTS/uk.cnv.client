package nts.uk.screen.at.app.dailymodify.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.app.command.dailyperform.DailyRecordWorkCommand;
import nts.uk.ctx.at.record.app.command.dailyperform.checkdata.RCDailyCorrectionResult;
import nts.uk.ctx.at.record.app.command.dailyperform.month.UpdateMonthDailyParam;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.record.dom.daily.itemvalue.DailyItemValue;
import nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem.RegisterPastMonthTotalResult;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.screen.at.app.dailymodify.command.common.DailyCalcParam;
import nts.uk.screen.at.app.dailymodify.command.common.DailyCalcResult;
import nts.uk.screen.at.app.dailymodify.command.common.ProcessCommonCalc;
import nts.uk.screen.at.app.dailymodify.command.common.ProcessDailyCalc;
import nts.uk.screen.at.app.dailymodify.command.common.ProcessMonthlyCalc;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyQuery;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyResult;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.dto.ErrorAfterCalcDaily;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.dto.FlexShortageRCDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemParent;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceCalculationDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceCorrectionDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DataResultAfterIU;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ResultReturnDCUpdateData;
import nts.uk.screen.at.app.dailyperformance.correction.dto.TypeError;
import nts.uk.screen.at.app.dailyperformance.correction.loadupdate.DPLoadRowProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.month.asynctask.ProcessMonthScreen;
import nts.uk.screen.at.app.monthlyperformance.correction.query.MonthlyModifyQuery;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
public class DailyCalculationRCommandFacade {

	@Inject
	private DPLoadRowProcessor dpLoadRowProcessor;

	@Inject
	private DailyModifyRCommandFacade dailyModifyResCommandFacade;

	@Inject
	private ProcessDailyCalc processDailyCalc;

	@Inject
	private ProcessMonthlyCalc processMonthlyCalc;
	
	@Inject
	private OptionalItemRepository optionalMasterRepo;
	
	@Inject
	private ProcessMonthScreen processMonthScreen;
	
	@Inject
	private RegisterPastMonthTotalResult registerPastMonthTotalResult;
	
	public static final int MINUTES_OF_DAY = 24 * 60;

	private static final String FORMAT_HH_MM = "%d:%02d";

	/**
	 * 修正した実績を計算する
	 */
	public DailyPerformanceCalculationDto calculateCorrectedResults(DPItemParent dataParent) {
		// chuan bi data
		DailyPerformanceCorrectionDto dailyCorrectDto = null;
		List<DailyRecordDto> editedDtos = dataParent.getDailyEdits();
		List<DailyRecordDto> editedKeep = editedDtos.stream().map(x -> x.clone()).collect(Collectors.toList());
		Map<Pair<String, GeneralDate>, List<DPItemValue>> mapSidDateEdit = dataParent.getItemValues().stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.getEmployeeId(), x.getDate())));

		List<DailyModifyQuery> querys = createQuerys(mapSidDateEdit);

		List<DailyRecordDto> domainOld = dataParent.getDailyOlds();

		List<DailyModifyResult> resultOlds = AttendanceItemUtil.toItemValues(domainOld).entrySet().stream()
				.map(dto -> DailyModifyResult.builder().items(dto.getValue()).employeeId(dto.getKey().getEmployeeId())
						.workingDate(dto.getKey().getDate()).completed())
				.collect(Collectors.toList());

		List<DailyItemValue> dailyItems = resultOlds.stream().map(
				x -> DailyItemValue.build().createEmpAndDate(x.getEmployeeId(), x.getDate()).createItems(x.getItems()))
				.collect(Collectors.toList());

		dailyModifyResCommandFacade.toDto(querys, editedDtos, true);
		List<IntegrationOfDaily> editedDomains = editedDtos.stream()
				.map(d -> d.toDomain(d.getEmployeeId(), d.getDate())).collect(Collectors.toList());

		String sid = AppContexts.user().employeeId();
		List<DailyRecordWorkCommand> commandNew = ProcessCommonCalc.createCommands(sid, editedDtos, querys);

		List<DailyRecordWorkCommand> commandOld = ProcessCommonCalc.createCommands(sid, editedDtos, querys);

		boolean editFlex = (dataParent.getMode() == 0 && dataParent.getMonthValue() != null
				&& !CollectionUtil.isEmpty(dataParent.getMonthValue().getItems()));

		// check error truoc khi tinh toan
		Map<Pair<String, GeneralDate>, ResultReturnDCUpdateData> resultError = new HashMap<>();
		FlexShortageRCDto flexShortage = null;
		UpdateMonthDailyParam monthParam = null;
		if (dataParent.getMonthValue() != null) {
			val month = dataParent.getMonthValue();
			if (month != null && month.getItems() != null && !month.getItems().isEmpty()) {
				Optional<IntegrationOfMonthly> domainMonthOpt = Optional.empty();
				if(dataParent.getDomainMonthOpt().isPresent()) {
					MonthlyRecordWorkDto monthDto = dataParent.getDomainMonthOpt().get();
					MonthlyModifyQuery monthQuery = new MonthlyModifyQuery(month.getItems().stream().map(x -> {
						return ItemValue.builder().itemId(x.getItemId()).layout(x.getLayoutCode()).value(x.getValue())
								.valueType(ValueType.valueOf(x.getValueType())).withPath("");
					}).collect(Collectors.toList()), month.getYearMonth(), month.getEmployeeId(), month.getClosureId(),
							month.getClosureDate());
					monthDto = AttendanceItemUtil.fromItemValues(monthDto, monthQuery.getItems(), AttendanceItemType.MONTHLY_ITEM);
					IntegrationOfMonthly domainMonth = monthDto.toDomain(monthDto.getEmployeeId(),
							monthDto.getYearMonth(), monthDto.getClosureID(), monthDto.getClosureDate());
					domainMonth.getAffiliationInfo().ifPresent(d -> {
						d.setVersion(dataParent.getMonthValue().getVersion());
					});
					domainMonth.getAttendanceTime().ifPresent(d -> {
						d.setVersion(dataParent.getMonthValue().getVersion());
					}); 
					domainMonthOpt = Optional.of(domainMonth);
				}
				monthParam = new UpdateMonthDailyParam(month.getYearMonth(), month.getEmployeeId(),
						month.getClosureId(), month.getClosureDate(), domainMonthOpt,
						new DatePeriod(dataParent.getDateRange().getStartDate(),
								dataParent.getDateRange().getEndDate()),
						month.getRedConditionMessage(), month.getHasFlex(), month.getNeedCallCalc(),
						dataParent.getMonthValue().getVersion());
			} else {
				monthParam = new UpdateMonthDailyParam(month.getYearMonth(), month.getEmployeeId(),
						month.getClosureId(), month.getClosureDate(), Optional.empty(),
						new DatePeriod(dataParent.getDateRange().getStartDate(),
								dataParent.getDateRange().getEndDate()),
						month.getRedConditionMessage(), month.getHasFlex(), month.getNeedCallCalc(),
						dataParent.getMonthValue().getVersion());
			}
		}

		Map<Pair<String, GeneralDate>, List<DPItemValue>> mapSidDate = dataParent.getItemValues().stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.getEmployeeId(), x.getDate())));

		val mapDtoOld = editedDtos.stream()
				.collect(Collectors.toMap(x -> Pair.of(x.getEmployeeId(), x.getDate()), x -> x));
		RCDailyCorrectionResult resultUI = null;
		List<EmployeeMonthlyPerError> errorMonthHoliday = new ArrayList<>();
		Map<Integer, List<DPItemValue>> resultErrorMonth = new HashMap<>();
		// 日別実績が修正されているかチェック
		if (dataParent.isCheckDailyChange()) {
			ExecutionType executionType = dataParent.isFlagCalculation()? ExecutionType.RERUN:ExecutionType.NORMAL_EXECUTION;
			// 日別実績の計算
			DailyCalcResult dailyCalcResult = processDailyCalc.processDailyCalc(
					new DailyCalcParam(mapSidDate, dataParent.getLstNotFoundWorkType(), resultOlds,
							dataParent.getDateRange(), dataParent.getDailyEdits(), dataParent.getItemValues()),
					editedDtos, domainOld, dailyItems, querys, monthParam, dataParent.getShowDialogError(),
					executionType,dataParent.getCheckUnLock());
			if (dailyCalcResult.getResultUI() == null) {
				return new DailyPerformanceCalculationDto(editedKeep, new ArrayList<>(),
						new DataResultAfterIU(dailyCalcResult.getDataResultAfterIU().getErrorMap(), flexShortage, false,
								"Msg_1492"),
						Collections.emptyList(), Collections.emptyList(), true, dailyCorrectDto, false);
			}

			resultUI = dailyCalcResult.getResultUI();
			editedDomains = resultUI.getLstDailyDomain();
//			editedDomains.stream().forEach(d -> {
//				editedDtos.stream()
//						.filter(c -> c.employeeId().equals(d.getEmployeeId())
//								&& c.workingDate().equals(d.getYmd()))
//						.findFirst().ifPresent(dto -> {
//							d.getWorkInformation().setVersion(dto.getWorkInfo().getVersion());
//						});
//			});

			// check error sau khi tinh toan
			// ErrorAfterCalcDaily
			ErrorAfterCalcDaily afterError = dailyCalcResult.getErrorAfterCheck();
			errorMonthHoliday.addAll(afterError.getErrorMonth());
			Map<Pair<String, GeneralDate>, ResultReturnDCUpdateData> resultErrorTemp = afterError.getResultError();
			resultError.putAll(resultErrorTemp);
			resultError.putAll(dailyCalcResult.getLstResultDaiRowError());
			resultErrorMonth = afterError.getResultErrorMonth();
			val lstErrorCheckDetail = afterError.getLstErrorEmpMonth();
			flexShortage = afterError.getFlexShortage();

			editedDomains = editedDomains.stream()
					.filter(x -> !resultError.containsKey(
							Pair.of(x.getEmployeeId(), x.getYmd()))
							&& !lstErrorCheckDetail.contains(
									Pair.of(x.getEmployeeId(), x.getYmd())))
					.collect(Collectors.toList());
			domainOld = domainOld.stream()
					.filter(x -> !resultError.containsKey(Pair.of(x.getEmployeeId(), x.getDate()))
							&& !lstErrorCheckDetail.contains(Pair.of(x.getEmployeeId(), x.getDate())))
					.collect(Collectors.toList());

			if (editedDomains.isEmpty()) {
				return new DailyPerformanceCalculationDto(editedKeep, new ArrayList<>(),
						new DataResultAfterIU(ProcessCommonCalc.convertErrorToType(resultError, resultErrorMonth),
								flexShortage, false, "Msg_1492"),
						Collections.emptyList(), Collections.emptyList(), true, null, false);
			}

		}
		List<IntegrationOfMonthly> monthlyResults = new ArrayList<>();
		if (dataParent.isCheckDailyChange()) {
			editedDomains = resultUI.getLstDailyDomain();
		}
		// check format display = individual
		if (dataParent.getMode() == 0 && monthParam != null && monthParam.getNeedCallCalc() != null
				&& monthParam.getNeedCallCalc()) {
			
			// kiem tra co can tinh toan monthly khong, neu can thi tinh lai
			// 月次集計を実施する必要があるかチェックする
			// 月別実績の集計
			DailyCalcResult resultCalcMonth = processMonthlyCalc.processMonthCalc(commandNew, commandOld, editedDomains,
					dailyItems, monthParam, dataParent.getMonthValue(), errorMonthHoliday, dataParent.getDateRange(),
					dataParent.getMode(), editFlex,dataParent.getCheckUnLock());

			RCDailyCorrectionResult resultMonth = resultCalcMonth.getResultUI();
			monthlyResults.addAll(resultMonth.getLstMonthDomain());
			if(resultCalcMonth.getErrorAfterCheck() !=null  && resultCalcMonth.getListAggregatePastMonthResult().isEmpty()) {
				ErrorAfterCalcDaily errorMonth = resultCalcMonth.getErrorAfterCheck();
				// map error holiday into result
				List<DPItemValue> lstItemErrorMonth = errorMonth.getResultErrorMonth().get(TypeError.ERROR_MONTH.value);
				if (lstItemErrorMonth != null) {
					List<DPItemValue> itemErrorMonth = resultErrorMonth.get(TypeError.ERROR_MONTH.value);
					if (itemErrorMonth == null) {
						resultErrorMonth.put(TypeError.ERROR_MONTH.value, lstItemErrorMonth);
					} else {
						lstItemErrorMonth.addAll(itemErrorMonth);
						resultErrorMonth.put(TypeError.ERROR_MONTH.value, lstItemErrorMonth);
					}
				}
				flexShortage = errorMonth.getFlexShortage();
			}
			
			//過去月集計結果を登録する
			if(resultCalcMonth.getErrorAfterCheck() ==null && !resultCalcMonth.getListAggregatePastMonthResult().isEmpty()) {
				registerPastMonthTotalResult.register(resultCalcMonth.getListAggregatePastMonthResult());
			}
		}
		

		// if (!editedDomains.isEmpty()) {
		// update lai daily results gui ve client
		Map<Integer, OptionalItem> optionalItem = optionalMasterRepo.findAll(AppContexts.user().companyId())
				.stream().collect(Collectors.toMap(c -> c.getOptionalItemNo().v(), c -> c));
		List<DailyRecordDto> calculatedDtos = editedDomains.stream().map(d -> DailyRecordDto.from(d, optionalItem))
				.collect(Collectors.toList());
		// set state calc
		val mapDtoEdits = calculatedDtos.stream()
				.collect(Collectors.toMap(x -> Pair.of(x.getEmployeeId(), x.getDate()), x -> x));
		val resultCompare = dpLoadRowProcessor.itemCalcScreen(mapDtoEdits, mapDtoOld, dataParent.getLstData(),
				dataParent.getLstAttendanceItem(), dataParent.getCellEdits());
		List<DailyModifyResult> resultValues = resultCompare.getRight().stream().map(x -> {
			x.items(x.getItems().stream().map(item -> {
				return (item.getValueType() == ValueType.TIME || item.getValueType() == ValueType.CLOCK
						|| item.getValueType() == ValueType.TIME_WITH_DAY) ? new ItemValue(
								item.getValue() == null ? "" : converTime(item.getValueType().value, item.getValue()),
								item.getValueType(), item.getLayoutCode(), item.getItemId(), item.getPathLink()) : item;
			}).collect(Collectors.toList()));
			return x;
		}).collect(Collectors.toList());

		String messageAlert = "";
		if (resultError.isEmpty()
				&& !resultErrorMonth.values().stream().flatMap(x -> x.stream()).findFirst().isPresent()) {
			messageAlert = "Msg_1491";
		} else {
			messageAlert = "Msg_1492";
		}

		val empSidUpdate = calculatedDtos.stream().map(x -> Pair.of(x.getEmployeeId(), x.getDate()))
				.collect(Collectors.toList());
		val dtoEditError = editedKeep.stream()
				.filter(x -> !empSidUpdate.contains(Pair.of(x.getEmployeeId(), x.getDate())))
				.collect(Collectors.toList());
		calculatedDtos.addAll(dtoEditError);
		
		if (!monthlyResults.isEmpty()) {
			// gen month data
			Optional<MonthlyRecordWorkDto> monthOpt = monthlyResults.stream().map(x -> MonthlyRecordWorkDto.fromDtoWithOptional(x, optionalItem)).findFirst();
			dataParent.getParamCommonAsync().setLoadAfterCalc(true);
			dataParent.getParamCommonAsync().setDomainMonthOpt(monthOpt);
			
			dailyCorrectDto = processMonthScreen.processMonth(dataParent.getParamCommonAsync());
		}
		
		DailyPerformanceCalculationDto returnData = new DailyPerformanceCalculationDto(calculatedDtos, resultValues,
				new DataResultAfterIU(ProcessCommonCalc.convertErrorToType(resultError, resultErrorMonth), flexShortage,
						false, messageAlert),
				resultCompare.getLeft(), empSidUpdate, false, dailyCorrectDto, true);
		return returnData;

	}

	private List<DailyModifyQuery> createQuerys(Map<Pair<String, GeneralDate>, List<DPItemValue>> mapSidDate) {
		List<DailyModifyQuery> querys = new ArrayList<>();
		mapSidDate.entrySet().forEach(x -> {
			List<ItemValue> itemCovert = x.getValue().stream()
					.map(y -> new ItemValue(y.getValue(),y.getValueType() == null?null: ValueType.valueOf(y.getValueType()), y.getLayoutCode(),////fixbug 121378, truyền auto null dc(tín bảo thế :)) )
							y.getItemId()))
					.collect(Collectors.toList()).stream().filter(distinctByKey(p -> p.itemId()))
					.collect(Collectors.toList());
			if (!itemCovert.isEmpty())
				querys.add(new DailyModifyQuery(x.getKey().getKey(), x.getKey().getValue(), itemCovert));
		});
		return querys;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		final Set<Object> seen = new HashSet<>();
		return t -> seen.add(keyExtractor.apply(t));
	}

	private String converTime(int valueType, String value) {
		int minute = 0;
		if (Integer.parseInt(value) >= 0) {
			minute = Integer.parseInt(value);
		} else {
			if (valueType == ValueType.TIME_WITH_DAY.value) {
				minute = 0 - ((Integer.parseInt(value)
						+ (1 + -Integer.parseInt(value) / MINUTES_OF_DAY) * MINUTES_OF_DAY));
			} else {
				minute = Integer.parseInt(value);
			}
		}
		int hours = minute / 60;
		int minutes = Math.abs(minute) % 60;
		String valueConvert = (minute < 0 && hours == 0) ? "-" + String.format(FORMAT_HH_MM, hours, minutes)
				: String.format(FORMAT_HH_MM, hours, minutes);
		return valueConvert;
	}

}
