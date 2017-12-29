package nts.uk.ctx.at.request.infra.repository.application.common;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository_New;
import nts.uk.ctx.at.request.dom.application.Application_New;
import nts.uk.ctx.at.request.infra.entity.application.common.KrqdpApplicationPK_New;
import nts.uk.ctx.at.request.infra.entity.application.common.KrqdtApplication_New;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class JpaApplicationRepository_New extends JpaRepository implements ApplicationRepository_New {
	
	private final String UPDATE = "UPDATE KrqdtApplication_New a "
			+ "SET a.reversionReason = :reversionReason"
			+ ", a.appReason = :appReason"
			+ ", a.stateReflectionReal = :stateReflectionReal"
			+ ", a.version = :version"
			+ " WHERE a.KrqdpApplicationPK_New.appID = :appID AND a.KrqdpApplicationPK_New.companyID = :companyID";
	private final String SELECT_APP = "SELECT c FROM KrqdtApplication_New c "
			+ "WHERE c.employeeID = :applicantSID "
			+ "AND c.appDate = :appDate "
			+ "AND c.prePostAtr = :prePostAtr "
			+ "AND c.appType = :applicationType "
			+ "ORDER BY c.inputDate DESC";
	@Override
	public Optional<Application_New> findByID(String companyID, String appID) {
		return this.queryProxy().find(new KrqdpApplicationPK_New(companyID, appID), KrqdtApplication_New.class)
				.map(x -> x.toDomain());
	}
	@Override
	public List<Application_New> getApp(String applicantSID, GeneralDate appDate, int prePostAtr,
			int appType) {
		return this.queryProxy().query(SELECT_APP, KrqdtApplication_New.class)
				.setParameter("applicantSID", applicantSID)
				.setParameter("appDate", appDate)
				.setParameter("prePostAtr", prePostAtr)
				.setParameter("applicationType", appType)
				.getList(c -> c.toDomain());
	}
	@Override
	public void insert(Application_New application) {
		this.commandProxy().insert(KrqdtApplication_New.fromDomain(application));
		this.getEntityManager().flush();
	}

	@Override
	public void update(Application_New application) {
		this.getEntityManager().createQuery(UPDATE)
			.setParameter("version", application.getVersion())
			.setParameter("companyID", application.getCompanyID())
			.setParameter("appID", application.getAppID())
			.setParameter("reversionReason", application.getReversionReason().v())
			.setParameter("appReason", application.getAppReason().v())
			.setParameter("stateReflectionReal", application.getReflectionInformation().getStateReflectionReal().value)
			.executeUpdate();
		this.getEntityManager().flush();
	}

	@Override
	public void updateWithVersion(Application_New application) {
		KrqdtApplication_New krqdtApplication = this.queryProxy()
			.find(new KrqdpApplicationPK_New(application.getCompanyID(), application.getAppID()), KrqdtApplication_New.class).get();
		krqdtApplication.version = application.getVersion();
		krqdtApplication.reversionReason = application.getReversionReason().v();
		krqdtApplication.appReason = application.getAppReason().v();
		krqdtApplication.stateReflectionReal = application.getReflectionInformation().getStateReflectionReal().value;
		this.commandProxy().update(krqdtApplication);
		this.getEntityManager().flush();
	}
}
