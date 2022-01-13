package nts.uk.ctx.at.record.dom.dailyperformanceformat.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nts.uk.ctx.at.record.dom.dailyperformanceformat.BusinessTypeFormatMonthly;

public interface BusinessTypeFormatMonthlyRepository {

	List<BusinessTypeFormatMonthly> getMonthlyDetail(String companyId, String workTypeCode);

	List<BusinessTypeFormatMonthly> getListBusinessTypeFormat(String companyId, Collection<String> listBusinessTypeCode);

	void update(BusinessTypeFormatMonthly businessTypeFormatMonthly);

	void updateColumnsWidth(String companyId, Map<Integer, Integer> lstHeader, List<String> formatCodes);

	void deleteExistData(String companyId, String businessTypeCode, List<Integer> attendanceItemIds);

	void add(List<BusinessTypeFormatMonthly> businessTypeFormatMonthlyAdds);
	
	void copy(String companyId, String businessTypeCode, List<String> listBusinessTypeCode);

	boolean checkExistData(String companyId, String businessTypeCode);

	List<BusinessTypeFormatMonthly> getMonthlyDetailByCompanyId(String companyId);

}
