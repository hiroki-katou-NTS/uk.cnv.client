package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import java.util.List;
/**
 * 
 * @author Doan Duy Hung
 *
 */
public interface PremiumItemRepository {
	
	 void update(PremiumItem premiumItem);
	
	 List<PremiumItem> findByCompanyID(String companyID);
	
	 List<PremiumItem> findByCompanyIDAndDisplayNumber(String companyID, List<Integer> displayNumbers);
	
	 List<PremiumItem> findByCompanyIDAndListPremiumNo (String companyID, List<Integer> premiumNos);
	
	 List<PremiumItem> findAllIsUse (String companyID);
}
