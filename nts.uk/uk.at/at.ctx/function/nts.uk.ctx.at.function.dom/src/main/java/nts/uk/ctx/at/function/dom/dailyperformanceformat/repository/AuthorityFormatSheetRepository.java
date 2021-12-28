package nts.uk.ctx.at.function.dom.dailyperformanceformat.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFormatSheet;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.primitivevalue.DailyPerformanceFormatCode;

public interface AuthorityFormatSheetRepository {
	
	Optional<AuthorityFormatSheet> find(String companyId, DailyPerformanceFormatCode dailyPerformanceFormatCode, BigDecimal sheetNo);
	
	List<AuthorityFormatSheet> findByCode(String companyId, String dailyPerformanceFormatCode);
	
	void add(AuthorityFormatSheet authorityFormatSheet);
	
	void add(List<AuthorityFormatSheet> listAuthorityFormatSheet);
	
	void update(AuthorityFormatSheet authorityFormatSheet);
	
	void remove(String companyId, DailyPerformanceFormatCode dailyPerformanceFormatCode);
	
	boolean checkExistData(String companyId, DailyPerformanceFormatCode dailyPerformanceFormatCode, BigDecimal sheetNo);
	
	void deleteBySheetNo(String companyId, String dailyPerformanceFormatCode,BigDecimal sheetNo);
}
