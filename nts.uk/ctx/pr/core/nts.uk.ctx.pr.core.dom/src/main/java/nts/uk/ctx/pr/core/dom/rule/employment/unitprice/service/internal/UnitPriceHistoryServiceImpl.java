/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.dom.rule.employment.unitprice.service.internal;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.YearMonth;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.StringUtil;
import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryRepository;
import nts.uk.ctx.pr.core.dom.rule.employment.unitprice.UnitPriceCode;
import nts.uk.ctx.pr.core.dom.rule.employment.unitprice.UnitPriceHistory;
import nts.uk.ctx.pr.core.dom.rule.employment.unitprice.UnitPriceHistoryRepository;
import nts.uk.ctx.pr.core.dom.rule.employment.unitprice.UnitPriceRepository;
import nts.uk.ctx.pr.core.dom.rule.employment.unitprice.service.UnitPriceHistoryService;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class UnitPriceHistoryServiceImpl.
 */
@Stateless
public class UnitPriceHistoryServiceImpl extends UnitPriceHistoryService {

	/** The unit price history repo. */
	@Inject
	private UnitPriceHistoryRepository unitPriceHistoryRepo;

	/** The unit price repo. */
	@Inject
	private UnitPriceRepository unitPriceRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryBaseService#
	 * deleteHistory(java.lang.String)
	 */
	@Override
	public void deleteHistory(String uuid) {
		UnitPriceHistory history = this.unitPriceHistoryRepo.findHistoryByUuid(uuid).get();
		List<UnitPriceHistory> unitPriceHistoryList = this.unitPriceHistoryRepo
				.findAllHistoryByMasterCode(AppContexts.user().companyCode(),
						history.getMasterCode().v());

		super.deleteHistory(uuid);

		// Remove unit price.
		if (!CollectionUtil.isEmpty(unitPriceHistoryList) && unitPriceHistoryList.size() == 1) {
			this.unitPriceRepo.remove(history.getCompanyCode(), history.getUnitPriceCode());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.rule.employment.unitprice.service.
	 * UnitPriceHistoryService#validateRequiredItem(nts.uk.ctx.pr.core.dom.rule.
	 * employment.unitprice.UnitPriceHistory)
	 */
	public void validateRequiredItem(UnitPriceHistory history) {
		if (history.getUnitPriceCode() == null
				|| StringUtil.isNullOrEmpty(history.getUnitPriceCode().v(), true)
				|| history.getApplyRange() == null || history.getBudget() == null) {
			throw new BusinessException("ER001");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.rule.employment.unitprice.service.
	 * UnitPriceHistoryService#validateDateRange(nts.uk.ctx.pr.core.dom.rule.
	 * employment.unitprice.UnitPriceHistory)
	 */
	public void validateDateRange(UnitPriceHistory unitPriceHistory) {
		if (unitPriceHistoryRepo.isInvalidDateRange(unitPriceHistory.getCompanyCode(),
				unitPriceHistory.getUnitPriceCode(),
				unitPriceHistory.getApplyRange().getStartMonth())) {
			// History after start date and time exists
			throw new BusinessException("ER010");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.rule.employment.unitprice.service.
	 * UnitPriceHistoryService#checkDuplicateCode(nts.uk.ctx.pr.core.dom.rule.
	 * employment.unitprice.UnitPriceHistory)
	 */
	public void checkDuplicateCode(UnitPriceHistory unitPriceHistory) {
		if (unitPriceHistoryRepo.isDuplicateCode(unitPriceHistory.getCompanyCode(),
				unitPriceHistory.getUnitPriceCode())) {
			throw new BusinessException("ER005");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryBaseService#
	 * getRepository()
	 */
	@Override
	public SimpleHistoryRepository<UnitPriceHistory> getRepository() {
		return this.unitPriceHistoryRepo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryBaseService#
	 * createInitalHistory(java.lang.String, java.lang.String)
	 */
	@Override
	public UnitPriceHistory createInitalHistory(String companyCode, String masterCode,
			YearMonth startYearMonth) {
		return UnitPriceHistory.createWithIntial(new CompanyCode(companyCode),
				new UnitPriceCode(masterCode), startYearMonth);
	}
}
