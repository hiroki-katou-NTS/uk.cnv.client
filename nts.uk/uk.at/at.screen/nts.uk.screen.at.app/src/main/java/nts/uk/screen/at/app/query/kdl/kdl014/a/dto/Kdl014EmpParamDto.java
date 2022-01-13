package nts.uk.screen.at.app.query.kdl.kdl014.a.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@Data
@NoArgsConstructor
public class Kdl014EmpParamDto {

	private GeneralDate start;
	
	private GeneralDate end;
	
	//0: mode person
	//1: mode date
	private Integer mode;
	
	private List<String> listEmp;
}
