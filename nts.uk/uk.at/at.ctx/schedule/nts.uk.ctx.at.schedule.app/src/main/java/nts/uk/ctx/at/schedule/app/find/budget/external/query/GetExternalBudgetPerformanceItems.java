package nts.uk.ctx.at.schedule.app.find.budget.external.query;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.schedule.app.find.budget.external.ExternalBudgetDto;
import nts.uk.ctx.at.schedule.dom.budget.external.ExternalBudget;
import nts.uk.ctx.at.schedule.dom.budget.external.ExternalBudgetRepository;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.予実集計.スケジュール集計.予算.外部予算.App.アルゴリズム.Query.外部予算実績項目を取得する
 * @author HieuLt
 * 外部予算実績項目を取得する
 */
@Stateless
public class GetExternalBudgetPerformanceItems {
	
	@Inject
	private ExternalBudgetRepository repo;
	public List<ExternalBudgetDto> getByComanyID(String companyId){
		//1ドメインモデル「外部予算実績項目」を取得する
		List<ExternalBudget> data = repo.findAll(companyId);
		List<ExternalBudgetDto> reslut = data.stream()
				.map(x -> {
					return new ExternalBudgetDto(x.getExternalBudgetCd().toString(), x.getExternalBudgetName().toString(), x.getBudgetAtr().value, x.getUnitAtr().value);
				}).collect(Collectors.toList());
		return reslut;
	}
	 
}
