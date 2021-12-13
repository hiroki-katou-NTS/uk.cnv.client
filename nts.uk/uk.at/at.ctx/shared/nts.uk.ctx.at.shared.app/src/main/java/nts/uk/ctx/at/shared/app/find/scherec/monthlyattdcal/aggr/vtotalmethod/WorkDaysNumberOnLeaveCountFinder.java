package nts.uk.ctx.at.shared.app.find.scherec.monthlyattdcal.aggr.vtotalmethod;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCount;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCountRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class WorkDaysNumberOnLeaveCountFinder {

	@Inject
	private WorkDaysNumberOnLeaveCountRepository repository;

	/**
	 * UKDesign.UniversalK.就業.KDW_日別実績.KMF_休暇マスタ.KMF001_休暇の設定.N:特別休暇の設定.アルゴリズム.特別休暇の設定の起動.特別休暇の設定の起動
	 */
	public WorkDaysNumberOnLeaveCountDto findByCid() {
		WorkDaysNumberOnLeaveCount domain = this.repository.findByCid(AppContexts.user().companyId());
		return WorkDaysNumberOnLeaveCountDto.fromDomain(domain);
	}
}
