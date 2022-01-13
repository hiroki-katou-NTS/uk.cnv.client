package nts.uk.ctx.exio.dom.input.setting;

import java.util.List;
import java.util.Optional;

public interface ExternalImportSettingRepository {

	void insert(ExternalImportSetting domain);

	void update(ExternalImportSetting domain);

	void delete(String companyId, ExternalImportCode settingCode);

	List<ExternalImportSetting> getAll(String companyId);

	List<ExternalImportSetting> getCsvBase(FromCsvBaseSettingToDomainRequire require,String companyId);
	List<ExternalImportSetting> getDomainBase(String companyId);

	Optional<ExternalImportSetting> get(Optional<FromCsvBaseSettingToDomainRequire> require,String companyId, ExternalImportCode settingCode);

	boolean exist(String companyId, ExternalImportCode settingCode);

	void registDomain(ExternalImportSetting setting, DomainImportSetting domain);

	void deleteDomain(String companyId, String code, int domainId);
}
