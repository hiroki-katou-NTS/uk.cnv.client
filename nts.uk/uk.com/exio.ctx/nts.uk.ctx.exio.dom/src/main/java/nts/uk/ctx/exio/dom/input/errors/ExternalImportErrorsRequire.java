package nts.uk.ctx.exio.dom.input.errors;

import nts.uk.ctx.exio.dom.input.ExecutionContext;

/**
 * 受入時のエラーを永続化するためのRequire
 */
public interface ExternalImportErrorsRequire {
	
	void add(ExecutionContext context, ExternalImportError error);
}
