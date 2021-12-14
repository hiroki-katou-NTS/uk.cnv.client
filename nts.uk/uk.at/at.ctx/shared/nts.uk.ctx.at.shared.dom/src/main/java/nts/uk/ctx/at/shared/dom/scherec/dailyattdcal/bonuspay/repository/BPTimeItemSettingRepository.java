/**
 * 9:44:48 AM Jun 6, 2017
 */
package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository;

import java.util.List;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BPTimeItemSetting;



/**
 * @author hungnm
 *
 */
public interface BPTimeItemSettingRepository {

	List<BPTimeItemSetting> getListSetting(String companyId);

	List<BPTimeItemSetting> getListSpecialSetting(String companyId);

	List<BPTimeItemSetting> getListAllSetting(String companyId);

	void addListSetting(List<BPTimeItemSetting> lstSetting);

	void updateListSetting(List<BPTimeItemSetting> lstSetting);
}
