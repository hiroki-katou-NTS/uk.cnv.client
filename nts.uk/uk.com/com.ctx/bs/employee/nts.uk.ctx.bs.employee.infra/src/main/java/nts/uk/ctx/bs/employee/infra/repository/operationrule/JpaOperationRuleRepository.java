package nts.uk.ctx.bs.employee.infra.repository.operationrule;

import java.util.Optional;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.employee.dom.operationrule.DepartmentWorkplaceSynchronizationAtr;
import nts.uk.ctx.bs.employee.dom.operationrule.OperationRule;
import nts.uk.ctx.bs.employee.dom.operationrule.OperationRuleRepository;
import nts.uk.ctx.bs.employee.infra.entity.operationrule.BsystOperationRule;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
public class JpaOperationRuleRepository extends JpaRepository implements OperationRuleRepository {
	@Override
	public Optional<OperationRule> getOperationRule(String companyId) {
		return this.queryProxy().find(companyId, BsystOperationRule.class).map(BsystOperationRule::toDomain);
	}
	
	/**
	 * 旧テーブル廃止対応
	 * 旧ドメインを返すための一時的処理。
	 */
	@Override
	public Optional<OperationRule> findOperationRule(String companyId) {
		val entity = getOperationRule(companyId);
		if(entity.isPresent()) {
			return Optional.of(new OperationRule(entity.get().getCompanyId(),
					DepartmentWorkplaceSynchronizationAtr.of(entity.get().getDepWkpSynchAtr().value)));
		}
		return Optional.empty();
	}

	@Override
	public void update(OperationRule operationRule) {
		BsystOperationRule entity = BsystOperationRule.fromDomain(operationRule);
		Optional<BsystOperationRule> oldOpt = this.queryProxy().find(entity.companyId, BsystOperationRule.class);
		if (!oldOpt.isPresent()) {
			insert(operationRule);
			return;
		}
		BsystOperationRule oldEntity = oldOpt.get();
		 oldEntity.depWkpSynchAtr = entity.depWkpSynchAtr;
		 this.commandProxy().update(oldEntity);
	}

	@Override
	/**
	 * 旧ドメイン使用側からのInsert用
	 */
	public void insert(OperationRule operationRule) {
		BsystOperationRule entity =BsystOperationRule.fromDomain(operationRule);
		this.commandProxy().insert(entity);
	}
	
	@Override
	/**
	 * 旧ドメイン使用側からの削除用
	 */
	public void delete(String companyId, String companyCode, String contractCd) {
		this.commandProxy().remove(BsystOperationRule.class, companyId);
	}
}
