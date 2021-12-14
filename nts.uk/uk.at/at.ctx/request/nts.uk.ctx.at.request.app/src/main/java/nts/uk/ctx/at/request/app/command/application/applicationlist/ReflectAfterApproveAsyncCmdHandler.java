package nts.uk.ctx.at.request.app.command.application.applicationlist;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.AsyncCommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.request.dom.applicationreflect.service.AppReflectManagerFromRecord;
@Stateless
public class ReflectAfterApproveAsyncCmdHandler extends AsyncCommandHandler<List<String>>{

	
	@Inject
	private AppReflectManagerFromRecord appReflectManager;
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	protected void handle(CommandHandlerContext<List<String>> context) {
		List<String> lstID = context.getCommand();
		//get list application by list id
		appReflectManager.reflectApplication(lstID);
	}
	
    @Override
    public boolean keepsTrack(){
        return false;
    }
}
