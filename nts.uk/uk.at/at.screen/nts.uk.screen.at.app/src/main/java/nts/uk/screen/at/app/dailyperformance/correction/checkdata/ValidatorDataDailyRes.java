package nts.uk.screen.at.app.dailyperformance.correction.checkdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.app.command.dailyperform.month.UpdateMonthDailyParam;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ApprovalStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ConfirmStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.approval.ApprovalStatusActualDayChange;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.ConfirmStatusActualDayChange;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.service.ContinuousHolidayCheckResult;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.service.ErAlWorkRecordCheckService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.AnnualLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.ErrorType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.ReserveLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.flexshortage.InsufficientFlexHolidayMnt;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.flexshortage.InsufficientFlexHolidayMntRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyResult;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.dto.FlexShortageRCDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalConfirmCache;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemCheckBox;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ResultReturnDCUpdateData;
import nts.uk.screen.at.app.dailyperformance.correction.dto.TypeError;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ValidatorDataDailyRes {

	@Inject
	private ErAlWorkRecordCheckService erAlWorkRecordCheckService;

	@Inject
	private BasicScheduleService basicScheduleService;

	@Inject
	private InsufficientFlexHolidayMntRepository flexHolidayMntRepository;
	
	@Inject
	private SpecialHolidayRepository specialHolidayRepository;
	
	@Inject
	private EmployeeDailyPerErrorRepository employeeErrorRepo;
	
	@Inject
	private ErrorAlarmWorkRecordRepository errorAlarmWRRepo;
	
	@Inject
	private ApprovalStatusActualDayChange approvalStatusActualDayChange;
	
	@Inject
	private ConfirmStatusActualDayChange confirmStatusActualDayChange;

	private static final Integer[] CHILD_CARE = { 759, 760, 761, 762, 580 };
	private static final Integer[] CARE = { 763, 764, 765, 766, 586 };
	private static final Integer[] INPUT_CHECK = { 759, 760, 761, 762, 763, 764, 765, 766, 157, 159, 163, 165, 171, 169,
			177, 175, 183, 181, 189, 187, 195, 193, 199, 201, 205, 207, 211, 213, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
			17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };

	public static final Map<Integer, Integer> INPUT_CHECK_MAP = IntStream.range(0, INPUT_CHECK.length).boxed()
			.collect(Collectors.toMap(x -> INPUT_CHECK[x], x -> x % 2 == 0 ? INPUT_CHECK[x + 1] : INPUT_CHECK[x - 1]));

	private static final Integer[] DEVIATION_REASON = { 436, 438, 439, 441, 443, 444, 446, 448, 449, 451, 453, 454, 456,
			458, 459, 799, 801, 802, 804, 806, 807, 809, 811, 812, 814, 816, 817, 819, 821, 822 };
	static final Map<Integer, Integer> DEVIATION_REASON_MAP = IntStream.range(0, DEVIATION_REASON.length).boxed()
			.collect(Collectors.toMap(x -> DEVIATION_REASON[x], x -> x / 3 + 1));
	
	private final static String EMPTY_STRING = "";

	private final static String PATTERN_1 = "[0-9]+$";
	// Arrays.stream(INPUT_CHECK).

	// 育児と介護の時刻が両方入力されていないかチェックする
	public List<DPItemValue> checkCareItemDuplicate(List<DPItemValue> items, List<DailyModifyResult> itemValueMapOlds) {
		List<DPItemValue> childCares = hasChildCare(items);
		List<DPItemValue> cares = hasCare(items);
		if(!childCares.isEmpty() && !cares.isEmpty()) {
			childCares.addAll(cares);
			return childCares;
		}
		
		if((!childCares.isEmpty() || !cares.isEmpty()) && !itemValueMapOlds.isEmpty()) {
			DPItemValue dpItem = childCares.isEmpty() ? cares.get(0) : childCares.get(0);
			List<DPItemValue> itemRowMapDB = itemValueMapOlds.get(0).getItems().stream()
					.map(x -> new DPItemValue(dpItem.getRowId(), dpItem.getEmployeeId(), dpItem.getDate(), x.getItemId(), x.getValue(), ""))
					.collect(Collectors.toList());
			List<DPItemValue> childCareDBs = hasChildCare(itemRowMapDB);
			List<DPItemValue> careDBs = hasCare(itemRowMapDB);
			
			if((!childCares.isEmpty() && !careDBs.isEmpty()) || (!childCareDBs.isEmpty() && !cares.isEmpty())) {
				childCares.addAll(cares);
				return childCares;
			}else {
				return Collections.emptyList();
			}
		}else {
			return Collections.emptyList();
		}
	}

	private List<DPItemValue> hasChildCare(List<DPItemValue> items) {
		List<DPItemValue> itemChild = items.stream()
				.filter(x -> {
					boolean check = false;
					
					if ((x.getValue() != null) && (x.getItemId() == CHILD_CARE[0] || x.getItemId() == CHILD_CARE[1]
							|| x.getItemId() == CHILD_CARE[2] || x.getItemId() == CHILD_CARE[3] || x.getItemId() == CHILD_CARE[4]))
						check = true;
					
					if (check == true) {
						if (x.getValue().equals("0") && x.getItemId() == CHILD_CARE[4]) check = false;
					}
					
					return check;
				})
				.collect(Collectors.toList());
		return itemChild.isEmpty() ? new ArrayList<>() : itemChild;
	}

	private List<DPItemValue> hasCare(List<DPItemValue> items) {
		List<DPItemValue> itemCare = items.stream().filter(x -> {
			boolean check = false;
			if (x.getValue() != null && (x.getItemId() == CARE[0]
					|| x.getItemId() == CARE[1] || x.getItemId() == CARE[2] || x.getItemId() == CARE[3] || x.getItemId() == CARE[4]))
				check = true;
			
			if (check == true) {
				if (x.getValue().equals("0") && x.getItemId() == CARE[4]) check = false;
			}
			
			return check;
				
		})
				.collect(Collectors.toList());
		return itemCare.isEmpty() ? new ArrayList<>() : itemCare;
	}

	public List<DPItemValue> checkCareInputData(List<DPItemValue> items) {
		List<DPItemValue> childCares = hasChildCare(items);
		List<DPItemValue> cares = hasCare(items);
		List<DPItemValue> result = new ArrayList<>();
		if (!childCares.isEmpty()) {
			Map<Integer, DPItemValue> childMap = childCares.stream()
					.collect(Collectors.toMap(DPItemValue::getItemId, x -> x));
			boolean childCare759 = childMap.containsKey(CHILD_CARE[0]);
			boolean childCare760 = childMap.containsKey(CHILD_CARE[1]);
			boolean childCare761 = childMap.containsKey(CHILD_CARE[2]);
			boolean childCare762 = childMap.containsKey(CHILD_CARE[3]);
			boolean childCare580 = childMap.containsKey(CHILD_CARE[4]);
			if (!(childCare759 && childCare760)) {
				if (childCare759) {
					result.add(childMap.get(CHILD_CARE[0]));
				} else if (childCare760) {
					result.add(childMap.get(CHILD_CARE[1]));
				}
			}
			if (!(childCare761 && childCare762)) {
				if (childCare761) {
					result.add(childMap.get(CHILD_CARE[2]));
				} else if (childCare762) {
					result.add(childMap.get(CHILD_CARE[3]));
				}
			}
			
			if(!childCare580) result.add(childMap.get(CHILD_CARE[4]));
			return result;
		} else if (!cares.isEmpty()) {
			Map<Integer, DPItemValue> caresMap = cares.stream()
					.collect(Collectors.toMap(DPItemValue::getItemId, x -> x));
			boolean care763 = caresMap.containsKey(CARE[0]);
			boolean care764 = caresMap.containsKey(CARE[1]);
			boolean care765 = caresMap.containsKey(CARE[2]);
			boolean care766 = caresMap.containsKey(CARE[3]);
			boolean care586 = caresMap.containsKey(CARE[4]);
			if (!(care763 && care764)) {
				if (care763) {
					result.add(caresMap.get(CARE[0]));
				} else if (care764) {
					result.add(caresMap.get(CARE[1]));
				}
			}
			if (!(care765 && care766)) {
				if (care765) {
					result.add(caresMap.get(CARE[2]));
				} else if (care766) {
					result.add(caresMap.get(CARE[3]));
				}
			}
			
			if(!care586) result.add(caresMap.get(CARE[4]));
			return result;
		}
		return Collections.emptyList();
	}

	// check trong 1 ngay nhom item yeu cau nhap theo cap
	public List<DPItemValue> checkInputData(List<DPItemValue> items, List<DailyModifyResult> itemValues) {
		List<DPItemValue> result = new ArrayList<>();
		// loc chua item can check
		List<DPItemValue> itemCanCheck = items.stream().filter(x -> INPUT_CHECK_MAP.containsKey(x.getItemId()))
				.collect(Collectors.toList());
		if (itemCanCheck.isEmpty())
			return result;
		Map<Integer, String> itemCheckMap = itemCanCheck.stream()
				.collect(Collectors.toMap(x -> x.getItemId(), x -> x.getValue() == null ? "" : x.getValue()));
		List<DPItemValue> itemCheckDBs = new ArrayList<>();
		// loc nhung thang chi duoc insert 1 trong 1 cap
		itemCanCheck.forEach(x -> {
			Integer itemCheck = INPUT_CHECK_MAP.get(x.getItemId());
			if (!itemCheckMap.containsKey(itemCheck)) {
				itemCheckDBs.add(x);
			}else {
				if(itemCheck != null) {
					 Integer itemId1 = x.getItemId();
					 Integer itemId2 = INPUT_CHECK_MAP.get(itemId1);
					 String valueItemIdStart = (itemId1 < itemId2) ? x.getValue()
							: itemCheckMap.get(itemId2);
					 String valueItemIdEnd = (itemId1 > itemId2) ? x.getValue()
								: itemCheckMap.get(itemId2);
					if (valueItemIdStart!= null && valueItemIdEnd != null && Integer.parseInt(valueItemIdStart) > Integer.parseInt(valueItemIdEnd)) {
						x.setMessage("Msg_1400");
						result.add(x);
					}
				}
			}
		});
		if (itemCheckDBs.isEmpty())
			return result;

		if (itemValues.isEmpty())
			return result;
		Map<Integer, String> valueGetFromDBMap = itemValues.get(0).getItems().stream()
				.collect(Collectors.toMap(x -> x.getItemId(), x -> x.getValue() == null ? "" : x.getValue()));
		itemCheckDBs.stream().forEach(x -> {
			if (valueGetFromDBMap.containsKey(INPUT_CHECK_MAP.get(x.getItemId()))
					&& valueGetFromDBMap.get(INPUT_CHECK_MAP.get(x.getItemId())).equals("")
			|| ((x.getValue() == null || x.getValue().equals("")) && !valueGetFromDBMap.get(INPUT_CHECK_MAP.get(x.getItemId())).equals(""))) {
				x.setMessage("Msg_1108");
				result.add(x);
			}else {
				Integer itemId = INPUT_CHECK_MAP.get(x.getItemId()); 
				if(itemId != null) {
					 Integer itemId1 = x.getItemId();
					 Integer itemId2 = INPUT_CHECK_MAP.get(itemId1);
					 String valueItemIdStart = (itemId1 < itemId2) ? x.getValue()
							: itemCheckMap.containsKey(itemId2) ? itemCheckMap.get(itemId2)
									: valueGetFromDBMap.get(itemId2);
					 String valueItemIdEnd = (itemId1 > itemId2) ? x.getValue()
								: itemCheckMap.containsKey(itemId2) ? itemCheckMap.get(itemId2)
										: valueGetFromDBMap.get(itemId2);
					if (valueItemIdStart!= null && valueItemIdEnd != null && Integer.parseInt(valueItemIdStart) > Integer.parseInt(valueItemIdEnd)) {
						x.setMessage("Msg_1400");
						result.add(x);
					}
				}
			}
		});
		return result;
	}

	public List<DPItemValue> checkContinuousHolidays(String employeeId, DateRange date) {
		
		return checkContinuousHolidays(employeeId, date, new ArrayList<>());
	}
	
	//大塚カスタマイズチェック処理
	public List<DPItemValue> checkContinuousHolidays(String employeeId, DateRange date, List<WorkInfoOfDailyPerformance> workInfos) {
		List<DPItemValue> r = new ArrayList<>();
		
		employeeErrorRepo.removeContinuosErrorIn(employeeId, new DatePeriod(date.getStartDate(), date.getEndDate()), ErAlWorkRecordCheckService.CONTINUOUS_CHECK_CODE);
		
		ContinuousHolidayCheckResult result = erAlWorkRecordCheckService.checkContinuousHolidays(employeeId,
				new DatePeriod(date.getStartDate(), date.getEndDate()), workInfos);
		if (result == null)
			return r;
		
		Map<GeneralDate, Integer> resultMap = result.getCheckResult();
		
		if (!resultMap.isEmpty()) {
			
			String compID = AppContexts.user().companyId();
			
			resultMap.entrySet().stream().forEach(x -> {
				employeeErrorRepo.insert(new EmployeeDailyPerError(compID, employeeId, x.getKey(), 
						ErAlWorkRecordCheckService.CONTINUOUS_CHECK_CODE, Arrays.asList(28), 0, result.message()));
				r.add(new DPItemValue("勤務種類", employeeId, x.getKey(), 0, String.valueOf(x.getValue()), result.message()));
			});
		} 

		return r;
	}

	// 乖離理由が選択、入力されているかチェックする
	public void checkReasonInput(List<DPItemValue> items) {
		// String companyId = AppContexts.user().companyId();
		// List<DivergenceTime> divergenceTime =
		// divergenceTimeRepository.getDivTimeListByUseSet(companyId);
		// items.stream().forEach(x -> {
		// JudgmentResult judgmentResult =
		// divergenceReasonInputMethodService.determineLeakageReason(x.getEmployeeId(),
		// x.getDate(), divergenceTimeNo, divergenceReasonCode,
		// divergenceReasonContent, justmentResult)
		// });
	}

	public List<DPItemValue> checkInput28And1(List<DPItemValue> itemChanges, List<DailyModifyResult> allItemValues) {
		List<DPItemValue> result = new ArrayList<>();
		result = checkInputItem28(itemChanges, allItemValues);
		result.addAll(checkInputItem1(itemChanges, allItemValues));
		return result;
	}

	public List<DPItemValue> checkInputItem28(List<DPItemValue> itemChanges, List<DailyModifyResult> itemValueAlls) {
		List<DPItemValue> result = new ArrayList<>();
		DPItemValue valueTemp;
		Optional<DPItemValue> item28 = itemChanges.stream().filter(x -> x.getItemId() == 28).findFirst();
		Optional<DPItemValue> item29 = itemChanges.stream().filter(x -> x.getItemId() == 29).findFirst();
		if (!item28.isPresent() && !item29.isPresent()) {
			return result;
		}

		String workTypeCode = "";
		if (item28.isPresent()) {
			workTypeCode = item28.get().getValue();
			if (workTypeCode == null || workTypeCode.equals("")) {
				valueTemp = item28.get();
				valueTemp.setLayoutCode(TextResource.localize("Msg_1329"));
				valueTemp.setMessage("Msg_1329");
				result.add(valueTemp);
				return result;
			}
		} else {
			List<ItemValue> itemValues = itemValueAlls.get(0).getItems().stream().filter(x -> x.getItemId() == 28)
					.collect(Collectors.toList());
			if (!itemValueAlls.isEmpty() && !itemValues.isEmpty())
				workTypeCode = itemValues.get(0).getValue();
		}

		if (workTypeCode.equals(""))
			return result;

		SetupType setupType = basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
		if (setupType.value == SetupType.NOT_REQUIRED.value || setupType.value == SetupType.OPTIONAL.value) {
			return result;
		}

		if (item29.isPresent()) {
			if (item29.get().getValue() == null || item29.get().getValue().equals("")) {
				valueTemp = item29.get();
				valueTemp.setLayoutCode(TextResource.localize("Msg_1270"));
				valueTemp.setMessage("Msg_1270");
				result.add(valueTemp);
				return result;
			}
			return result;
		}

		// check DB item 29
		List<ItemValue> itemValues = itemValueAlls.get(0).getItems().stream().filter(x -> x.getItemId() == 29)
				.collect(Collectors.toList());
		if (itemValueAlls.isEmpty() || itemValues.isEmpty())
			return result;
		ItemValue value = itemValues.get(0);
		if (value.getValue() == null || value.getValue().equals("")) {
			valueTemp = item28.get();
			valueTemp.setLayoutCode(TextResource.localize("Msg_1270"));
			valueTemp.setMessage("Msg_1270");
			result.add(valueTemp);
			return result;
		}
		return result;
	}

	public List<DPItemValue> checkInputItem1(List<DPItemValue> items, List<DailyModifyResult> itemValueAlls) {
		List<DPItemValue> result = new ArrayList<>();
		DPItemValue valueTemp;
		Optional<DPItemValue> item1 = items.stream().filter(x -> x.getItemId() == 1).findFirst();
		Optional<DPItemValue> item2 = items.stream().filter(x -> x.getItemId() == 2).findFirst();
		if (!item1.isPresent() && !item2.isPresent()) {
			return result;
		}

		String workTypeCode = "";
		if (item1.isPresent()) {
			workTypeCode = item1.get().getValue();
			if (workTypeCode == null || workTypeCode.equals("")) {
				valueTemp = item1.get();
				valueTemp.setLayoutCode(TextResource.localize("Msg_1328"));
				valueTemp.setMessage("Msg_1328");
				result.add(valueTemp);
				return result;
			}
		} else {
			List<ItemValue> itemValues = itemValueAlls.get(0).getItems().stream().filter(x -> x.getItemId() == 1)
					.collect(Collectors.toList());
			if (!itemValueAlls.isEmpty() && !itemValues.isEmpty())
				workTypeCode = itemValues.get(0).getValue();
		}

		if (workTypeCode.equals(""))
			return result;

		SetupType setupType = basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
		if (setupType.value == SetupType.NOT_REQUIRED.value || setupType.value == SetupType.OPTIONAL.value) {
			return result;
		}

		if (item2.isPresent()) {
			if (item2.get().getValue() == null || item2.get().getValue().equals("")) {
				valueTemp = item2.get();
				valueTemp.setLayoutCode(TextResource.localize("Msg_1308"));
				valueTemp.setMessage("Msg_1308");
				result.add(valueTemp);
				return result;
			}
			return result;
		}

		// check DB item 2
		List<ItemValue> itemValues = itemValueAlls.get(0).getItems().stream().filter(x -> x.getItemId() == 1)
				.collect(Collectors.toList());
		if (itemValueAlls.isEmpty() || itemValues.isEmpty())
			return result;
		ItemValue value = itemValues.get(0);
		if (value.getValue() == null || value.getValue().equals("")) {
			valueTemp = item1.get();
			valueTemp.setLayoutCode(TextResource.localize("Msg_1308"));
			valueTemp.setMessage("Msg_1308");
			result.add(valueTemp);
			return result;
		}
		return result;
	}

	/**
	 * 計算後エラーチェック
	 */
	public Map<Pair<String, GeneralDate>, ResultReturnDCUpdateData> errorCheckDivergence(List<IntegrationOfDaily> dailyResults,
			List<IntegrationOfMonthly> monthlyResults) {
		Map<Pair<String, GeneralDate>, ResultReturnDCUpdateData> resultError = new HashMap<>();

		// 乖離エラーのチェック
		for (IntegrationOfDaily d : dailyResults) {
			List<DPItemValue> divergenceErrors = new ArrayList<>();
			List<EmployeeDailyPerError> employeeError = d.getEmployeeError();
			for (EmployeeDailyPerError err : employeeError) {
				if (err != null && err.getErrorAlarmWorkRecordCode().v().startsWith("D")
						&& err.getErrorAlarmMessage().isPresent()
						&& err.getErrorAlarmMessage().get().v().equals(TextResource.localize("Msg_1298")) && checkErrorD1(err.getErrorAlarmWorkRecordCode().v())) {
					if (err.getAttendanceItemList().isEmpty()) {
						divergenceErrors.add(new DPItemValue("", err.getEmployeeID(), err.getDate(), 0));
					} else {
						divergenceErrors.addAll(err.getAttendanceItemList().stream()
								.map(itemId -> new DPItemValue("", err.getEmployeeID(), err.getDate(), itemId))
								.collect(Collectors.toList()));
					}
				}
			}
			if (!divergenceErrors.isEmpty()) {
				 Map<Integer, List<DPItemValue>> temMap = new HashMap<>();
				temMap.put(TypeError.DEVIATION_REASON.value, divergenceErrors);
				resultError.put(Pair.of(d.getEmployeeId(), d.getYmd()), new ResultReturnDCUpdateData(d.getEmployeeId(), d.getYmd(), temMap));
			}
		}
		return resultError;
	}

	/**
	 * フレックス繰越時間が正しい範囲で入力されているかチェックする
	 */
	public FlexShortageRCDto errorCheckFlex(List<IntegrationOfMonthly> lstMonthDomain,
			UpdateMonthDailyParam monthParam) {
		Optional<EmployeeMonthlyPerError> monthDomainOpt = getDataErrorMonth(lstMonthDomain, monthParam);
		FlexShortageRCDto result = new FlexShortageRCDto();
		if (!monthDomainOpt.isPresent())
			return result.createError(false);
		Optional<InsufficientFlexHolidayMnt> flexHoliday = flexHolidayMntRepository
				.findByCId(AppContexts.user().companyId());
		result.createMessage(monthParam.getMessageRed(),
				flexHoliday.isPresent() ? String.valueOf(flexHoliday.get().getSupplementableDays().v()) : "");
		return result.createError(monthDomainOpt);
	}

	private Optional<EmployeeMonthlyPerError> getDataErrorMonth(List<IntegrationOfMonthly> lstDomain,
			UpdateMonthDailyParam monthParam) {
		for (IntegrationOfMonthly month : lstDomain) {
			List<EmployeeMonthlyPerError> results = month.getEmployeeMonthlyPerError().stream()
					.filter(x -> x.getErrorType().value == ErrorType.FLEX.value
							&& x.getClosureId().value == monthParam.getClosureId()
							&& x.getEmployeeID().equals(monthParam.getEmployeeId())
							&& x.getClosureDate().getClosureDay().v().intValue() == monthParam.getClosureDate()
									.getClosureDay().intValue()
							&& x.getClosureDate().getLastDayOfMonth() == monthParam.getClosureDate().getLastDayOfMonth()
									.booleanValue()).collect(Collectors.toList());
			if (!results.isEmpty())
				return results.stream().findFirst();
		}
		return Optional.empty();
	}

	// 残数系のエラーチェック
	public Map<Integer, List<DPItemValue>> errorMonth(List<IntegrationOfMonthly> lstMonthDomain,
			UpdateMonthDailyParam monthParam) {
		Map<Integer, List<DPItemValue>> resultError = new HashMap<>();
		String companyId = AppContexts.user().companyId();
		List<DPItemValue> items = new ArrayList<>();
		for (IntegrationOfMonthly month : lstMonthDomain) {
			val lstEmpError = month.getEmployeeMonthlyPerError().stream()
					.filter(x -> x.getErrorType().value != ErrorType.FLEX.value && x.getErrorType().value != ErrorType.FLEX_SUPP.value).collect(Collectors.toList());
			val listNo = lstEmpError.stream().filter(x -> x.getErrorType().value == ErrorType.SPECIAL_REMAIN_HOLIDAY_NUMBER.value).map(x -> x.getNo()).collect(Collectors.toList());
			
			Map<Integer, SpecialHoliday> sHolidayMap = listNo.isEmpty() ? new HashMap<>() : specialHolidayRepository.findSimpleByCompanyIdNoMaster(companyId, listNo)
					.stream().filter(x -> x.getSpecialHolidayCode() != null)
					.collect(Collectors.toMap(x -> x.getSpecialHolidayCode().v(), x -> x));
			
			lstEmpError.stream().forEach(error -> {
				createMessageError(error).stream().forEach(message -> {
					if(message.equals("Msg_1414")){
						val sh = sHolidayMap.get(error.getNo());
						message =  TextResource.localize(message, sh == null ? "" : sh.getSpecialHolidayName().v());
					}else{
						message = TextResource.localize(message);
					}
					items.add(new DPItemValue(error.getEmployeeID(), message));
				});
			});

		}
		if (!items.isEmpty()) {
			resultError.put(TypeError.ERROR_MONTH.value, items);
			return resultError;
		}
		return resultError;
	}
	
	// check error month(残数系のエラーチェック) update
	public Map<Integer, List<DPItemValue>> errorMonthNew(List<EmployeeMonthlyPerError> monthErrors) {
		Map<Integer, List<DPItemValue>> resultError = new HashMap<>();
		String companyId = AppContexts.user().companyId();
		List<DPItemValue> items = new ArrayList<>();
		items.addAll(getErrorMonthAll(companyId, monthErrors, true));
		if (!items.isEmpty()) {
			resultError.put(TypeError.ERROR_MONTH.value, items);
			return resultError;
		}
		return resultError;
	}

	public List<DPItemValue> getErrorMonthAll(String companyId, List<EmployeeMonthlyPerError> monthErrors, boolean flexSup){
		List<DPItemValue> items = new ArrayList<>();
		// not flex
		val lstEmpError = flexSup ?  monthErrors.stream()
				.filter(x -> x.getErrorType().value != ErrorType.FLEX.value && x.getErrorType().value != ErrorType.FLEX_SUPP.value).collect(Collectors.toList()) : 
				monthErrors.stream()
				.filter(x -> x.getErrorType().value != ErrorType.FLEX.value).collect(Collectors.toList());
		val listNo = lstEmpError.stream().filter(x -> x.getErrorType().value == ErrorType.SPECIAL_REMAIN_HOLIDAY_NUMBER.value).map(x -> x.getNo()).collect(Collectors.toList());
		
		Map<Integer, SpecialHoliday> sHolidayMap = listNo.isEmpty() ? new HashMap<>() : specialHolidayRepository.findSimpleByCompanyIdNoMaster(companyId, listNo)
				.stream().filter(x -> x.getSpecialHolidayCode() != null)
				.collect(Collectors.toMap(x -> x.getSpecialHolidayCode().v(), x -> x));
		
		lstEmpError.stream().forEach(error -> {
			createMessageError(error).stream().forEach(message -> {
				if(message.equals("Msg_1414")){
					val sh = sHolidayMap.get(error.getNo());
					message =  TextResource.localize(message, sh == null ? "" : sh.getSpecialHolidayName().v());
				}else{
					message = TextResource.localize(message);
				}
				items.add(new DPItemValue(error.getEmployeeID(), message));
			});
		});
		return items;
	}
	/**
	 * 乖離エラー発生時の本人確認解除
	 */
	public List<DPItemValue> releaseDivergence(List<IntegrationOfDaily> dailyResults) {
		// 乖離エラーのチェック
		if(CollectionUtil.isEmpty(dailyResults)) {
			return new ArrayList<>();
		}
		List<DPItemValue> divergenceErrors = new ArrayList<>();
		for (IntegrationOfDaily d : dailyResults) {
			List<EmployeeDailyPerError> employeeError = d.getEmployeeError();
			for (EmployeeDailyPerError err : employeeError) {
				if (err != null && err.getErrorAlarmWorkRecordCode().v().startsWith("D") && checkErrorOdd(err.getErrorAlarmWorkRecordCode().v())
						&& (!err.getErrorAlarmMessage().isPresent()
								|| !err.getErrorAlarmMessage().get().v().equals(TextResource.localize("Msg_1298")))) {
					if (err.getAttendanceItemList().isEmpty()) {
						divergenceErrors.add(new DPItemValue("", err.getEmployeeID(), err.getDate(), 0));
					} else {
						divergenceErrors.addAll(err.getAttendanceItemList().stream()
								.map(itemId -> new DPItemValue("", err.getEmployeeID(), err.getDate(), itemId))
								.collect(Collectors.toList()));
					}
				}
			}
		}
		return divergenceErrors;
	}
	
	private boolean checkErrorOdd(String errorCode) {
		String valueEnd = errorCode.substring(errorCode.length() - 1, errorCode.length());
		if (StringUtils.isNumeric(valueEnd) && Integer.parseInt(valueEnd) % 2 != 0)
			return true;
		else
			return false;
	}
	
	private boolean checkErrorD1(String code){
		String number = code.replace(code.replaceAll(PATTERN_1, EMPTY_STRING), EMPTY_STRING);
		if(Integer.parseInt(number) >= 100) return true;
		return false;
	}
	
	public List<String> createMessageError(EmployeeMonthlyPerError errorEmployeeMonth) {
		List<String> messageIds = new ArrayList<>();
		ErrorType errroType = errorEmployeeMonth.getErrorType();
		// 年休: 年休エラー
		Optional<AnnualLeaveError> annualHoliday = errorEmployeeMonth.getAnnualHoliday();
		if (annualHoliday.isPresent()) {
			messageIds.add(messageIdAnnualLeave(annualHoliday.get().value));
		}
		Optional<ReserveLeaveError> yearlyReserved = errorEmployeeMonth.getYearlyReserved();
		if (yearlyReserved.isPresent()) {
			messageIds.add("Msg_" + (1385 + yearlyReserved.get().value));
		}
		if (errroType.value == ErrorType.REMAINING_ALTERNATION_NUMBER.value) {
			messageIds.add("Msg_1387");
		} else if (errroType.value == ErrorType.REMAIN_LEFT.value) {
			messageIds.add("Msg_1388");
		} else if (errroType.value == ErrorType.PUBLIC_HOLIDAY.value) {
			messageIds.add("Msg_1389");
		} else if (errroType.value == ErrorType.H60_SUPER_HOLIDAY.value) {
			messageIds.add("Msg_1390");
		} else if(errroType.value == ErrorType.FLEX_SUPP.value){
			messageIds.add("Msg_1415");
		} else if(errroType.value == ErrorType.SPECIAL_REMAIN_HOLIDAY_NUMBER.value){
			messageIds.add("Msg_1414");
		} else if(errroType.value == ErrorType.CHILDCARE_HOLIDAY.value){
            messageIds.add("Msg_2264");
        } else if(errroType.value == ErrorType.CARE_HOLIDAY.value){
            messageIds.add("Msg_2265");
        }

		return messageIds;
	}

	private String messageIdAnnualLeave(int value) {
		switch (value) {
		case 0:

			return "Msg_1383";
		case 1:

			return "Msg_1384";
		case 2:

			return "Msg_1381";
		case 3:

			return "Msg_1382";
		case 4:

			return "Msg_1379";
		case 5:

			return "Msg_1380";

		default:
			return "";
		}

	}
	
	// 備考で日次エラー解除
	public List<IntegrationOfDaily> removeErrorRemarkAll(String companyId, List<IntegrationOfDaily> domainDailyNews, List<DailyRecordDto> dtoNews) {
		Set<String> errors = domainDailyNews.stream().flatMap(x -> x.getEmployeeError().stream())
				.map(x -> x.getErrorAlarmWorkRecordCode().v()).collect(Collectors.toSet());
		if (errors.isEmpty())
			return new ArrayList<>();
		List<ErrorAlarmWorkRecord> errorAlarms = errorAlarmWRRepo.getListErAlByListCodeRemark(companyId, errors);
		for (IntegrationOfDaily domain : domainDailyNews) {
			val dtoCorrespon = dtoNews.stream().filter(x -> x.getEmployeeId().equals(domain.getEmployeeId()) && x.getDate().equals(domain.getYmd())).findFirst().orElse(null);
			val error = domain.getEmployeeError().stream().filter(x -> {
				val errorSelect = errorAlarms.stream()
						.filter(y -> x.getErrorAlarmWorkRecordCode().v().equals(y.getCode().v())).findFirst()
						.orElse(null);
				if (errorSelect != null && dtoCorrespon != null) {
					ItemValue itemValue = AttendanceItemUtil.toItemValues(dtoCorrespon, Arrays.asList(errorSelect.getRemarkColumnNo())).stream().findFirst().orElse(null);
					// có lỗi nhưng đã nhập lý do
					if(itemValue != null && itemValue.getValue() != null && !itemValue.getValue().equals("")) {
						return false;
					}else {
						val item = x.getAttendanceItemList().stream().filter(z -> !(z.intValue() == errorSelect.getErrorDisplayItem() && itemValue != null && itemValue.getValue() != null && !itemValue.getValue().equals("")))
								.collect(Collectors.toList());
						if(!item.isEmpty()) {
							x.setAttendanceItemList(item);
						}
						return true;
					}
				}else {
					return true;
				}
			}).collect(Collectors.toList());
			domain.setEmployeeError(error);
		}
		return domainDailyNews;
	}

	// 備考で日次エラー解除
	public List<EmployeeDailyPerError> removeErrorRemark(String companyId, List<EmployeeDailyPerError> lstError, List<DailyRecordDto> dtoNews) {
		Set<String> errors = lstError.stream().map(x -> x.getErrorAlarmWorkRecordCode().v())
				.collect(Collectors.toSet());
		if (errors.isEmpty())
			return new ArrayList<>();
		List<ErrorAlarmWorkRecord> errorAlarms = errorAlarmWRRepo.getListErAlByListCodeRemark(companyId, errors);
		lstError = lstError.stream().filter(x -> {
			val errorSelect = errorAlarms.stream()
					.filter(y -> x.getErrorAlarmWorkRecordCode().v().equals(y.getCode().v())).findFirst()
					.orElse(null);
			if (errorSelect != null) {
				val dtoCorrespon = dtoNews.stream().filter(k -> k.getEmployeeId().equals(x.getEmployeeID()) && k.getDate().equals(x.getDate())).findFirst().orElse(null);
				if(dtoCorrespon == null) return true;
				ItemValue itemValue = AttendanceItemUtil.toItemValues(dtoCorrespon, Arrays.asList(errorSelect.getRemarkColumnNo())).stream().findFirst().orElse(null);
				val item = x.getAttendanceItemList().stream().filter(z -> !(z.intValue() == errorSelect.getErrorDisplayItem() && itemValue != null && itemValue.getValue() != null && !itemValue.getValue().equals("")))
						.collect(Collectors.toList());
				if(item.isEmpty()) {
					return false;
				}else {
					x.setAttendanceItemList(item);
					return true;
				}
			}else {
				return true;
			}
		}).collect(Collectors.toList());
		return lstError;
	}
	
	//日の確認承認の排他チェック
    public void checkVerConfirmApproval(ApprovalConfirmCache cacheOld, List<DPItemCheckBox> dataCheckSign, List<DPItemCheckBox> dataCheckApproval, List<DPItemValue> itemValues) {
		String companyId = AppContexts.user().companyId();
		String sId = AppContexts.user().employeeId();
        List<Pair<String, GeneralDate>> signChangeMap = dataCheckSign.stream().map(x -> Pair.of(x.getEmployeeId(), x.getDate())).collect(Collectors.toList());
        List<Pair<String, GeneralDate>> approvalChangeMap = dataCheckApproval.stream().map(x -> Pair.of(x.getEmployeeId(), x.getDate())).collect(Collectors.toList());
        List<Pair<String, GeneralDate>> mapItemChange = itemValues.stream().map(x -> Pair.of(x.getEmployeeId(), x.getDate())).collect(Collectors.toList());
		List<ConfirmStatusActualResult> confirmResults = confirmStatusActualDayChange.processConfirmStatus(companyId, sId, cacheOld.getEmployeeIds(), Optional.of(cacheOld.getPeriod()), Optional.empty());
		List<ApprovalStatusActualResult> approvalResults = approvalStatusActualDayChange.processApprovalStatus(companyId, sId, cacheOld.getEmployeeIds(), Optional.of(cacheOld.getPeriod()), Optional.empty(), cacheOld.getMode());
		confirmResults = confirmResults.stream()
				.filter(x -> signChangeMap.contains(Pair.of(x.getEmployeeId(), x.getDate()))
						|| mapItemChange.contains(Pair.of(x.getEmployeeId(), x.getDate())))
				.collect(Collectors.toList());
		approvalResults = approvalResults.stream()
				.filter(x -> approvalChangeMap.contains(Pair.of(x.getEmployeeId(), x.getDate()))
						|| mapItemChange.contains(Pair.of(x.getEmployeeId(), x.getDate())))
				.collect(Collectors.toList());
		ApprovalConfirmCache cacheOldTemp = new ApprovalConfirmCache(sId, cacheOld.getEmployeeIds(), cacheOld.getPeriod(), cacheOld.getMode(), new ArrayList<>(), new ArrayList<>());
		cacheOldTemp.setLstConfirm(cacheOld.getLstConfirm().stream()
				.filter(x -> signChangeMap.contains(Pair.of(x.getEmployeeId(), x.getDate()))
						|| mapItemChange.contains(Pair.of(x.getEmployeeId(), x.getDate())))
				.collect(Collectors.toList()));
		cacheOldTemp.setLstApproval(cacheOld.getLstApproval().stream()
				.filter(x -> approvalChangeMap.contains(Pair.of(x.getEmployeeId(), x.getDate()))
						|| mapItemChange.contains(Pair.of(x.getEmployeeId(), x.getDate())))
				.collect(Collectors.toList()));
		ApprovalConfirmCache cacheNew = new ApprovalConfirmCache(sId,  cacheOld.getEmployeeIds(), cacheOld.getPeriod(), cacheOld.getMode(), confirmResults, approvalResults);
		cacheOldTemp.checkVer(cacheNew);
	}
}
