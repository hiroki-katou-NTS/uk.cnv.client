package nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.app.find.budget.external.ExternalBudgetDto;
import nts.uk.ctx.at.schedule.app.find.budget.external.query.GetExternalBudgetPerformanceItems;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu.ksu001q.query.DailyExternalBudget;
import nts.uk.screen.at.app.ksu.ksu001q.query.DailyExternalBudgetDto;
import nts.uk.screen.at.app.ksu.ksu001q.query.DailyExternalBudgetQuery;
import nts.uk.shr.com.context.AppContexts;
/**
 * 外部予算実績を取得する
 * @author hoangnd
 *
 */
@Stateless
public class ScreenQueryExternalBudgetPerformance {
	
	@Inject
	private GetExternalBudgetPerformanceItems getExternalBudgetPerformanceItems;
	
	@Inject
	private DailyExternalBudgetQuery dailyExternalBudgetQuery;

	/**
	 * 
	 * @param targetOrg 対象組織識別情報
	 * @param datePeriod 期間
	 */
	public Map<GeneralDate, Map<ExternalBudgetDto, String>> aggrerate(
			TargetOrgIdenInfor targetOrg,
			DatePeriod datePeriod
			) {
		
		Map<GeneralDate, Map<ExternalBudgetDto, String>> output = new HashMap<GeneralDate, Map<ExternalBudgetDto, String>>();
		Map<String, Map<ExternalBudgetDto, String>> map = 
				datePeriod.datesBetween()
				  .stream()
				  .map(x -> x.toString())
				  .collect(Collectors.toMap(x -> x, x -> new HashMap<ExternalBudgetDto, String>()));
		
		String companyId = AppContexts.user().companyId();
		//1: 取得する()
		List<ExternalBudgetDto> externalBudgets = getExternalBudgetPerformanceItems.getByComanyID(companyId);
		
		// not List<外部予算実績項目>.isEmpty
		if (!CollectionUtil.isEmpty(externalBudgets)) {
			// 取得する(対象組織識別情報, 外部予算実績項目コード, 外部予算実績受入値年月日)
			externalBudgets.forEach(x -> {
							   DailyExternalBudget param = new DailyExternalBudget();
							   param.setUnit(String.valueOf(targetOrg.getUnit().value));
							   param.setId(targetOrg.getUnit()==TargetOrganizationUnit.WORKPLACE ? targetOrg.getWorkplaceId().orElse("") : targetOrg.getWorkplaceGroupId().orElse(""));
							   param.setStartDate(datePeriod.start().toString());
							   param.setEndDate(datePeriod.end().toString());
							   param.setItemCode(x.getExternalBudgetCode());
							   List<DailyExternalBudgetDto> dailyExternalBudgets = dailyExternalBudgetQuery.getDailyExternalBudget(param); 
							   if (CollectionUtil.isEmpty(dailyExternalBudgets)) {
								   map
								   	.entrySet()
								   	.stream()
								   	.collect(Collectors.toMap(Map.Entry::getKey, y -> {
								   		y.getValue().put(x, "");
								   		return y.getValue();
								   	}));
							   } else {
								   
								   for (DailyExternalBudgetDto dto: dailyExternalBudgets) {
//								   GeneralDate date = GeneralDate.fromString(dto.getDate(), "yyyy/MM/dd");
									   String date = dto.getDate();
									   if (map.containsKey(date)) {
										   Map<ExternalBudgetDto, String> value = map.get(date);
										   value.put(x, dto.getValue());
									   } else {
										   Map<ExternalBudgetDto, String> value = new HashMap<ExternalBudgetDto, String>();
										   value.put(x, dto.getValue());
										   map.put(date, value);
									   }
								   }
							   }
							   
						   });
			return map.entrySet()
			 	.stream()
			 	.collect(Collectors.toMap(
			 			e -> GeneralDate.fromString(e.getKey(), "yyyy/MM/dd"),
			 			Map.Entry::getValue)
	 			);
		}
		
		return output;
		
	}
}
