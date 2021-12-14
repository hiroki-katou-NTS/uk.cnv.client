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
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSettingPerson;

/**
 * @author ThanhNX
 *
 *         個人打刻から打刻を入力する (old : 個人打刻入力から打刻を作成する)		
 *         UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.個人打刻から打刻を入力する
 */
public class EnterStampFromPersonalStampService {

	// [1] 作成する
	/**
	 * @param require 	@Require
	 * @param contractCode  契約コード
	 * @param cid 会社ID
	 * @param employeeId 社員ID	
	 * @param stmapDateTime 打刻日時
	 * @param stampButton 打刻ボタン
	 * @param refActualResults 実績への反映内容
	 * 
	 * @return 打刻入力結果
	 */
	
	public static TimeStampInputResult create(Require require, String cid, String contractCode, String employeeId, GeneralDateTime stmapDateTime, StampButton stampButton,
			RefectActualResult refActualResults) {

		// $個人利用の打刻設定 = require.個人利用の打刻設定を取得する()
		Optional<StampSettingPerson> stampSettingPerOpt = require.getStampSet();
		if (!stampSettingPerOpt.isPresent()) {
			throw new BusinessException("Msg_1632");
		}

		// $ボタン詳細設定 = $個人利用の打刻設定.ボタン詳細設定を取得する(ページNO, ボタン位置NO)
		Optional<ButtonSettings> buttonSet = stampSettingPerOpt.get().getButtonSet(stampButton);
		if (!buttonSet.isPresent()) {
			throw new BusinessException("Msg_1632");
		}
		
		//$打刻する方法 = 打刻する方法#打刻する方法(ID認証, 個人打刻)
		Relieve relieve = new Relieve(AuthcMethod.ID_AUTHC, StampMeans.INDIVITION);
		
		// return 社員の打刻データを作成する#作成する(require, 契約コード, 社員ID, empty, 打刻日時, $打刻する方法, $ボタン詳細設定.ボタン種類, 実績への反映内容, empty)	
		return CreateStampDataForEmployeesService.create(require, cid, new ContractCode(contractCode), employeeId, null, stmapDateTime,
				relieve, buttonSet.get().getButtonType(), refActualResults, null);
	}

	public static interface Require extends CreateStampDataForEmployeesService.Require {
		
		 //[R-1] 個人利用の打刻設定を取得する -StampSetPerRepository
		 Optional<StampSettingPerson> getStampSet ();
	}
}
