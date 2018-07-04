package nts.uk.ctx.at.function.dom.alarm.alarmlist.monthly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.dom.adapter.ResponseImprovementAdapter;
import nts.uk.ctx.at.function.dom.adapter.checkresultmonthly.CheckResultMonthlyAdapter;
import nts.uk.ctx.at.function.dom.adapter.monthlycheckcondition.ExtraResultMonthlyFunAdapter;
import nts.uk.ctx.at.function.dom.adapter.monthlycheckcondition.FixedExtraMonFunAdapter;
import nts.uk.ctx.at.function.dom.adapter.monthlycheckcondition.FixedExtraMonFunImport;
import nts.uk.ctx.at.function.dom.adapter.sysfixedcheckcondition.SysFixedCheckConMonAdapter;
import nts.uk.ctx.at.function.dom.alarm.AlarmCategory;
import nts.uk.ctx.at.function.dom.alarm.alarmdata.ValueExtractAlarm;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.EmployeeSearchDto;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategory;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategoryRepository;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.monthly.MonAlarmCheckCon;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.monthly.dtoevent.ExtraResultMonthlyDomainEventDto;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureDate;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.calendar.period.DatePeriod;
/**
 * 月次の集計処理
 * @author tutk
 *
 */
@Stateless
public class MonthlyAggregateProcessService {
	
	@Inject
	private AlarmCheckConditionByCategoryRepository alCheckConByCategoryRepo;
	
	@Inject
	private ExtraResultMonthlyFunAdapter extraResultMonthlyFunAdapter;
	
	@Inject
	private FixedExtraMonFunAdapter fixedExtraMonFunAdapter;
	
	@Inject
	private ResponseImprovementAdapter responseImprovementAdapter;
	
	@Inject 
	private SysFixedCheckConMonAdapter sysFixedCheckConMonAdapter;
	
	@Inject
	private CheckResultMonthlyAdapter checkResultMonthlyAdapter;
	
	@Inject
	private ClosureService closureService;
	
	public List<ValueExtractAlarm> monthlyAggregateProcess(String companyID , String  checkConditionCode,DatePeriod period,List<EmployeeSearchDto> employees){
		
		
		
		List<String> employeeIds = employees.stream().map( e ->e.getId()).collect(Collectors.toList());
		
		List<ValueExtractAlarm> listValueExtractAlarm = new ArrayList<>(); 	
		
		
		Optional<AlarmCheckConditionByCategory> alCheckConByCategory =   alCheckConByCategoryRepo.find(companyID, AlarmCategory.MONTHLY.value, checkConditionCode);
		if(!alCheckConByCategory.isPresent()) {
			return Collections.emptyList();
		}
				
		MonAlarmCheckCon monAlarmCheckCon = (MonAlarmCheckCon) alCheckConByCategory.get().getExtractionCondition();
		List<FixedExtraMonFunImport> listFixed = fixedExtraMonFunAdapter.getByEralCheckID(monAlarmCheckCon.getMonAlarmCheckConID());
		List<ExtraResultMonthlyDomainEventDto> listExtra = extraResultMonthlyFunAdapter.getListExtraResultMonByListEralID(monAlarmCheckCon.getArbExtraCon());
		
		//対象者を絞り込む
		DatePeriod endDatePerior = new DatePeriod(period.end(),period.end());
		List<String> listEmployeeID = responseImprovementAdapter.reduceTargetResponseImprovement(employeeIds, endDatePerior, alCheckConByCategory.get().getExtractTargetCondition());
		
		
		//対象者の件数をチェック : 対象者　≦　0
		if(listEmployeeID.isEmpty()) {
			return Collections.emptyList();
		}
		List<EmployeeSearchDto> employeesDto = employees.stream().filter(c-> listEmployeeID.contains(c.getId())).collect(Collectors.toList());
		//tab 2
		listValueExtractAlarm.addAll(this.extractMonthlyFixed(listFixed, period, employeesDto));
		//tab 3
		
		listValueExtractAlarm.addAll(this.extraResultMonthly(companyID, listExtra, period, employeesDto));
		
		return listValueExtractAlarm;
	}
	//tab 2
	private List<ValueExtractAlarm> extractMonthlyFixed(List<FixedExtraMonFunImport> listFixed,
			DatePeriod period, List<EmployeeSearchDto> employees) {
		List<ValueExtractAlarm> listValueExtractAlarm = new ArrayList<>();
		List<YearMonth> lstYearMonth = period.yearMonthsBetween();
		GeneralDate lastDateInPeriod = period.end();
		
		
		
		for(EmployeeSearchDto employee : employees) {
			//社員(list)に対応する処理締めを取得する(get closing xử lý đối ứng với employee (List))
			Closure closure = closureService.getClosureDataByEmployee(employee.getId(), GeneralDate.today());
			int closureID= closure.getClosureId().value;
			ClosureDate closureDate = null;
			for(ClosureHistory ClosureHistory :closure.getClosureHistories() ) {
				GeneralDate endDateYearMonthly = GeneralDate.fromString(String.valueOf(ClosureHistory.getEndYearMonth().v())+String.valueOf(ClosureHistory.getEndYearMonth().lastDateInMonth()), "yyyy/MM/DD");
				GeneralDate startDateYearMonthly = GeneralDate.fromString(String.valueOf(ClosureHistory.getStartYearMonth().v())+"01", "yyyy/MM/DD");
				if(lastDateInPeriod.beforeOrEquals(endDateYearMonthly) && lastDateInPeriod.afterOrEquals(startDateYearMonthly)){
					closureDate = ClosureHistory.getClosureDate();
					break;
				}
			}
			for (YearMonth yearMonth : lstYearMonth) {
				for(int i = 0;i<listFixed.size();i++) {
					if(listFixed.get(i).isUseAtr()) {
//						FixedExtraMonFunImport fixedExtraMon = listFixed.get(i);
						switch(i) {
							case 0 :
								Optional<ValueExtractAlarm> unconfirmed = sysFixedCheckConMonAdapter.checkMonthlyUnconfirmed(employee.getId(), yearMonth.v().intValue());
								if(unconfirmed.isPresent()) {
									unconfirmed.get().setAlarmValueMessage(listFixed.get(i).getMessage());
									unconfirmed.get().setWorkplaceID(Optional.ofNullable(employee.getWorkplaceId()));
									String dateString = unconfirmed.get().getAlarmValueDate().substring(0, 7);
									unconfirmed.get().setAlarmValueDate(dateString);
									listValueExtractAlarm.add(unconfirmed.get());
								}
								
							break;
							case 1 :break;//chua co
							case 2 :break;//chua co
							case 3 :break;//chua co
							case 4 :
								Optional<ValueExtractAlarm> agreement = sysFixedCheckConMonAdapter.checkAgreement(employee.getId(), yearMonth.v().intValue(),closureID,closureDate);
								if(agreement.isPresent()) {
									agreement.get().setAlarmValueMessage(listFixed.get(i).getMessage());
									agreement.get().setWorkplaceID(Optional.ofNullable(employee.getWorkplaceId()));
									String dateAgreement = agreement.get().getAlarmValueDate().substring(0, 7);
									agreement.get().setAlarmValueDate(dateAgreement);
									listValueExtractAlarm.add(agreement.get());
								}
							break;
							default : break; // so 6 : chua co
						}//end switch
						
					}
						
				}
			}
			
		}
		
		
		return listValueExtractAlarm;
	}
	
	
	//tab 3
	private List<ValueExtractAlarm> extraResultMonthly(String companyId,List<ExtraResultMonthlyDomainEventDto> listExtra,
			DatePeriod period, List<EmployeeSearchDto> employees) {
		List<ValueExtractAlarm> listValueExtractAlarm = new ArrayList<>(); 
		List<YearMonth> lstYearMonth = period.yearMonthsBetween();
		GeneralDate lastDateInPeriod = period.end();
		
		
		
		for (ExtraResultMonthlyDomainEventDto extra : listExtra) {
			for (YearMonth yearMonth : lstYearMonth) {
				for (EmployeeSearchDto employee : employees) {
					
					//社員(list)に対応する処理締めを取得する(get closing xử lý đối ứng với employee (List))
					Closure closure = closureService.getClosureDataByEmployee(employee.getId(), GeneralDate.today());
					int closureID= closure.getClosureId().value;
					ClosureDate closureDate = null;
					for(ClosureHistory ClosureHistory :closure.getClosureHistories() ) {
						GeneralDate endDateYearMonthly = GeneralDate.fromString(String.valueOf(ClosureHistory.getEndYearMonth().v())+String.valueOf(ClosureHistory.getEndYearMonth().lastDateInMonth()), "yyyy/MM/DD");
						GeneralDate startDateYearMonthly = GeneralDate.fromString(String.valueOf(ClosureHistory.getStartYearMonth().v())+"01", "yyyy/MM/DD");
						if(lastDateInPeriod.beforeOrEquals(endDateYearMonthly) && lastDateInPeriod.afterOrEquals(startDateYearMonthly)){
							closureDate = ClosureHistory.getClosureDate();
							break;
						}
					}
					
					
					
					switch (extra.getTypeCheckItem()) {
					case 0:
//						boolean checkPublicHoliday = checkResultMonthlyAdapter.checkPublicHoliday(companyId, employee.getCode(), employee.getId(),
//								employee.getWorkplaceId(), true, yearMonth, extra.getSpecHolidayCheckCon());
//						if(checkPublicHoliday) {
//							ValueExtractAlarm resultMonthlyValue = new ValueExtractAlarm(
//									employee.getWorkplaceId(),
//									employee.getId(),
//									yearMonth.toString(),
//									TextResource.localize("KAL010_100"),
//									TextResource.localize("KAL010_209"),
//									TextResource.localize("KAL010_210"),
//									extra.getDisplayMessage()
//									);
//							listValueExtractAlarm.add(resultMonthlyValue);
//						}
						break;
					case 1:
						//TODO : chua biet date là gì
						GeneralDate date = GeneralDate.today();
						boolean checkAgreementError = checkResultMonthlyAdapter.check36AgreementCondition(employee.getId(),
								yearMonth,closureID,closureDate,extra.getAgreementCheckCon36());
						if(checkAgreementError) {
							ValueExtractAlarm resultMonthlyValue = new ValueExtractAlarm(
									employee.getWorkplaceId(),
									employee.getId(),
									yearMonth.toString(),
									TextResource.localize("KAL010_100"),
									TextResource.localize("KAL010_204"),
									//TODO : còn thiếu
									TextResource.localize("KAL010_205"),
									
									extra.getDisplayMessage()
									);
							listValueExtractAlarm.add(resultMonthlyValue);
						}
						break;
					case 2:
						boolean checkAgreementAlarm = checkResultMonthlyAdapter.check36AgreementCondition(employee.getId(),
								yearMonth,closureID,closureDate,extra.getAgreementCheckCon36());
						if(checkAgreementAlarm) {
							ValueExtractAlarm resultMonthlyValue = new ValueExtractAlarm(
									employee.getWorkplaceId(),
									employee.getId(),
									yearMonth.toString(),
									TextResource.localize("KAL010_100"),
									TextResource.localize("KAL010_206"),
									//TODO : còn thiếu
									TextResource.localize("KAL010_207"),
									
									extra.getDisplayMessage()
									);
							listValueExtractAlarm.add(resultMonthlyValue);
						}
						if(true) {
							
						}
						break;
					case 3 :
						break;
					case 4 : 
						//Chưa có, chưa thiết kế	
						break;
					default:
						boolean checkPerTimeMonActualResult = checkResultMonthlyAdapter.checkPerTimeMonActualResult(
								yearMonth, 1, 1, employee.getId(), extra.getCheckConMonthly());
						if(checkPerTimeMonActualResult) {
							if(extra.getTypeCheckItem() ==8) {
								ValueExtractAlarm resultMonthlyValue = new ValueExtractAlarm(
										employee.getWorkplaceId(),
										employee.getId(),
										yearMonth.toString(),
										TextResource.localize("KAL010_100"),
										TextResource.localize("KAL010_60"),
										//TODO : còn thiếu
										TextResource.localize("KAL010_207"),//fix tạm
										
										extra.getDisplayMessage()
										);
								listValueExtractAlarm.add(resultMonthlyValue);
							}else {
								String nameItem = "";
								
								switch(extra.getTypeCheckItem()) {
								case 4 :
									nameItem = TextResource.localize("KAL010_47");
									break;
								case 5 :
									nameItem = TextResource.localize("KAL010_113");
									break;
								case 6 :
									nameItem = TextResource.localize("KAL010_50");
									break;
								case 7 :
									nameItem = TextResource.localize("KAL010_51");
									break;
								default : break;
								}
								ValueExtractAlarm resultMonthlyValue = new ValueExtractAlarm(
										employee.getWorkplaceId(),
										employee.getId(),
										yearMonth.toString(),
										TextResource.localize("KAL010_100"),
										nameItem,
										//TODO : còn thiếu
										TextResource.localize("KAL010_207"),//fix tạm
										
										extra.getDisplayMessage()
										);
								listValueExtractAlarm.add(resultMonthlyValue);
							}
						}
						
						break;
					}
					
					//this.checkPublicHoliday(companyId,employee.getWorkplaceCode(), employee.getId(),employee.getWorkplaceId(), true, yearMonth);
				}
			}
		}
		
		return listValueExtractAlarm;
	}
		

}
