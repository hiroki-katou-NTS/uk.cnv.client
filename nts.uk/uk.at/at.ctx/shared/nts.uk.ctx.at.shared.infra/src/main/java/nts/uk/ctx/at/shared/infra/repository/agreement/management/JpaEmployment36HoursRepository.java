package nts.uk.ctx.at.shared.infra.repository.agreement.management;


import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfEmployment;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.Employment36HoursRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.enums.LaborSystemtAtr;
import nts.uk.ctx.at.shared.infra.entity.agreement.management.Ksrmt36AgrMgtEmp;
import nts.uk.ctx.at.shared.infra.entity.agreement.management.Ksrmt36AgrMgtEmpPk;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 	Repository: 雇用３６協定時間
 */
@Stateless
public class JpaEmployment36HoursRepository extends JpaRepository implements Employment36HoursRepository {
    private static final String FIND_BY_CID;

    private static final String FIND_BY_CID_AND_CD_LABOR;

    private static final String FIND_EMPLOYMENT_SETTING;

    private static final String FIND_BY_CID_AND_CD;

    private static final String FIND_LIST_EMP_BY_CID_AND_CD;

    private static final String FIND_BY_CID_AND_LIST_CD;


    static {
        StringBuilder builderString = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtEmp a");
        builderString.append(" WHERE a.ksrmt36AgrMgtEmpPk.companyID = :cid ");
        builderString.append(" AND a.ksrmt36AgrMgtEmpPk.employmentCode = :cd ");
        builderString.append(" AND a.ksrmt36AgrMgtEmpPk.laborSystemAtr = :laborSystemAtr ");
        FIND_BY_CID_AND_CD_LABOR = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtEmp a");
        builderString.append(" WHERE a.ksrmt36AgrMgtEmpPk.companyID = :cid ");
        FIND_BY_CID = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(" SELECT a ");
        builderString.append(" FROM Ksrmt36AgrMgtEmp a ");
        builderString.append(" WHERE a.ksrmt36AgrMgtEmpPk.companyID = :companyId ");
        builderString.append(" AND a.ksrmt36AgrMgtEmpPk.laborSystemAtr = :laborSystemAtr ");
        FIND_EMPLOYMENT_SETTING = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtEmp a");
        builderString.append(" WHERE a.ksrmt36AgrMgtEmpPk.companyID = :cid ");
        builderString.append(" AND a.ksrmt36AgrMgtEmpPk.employmentCode = :cd ");
        FIND_BY_CID_AND_CD = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtEmp a");
        builderString.append(" WHERE a.ksrmt36AgrMgtEmpPk.companyID = :cid ");
        builderString.append(" AND a.ksrmt36AgrMgtEmpPk.employmentCode = :cd ");
        FIND_LIST_EMP_BY_CID_AND_CD = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(" SELECT a");
        builderString.append(" FROM Ksrmt36AgrMgtEmp a");
        builderString.append(" WHERE a.ksrmt36AgrMgtEmpPk.companyID = :cid ");
        FIND_BY_CID_AND_LIST_CD = builderString.toString();
    }

    @Override
    public void insert(AgreementTimeOfEmployment domain) {
        this.commandProxy().insert(Ksrmt36AgrMgtEmp.toEntity(domain));
    }

    @Override
    public void update(AgreementTimeOfEmployment domain) {
        this.commandProxy().update(Ksrmt36AgrMgtEmp.toEntity(domain));
    }

    @Override
    public void delete(AgreementTimeOfEmployment domain) {

        val entity = this.queryProxy().find(new Ksrmt36AgrMgtEmpPk(domain.getCompanyId(),domain.getEmploymentCategoryCode().v()
                ,domain.getLaborSystemAtr().value),Ksrmt36AgrMgtEmp.class);
        if(entity.isPresent()){
            this.commandProxy().remove(Ksrmt36AgrMgtEmp.class,new Ksrmt36AgrMgtEmpPk(domain.getCompanyId(),domain.getEmploymentCategoryCode().v()
                    ,domain.getLaborSystemAtr().value));
			this.getEntityManager().flush();
        }
    }

    @Override
    public List<AgreementTimeOfEmployment> getByCid(String cid) {
        return this.queryProxy().query(FIND_BY_CID, Ksrmt36AgrMgtEmp.class)
                .setParameter("cid", cid)
                .getList(Ksrmt36AgrMgtEmp::toDomain);
    }

    @Override
    public Optional<AgreementTimeOfEmployment> getByCidAndCd(String cid, String employCode) {
        return this.queryProxy().query(FIND_BY_CID_AND_CD, Ksrmt36AgrMgtEmp.class)
                .setParameter("cid", cid)
                .setParameter("cd", employCode)
                .getSingle(Ksrmt36AgrMgtEmp::toDomain);
    }

    @Override
    public List<AgreementTimeOfEmployment> find(String cid, String employCode) {
        return this.queryProxy().query(FIND_LIST_EMP_BY_CID_AND_CD, Ksrmt36AgrMgtEmp.class)
                .setParameter("cid", cid)
                .setParameter("cd", employCode)
                .getList(Ksrmt36AgrMgtEmp::toDomain);
    }

    @Override
    public List<AgreementTimeOfEmployment> findByCidAndListCd(String cid, List<String> employCodes) {
        String query = FIND_BY_CID_AND_LIST_CD;
        if (employCodes.size() > 0){
            query += "AND a.ksrmt36AgrMgtEmpPk.employmentCode IN :cds";
        }

        return this.queryProxy().query(query, Ksrmt36AgrMgtEmp.class)
                .setParameter("cid", cid)
                .setParameter("cds", employCodes)
                .getList(Ksrmt36AgrMgtEmp::toDomain);
    }

    @Override
    public List<String> findEmploymentSetting(String companyId, LaborSystemtAtr laborSystemAtr) {

        return this.queryProxy().query(FIND_EMPLOYMENT_SETTING, Ksrmt36AgrMgtEmp.class)
                .setParameter("companyId", companyId).setParameter("laborSystemAtr", laborSystemAtr.value)
                .getList(f -> f.ksrmt36AgrMgtEmpPk.employmentCode);
    }

    @Override
    public Optional<AgreementTimeOfEmployment> getByCidAndCd(String cid, String employCode,LaborSystemtAtr laborSystemAtr) {

        return this.queryProxy().query(FIND_BY_CID_AND_CD_LABOR, Ksrmt36AgrMgtEmp.class)
                .setParameter("cid", cid)
                .setParameter("cd", employCode)
                .setParameter("laborSystemAtr", laborSystemAtr.value)
                .getSingle(Ksrmt36AgrMgtEmp::toDomain);
    }

}
