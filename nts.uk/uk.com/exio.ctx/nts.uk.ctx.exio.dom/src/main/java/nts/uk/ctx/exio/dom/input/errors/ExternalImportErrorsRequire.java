package nts.uk.ctx.exio.dom.input.errors;

/**
 * 受入時のエラーを永続化するためのRequire
 */
public interface ExternalImportErrorsRequire {
	
	void add(ExternalImportError error);
}
