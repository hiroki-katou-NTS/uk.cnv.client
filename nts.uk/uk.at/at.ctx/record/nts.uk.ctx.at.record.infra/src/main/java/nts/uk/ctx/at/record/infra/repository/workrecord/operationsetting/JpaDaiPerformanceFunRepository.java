package nts.uk.ctx.at.record.infra.repository.workrecord.operationsetting;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFunRepository;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDayFuncControl;

@Stateless
public class JpaDaiPerformanceFunRepository extends JpaRepository implements DaiPerformanceFunRepository
{

    private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM KrcmtDayFuncControl f";
    private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING + " WHERE  f.dayFuncControlPk.cid =:cid ";

    @Override
    public List<DaiPerformanceFun> getAllDaiPerformanceFun(){
        return this.queryProxy().query(SELECT_ALL_QUERY_STRING, KrcmtDayFuncControl.class)
                .getList(item -> item.toDomainDaiPerformanceFun());
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public Optional<DaiPerformanceFun> getDaiPerformanceFunById(String cid){
        return this.queryProxy().query(SELECT_BY_KEY_STRING, KrcmtDayFuncControl.class)
	        .setParameter("cid", cid)
	        .getSingle(c->c.toDomainDaiPerformanceFun());
    }
    
}
