package nts.uk.ctx.at.function.ac.workplace;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.AffWorkplaceHistoryImport;
import nts.uk.ctx.at.function.dom.adapter.AffWorkplaceHistoryItemImport;
import nts.uk.ctx.at.function.dom.adapter.workplace.SyWorkplaceAdapter;
import nts.uk.ctx.at.function.dom.adapter.workplace.WkpConfigAtTimeAdapterDto;
import nts.uk.ctx.bs.employee.pub.workplace.WkpConfigAtTimeExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
@Stateless
public class SyWorkplaceAcFinder implements SyWorkplaceAdapter {

//	@Inject
//	private SyWorkplacePub syWorkplacePub;
	
	@Inject
	private WorkplacePub workplacePub;
	
	@Override
	public List<WkpConfigAtTimeAdapterDto> findByWkpIdsAtTime(String companyId, GeneralDate baseDate,
			List<String> wkpIds) {
		List<WkpConfigAtTimeAdapterDto> data  = workplacePub.findByWkpIdsAtTime(companyId, baseDate, wkpIds)
				.stream().map(c->convertToExport(c)).collect(Collectors.toList());
		return data;
	}
	
	private WkpConfigAtTimeAdapterDto convertToExport(WkpConfigAtTimeExport export) {
		return new WkpConfigAtTimeAdapterDto(
				export.getWorkplaceId(),
				export.getHierarchyCd()
				);
	}

	@Override
	public List<AffWorkplaceHistoryImport> getWorkplaceBySidsAndBaseDate(List<String> sids, GeneralDate baseDate) {
		return this.workplacePub.getWorkplaceBySidsAndBaseDate(sids, baseDate).stream()
				.map(data -> new AffWorkplaceHistoryImport(data.getSid(), data.getHistoryItems(), 
						data.getWorkplaceHistItems().entrySet().stream()
						.collect(Collectors.toMap(
								Entry::getKey, 
								item -> new AffWorkplaceHistoryItemImport(item.getValue().getHistoryId(), 
										item.getValue().getWorkplaceId())))))
				.collect(Collectors.toList());
	}

}
