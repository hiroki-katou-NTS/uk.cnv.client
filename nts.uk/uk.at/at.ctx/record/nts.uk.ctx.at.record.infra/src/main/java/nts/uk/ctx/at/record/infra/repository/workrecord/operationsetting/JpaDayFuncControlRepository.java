package nts.uk.ctx.at.record.infra.repository.workrecord.operationsetting;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DayFuncControlRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcess;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDayFuncControl;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDayFuncControlPk;

@Stateless
public class JpaDayFuncControlRepository extends JpaRepository implements DayFuncControlRepository{

	@Override
	public void add(DaiPerformanceFun daiPerformanceFun,
			IdentityProcess identityProcess, ApprovalProcess approvalProcess) {
		this.commandProxy().insert(KrcmtDayFuncControl.toEntity(daiPerformanceFun, identityProcess, approvalProcess));
	}

	@Override
	public void update(DaiPerformanceFun daiPerformanceFun,
			IdentityProcess identityProcess, ApprovalProcess approvalProcess) {
		KrcmtDayFuncControl newEntity = KrcmtDayFuncControl.toEntity(daiPerformanceFun, identityProcess, approvalProcess);
		KrcmtDayFuncControl updateEntity = this.queryProxy().find(newEntity.dayFuncControlPk, KrcmtDayFuncControl.class).orElse(null);
        if (null == updateEntity) {
        	this.add(daiPerformanceFun, identityProcess, approvalProcess);
            return;
        }
        updateEntity.comment = newEntity.comment;
        updateEntity.disp36Atr = newEntity.disp36Atr;
        updateEntity.flexDispAtr = newEntity.flexDispAtr;
        updateEntity.checkErrRefDisp = newEntity.checkErrRefDisp;
        updateEntity.daySelfChk = newEntity.daySelfChk;
        updateEntity.monSelfChk = newEntity.monSelfChk;
        updateEntity.daySelfChkError = newEntity.daySelfChkError;
        updateEntity.dayBossChk = newEntity.dayBossChk;
        updateEntity.monBossChk = newEntity.monBossChk;
        updateEntity.dayBossChkError = newEntity.dayBossChkError;
        this.commandProxy().update(updateEntity);
	}

	@Override
	public void remove(String cid) {
		this.commandProxy().remove(KrcmtDayFuncControl.class, new KrcmtDayFuncControlPk(cid)); 
	}
}
