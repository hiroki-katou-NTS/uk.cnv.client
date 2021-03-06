package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management;


import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.enums.LaborSystemtAtr;

import java.util.List;
import java.util.Optional;

/**
 * 雇用３６協定時間 IRepository
 */
public interface Employment36HoursRepository {
     void insert(AgreementTimeOfEmployment domain);
     void update(AgreementTimeOfEmployment domain);
     void delete(AgreementTimeOfEmployment domain);
     List<AgreementTimeOfEmployment> getByCid(String cid);
     List<String> findEmploymentSetting(String companyId, LaborSystemtAtr laborSystemAtr);
     Optional<AgreementTimeOfEmployment>getByCidAndCd(String cid, String employCode);
     List<AgreementTimeOfEmployment>find(String cid, String employCode);
     Optional<AgreementTimeOfEmployment>getByCidAndCd(String cid, String employCode,LaborSystemtAtr laborSystemAtr);
     List<AgreementTimeOfEmployment>findByCidAndListCd(String cid, List<String> employCodes);

}
