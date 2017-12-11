package nts.uk.ctx.pereg.app.find.reghistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.pereg.dom.reghistory.LastEmRegHistory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpRegHistoryDto {

	RegEmployeeDto lastRegEmployee;

	RegEmployeeDto lastRegEmployeeOfCompany;

	public static EmpRegHistoryDto createFromDomain(LastEmRegHistory domain) {
		return new EmpRegHistoryDto(
				new RegEmployeeDto(domain.getLastRegEmployeeID(), domain.getLastRegEmployeeCd(), ""),
				domain.getLastRegEmployeeOfCompanyID() != null ? new RegEmployeeDto(
						domain.getLastRegEmployeeOfCompanyID(), domain.getLastRegEmployeeOfCompanyCd(), "") : null);
	}

}
