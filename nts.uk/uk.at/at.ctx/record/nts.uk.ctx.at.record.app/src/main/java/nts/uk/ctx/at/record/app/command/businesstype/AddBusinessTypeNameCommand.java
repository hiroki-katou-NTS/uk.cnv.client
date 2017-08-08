package nts.uk.ctx.at.record.app.command.businesstype;

import lombok.Getter;
import lombok.Setter;
/**
 * @author yennth
 * insert business type name command
 */
@Getter
@Setter
public class AddBusinessTypeNameCommand {
	/*会社ID*/
	private String companyId;
	/*勤務種別コード*/
	private String businessTypeCode;
	/*勤務種別名*/
	private String businessTypeName;
}
