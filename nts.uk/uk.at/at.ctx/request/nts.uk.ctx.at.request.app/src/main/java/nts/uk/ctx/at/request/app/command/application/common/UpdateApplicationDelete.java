package nts.uk.ctx.at.request.app.command.application.common;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.uk.ctx.at.request.dom.application.common.Application;
import nts.uk.ctx.at.request.dom.application.common.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.AfterProcessDelete;
import nts.uk.shr.com.context.AppContexts;
@Stateless
@Transactional
public class UpdateApplicationDelete {
	@Inject
	private ApplicationRepository appRepo;
	
	@Inject
	private AfterProcessDelete afterProcessDelete;
	
	public List<String> deleteApp(String appID) {
		String companyID = AppContexts.user().companyId();

		Application application = appRepo.getAppById(companyID, appID).get();
		
		//5.2(hieult)
		return afterProcessDelete.screenAfterDelete(companyID, appID);
		
		//refresh man hinh, k hien thi don xin da xoa 
		
		
	}
}
