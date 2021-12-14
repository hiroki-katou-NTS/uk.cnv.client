package nts.uk.ctx.exio.dom.input.setting;

import java.util.List;
import java.util.Optional;

public interface ExternalImportSettingRepository {

	void insert(ExternalImportSetting domain);

	void update(ExternalImportSetting domain);

	void delete(String companyId, ExternalImportCode settingCode);

	List<ExternalImportSetting> getAll(String companyId);

	Optional<ExternalImportSetting> get(String companyId, ExternalImportCode settingCode);

	boolean exist(String companyId, ExternalImportCode settingCode);
}
