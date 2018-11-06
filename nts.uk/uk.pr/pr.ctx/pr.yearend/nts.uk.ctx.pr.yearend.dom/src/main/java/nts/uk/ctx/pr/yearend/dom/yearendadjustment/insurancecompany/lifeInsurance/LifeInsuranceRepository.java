package nts.uk.ctx.pr.yearend.dom.yearendadjustment.insurancecompany.lifeInsurance;

import java.util.Optional;
import java.util.List;

/**
* 生命保険情報
*/
public interface LifeInsuranceRepository
{

    List<LifeInsurance> getAllLifeInsurance();

    List<LifeInsurance> getLifeInsuranceBycId(String cid);

    Optional<LifeInsurance> getLifeInsuranceById(String cid, String lifeInsuranceCode);

    void add(LifeInsurance domain);

    void update(LifeInsurance domain);

    void remove(String cid, String lifeInsuranceCode);

    void removeLifeInsurance(String cid, String lifeInsuranceCode);

}
