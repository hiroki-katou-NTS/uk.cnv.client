package nts.uk.ctx.exio.dom.exo.commonalgorithm;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.exio.dom.exo.condset.StdOutputCondSet;
import nts.uk.ctx.exio.dom.exo.condset.StdOutputCondSetRepository;

/**
 * 外部出力取得設定一覧
 * 
 * @author tam.nx
 *
 */
@Stateless
public class AcquisitionSettingList {

	@Inject
	private StdOutputCondSetRepository stdOutputCondSetRepository;
	
	/**
	 * 外部出力取得設定一覧
	 * 
	 * @param cId
	 *            会社ID
	 * @param userId
	 *            ユーザID
	 * @param conditionSettingCode
	 *            条件設定コード
	 * @return 出力条件設定（定型）
	 */
	public List<StdOutputCondSet> getAcquisitionSettingList(String cId, String conditionSettingCode) {
		if (conditionSettingCode == null) {
			return this.stdOutputCondSetRepository.getListStdOutputCondSetByCid(cId);
		}
		return null;
	}
}
