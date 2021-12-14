package nts.uk.ctx.exio.dom.input.setting.assembly.revise;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;

public interface ReviseItemRepository {
	
	Optional<ReviseItem> get(String companyId, ExternalImportCode settingCode, int importItemNumber);

	void persist(ReviseItem reviseItem);
	
	void delete(String companyId, ExternalImportCode settingCode, int importItemNumber);

	void delete(String companyId, ExternalImportCode settingCode);

	void delete(String companyId, ExternalImportCode settingCode, List<Integer> itemNos);
}
