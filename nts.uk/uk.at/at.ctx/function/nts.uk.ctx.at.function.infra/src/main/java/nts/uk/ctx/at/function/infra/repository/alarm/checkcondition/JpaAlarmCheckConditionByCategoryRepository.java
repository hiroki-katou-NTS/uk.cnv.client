package nts.uk.ctx.at.function.infra.repository.alarm.checkcondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.transaction.Transactional;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategory;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategoryRepository;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.daily.DailyAlarmCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.fourweekfourdayoff.AlarmCheckCondition4W4D;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.monthly.MonAlarmCheckCon;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.multimonth.MulMonAlarmCond;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlarmCheckConditionCategory;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlarmCheckConditionCategoryPk;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlarmCheckConditionCategoryRole;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlarmCheckTargetBusinessType;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlarmCheckTargetClassification;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlarmCheckTargetEmployment;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlarmCheckTargetJobTitle;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.daily.KrcmtDailyErrorCode;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.daily.KrcmtDailyErrorCodePK;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.daily.KrcmtDailyWkRecord;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.daily.KrcmtDailyWkRecordPK;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.monthly.KfnmtMonAlarmCode;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.monthly.KfnmtMonAlarmCodePK;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.multimonth.KfnmtMulMonAlarmCode;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.multimonth.KfnmtMulMonAlarmCodePK;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
@Transactional
public class JpaAlarmCheckConditionByCategoryRepository extends JpaRepository
		implements AlarmCheckConditionByCategoryRepository {

	private static final String GET_All_BY_COMPANY = "SELECT c FROM KfnmtAlarmCheckConditionCategory c WHERE c.pk.companyId = :companyId ";
	private static final String GET_All_BY_COMPANY_CATEGORY = "SELECT c FROM KfnmtAlarmCheckConditionCategory c WHERE c.pk.companyId = :companyId AND c.pk.category = :category ";

	@Override
	public Optional<AlarmCheckConditionByCategory> find(String companyId, int category, String code) {
		Optional<KfnmtAlarmCheckConditionCategory> entity = this.queryProxy().find(
				new KfnmtAlarmCheckConditionCategoryPk(companyId, category, code),
				KfnmtAlarmCheckConditionCategory.class);
		if (entity.isPresent()) {
			return Optional.of(KfnmtAlarmCheckConditionCategory.toDomain(entity.get()));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public List<AlarmCheckConditionByCategory> findAll(String companyId) {
		return this.queryProxy().query(GET_All_BY_COMPANY, KfnmtAlarmCheckConditionCategory.class)
				.setParameter("companyId", companyId).getList(c -> KfnmtAlarmCheckConditionCategory.toDomain(c));
	}

	@Override
	public List<AlarmCheckConditionByCategory> findByCategory(String companyId, int category) {
		return this.queryProxy().query(GET_All_BY_COMPANY_CATEGORY, KfnmtAlarmCheckConditionCategory.class)
				.setParameter("companyId", companyId).setParameter("category", category)
				.getList(c -> KfnmtAlarmCheckConditionCategory.toDomain(c));
	}
	
	@Override
	public List<AlarmCheckConditionByCategory> findByCategoryAndCode(String companyId, int category, List<String> codes) {
		String query = "SELECT c FROM KfnmtAlarmCheckConditionCategory c WHERE c.pk.companyId = :companyId AND c.pk.category = :category AND c.pk.code IN :codes ";
		List<KfnmtAlarmCheckConditionCategory> resultList = new ArrayList<>();
		
		CollectionUtil.split(codes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(query, KfnmtAlarmCheckConditionCategory.class)
					.setParameter("companyId", companyId).setParameter("category", category).setParameter("codes", subList)
					.getList());
		});
		
		return resultList.stream().map(c -> KfnmtAlarmCheckConditionCategory.toDomain(c)).collect(Collectors.toList());
	}
	
	@Override
	public List<AlarmCheckConditionByCategory> findByCategoryAndCode(String companyId, List<Integer> categories, List<String> codes) {
		String query = "SELECT c FROM KfnmtAlarmCheckConditionCategory c WHERE c.pk.companyId = :companyId AND c.pk.category in :category AND c.pk.code IN :codes ";
		List<KfnmtAlarmCheckConditionCategory> resultList = new ArrayList<>();
		
		CollectionUtil.split(codes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(query, KfnmtAlarmCheckConditionCategory.class)
					.setParameter("companyId", companyId)
					.setParameter("category", categories)
					.setParameter("codes", subList)
					.getList());
		});
		
		return resultList.stream().map(c -> KfnmtAlarmCheckConditionCategory.toDomain(c)).collect(Collectors.toList());
	}

	@Override
	public void add(AlarmCheckConditionByCategory domain) {
		this.commandProxy().insert(KfnmtAlarmCheckConditionCategory.fromDomain(domain));
	}

	@Override
	public void update(AlarmCheckConditionByCategory domain) {
		Optional<KfnmtAlarmCheckConditionCategory> entityOpt = this.queryProxy()
				.find(new KfnmtAlarmCheckConditionCategoryPk(domain.getCompanyId(), domain.getCategory().value,
						domain.getCode().v()), KfnmtAlarmCheckConditionCategory.class);
		if (entityOpt.isPresent()) {
			KfnmtAlarmCheckConditionCategory entity = entityOpt.get();
			entity.name = domain.getName().v();
			List<KfnmtAlarmCheckConditionCategoryRole> oldListRole = entity.listAvailableRole;
			List<KfnmtAlarmCheckConditionCategoryRole> newListRole = domain.getListRoleId().stream()
					.map(item -> new KfnmtAlarmCheckConditionCategoryRole(domain.getCompanyId(),
							domain.getCategory().value, domain.getCode().v(), item))
					.collect(Collectors.toList());
			for (KfnmtAlarmCheckConditionCategoryRole newRole : newListRole) {
				for (KfnmtAlarmCheckConditionCategoryRole oldRole : oldListRole) {
					if (oldRole.pk.equals(newRole.pk)) {
						newListRole.set(newListRole.indexOf(newRole), oldRole);
						break;
					}
				}
			}
			entity.listAvailableRole = newListRole;

			entity.targetCondition.filterByBusinessType = domain.getExtractTargetCondition().isFilterByBusinessType();
			entity.targetCondition.filterByClassification = domain.getExtractTargetCondition().isFilterByClassification();
			entity.targetCondition.filterByEmployment = domain.getExtractTargetCondition().isFilterByEmployment();
			entity.targetCondition.filterByJobTitle = domain.getExtractTargetCondition().isFilterByJobTitle();

			List<KfnmtAlarmCheckTargetEmployment> oldListTargetEmployment = entity.targetCondition.listEmployment;
			List<KfnmtAlarmCheckTargetEmployment> newListTargetEmployment = domain.getExtractTargetCondition().getLstEmploymentCode().stream().map(item -> new KfnmtAlarmCheckTargetEmployment(domain.getExtractTargetCondition().getId(), item)).collect(Collectors.toList());
			for (KfnmtAlarmCheckTargetEmployment newTarget : newListTargetEmployment) {
				for (KfnmtAlarmCheckTargetEmployment oldTarget : oldListTargetEmployment) {
					if (oldTarget.pk.equals(newTarget.pk)) {
						newListTargetEmployment.set(newListTargetEmployment.indexOf(newTarget), oldTarget);
						break;
					}
				}
			}
			entity.targetCondition.listEmployment = newListTargetEmployment;
			
			List<KfnmtAlarmCheckTargetClassification> oldListTargetClassification = entity.targetCondition.listClassification;
			List<KfnmtAlarmCheckTargetClassification> newListTargetClassification = domain.getExtractTargetCondition().getLstClassificationCode().stream().map(item -> new KfnmtAlarmCheckTargetClassification(domain.getExtractTargetCondition().getId(), item)).collect(Collectors.toList());
			for (KfnmtAlarmCheckTargetClassification newTarget : newListTargetClassification) {
				for (KfnmtAlarmCheckTargetClassification oldTarget : oldListTargetClassification) {
					if (oldTarget.pk.equals(newTarget.pk)) {
						newListTargetClassification.set(newListTargetClassification.indexOf(newTarget), oldTarget);
						break;
					}
				}
			}
			entity.targetCondition.listClassification = newListTargetClassification;
			
			List<KfnmtAlarmCheckTargetJobTitle> oldListTargetJobTitle = entity.targetCondition.listJobTitle;
			List<KfnmtAlarmCheckTargetJobTitle> newListTargetJobTitle = domain.getExtractTargetCondition().getLstJobTitleId().stream().map(item -> new KfnmtAlarmCheckTargetJobTitle(domain.getExtractTargetCondition().getId(), item)).collect(Collectors.toList());
			for (KfnmtAlarmCheckTargetJobTitle newTarget : newListTargetJobTitle) {
				for (KfnmtAlarmCheckTargetJobTitle oldTarget : oldListTargetJobTitle) {
					if (oldTarget.pk.equals(newTarget.pk)) {
						newListTargetJobTitle.set(newListTargetJobTitle.indexOf(newTarget), oldTarget);
						break;
					}
				}
			}
			entity.targetCondition.listJobTitle = newListTargetJobTitle;
			
			List<KfnmtAlarmCheckTargetBusinessType> oldListTargetBusinessType = entity.targetCondition.listBusinessType;
			List<KfnmtAlarmCheckTargetBusinessType> newListTargetBusinessType = domain.getExtractTargetCondition().getLstBusinessTypeCode().stream().map(item -> new KfnmtAlarmCheckTargetBusinessType(domain.getExtractTargetCondition().getId(), item)).collect(Collectors.toList());
			for (KfnmtAlarmCheckTargetBusinessType newTarget : newListTargetBusinessType) {
				for (KfnmtAlarmCheckTargetBusinessType oldTarget : oldListTargetBusinessType) {
					if (oldTarget.pk.equals(newTarget.pk)) {
						newListTargetBusinessType.set(newListTargetBusinessType.indexOf(newTarget), oldTarget);
						break;
					}
				}
			}
			entity.targetCondition.listBusinessType = newListTargetBusinessType;
			
			if (entity.pk.category == AlarmCategory.DAILY.value) {
				DailyAlarmCondition dailyAlarmCondition = (DailyAlarmCondition) domain.getExtractionCondition();
				entity.dailyAlarmCondition.addApplication = dailyAlarmCondition.isAddApplication();
				entity.dailyAlarmCondition.conExtractedDaily = dailyAlarmCondition.getConExtractedDaily().value;
				
				List<KrcmtDailyErrorCode> oldListErrorAlarmCode = entity.dailyAlarmCondition.listErrorAlarmCode;
				List<KrcmtDailyErrorCode> newListErrorAlarmCode = dailyAlarmCondition.getErrorAlarmCode().stream().map(item -> new KrcmtDailyErrorCode(new KrcmtDailyErrorCodePK(dailyAlarmCondition.getDailyAlarmConID(), item))).collect(Collectors.toList());
				for (KrcmtDailyErrorCode newTarget : newListErrorAlarmCode) {
					for (KrcmtDailyErrorCode oldTarget : oldListErrorAlarmCode) {
						if (oldTarget.krcmtDailyErrorCodePK.equals(newTarget.krcmtDailyErrorCodePK)) {
							newListErrorAlarmCode.set(newListErrorAlarmCode.indexOf(newTarget), oldTarget);
							break;
						}
					}
				}
				entity.dailyAlarmCondition.listErrorAlarmCode = newListErrorAlarmCode;
				
				List<KrcmtDailyWkRecord> oldListWorkRecord = entity.dailyAlarmCondition.listExtractConditionWorkRecord;
				List<KrcmtDailyWkRecord> newListWorkRecord = dailyAlarmCondition.getExtractConditionWorkRecord().stream().map(item -> new KrcmtDailyWkRecord(new KrcmtDailyWkRecordPK(dailyAlarmCondition.getDailyAlarmConID(), item))).collect(Collectors.toList());
				for (KrcmtDailyWkRecord newTarget : newListWorkRecord) {
					for (KrcmtDailyWkRecord oldTarget : oldListWorkRecord) {
						if (oldTarget.krcmtDailyWkRecordPK.equals(newTarget.krcmtDailyWkRecordPK)) {
							newListWorkRecord.set(newListWorkRecord.indexOf(newTarget), oldTarget);
							break;
						}
					}
				}
				entity.dailyAlarmCondition.listExtractConditionWorkRecord = newListWorkRecord;
			}
			
			if (entity.pk.category == AlarmCategory.MONTHLY.value) {
				MonAlarmCheckCon monAlarmCheckCon = (MonAlarmCheckCon) domain.getExtractionCondition();
				
				List<KfnmtMonAlarmCode> oldListErrorAlarmCode = entity.kfnmtMonAlarmCheckCon.listMonAlarmCode;
				List<KfnmtMonAlarmCode> newListErrorAlarmCode = monAlarmCheckCon.getArbExtraCon().stream().map(item -> new KfnmtMonAlarmCode(new KfnmtMonAlarmCodePK(entity.kfnmtMonAlarmCheckCon.monAlarmCheckConID, item))).collect(Collectors.toList());
				for (KfnmtMonAlarmCode newTarget : newListErrorAlarmCode) {
					for (KfnmtMonAlarmCode oldTarget : oldListErrorAlarmCode) {
						if (oldTarget.kfnmtMonAlarmCodePK.equals(newTarget.kfnmtMonAlarmCodePK)) {
							newListErrorAlarmCode.set(newListErrorAlarmCode.indexOf(newTarget), oldTarget);
							break;
						}
					}
				}
				entity.kfnmtMonAlarmCheckCon.listMonAlarmCode = newListErrorAlarmCode;
				
			}
			
			
			if (entity.pk.category == AlarmCategory.MULTIPLE_MONTH.value) {
				MulMonAlarmCond mulMonAlarmCond = (MulMonAlarmCond) domain.getExtractionCondition();
				
				List<KfnmtMulMonAlarmCode> oldListErrorAlarmCode = entity.mulMonAlarmCond.listMulMonAlarmCode;
				List<KfnmtMulMonAlarmCode> newListErrorAlarmCode = mulMonAlarmCond.getErrorAlarmCondIds().stream().map(item -> 
				new KfnmtMulMonAlarmCode(new KfnmtMulMonAlarmCodePK(entity.mulMonAlarmCond.mulMonAlarmConID, item))).collect(Collectors.toList());
				for (KfnmtMulMonAlarmCode newTarget : newListErrorAlarmCode) {
					for (KfnmtMulMonAlarmCode oldTarget : oldListErrorAlarmCode) {
						if (oldTarget.kfnmtMulMonAlarmCodePK.equals(newTarget.kfnmtMulMonAlarmCodePK)) {
							newListErrorAlarmCode.set(newListErrorAlarmCode.indexOf(newTarget), oldTarget);
							break;
						}
					}
				}
				entity.mulMonAlarmCond.listMulMonAlarmCode = newListErrorAlarmCode;
			}
			
			if (entity.pk.category == AlarmCategory.SCHEDULE_4WEEK.value) {
				AlarmCheckCondition4W4D schedule4Week = (AlarmCheckCondition4W4D) domain.getExtractionCondition();
				entity.schedule4W4DAlarmCondition.fourW4DCheckCond = schedule4Week.getFourW4DCheckCond().value;
			}
			
			this.commandProxy().update(entity);
		}

	}

	@Override
	public void delete(String companyId, int category, String alarmConditionCode) {
		this.commandProxy().remove(KfnmtAlarmCheckConditionCategory.class, new KfnmtAlarmCheckConditionCategoryPk(
				companyId, category, alarmConditionCode));
	}

	@Override
	public boolean isCodeExist(String companyId, int category, String code) {
		return this.queryProxy().find(new KfnmtAlarmCheckConditionCategoryPk(companyId, category, code),
				KfnmtAlarmCheckConditionCategory.class).isPresent();
	}

}
