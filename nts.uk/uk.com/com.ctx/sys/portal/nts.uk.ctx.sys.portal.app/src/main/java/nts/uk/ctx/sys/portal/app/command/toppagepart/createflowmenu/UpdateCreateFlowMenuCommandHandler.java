package nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.portal.dom.toppagepart.TopPagePartName;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.CreateFlowMenu;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.CreateFlowMenuRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CCG_メニュートップページ.CCG034_フローページの作成.A：フローメニューの作成.メニュー別OCD.フローメニュー作成の更新を行う
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UpdateCreateFlowMenuCommandHandler extends CommandHandler<UpdateFlowMenuCommand> {

	@Inject
	private CreateFlowMenuRepository createFlowMenuRepository;

	@Override
	protected void handle(CommandHandlerContext<UpdateFlowMenuCommand> context) {
		UpdateFlowMenuCommand command = context.getCommand();
		//1. get(ログイン会社ID、フローメニューコード)
		Optional<CreateFlowMenu> optCreateFlowMenu = createFlowMenuRepository
				.findByPk(AppContexts.user().companyId(), command.getFlowMenuCode());
		
		//3. not　フローメニュー作成　empty
		if (optCreateFlowMenu.isPresent()) {
			CreateFlowMenu domain = optCreateFlowMenu.get();
			domain.setFlowMenuName(new TopPagePartName(command.getFlowMenuName()));
			
			//4. persist
			createFlowMenuRepository.update(domain);
			
		//2. フローメニュー作成　empty
		} else throw new BusinessException("Msg_1806");
	}
}
