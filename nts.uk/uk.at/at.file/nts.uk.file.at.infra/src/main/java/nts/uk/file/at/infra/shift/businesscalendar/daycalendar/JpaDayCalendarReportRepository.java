package nts.uk.file.at.infra.shift.businesscalendar.daycalendar;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.gul.text.StringUtil;
import nts.uk.file.at.app.export.shift.businesscalendar.daycalendar.ClassCalendarReportData;
import nts.uk.file.at.app.export.shift.businesscalendar.daycalendar.CompanyCalendarReportData;
import nts.uk.file.at.app.export.shift.businesscalendar.daycalendar.DayCalendarReportRepository;
import nts.uk.file.at.app.export.shift.businesscalendar.daycalendar.WorkplaceCalendarReportData;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
public class JpaDayCalendarReportRepository extends JpaRepository implements DayCalendarReportRepository{

//	private static final String GET_BEGIN_MONTH_COMPANY = "SELECT a.MONTH_STR FROM BCMMT_COMPANY a WHERE  a.CID = ?companyId";
	private static final String SELECT_COMPANY_CALENDAR_BY_DATE = " SELECT c.CID, c.YMD, c.WORKING_DAY_ATR, c1.EVENT_NAME FROM KSCMT_BUSINESS_CALENDAR_CMP c LEFT JOIN KSCMT_EVENT_CMP c1 on c.CID = c1.CID and c.YMD = c1.YMD" 
			+ " WHERE c.CID = ?companyId"
			+ " AND c.YMD >= ?startDate "
			+ " AND c.YMD <= ?endDate";
	
	private static final String GET_WORKSPACE_CALENDAR_BY_DATE = "SELECT"
			+ " s.WKP_ID, w.WKP_CD, s.YMD, s.WORKING_DAY_ATR, w.WKP_NAME, e.EVENT_NAME "
			+ "	FROM KSCMT_BUSINESS_CALENDAR_WKP s"
			+ " LEFT JOIN BSYMT_WKP_INFO w ON w.CID = ?companyId "
			+ "	and w.WKP_ID = s.WKP_ID"
			+ "	LEFT JOIN KSCMT_EVENT_WKP e ON s.WKP_ID = e.WKPID AND s.YMD = e.YMD_K"
			+ " WHERE s.YMD >= ?startYm"
			+ " AND s.YMD <= ?endYm";
	
	private static final String GET_CLASS_CALENDAR_BY_DATE = "SELECT"
			+ " w.CLSNAME, s.CLS_CD, s.YMD, s.WORKING_DAY_ATR "
			+ "	FROM KSCMT_BUSINESS_CALENDAR_CLS s"
			+ " LEFT JOIN BSYMT_CLASSIFICATION w ON w.CID = s.CID "
			+ "	and w.CLSCD = s.CLS_CD"
			+ " WHERE s.YMD >= ?startYm"
			+ " AND s.YMD <= ?endYm"
			+ " AND s.CID = ?companyId";
	
	
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

	@Override
	public Optional<Map<String, List<CompanyCalendarReportData>>> findCalendarCompanyByDate(String companyId, GeneralDate startDate,
			GeneralDate endDate) {		
		List<?> data = this.getEntityManager().createNativeQuery(SELECT_COMPANY_CALENDAR_BY_DATE)
				.setParameter("companyId", companyId)
				.setParameter("startDate", startDate.date())
				.setParameter("endDate", endDate.date()).getResultList();
				
		List<CompanyCalendarReportData> companyCalendarReportDatas = data.stream().map(x -> toDomainCalendarCompany((Object[])x)).collect(Collectors.toList());
		return Optional.ofNullable(companyCalendarReportDatas.stream().collect(Collectors.groupingBy(CompanyCalendarReportData::getYearMonth)));
	}
	
	/**
	 * toDomanin calendar company
	 * @param entity
	 * @return
	 */
	private static CompanyCalendarReportData toDomainCalendarCompany(Object[] entity){
		String companyId = (String)entity[0];
		Timestamp timeStamp = (Timestamp)entity[1];
		GeneralDate date = GeneralDate.legacyDate(new Date(timeStamp.getTime()));
		int workingDayAtr = ((BigDecimal)entity[2]).intValue();
		String eventName = (String)entity[3];
		val domain = CompanyCalendarReportData.createFromJavaType(companyId,
				date, workingDayAtr, eventName);
		return domain;
	}
	
	
	@Override
	public Optional<Map<String, List<WorkplaceCalendarReportData>>> findCalendarWorkplaceByDate(String companyId, GeneralDate startDate, GeneralDate endDate) {
		List<?> data = this.getEntityManager().createNativeQuery(GET_WORKSPACE_CALENDAR_BY_DATE)
				.setParameter("companyId", companyId)
				.setParameter("startYm", startDate.date())
				.setParameter("endYm", endDate.date()).getResultList();
		
		List<WorkplaceCalendarReportData> workplaceCalendarReportDatas = data.stream().map(x -> toDomainWithWorkspace((Object[])x)).collect(Collectors.toList());
		return Optional.of(workplaceCalendarReportDatas.stream().collect(Collectors.groupingBy(WorkplaceCalendarReportData::getWorkplaceId)));
	}

	private static WorkplaceCalendarReportData toDomainWithWorkspace(Object[] entity) {
//		w.WKPID, s.YMD_K, s.WORKING_DAY_ATR
		String workPlaceId = (String)entity[0];
		Optional<String> workPlaceCode = Optional.ofNullable((String)entity[1]);
//		String workPlaceName = (String)entity[2];
		Timestamp timeStamp = (Timestamp)entity[2];
		GeneralDate date = GeneralDate.legacyDate(new Date(timeStamp.getTime()));
		int workingDayAtr = ((BigDecimal)entity[3]).intValue();
		Optional<String> workPlaceName = Optional.ofNullable((String)entity[4]);
		
		String eventName = (String)entity[5];
		WorkplaceCalendarReportData domain = WorkplaceCalendarReportData.createFromJavaType(
				workPlaceId, date, workingDayAtr, workPlaceCode, workPlaceName, eventName);
		return domain;
	}
	
	@Override
	public Optional<Map<String, List<ClassCalendarReportData>>> findCalendarClassByDate(String companyId, GeneralDate startDate, GeneralDate endDate) {
		List<?> data = this.getEntityManager().createNativeQuery(GET_CLASS_CALENDAR_BY_DATE)
				.setParameter("companyId", companyId)
				.setParameter("startYm", startDate.date())
				.setParameter("endYm", endDate.date()).getResultList();
				List<ClassCalendarReportData> classCalendarReportDatas = data.stream().map(x -> toDomainWithClass((Object[])x)).collect(Collectors.toList());
		return Optional.of(classCalendarReportDatas.stream().collect(Collectors.groupingBy(ClassCalendarReportData::getClassId)));
	}

	private static ClassCalendarReportData toDomainWithClass(Object[] entity) {
		//w.CLSNAME, s.CLSCD, s.YMD_K, s.WORKING_DAY_ATR
		String className = (String) entity[0];
		if (StringUtil.isNullOrEmpty(className, true)) {
			className = TextResource.localize("KSM004_104");
		}
		
		String classCode = (String) entity[1];
		Timestamp timeStamp = (Timestamp)entity[2];
		GeneralDate date = GeneralDate.legacyDate(new Date(timeStamp.getTime()));
		int workingDayAtr = ((BigDecimal)entity[3]).intValue();
		ClassCalendarReportData domain = ClassCalendarReportData.createFromJavaType(
				classCode,date, workingDayAtr, className);
		return domain;
	}
}
