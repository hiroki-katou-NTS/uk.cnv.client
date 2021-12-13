package nts.uk.ctx.at.function.infra.repository.alarm.appapproval;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.appapproval.AppApprovalAlarmCheckCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.appapproval.AppApprovalFixedCheckItem;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.appapproval.AppApprovalFixedExtractCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.appapproval.AppApprovalFixedExtractConditionRepository;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.appapproval.ErrorAlarmMessage;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.appapproval.KrqmtAppApprovalCondition;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.appapproval.KrqmtAppApprovalFixedExtractCondition;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.appapproval.KrqmtAppApprovalFixedExtractConditionPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaAppApprovalFixedExtractConditionRepository extends JpaRepository 
	implements AppApprovalFixedExtractConditionRepository {
	

	@Override
	public List<AppApprovalFixedExtractCondition> findAll(List<String> extractConditionIds, boolean useAtr) {
		String query = "SELECT a FROM KrqmtAppApprovalFixedExtractCondition a WHERE a.useAtr = :useAtr AND a.pk.erAlId IN :ids";
		List<KrqmtAppApprovalFixedExtractCondition> results = new ArrayList<>();
		
		CollectionUtil.split(extractConditionIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			results.addAll(this.queryProxy().query(query, KrqmtAppApprovalFixedExtractCondition.class)
					.setParameter("useAtr", useAtr).setParameter("ids", subList).getList());
		});
		
		return results.stream().map(a -> toDomain(a))
				.collect(Collectors.toList());
	}
	

	@Override
	public Optional<AppApprovalAlarmCheckCondition> findByCodeAndCategory(String companyId, String code, int category) {
		String query= "SELECT a FROM KrqmtAppApprovalCondition a WHERE a.companyId = :cid AND a.checkConditionCode = :code AND a.category = :category";
		return this.queryProxy().query(query, KrqmtAppApprovalCondition.class)
				.setParameter("cid", companyId)
				.setParameter("code", code)
				.setParameter("category", category)
				.getSingle().map(e -> new AppApprovalAlarmCheckCondition(e.getId()));
	}


	@Override
	public List<AppApprovalFixedExtractCondition> findAll() {
		String query = "SELECT a FROM KrqmtAppApprovalFixedExtractCondition a";
		return this.queryProxy().query(query, KrqmtAppApprovalFixedExtractCondition.class).getList()
				.stream().map(a -> toDomain(a))
				.collect(Collectors.toList());
	}


	@Override
	public void add(AppApprovalFixedExtractCondition domain) {
		String contractCode = AppContexts.user().contractCode();
		KrqmtAppApprovalFixedExtractCondition entity = new KrqmtAppApprovalFixedExtractCondition(
				new KrqmtAppApprovalFixedExtractConditionPK(domain.getErrorAlarmCheckId(), domain.getNo().value),
				contractCode,
				domain.getMessage().isPresent() ? domain.getMessage().get().v() : "",
				domain.isUseAtr() == true ? 1 : 0);
		this.commandProxy().insert(entity);
	}


	@Override
	public void update(AppApprovalFixedExtractCondition domain) {
		KrqmtAppApprovalFixedExtractCondition entity = this.queryProxy().find(
				new KrqmtAppApprovalFixedExtractConditionPK(domain.getErrorAlarmCheckId(), domain.getNo().value),
				KrqmtAppApprovalFixedExtractCondition.class).get();
		entity.setMessage(domain.getMessage().isPresent() ? domain.getMessage().get().v() : "");
		entity.setUseAtr(domain.isUseAtr());
		this.commandProxy().update(entity);
	}


	@Override
	public void delete(String id) {
		String query = "DELETE FROM KrqmtAppApprovalFixedExtractCondition a WHERE a.pk.erAlId = :id";
		this.getEntityManager().createQuery(query)
		.setParameter("id", id).executeUpdate();
	}


	@Override
	public List<AppApprovalFixedExtractCondition> findById(String id) {
		String query = "SELECT a FROM KrqmtAppApprovalFixedExtractCondition a WHERE a.pk.erAlId = :id";
		List<AppApprovalFixedExtractCondition> result = this.queryProxy().query(query, KrqmtAppApprovalFixedExtractCondition.class)
				.setParameter("id", id).getList(a -> toDomain(a));
		return result;
	}
	
	private AppApprovalFixedExtractCondition toDomain(KrqmtAppApprovalFixedExtractCondition entity) {
		return new AppApprovalFixedExtractCondition(
				entity.getPk().getErAlId(),
				EnumAdaptor.valueOf(entity.getPk().getNo(), AppApprovalFixedCheckItem.class), 
				Optional.ofNullable(new ErrorAlarmMessage(entity.getMessage())), 
				entity.isUseAtr());
	}


	@Override
	public List<AppApprovalFixedExtractCondition> findById(String id, boolean useAtr) {
		String query = "SELECT a FROM KrqmtAppApprovalFixedExtractCondition a WHERE a.pk.erAlId = :id and a.useAtr = :useAtr";
		List<AppApprovalFixedExtractCondition> result = this.queryProxy().query(query, KrqmtAppApprovalFixedExtractCondition.class)
				.setParameter("id", id)
				.setParameter("useAtr", useAtr ? 1 : 0)
				.getList(a -> toDomain(a));
		return result;
	}
}
