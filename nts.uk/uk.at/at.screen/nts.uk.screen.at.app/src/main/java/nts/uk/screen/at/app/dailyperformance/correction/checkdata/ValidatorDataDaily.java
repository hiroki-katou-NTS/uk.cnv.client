package nts.uk.screen.at.app.dailyperformance.correction.checkdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordWorkFinder;
import nts.uk.ctx.at.record.app.service.workrecord.erroralarm.recordcheck.ErAlWorkRecordCheckService;
import nts.uk.ctx.at.record.app.service.workrecord.erroralarm.recordcheck.result.ContinuousHolidayCheckResult;
import nts.uk.ctx.at.record.dom.divergence.time.DivergenceReasonInputMethodService;
import nts.uk.ctx.at.record.dom.divergence.time.DivergenceTime;
import nts.uk.ctx.at.record.dom.divergence.time.DivergenceTimeRepository;
import nts.uk.ctx.at.record.dom.divergence.time.JudgmentResult;
import nts.uk.ctx.at.shared.dom.attendance.util.AttendanceItemUtil;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyResult;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

@Stateless
public class ValidatorDataDaily {
	
	@Inject
	private ErAlWorkRecordCheckService erAlWorkRecordCheckService;
	
	@Inject
	private DailyRecordWorkFinder fullFinder;
	
//	@Inject
//	private DivergenceTimeRepository divergenceTimeRepository;
//	
//	@Inject
//	private DivergenceReasonInputMethodService divergenceReasonInputMethodService;

	private static final Integer[] CHILD_CARE = { 759, 760, 761, 762 };
	private static final Integer[] CARE = { 763, 764, 765, 766 };
	private static final Integer[] INPUT_CHECK = { 759, 760, 761, 762, 763, 764, 765, 766, 157, 159, 163, 165, 171, 169,
			177, 175, 183, 181, 189, 187, 195, 193, 199, 201, 205, 207, 211, 213 };
	
	static final Map<Integer, Integer> INPUT_CHECK_MAP = IntStream.range(0, INPUT_CHECK.length-1).boxed().collect(Collectors.toMap(x -> INPUT_CHECK[x], x -> x%2 == 0 ? INPUT_CHECK[x+1] : INPUT_CHECK[x-1]));
	//Arrays.stream(INPUT_CHECK).
	
	// 育児と介護の時刻が両方入力されていないかチェックする
	public List<DPItemValue> checkCareItemDuplicate(List<DPItemValue> items) {
		List<DPItemValue> childCares = hasChildCare(items);
		List<DPItemValue> cares = hasCare(items);
		if (!childCares.isEmpty() && !cares.isEmpty()) {
			// throw new BusinessException("Msg_996");
			childCares.addAll(cares);
			return childCares;
		} else
			return Collections.emptyList();

	}

	private List<DPItemValue> hasChildCare(List<DPItemValue> items) {
		List<DPItemValue> itemChild = items.stream()
				.filter(x -> x.getValue() != null && (x.getItemId() == CHILD_CARE[0] || x.getItemId() == CHILD_CARE[1]
						|| x.getItemId() == CHILD_CARE[2] || x.getItemId() == CHILD_CARE[3]))
				.collect(Collectors.toList());
		return itemChild.isEmpty() ? Collections.emptyList() : itemChild;
	}

	private List<DPItemValue> hasCare(List<DPItemValue> items) {
		List<DPItemValue> itemCare = items.stream().filter(x -> x.getValue() != null && (x.getItemId() == CARE[0]
				|| x.getItemId() == CARE[1] || x.getItemId() == CARE[2] || x.getItemId() == CARE[3]))
				.collect(Collectors.toList());
		return itemCare.isEmpty() ? Collections.emptyList() : itemCare;
	}

	public List<DPItemValue> checkCareInputData(List<DPItemValue> items) {
		List<DPItemValue> childCares = hasChildCare(items);
		List<DPItemValue> cares = hasCare(items);
		List<DPItemValue> result = new ArrayList<>();
		if (!childCares.isEmpty()) {
			Map<Integer, DPItemValue> childMap= childCares.stream().collect(Collectors.toMap(DPItemValue::getItemId, x -> x));
		    boolean childCare759 = childMap.containsKey(CHILD_CARE[0]);
		    boolean childCare760 = childMap.containsKey(CHILD_CARE[1]);
		    boolean childCare761 = childMap.containsKey(CHILD_CARE[2]);
		    boolean childCare762 = childMap.containsKey(CHILD_CARE[3]);
		    if(!(childCare759 && childCare760)){
		    	if(childCare759){
		    		result.add(childMap.get(CHILD_CARE[0]));
		    	}else if(childCare760){
		    		result.add(childMap.get(CHILD_CARE[1]));
		    	}
		    }
			if (!(childCare761 && childCare762)) {
				if(childCare761){
		    		result.add(childMap.get(CHILD_CARE[2]));
		    	}else if(childCare762){
		    		result.add(childMap.get(CHILD_CARE[3]));
		    	}
			}
			return result;
		} else if (!cares.isEmpty()) {
			Map<Integer, DPItemValue> caresMap= cares.stream().collect(Collectors.toMap(DPItemValue::getItemId, x -> x));
		    boolean care763 = caresMap.containsKey(CARE[0]);
		    boolean care764 = caresMap.containsKey(CARE[1]);
		    boolean care765 = caresMap.containsKey(CARE[2]);
		    boolean care766 = caresMap.containsKey(CARE[3]);
		    if(!(care763 && care764)){
		    	if(care763){
		    		result.add(caresMap.get(CARE[0]));
		    	}else if(care764){
		    		result.add(caresMap.get(CARE[1]));
		    	}
		    }
			if (!(care765 && care766)) {
				if(care765){
		    		result.add(caresMap.get(CARE[2]));
		    	}else if(care766){
		    		result.add(caresMap.get(CARE[3]));
		    	}
			}
			return result;
		}
		return Collections.emptyList();
	}
	
	//check trong 1 ngay nhom item yeu cau nhap theo cap
	public List<DPItemValue> checkInputData(List<DPItemValue> items) {
		List<DPItemValue> result = new ArrayList<>();
		//loc chua item can check
		List<DPItemValue> itemCanCheck = items.stream().filter(x -> INPUT_CHECK_MAP.containsKey(x.getItemId())).collect(Collectors.toList());
		if (itemCanCheck.isEmpty())
			return result;
		Map<Integer, String> itemCheckMap = itemCanCheck.stream().collect(Collectors.toMap(x -> x.getItemId(), x -> x.getValue()));
		List<DPItemValue> itemCheckDBs = new ArrayList<>();
		// loc nhung thang chi duoc insert 1 trong 1 cap 
		itemCanCheck.forEach(x ->{
			Integer itemCheck = INPUT_CHECK_MAP.get(x.getItemId());
			if(!itemCheckMap.containsKey(itemCheck)) itemCheckDBs.add(x);
		});
		if(itemCheckDBs.isEmpty()) return result;
		
		List<GeneralDate> dates = items.stream().map(x -> x.getDate()).sorted((x, y) -> x.compareTo(y))
				.collect(Collectors.toSet()).stream().collect(Collectors.toList());
		List<String> employees = items.stream().map(x -> x.getEmployeeId()).collect(Collectors.toSet()).stream().collect(Collectors.toList());
		
		List<DailyModifyResult> itemValues =  this.fullFinder.find(employees, new DatePeriod(dates.get(0), dates.get(dates.size() - 1))).stream()
				.map(c -> DailyModifyResult.builder().items(AttendanceItemUtil.toItemValues(c, Arrays.asList(INPUT_CHECK)))
						.workingDate(c.workingDate()).employeeId(c.employeeId()).completed())
				.collect(Collectors.toList());
		if(itemValues.isEmpty()) return result;
		Map<Integer, String> valueGetFromDBMap = itemValues.get(0).getItems().stream().collect(Collectors.toMap(x -> x.getItemId(), x -> x.getValue() == null ? "" : x.getValue()));
		itemCheckDBs.stream().forEach( x ->{
			if(valueGetFromDBMap.containsKey(INPUT_CHECK_MAP.get(x.getItemId())) && valueGetFromDBMap.get(INPUT_CHECK_MAP.get(x.getItemId())).equals("")){
				result.add(x);
			}
		});
		return result;
	}
	
	public List<DPItemValue> checkContinuousHolidays(String employeeId, DateRange date) {
		ContinuousHolidayCheckResult result = erAlWorkRecordCheckService.checkContinuousHolidays(employeeId,
				new DatePeriod(date.getStartDate(), date.getEndDate()));
		if(result == null) return Collections.emptyList();
		Map<GeneralDate, Integer> resultMap = erAlWorkRecordCheckService.checkContinuousHolidays(employeeId,
				new DatePeriod(date.getStartDate(), date.getEndDate())).getCheckResult();
		if (!resultMap.isEmpty()) {
			return resultMap.entrySet().stream()
					.map(x -> new DPItemValue("勤務種類", employeeId, x.getKey(), 0, String.valueOf(x.getValue()), result.message()))
					.collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}
	
	//乖離理由が選択、入力されているかチェックする
	public void checkReasonInput(List<DPItemValue> items){
//		String companyId = AppContexts.user().companyId();
//		List<DivergenceTime> divergenceTime = divergenceTimeRepository.getDivTimeListByUseSet(companyId);
//		items.stream().forEach(x -> {
//			JudgmentResult judgmentResult = divergenceReasonInputMethodService.determineLeakageReason(x.getEmployeeId(), x.getDate(), divergenceTimeNo, divergenceReasonCode, divergenceReasonContent, justmentResult)
//		});
		
	}
}
