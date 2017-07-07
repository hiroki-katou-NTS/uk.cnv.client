package nts.uk.ctx.sys.portal.app.command.webmenu;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.portal.dom.webmenu.DefaultMenu;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenu;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuCode;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuName;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class CopyWebMenuCommandHandler extends CommandHandler<CopyWebMenuCommand> {

	@Inject
	private WebMenuRepository webMenuRepository;

	@Override
	protected void handle(CommandHandlerContext<CopyWebMenuCommand> context) {
		CopyWebMenuCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();

		Optional<WebMenu> currentWebMenuOp = webMenuRepository.find(companyId, command.getCurrentWebMenuCode());
		if (!currentWebMenuOp.isPresent()) {
			throw new RuntimeException("Error: web menu " + command.getCurrentWebMenuCode() + " not found");
		}
		
		WebMenu currentWebMenu = currentWebMenuOp.get();
		WebMenu newWebMenu = new WebMenu(companyId, new WebMenuCode(command.getWebMenuCode()), new WebMenuName(command.getWebMenuName()), DefaultMenu.NoDefaultMenu, currentWebMenu.getMenuBars());
		
		Optional<WebMenu> webMenu = webMenuRepository.find(companyId, command.getWebMenuCode());

		if (webMenu.isPresent()) {
			if (!command.isAllowOverwrite()) {
				throw new BusinessException("Msg_003");
			}

			webMenuRepository.update(newWebMenu);
		} else {
			webMenuRepository.add(newWebMenu);
		}
	}

}
