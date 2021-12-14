package nts.uk.file.at.infra.shift.basicworkregister;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.text.StringUtil;
import nts.uk.file.at.app.export.shift.basicworkregister.BasicWorkRegisterReportRepository;
import nts.uk.file.at.app.export.shift.basicworkregister.ClassBasicWorkData;
import nts.uk.file.at.app.export.shift.basicworkregister.CompanyBasicWorkData;
import nts.uk.file.at.app.export.shift.basicworkregister.WorkplaceBasicWorkData;
import nts.uk.shr.com.i18n.TextResource;


@Stateless
public class JpaBasicWorkRegisterReportRepository extends JpaRepository implements BasicWorkRegisterReportRepository {

	private static final String SELECT_COMPANY_BASIC_WORK = "SELECT a.WORK_DAY_ATR, ISNULL(a.WORK_TYPE_CD, ''), "
			+ "ISNULL(b.NAME, '') AS WORK_TYPE_NAME, ISNULL(a.WORKING_CD, ''), ISNULL(c.NAME,'') AS WORK_TIME_NAME  "
			+ " FROM KSCMT_BASIC_WORK_COM a "
			+ " LEFT JOIN KSHMT_WKTP b ON b.CD = a.WORK_TYPE_CD AND b.CID = a.CID"
			+ " LEFT JOIN KSHMT_WT c ON c.WORKTIME_CD = a.WORKING_CD AND c.CID = a.CID AND c.ABOLITION_ATR = 1"
			+ " WHERE a.CID = ?companyId ORDER BY a.WORK_DAY_ATR ASC";
	
	private static final String GET_WORKSPACE_BASIC_WORK = "SELECT a.WKPID, "
			+ "a.WORK_DAY_ATR, ISNULL(a.WORK_TYPE_CD, '') as WORK_TYPE_CD, "
			+ "ISNULL(b.NAME, '') AS WORK_TYPE_NAME, "
			+ "ISNULL(a.WORKING_CD, '') as WORKING_CD, "
			+ "ISNULL(c.NAME,'') AS WORK_TIME_NAME"
			+ "	FROM KSCMT_BASIC_WORK_WKP a "
			+ "	LEFT JOIN KSHMT_WKTP b ON b.CD = a.WORK_TYPE_CD AND b.CID = ?companyId"
			+ "	LEFT JOIN KSHMT_WT c ON c.WORKTIME_CD = a.WORKING_CD AND c.CID = b.CID AND c.ABOLITION_ATR = 1";
	
	private static final String GET_CLASS_BASIC_WORK = "SELECT b.CLSCD, a.CLSNAME, b.WORK_DAY_ATR, "
			+ "ISNULL(b.WORK_TYPE_CD, '') as WORK_TYPE_CD, ISNULL(c.NAME, '') AS WORK_TYPE_NAME, "
			+ "ISNULL(b.WORKING_CD, '') as WORKING_CD, ISNULL(d.NAME,'') AS WORK_TIME_NAME "
			+ "	FROM  KSCMT_BASIC_WORK_CLS b"
			+ "	LEFT JOIN BSYMT_CLASSIFICATION a ON b.CID = a.CID and b.CLSCD = a.CLSCD"
			+ "	LEFT JOIN KSHMT_WKTP c ON c.CD =b.WORK_TYPE_CD and c.CID = b.CID"
			+ "	LEFT JOIN KSHMT_WT d ON d.WORKTIME_CD = b.WORKING_CD AND d.CID = b.CID AND d.ABOLITION_ATR = 1"
			+ "	WHERE b.CID = ?companyId "
			+ "	ORDER BY a.CLSCD ASC";
	
	@Override
	public List<CompanyBasicWorkData> findCompanyBasicWork(String companyId) {		
		List<?> data = this.getEntityManager().createNativeQuery(SELECT_COMPANY_BASIC_WORK)
				.setParameter("companyId", companyId).getResultList();
		return data.stream().map(x -> toDomainBasicWork((Object[])x)).collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	private static CompanyBasicWorkData toDomainBasicWork(Object[] entity){
		int workingDayAtr = ((BigDecimal)entity[0]).intValue();
		String workTypeCD = (String)entity[1];
		String workTypeName = (String)entity[2];
		if (!StringUtil.isNullOrEmpty(workTypeCD, true) && StringUtil.isNullOrEmpty(workTypeName, true)) {
			workTypeName = TextResource.localize("KSM006_13");
		}
		
		String workTimeCD = (String)entity[3];
		String workTimeName = (String)entity[4];
		if (!StringUtil.isNullOrEmpty(workTimeCD, true) && StringUtil.isNullOrEmpty(workTimeName, true)) {
			workTimeName = TextResource.localize("KSM006_13");
		}
		
		val domain = CompanyBasicWorkData.createFromJavaType(workingDayAtr,
				workTypeCD,workTypeName,workTimeCD,workTimeName);
		return domain;
	}
	
	
	@Override
	public List<WorkplaceBasicWorkData> findWorkplaceBasicWork(String companyId) {		
		List<?> data = this.getEntityManager().createNativeQuery(GET_WORKSPACE_BASIC_WORK)
				.setParameter("companyId", companyId).getResultList();
		return data.stream().map(x -> toDomainWorkplaceBasicWork((Object[])x)).collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	private static WorkplaceBasicWorkData toDomainWorkplaceBasicWork(Object[] entity){
		String workplaceId = (String)entity[0];
		int workingDayAtr = ((BigDecimal)entity[1]).intValue();
		String workTypeCD = (String)entity[2];
		String workTypeName = (String)entity[3];
		if (!StringUtil.isNullOrEmpty(workTypeCD, true) && StringUtil.isNullOrEmpty(workTypeName, true)) {
			workTypeName = TextResource.localize("KSM006_13");
		}
		
		String workTimeCD = (String)entity[4];
		String workTimeName = (String)entity[5];
		if (!StringUtil.isNullOrEmpty(workTimeCD, true) && StringUtil.isNullOrEmpty(workTimeName, true)) {
			workTimeName = TextResource.localize("KSM006_13");
		}
		
		val domain = WorkplaceBasicWorkData.createFromJavaType(workplaceId, workingDayAtr,
				workTypeCD, workTypeName, workTimeCD, workTimeName);
		return domain;
	}
	
	
	@Override
	public List<ClassBasicWorkData> findClassBasicWork(String companyId) {
		List<?> data = this.getEntityManager().createNativeQuery(GET_CLASS_BASIC_WORK)
				.setParameter("companyId", companyId).getResultList();
				return data.stream().map(x -> toDomainWithClass((Object[])x)).collect(Collectors.toList());
	}

	private static ClassBasicWorkData toDomainWithClass(Object[] entity) {
		//a.CLSCD, a.CLSNAME, b.WORK_DAY_ATR, WORK_TYPE_CD,WORK_TYPE_NAME, WORKING_CD,WORK_TIME_NAME
		String classCode = (String)entity[0];
		String className = (String)entity[1];
		if (StringUtil.isNullOrEmpty(className, true)) {
			className = TextResource.localize("KSM006_13");
		}
		
		Optional<Integer> workingDayAtr = Optional.empty();
		if (entity[2] != null) {
			workingDayAtr = Optional.of(((BigDecimal)entity[2]).intValue());
		}
		Optional<String> workTypeCD = Optional.ofNullable((String)entity[3]);
		Optional<String> workTypeName = Optional.ofNullable((String)entity[4]);
		if (!StringUtil.isNullOrEmpty(workTypeCD.get(), true) && StringUtil.isNullOrEmpty(workTypeName.get(), true)) {
			workTypeName = Optional.of(TextResource.localize("KSM006_13"));
		}
		
		Optional<String> workTimeCD = Optional.ofNullable((String)entity[5]);
		Optional<String> workTimeName = Optional.ofNullable((String)entity[6]);
		if (!StringUtil.isNullOrEmpty(workTimeCD.get(), true) && StringUtil.isNullOrEmpty(workTimeName.get(), true)) {
			workTimeName = Optional.of(TextResource.localize("KSM006_13"));
		}
		ClassBasicWorkData domain = ClassBasicWorkData.createFromJavaType(
				classCode,className, workingDayAtr, workTypeCD, workTypeName, workTimeCD, workTimeName);
		return domain;
	}

}
