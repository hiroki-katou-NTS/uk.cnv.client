/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.scherec.optitem;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.scherec.optitem.export.CalFormulasItemExportData;
import nts.uk.ctx.at.shared.dom.scherec.optitem.export.CalFormulasItemTableExportData;

/**
 * The Interface OptionalItemRepository.
 */
public interface OptionalItemRepository {

	// Repo export excel
	List<CalFormulasItemExportData> findAllData(String companyId);

	/**
	 * Update.
	 *
	 * @param dom
	 *            the dom
	 */
	void update(OptionalItem dom);

	/**
	 * Find.
	 *
	 * @param companyId
	 *            the company id
	 * @param optionalItemNo
	 *            the optional item no
	 * @return the optional item
	 */
	OptionalItem find(String companyId, Integer optionalItemNo);
	
	/**
	 * Find.
	 *
	 * @param companyId
	 *            the company id
	 * @param optionalItemNo
	 *            the optional item no
	 * @return the optional item
	 */
	Optional<OptionalItem> findByItemNo(String companyId, Integer optionalItemNo);

	/**
	 * Find all.
	 *
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<OptionalItem> findAll(String companyId);

	/**
	 * Find by list nos.
	 *
	 * @param companyId
	 *            the company id
	 * @param optionalitemNos
	 *            the optionalitem nos
	 * @return the list
	 */
	List<OptionalItem> findByListNos(String companyId, List<Integer> optionalitemNos);

	/**
	 * Find by atr.
	 *
	 * @param companyId
	 *            the company id
	 * @param atr
	 *            the atr
	 * @return the list
	 */
	List<OptionalItem> findByAtr(String companyId, int atr);

	/**
	 * Find by atr.
	 *
	 * @param companyId
	 *            the company id
	 * @param atr
	 *            the atr
	 * @return the list
	 */
	List<OptionalItem> findByAtr(String companyId, OptionalItemAtr atr);

	/**
	 * Find by performance atr.
	 *
	 * @param companyId
	 *            the company id
	 * @param atr
	 *            the atr
	 * @return the list
	 */
	List<OptionalItem> findByPerformanceAtr(String companyId, PerformanceAtr atr);

	/**
	 * Find by performance atr.
	 *
	 * @param companyId
	 *            the company id
	 * @param atr
	 *            the atr
	 * @return the list
	 */
	List<OptionalItem> findUsedByPerformanceAtr(String companyId, PerformanceAtr atr);

	/**
	 * Find by performance atr.
	 *
	 * @param companyId
	 *            the company id
	 * @param atr
	 *            the atr
	 * @return the list
	 */
	Map<Integer, OptionalItemAtr> findOptionalTypeBy(String companyId, PerformanceAtr atr);

	/* Export Excel */

	List<CalFormulasItemExportData> findAllCalFormulasItem(String companyId, String languageId);
	
	List<CalFormulasItemTableExportData> findAllCalFormulasTableItem(String companyId, String languageId);

}
