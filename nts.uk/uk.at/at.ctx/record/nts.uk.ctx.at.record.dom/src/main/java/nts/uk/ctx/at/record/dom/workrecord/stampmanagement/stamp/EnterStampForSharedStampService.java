package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import java.util.Optional;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.CreateStampDataForEmployeesService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.TimeStampInputResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSetCommunal;

/**
 * DS: 共有打刻から打刻を入力する
 * path: 
 * @author chungnt
 *
 */
public class EnterStampForSharedStampService {

	/**
	 * [1] Create [1] 作成する
	 * 
	 * @param require
	 * @param ContractCode
	 *            契約コード
	 * @param cid
	 *            会社ID
	 * @param employeeID
	 *            社員ID
	 * @param stampCardNo
	 *            Optional<打刻カード番号>	
	 * @param Relieve
	 *            打刻する方法
	 * @param stampDatetime
	 *            打刻日時
	 * @param stampButton
	 *            打刻ボタン
	 * @param 実績への反映内容
	 * 
	 * @return stampInputResutl 打刻入力結果
	 * 
	 * 	ページNOとボタン位置NOから作成する打刻種類を判断する
	 * 
	 */
	public static TimeStampInputResult create(Require require , String cid, String contractCode, String employeeID, Optional<StampNumber> StampNumber,
			Relieve relieve,GeneralDateTime stmapDateTime, StampButton stampButton , RefectActualResult refActualResult) {
		
		//	$共有打刻の打刻設定 = require.共有打刻の打刻設定を取得する()
		Optional<StampSetCommunal> setShareTStamp = require.gets();
		
		if (!setShareTStamp.isPresent()) {
			throw new BusinessException("Msg_1632");
		}
		
		//$ボタン詳細設定 = $共有打刻の打刻設定.ボタン詳細設定を取得する(打刻ボタン)
		Optional<ButtonSettings> buttonSetting = setShareTStamp.get().getDetailButtonSettings(stampButton);		
		
		if (!buttonSetting.isPresent()) {
			throw new BusinessException("Msg_1632");
		}
		
		// return 社員の打刻データを作成する#作成する(require, 契約コード, 社員ID, 打刻カード番号, 打刻日時, 打刻する方法, $ボタン詳細設定.ボタン種類, 実績への反映内容, empty)
		return CreateStampDataForEmployeesService.create(require, cid, new ContractCode(contractCode), employeeID,
				StampNumber, stmapDateTime, relieve, buttonSetting.get().getButtonType(), refActualResult, Optional.ofNullable(null));
	
	}
	
	public static interface Require extends CreateStampDataForEmployeesService.Require{
		/** 
		 * [R-1] 共有打刻の打刻設定を取得する
		 * 
		 * 	共有打刻の打刻設定Repository.取得する()										
		 */
		Optional<StampSetCommunal> gets();
	}

}
