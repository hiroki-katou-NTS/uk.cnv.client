package nts.uk.ctx.exio.dom.input.workspace;

import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomain;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspace;

/**
 * 外部受入のワークスペースに対する入出力を担当するRepository
 */
public interface ExternalImportWorkspaceRepository {
	void cleanOldTables(Require require, ExecutionContext context);
	
	void setup(Require require, ExecutionContext context);
	
	public static interface Require {
		
		ImportingDomain getImportingDomain(ImportingDomainId domainId);
		
		DomainWorkspace getDomainWorkspace(ImportingDomainId domainId);
		
	}
}
