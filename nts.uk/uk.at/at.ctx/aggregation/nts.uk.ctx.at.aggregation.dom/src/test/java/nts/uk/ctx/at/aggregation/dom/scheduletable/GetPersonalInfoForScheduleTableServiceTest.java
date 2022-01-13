package nts.uk.ctx.at.aggregation.dom.scheduletable;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.aggregation.dom.adapter.rank.EmployeeRankInfoImported;
import nts.uk.ctx.at.aggregation.dom.adapter.team.EmployeeTeamInfoImported;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpLicenseClassification;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.GetEmpLicenseClassificationService;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.LicenseClassification;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.EmployeeInfoWantToBeGet;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.ClassificationImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.EmployeeInfoImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.EmploymentImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.PositionImported;

public class GetPersonalInfoForScheduleTableServiceTest {
	
	@Injectable
	GetPersonalInfoForScheduleTableService.Require require;
	
	private static final List<String> employeeIds = Arrays.asList("employee-id1", "employee-id2");
	private static final GeneralDate date = GeneralDate.ymd(2021, 7, 1);
	
	@Test
	public void testGet_onlyEmployeeName() {
		
		// Arrange
		new Expectations() {{
			require.getEmployeeInfo(employeeIds, date, new EmployeeInfoWantToBeGet(false, false, false, false, false));
			result = Arrays.asList(
					new EmployeeInfoImported("employee-id1", "employee-code1", "business-name1", "business-name-kana1", 
							Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
					new EmployeeInfoImported("employee-id2", "employee-code2", "business-name2", "business-name-kana2", 
							Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())
					);
		}};
		
		// Act
		val result = GetPersonalInfoForScheduleTableService.get(
				require, 
				Arrays.asList("employee-id1", "employee-id2"), 
				GeneralDate.ymd(2021, 7, 1), 
				Arrays.asList(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME));
		
		// Assert
		assertThat(result)
			.extracting(d -> d.getEmployeeId())
			.containsExactly("employee-id1", "employee-id2");
		
		val employee1_personalInfoMap = result.get(0).getPersonalInfoMap();
		assertThat(employee1_personalInfoMap.keySet()).containsExactly(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME);
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getCode()).isEqualTo("employee-code1");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getName()).isEqualTo("business-name1");
		
		val employee2_personalInfoMap = result.get(1).getPersonalInfoMap();
		assertThat(employee2_personalInfoMap.keySet()).containsExactly(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME);
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getCode()).isEqualTo("employee-code2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getName()).isEqualTo("business-name2");
	}
	
	@Test
	public void testGet_AllInfo() {
		
		// Arrange
		new Expectations(GetEmpLicenseClassificationService.class, EnumAdaptor.class) {{
			
			require.getEmployeeInfo(employeeIds, date, new EmployeeInfoWantToBeGet(false, false, true, true, true));
			result = Arrays.asList(
					new EmployeeInfoImported("employee-id1", "employee-code1", "business-name1", "business-name-kana1", 
							Optional.empty(), // 職場
							Optional.empty(), // 部門
							Optional.of(new PositionImported("job-title-id1", "job-title-code1", "job-title-name1")), // 職位
							Optional.of(new EmploymentImported("employment-code1", "employment-name1")), // 雇用
							Optional.empty()), // 分類 empty
					
					new EmployeeInfoImported("employee-id2", "employee-code2", "business-name2", "business-name-kana2", 
							Optional.empty(), // 職場
							Optional.empty(), // 部門
							Optional.empty(), // 職位 empty
							Optional.of(new EmploymentImported("employment-code2", "employment-name2")), // 雇用
							Optional.of(new ClassificationImported("classification-code2", "classification-name2"))) // 分類
					);
			
			require.getEmployeeTeamInfo(employeeIds);
			result = Arrays.asList(
					new EmployeeTeamInfoImported("employee-id1", Optional.of("team-code1"), Optional.of("team-name1")),
					new EmployeeTeamInfoImported("employee-id2", Optional.empty(), Optional.empty())); // empty
			
			require.getEmployeeRankInfo(employeeIds);
			result = Arrays.asList(
					new EmployeeRankInfoImported("employee-id1", Optional.empty(), Optional.empty()), // empty
					new EmployeeRankInfoImported("employee-id2", Optional.of("rank-code2"), Optional.of("rank-symbol2")));
			
			GetEmpLicenseClassificationService.get(require, date, employeeIds);
			result = Arrays.asList(
					new EmpLicenseClassification("employee-id1", Optional.of(LicenseClassification.NURSE)),
					new EmpLicenseClassification("employee-id2", Optional.of(LicenseClassification.NURSE_ASSIST)));
			
			EnumAdaptor.convertToValueName(LicenseClassification.NURSE);
			result = new EnumConstant(0, "NURSE", "看護師");
			EnumAdaptor.convertToValueName(LicenseClassification.NURSE_ASSIST);
			result = new EnumConstant(2, "NURSE_ASSIST", "看護補助者");
		}};
		
		// Act
		val result = GetPersonalInfoForScheduleTableService.get(
				require, 
				Arrays.asList("employee-id1", "employee-id2"), 
				GeneralDate.ymd(2021, 7, 1), 
				Arrays.asList(
						ScheduleTablePersonalInfoItem.EMPLOYEE_NAME,
						ScheduleTablePersonalInfoItem.EMPLOYMENT,
						ScheduleTablePersonalInfoItem.JOBTITLE,
						ScheduleTablePersonalInfoItem.CLASSIFICATION,
						ScheduleTablePersonalInfoItem.TEAM,
						ScheduleTablePersonalInfoItem.RANK,
						ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION));
		
		// Assert
		assertThat(result)
			.extracting(d -> d.getEmployeeId())
			.containsExactly("employee-id1", "employee-id2");
		
		val employee1_personalInfoMap = result.get(0).getPersonalInfoMap();
		assertThat(employee1_personalInfoMap.keySet()).containsExactlyInAnyOrder(
				ScheduleTablePersonalInfoItem.EMPLOYEE_NAME,
				ScheduleTablePersonalInfoItem.EMPLOYMENT,
				ScheduleTablePersonalInfoItem.JOBTITLE,
				// ScheduleTablePersonalInfoItem.CLASSIFICATION,
				ScheduleTablePersonalInfoItem.TEAM,
				// ScheduleTablePersonalInfoItem.RANK,
				ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION);
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getCode()).isEqualTo("employee-code1");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getName()).isEqualTo("business-name1");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYMENT).getCode()).isEqualTo("employment-code1");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYMENT).getName()).isEqualTo("employment-name1");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.JOBTITLE).getCode()).isEqualTo("job-title-code1");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.JOBTITLE).getName()).isEqualTo("job-title-name1");
		//assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.CLASSIFICATION).getCode()).isEqualTo("classification-code1");
		//assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.CLASSIFICATION).getName()).isEqualTo("classification-name1");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.TEAM).getCode()).isEqualTo("team-code1");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.TEAM).getName()).isEqualTo("team-name1");
		//assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.RANK).getCode()).isEqualTo("rank-code1");
		//assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.RANK).getName()).isEqualTo("rank-symbol1");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION).getCode()).isEqualTo("0");
		assertThat(employee1_personalInfoMap.get(ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION).getName()).isEqualTo("看護師");
		
		val employee2_personalInfoMap = result.get(1).getPersonalInfoMap();
		assertThat(employee2_personalInfoMap.keySet()).containsExactlyInAnyOrder(
				ScheduleTablePersonalInfoItem.EMPLOYEE_NAME,
				ScheduleTablePersonalInfoItem.EMPLOYMENT,
				// ScheduleTablePersonalInfoItem.JOBTITLE,
				ScheduleTablePersonalInfoItem.CLASSIFICATION,
				// ScheduleTablePersonalInfoItem.TEAM,
				ScheduleTablePersonalInfoItem.RANK,
				ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION);
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getCode()).isEqualTo("employee-code2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getName()).isEqualTo("business-name2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYMENT).getCode()).isEqualTo("employment-code2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYMENT).getName()).isEqualTo("employment-name2");
		//assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.JOBTITLE).getCode()).isEqualTo("job-title-code2");
		//assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.JOBTITLE).getName()).isEqualTo("job-title-name2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.CLASSIFICATION).getCode()).isEqualTo("classification-code2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.CLASSIFICATION).getName()).isEqualTo("classification-name2");
		//assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.TEAM).getCode()).isEqualTo("team-code2");
		//assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.TEAM).getName()).isEqualTo("team-name2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.RANK).getCode()).isEqualTo("rank-code2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.RANK).getName()).isEqualTo("rank-symbol2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION).getCode()).isEqualTo("2");
		assertThat(employee2_personalInfoMap.get(ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION).getName()).isEqualTo("看護補助者");
	}

}
