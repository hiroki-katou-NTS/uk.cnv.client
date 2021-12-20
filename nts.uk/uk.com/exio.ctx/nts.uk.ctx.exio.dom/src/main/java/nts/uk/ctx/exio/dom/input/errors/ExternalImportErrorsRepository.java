package nts.uk.ctx.exio.dom.input.errors;

public interface ExternalImportErrorsRepository {

	void cleanOldTables(String companyId);
	
	void setup(String companyId);

	void add(String companyId, ExternalImportError error);
	
	ExternalImportErrors find(String companyId, int startErrorNo, int size);
}
