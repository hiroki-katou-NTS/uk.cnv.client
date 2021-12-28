package nts.uk.screen.at.app.kdw013.e;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.E：時刻なし作業内容.メニュー別OCD.時刻なし作業内容を起動する
 * @author tutt
 *
 */
@Stateless
public class StartWorkNoTime {
	
	@Inject
	private GetWorkDataMasterInforQuery query;

	/**
	 * 
	 * @param command
	 * @return
	 */
	public GetWorkDataMasterInforDto startWorkNoTime(GetWorkDataMasterInforCommand command) {
		return query.get(command);
	}

}
