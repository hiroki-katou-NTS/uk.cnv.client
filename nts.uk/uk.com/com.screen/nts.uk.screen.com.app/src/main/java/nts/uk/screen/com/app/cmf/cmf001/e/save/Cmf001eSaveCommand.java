package nts.uk.screen.com.app.cmf.cmf001.e.save;

import lombok.Getter;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ImportSettingBaseType;
import nts.uk.screen.com.app.cmf.cmf001.b.get.ExternalImportSettingDto;

@Getter
public class Cmf001eSaveCommand {

	/** 新規モード=true, 更新=false */
	private boolean createMode;
	
	/** 登録内容 */
	private ExternalImportSettingDto setting;

	public ExternalImportCode getCode() {
		return new ExternalImportCode(this.setting.getCode());
	}

	public boolean isNew() {
		return this.createMode;
	}

	public ExternalImportSetting toDomain() {
		return this.getSetting().toDomain(ImportSettingBaseType.CSV_BASE);
	}
}
