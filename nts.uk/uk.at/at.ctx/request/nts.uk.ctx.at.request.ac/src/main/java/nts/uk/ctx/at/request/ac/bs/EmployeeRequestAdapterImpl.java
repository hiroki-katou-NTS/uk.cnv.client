/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.request.ac.bs;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.AffWorkplaceImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.ConcurrentEmployeeRequest;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeEmailImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.PersonEmpBasicInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.PesionInforImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SWkpHistImport;
import nts.uk.ctx.bs.employee.pub.employee.EmployeeBasicInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.ctx.bs.employee.pub.employee.export.PersonEmpBasicInfoPub;
import nts.uk.ctx.bs.employee.pub.employment.SEmpHistExport;
import nts.uk.ctx.bs.employee.pub.employment.SyEmploymentPub;
import nts.uk.ctx.bs.employee.pub.person.IPersonInfoPub;
import nts.uk.ctx.bs.employee.pub.person.PersonInfoExport;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;

/**
 * The Class EmployeeAdaptorImpl.
 */
@Stateless
public class EmployeeRequestAdapterImpl implements EmployeeRequestAdapter {


	/** The employment pub. */
	@Inject
	private SyEmploymentPub employmentPub;

	/** The workplace pub. */
//	@Inject
//	private SyWorkplacePub workplacePub;
	
	@Inject
	private IPersonInfoPub personPub;
	
	@Inject
	private SyEmployeePub syEmployeePub;
	
	@Inject
	private PersonEmpBasicInfoPub perEmpBasicInfoPub;
	
	@Inject
	private WorkplacePub workplacePub;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeAdaptor#
	 * getEmploymentCode(java.lang.String, java.lang.String,
	 * nts.arc.time.GeneralDate)
	 */
	public String getEmploymentCode(String companyId, String employeeId, GeneralDate baseDate) {
		Optional<SEmpHistExport> employmentData = this.employmentPub.findSEmpHistBySid(companyId, employeeId, baseDate);
		if (employmentData.isPresent()){
			return employmentData.get().getEmploymentCode();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeAdaptor#
	 * findWpkIdsBySid(java.lang.String, java.lang.String,
	 * nts.arc.time.GeneralDate)
	 */
	@Override
	public List<String> findWpkIdsBySid(String companyId, String sid, GeneralDate baseDate) {
		// 社員と基準日から所属職場履歴項目を取得する
		String workplaceID = workplacePub.getAffWkpHistItemByEmpDate(sid, baseDate).getWorkplaceId();
		// [No.571]職場の上位職場を基準職場を含めて取得する
		return workplacePub.getWorkplaceIdAndUpper(companyId, baseDate, workplaceID);
	}

	/**
	 * 
	 * @param sID
	 * @return
	 */
	@Override
	public String getEmployeeName(String sID) {
		return this.personPub.getPersonInfo(sID).getBusinessName();
	}

	@Override
	public PesionInforImport getEmployeeInfor(String sID) {
		PersonInfoExport personIn = this.personPub.getPersonInfo(sID);
		PesionInforImport person = new PesionInforImport(personIn.getPid(),
				personIn.getBusinessName(),
				personIn.getEntryDate(),
				personIn.getGender(),
				personIn.getBirthDay(),
				personIn.getEmployeeId(),
				personIn.getEmployeeCode(),
				personIn.getRetiredDate(),
				"");
		return person;
	}

	@Override
	public String empEmail(String sID) {
		//PersonInfoExport data = this.personPub.getPersonInfo(sID);
		/*if(!data.getCompanyMail().isEmpty()) {
			return data.getCompanyMail();
		}else {
			return null;
		}*/
		//TODO mail can xem lai
		return null;
	}

	@Override
	public List<ConcurrentEmployeeRequest> getConcurrentEmployee(String companyId, String jobId, GeneralDate baseDate) {
		List<ConcurrentEmployeeRequest>  data = syEmployeePub.getConcurrentEmployee(companyId, jobId, baseDate)
				.stream()
				.map(x -> new ConcurrentEmployeeRequest(x.getEmployeeId(),
						x.getEmployeeCd(),
						x.getPersonName(),
						x.getJobId(),
						x.getJobCls().value)).collect(Collectors.toList());
		return data;
	}
	
	@Override
	public SEmpHistImport getEmpHist(String companyId, String employeeId,
			GeneralDate baseDate){
		SEmpHistImport sEmpHistImport = new SEmpHistImport();
		Optional<SEmpHistExport> sEmpHistExport = this.employmentPub.findSEmpHistBySid(companyId, employeeId, baseDate);
		if(sEmpHistExport.isPresent()){
			sEmpHistImport.setEmployeeId(sEmpHistExport.get().getEmployeeId());
			sEmpHistImport.setEmploymentCode(sEmpHistExport.get().getEmploymentCode());
			sEmpHistImport.setEmploymentName(sEmpHistExport.get().getEmploymentName());
			sEmpHistImport.setPeriod(sEmpHistExport.get().getPeriod());
			return sEmpHistImport;
		}
		return null;
	}

	// 社員の雇用履歴を全て取得する
	@Override
	public List<SEmpHistImport> getEmpHist(String companyId, String employeeId) {
		
		List<SEmpHistExport> sEmpHistExport = this.employmentPub.findSEmpHistBySid(companyId, employeeId);
		return sEmpHistExport.stream().map(x -> {
			SEmpHistImport sEmpHistImport = new SEmpHistImport();
			sEmpHistImport.setEmployeeId(x.getEmployeeId());
			sEmpHistImport.setEmploymentCode(x.getEmploymentCode());
			sEmpHistImport.setEmploymentName(x.getEmploymentName());
			sEmpHistImport.setPeriod(x.getPeriod());
			return sEmpHistImport;
		}).collect(Collectors.toList());
	}
		
	@Override
	public SWkpHistImport getSWkpHistByEmployeeID(String employeeId, GeneralDate baseDate) {
		Optional<SWkpHistExport> sWkpHistExport = this.workplacePub.findBySid(employeeId, baseDate);
		if(sWkpHistExport.isPresent()){
			SWkpHistImport sWkpHistImport = new SWkpHistImport(sWkpHistExport.get().getDateRange(),
					sWkpHistExport.get().getEmployeeId(),
					sWkpHistExport.get().getWorkplaceId(),
					sWkpHistExport.get().getWorkplaceCode(), 
					sWkpHistExport.get().getWorkplaceName(),
					sWkpHistExport.get().getWkpDisplayName());
			return sWkpHistImport;	
		}
		return null;
	}

	@Override
	public List<EmployeeEmailImport> getApprovalStatusEmpMailAddr(List<String> sIds) {
		List<EmployeeBasicInfoExport> list = this.syEmployeePub.findBySIds(sIds);
		if(list == null) return  Collections.emptyList();
		List<EmployeeEmailImport> data = list
			.stream()
			.map(x-> new EmployeeEmailImport(
						x.getEmployeeId(),
						x.getPName(),
						x.getEntryDate(),
						x.getRetiredDate(),
						null
					)).collect(Collectors.toList());
		return data;
	}

	@Override
	public List<AffWorkplaceImport> getListSIdByWkpIdAndPeriod(String workplaceId, GeneralDate startDate,
			GeneralDate endDate) {
		List<AffWorkplaceImport> data = this.workplacePub
				.findListSIdByCidAndWkpIdAndPeriod(workplaceId, startDate, endDate)
				.stream()
				.map(x -> new AffWorkplaceImport(
						x.getEmployeeId(), 
						x.getJobEntryDate(), 
						x.getRetirementDate()))
				.collect(Collectors.toList());
		return data;
	}

	@Override
	public List<PersonEmpBasicInfoImport> getPerEmpBasicInfo(String companyId, List<String> employeeIds) {
		List<PersonEmpBasicInfoImport> data = this.perEmpBasicInfoPub
				.getEmpBasicInfo(companyId, employeeIds)
				.stream()
				.map(x -> new PersonEmpBasicInfoImport(
						x.getPersonId(), 
						x.getEmployeeId(), 
						x.getBusinessName(),
						x.getGender(),
						x.getBirthday(),
						x.getEmployeeCode(),
						x.getJobEntryDate(),
						x.getRetirementDate()))
				.collect(Collectors.toList());
		
		return data;
	}

	@Override
	public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId) {
		return this.workplacePub.getWorkplaceIdAndUpper(companyId, baseDate, workplaceId);

	}

	@Override
	public String getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date) {
		return workplacePub.getAffWkpHistItemByEmpDate(employeeID, date).getWorkplaceId();
	}
}
