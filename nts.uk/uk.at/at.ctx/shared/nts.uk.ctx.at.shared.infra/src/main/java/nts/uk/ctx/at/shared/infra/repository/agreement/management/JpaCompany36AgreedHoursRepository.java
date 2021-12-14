package nts.uk.ctx.at.shared.infra.repository.agreement.management;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfCompany;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.Company36AgreedHoursRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.enums.LaborSystemtAtr;
import nts.uk.ctx.at.shared.infra.entity.agreement.management.Ksrmt36AgrMgtCmp;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Optional;

@Stateless
public class JpaCompany36AgreedHoursRepository extends JpaRepository implements Company36AgreedHoursRepository {
    private static final String FIND_BY_CID_AND_LABORSYSTEM;
    private static final String FIND_BY_CID;
    static {
        StringBuilder builderString = new StringBuilder();

        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtCmp a");
        builderString.append(" WHERE a.ksrmt36AgrMgtCmpPk.companyID = :cid");
        builderString.append(" AND a.ksrmt36AgrMgtCmpPk.laborSystemAtr = :laborSystemAtr");
        FIND_BY_CID_AND_LABORSYSTEM = builderString.toString();

        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtCmp a");
        builderString.append(" WHERE a.ksrmt36AgrMgtCmpPk.companyID = :cid");
        FIND_BY_CID = builderString.toString();
    }
    @Override
    public void insert(AgreementTimeOfCompany domain) {
        this.commandProxy().insert(Ksrmt36AgrMgtCmp.toEntity(domain));
    }

    @Override
    public void update(AgreementTimeOfCompany domain) {

        this.commandProxy().update(Ksrmt36AgrMgtCmp.toEntity(domain));
    }

    @Override
    public Optional<AgreementTimeOfCompany> getByCid(String cid, LaborSystemtAtr laborSystemAtr) {
        return this.queryProxy().query(FIND_BY_CID_AND_LABORSYSTEM, Ksrmt36AgrMgtCmp.class)
                .setParameter("cid",cid)
				.setParameter("laborSystemAtr",laborSystemAtr.value)
                .getSingle(Ksrmt36AgrMgtCmp::toDomain);

    }

    @Override
    public List<AgreementTimeOfCompany> find(String cid) {
        return this.queryProxy().query(FIND_BY_CID, Ksrmt36AgrMgtCmp.class)
                .setParameter("cid",cid)
                .getList(Ksrmt36AgrMgtCmp::toDomain);
    }

}
