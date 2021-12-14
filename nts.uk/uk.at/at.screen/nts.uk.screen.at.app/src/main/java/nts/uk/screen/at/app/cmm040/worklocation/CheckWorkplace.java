package nts.uk.screen.at.app.cmm040.worklocation;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocation;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocationRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 職場をチェックする
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM040_勤務場所の登録.A：勤務場所の登録.メニュー別OCD.職場をチェックする
 * @author chungnt
 *
 */

@Stateless
public class CheckWorkplace {
	
	@Inject
	private WorkLocationRepository worklocationRepo; 
	
	
	public void checkWorkPlace(String workplaceID) {
		List<WorkLocation> result = worklocationRepo.findByWorkPlace(
				AppContexts.user().contractCode(),
				AppContexts.user().companyId(),
				workplaceID);
		if (!result.isEmpty()){
			if (result.size() > 0) {
				throw new BusinessException("Msg_2212");
			}
		}
	}
}
