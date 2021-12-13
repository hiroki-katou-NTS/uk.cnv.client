package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PortalStampSettings;

/**
 * 
 * @author sonnlb
 * 
 *         ポータル打刻から打刻を入力する
 *         UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.ポータル打刻から打刻を入力する.ポータル打刻から打刻を入力する
 *
 */
@Stateless
public class EnterStampFromPortalService {
	/**
	 * [1] 作成する
	 * 
	 * @param require
	 * @param 契約コード
	 *            contractCode
	 * @param 会社ID
	 *            cid
	 * @param 社員ID
	 *            employeeID
	 * @param 打刻日時
	 *            stampDatetime
	 * @param ボタン位置NO
	 *            buttonPositionNo
	 * @param 実績への反映内容
	 *            refActualResults
	 * @return 打刻入力結果
	 * 
	 *         ページNOとボタン位置NOから作成する打刻種類を判断する 社員の打刻データを作成する
	 */
	public static TimeStampInputResult create(Require require, String cid, ContractCode contractCode, String employeeID,
			GeneralDateTime stampDatetime, ButtonPositionNo buttonPositionNo, RefectActualResult refActualResults) {

		// $ポータルの打刻設定 = require.ポータルの打刻設定を取得する()
		Optional<PortalStampSettings> settingStampPotal = require.getPortalStampSetting();

		if (!settingStampPotal.isPresent()) {
			throw new BusinessException("Msg_1632");
		}

		// $ボタン詳細設定 = $ポータルの打刻設定.ボタン詳細設定を取得する(ボタン位置NO)
		Optional<ButtonSettings> settingButton = settingStampPotal.get().getDetailButtonSettings(buttonPositionNo);

		if (!settingButton.isPresent()) {
			throw new BusinessException("Msg_1632");
		}

		// $打刻する方法 = 打刻する方法#打刻する方法(ID認証, ポータル打刻)
		Relieve relieve = new Relieve(AuthcMethod.ID_AUTHC, StampMeans.PORTAL);

		// return 社員の打刻データを作成する#作成する(require, 契約コード, 社員ID, NULLL, 打刻日時, $打刻する方法,
		// $ボタン詳細設定.ボタン種類, 実績への反映内容, NULL)
		return CreateStampDataForEmployeesService.create(require, cid, contractCode, employeeID, Optional.ofNullable(null),
				stampDatetime, relieve, settingButton.get().getType(), refActualResults,
				Optional.ofNullable(null));
	}

	public static interface Require extends CreateStampDataForEmployeesService.Require {

		// [R-1] ポータルの打刻設定を取得する
		Optional<PortalStampSettings> getPortalStampSetting();
	}

}
