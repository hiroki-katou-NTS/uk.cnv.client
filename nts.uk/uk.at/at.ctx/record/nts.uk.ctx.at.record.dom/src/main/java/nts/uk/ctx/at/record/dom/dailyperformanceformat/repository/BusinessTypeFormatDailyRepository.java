package nts.uk.ctx.at.record.dom.dailyperformanceformat.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import nts.uk.ctx.at.record.dom.dailyperformanceformat.BusinessTypeFormatDaily;

public interface BusinessTypeFormatDailyRepository {
	
	List<BusinessTypeFormatDaily> getBusinessTypeFormat(String companyId, String businessTypeCode);
	
	List<BusinessTypeFormatDaily> getBusinessTypeFormatDailyDetail(String companyId, String businessTypeCode, BigDecimal sheetNo);

	List<BusinessTypeFormatDaily> getBusinessTypeFormatDailyDetail(String companyId, String businessTypeCode);

	void deleteExistData(List<Integer> attendanceItemIds);
	
	void deleteExistDataByCode(String businesstypeCode,String companyId,int sheetNo, List<Integer> attendanceItemIds);
	
	void copy(String companyId, String businessTypeCode, List<String> listBusinessTypeCode);
	
	void update(BusinessTypeFormatDaily businessTypeFormatDaily);
	
	void add(List<BusinessTypeFormatDaily> businessTypeFormatDailies);
	
	boolean checkExistData(String companyId, String businessTypeCode, BigDecimal sheetNo);
	
	// Set header width
	void updateColumnsWidth(Map<Integer, Integer> lstHeader);
	
	List<BusinessTypeFormatDaily> getBusinessTypeFormatByCompanyId(String companyId);
}
