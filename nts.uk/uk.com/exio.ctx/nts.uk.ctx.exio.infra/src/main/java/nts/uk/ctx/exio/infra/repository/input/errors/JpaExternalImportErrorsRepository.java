package nts.uk.ctx.exio.infra.repository.input.errors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrors;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrorsRepository;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaExternalImportErrorsRepository extends JpaRepository implements ExternalImportErrorsRepository {

	@Override
	public void cleanOldTables(String companyId) {
		table(companyId).dropTable();
	}

	@Override
	public void setup(String companyId) {
		table(companyId).createTable();
	}

	@Override
	public void add(String companyId, ExternalImportError error) {
		table(companyId).insert(error);
	}

	@Override
	public ExternalImportErrors find(String companyId, int startErrorNo, int size) {
		val errors = table(companyId).select(startErrorNo, size);
		return new ExternalImportErrors(errors);
	}


	private ErrorsTable table(String companyId) {
		return new ErrorsTable(companyId, this.database().product(), this.jdbcProxy());
	}
}
