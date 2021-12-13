package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class GetRefWorkplaceAndEmployeeResultDto {
	
	/** 社員の所属情報(Map<社員ID,職場ID>)*/
	private List<RefEmpWkpInfoDto> employeeInfos;
	
	/** List＜社員ID（List）から社員コードと表示名を取得＞*/
	private List<EmployeeBasicInfoDto> lstEmployeeInfo;
	 
	/** List＜職場情報一覧＞ */
	private List<WorkplaceInfoDto> workplaceInfos;
}
