package nts.uk.ctx.at.record.dom.divergence.time.history;

import java.util.List;

/**
 * The Interface CompanyDivergenceReferenceTimeRepository.
 */
public interface CompanyDivergenceReferenceTimeRepository {
	
	/**
	 * Find by key.
	 *
	 * @param histId the hist id
	 * @param divergenceTimeNo the divergence time no
	 * @return the company divergence reference time
	 */
	CompanyDivergenceReferenceTime findByKey(String histId, DivergenceType divergenceTimeNo);
	
	/**
	 * Find all.
	 *
	 * @param histId the hist id
	 * @return the list
	 */
	List<CompanyDivergenceReferenceTime> findAll(String histId);
	
	/**
	 * Adds the.
	 *
	 * @param domain the domain
	 */
	void add(CompanyDivergenceReferenceTime domain);
	
	/**
	 * Update.
	 *
	 * @param domain the domain
	 */
	void update(CompanyDivergenceReferenceTime domain);
	
	/**
	 * Delete.
	 *
	 * @param domain the domain
	 */
	void delete(CompanyDivergenceReferenceTime domain);
}
