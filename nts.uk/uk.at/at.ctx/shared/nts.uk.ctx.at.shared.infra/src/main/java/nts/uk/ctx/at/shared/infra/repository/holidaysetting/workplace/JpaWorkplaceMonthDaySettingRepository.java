/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.holidaysetting.workplace;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace.WorkplaceMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace.WorkplaceMonthDaySettingRepository;
//import nts.uk.ctx.at.shared.infra.entity.holidaysetting.company.KshmtComMonthDaySetPK_;
//import nts.uk.ctx.at.shared.infra.entity.holidaysetting.company.KshmtComMonthDaySet_;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.workplace.KshmtHdpubDPerMWkp;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.workplace.KshmtWkpMonthDaySetPK;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.workplace.KshmtWkpMonthDaySetPK_;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.workplace.KshmtWkpMonthDaySet_;

/**
 * The Class JpaWorkplaceMonthDaySettingRepository.
 */
@Stateless
public class JpaWorkplaceMonthDaySettingRepository extends JpaRepository implements WorkplaceMonthDaySettingRepository {

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingRepository#findByYear(nts.uk.ctx.bs.employee.dom.common.CompanyId, java.lang.String, nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year)
	 */
	@Override
	public Optional<WorkplaceMonthDaySetting> findByYear(CompanyId companyId, String workplaceId, Year year) {
		List<KshmtHdpubDPerMWkp> result = this.findBy(companyId, workplaceId, year, null, null);
		// Check exist
		if (result.isEmpty()) {
			return Optional.empty();
		}
		
		WorkplaceMonthDaySetting domain = new WorkplaceMonthDaySetting(new JpaWorkplaceMonthDaySettingGetMemento(result));
			
		return Optional.of(domain);
	}

	@Override
	public List<WorkplaceMonthDaySetting> findByWorkplaceIds(CompanyId companyId, List<String> workplaceIds) {
		if (CollectionUtil.isEmpty(workplaceIds)) return new ArrayList<>();
		List<KshmtHdpubDPerMWkp> result = this.findBy(companyId, null, null, null, workplaceIds);
		// Check exist
		if (result.isEmpty()) {
			return new ArrayList<>();
		}
		Map<String, List<KshmtHdpubDPerMWkp>> entityAll = result.stream()
				.collect(Collectors.groupingBy(x -> x.getKshmtWkpMonthDaySetPK().getWkpId(), Collectors.toList()));
		return entityAll.entrySet().stream().map(x -> new WorkplaceMonthDaySetting(new JpaWorkplaceMonthDaySettingGetMemento(x.getValue())))
				.collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingRepository#add(nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySetting)
	 */
	@Override
	public void add(WorkplaceMonthDaySetting domain) {
		List<KshmtHdpubDPerMWkp> entities = new ArrayList<>();
		domain.saveToMemento(new JpaWorkplaceMonthDaySettingSetMemento(entities));
		this.commandProxy().insertAll(entities);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingRepository#update(nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySetting)
	 */
	@Override
	public void update(WorkplaceMonthDaySetting domain) {
		List<KshmtHdpubDPerMWkp> entities = this.findBy(domain.getCompanyId(), domain.getWorkplaceId(),
				domain.getManagementYear(), null, null);
		domain.saveToMemento(new JpaWorkplaceMonthDaySettingSetMemento(entities));
		this.commandProxy().updateAll(entities);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingRepository#remove(nts.uk.ctx.bs.employee.dom.common.CompanyId, java.lang.String, nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year)
	 */
	@Override
	public void remove(CompanyId companyId, String workplaceId, Year year, Integer startMonth) {
		Year nextYear = new Year(year.v() + 1);
		List<Year> years = Arrays.asList(year, nextYear);
		List<KshmtHdpubDPerMWkp> entities = years.stream()
				.map(y -> this.findBy(companyId, workplaceId, y, null, null))
				.flatMap(List::stream)
				.filter(data -> {
					if (data.getKshmtWkpMonthDaySetPK().getManageYear() == year.v()) {
						return data.getKshmtWkpMonthDaySetPK().getMonth() >= startMonth;
					} else if (data.getKshmtWkpMonthDaySetPK().getManageYear() == nextYear.v()) {
						return data.getKshmtWkpMonthDaySetPK().getMonth() < startMonth;
					}
					return false;
				})
				.collect(Collectors.toList());
		this.commandProxy().removeAll(entities);
	}
	
	/**
	 * Find by.
	 *
	 * @param companyId the company id
	 * @param workplaceId the workplace id
	 * @param year the year
	 * @param month the month
	 * @return the list
	 */
	private List<KshmtHdpubDPerMWkp> findBy(CompanyId companyId, String workplaceId, Year year, Integer month, List<String> workplaceIds) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KshmtHdpubDPerMWkp> cq = criteriaBuilder.createQuery(KshmtHdpubDPerMWkp.class);

		// root data
		Root<KshmtHdpubDPerMWkp> root = cq.from(KshmtHdpubDPerMWkp.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();

		// eq company id
		if (companyId != null) {
			lstpredicateWhere
					.add(criteriaBuilder.equal(root.get(KshmtWkpMonthDaySet_.kshmtWkpMonthDaySetPK).get(KshmtWkpMonthDaySetPK_.cid), companyId.v()));
		}
		
		if (workplaceId != null) {
			lstpredicateWhere.add(criteriaBuilder.equal(
					root.get(KshmtWkpMonthDaySet_.kshmtWkpMonthDaySetPK).get(KshmtWkpMonthDaySetPK_.wkpId),
					workplaceId));
		}

		if (year != null) {
			lstpredicateWhere.add(criteriaBuilder.equal(
					root.get(KshmtWkpMonthDaySet_.kshmtWkpMonthDaySetPK).get(KshmtWkpMonthDaySetPK_.manageYear),
					year.v()));
		}

		if (month != null) {
			lstpredicateWhere
					.add(criteriaBuilder.equal(root.get(KshmtWkpMonthDaySet_.kshmtWkpMonthDaySetPK)
							.get(KshmtWkpMonthDaySetPK_.month), month.intValue()));
		}

		if (!CollectionUtil.isEmpty(workplaceIds)){
			lstpredicateWhere.add(root.get(KshmtWkpMonthDaySet_.kshmtWkpMonthDaySetPK).get(KshmtWkpMonthDaySetPK_.wkpId).in(workplaceIds));
		}

		cq.orderBy(criteriaBuilder.asc(root.get(KshmtWkpMonthDaySet_.kshmtWkpMonthDaySetPK).get(KshmtWkpMonthDaySetPK_.month)));
		// set where to SQL
		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

		// creat query
		TypedQuery<KshmtHdpubDPerMWkp> query = em.createQuery(cq);

		return query.getResultList();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingRepository#findWkpRegisterByYear(nts.uk.ctx.bs.employee.dom.common.CompanyId, nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year)
	 */
	@Override
	public List<String> findWkpRegisterByYear(CompanyId companyId, Year year) {
		List<KshmtHdpubDPerMWkp> result = this.findBy(companyId, null, year, null,null);

		// Check exist
		if (result.isEmpty()) {
			return new ArrayList<>();
		}
		
		return result.stream()
						.map(obj -> obj.getKshmtWkpMonthDaySetPK().getWkpId())
						.distinct()
						.collect(toList());
	}

	@Override
	public List<WorkplaceMonthDaySetting> findByYear(CompanyId companyId, Year year) {
		List<KshmtHdpubDPerMWkp> result = this.findBy(companyId, null, year, null, null);
		// Check exist
		if (result.isEmpty()) {
			return new ArrayList<>();
		}
		
		Map<Integer, List<KshmtHdpubDPerMWkp>> entityAll = result.stream()
				.collect(Collectors.groupingBy(x -> x.getKshmtWkpMonthDaySetPK().getManageYear(), Collectors.toList()));
		return entityAll.entrySet().stream().map(x -> new WorkplaceMonthDaySetting(new JpaWorkplaceMonthDaySettingGetMemento(x.getValue())))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<WorkplaceMonthDaySetting> findByYears(CompanyId companyId, String workplaceId, List<Year> years) {
		List<WorkplaceMonthDaySetting> domain =new ArrayList<>();
		
		for(Year year:years){
			List<KshmtHdpubDPerMWkp> result = this.findBy(companyId, workplaceId, year, null, null);
			if (!result.isEmpty()) {	
				domain.add(new WorkplaceMonthDaySetting(new JpaWorkplaceMonthDaySettingGetMemento(result)));
			}
		}
		return domain;
	}

	@Override
	public void remove(WorkplaceMonthDaySetting domain) {
		List<KshmtWkpMonthDaySetPK> primaryKeys = domain.getPublicHolidayMonthSettings().stream()
				.map(data -> new KshmtWkpMonthDaySetPK(domain.getCompanyId().v(), domain.getWorkplaceId(),
						data.getPublicHdManagementYear().v(), data.getMonth()))
				.collect(Collectors.toList());
		this.commandProxy().removeAll(KshmtHdpubDPerMWkp.class, primaryKeys);
		this.getEntityManager().flush();
	}
}
