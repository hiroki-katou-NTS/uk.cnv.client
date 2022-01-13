package nts.uk.ctx.at.aggregation.app.find.schedulecounter.employmentsetting;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSetting;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSettingRepository;

/**
 * 利用単位の設定を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.目安金額.App.利用単位の設定を取得する
 * @author hoangdd
 *
 */

@Stateless
public class EmploymentUsageSettingFinder {

	@Inject
	private CriterionAmountUsageSettingRepository criterionAmountUsageSettingRepository;
	
	public CriterionAmountUsageSettingDto getSetting(String cid) {
		CriterionAmountUsageSettingDto usageSettingDto = new CriterionAmountUsageSettingDto();
		Optional<CriterionAmountUsageSetting> optional = criterionAmountUsageSettingRepository.get(cid);
		
		if(optional.isPresent()) {
			usageSettingDto = CriterionAmountUsageSettingDto.fromDomain(optional.get());
		}		
		return usageSettingDto;
	}
	
}
