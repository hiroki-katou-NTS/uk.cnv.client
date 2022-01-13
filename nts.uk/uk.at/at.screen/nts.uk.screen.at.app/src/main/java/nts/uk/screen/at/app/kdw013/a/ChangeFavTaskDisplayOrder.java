package nts.uk.screen.at.app.kdw013.a;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.command.ChangeDisplayOrderCommand;
import nts.uk.screen.at.app.kdw013.command.ChangeDisplayOrderCommandHandler;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.お気に入り作業の順番を変更する
 * @author tutt
 *
 */
@Stateless
public class ChangeFavTaskDisplayOrder {
	
	@Inject
	private ChangeDisplayOrderCommandHandler handler;
	
	public void changeDisplayOrder(ChangeDisplayOrderCommand command) {
		handler.handle(command);
	}

}
