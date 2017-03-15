/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
/**
 * 
 */
package nts.uk.file.pr.app.export.insurance;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.uk.file.pr.app.export.insurance.data.ColumnInformation;
import nts.uk.file.pr.app.export.insurance.data.EmployeeDto;
import nts.uk.file.pr.app.export.insurance.data.InsuranceOfficeDto;
import nts.uk.file.pr.app.export.insurance.data.SocialInsuranceHeaderReportData;
import nts.uk.file.pr.app.export.insurance.data.SocialInsuranceReportData;

/**
 * @author duongnd
 *
 */

@Stateless
public class SocialInsuranceReportService extends ExportService<SocialInsuranceQuery> {

    @Inject
    SocialInsuranceGenerator generator;
    
    @Override
    protected void handle(ExportServiceContext<SocialInsuranceQuery> context) {
        // get query
        SocialInsuranceQuery query = context.getQuery();
        // get data from repository follow query.
        SocialInsuranceReportData reportData = fakeData();
        this.generator.generate(context.getGeneratorContext(), reportData);
    }
    
    private SocialInsuranceReportData fakeData() {
        SocialInsuranceReportData reportData = new SocialInsuranceReportData();
        
        SocialInsuranceHeaderReportData headerData = new SocialInsuranceHeaderReportData();
        reportData.setHeaderData(headerData);
        
        List<InsuranceOfficeDto> officeItems = fakeOffice();
        reportData.setOfficeItems(officeItems);
        
        return reportData;
    }
    
    private List<InsuranceOfficeDto> fakeOffice() {
        List<InsuranceOfficeDto> offices = new ArrayList<>();
        for(int i=0; i<10; i++) {
            InsuranceOfficeDto office = setOffice(i + 1);
            offices.add(office);
        }
        return offices;
    }
    private InsuranceOfficeDto setOffice(int index) {
        InsuranceOfficeDto office = new InsuranceOfficeDto();
        
        office.setNumberOfEmployee(5);
        office.setOfficeCode("A000" + index);
        office.setOfficeName("Office " + index);
        
        List<EmployeeDto> employees = new ArrayList<>();
        for (int i=0; i< office.getNumberOfEmployee(); i++) {
            EmployeeDto employee = setEmployee("Employee", 1);
            employees.add(employee);
        }
        office.setEmployeeDtos(employees);
        
        return office;
    }
    
    private EmployeeDto setEmployee(String name, int indexRaw) {
        double index = indexRaw;
        EmployeeDto employee = new EmployeeDto();
        
        employee.setEmployeeCode("E000" + index);
        employee.setEmployeeName(name +" " + index);
        
        employee.setMonthlyHealthInsuranceNormal(index);index++;
        employee.setMonthlyGeneralInsuranceNormal(index);index++;
        employee.setMonthlyLongTermInsuranceNormal(index);index++;
        employee.setMonthlySpecificInsuranceNormal(index);index++;
        employee.setMonthlyBasicInsuranceNormal(index);index++;
        
        employee.setMonthlyHealthInsuranceDeduction(index);index++;
        employee.setMonthlyGeneralInsuranceDeduction(index);index++;
        employee.setMonthlyLongTermInsuranceDeduction(index);index++;
        employee.setMonthlySpecificInsuranceDeduction(index);index++;
        employee.setMonthlyBasicInsuranceDeduction(index);index++;
        
        employee.setWelfarePensionInsuranceNormal(index);index++;
        employee.setWelfarePensionInsuranceDeduction(index);index++;
        employee.setWelfarePensionFundNormal(index);index++;
        employee.setWelfarePensionFundDeduction(index);index++;
        employee.setChildRaisingContributionMoney(index);index++;
        
        return employee;
    }

}
