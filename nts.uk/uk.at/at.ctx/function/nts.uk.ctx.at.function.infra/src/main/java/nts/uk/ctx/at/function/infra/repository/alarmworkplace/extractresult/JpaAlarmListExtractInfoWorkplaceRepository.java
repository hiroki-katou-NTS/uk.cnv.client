package nts.uk.ctx.at.function.infra.repository.alarmworkplace.extractresult;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.function.dom.alarmworkplace.extractresult.AlarmListExtractInfoWorkplace;
import nts.uk.ctx.at.function.dom.alarmworkplace.extractresult.AlarmListExtractInfoWorkplaceRepository;
import nts.uk.ctx.at.function.infra.entity.alarmworkplace.extractresult.KfndtAlarmExtractWpl;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Stateless
public class JpaAlarmListExtractInfoWorkplaceRepository extends JpaRepository implements AlarmListExtractInfoWorkplaceRepository {

    private static final String SELECT;

    private static final String FIND_BY_ID;


    static {
        StringBuilder builderString = new StringBuilder();
        builderString.append(" SELECT a FROM KfndtAlarmExtractWpl a");
        SELECT = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(SELECT);
        builderString.append(" WHERE a.processId = :processId ");
        FIND_BY_ID = builderString.toString();
    }

    @Override
    public void addAll(List<AlarmListExtractInfoWorkplace> domains) {
        commandProxy().insertAll(KfndtAlarmExtractWpl.toEntity(domains));
    }

    @Override
    public List<AlarmListExtractInfoWorkplace> getById(String processId) {
        List<KfndtAlarmExtractWpl> result = this.queryProxy().query(FIND_BY_ID, KfndtAlarmExtractWpl.class)
                .setParameter("processId", processId)
                .getList();
        result.sort(Comparator.comparing(i -> i.workplaceCode));
        return KfndtAlarmExtractWpl.toDomain(result);
    }
}
