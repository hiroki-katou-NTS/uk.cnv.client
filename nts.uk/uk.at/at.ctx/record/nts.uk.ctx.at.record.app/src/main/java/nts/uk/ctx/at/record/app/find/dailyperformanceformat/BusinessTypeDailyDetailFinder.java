package nts.uk.ctx.at.record.app.find.dailyperformanceformat;

import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
//import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

//import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.AttendanceItemDto;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.BusinessTypeFormatDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.BusinessTypeFormatDetailDto;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.BusinessFormatSheet;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.BusinessTypeFormatDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.repository.BusinessFormatSheetRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.repository.BusinessTypeFormatDailyRepository;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;
//import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.service.CompanyDailyItemService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

/**
 * 
 * @author nampt
 *
 */
@Stateless
public class BusinessTypeDailyDetailFinder {

	@Inject
	private BusinessTypeFormatDailyRepository workTypeFormatDailyRepository;

	@Inject
	private BusinessFormatSheetRepository businessFormatSheetRepository;

//	@Inject
//	private CompanyDailyItemService companyDailyItemService;
	
	public List<String> getAllBusinessCode() {
		
		String companyId = AppContexts.user().companyId();
		
		List<String> listBusinessCode = workTypeFormatDailyRepository.getBusinessTypeFormatByCompanyId(companyId).stream()
							.map(e -> e.getBusinessTypeCode().v())
							.distinct()
							.collect(Collectors.toList());
		
		return listBusinessCode;
	}

	public BusinessTypeFormatDailyDto getDetail(String businessTypeCode, BigDecimal sheetNo) {
		LoginUserContext login = AppContexts.user();
		String companyId = login.companyId();
		// find daily attendance item
		/*List<AttendanceItemDto> dailyAttItem = companyDailyItemService
				.getDailyItems(companyId, Optional.empty(), Collections.emptyList(), Collections.emptyList()).stream()
				.map(x -> {
					AttendanceItemDto dto = new AttendanceItemDto();
					dto.setAttendanceItemId(x.getAttendanceItemId());
					dto.setAttendanceItemName(x.getAttendanceItemName());
					dto.setAttendanceItemDisplayNumber(x.getAttendanceItemDisplayNumber());
					return dto;
				}).collect(Collectors.toList());
		if (dailyAttItem.isEmpty()) {
			return null;
		}
		Map<Integer, AttendanceItemDto> attendanceItemMaps = dailyAttItem.stream()
				.collect(Collectors.toMap(AttendanceItemDto::getAttendanceItemId, x -> x));*/
		List<BusinessTypeFormatDaily> businessTypeFormatDailies = workTypeFormatDailyRepository
				.getBusinessTypeFormatDailyDetail(companyId, businessTypeCode, sheetNo);
		List<BusinessTypeFormatDetailDto> businessTypeFormatDetailDtos = businessTypeFormatDailies.stream()
				.map(x -> new BusinessTypeFormatDetailDto(x.getAttendanceItemId(), x.getOrder(), x.getColumnWidth()))
				.collect(Collectors.toList());
		/*if (!businessTypeFormatDailies.isEmpty()) {
			businessTypeFormatDetailDtos = businessTypeFormatDailies.stream().map(f -> {
				if (attendanceItemMaps.containsKey(f.getAttendanceItemId()))
					return new BusinessTypeFormatDetailDto(f.getAttendanceItemId(),
							attendanceItemMaps.get(f.getAttendanceItemId()).getAttendanceItemDisplayNumber(),
							attendanceItemMaps.get(f.getAttendanceItemId()).getAttendanceItemName(), f.getOrder(),
							f.getColumnWidth());
				return null;
			}).collect(Collectors.toList());
		}*/
		
		Optional<BusinessFormatSheet> businessFormatSheet = businessFormatSheetRepository.getSheetInformation(companyId,
				new BusinessTypeCode(businessTypeCode), sheetNo);
		BusinessTypeFormatDailyDto businessTypeFormatDailyDto = new BusinessTypeFormatDailyDto(sheetNo,
				businessFormatSheet.isPresent() ? businessFormatSheet.get().getSheetName() : null,
				businessTypeFormatDetailDtos);
		return businessTypeFormatDailyDto;
	}
	
	public int checkDailyMode(String businessTypeCode) {
		
		String companyId = AppContexts.user().companyId();
		List<BusinessTypeFormatDaily> listBusinessTypeFormatDaily = workTypeFormatDailyRepository.getBusinessTypeFormatDailyDetail(companyId, businessTypeCode);
		
		if (listBusinessTypeFormatDaily.isEmpty()) {
			return 0;
		}
		
		return 1;
	}
}
