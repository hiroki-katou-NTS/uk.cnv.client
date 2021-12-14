package nts.uk.ctx.at.shared.infra.repository.holidaysetting.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySettingRepository;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.company.KshmtHdpubDPerMCom;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.company.KshmtComMonthDaySetPK_;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.company.KshmtComMonthDaySet_;

/**
 * The Class JpaCompanyMonthDaySettingRepository.
 */
@Stateless
public class JpaCompanyMonthDaySettingRepository extends JpaRepository implements CompanyMonthDaySettingRepository {

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingRepository#findByYear(nts.uk.ctx.bs.employee.dom.common.CompanyId, nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year)
	 */
	@Override
	public Optional<CompanyMonthDaySetting> findByYear(CompanyId companyId, Year year) {
		List<KshmtHdpubDPerMCom> result = this.findBy(companyId, year, null);
		
		// Check exist
		if (result.isEmpty()) {
			return Optional.empty();
		}
		
		CompanyMonthDaySetting domain = new CompanyMonthDaySetting(new JpaCompanyMonthDaySettingGetMemento(result));
			
		return Optional.of(domain);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingRepository#add(nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySetting)
	 */
	@Override
	public void add(CompanyMonthDaySetting domain) {
		List<KshmtHdpubDPerMCom> entities = new ArrayList<>();
		domain.saveToMemento(new JpaCompanyMonthDaySettingSetMemento(entities));
		this.commandProxy().insertAll(entities);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingRepository#update(nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySetting)
	 */
	@Override
	public void update(CompanyMonthDaySetting domain) {
		List<KshmtHdpubDPerMCom> entities = this.findBy(domain.getCompanyId(),
				domain.getManagementYear(), null);
		domain.saveToMemento(new JpaCompanyMonthDaySettingSetMemento(entities));
		this.commandProxy().updateAll(entities);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingRepository#remove(nts.uk.ctx.bs.employee.dom.common.CompanyId, nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year)
	 */
	@Override
	public void remove(CompanyId companyId, Year year) {
		List<KshmtHdpubDPerMCom> result = this.findBy(companyId, year, null);
		this.commandProxy().removeAll(result);
	}
	
	/**
	 * Find by.
	 *
	 * @param companyId the company id
	 * @param year the year
	 * @param month the month
	 * @return the list
	 */
	private List<KshmtHdpubDPerMCom> findBy(CompanyId companyId, Year year, Integer month) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KshmtHdpubDPerMCom> cq = criteriaBuilder.createQuery(KshmtHdpubDPerMCom.class);

		// root data
		Root<KshmtHdpubDPerMCom> root = cq.from(KshmtHdpubDPerMCom.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();

		// eq company id
		if (companyId != null) {
			lstpredicateWhere
					.add(criteriaBuilder.equal(root.get(KshmtComMonthDaySet_.kshmtComMonthDaySetPK).get(KshmtComMonthDaySetPK_.cid), companyId.v()));
		}

		if (year != null) {
			lstpredicateWhere.add(criteriaBuilder.equal(
					root.get(KshmtComMonthDaySet_.kshmtComMonthDaySetPK).get(KshmtComMonthDaySetPK_.manageYear),
					year.v()));
		}

		if (month != null) {
			lstpredicateWhere
					.add(criteriaBuilder.equal(root.get(KshmtComMonthDaySet_.kshmtComMonthDaySetPK)
							.get(KshmtComMonthDaySetPK_.month), month.intValue()));
		}
		
		cq.orderBy(criteriaBuilder.asc(root.get(KshmtComMonthDaySet_.kshmtComMonthDaySetPK).get(KshmtComMonthDaySetPK_.month)));
		// set where to SQL
		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

		// creat query
		TypedQuery<KshmtHdpubDPerMCom> query = em.createQuery(cq);

		return query.getResultList();
	}
	
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySettingRepository#findByYears(nts.uk.ctx.at.shared.dom.common.CompanyId, java.util.List)
	 */
	@Override
	public List<CompanyMonthDaySetting> findByYears(CompanyId companyId, List<Year> years) {
		
		List<CompanyMonthDaySetting> domain = new ArrayList<>();
		
		for(Year year:years){
			List<KshmtHdpubDPerMCom> result = this.findBy(companyId, year, null);
		
			if (!result.isEmpty()) {
				domain.add(new CompanyMonthDaySetting(new JpaCompanyMonthDaySettingGetMemento(result)));
			}
		
		}
		return domain;
	}

}
