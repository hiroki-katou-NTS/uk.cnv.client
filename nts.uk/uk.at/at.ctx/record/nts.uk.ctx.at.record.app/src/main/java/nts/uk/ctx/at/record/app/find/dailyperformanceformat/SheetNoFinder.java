package nts.uk.ctx.at.record.app.find.dailyperformanceformat;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.SheetNo;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.BusinessTypeFormatDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.repository.BusinessTypeFormatDailyRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

/**
 * 
 * @author nampt
 *
 */
@Stateless
public class SheetNoFinder {
	

	@Inject
	private BusinessTypeFormatDailyRepository workTypeFormatDailyRepository;
	
	public SheetNo getSheetNo(String businessTypeCode){
		LoginUserContext login = AppContexts.user();
		String companyId = login.companyId();
		
		List<BusinessTypeFormatDaily> workTypeFormatDailies = workTypeFormatDailyRepository.getBusinessTypeFormat(companyId,
				businessTypeCode);
		
		List<BigDecimal> sheetNos = workTypeFormatDailies.stream().map(f -> {
			return f.getSheetNo();
		}).collect(Collectors.toList());
		
		SheetNo sheetNo = new SheetNo(sheetNos);
		
		return sheetNo;
	}
	
	public List<String> getAllBusinessTypeCode() {
		
		String companyId = AppContexts.user().companyId();
		
		List<String> listBusinessTypeCode = workTypeFormatDailyRepository.getBusinessTypeFormatByCompanyId(companyId).stream()
				.map(e -> e.getBusinessTypeCode().v())
				.distinct()
				.collect(Collectors.toList());
		
		return listBusinessTypeCode;
	}

}
