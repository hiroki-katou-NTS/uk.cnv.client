package nts.uk.ctx.at.record.infra.repository.workrecord.operationsetting;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import lombok.SneakyThrows;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcessRepository;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDayFuncControl;

@Stateless
public class JpaIdentityProcessRepository extends JpaRepository implements IdentityProcessRepository
{

    private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM KrcmtDayFuncControl f";
    private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING + " WHERE  f.dayFuncControlPk.cid =:cid ";

    @Override
    public List<IdentityProcess> getAllIdentityProcess(){
        return this.queryProxy().query(SELECT_ALL_QUERY_STRING, KrcmtDayFuncControl.class)
                .getList(item -> item.toDomainIdentityProcess());
    }

    @Override
    @SneakyThrows
    public Optional<IdentityProcess> getIdentityProcessById(String cid){
    	return this.queryProxy().query(SELECT_BY_KEY_STRING, KrcmtDayFuncControl.class)
    	        .setParameter("cid", cid)
    	        .getSingle(c->c.toDomainIdentityProcess());
    }
}
