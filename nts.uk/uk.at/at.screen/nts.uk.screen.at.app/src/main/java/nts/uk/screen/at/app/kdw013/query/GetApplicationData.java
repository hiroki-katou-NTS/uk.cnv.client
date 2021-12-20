package nts.uk.screen.at.app.kdw013.query;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.申請.App.申請データを取得する
 * @author tutt
 * <<Query>>
 */
@Stateless
public class GetApplicationData {
	
	@Inject
	private ApplicationRepository appRepo;
	
	public List<Application> get(String employeeID, int appType, GeneralDate appDate, int prePostAtr) {
		
		return appRepo.getAllApplicationByAppTypeAndPrePostAtr(employeeID, appType, appDate, prePostAtr);
		
	}

}
