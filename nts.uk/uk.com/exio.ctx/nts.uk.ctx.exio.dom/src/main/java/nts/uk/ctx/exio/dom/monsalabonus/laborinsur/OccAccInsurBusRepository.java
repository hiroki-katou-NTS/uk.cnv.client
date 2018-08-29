package nts.uk.ctx.exio.dom.monsalabonus.laborinsur;

import java.util.List;
import java.util.Optional;

/**
* 労災保険事業
*/
public interface OccAccInsurBusRepository
{

    List<OccAccInsurBus> getAllOccAccInsurBus();

    Optional<OccAccInsurBus> getOccAccInsurBusById(String cid, int occAccInsurBusNo);

    void add(OccAccInsurBus domain);

    void update(OccAccInsurBus domain);

    void remove(String cid, int occAccInsurBusNo);

}
