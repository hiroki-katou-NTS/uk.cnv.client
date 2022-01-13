/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.ac.employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employee.AffComHistItemShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.AffCompanyHistSharedImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.ClassificationImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeBasicInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeRecordImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.MailAddress;
import nts.uk.ctx.at.shared.dom.adapter.employee.PersonEmpBasicInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.SClsHistImport;
import nts.uk.ctx.bs.employee.pub.classification.SClsHistExport;
import nts.uk.ctx.bs.employee.pub.classification.SyClassificationPub;
import nts.uk.ctx.bs.employee.pub.company.AffCompanyHistExport;
import nts.uk.ctx.bs.employee.pub.company.SyCompanyPub;
import nts.uk.ctx.bs.employee.pub.employee.EmployeeBasicInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.EmployeeDataMngInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.ResultRequest600Export;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.ctx.bs.employee.pub.employee.export.PersonEmpBasicInfoPub;
import nts.uk.ctx.bs.employee.pub.employee.export.dto.PersonEmpBasicInfoDto;

/**
 * The Class EmpEmployeeAdapterImpl.
 */
@Stateless
public class EmpEmployeeAdapterImpl implements EmpEmployeeAdapter {

	/** The employee pub. */
	@Inject
	private SyEmployeePub employeePub;

	@Inject
	private PersonEmpBasicInfoPub personEmpBasicInfoPub;

	@Inject
	private SyCompanyPub syCompanyPub;
	@Inject
	private SyClassificationPub classPub;

	// @Inject
	// private PersonPub personPub;

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter#findByEmpId(java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EmployeeImport findByEmpId(String empId) {
		val cacheCarrier = new CacheCarrier();
		return findByEmpIdRequire(cacheCarrier, empId);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EmployeeImport findByEmpIdRequire(CacheCarrier cacheCarrier, String empId) {
		// Get Employee Basic Info
		EmployeeBasicInfoExport empExport = employeePub.findBySIdRequire(cacheCarrier, empId);
		// Check Null
		if (empExport != null) {
			// Map to EmployeeImport
			EmployeeImport empDto = EmployeeImport.builder()
					.employeeId(empExport.getEmployeeId())
					.employeeCode(empExport.getEmployeeCode())
					.employeeName(empExport.getPName())
					.employeeMailAddress(
							empExport.getPMailAddr() == null ? null : (new MailAddress(empExport.getPMailAddr().v())))
					.entryDate(empExport.getEntryDate())
					.retiredDate(empExport.getRetiredDate())
					.build();
			return empDto;
		}
		return null;
	}

	@Override
	public List<EmployeeImport> findByEmpId(List<String> empIds) {
		// Get Employee Basic Info
		List<EmployeeBasicInfoExport> empExportList = employeePub.findBySIds(empIds);
		List<EmployeeImport> lstEmp =  empExportList.stream().map(empExport -> EmployeeImport.builder().employeeId(empExport.getEmployeeId())
				.employeeCode(empExport.getEmployeeCode()).employeeName(empExport.getPName())
				.employeeMailAddress(
						empExport.getPMailAddr() == null ? null : (new MailAddress(empExport.getPMailAddr().v())))
				.entryDate(empExport.getEntryDate()).retiredDate(empExport.getRetiredDate()).build())
				.collect(Collectors.toList());
        lstEmp = lstEmp.stream().sorted((a, b) -> a.getEmployeeCode().compareTo(b.getEmployeeCode())).collect(Collectors.toList());
        return lstEmp;
	}

	@Override
	public List<String> getListEmpByWkpAndEmpt(List<String> wkps, List<String> lstempts, DatePeriod dateperiod) {
		List<String> data = employeePub.getListEmpByWkpAndEmpt(wkps, lstempts, dateperiod);
	if (data.isEmpty()){
		return new ArrayList<>();
	}
		return data;
	}

	@Override
	public List<PersonEmpBasicInfoImport> getPerEmpBasicInfo(List<String> employeeIds) {
		List<PersonEmpBasicInfoDto> export = personEmpBasicInfoPub.getPerEmpBasicInfo(employeeIds);
		List<PersonEmpBasicInfoImport> data = new ArrayList<>();
			if(export.isEmpty()){
				return new ArrayList<>();
			}else{
			data = export.stream().map(c ->{
				return new PersonEmpBasicInfoImport(
						c.getPersonId(),
						c.getEmployeeId(),
						c.getBusinessName(),
						c.getGender(),
						c.getBirthday(),
						c.getEmployeeCode(),
						c.getJobEntryDate(),
						c.getRetirementDate());
						}).collect(Collectors.toList());
					}
		return data;
	}

	@Override
	public List<AffCompanyHistSharedImport> getAffCompanyHistByEmployee(CacheCarrier cacheCarrier, List<String> sids, DatePeriod datePeriod) {
		List<AffCompanyHistSharedImport> importList = this.syCompanyPub.GetAffCompanyHistByEmployeeRequire(cacheCarrier, sids, datePeriod).stream()
				.map(x -> convert(x)).collect(Collectors.toList());
		return importList;
	}
	private AffCompanyHistSharedImport convert(AffCompanyHistExport dataExpprt) {
		List<AffComHistItemShareImport> subListImport = dataExpprt.getLstAffComHistItem().stream()
				.map(x -> new AffComHistItemShareImport(x.getHistoryId(), x.isDestinationData(), x.getDatePeriod()))
				.collect(Collectors.toList());
		return new AffCompanyHistSharedImport(dataExpprt.getEmployeeId(), subListImport);
	}

	@Override
	public EmployeeRecordImport findByAllInforEmpId(CacheCarrier cacheCarrier, String empId) {
		// Get Employee Basic Info
		EmployeeBasicInfoExport empExport = employeePub.findBySIdRequire(cacheCarrier, empId);
		// Check Null
		if (empExport != null) {
			// Map to EmployeeImport
			EmployeeRecordImport empDto = new EmployeeRecordImport(empExport.getPId(),
					empExport.getEmployeeId(),
					empExport.getPName(),
					empExport.getGender(),
					empExport.getBirthDay(),
					empExport.getPMailAddr() == null ? null : (new MailAddress(empExport.getPMailAddr().v())),
					empExport.getEmployeeCode(),
					empExport.getEntryDate(),
					empExport.getRetiredDate(),
					empExport.getPMailAddr() == null ? null : (new MailAddress(empExport.getCompanyMailAddr().v())));
			return empDto;
		}
		return null;
	}

	@Override
	public List<SClsHistImport> lstClassByEmployeeId(CacheCarrier cacheCarrier,String companyId, List<String> employeeIds,
			DatePeriod datePeriod) {
		List<SClsHistExport> lstExport = classPub.findSClsHistBySidRequire(cacheCarrier, companyId, employeeIds, datePeriod);
		return lstExport.stream().map(x -> new SClsHistImport(x.getPeriod(), x.getEmployeeId(), x.getClassificationCode(), x.getClassificationName()))
				.collect(Collectors.toList());
	}

	@Override
	public AffCompanyHistSharedImport GetAffComHisBySidAndBaseDate(String sid, GeneralDate baseDate) {
		AffCompanyHistSharedImport importList = convert(this.syCompanyPub.GetAffComHisBySidAndBaseDate(sid, baseDate));
		return importList;
	}

	@Override
	public AffCompanyHistSharedImport GetAffComHisBySid(String cid, String sid){
		AffCompanyHistSharedImport importList = convert(this.syCompanyPub.GetAffComHisBySid(cid,sid));
		return importList;
	}

	@Override
	public List<AffCompanyHistSharedImport> getAffComHisBySids(String cid, List<String> sids) {
		List<AffCompanyHistSharedImport> result =  this.syCompanyPub.getAffComHisBySids(cid, sids).stream().map(c -> convert(c)).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<ClassificationImport> getClassificationByCompanyId(String companyId) {
		return this.classPub.getClassificationByCompanyId(companyId).stream()
				.map(x -> new ClassificationImport(x.getCompanyId(), x.getClassificationCode(),x.getClassificationName(),x.getMemo()))
				.collect(Collectors.toList());
	}

	@Override
	public List<EmployeeBasicInfoImport> getEmpInfoLstBySids(List<String> sids, DatePeriod period, boolean isDelete, boolean isGetAffCompany) {
		List<ResultRequest600Export> data = employeePub.getEmpInfoLstBySids(sids, period, isDelete, isGetAffCompany);
		if (data.isEmpty())
			return Collections.emptyList();
		return data.stream().map(c -> new EmployeeBasicInfoImport(c.getSid(), c.getEmployeeCode(), c.getEmployeeName()))
				.collect(Collectors.toList());
	}

	//社員コードから社員IDを取得する
	@Override
	public Map<String, String> getEmployeeIDListByCode(String companyId, List<String> employeeCodes) {
		return employeePub.findSdataMngInfoByEmployeeCodes(companyId, employeeCodes)
				.stream().collect(Collectors.toMap(EmployeeDataMngInfoExport::getEmployeeCode, EmployeeDataMngInfoExport::getEmployeeId));
	}
}