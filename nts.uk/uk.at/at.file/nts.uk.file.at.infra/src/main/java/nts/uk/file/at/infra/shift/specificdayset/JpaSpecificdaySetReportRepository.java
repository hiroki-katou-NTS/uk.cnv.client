package nts.uk.file.at.infra.shift.specificdayset;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.file.at.app.export.shift.specificdayset.SpecificdaySetCompanyReportData;
import nts.uk.file.at.app.export.shift.specificdayset.SpecificdaySetReportRepository;
import nts.uk.file.at.app.export.shift.specificdayset.SpecificdaySetWorkplaceReportData;

@Stateless
public class JpaSpecificdaySetReportRepository extends JpaRepository implements SpecificdaySetReportRepository {
	
	private static final String GET_COMPANY_SPEC_SET = "SELECT s.SPECIFIC_DATE, s.SPECIFIC_DATE_ITEM_NO, p.NAME  FROM KSCMT_SPEC_DATE_COM s"
			+ " INNER JOIN KSCMT_SPEC_DATE_ITEM p "
			+ " ON p.SPECIFIC_DATE_ITEM_NO = s.SPECIFIC_DATE_ITEM_NO "
			+ "	AND s.CID = p.CID "
			+ " WHERE s.CID = ?companyId AND p.USE_ATR = 1"
			+ " AND s.SPECIFIC_DATE >= ?startYm"
			+ " AND s.SPECIFIC_DATE <= ?endYm";
	
	private static final String GET_WORKSPACE_SPEC_SET = "SELECT"
			+ " DISTINCT s.WKPID, s.SPECIFIC_DATE, s.SPECIFIC_DATE_ITEM_NO, w.WKP_CD, p.NAME "
			+ "	FROM KSCMT_SPEC_DATE_WKP s"
			+ " LEFT JOIN KSCMT_SPEC_DATE_ITEM p "
			+ "	ON s.SPECIFIC_DATE_ITEM_NO = p.SPECIFIC_DATE_ITEM_NO "
			+ " LEFT JOIN BSYMT_WKP_INFO w ON w.CID = ?companyId "
			+ "	AND w.WKP_ID = s.WKPID"
			+ " WHERE p.USE_ATR = 1"
			+ " AND s.SPECIFIC_DATE >= ?startYm"
			+ " AND s.SPECIFIC_DATE <= ?endYm";
	
//	private static final String GET_BEGIN_MONTH_COMPANY = "SELECT a.MONTH_STR FROM BCMMT_COMPANY a WHERE  a.CID = ?companyId";
	
	@Override
	public Optional<Map<String, List<SpecificdaySetCompanyReportData>>> findAllSpecificdaySetCompany(String companyId, GeneralDate startDate, GeneralDate endDate) {
		List<?> data = this.getEntityManager().createNativeQuery(GET_COMPANY_SPEC_SET)
				.setParameter("companyId", companyId)
				.setParameter("startYm", startDate.date())
				.setParameter("endYm", endDate.date()).getResultList();
		
		List<SpecificdaySetCompanyReportData> specificdaySetCompanyReportDatas
			= data.stream().map(x -> toDomainWithCompany((Object[])x)).collect(Collectors.toList());
		return Optional.ofNullable(specificdaySetCompanyReportDatas.stream().collect(Collectors.groupingBy(SpecificdaySetCompanyReportData::getYearMonth)));
	}


	@Override
	public Optional<Map<String, List<SpecificdaySetWorkplaceReportData>>> findAllSpecificdaySetWorkplace(String companyId, GeneralDate startDate, GeneralDate endDate) {
		List<?> data = this.getEntityManager().createNativeQuery(GET_WORKSPACE_SPEC_SET)
				.setParameter("companyId", companyId)
				.setParameter("startYm", startDate.date())
				.setParameter("endYm", endDate.date()).getResultList();
		
		List<SpecificdaySetWorkplaceReportData> specificdaySetWorkplaceReportDatas 
			= data.stream().map(x -> toDomainWithWorkspace((Object[])x)).collect(Collectors.toList());
		return Optional.of(specificdaySetWorkplaceReportDatas.stream().collect(Collectors.groupingBy(SpecificdaySetWorkplaceReportData::getWorkplaceId)));
	}
	
	
	private static SpecificdaySetCompanyReportData toDomainWithCompany(Object[] object) {
//		SELECT s.SPECIFIC_DATE, s.SPECIFIC_DATE_ITEM_NO, p.NAME
		Timestamp timeStamp = (Timestamp)object[0];
		GeneralDate date = GeneralDate.legacyDate(new Date(timeStamp.getTime()));
		Integer specificDateItemNo = ((BigDecimal)object[1]).intValue();
		String specificDateItemName = (String) object[2];
		SpecificdaySetCompanyReportData domain = SpecificdaySetCompanyReportData.createFromJavaType(
				date, specificDateItemNo, specificDateItemName);
		return domain;
	}
		
	private static SpecificdaySetWorkplaceReportData toDomainWithWorkspace(Object[] object) {
//		DISTINCT s.WKPID, s.SPECIFIC_DATE, s.SPECIFIC_DATE_ITEM_NO, w.WKPCD, p.NAME
		String workplaceId = (String) object[0];
		Timestamp timeStamp = (Timestamp)object[1];
		GeneralDate date = GeneralDate.legacyDate(new Date(timeStamp.getTime()));
		Integer specificDateItemNo = ((BigDecimal)object[2]).intValue();
		Optional<String> workplaceCode = Optional.ofNullable((String) object[3]);
		String specificDateItemName = (String) object[4];
//		String workplaceName = (String) object[5];
		
		SpecificdaySetWorkplaceReportData domain = SpecificdaySetWorkplaceReportData.createFromJavaType(
				workplaceId, date, specificDateItemNo, specificDateItemName, workplaceCode,Optional.empty());
		return domain;
	}
	
//	@Override
//	public Period getBaseDateByCompany(String companyId, GeneralDate startExportDate, GeneralDate endExportDate) {
//		Integer beginMonth = ((BigDecimal)this.getEntityManager().createNativeQuery(GET_BEGIN_MONTH_COMPANY)
//				.setParameter("companyId", companyId).getSingleResult()).intValue();
//		int startYear = startExportDate.year();
//		int endYear = endExportDate.year();
//		
//		GeneralDate start = GeneralDate.ymd(startYear, beginMonth, 1);
//		
//		int endMonth = beginMonth - 1;
//		if (endMonth > 0) {
//			endYear = endYear + 1;
//		}
//		else {
//			endMonth = 12;
//		}
//		GeneralDate startdateOfEndMonth = GeneralDate.ymd(endYear, endMonth, 1);
//		GeneralDate end = GeneralDate.ymd(endYear, endMonth, startdateOfEndMonth.lastDateInMonth());
//		
//		return new Period(start, end);
//	}

}
