package nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.childnursingleave;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.getdeitalinfonursingbyemps.NursingAndChildNursingRemainDto;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;

/**
 * 
 * @author tutk
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildNursingLeaveDto {
	
	/**
	 * List＜個人社員基本情報＞
	 */
	List<EmployeeImport> lstEmployee = new ArrayList<>();
	
	/**
	 * 看護・介護残数DTO
	 */
	private NursingAndChildNursingRemainDto nursingAndChildNursingRemainDto;
}
