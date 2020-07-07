package nts.uk.ctx.at.request.infra.repository.setting.workplace.requestbycompany;

import java.util.Arrays;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApplicationUseSetting;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApprovalFunctionSet;
import nts.uk.ctx.at.request.dom.setting.workplace.requestbycompany.RequestByCompanyRepository;

/**
 * refactor 4
 * @author Doan Duy Hung
 *
 */
@Stateless
public class JpaRequestByCompanyRepository extends JpaRepository implements RequestByCompanyRepository {

	@Override
	public Optional<ApprovalFunctionSet> findByAppType(String companyID, ApplicationType appType) {
		String sql = "select * from KRQST_APP_APV_CMP where CID = @companyID and APP_TYPE = @appType";
		
		return new NtsStatement(sql, this.jdbcProxy())
				.paramString("companyID", companyID)
				.paramInt("appType", appType.value)
				.getSingle(rec -> {
					ApplicationUseSetting applicationUseSetting = ApplicationUseSetting.createNew(
							rec.getInt("USE_ATR"), 
							rec.getInt("APP_TYPE"), 
							rec.getString("MEMO"));
					return new ApprovalFunctionSet(Arrays.asList(applicationUseSetting));	
				});
	}

}
