package nts.uk.ctx.bs.person.dom.person.setting.copySetting;

import java.util.Optional;

import nts.uk.ctx.bs.person.dom.person.info.setting.copySetting.EmpCopySetting;

public interface EmpCopySettingRepository {

	Optional<EmpCopySetting> find(String companyId);
}
