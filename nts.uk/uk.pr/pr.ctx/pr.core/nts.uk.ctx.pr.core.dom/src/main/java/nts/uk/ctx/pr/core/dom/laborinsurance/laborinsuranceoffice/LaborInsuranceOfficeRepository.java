package nts.uk.ctx.pr.core.dom.laborinsurance.laborinsuranceoffice;

import java.util.Optional;
import java.util.List;

/**
* 労働保険事業所
*/
public interface LaborInsuranceOfficeRepository
{

    List<LaborInsuranceOffice> getAllLaborInsuranceOffice();

    Optional<LaborInsuranceOffice> getLaborInsuranceOfficeById();

    void add(LaborInsuranceOffice domain);

    void update(LaborInsuranceOffice domain);

    void remove();

}
