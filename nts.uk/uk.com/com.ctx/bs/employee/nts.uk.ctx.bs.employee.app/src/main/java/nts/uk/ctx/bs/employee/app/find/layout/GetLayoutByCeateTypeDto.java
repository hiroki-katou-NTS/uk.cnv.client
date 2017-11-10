package nts.uk.ctx.bs.employee.app.find.layout;

import lombok.Getter;
import lombok.Value;
import nts.arc.time.GeneralDate;

@Value
@Getter
public class GetLayoutByCeateTypeDto {

	private int createType;

	private String initSettingId;

	private GeneralDate baseDate;

	private String employeeId;

}
