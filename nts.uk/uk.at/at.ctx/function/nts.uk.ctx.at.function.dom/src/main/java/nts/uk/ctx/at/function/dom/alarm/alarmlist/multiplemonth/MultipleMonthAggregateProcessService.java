package nts.uk.ctx.at.function.dom.alarm.alarmlist.multiplemonth;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.dom.adapter.ResponseImprovementAdapter;
import nts.uk.ctx.at.function.dom.adapter.actualmultiplemonth.ActualMultipleMonthAdapter;
import nts.uk.ctx.at.function.dom.adapter.actualmultiplemonth.MonthlyRecordValueImport;
import nts.uk.ctx.at.function.dom.adapter.eralworkrecorddto.ErAlAtdItemConAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.multimonth.MultiMonthFucAdapter;
import nts.uk.ctx.at.function.dom.alarm.AlarmCategory;
import nts.uk.ctx.at.function.dom.alarm.alarmdata.ValueExtractAlarm;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.EmployeeSearchDto;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess.daily.dailyaggregationprocess.TypeCheckWorkRecord;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.monthly.CompareOperatorText;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategory;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategoryRepository;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.multimonth.MulMonAlarmCond;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.multimonth.doevent.MulMonCheckCondDomainEventDto;
import nts.uk.ctx.at.function.dom.attendanceitemname.AttendanceItemName;
import nts.uk.ctx.at.function.dom.attendanceitemname.service.AttendanceItemNameDomainService;
import nts.uk.ctx.at.shared.dom.attendance.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.attendance.util.item.ValueType;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.calendar.period.DatePeriod;
import nts.uk.shr.com.time.calendar.period.YearMonthPeriod;

@Stateless
public class MultipleMonthAggregateProcessService {

	@Inject
	private AlarmCheckConditionByCategoryRepository alCheckConByCategoryRepo;

	@Inject
	private ResponseImprovementAdapter responseImprovementAdapter; // filter đối
																	// tượng

	@Inject
	private ActualMultipleMonthAdapter actualMultipleMonthAdapter;

	@Inject
	private MultiMonthFucAdapter multiMonthFucAdapter;

	@Inject
	private AttendanceItemNameDomainService attdItemNameDomainService;

	public List<ValueExtractAlarm> multimonthAggregateProcess(String companyID, String checkConditionCode,
			DatePeriod period, List<EmployeeSearchDto> employees, List<Integer> listCategory) {

		List<String> employeeIds = employees.stream().map(e -> e.getId()).collect(Collectors.toList());

		List<ValueExtractAlarm> listValueExtractAlarm = new ArrayList<>();

		Optional<AlarmCheckConditionByCategory> alCheckConByCategory = alCheckConByCategoryRepo.find(companyID,
				AlarmCategory.MULTIPLE_MONTH.value, checkConditionCode);
		if (!alCheckConByCategory.isPresent()) {
			return Collections.emptyList();
		}
		// list alarmPartem
		MulMonAlarmCond mulMonAlarmCond = (MulMonAlarmCond) alCheckConByCategory.get().getExtractionCondition();
		List<MulMonCheckCondDomainEventDto> listExtra = multiMonthFucAdapter
				.getListMultiMonCondByListEralID(mulMonAlarmCond.getErrorAlarmCondIds());
		
		//Tesst 
//		List<String> a = Arrays.asList("54656f07-1b0f-4df0-88e8-52ea8e2nf01");
//		List<MulMonCheckCondDomainEventDto> listExtra = multiMonthFucAdapter
//				.getListMultiMonCondByListEralID(a);
		System.out.println("=>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+listExtra.get(0).toString());
		// 対象者を絞り込む
		DatePeriod endDatePerior = new DatePeriod(period.end(), period.end());
		GeneralDate tempStart = period.start();
		GeneralDate tempEnd = period.end();
		YearMonth startYearMonth = tempStart.yearMonth();
		YearMonth endYearMonth = tempEnd.yearMonth();
		YearMonthPeriod yearMonthPeriod = new YearMonthPeriod(startYearMonth, endYearMonth);
		List<String> listEmployeeID = responseImprovementAdapter.reduceTargetResponseImprovement(employeeIds,
				endDatePerior, alCheckConByCategory.get().getExtractTargetCondition());
		
		// 対象者の件数をチェック : 対象者 ≦ 0
		if (listEmployeeID.isEmpty()) {
			return Collections.emptyList();
		}
		List<EmployeeSearchDto> employeesDto = employees.stream().filter(c -> listEmployeeID.contains(c.getId()))
				.collect(Collectors.toList());

		/** Tạm thời dùng list 436 để test */
		Map<String, List<MonthlyRecordValueImport>> resultActuals = new HashMap<String, List<MonthlyRecordValueImport>>();
		// 月別実績を取得する
		for (String employeeId : listEmployeeID) {
			List<MonthlyRecordValueImport> resultActual = actualMultipleMonthAdapter
					.getActualMultipleMonth(listEmployeeID.get(0), yearMonthPeriod, listCategory);
			resultActuals.put(employeeId, resultActual);
		}
		
		
		// tab1
		listValueExtractAlarm.addAll(this.extraResultMulMon(companyID, listExtra, period, employeesDto, resultActuals));
		
		//TEst 22/7
		ValueExtractAlarm resultMonthlyValueTest = new ValueExtractAlarm(employeesDto.get(0).getWorkplaceId(),
				employeesDto.get(0).getId(), "yearMonthString",// fix tạm
				TextResource.localize("KAL010_100"),
				"NameItem",
				"Description", 
				listExtra.get(0).getDisplayMessage());
		listValueExtractAlarm.add(resultMonthlyValueTest);	
		return listValueExtractAlarm;
	}

	// tab1
	private List<ValueExtractAlarm> extraResultMulMon(String companyId, List<MulMonCheckCondDomainEventDto> listExtra,
			DatePeriod period, List<EmployeeSearchDto> employees,
			Map<String, List<MonthlyRecordValueImport>> resultActuals) {

		List<ValueExtractAlarm> listValueExtractAlarm = new ArrayList<>();
		List<YearMonth> lstYearMonth = period.yearMonthsBetween();
		List<YearMonth> yearMulMonth  ;
//		GeneralDate lastDateInPeriod = period.end();
		
		for (MulMonCheckCondDomainEventDto extra : listExtra) {
			for (EmployeeSearchDto employee : employees) {
				int countContinus = 0;
				int countNumber = 0;
				float sumActual = 0;
				// trung binh
				float avg = 0.0f;
				TypeCheckWorkRecordMultipleMonthImport checkItem = EnumAdaptor.valueOf(extra.getTypeCheckItem(), TypeCheckWorkRecordMultipleMonthImport.class);
				List<MonthlyRecordValueImport> result = resultActuals.get(employee.getId());
				ErAlAtdItemConAdapterDto erAlAtdItemConAdapterDto = extra.getErAlAtdItem();
				int compare = erAlAtdItemConAdapterDto.getCompareOperator();
				CompareOperatorText compareOperatorText = convertCompareType(compare);
				BigDecimal startValue = erAlAtdItemConAdapterDto.getCompareStartValue();
				BigDecimal endValue = erAlAtdItemConAdapterDto.getCompareEndValue();
				String nameErrorAlarm = extra.getNameAlarmMulMon();
				// TODO get ..?
				String nameItem = "";
				String alarmDescription = "";
				
				// Tinh tong thực tích va gia tri trung binh 0->5
				for (MonthlyRecordValueImport eachResult : result) {
					for (ItemValue itemValue : eachResult.getItemValues()) {
						sumActual += Integer.parseInt(itemValue.getValue());
						avg = sumActual / (result.size()*eachResult.getItemValues().size());
					}
					
				}
				
				//Xử lí trường hợp continusMonth 6-7-8
				if(extra.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonthImport.CONTINUOUS_TIME.value 
						|| extra.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonthImport.CONTINUOUS_TIMES.value
							|| extra.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonthImport.CONTINUOUS_AMOUNT.value){
				for (MonthlyRecordValueImport eachResult : result) {
					float sumActualPermonth = 0;
					for (ItemValue itemValue : eachResult.getItemValues()) {
						sumActualPermonth += Integer.parseInt(itemValue.getValue());
						if (checkPerMonth(extra, sumActualPermonth)) {
							countContinus++;
							if (countContinus >= extra.getTimes()) {
								// TODO
								countContinus = 0;
							}
						} else {
							countContinus = 0;
						}
						}
					}
				}
				
				//Xử lí trường hợp numberMonth 9-10-11
				if(extra.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonthImport.NUMBER_TIME.value 
						|| extra.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonthImport.NUMBER_TIMES.value
						|| extra.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonthImport.NUMBER_AMOUNT.value){
					for (MonthlyRecordValueImport eachResult : result) {
						float sumActualPermonth = 0;
						for (ItemValue itemValue : eachResult.getItemValues()) {
							sumActualPermonth += Integer.parseInt(itemValue.getValue());
						}
						if (checkPerMonth(extra, sumActualPermonth)) {
							countContinus++;
						} 
					}
				}
				
					switch (checkItem) {
					// TIME(0,"時間")
					case TIME:
					case TIMES:
					case AMOUNT:
						if (checkPerMonth(extra, sumActual)) {
							// TODO
							nameItem = TextResource.localize("NameItem012");
							String startValueTime = String.valueOf(startValue.intValue()/60)+":"+String.valueOf(startValue.intValue()%60);
							String endValueTime = "";
							if(compare<=5) {
								alarmDescription = TextResource.localize("alarmdescip012",nameErrorAlarm,compareOperatorText.getCompareLeft(),startValueTime);
							}else {
								endValueTime = String.valueOf(endValue.intValue()/60)+":"+String.valueOf(endValue.intValue()%60);
								if(compare>5 && compare<=7) {
									alarmDescription = TextResource.localize("alarmdescip012",startValueTime,
											compareOperatorText.getCompareLeft(),
											nameErrorAlarm,
											compareOperatorText.getCompareright(),
											endValueTime
											);	
								}else {
									alarmDescription = TextResource.localize("alarmdescip012",
											nameErrorAlarm,
											compareOperatorText.getCompareLeft(),
											startValueTime + ","+endValueTime,
											compareOperatorText.getCompareright(),
											nameErrorAlarm
											);
								}
							}
						}
					case AVERAGE_TIME:
					case AVERAGE_TIMES:
					case AVERAGE_AMOUNT:
						if (checkPerMonth(extra, avg)) {
							nameItem = TextResource.localize("NameItem345");
							String startValueTime = String.valueOf(startValue.intValue()/60)+":"+String.valueOf(startValue.intValue()%60);
							String endValueTime = "";
							if(compare<=5) {
								alarmDescription = TextResource.localize("alarmdescip345",nameErrorAlarm,compareOperatorText.getCompareLeft(),startValueTime);
							}else {
								endValueTime = String.valueOf(endValue.intValue()/60)+":"+String.valueOf(endValue.intValue()%60);
								if(compare>5 && compare<=7) {
									alarmDescription = TextResource.localize("alarmdescip345",startValueTime,
											compareOperatorText.getCompareLeft(),
											nameErrorAlarm,
											compareOperatorText.getCompareright(),
											endValueTime
											);	
								}else {
									alarmDescription = TextResource.localize("alarmdescip345",
											nameErrorAlarm,
											compareOperatorText.getCompareLeft(),
											startValueTime + ","+endValueTime,
											compareOperatorText.getCompareright(),
											nameErrorAlarm
											);
								}
							}
						}
					case CONTINUOUS_TIME:
					case CONTINUOUS_TIMES:
					case CONTINUOUS_AMOUNT:
						if (checkMulMonth(extra, countContinus)) {
							nameItem = TextResource.localize("NameItem678");
							String startValueTime = String.valueOf(startValue.intValue()/60)+":"+String.valueOf(startValue.intValue()%60);
							String endValueTime = "";
							if(compare<=5) {
								alarmDescription = TextResource.localize("alarmdescip678",nameErrorAlarm,compareOperatorText.getCompareLeft(),startValueTime);
							}else {
								endValueTime = String.valueOf(endValue.intValue()/60)+":"+String.valueOf(endValue.intValue()%60);
								if(compare>5 && compare<=7) {
									alarmDescription = TextResource.localize("alarmdescip678",startValueTime,
											compareOperatorText.getCompareLeft(),
											nameErrorAlarm,
											compareOperatorText.getCompareright(),
											endValueTime
											);	
								}else {
									alarmDescription = TextResource.localize("alarmdescip678",
											nameErrorAlarm,
											compareOperatorText.getCompareLeft(),
											startValueTime + ","+endValueTime,
											compareOperatorText.getCompareright(),
											nameErrorAlarm
											);
								}
							}
						}
					// 9-10-11
					default:
						if (checkMulMonth(extra, countNumber)) {
							nameItem = TextResource.localize("NameItem91011");
							String startValueTime = String.valueOf(startValue.intValue()/60)+":"+String.valueOf(startValue.intValue()%60);
							String endValueTime = "";
							if(compare<=5) {
								alarmDescription = TextResource.localize("alarmdescip91011",nameErrorAlarm,compareOperatorText.getCompareLeft(),startValueTime);
							}else {
								endValueTime = String.valueOf(endValue.intValue()/60)+":"+String.valueOf(endValue.intValue()%60);
								if(compare>5 && compare<=7) {
									alarmDescription = TextResource.localize("alarmdescip91011",startValueTime,
											compareOperatorText.getCompareLeft(),
											nameErrorAlarm,
											compareOperatorText.getCompareright(),
											endValueTime
											);	
								}else {
									alarmDescription = TextResource.localize("alarmdescip91011",
											nameErrorAlarm,
											compareOperatorText.getCompareLeft(),
											startValueTime + ","+endValueTime,
											compareOperatorText.getCompareright(),
											nameErrorAlarm
											);
								}
							}
						}
						break;
					}
					ValueExtractAlarm resultMonthlyValue = new ValueExtractAlarm(employee.getWorkplaceId(),
							employee.getId(), "yearMonthString",// fix tạm
							TextResource.localize("KAL010_100"),
							nameItem,
							alarmDescription, 
							extra.getDisplayMessage());
					listValueExtractAlarm.add(resultMonthlyValue);		
			}
			
		}
		return listValueExtractAlarm;
	}
	private boolean checkMulMonth(MulMonCheckCondDomainEventDto extra, int count) {
		boolean check = false;
		if(compareSingle(extra.getTimes(), count, extra.getCompareOperator())){
			check = true;
		}
		return check;
	}

	private boolean checkPerMonth(MulMonCheckCondDomainEventDto extra, float sumActual) {
		boolean check = false;
		BigDecimal sumActualBD = new BigDecimal(sumActual);
		if (extra.getCompareOperator() <= 5) {
			if (compareSingle(extra.getErAlAtdItem().getCompareStartValue(), sumActualBD, extra.getErAlAtdItem().getCompareOperator())) {
				check = true;
			}
		}else {
			if (CompareDouble(extra.getErAlAtdItem().getCompareStartValue(), extra.getErAlAtdItem().getCompareEndValue(), sumActualBD, extra.getErAlAtdItem().getCompareOperator())) {
				check = true;
			}
		}
		return check;
	}
	private boolean compareSingle(double valueAgreement, double value, int compareType) {
		boolean check = false;
		switch (compareType) {
		case 0:/* 等しい（＝） */
			if (valueAgreement == value)
				check = true;
			break;
		case 1:/* 等しくない（≠） */
			if (valueAgreement != value)
				check = true;
			break;
		case 2:/* より大きい（＞） */
			if (valueAgreement > value)
				check = true;
			break;
		case 3:/* 以上（≧） */
			if (valueAgreement >= value)
				check = true;
			break;
		case 4:/* より小さい（＜） */
			if (valueAgreement < value)
				check = true;
			break;
		default:/* 以下（≦） */
			if (valueAgreement <= value)
				check = true;
			break;
		}

		return check;
	}

	private boolean compareSingle(BigDecimal valueAgreement, BigDecimal value, int compareType) {
		boolean check = false;
		switch (compareType) {
		case 0:/* 等しい（＝） */
			if (valueAgreement == value)
				check = true;
			break;
		case 1:/* 等しくない（≠） */
			if (valueAgreement != value)
				check = true;
			break;
		case 2:/* より大きい（＞） */
			if (valueAgreement.compareTo(value) == 1)
				check = true;
			break;
		case 3:/* 以上（≧） */
			if (valueAgreement.compareTo(value) >= 0)
				check = true;
			break;
		case 4:/* より小さい（＜） */
			if (valueAgreement.compareTo(value) == -1)
				check = true;
			break;
		default:/* 以下（≦） */
			if (valueAgreement.compareTo(value) <= 0)
				check = true;
			break;
		}

		return check;
	}

	
	private boolean CompareDouble(BigDecimal value, BigDecimal valueAgreementStart, BigDecimal valueAgreementEnd, int compare) {
		boolean check = false;
		switch (compare) {
		/* 範囲の間（境界値を含まない）（＜＞） */
		case 6:
			if (value.compareTo(valueAgreementStart)>0 && value.compareTo(valueAgreementEnd)<0 ) {
				check = true;
			}
			break;
			/* 範囲の間（境界値を含む）（≦≧） */
		case 7:
			if (value.compareTo(valueAgreementStart)>=0 && value.compareTo(valueAgreementEnd)<=0 ) {
				check = true;
			}
			break;
			/* 範囲の外（境界値を含まない）（＞＜） */
		case 8:
			if (value.compareTo(valueAgreementStart)<0 || value.compareTo(valueAgreementEnd)>0 ) {
				check = true;
			}
			break;
			/* 範囲の外（境界値を含む）（≧≦） */
		default:
			if (value.compareTo(valueAgreementStart)<=0 || value.compareTo(valueAgreementEnd)>=0 ) {
				check = true;
			}
			break;
		}
		return check;
	}

	private CompareOperatorText convertCompareType(int compareOperator) {
		CompareOperatorText compare = new CompareOperatorText();
		switch (compareOperator) {
		case 0:/* 等しい（＝） */
			compare.setCompareLeft("＝");
			compare.setCompareright("");
			break;
		case 1:/* 等しくない（≠） */
			compare.setCompareLeft("≠");
			compare.setCompareright("");
			break;
		case 2:/* より大きい（＞） */
			compare.setCompareLeft("＞");
			compare.setCompareright("");
			break;
		case 3:/* 以上（≧） */
			compare.setCompareLeft("≧");
			compare.setCompareright("");
			break;
		case 4:/* より小さい（＜） */
			compare.setCompareLeft("＜");
			compare.setCompareright("");
			break;
		case 5:/* 以下（≦） */
			compare.setCompareLeft("≦");
			compare.setCompareright("");
			break;
		case 6:/* 範囲の間（境界値を含まない）（＜＞） */
			compare.setCompareLeft("＜");
			compare.setCompareright("＜");
			break;
		case 7:/* 範囲の間（境界値を含む）（≦≧） */
			compare.setCompareLeft("≦");
			compare.setCompareright("≦");
			break;
		case 8:/* 範囲の外（境界値を含まない）（＞＜） */
			compare.setCompareLeft("＞");
			compare.setCompareright("＞");
			break;

		default:/* 範囲の外（境界値を含む）（≧≦） */
			compare.setCompareLeft("≧");
			compare.setCompareright("≧");
			break;
		}

		return compare;
	}

}
