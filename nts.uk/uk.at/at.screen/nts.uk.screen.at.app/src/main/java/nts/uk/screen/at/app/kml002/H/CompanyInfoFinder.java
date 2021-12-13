package nts.uk.screen.at.app.kml002.H;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSetting;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSettingRepository;
import nts.uk.shr.com.context.AppContexts;
/**
 * 
 * @author hoangnd
 *
 */
@Stateless
// 会社の初期情報を取得する
public class CompanyInfoFinder {

	@Inject
	private EstimatedCompanyInfoFinder estimatedInfoFinder;
	
	@Inject
	private CriterionAmountUsageSettingRepository criterionAmountUsageSettingRepository;
	
	
	/**
	 * 初期情報を取得する
	 */
	public InitCompanyInfoDto getInitInfo() {
		String cid = AppContexts.user().companyId();
		// 1: call
		Optional<CriterionAmountUsageSetting> criOptional = criterionAmountUsageSettingRepository.get(cid);
		
		// 2: call
		EsimatedInfoDto esimatedInfoDto = estimatedInfoFinder.getEstimatedInfo();
		
		return new InitCompanyInfoDto(
				esimatedInfoDto,
				criOptional.map(x -> BooleanUtils.toBoolean(x.getEmploymentUse().value)).orElse(null));
	}
	
	
	
	
	
	
}
