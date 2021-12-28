package nts.uk.ctx.at.record.infra.repository.workrecord.operationsetting;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import lombok.SneakyThrows;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcessRepository;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDayFuncControl;

@Stateless
public class JpaApprovalProcessRepository extends JpaRepository implements ApprovalProcessRepository
{

    private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM KrcmtDayFuncControl f";
    private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING + " WHERE  f.dayFuncControlPk.cid =:cid ";

    @Override
    public List<ApprovalProcess> getAllApprovalProcess(){
    	List<ApprovalProcess> data = this.queryProxy().query(SELECT_ALL_QUERY_STRING, KrcmtDayFuncControl.class)
    			.getList(c -> c.toDomainApprovalProcess());
    	
        return data;
    }

    @Override
    @SneakyThrows
    public Optional<ApprovalProcess> getApprovalProcessById(String cid){
    	return this.queryProxy().query(SELECT_BY_KEY_STRING, KrcmtDayFuncControl.class)
    	        .setParameter("cid", cid)
    	        .getSingle(c->c.toDomainApprovalProcess());
    }
}
