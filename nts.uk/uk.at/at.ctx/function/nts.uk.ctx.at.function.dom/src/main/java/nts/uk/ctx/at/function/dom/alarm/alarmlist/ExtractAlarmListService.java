package nts.uk.ctx.at.function.dom.alarm.alarmlist;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.function.dom.adapter.employeebasic.EmployeeBasicInfoFnImport;
import nts.uk.ctx.at.function.dom.adapter.employeebasic.SyEmployeeFnAdapter;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkPlaceInforExport;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.at.function.dom.alarm.AlarmPatternSetting;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess.AggregationProcessService;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess.AlarmListResultDto;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategory;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.CheckCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmEmployeeList;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmExtractInfoResult;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.ExtractResultDetail;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.PersistenceAlarmListExtractResult;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Stateless
public class ExtractAlarmListService {

	@Inject
	private SyEmployeeFnAdapter empAdapter;
	@Inject
	private WorkplaceAdapter wplAdapter;
	@Inject
	private AggregationProcessService aggregationProcessService;
	
	public ExtractedAlarmDto extractAlarm(List<EmployeeSearchDto> listEmployee, String checkPatternCode,
			List<PeriodByAlarmCategory> periodByCategory, String runCode) {
		String companyID = AppContexts.user().companyId();
		// チェック条件に当てはまらない場合(When it does not fit the check condition)
		// 条件を満たしていない
		if (listEmployee.isEmpty()) {
			// エラーメッセージ(#Msg_834)を表示する
			throw new BusinessException("Msg_834");	
		}
		List<String> lstSid = listEmployee.stream().map(x -> x.getId()).collect(Collectors.toList());
		AtomicInteger counter = new AtomicInteger(lstSid.size());
		// 勤務実績のアラームリストの集計処理を行う
		ExtractedAlarmDto extractData = this.extractResultAlarm(companyID,
				checkPatternCode,
				periodByCategory,
				lstSid,
                runCode,
				null,
				new ArrayList<>(), finished -> {
					counter.set(counter.get() + finished);
				},
				() -> {
					return false;
				});
		// 集計データが無い場合
		List<AlarmExtraValueWkReDto> listAlarmExtraValueWR = extractData.getExtractedAlarmData();
		if (listAlarmExtraValueWR.isEmpty()) {
			// 情報メッセージ(#Msg_835) を表示する
			return  new ExtractedAlarmDto(new ArrayList<>(), false, true, extractData.getPersisAlarmExtractResult(),
					extractData.getAlarmExtractConditions());
		}
		// 集計データがある場合
		// B画面 ダイアログ「アラームリスト」を起動する
		return new ExtractedAlarmDto(listAlarmExtraValueWR, false, false, extractData.getPersisAlarmExtractResult(),
				extractData.getAlarmExtractConditions());

	}
	/**
	 * アラームリストを出力する (削除する予定）
	 * @param listEmployee
	 * @param periodByCategory
	 * @param eralCate
	 * @param checkConList
	 * @param counter
	 * @param shouldStop
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ExtractedAlarmDto extractAlarmV2(List<EmployeeSearchDto> listEmployee,
			List<PeriodByAlarmCategory> periodByCategory, List<AlarmCheckConditionByCategory> eralCate,
			List<CheckCondition> checkConList, Consumer<Integer> counter, Supplier<Boolean> shouldStop) {
		
		String companyID = AppContexts.user().companyId();
		
		// チェック条件に当てはまらない場合(When it does not fit the check condition)
		// 条件を満たしていない
		if (listEmployee.isEmpty()) {
			// エラーメッセージ(#Msg_834)を表示する
			return  new ExtractedAlarmDto(new ArrayList<>(), false, true, null, new ArrayList<>());
		}
		
		// ドメインモデル「アラームリスト抽出処理状況」を作成する
//		GeneralDateTime now1 = GeneralDateTime.now();
		GeneralDate today = GeneralDate.today();
		
		// 勤務実績のアラームリストの集計処理を行う
		List<AlarmExtraValueWkReDto> listAlarmExtraValueWR = aggregationProcessService.processAlarmListWorkRecordV2(today, companyID, listEmployee,
				periodByCategory, eralCate, checkConList, counter, shouldStop);
		
		
		// 集計結果を確認する sort list
		Comparator<AlarmExtraValueWkReDto> comparator = Comparator.comparing(AlarmExtraValueWkReDto::getHierarchyCd)
																	.thenComparing(Comparator.comparing(AlarmExtraValueWkReDto::getEmployeeCode))
																	.thenComparing(Comparator.comparing(AlarmExtraValueWkReDto::getAlarmValueDate))
																	.thenComparing(Comparator.comparing(AlarmExtraValueWkReDto::getCategory));
		List<AlarmExtraValueWkReDto> sortedAlarmExtraValue = listAlarmExtraValueWR.stream().sorted(comparator).collect(Collectors.toList());
		// 集計データが無い場合
		if (listAlarmExtraValueWR.isEmpty()) {
			// 情報メッセージ(#Msg_835) を表示する
			return  new ExtractedAlarmDto(new ArrayList<>(), false, true, null, new ArrayList<>());
		}
		// 集計データがある場合
		// B画面 ダイアログ「アラームリスト」を起動する
		return new ExtractedAlarmDto(sortedAlarmExtraValue, false, false, null, new ArrayList<>());

	}
	/**
	 * アラームリストを出力する
	 * @param cid 会社ID
	 * @param pattentCd　パターンコード
	 * @param lstCategoryPeriod　List<カテゴリ別期間>
	 * @param lstSid　List<従業員>
	 * @param runCode　自動実行コード　（Default：　”Z”）
	 * @param alarmPattern 自動変更の場合NULLを渡す
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ExtractedAlarmDto extractResultAlarm(String cid, String pattentCd,
			List<PeriodByAlarmCategory> lstCategoryPeriod,List<String> lstSid, String runCode,
			AlarmPatternSetting alarmPattern, List<AlarmCheckConditionByCategory> lstCondCate,
			Consumer<Integer> counter, Supplier<Boolean> shouldStop) {
		ExtractedAlarmDto result = new ExtractedAlarmDto();
		if (lstSid.isEmpty()) {
			// エラーメッセージ(#Msg_834)を表示する
			throw new BusinessException("Msg_834");	
		}
		AlarmListResultDto alarmResult = aggregationProcessService.processAlarmListResult(cid, 
				pattentCd,
				lstCategoryPeriod,
				lstSid,
				runCode,
				alarmPattern,
				lstCondCate,
				counter,
				shouldStop);
		result.setExtracting(alarmResult.isExtracting());
		result.setNullData(true);
		List<AlarmExtraValueWkReDto> lstExtractedAlarmData = new ArrayList<>();
		//個人情報を取得
		List<EmployeeBasicInfoFnImport> findBySIds = empAdapter.findBySIds(lstSid);
		List<String> lstWpl = new ArrayList<>();
		PersistenceAlarmListExtractResult persisExtractResult = alarmResult.getPersisAlarmExtractResult();
		result.setPersisAlarmExtractResult(persisExtractResult);
		result.setAlarmExtractConditions(alarmResult.getAlarmExtractConditions());
		persisExtractResult.getAlarmListExtractResults().stream().forEach(x -> {
			x.getAlarmExtractInfoResults().stream().forEach(y -> {
				y.getExtractionResultDetails().stream().forEach(z -> {
					if (z.getWpID().isPresent()) {
						lstWpl.add(z.getWpID().get());
					}
				});
			});
		});

		//職場情報を取得
		List<WorkPlaceInforExport> getWorkplaceInforByWkpIds = wplAdapter.getWorkplaceInforByWkpIds(cid, lstWpl, GeneralDate.today());
		if(persisExtractResult.getAlarmListExtractResults().isEmpty()) {
			result.setNullData(false);
		}

		if (!persisExtractResult.getAlarmListExtractResults().isEmpty()) {
			for (int a = 0; a < persisExtractResult.getAlarmListExtractResults().size(); a++) {
				AlarmEmployeeList x = persisExtractResult.getAlarmListExtractResults().get(a);
				if (x.getAlarmExtractInfoResults().isEmpty()) {
					continue;
				}
				for (int i = 0; i < x.getAlarmExtractInfoResults().size(); i++) {
					AlarmExtractInfoResult y = x.getAlarmExtractInfoResults().get(i);
					List<ExtractResultDetail> lstResultDetail = y.getExtractionResultDetails();
					for (int j = 0; j < lstResultDetail.size(); j++) {
						ExtractResultDetail z = lstResultDetail.get(j);
						AlarmExtraValueWkReDto alarmValue = new AlarmExtraValueWkReDto();
						EmployeeBasicInfoFnImport empInfo = findBySIds.stream().filter(e -> e.getEmployeeId().equals(x.getEmployeeID()))
								.collect(Collectors.toList()).get(0);

						if(z.getWpID().isPresent()) {
							List<WorkPlaceInforExport> lstWplInfo = getWorkplaceInforByWkpIds.stream().filter(d -> d.getWorkplaceId().equals(z.getWpID().get()))
									.collect(Collectors.toList());
							if(!lstWplInfo.isEmpty()) {
								WorkPlaceInforExport wplInfo  = lstWplInfo.get(0);
								alarmValue.setHierarchyCd(wplInfo.getHierarchyCode());
								alarmValue.setWorkplaceID(wplInfo.getWorkplaceId());
								alarmValue.setWorkplaceName(wplInfo.getWorkplaceName());
							}
						}
						alarmValue.setAlarmItem(z.getAlarmName());
						alarmValue.setAlarmValueDate(z.getPeriodDate().getStartDate().isPresent() ? z.getPeriodDate().getStartDate().get().toString() : GeneralDate.today().toString());
						alarmValue.setComment(z.getMessage().isPresent() ? z.getMessage().get() : "");
						alarmValue.setAlarmValueMessage(z.getAlarmContent());
						alarmValue.setCategory(y.getAlarmCategory().value);
						alarmValue.setCategoryName(y.getAlarmCategory().nameId);
						alarmValue.setCheckedValue(z.getCheckValue().isPresent() ? z.getCheckValue().get() : "");
						alarmValue.setEmployeeCode(empInfo.getEmployeeCode());
						alarmValue.setEmployeeID(empInfo.getEmployeeId());
						alarmValue.setEmployeeName(empInfo.getPName());
						alarmValue.setEndDate(z.getPeriodDate().getEndDate().isPresent() ? z.getPeriodDate().getEndDate().get().toString() : "");
						lstExtractedAlarmData.add(alarmValue);
						alarmValue.setGuid(IdentifierUtil.randomUniqueId());
					}
				}
			}
		}
		Comparator<AlarmExtraValueWkReDto> comparator = Comparator.comparing(AlarmExtraValueWkReDto::getHierarchyCd)
				.thenComparing(Comparator.comparing(AlarmExtraValueWkReDto::getEmployeeCode))
				.thenComparing(Comparator.comparing(AlarmExtraValueWkReDto::getAlarmValueDate))
				.thenComparing(Comparator.comparing(AlarmExtraValueWkReDto::getCategory));
		List<AlarmExtraValueWkReDto> sortedAlarmExtraValue = lstExtractedAlarmData.stream().sorted(comparator).collect(Collectors.toList());		
		result.setExtractedAlarmData(sortedAlarmExtraValue);
		return result;
		
	}
	
	private List<AlarmExtraValueWkReDto> sortExtractedResult(List<AlarmExtraValueWkReDto> lstAlarmResult){
		return lstAlarmResult.stream().sorted((a, b) -> {
			Integer wpl = a.getHierarchyCd().compareTo(b.getHierarchyCd());
			if(wpl == 0) {
				Integer rs = a.getCategory().compareTo(b.getCategory());
				if (rs == 0) {
					Integer rs2 = a.getEmployeeCode().compareTo(b.getEmployeeCode());
					if (rs2 == 0) {
						if(a.getAlarmValueDate() != null) {
							GeneralDate dateA = GeneralDate.fromString(a.getAlarmValueDate(), "yyyy/MM/dd");
							GeneralDate dateB = GeneralDate.fromString(b.getAlarmValueDate(), "yyyy/MM/dd");
							Integer rs3 = dateA.compareTo(dateB);
		                    if(rs3 == 0){
		                        return dateA.compareTo(dateB);
		                    }else{
		                        return rs3;
		                    }
						} else {
							return 0;
						}
	                    
					} else {
						return rs2;
					}
				} else {
					return rs;
				}
			} else {
				return wpl;
			}
			
		}).collect(Collectors.toList());
	}

}
