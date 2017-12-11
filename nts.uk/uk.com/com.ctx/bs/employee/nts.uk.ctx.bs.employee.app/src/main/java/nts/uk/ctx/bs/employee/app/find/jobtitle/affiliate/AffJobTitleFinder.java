/**
 * 
 */
package nts.uk.ctx.bs.employee.app.find.jobtitle.affiliate;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryItem;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryItemRepository_v1;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryRepository_ver1;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistory_ver1;
import nts.uk.shr.pereg.app.ComboBoxObject;
import nts.uk.shr.pereg.app.find.PeregFinder;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.dto.DataClassification;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

/**
 * @author danpv
 *
 */
@Stateless
public class AffJobTitleFinder implements PeregFinder<AffJobTitleDto> {

	@Inject
	private AffJobTitleHistoryItemRepository_v1 affJobTitleItemRepo;

	@Inject
	private AffJobTitleHistoryRepository_ver1 affJobTitleRepo;

	@Override
	public String targetCategoryCode() {
		return "CS00016";
	}

	@Override
	public Class<AffJobTitleDto> dtoClass() {
		return AffJobTitleDto.class;
	}

	@Override
	public DataClassification dataType() {
		return DataClassification.EMPLOYEE;
	}

	@Override
	public PeregDomainDto getSingleData(PeregQuery query) {
		
		Optional<AffJobTitleHistory_ver1> historyOpt;
		if ( query.getInfoId() != null ) {
			historyOpt = affJobTitleRepo.getByHistoryId(query.getInfoId());
		} else {
			historyOpt = affJobTitleRepo.getByEmpIdAndStandardDate(query.getEmployeeId(), query.getStandardDate());
		}
		
		if ( historyOpt.isPresent()) {
			Optional<AffJobTitleHistoryItem> histItemOpt = affJobTitleItemRepo
					.findByHitoryId(historyOpt.get().getHistoryItems().get(0).identifier());
			if ( histItemOpt.isPresent()) {
				return AffJobTitleDto.createFromDomain(histItemOpt.get(), historyOpt.get());
			}
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.shr.pereg.app.find.PeregFinder#getListData(nts.uk.shr.pereg.app.
	 * find.PeregQuery)
	 */
	@Override
	public List<PeregDomainDto> getListData(PeregQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ComboBoxObject> getListFirstItems(PeregQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

}
