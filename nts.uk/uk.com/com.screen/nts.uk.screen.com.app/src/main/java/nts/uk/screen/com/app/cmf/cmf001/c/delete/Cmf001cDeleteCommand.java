package nts.uk.screen.com.app.cmf.cmf001.c.delete;

import lombok.Value;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;

@Value
public class Cmf001cDeleteCommand {

	private String settingCode;
	private int itemNo;
	
	public ExternalImportCode getExternalImportCode() {
		return new ExternalImportCode(settingCode);
	}
}
