package nts.uk.ctx.at.record.dom.workrecord.erroralarm.multimonth.algorithm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.monthly.TimeOfMonthlyRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.algorithm.DataCheckAlarmListService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareSingleValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.ErAlAttendanceItemCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.CompareOperatorText;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConditionType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConvertCompareTypeToText;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.TypeCheckWorkRecordMultipleMonth;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceHistImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceIdAndPeriodImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.monthlycheckcondition.algorithm.MonthlyRecordValuesDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.multimonth.MulMonAlarmCheckCondRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.multimonth.MulMonthAlarmCheckCond;
import nts.uk.ctx.at.shared.dom.adapter.attendanceitemname.AttendanceItemNameAdapter;
import nts.uk.ctx.at.shared.dom.adapter.attendanceitemname.MonthlyAttendanceItemNameDto;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckType;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ExtractionAlarmPeriodDate;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.*;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMergeRepository;
import nts.uk.shr.com.i18n.TextResource;
@Stateless
public class MultiMonthlyExtractCheckServiceImpl<V> implements MultiMonthlyExtractCheckService{
	
	
	@Inject
	private TimeOfMonthlyRepository timeRepo;
	
	@Inject
	private RemainMergeRepository remainRepo;
	@Inject
	private AttendanceItemNameAdapter attendanceAdapter;
	@Inject
	private MulMonAlarmCheckCondRepository mulMunCondRepos;
	@Inject
	private ConvertCompareTypeToText convertComparaToText;
	@Inject
	private DataCheckAlarmListService dataCheckAlarm;
	@Override
	public void extractMultiMonthlyAlarm(String cid, List<String> lstSid, YearMonthPeriod mPeriod,
			List<String> lstAnyConID, List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
			List<AlarmEmployeeList> alarmEmployeeList, List<AlarmExtractionCondition> alarmExtractConditions,
			String alarmCheckConditionCode) {
		DataCheck data = new DataCheck(cid, lstSid, mPeriod, lstAnyConID);
		if(data.lstAnyCondCheck.isEmpty()) return;
		
		for(MulMonthAlarmCheckCond anyCond : data.lstAnyCondCheck) {
			// 「アラーム抽出条件」を作成してInput．List＜アラーム抽出条件＞を追加
			val extractionCond = alarmExtractConditions.stream()
					.filter(x -> x.getAlarmListCheckType() == AlarmListCheckType.FreeCheck
							&& x.getAlarmCheckConditionNo().equals(String.valueOf(anyCond.getCondNo())))
					.findAny();
			if (!extractionCond.isPresent()) {
				alarmExtractConditions.add(new AlarmExtractionCondition(
						String.valueOf(anyCond.getCondNo()),
						new AlarmCheckConditionCode(alarmCheckConditionCode),
						AlarmCategory.MULTIPLE_MONTH,
						AlarmListCheckType.FreeCheck
				));
			}

			lstCheckType.add(new AlarmListCheckInfor(String.valueOf(anyCond.getCondNo()), AlarmListCheckType.FreeCheck));
			ErAlAttendanceItemCondition<?> erCondition = anyCond.getErAlAttendanceItemCondition();
			if(erCondition == null) continue;			
			//比較演算子
			int compare = erCondition.getCompareSingleValue() == null ?
					erCondition.getCompareRange().getCompareOperator().value :
						erCondition.getCompareSingleValue().getCompareOpertor().value;
			for(String sid : lstSid) {
				String startValue = erCondition.getCompareSingleValue() == null ?
						erCondition.getCompareRange().getStartValue().toString() :
						erCondition.getCompareSingleValue().getValue().toString();					
				String endValue = erCondition.getCompareRange() == null ? null :
						erCondition.getCompareRange().getEndValue().toString();
				//複数月のチェック条件(平均)
				float avg = 0;
				//複数月のチェック条件(連続)
				//int countTmp = 0;
				//複数月のチェック条件(該当月数)
				List<String> lstYM = new ArrayList<>();
				List<MonthlyRecordValuesDto> lstSidOfMonthData = data.lstMonthData.get(sid);
				//チェック対象値
				double checkedValue = 0;
				String checkValue = "";
				boolean checkAddAlarm = false;
				String avgUnit = "";
				if(lstSidOfMonthData == null || lstSidOfMonthData.isEmpty()) continue;
				
				List<Integer> lstAddSub = erCondition.getCountableTarget().getAddSubAttendanceItems().getAdditionAttendanceItems();
				List<Integer> lstSubStr = erCondition.getCountableTarget().getAddSubAttendanceItems().getSubstractionAttendanceItems();
				switch (anyCond.getTypeCheckItem()) {
				//時間、回数、金額、日数
				case TIME:
				case TIMES:
				case AMOUNT:
				case DAYS:
					List<ItemValue> itemValues = new ArrayList<ItemValue>();
					for(MonthlyRecordValuesDto result :lstSidOfMonthData ){
						itemValues.addAll(result.getItemValues());
						checkAddAlarm = erCondition.checkTarget(item->{
								if (item.isEmpty()) {
									return new ArrayList<>();
								}
								return itemValues.stream().filter(x -> item.contains(x.getItemId())).map(iv -> getValue(iv))
										.collect(Collectors.toList());
							});
						if(checkAddAlarm) {
							checkedValue = erCondition.calculateTargetValue(item ->{
								if(item.isEmpty()) {
									return new ArrayList<>();
								}
								return itemValues.stream().filter(x -> item.contains(x.getItemId())).map(iv -> getValue(iv))
										.collect(Collectors.toList());
							});	
						}
					}
					break;
				//連続時間、連続回数、連続金額、連続日数
				case CONTINUOUS_TIME:
				case CONTINUOUS_TIMES:
				case CONTINUOUS_AMOUNT:
				case CONTINUOUS_DAYS:
					//Count連続月数
					int countContinus = 0;
					
					for(YearMonth ym : mPeriod.yearMonthsBetween()) {
						for(MonthlyRecordValuesDto result :lstSidOfMonthData ){
							if(result.getYearMonth().equals(ym)) {
								boolean checkPerMonth = false;
								checkPerMonth = erCondition.checkTarget(item -> {
											if (item.isEmpty()) {
												return new ArrayList<>();
											}
											return result.getItemValues().stream().filter(x -> item.contains(x.getItemId())).map(iv -> getValue(iv))
													.collect(Collectors.toList());
								});
								countContinus = checkPerMonth ? countContinus + 1 : 0; 
								if(countContinus >= anyCond.getContinuousMonths().get()){
									checkAddAlarm = true;
									checkedValue = countContinus;
								}
							}
						}
					}
					
					break;
				//平均時間、平均回数、平均金額、平均日数
				case AVERAGE_TIME:
				case AVERAGE_TIMES:
				case AVERAGE_AMOUNT:
				case AVERAGE_DAYS:
					float sum = 0 ;
					
					for(MonthlyRecordValuesDto result :lstSidOfMonthData ){
						List<ItemValue> listValue = result.getItemValues().stream()
								.filter(x -> lstAddSub.contains(x.getItemId()) || lstSubStr.contains(x.getItemId())).collect(Collectors.toList());
						for (ItemValue itemValue : listValue) {
							sum += getValue(itemValue);
						}
						
					}	
					avg = sum/mPeriod.yearMonthsBetween().size();
					double av = avg;
					BigDecimal bdAVG = new BigDecimal(avg);
					bdAVG = bdAVG.setScale(2, RoundingMode.HALF_UP);
					/*checkAddAlarm = CompareDouble(bdAVG, new BigDecimal(startValue),
							new BigDecimal(endValue),
							compare);*/
					List<Double> lstT = new ArrayList<>();
					checkAddAlarm = erCondition.checkTarget(item -> {
						if(item.isEmpty()) {
							return new ArrayList<>();
						}
						lstT.add(av);
						return lstT;
					});
					avgUnit = anyCond.getTypeCheckItem().nameId;
					checkedValue = bdAVG.doubleValue();
					break;
				//該当月数時間、該当月数回数、該当月数金額、該当月数日数
				case NUMBER_TIME:
				case NUMBER_TIMES:
				case NUMBER_AMOUNT:
				case NUMBER_DAYS:
					double countCosp = 0 ;
					
					for(MonthlyRecordValuesDto result :lstSidOfMonthData ){
						boolean checkPerMonth = false;
						checkPerMonth = erCondition.checkTarget(item->{
							if (item.isEmpty()) {
								return new ArrayList<>();
							}
							return result.getItemValues().stream().filter(x -> item.contains(x.getItemId())).map(iv -> getValue(iv))
									.collect(Collectors.toList());
						});
						if(checkPerMonth) {
							countCosp += 1;
							lstYM.add(result.getYearMonth().lastGeneralDate().toString("yyyy/MM"));
						}
					}	
					CompareSingleValue<Integer> checkgaitou = new CompareSingleValue<>(anyCond.getCompaOperator().get(), ConditionType.FIXED_VALUE);
					checkgaitou.setValue(anyCond.getNumbers().get());
					checkAddAlarm = checkgaitou.checkWithFixedValue(countCosp,  c -> Double.valueOf(c));
					if(checkAddAlarm) checkedValue = countCosp;
					break;
					
					default:
						break;
					
				} 
				
				if(checkAddAlarm) {
					String txtUnit = getUnit(anyCond); 
					
					if(anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.TIME
							|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.AVERAGE_TIME
							|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.CONTINUOUS_TIME
							|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.NUMBER_TIME) {
						startValue = dataCheckAlarm.timeToString(Integer.valueOf(startValue));
						endValue = endValue == null ? null : dataCheckAlarm.timeToString(Integer.valueOf(endValue));
						if(anyCond.getTypeCheckItem() != TypeCheckWorkRecordMultipleMonth.NUMBER_TIME) checkValue = dataCheckAlarm.timeToString((int) checkedValue);
					}
					
					List<MonthlyAttendanceItemNameDto> addSubName = data.lstItemMond.stream().filter(x -> lstAddSub.contains(x.getAttendanceItemId()))
							.collect(Collectors.toList());
					List<MonthlyAttendanceItemNameDto> subStrName = data.lstItemMond.stream().filter(x -> lstSubStr.contains(x.getAttendanceItemId()))
							.collect(Collectors.toList());
					
					String nameItem = dataCheckAlarm.getNameErrorAlarm(addSubName,	0, "");
					nameItem = dataCheckAlarm.getNameErrorAlarm(subStrName, 1, nameItem);
					String alarmDescription = "";
					CompareOperatorText compareOperatorText = convertComparaToText.convertCompareType(
							erCondition.getCompareSingleValue() != null 
									? erCondition.getCompareSingleValue().getCompareOpertor().value
									: erCondition.getCompareRange().getCompareOperator().value);
					
					if(anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.CONTINUOUS_AMOUNT
							|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.CONTINUOUS_DAYS
							|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.CONTINUOUS_TIME
							|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.CONTINUOUS_TIMES) {
						alarmDescription = TextResource.localize("KAL010_260",
								nameItem,
								compareOperatorText.getCompareLeft(),
								startValue + txtUnit,
								String.valueOf(anyCond.getContinuousMonths().get()));
						checkValue = TextResource.localize("KAL010_289", 
								nameItem + compareOperatorText.getCompareLeft() + startValue + txtUnit, 
								String.valueOf(checkedValue));
					} else if (anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.NUMBER_AMOUNT
							|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.NUMBER_DAYS
							|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.NUMBER_TIME
							|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.NUMBER_TIMES) {
						CompareOperatorText numberCompare = convertComparaToText.convertCompareType(anyCond.getCompaOperator().get().value);
						alarmDescription = TextResource.localize("KAL010_270",								
								anyCond.getTypeCheckItem().nameId +":"+ nameItem,
								compareOperatorText.getCompareLeft(),
								startValue + txtUnit,
								numberCompare.getCompareLeft(),
								String.valueOf(anyCond.getNumbers().get()));
						checkValue = TextResource.localize("KAL010_290", lstYM.toString(), String.valueOf(checkedValue));
					} else {
						if(compare <= 5) {
							alarmDescription = TextResource.localize("KAL010_254",
									nameItem,
									compareOperatorText.getCompareLeft(),
									startValue + txtUnit);
						} else {
							if (compare > 5 && compare <= 7) {
								alarmDescription = TextResource.localize("KAL010_255",
										startValue + txtUnit,
										compareOperatorText.getCompareLeft(),
										nameItem,
										compareOperatorText.getCompareright(),
										endValue + txtUnit);
							} else {
								alarmDescription = TextResource.localize("KAL010_256", 
										startValue + txtUnit,
										compareOperatorText.getCompareLeft(),
										nameItem,
										nameItem,
										compareOperatorText.getCompareright(),
										endValue + txtUnit);
							}
						}
						checkValue = TextResource.localize("KAL010_284", nameItem, checkValue.isEmpty() ? String.valueOf(checkedValue) : checkValue);
						checkValue += txtUnit;
						if(!avgUnit.isEmpty()) {
							alarmDescription = avgUnit +": "+ alarmDescription;
							checkValue = avgUnit + checkValue;
						}
					}
					
					GeneralDate startDate = GeneralDate.ymd(mPeriod.start().year() , mPeriod.start().month(), 1);
					GeneralDate enDate = GeneralDate.ymd(mPeriod.start().year() , mPeriod.start().month() + 1, 1).addDays(-1);
					ExtractionAlarmPeriodDate pDate = new ExtractionAlarmPeriodDate(Optional.ofNullable(startDate), Optional.ofNullable(enDate));
					
					String workplaceId = "";
					Optional<WorkPlaceHistImportAl> optWorkPlaceHistImportAl = 	getWplByListSidAndPeriod.stream().filter(x -> x.getEmployeeId().equals(sid)).findFirst();
					if(optWorkPlaceHistImportAl.isPresent()) {
						Optional<WorkPlaceIdAndPeriodImportAl> optWorkPlaceIdAndPeriodImportAl = optWorkPlaceHistImportAl.get()
								.getLstWkpIdAndPeriod().stream().filter(x -> x.getDatePeriod().start().beforeOrEquals(enDate) 
										&& x.getDatePeriod().end().afterOrEquals(startDate)).findFirst();
						if(optWorkPlaceIdAndPeriodImportAl.isPresent()) {
							workplaceId = optWorkPlaceIdAndPeriodImportAl.get().getWorkplaceId();
						}
					}

					ExtractResultDetail detail = new ExtractResultDetail(
							pDate,
							anyCond.getNameAlarmCon().v(),
							alarmDescription, 
							GeneralDateTime.now(),
							Optional.ofNullable(workplaceId),
							Optional.ofNullable(anyCond.getDisplayMessage().isPresent() ? anyCond.getDisplayMessage().get().v() : null),
							Optional.ofNullable(checkValue)
					);

					List<ExtractResultDetail> details = new ArrayList<>(Arrays.asList(detail));
					if (alarmEmployeeList.stream().anyMatch(i -> i.getEmployeeID().equals(sid))) {
						for (AlarmEmployeeList i : alarmEmployeeList) {
							if (i.getEmployeeID().equals(sid)) {
								List<AlarmExtractInfoResult> tmp = new ArrayList<>(i.getAlarmExtractInfoResults());
								tmp.add(new AlarmExtractInfoResult(
										String.valueOf(anyCond.getCondNo()),
										new AlarmCheckConditionCode(alarmCheckConditionCode),
										AlarmCategory.MULTIPLE_MONTH,
										AlarmListCheckType.FreeCheck,
										details
								));
								i.setAlarmExtractInfoResults(tmp);
								break;
							}
						}
					} else {
						List<AlarmExtractInfoResult> alarmExtractInfoResults = new ArrayList<>(Arrays.asList(
								new AlarmExtractInfoResult(
										String.valueOf(anyCond.getCondNo()),
										new AlarmCheckConditionCode(alarmCheckConditionCode),
										AlarmCategory.MULTIPLE_MONTH,
										AlarmListCheckType.FreeCheck,
										details
								)
						));
						alarmEmployeeList.add(new AlarmEmployeeList(alarmExtractInfoResults, sid));
					}

//					List<ResultOfEachCondition> result = lstResultCondition.stream()
//							.filter(x -> x.getCheckType() == AlarmListCheckType.FreeCheck && x.getNo().equals(String.valueOf(anyCond.getCondNo())))
//							.collect(Collectors.toList());
//					if(result.isEmpty()) {
//						ResultOfEachCondition resultCon = new ResultOfEachCondition(AlarmListCheckType.FixCheck,
//								String.valueOf(anyCond.getCondNo()),
//								new ArrayList<>());
//						resultCon.getLstResultDetail().add(detail);
//						lstResultCondition.add(resultCon);
//					} else {
//						ResultOfEachCondition ex = result.get(0);
//						lstResultCondition.remove(ex);
//						ex.getLstResultDetail().add(detail);
//						lstResultCondition.add(ex);
//					}
				}
			}
		}
		
	}
	/**
	 * 単位
	 * @param anyCond
	 * @return
	 */
	private String getUnit(MulMonthAlarmCheckCond anyCond) {
		String txtUnit = "";
		if(anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.TIME
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.AVERAGE_TIME
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.CONTINUOUS_TIME
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.NUMBER_TIME) {
			 txtUnit = TextResource.localize("KAL010_285");	
		}
		if(anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.TIMES
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.AVERAGE_TIMES
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.CONTINUOUS_TIMES
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.NUMBER_TIMES) {
			txtUnit = TextResource.localize("KAL010_286");
		} 
		if(anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.AMOUNT
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.AVERAGE_AMOUNT
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.CONTINUOUS_AMOUNT
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.NUMBER_AMOUNT) {
			txtUnit = TextResource.localize("KAL010_287");
		} 
		if(anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.DAYS
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.AVERAGE_DAYS
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.CONTINUOUS_DAYS
				|| anyCond.getTypeCheckItem() == TypeCheckWorkRecordMultipleMonth.NUMBER_DAYS) {
			txtUnit = TextResource.localize("KAL010_288");
		}
		return txtUnit;
	}

	private boolean CompareDouble(BigDecimal value, BigDecimal valueAgreementStart, BigDecimal valueAgreementEnd,
			int compare) {
		boolean check = false;
		switch (compare) {
		/* 範囲の間（境界値を含まない）（＜＞） */
		case 6:
			if (value.compareTo(valueAgreementStart) > 0 && value.compareTo(valueAgreementEnd) < 0) {
				check = true;
			}
			break;
		/* 範囲の間（境界値を含む）（≦≧） */
		case 7:
			if (value.compareTo(valueAgreementStart) >= 0 && value.compareTo(valueAgreementEnd) <= 0) {
				check = true;
			}
			break;
		/* 範囲の外（境界値を含まない）（＞＜） */
		case 8:
			if (value.compareTo(valueAgreementStart) < 0 || value.compareTo(valueAgreementEnd) > 0) {
				check = true;
			}
			break;
		/* 範囲の外（境界値を含む）（≧≦） */
		default:
			if (value.compareTo(valueAgreementStart) <= 0 || value.compareTo(valueAgreementEnd) >= 0) {
				check = true;
			}
			break;
		}
		return check;
	}
	
	private Double getValue(ItemValue value) {
		/*if(value.getValueType()==ValueType.DATE){
			return 0d;
		}*/
		if (value.value() == null) {
			return 0d;
		}
		else if (value.getValueType().isDouble()||value.getValueType().isInteger()) {
			return value.getValueType().isDouble() ? ((Double) value.value()) : Double.valueOf((Integer) value.value());
		}
		return 0d;
	}
	public class DataCheck {
		/**	月次の勤怠項目 */
		private List<MonthlyAttendanceItemNameDto> lstItemMond;
		/**月別実績(Work)	 */
		private Map<String, List<MonthlyRecordValuesDto>> lstMonthData;
		/**複数月のアラームチェック条件 */
		private List<MulMonthAlarmCheckCond>  lstAnyCondCheck;
		
		public DataCheck(String cid, List<String> lstSid, YearMonthPeriod mPeriod, List<String> lstAnyConId) {
		
			//ドメインモデル「複数月のアラームチェック条件」を取得する
			lstAnyCondCheck = mulMunCondRepos.getMulCondByUseAtr(lstAnyConId, true);
			List<Integer> lstAttendancCheck = new ArrayList<>();
			lstAnyCondCheck.stream().forEach(x -> {
				lstAttendancCheck.addAll(x.getErAlAttendanceItemCondition().getCountableTarget().getAddSubAttendanceItems().getAdditionAttendanceItems());
				lstAttendancCheck.addAll(x.getErAlAttendanceItemCondition().getCountableTarget().getAddSubAttendanceItems().getSubstractionAttendanceItems());
			});
			//社員ID（List）、期間を設定して月別実績を取得する
			this.lstMonthData = dataCheckAlarm.getMonthData(lstSid, mPeriod,lstAttendancCheck.stream().distinct().collect(Collectors.toList()));
			//月次の勤怠項目を取得する
			this.lstItemMond = attendanceAdapter.getMonthlyAttendanceItem(2);
		}		
	}
}
