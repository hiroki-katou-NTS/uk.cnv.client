/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.app.wagetable.certification.command;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroup;
import nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroupRepository;
import nts.uk.ctx.pr.core.dom.wagetable.certification.service.CertifyGroupService;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class CertifyGroupAddCommandHandler.
 */
@Stateless
public class CertifyGroupAddCommandHandler extends CommandHandler<CertifyGroupAddCommand> {

	/** The certify group repository. */
	@Inject
	private CertifyGroupRepository certifyGroupRepository;

	/** The certify group service. */
	@Inject
	private CertifyGroupService certifyGroupService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command
	 * .CommandHandlerContext)
	 */
	@Override
	@Transactional
	protected void handle(CommandHandlerContext<CertifyGroupAddCommand> context) {

		String companyCode = AppContexts.user().companyCode();

		CertifyGroup certifyGroup = context.getCommand().toDomain(companyCode);

		certifyGroupService.validateRequiredItem(certifyGroup);

		certifyGroupService.checkDuplicateCode(certifyGroup);

		this.certifyGroupRepository.add(certifyGroup);
	}

}
