package nts.uk.ctx.exio.infra.repository.exi.execlog;

import java.util.Optional;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.exio.infra.entity.exi.execlog.OiodtExAcExecLog;
import nts.uk.ctx.exio.infra.entity.exi.execlog.OiomtExacExeResultLogPk;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.ctx.exio.dom.exi.execlog.ExacExeResultLogRepository;
import nts.uk.ctx.exio.dom.exi.execlog.ExtExecutionMode;
import nts.uk.ctx.exio.dom.exi.execlog.ExtResultStatus;
import nts.uk.ctx.exio.dom.exi.execlog.ProcessingFlg;
import nts.uk.ctx.exio.dom.exi.execlog.StandardFlg;
import nts.uk.ctx.exio.dom.exi.condset.SystemType;
import nts.uk.ctx.exio.dom.exi.execlog.ExacExeResultLog;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;

@Stateless
public class JpaExacExeResultLogRepository extends JpaRepository implements ExacExeResultLogRepository
{

    private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM OiodtExAcExecLog f";
    private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING + " WHERE  f.pk.cid =:cid"
    		+ " AND  f.pk.conditionSetCd =:conditionSetCd"
    		+ " AND  f.pk.externalProcessId =:externalProcessId ";
    private static final String SELECT_BY_PROCESS_ID = SELECT_ALL_QUERY_STRING + " WHERE f.pk.externalProcessId =:externalProcessId";
    
    private static final String SELECT_BY_STAND_SYSTEM = SELECT_ALL_QUERY_STRING + " WHERE f.pk.cid =:cid AND f.standardAtr = 0 AND f.systemType in :listSystem "
    		+ "AND f.processStartDatetime between :startDate and :endDate";
    
    

    @Override
    public List<ExacExeResultLog> getAllExacExeResultLog(String cid, List<Integer> listSystem, GeneralDateTime startDate, GeneralDateTime endDate){
        return this.queryProxy().query(SELECT_BY_STAND_SYSTEM, OiodtExAcExecLog.class)
        		.setParameter("cid", cid)
                .setParameter("listSystem", listSystem)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getList(item -> toDomain(item));
    }

    @Override
    public Optional<ExacExeResultLog> getExacExeResultLogById(String cid, String conditionSetCd, String externalProcessId){
        return this.queryProxy().query(SELECT_BY_KEY_STRING, OiodtExAcExecLog.class)
        .setParameter("cid", cid)
        .setParameter("conditionSetCd", conditionSetCd)
        .setParameter("externalProcessId", externalProcessId)
        .getSingle(c->toDomain(c));
    }

    @Override
    public void add(ExacExeResultLog domain){
        this.commandProxy().insert(toEntity(domain));
        this.getEntityManager().flush();
    }

    @Override
    public void update(ExacExeResultLog domain){
        OiodtExAcExecLog newExacExeResultLog = toEntity(domain);
        OiodtExAcExecLog updateExacExeResultLog = this.queryProxy().find(newExacExeResultLog.pk,
        		OiodtExAcExecLog.class).get();
        if (null == updateExacExeResultLog) {
            return;
        }
        updateExacExeResultLog.executorId = newExacExeResultLog.executorId;
        updateExacExeResultLog.userId = newExacExeResultLog.userId;
        updateExacExeResultLog.processStartDatetime = newExacExeResultLog.processStartDatetime;
        updateExacExeResultLog.standardAtr = newExacExeResultLog.standardAtr;
        updateExacExeResultLog.executeForm = newExacExeResultLog.executeForm;
        updateExacExeResultLog.targetCount = newExacExeResultLog.targetCount;
        updateExacExeResultLog.errorCount = newExacExeResultLog.errorCount;
        updateExacExeResultLog.fileName = newExacExeResultLog.fileName;
        updateExacExeResultLog.systemType = newExacExeResultLog.systemType;
        updateExacExeResultLog.resultStatus = newExacExeResultLog.resultStatus;
        updateExacExeResultLog.processEndDatetime = newExacExeResultLog.processEndDatetime;
        updateExacExeResultLog.processAtr = newExacExeResultLog.processAtr;
        this.commandProxy().update(updateExacExeResultLog);
    }

    @Override
    public void remove(String cid, String conditionSetCd, String externalProcessId){
        this.commandProxy().remove(OiodtExAcExecLog.class, new OiomtExacExeResultLogPk(cid, conditionSetCd, externalProcessId)); 
    }

    private static ExacExeResultLog toDomain(OiodtExAcExecLog entity) {
    	ExacExeResultLog domain = new ExacExeResultLog(entity.pk.cid,
    			entity.pk.conditionSetCd,
    			entity.pk.externalProcessId,
    			entity.executorId,
    			entity.userId,
    			entity.processStartDatetime,
    			EnumAdaptor.valueOf(entity.standardAtr, StandardFlg.class),
    			EnumAdaptor.valueOf(entity.executeForm ? 1 : 0, ExtExecutionMode.class),
    			entity.targetCount,
    			entity.errorCount,
    			entity.fileName,
    			EnumAdaptor.valueOf(entity.systemType, SystemType.class),
    			Optional.ofNullable(entity.resultStatus == null ? null : EnumAdaptor.valueOf(entity.resultStatus, ExtResultStatus.class)),
    			Optional.ofNullable(entity.processEndDatetime),
    			EnumAdaptor.valueOf(entity.processAtr, ProcessingFlg.class));
    	return domain;
    }

    private OiodtExAcExecLog toEntity(ExacExeResultLog domain) {
    	OiomtExacExeResultLogPk pk = new OiomtExacExeResultLogPk(domain.getCid(),
    			domain.getConditionSetCd(),
    			domain.getExternalProcessId());
    	OiodtExAcExecLog entity = new OiodtExAcExecLog(pk,
    			AppContexts.user().contractCode(),
    			domain.getExecutorId(),
    			domain.getUserId(),
    			domain.getProcessStartDatetime(),
    			domain.getStandardAtr().value,
    			domain.getExecuteForm().value == 1,
    			domain.getTargetCount(),
    			domain.getErrorCount(),
    			domain.getFileName(),
    			domain.getSystemType().value,
    			domain.getResultStatus().isPresent() ? domain.getResultStatus().get().value : null,
    			domain.getProcessEndDatetime().isPresent() ? domain.getProcessEndDatetime().get() : null,
    			domain.getProcessAtr().value);
        return entity;
    }

	/* (non-Javadoc)
	 * @see nts.uk.ctx.exio.dom.exi.execlog.ExacExeResultLogRepository#getExacExeResultLogByProcessId(java.lang.String)
	 */
	@Override
	public List<ExacExeResultLog> getExacExeResultLogByProcessId(String externalProcessId) {
		 return this.queryProxy().query(SELECT_BY_PROCESS_ID, OiodtExAcExecLog.class)
				 .setParameter("externalProcessId", externalProcessId)
	                .getList(item -> toDomain(item));
	}
    

}
