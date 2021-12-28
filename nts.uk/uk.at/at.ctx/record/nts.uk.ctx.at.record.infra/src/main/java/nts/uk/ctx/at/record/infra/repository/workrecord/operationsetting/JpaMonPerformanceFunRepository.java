package nts.uk.ctx.at.record.infra.repository.workrecord.operationsetting;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.MonPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.MonPerformanceFunRepository;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtMonPerformanceFun;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtMonPerformanceFunPk;

@Stateless
public class JpaMonPerformanceFunRepository extends JpaRepository implements MonPerformanceFunRepository
{

    private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM KrcmtMonPerformanceFun f";
    private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING + " WHERE  f.monPerformanceFunPk.cid =:cid ";

    @Override
    public List<MonPerformanceFun> getAllMonPerformanceFun(){
        return this.queryProxy().query(SELECT_ALL_QUERY_STRING, KrcmtMonPerformanceFun.class)
                .getList(item -> item.toDomain());
    }

    @Override
    public Optional<MonPerformanceFun> getMonPerformanceFunById(String cid){
        return this.queryProxy().query(SELECT_BY_KEY_STRING, KrcmtMonPerformanceFun.class)
        .setParameter("cid", cid)
        .getSingle(c->c.toDomain());
    }

    @Override
    public void add(MonPerformanceFun domain){
        this.commandProxy().insert(KrcmtMonPerformanceFun.toEntity(domain));
    }

    @Override
    public void update(MonPerformanceFun domain){
        KrcmtMonPerformanceFun newMonPerformanceFun = KrcmtMonPerformanceFun.toEntity(domain);
        KrcmtMonPerformanceFun updateMonPerformanceFun = this.queryProxy().find(newMonPerformanceFun.monPerformanceFunPk, KrcmtMonPerformanceFun.class).orElse(null);
        if (null == updateMonPerformanceFun) {
        	this.add(domain);
            return;
        }
        updateMonPerformanceFun.comment = newMonPerformanceFun.comment;
        updateMonPerformanceFun.dailySelfChkDispAtr = newMonPerformanceFun.dailySelfChkDispAtr;
        this.commandProxy().update(updateMonPerformanceFun);
    }

    @Override
    public void remove(String cid){
        this.commandProxy().remove(KrcmtMonPerformanceFun.class, new KrcmtMonPerformanceFunPk(cid)); 
    }
}
