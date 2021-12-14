package nts.uk.ctx.at.shared.infra.repository.agreement.management;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.Classification36AgreementTimeRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfClassification;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.enums.LaborSystemtAtr;
import nts.uk.ctx.at.shared.infra.entity.agreement.management.Ksrmt36AgrMgtCls;
import nts.uk.ctx.at.shared.infra.entity.agreement.management.Ksrmt36AgrMgtClsPk;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Optional;

/**
 * 	Repository	 分類３６協定時間
 */
@Stateless
public class JpaClassification36AgreementTimeRepository extends JpaRepository implements Classification36AgreementTimeRepository {
    private static final String FIND_BY_CID;
    private static final String FIND_BY_CID_AND_CODE;
    private static final String FIND_BY_CID_AND_CLS_CD;
    private static final String FIND_BY_CID_AND_LIST_CD;
    private static final String FIND_BY_LABORSYSTEM;



    static {
        StringBuilder builderString  = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtCls a");
        builderString.append(" WHERE a.ksrmt36AgrMgtClsPk.companyID = :cid ");
        FIND_BY_CID = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtCls a");
        builderString.append(" WHERE a.ksrmt36AgrMgtClsPk.companyID = :cid ");
        builderString.append(" AND a.ksrmt36AgrMgtClsPk.classificationCode = :classificationCode ");
        builderString.append(" AND a.ksrmt36AgrMgtClsPk.laborSystemAtr = :laborSystemAtr ");
        FIND_BY_CID_AND_CLS_CD = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtCls a");
        builderString.append(" WHERE a.ksrmt36AgrMgtClsPk.companyID = :cid ");
        builderString.append(" AND a.ksrmt36AgrMgtClsPk.classificationCode = :classificationCode ");
        FIND_BY_CID_AND_CODE = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtCls a");
        builderString.append(" WHERE a.ksrmt36AgrMgtClsPk.companyID = :cid ");
        FIND_BY_CID_AND_LIST_CD = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtCls a");
        builderString.append(" WHERE a.ksrmt36AgrMgtClsPk.companyID = :cid ");
        builderString.append(" AND a.ksrmt36AgrMgtClsPk.laborSystemAtr = :laborSystemAtr ");
        FIND_BY_LABORSYSTEM = builderString.toString();
    }
    @Override
    public void insert(AgreementTimeOfClassification domain) {
        this.commandProxy().insert(Ksrmt36AgrMgtCls.toEntity(domain));
        this.getEntityManager().flush();
    }

    @Override
    public void update(AgreementTimeOfClassification domain) {
        this.commandProxy().update(Ksrmt36AgrMgtCls.toEntity(domain));
    }

    @Override
    public void delete(AgreementTimeOfClassification domain) {
        val entity = this.queryProxy().find(new Ksrmt36AgrMgtClsPk(domain.getCompanyId(),domain.getClassificationCode().v()
                ,domain.getLaborSystemAtr().value),Ksrmt36AgrMgtCls.class);
        if(entity.isPresent()){
            this.commandProxy().remove(Ksrmt36AgrMgtCls.class,new Ksrmt36AgrMgtClsPk(domain.getCompanyId(),domain.getClassificationCode().v()
                    ,domain.getLaborSystemAtr().value));
			this.getEntityManager().flush();
        }
    }

    @Override
    public List<AgreementTimeOfClassification> getByCid(String cid) {
        return this.queryProxy().query(FIND_BY_CID,Ksrmt36AgrMgtCls.class).setParameter("cid",cid).getList(Ksrmt36AgrMgtCls::toDomain);
    }

    @Override
    public Optional<AgreementTimeOfClassification> getByCidAndClassificationCode(String cid, String classificationCode,LaborSystemtAtr laborSystemAtr) {
        return this.queryProxy().query(FIND_BY_CID_AND_CLS_CD,Ksrmt36AgrMgtCls.class)
                .setParameter("cid",cid)
                .setParameter("classificationCode",classificationCode)
                .setParameter("laborSystemAtr",laborSystemAtr.value)
                .getSingle(Ksrmt36AgrMgtCls::toDomain);

    }

    @Override
    public List<String> findClassificationCodes(String cid,LaborSystemtAtr laborSystemAtr) {
        return this.queryProxy().query(FIND_BY_LABORSYSTEM, Ksrmt36AgrMgtCls.class)
                .setParameter("cid",cid)
                .setParameter("laborSystemAtr", laborSystemAtr.value)
                .getList(f -> f.ksrmt36AgrMgtClsPk.classificationCode);
    }

    @Override
    public List<AgreementTimeOfClassification> find(String cid, String classificationCode) {
        return this.queryProxy().query(FIND_BY_CID_AND_CODE,Ksrmt36AgrMgtCls.class)
                .setParameter("cid",cid)
                .setParameter("classificationCode",classificationCode)
                .getList(Ksrmt36AgrMgtCls::toDomain);

    }

    @Override
    public List<AgreementTimeOfClassification> findCidAndLstCd(String cid, List<String> classificationCodes) {
        String query = FIND_BY_CID_AND_LIST_CD;
        if (classificationCodes.size() > 0){
            query += "AND a.ksrmt36AgrMgtClsPk.classificationCode IN :cds";
        }

        return this.queryProxy().query(query,Ksrmt36AgrMgtCls.class)
                .setParameter("cid",cid)
                .setParameter("cds",classificationCodes)
                .getList(Ksrmt36AgrMgtCls::toDomain);
    }


}
