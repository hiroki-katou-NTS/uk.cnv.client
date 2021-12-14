package nts.uk.screen.com.app.cmf.cmf001.b.save;

import lombok.Getter;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.screen.com.app.cmf.cmf001.b.get.ExternalImportSettingDto;

@Getter
public class Cmf001bSaveCommand {

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
		return this.getSetting().toDomain();
	}
}
