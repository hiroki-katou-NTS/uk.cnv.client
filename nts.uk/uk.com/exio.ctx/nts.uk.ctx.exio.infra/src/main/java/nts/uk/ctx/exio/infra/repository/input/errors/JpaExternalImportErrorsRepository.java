package nts.uk.ctx.exio.infra.repository.input.errors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrors;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrorsRepository;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaExternalImportErrorsRepository extends JpaRepository implements ExternalImportErrorsRepository {

	@Override
	public void setup(ExecutionContext context) {
		table(context).createTable();
	}

	@Override
	public void add(ExecutionContext context, ExternalImportError error) {
		table(context).insert(error);
	}

	@Override
	public ExternalImportErrors find(ExecutionContext context, int startErrorNo, int size) {
		val errors = table(context).select(startErrorNo, size);
		return new ExternalImportErrors(errors);
	}


	private ErrorsTable table(ExecutionContext context) {
		return new ErrorsTable(context, this.database().product(), this.jdbcProxy());
	}
}
