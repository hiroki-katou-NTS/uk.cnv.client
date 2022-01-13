package nts.uk.ctx.at.record.infra.repository.confirmemployment;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmployment;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmploymentRepository;
import nts.uk.ctx.at.record.infra.entity.confirmemployment.KrcmtRestrictConfirmEmployment;
import nts.uk.ctx.at.record.infra.entity.confirmemployment.KrcmtRestrictConfirmEmploymentPk;

/**
 * refactor 5
 * @author Doan Duy Hung
 *
 */
@Stateless
public class JpaRestrictConfirmEmploymentRepository extends JpaRepository implements RestrictConfirmEmploymentRepository {

	@Override
	public Optional<RestrictConfirmEmployment> findByCompanyID(String companyID) {
		String sql = "select * from KRCMT_WORK_FIXED_CTR where CID = @companyID";
		return new NtsStatement(sql, this.jdbcProxy())
				.paramString("companyID", companyID)
				.getSingle(rec -> {
					return new RestrictConfirmEmployment(
							rec.getString("CID"), 
							rec.getBoolean("USAGE_ATR"));
		});
	}

	@Override
	public void add(RestrictConfirmEmployment domain) {
		this.commandProxy().insert(KrcmtRestrictConfirmEmployment.toEntity(domain));
	}

	@Override
	public void update(RestrictConfirmEmployment domain) {
		KrcmtRestrictConfirmEmployment newRestrictConfirmEmployment = KrcmtRestrictConfirmEmployment.toEntity(domain);
		KrcmtRestrictConfirmEmployment updateRestrictConfirmEmployment = 
				this.queryProxy().find(newRestrictConfirmEmployment.restrictConfirmEmploymentPk, KrcmtRestrictConfirmEmployment.class).orElse(null);
        if (null == updateRestrictConfirmEmployment) {
        	this.add(domain);
            return;
        }
        updateRestrictConfirmEmployment.usageAtr = newRestrictConfirmEmployment.usageAtr;
        this.commandProxy().update(updateRestrictConfirmEmployment);
	}

	@Override
	public void remove(String cid) {
		this.commandProxy().remove(KrcmtRestrictConfirmEmployment.class, new KrcmtRestrictConfirmEmploymentPk(cid)); 
	}

}
