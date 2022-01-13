package nts.uk.ctx.at.shared.infra.repository.specialholiday.specialholidayevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.UseAtr;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.AgeLimit;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.AgeRange;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.GenderCls;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.AgeStandardType;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.ClassificationList;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.EmploymentList;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.FixedDayGrant;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.MaxNumberDayType;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.SpecialHolidayEvent;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.SpecialHolidayEventRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.specialholidayevent.KshstClassificationList;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.specialholidayevent.KshstClassificationListPK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.specialholidayevent.KshmtHdspevCondEmp;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.specialholidayevent.KshstEmploymentListPK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.specialholidayevent.KshstSpecialHolidayEvent;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.specialholidayevent.KshstSpecialHolidayEventPK;
import nts.uk.shr.com.primitive.Memo;

@Stateless
public class JpaSpecialHolidayEvent extends JpaRepository implements SpecialHolidayEventRepository {

	private static final String FIND_EMP_LIST_QUERY;
	private static final String FIND_CLS_LIST_QUERY;
	private static final String FIND_BY_NO_LIST_QUERY;
	private static final String FIND_BY_CID_LIST_QUERY;
	private static final String REMOVE_EMP_ITEMS_QUERY;
	private static final String REMOVE_CLS_ITEMS_QUERY;

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM KshmtHdspevCondEmp e");
		builderString.append(" WHERE e.pk.companyId = :companyId");
		builderString.append(" AND e.pk.specialHolidayEventNo= :SHENo ORDER BY e.pk.employmentCd ");
		FIND_EMP_LIST_QUERY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT c");
		builderString.append(" FROM KshstClassificationList c");
		builderString.append(" WHERE c.pk.companyId = :companyId");
		builderString.append(" AND c.pk.specialHolidayEventNo= :SHENo ORDER BY c.pk.classificationCd ");
		FIND_CLS_LIST_QUERY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT c");
		builderString.append(" FROM KshstSpecialHolidayEvent c");
		builderString.append(" WHERE c.pk.companyId = :companyId");
		builderString.append(" AND c.pk.specialHolidayEventNo IN :SHENos");
		FIND_BY_NO_LIST_QUERY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("DELETE FROM KshmtHdspevCondEmp e");
		builderString.append(" WHERE e.pk.companyId = :companyId");
		builderString.append(" AND e.pk.specialHolidayEventNo = :SHENo");
		REMOVE_EMP_ITEMS_QUERY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("DELETE FROM KshstClassificationList c");
		builderString.append(" WHERE c.pk.companyId = :companyId");
		builderString.append(" AND c.pk.specialHolidayEventNo = :SHENo");
		REMOVE_CLS_ITEMS_QUERY = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT c");
		builderString.append(" FROM KshstSpecialHolidayEvent c");
		builderString.append(" WHERE c.pk.companyId = :companyId");
		FIND_BY_CID_LIST_QUERY = builderString.toString();
	}

	@Override
	public List<SpecialHolidayEvent> findByCompanyIdAndNoLst(String companyId, List<Integer> sHsNos) {
		if (CollectionUtil.isEmpty(sHsNos)) {
			return Collections.emptyList();
		}
		List<SpecialHolidayEvent> resultList = new ArrayList<>();
		CollectionUtil.split(sHsNos, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_NO_LIST_QUERY, KshstSpecialHolidayEvent.class)
								.setParameter("companyId", companyId)
								.setParameter("SHENos", subList)
								.getList(c -> toDomain(c)));
		});
		return resultList;
	}

	@Override
	public Optional<SpecialHolidayEvent> findByKey(String companyId, int eventNo) {
		return this.queryProxy()
				.find(new KshstSpecialHolidayEventPK(companyId, eventNo), KshstSpecialHolidayEvent.class)
				.map(x -> toDomain(x));
	}

	private SpecialHolidayEvent toDomain(KshstSpecialHolidayEvent entity) {
		return new SpecialHolidayEvent(entity.pk.companyId, entity.pk.specialHolidayEventNo,
				EnumAdaptor.valueOf(entity.maxNumberDayType, MaxNumberDayType.class),
				new FixedDayGrant(entity.fixedDayGrant), 
				EnumAdaptor.valueOf(BooleanUtils.toInteger(entity.makeInvitation), UseAtr.class),
				EnumAdaptor.valueOf(BooleanUtils.toInteger(entity.includeHolidays), UseAtr.class),
				EnumAdaptor.valueOf(BooleanUtils.toInteger(entity.ageLimit), UseAtr.class),
				EnumAdaptor.valueOf(BooleanUtils.toInteger(entity.genderRestrict), UseAtr.class),
				EnumAdaptor.valueOf(BooleanUtils.toInteger(entity.restrictEmployment), UseAtr.class),
				EnumAdaptor.valueOf(BooleanUtils.toInteger(entity.restrictClassification), UseAtr.class),
				EnumAdaptor.valueOf(BooleanUtils.toInteger(entity.gender), GenderCls.class),
				createAgeRange(entity.ageRangeLowerLimit, entity.ageRangeHigherLimit),
				EnumAdaptor.valueOf(entity.ageStandard, AgeStandardType.class), entity.ageStandardBaseDate,
				new Memo(entity.memo), getClsList(entity.pk.companyId, entity.pk.specialHolidayEventNo),
				getEmpList(entity.pk.companyId, entity.pk.specialHolidayEventNo));
	}

	private AgeRange createAgeRange(Integer lower, Integer higher) {
		if (lower != null && higher != null) {
			return new AgeRange(new AgeLimit(lower), new AgeLimit(higher));
		}
		return null;
	}

	private List<EmploymentList> getEmpList(String companyId, int SHENo) {
		return this.queryProxy().query(FIND_EMP_LIST_QUERY, KshmtHdspevCondEmp.class)
				.setParameter("companyId", companyId).setParameter("SHENo", SHENo).getList(c -> toEmpList(c));
	}

	private EmploymentList toEmpList(KshmtHdspevCondEmp entity) {
		return new EmploymentList(entity.pk.companyId, entity.pk.specialHolidayEventNo,
				new EmploymentCode(entity.pk.employmentCd));
	}

	private List<ClassificationList> getClsList(String companyId, int SHENo) {
		return this.queryProxy().query(FIND_CLS_LIST_QUERY, KshstClassificationList.class)
				.setParameter("companyId", companyId).setParameter("SHENo", SHENo).getList(c -> toClsList(c));
	}

	private ClassificationList toClsList(KshstClassificationList entity) {
		return new ClassificationList(entity.pk.companyId, entity.pk.specialHolidayEventNo, entity.pk.classificationCd);
	}

	@Override
	public void insert(SpecialHolidayEvent domain) {
		this.commandProxy().insert(toEntity(domain));
		addClsItems(domain);
		addEmpItems(domain);
	}

	private void addEmpItems(SpecialHolidayEvent domain) {
		domain.getEmpList().forEach(x -> {
			this.commandProxy().insert(toEmpEntity(x));
		});

	}

	private KshmtHdspevCondEmp toEmpEntity(EmploymentList domain) {
		return new KshmtHdspevCondEmp(new KshstEmploymentListPK(domain.getCompanyId(),
				domain.getSpecialHolidayEventNo(), domain.getEmploymentCd().v()));
	}

	private void addClsItems(SpecialHolidayEvent domain) {
		domain.getClsList().forEach(x -> {
			this.commandProxy().insert(toClsEntity(x));
		});
	}

	private KshstClassificationList toClsEntity(ClassificationList domain) {
		return new KshstClassificationList(new KshstClassificationListPK(domain.getCompanyId(),
				domain.getSpecialHolidayEventNo(), domain.getClassificationCd()));
	}

	private KshstSpecialHolidayEvent toEntity(SpecialHolidayEvent domain) {
		return new KshstSpecialHolidayEvent(
				new KshstSpecialHolidayEventPK(
						domain.getCompanyId(), 
						domain.getSpecialHolidayEventNo()),
				domain.getMaxNumberDay().value, 
				domain.getFixedDayGrant().v(), 
				BooleanUtils.toBoolean(domain.getMakeInvitation().value),
				BooleanUtils.toBoolean(domain.getIncludeHolidays().value), 
				BooleanUtils.toBoolean(domain.getAgeLimit().value), 
				BooleanUtils.toBoolean(domain.getGenderRestrict().value),
				BooleanUtils.toBoolean(domain.getRestrictEmployment().value), 
				BooleanUtils.toBoolean(domain.getRestrictClassification().value),
				BooleanUtils.toBoolean(domain.getGender().value), 
				domain.getAgeLowerLimit(), 
				domain.getAgeRangeHigherLimit(),
				domain.getAgeStandard().value, 
				domain.getAgeStandardBaseDate(), 
				domain.getMemo().v());
	}

	@Override
	public void update(SpecialHolidayEvent domain) {
		this.queryProxy().find(new KshstSpecialHolidayEventPK(domain.getCompanyId(), domain.getSpecialHolidayEventNo()),
				KshstSpecialHolidayEvent.class).ifPresent(x -> {
					x.updateEntity(domain);
					this.commandProxy().update(x);
					updateClsItem(domain);
					updateEmpItem(domain);
				});

	}

	private void updateEmpItem(SpecialHolidayEvent domain) {
		removeEmpItems(domain.getCompanyId(), domain.getSpecialHolidayEventNo());
		addEmpItems(domain);
	}

	private void removeEmpItems(String companyId, int ShENo) {
		this.getEntityManager().createQuery(REMOVE_EMP_ITEMS_QUERY).setParameter("companyId", companyId)
				.setParameter("SHENo", ShENo).executeUpdate();

	}

	private void updateClsItem(SpecialHolidayEvent domain) {
		removeClsItems(domain.getCompanyId(), domain.getSpecialHolidayEventNo());
		addClsItems(domain);

	}

	private void removeClsItems(String companyId, int ShENo) {
		this.getEntityManager().createQuery(REMOVE_CLS_ITEMS_QUERY).setParameter("companyId", companyId)
				.setParameter("SHENo", ShENo).executeUpdate();
	}

	@Override
	public void remove(String companyId, int specialHolidayEventNo) {
		this.commandProxy().remove(KshstSpecialHolidayEvent.class,
				new KshstSpecialHolidayEventPK(companyId, specialHolidayEventNo));
		removeClsItems(companyId, specialHolidayEventNo);
		removeEmpItems(companyId, specialHolidayEventNo);

	}
	
	@Override
	public List<SpecialHolidayEvent> findByCompany(String companyId) {
		return this.queryProxy().query(FIND_BY_CID_LIST_QUERY, KshstSpecialHolidayEvent.class)
				.setParameter("companyId", companyId).getList(c -> toDomain(c));
	}
}
