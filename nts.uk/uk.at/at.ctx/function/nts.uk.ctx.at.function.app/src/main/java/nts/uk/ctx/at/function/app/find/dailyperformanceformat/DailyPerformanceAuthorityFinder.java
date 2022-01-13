package nts.uk.ctx.at.function.app.find.dailyperformanceformat;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto.AuthorityDailyFormatDto;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto.DailyAttendanceAuthorityDailyDto;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto.DailyAttendanceAuthorityDetailDto;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityDailyPerformanceFormat;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFomatDaily;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFormatSheet;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.primitivevalue.DailyPerformanceFormatCode;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.repository.AuthorityDailyPerformanceFormatRepository;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.repository.AuthorityFormatDailyRepository;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.repository.AuthorityFormatMonthlyRepository;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.repository.AuthorityFormatSheetRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

@Stateless
public class DailyPerformanceAuthorityFinder {

	@Inject
	private AuthorityDailyPerformanceFormatRepository authorityDailyPerformanceFormatRepository;
	
	@Inject
	private AuthorityFormatSheetRepository authorityFormatSheetRepository;
	
	@Inject
	private AuthorityFormatDailyRepository authorityFormatDailyItemRepository;

	@Inject
	private AuthorityFormatMonthlyRepository authorityFormatMonthlyRepository;

	@Inject
	private AuthorityFormatDailyRepository authorityFormatDailyRepository;

	public DailyAttendanceAuthorityDailyDto findAllDaily(String formatCode, BigDecimal sheetNo) {
		LoginUserContext login = AppContexts.user();
		String companyId = login.companyId();
		// find daily detail
		List<AuthorityFomatDaily> authorityFormatDailies = this.authorityFormatDailyRepository
				.getAuthorityFormatDailyDetail(companyId, new DailyPerformanceFormatCode(formatCode), sheetNo);
		List<DailyAttendanceAuthorityDetailDto> dailyAttendanceAuthorityDetailDtos = authorityFormatDailies
				.stream()
				.map(f -> {
					return new DailyAttendanceAuthorityDetailDto(f.getAttendanceItemId(), 
																 f.getDisplayOrder(),
																 f.getColumnWidth());
				}).collect(Collectors.toList());
		String sheetName = this.authorityFormatSheetRepository
				.find(companyId, new DailyPerformanceFormatCode(formatCode), sheetNo)
				.map(f -> f.getSheetName())
				.orElse(null);
		DailyAttendanceAuthorityDailyDto dailyAttendanceAuthorityDailyDto = new DailyAttendanceAuthorityDailyDto(
				sheetNo, sheetName, dailyAttendanceAuthorityDetailDtos);
		return dailyAttendanceAuthorityDailyDto;
	}

	public List<DailyAttendanceAuthorityDetailDto> findAllMonthly(String formatCode) {
		LoginUserContext login = AppContexts.user();
		String companyId = login.companyId();
		// find monthly detail
		List<DailyAttendanceAuthorityDetailDto> dailyAttendanceAuthorityMonthlyDto = this.authorityFormatMonthlyRepository
				.getMonthlyDetail(companyId, new DailyPerformanceFormatCode(formatCode)).stream().map(f -> {
					return new DailyAttendanceAuthorityDetailDto(f.getAttendanceItemId(), f.getDisplayOrder(),
							f.getColumnWidth());
				}).collect(Collectors.toList());
		return dailyAttendanceAuthorityMonthlyDto;
	}

	public AuthorityDailyFormatDto findAllByCompanyId(String formatCode) {
		String companyId = AppContexts.user().companyId();
		List<AuthorityFormatSheet> listAuthorityFormatSheet = authorityFormatSheetRepository.findByCode(companyId, formatCode);
		List<AuthorityFomatDaily> listAuthorityFomatDaily = authorityFormatDailyItemRepository.getAuthorityFormatDailyDetail(companyId, new DailyPerformanceFormatCode(formatCode));
		
		return AuthorityDailyFormatDto.toDto(listAuthorityFormatSheet, listAuthorityFomatDaily);
	}
}
