package nts.uk.ctx.at.record.app.find.dailyperformanceformat;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.BusinessTypeFormatDetailDto;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.BusinessTypeMonthlyDetailDto;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.BusinessTypeFormatMonthly;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.repository.BusinessTypeFormatMonthlyRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

/**
 * 
 * @author nampt
 *
 */
@Stateless
public class BusinessTypeMonthlyDetailFinder {

	@Inject
	private BusinessTypeFormatMonthlyRepository workTypeFormatMonthlyRepository;

	public BusinessTypeMonthlyDetailDto findDetail(String businessTypeCode, BigDecimal sheetNo) {
		LoginUserContext login = AppContexts.user();
		String companyId = login.companyId();
		
		
//		List<WorkTypeFormatDailyDto> workTypeFormatDailyDtos = workTypeFormatDailies.stream().map(f -> {
//			return new WorkTypeFormatDailyDto(
//					f.getAttendanceItemId(),
//					f.getSheetNo(),
//					f.getOrder(), f.getColumnWidth());
//		}).collect(Collectors.toList());
		

		List<BusinessTypeFormatMonthly> workTypeFormatMonthlies = workTypeFormatMonthlyRepository
				.getMonthlyDetail(companyId, businessTypeCode);
		
		List<BusinessTypeFormatDetailDto> workTypeFormatMonthlyDtos = workTypeFormatMonthlies.stream().map(f -> {
			return new BusinessTypeFormatDetailDto(f.getAttendanceItemId(), f.getOrder(), f.getColumnWidth());
		}).collect(Collectors.toList());

		 BusinessTypeMonthlyDetailDto workTypeDetailDto = new BusinessTypeMonthlyDetailDto(workTypeFormatMonthlyDtos);

		return workTypeDetailDto;
	}

}
