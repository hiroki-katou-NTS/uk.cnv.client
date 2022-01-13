package nts.uk.ctx.at.aggregation.dom.scheduletable;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import lombok.val;
import mockit.Expectations;
import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.uk.ctx.at.aggregation.dom.adapter.rank.EmployeeRankInfoImported;
import nts.uk.ctx.at.aggregation.dom.adapter.team.EmployeeTeamInfoImported;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpLicenseClassification;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.LicenseClassification;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.ClassificationImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.EmployeeInfoImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.EmploymentImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.PositionImported;

public class ScheduleTablePersonalInfoTest {
	
	/**
	 * 
	 * 職位, 雇用, 分類, チーム, ランク, 看護区分がない場合
	 * job-title, employment, classification, team, rank, license_class are empty
	 */
	@Test
	public void testCreate_onlyEmployeeName() {
		
		// Arrange
		EmployeeInfoImported empInfo = new EmployeeInfoImported(
				"employee-id","employee-code", 
				"business-name", "business-name-kana",
				Optional.empty(), // 職場
				Optional.empty(), // 部門
				Optional.empty(), // 職位
				Optional.empty(), // 雇用
				Optional.empty());// 分類
		
		EmployeeTeamInfoImported empTeam = new EmployeeTeamInfoImported("employee-id", Optional.empty(), Optional.empty());
		EmployeeRankInfoImported empRank = new EmployeeRankInfoImported("employee-id", Optional.empty(), Optional.empty());
		EmpLicenseClassification empLicenseClass = new EmpLicenseClassification("employee-id", Optional.empty());
		
		// Act
		ScheduleTablePersonalInfo result = ScheduleTablePersonalInfo.create("employee-id", empInfo, empTeam, empRank, empLicenseClass);
		
		// Assert
		assertThat(result.getEmployeeId()).isEqualTo("employee-id");
		
		val personalInfoMap = result.getPersonalInfoMap();
		assertThat(personalInfoMap.keySet()).containsExactly(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME);
		
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getCode()).isEqualTo("employee-code");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getName()).isEqualTo("business-name");
	}
	
	/**
	 * 
	 * 職位, 雇用, 分類, チーム, ランク, 看護区分がある場合
	 * job-title, employment, classification, team, rank, license_class are exist
	 */
	@Test
	public void testCreate_all() {
		
		// Arrange
		EmployeeInfoImported empInfo = new EmployeeInfoImported(
				"employee-id","employee-code", 
				"business-name", "business-name-kana",
				Optional.empty(), // 職場
				Optional.empty(), // 部門
				Optional.of(new PositionImported("job-title-id", "job-title-code", "job-title-name")), // 職位
				Optional.of(new EmploymentImported("employment-code", "employment-name")), // 雇用
				Optional.of(new ClassificationImported("classification-code", "classification-name")));// 分類
		
		EmployeeTeamInfoImported empTeam = new EmployeeTeamInfoImported("employee-id", Optional.of("team-code"), Optional.of("team-name"));
		EmployeeRankInfoImported empRank = new EmployeeRankInfoImported("employee-id", Optional.of("rank-code"), Optional.of("rank-symbol"));
		EmpLicenseClassification empLicenseClass = new EmpLicenseClassification("employee-id", Optional.of(LicenseClassification.NURSE));
		
		new Expectations(EnumAdaptor.class) {{
			 EnumAdaptor.convertToValueName(LicenseClassification.NURSE);
			 result = new EnumConstant(0, "NURSE", "看護師");
		}};
		
		// Act
		ScheduleTablePersonalInfo result = ScheduleTablePersonalInfo.create("employee-id", empInfo, empTeam, empRank, empLicenseClass);
		
		// Assert
		assertThat(result.getEmployeeId()).isEqualTo("employee-id");
		
		val personalInfoMap = result.getPersonalInfoMap();
		assertThat(personalInfoMap.keySet()).containsExactlyInAnyOrder(
				ScheduleTablePersonalInfoItem.EMPLOYEE_NAME,
				ScheduleTablePersonalInfoItem.EMPLOYMENT,
				ScheduleTablePersonalInfoItem.JOBTITLE,
				ScheduleTablePersonalInfoItem.CLASSIFICATION,
				ScheduleTablePersonalInfoItem.TEAM,
				ScheduleTablePersonalInfoItem.RANK,
				ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION);
		
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getCode()).isEqualTo("employee-code");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getName()).isEqualTo("business-name");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYMENT).getCode()).isEqualTo("employment-code");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.EMPLOYMENT).getName()).isEqualTo("employment-name");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.JOBTITLE).getCode()).isEqualTo("job-title-code");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.JOBTITLE).getName()).isEqualTo("job-title-name");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.CLASSIFICATION).getCode()).isEqualTo("classification-code");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.CLASSIFICATION).getName()).isEqualTo("classification-name");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.TEAM).getCode()).isEqualTo("team-code");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.TEAM).getName()).isEqualTo("team-name");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.RANK).getCode()).isEqualTo("rank-code");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.RANK).getName()).isEqualTo("rank-symbol");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION).getCode()).isEqualTo("0");
		assertThat(personalInfoMap.get(ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION).getName()).isEqualTo("看護師");
		
	}

}
