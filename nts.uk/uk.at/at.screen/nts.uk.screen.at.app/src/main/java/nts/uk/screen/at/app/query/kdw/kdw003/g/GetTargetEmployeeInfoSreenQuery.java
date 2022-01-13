package nts.uk.screen.at.app.query.kdw.kdw003.g;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationQueryDtoImport;

/**
 * 対象社員情報を取得する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW003_日別実績の修正.G：初期作業選択設定.メニュー別OCD.対象社員情報を取得する.対象社員情報を取得する
 * @author quytb
 *
 */

@Stateless
public class GetTargetEmployeeInfoSreenQuery {
	@Inject
	private EmployeeInformationAdapter employeeInformationAdapter;
	
	public List<EmployeeInfoDto> get(List<String> employeeIds, GeneralDate baseDate){
		
		EmployeeInformationQueryDtoImport param = new EmployeeInformationQueryDtoImport(
				new ArrayList<>(employeeIds), baseDate, true, false, false, false, false, false);
		
		//1.call <<Public>> 社員の情報を取得する
		List<EmployeeInformationImport> listEmpInfo = employeeInformationAdapter.getEmployeeInfo(param);
		return listEmpInfo.stream().map(emp -> {
			EmployeeInfoDto dto = new EmployeeInfoDto();
			dto.setCode(emp.getEmployeeCode());
			dto.setId(emp.getEmployeeId());
			dto.setBusinessName(emp.getBusinessName());
			dto.setWorkplaceName(emp.getWorkplace() != null ? emp.getWorkplace().getWorkplaceName() : null);
			dto.setWorkplaceId(emp.getWorkplace() != null ? emp.getWorkplace().getWorkplaceCode() : null);
			return dto;
		}).collect(Collectors.toList());
	}
}
