package nts.uk.ctx.at.shared.app.command.workrule.shiftmaster;

import java.util.Optional;

import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.*;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author anhdt 職場で使うシフトマスタを登録する
 */
@Data
public class DeleteShiftMasterCommand {
	private String companyId;
	private String shiftMasterName;
	private String shiftMasterCode;
	private String color;
	private String colorSmartphone;
	private String remark;
	private String importCode;
	private String workTypeCd;
	private String workTimeSetCd;

	public ShiftMaster toDomain() {
		String companyId = AppContexts.user().companyId();
		ShiftMasterCode code = new ShiftMasterCode(shiftMasterCode);
		ShiftMasterName name = new ShiftMasterName(shiftMasterName);
		ColorCodeChar6 colorP = new ColorCodeChar6(color);
		ColorCodeChar6 colorSmartphone  = new ColorCodeChar6(color);
		Optional<ShiftMasterImportCode> importCode = !StringUtils.isEmpty(this.importCode) ? Optional.of(new ShiftMasterImportCode(this.importCode)) : Optional.empty();
		Optional<Remarks> remarks = Optional.ofNullable( StringUtils.isEmpty(remark) ? null : new Remarks(remark) );
		ShiftMasterDisInfor display = new ShiftMasterDisInfor(name, colorP,colorSmartphone, remarks);
		return new ShiftMaster(companyId, code, display, workTypeCd, workTimeSetCd, importCode);
	}
}
