package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Optional;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.stampsettingofRICOHcopier.StampSettingOfRICOHCopier;

/**
 * @author ThanhPV
 * @name RICOH複写機から打刻する
 * @part UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.打刻管理.打刻.ICカードから打刻を入力する
 */
public class StampFromRICOHCopierService {
	
	/**
	 * [1] 作成する
	 * @param require
	 * @param contractCode 契約コード
	 * @param empId 社員ID		
	 * @param stampDatetime 打刻日時	
	 * @param stampButton 打刻ボタン		
	 * @param refectActualResult 実績への反映内容		
	 * @return 	打刻入力結果
	 */
	public static TimeStampInputResult create(Require require, String CID, ContractCode contractCode, String empId,
			GeneralDateTime stampDatetime, StampButton stampButton, RefectActualResult refectActualResult) {

		// 	$打刻設定 = require.RICOH複写機から打刻するを取得する()
		Optional<StampSettingOfRICOHCopier> stampSettingOfRICOHCopier = require.getStampSettingOfRICOHCopier(CID);

		if (!stampSettingOfRICOHCopier.isPresent()) {
			throw new BusinessException("Msg_1632");
		}
		
		//	$ボタン詳細設定 = $打刻設定.ボタン詳細設定を取得する(打刻ボタン)	
		Optional<ButtonSettings> detailButtonSettings =  stampSettingOfRICOHCopier.get().getDetailButtonSettings(stampButton);
		
		if (!detailButtonSettings.isPresent()) {
			throw new BusinessException("Msg_1632");
		}
		
		// 	$打刻する方法 = 打刻する方法#打刻する方法(ICカード認証, リコー複写機打刻)
		Relieve relieve = new Relieve(AuthcMethod.IC_CARD_AUTHC, StampMeans.RICOH_COPIER);
		
		//return 社員の打刻データを作成する#作成する(require, 契約コード, 社員ID, empty, 打刻日時, $打刻する方法, $ボタン詳細設定.ボタン種類, 実績への反映内容, empty)
		return CreateStampDataForEmployeesService.create(require, CID, contractCode, empId, Optional.empty(),
				stampDatetime, relieve, detailButtonSettings.get().getType(), refectActualResult, Optional.empty());
	}

	public static interface Require extends CreateStampDataForEmployeesService.Require {

		// [R-1] RICOH複写機から打刻するを取得する
		Optional<StampSettingOfRICOHCopier> getStampSettingOfRICOHCopier(String cid);
	}
}
