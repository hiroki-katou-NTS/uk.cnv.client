package nts.uk.ctx.sys.portal.app.command.flowmenu;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.portal.dom.flowmenu.CreateFlowMenu;
import nts.uk.ctx.sys.portal.dom.flowmenu.CreateFlowMenuRepository;
import nts.uk.ctx.sys.portal.dom.flowmenu.FlowMenuLayout;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CCG_メニュートップページ.CCG034_フローページの作成.D：フローメニューレイアウト設定.メニュー別OCD.フローメニュー作成のレイアウトを登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UpdateFlowMenuLayoutCommandHandler extends CommandHandler<UpdateFlowMenuLayoutCommand> {
	
	@Inject
	private CreateFlowMenuRepository createFlowMenuRepository;

	@Override
	protected void handle(CommandHandlerContext<UpdateFlowMenuLayoutCommand> context) {
		UpdateFlowMenuLayoutCommand command = context.getCommand();
		Optional<CreateFlowMenu> optCreateFlowMenu = this.createFlowMenuRepository
				.findByPk(AppContexts.user().companyId(), command.getFlowMenuCode());
		optCreateFlowMenu.ifPresent(domain -> {
			domain.setFlowMenuLayout(command.getFlowMenuLayout() != null 
									? Optional.of(FlowMenuLayout.createFromMemento(command.getFlowMenuLayout()))
									: Optional.empty());
			this.createFlowMenuRepository.update(domain);
		}); 
	}
}
