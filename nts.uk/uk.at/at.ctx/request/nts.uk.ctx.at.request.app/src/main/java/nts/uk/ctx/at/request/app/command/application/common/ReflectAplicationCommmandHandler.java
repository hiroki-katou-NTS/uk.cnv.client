package nts.uk.ctx.at.request.app.command.application.common;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.request.dom.applicationreflect.service.AppReflectManagerFromRecord;
@Stateless
public class ReflectAplicationCommmandHandler extends CommandHandler<List<String>>{
	@Inject
	private AppReflectManagerFromRecord appReflectManager;
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	protected void handle(CommandHandlerContext<List<String>> context) {
		List<String> lstAppID = context.getCommand();
		appReflectManager.reflectApplication(lstAppID);
	}
}


