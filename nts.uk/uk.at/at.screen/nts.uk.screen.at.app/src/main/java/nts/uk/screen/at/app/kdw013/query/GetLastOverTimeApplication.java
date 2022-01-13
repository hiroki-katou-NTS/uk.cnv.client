package nts.uk.screen.at.app.kdw013.query;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTimeRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.申請.残業休出申請.残業申請.App.最新の事前残業申請を取得する.最新の事前残業申請を取得する
 */
@Stateless
public class GetLastOverTimeApplication {
	
	@Inject
	private ApplicationRepository appRepo;
	
	@Inject
	private AppOverTimeRepository appOverRepo;
	
	/**
	 * 取得する
	 * 
	 * @param sId 社員ID
	 * @param date 年月日
	 * @return Optional<残業申請Dto>
	 */
	public Optional<AppOverTime> get(String sId, GeneralDate date) {
		
		Optional<AppOverTime> appOverTime = Optional.empty();
		// 1.get
		Optional<Application> OptApp = this.appRepo.getApplicationBySIDs(Arrays.asList(sId), date, date).stream()
				.filter(x -> x.getAppType().equals(ApplicationType.OVER_TIME_APPLICATION) && x.getPrePostAtr().equals(PrePostAtr.PREDICT))
				.sorted(Comparator.comparing(Application::getInputDate).reversed())
				.findFirst();
		
		if (OptApp.isPresent()) {
			appOverTime =  this.appOverRepo.find(AppContexts.user().companyId(), OptApp.get().getAppID());
		}
		return appOverTime;
	}
}
