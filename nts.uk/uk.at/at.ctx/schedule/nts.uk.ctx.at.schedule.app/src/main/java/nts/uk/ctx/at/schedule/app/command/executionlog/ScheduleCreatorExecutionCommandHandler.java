/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.app.command.executionlog;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.AsyncCommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;

/**
 * The Class ScheduleCreatorExecutionCommandHandler.
 *///
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class ScheduleCreatorExecutionCommandHandler extends AsyncCommandHandler<ScheduleCreatorExecutionCommand> {

	@Inject
	private ScheduleCreatorExecutionService executeService;

	@Override
	public void handle(CommandHandlerContext<ScheduleCreatorExecutionCommand> context) {
		
		executeService.handle(context.getCommand(), Optional.of(context.asAsync()));
	}
}