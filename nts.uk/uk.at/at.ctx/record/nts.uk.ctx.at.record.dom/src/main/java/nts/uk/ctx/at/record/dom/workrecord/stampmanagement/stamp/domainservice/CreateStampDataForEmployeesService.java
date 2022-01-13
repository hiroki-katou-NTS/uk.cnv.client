package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDateTime;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.reflectstamp.ReflectStampInDailyRecord;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.service.ConvertTimeRecordStampService;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.AutoCreateStampCardNumberService;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardCreateResult;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
/**
 * DS : 社員の打刻データを作成する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.社員の打刻データを作成する
 * 
 * @author tutk
 *
 */
public class CreateStampDataForEmployeesService {
	/**
	 * [1] 作成する
	 * 
	 * @param require
	 * @param contractCode
	 *            契約コード
	 * @param companyID
	 *            会社ID
	 * @param employeeId
	 *            社員ID
	 *@param stampNumber
	 *            打刻カード番号
	 * @param stampDateTime
	 *            打刻日時
	 * @param relieve
	 *            打刻する方法
	 * @param buttonType
	 *            ボタン種類
	 * @param refActualResults
	 *            実績への反映内容
	 * @param stampLocationInfor
	 *            打刻位置情報
	 * @return
	 */
	
	
	public static TimeStampInputResult create(Require require, String cid, ContractCode contractCode, String employeeId,
			Optional<StampNumber> stampNumber, GeneralDateTime stampDateTime, Relieve relieve, StampType stamptype,
			RefectActualResult refActualResults, Optional<GeoCoordinate> stampLocationInfor) {
		//	$打刻カード作成結果 = [prv-1] 打刻カード番号を取得する(require, 社員ID, 打刻カード番号, 打刻する方法.打刻手段)	
		StampCardCreateResult stampResult=	getCardNumber(require, employeeId, stampNumber, relieve.getStampMeans());
//		//	$打刻作成するか = ボタン種類.打刻区分を取得する()
//		boolean stampAtr = buttonType.checkStampType();
//		//	if not $打刻作成するか
//		if(!stampAtr) {
//			// $永続化処理 = 打刻データ反映処理#打刻を登録する(require, $打刻記録, $打刻データ)
//			AtomTask reflectResult = RegisterStampData.registerStamp(require, null).orElse(AtomTask.none());
//			// $処理結果 = 打刻データ反映処理結果#打刻データ反映処理結果(Optional.Empty,$永続化処理)
//			StampDataReflectResult stampDataReflectResult = new StampDataReflectResult(Optional.empty(), reflectResult);
//			//	return 打刻入力結果#打刻入力結果($処理結果, $打刻カード作成結果.永続化処理)	
//			return new TimeStampInputResult(stampDataReflectResult, stampResult.getAtomTask());
//			
//		}
		
		//$打刻データ = 打刻#初回打刻データを作成する(契約コード,$打刻カード作成結果.打刻カード番号,打刻日時,打刻する方法,ボタン種類.打刻種類,実績への反映内容, 打刻場所情報)
		Stamp stamp = new Stamp(contractCode, new StampNumber(stampResult.getCardNumber()), stampDateTime, relieve,
				stamptype, refActualResults, stampLocationInfor);
		
		//$打刻の永続化 = 打刻を登録する#打刻を登録する(require, $打刻データ)
		AtomTask atom = RegisterStampData.registerStamp(require, stamp).orElse(AtomTask.none());
		
		//$打刻反映結果 = DS_打刻を日別実績へ反映する.反映する(require, 会社ID, $打刻の永続化)
//		Optional<StampDataReflectResult> reflectResult = ConvertTimeRecordStampService.createDailyData(require,
//				Optional.of(cid), Optional.of(employeeId), Optional.of(stamp), atom);
		Optional<StampDataReflectResult> stampDataResultOpt = ReflectStampInDailyRecord.reflect(require, stamp);
		
		if(!stampDataResultOpt.isPresent()) {
			return new TimeStampInputResult(new StampDataReflectResult(Optional.empty(), atom), stampResult.getAtomTask());
		}
		
		List<AtomTask> taskLst = new ArrayList<>();
		taskLst.add(atom);
		taskLst.add(stampDataResultOpt.get().getAtomTask());
		//return 打刻入力結果#打刻入力結果($打刻反映結果, $打刻カード作成結果.永続化処理)

		return new TimeStampInputResult(stampDataResultOpt
				.map(m -> new StampDataReflectResult(m.getReflectDate(), AtomTask.bundle(taskLst))).get(),
				stampResult.getAtomTask());
	}

	/**
	 * [prv-1] 打刻カード番号を取得する
	 * 
	 * @param require
	 * @param employeeId
	 *            社員ID
	 * @param stampNumber
	 *            打刻カード番号
	 * @param stampMeans
	 *            打刻手段
	 * @return 打刻カード番号
	 */
	private static  StampCardCreateResult getCardNumber(Require require, String employeeId, Optional<StampNumber> stampNumber, StampMeans stampMeans) {
		//	if 打刻する方法.打刻手段 == ICカード
		if(stampMeans.equals(StampMeans.IC_CARD)){
			//return 打刻カード作成結果#打刻カード作成結果($打刻カード, empty)
			return new StampCardCreateResult(stampNumber.get().v(), Optional.ofNullable(null));
		}
		//$打刻カードリスト = require.打刻カード番号を取得する(社員ID)
		List<StampCard> listStampCard = require.getLstStampCardBySidAndContractCd(employeeId);
		//	if not $打刻カードリスト.isEmpty
		if (!listStampCard.isEmpty()) {
			// $打刻カード = $打刻カードリスト :map $番号 first
			
			Optional<StampNumber> stampNumberOpt =  listStampCard.stream().map(x-> x.getStampNumber()).findFirst();
			//	return 打刻カード作成結果#打刻カード作成結果($打刻カード, empty)	
			return new StampCardCreateResult(stampNumberOpt.get().v(), Optional.ofNullable(null) );
		}
		//	$打刻カード作成結果 = 打刻カード番号を自動作成する#作成する(require, 社員ID, 打刻手段)	
		Optional<StampCardCreateResult> stampCardCreateResultOpt = AutoCreateStampCardNumberService.create(require,
				employeeId, stampMeans);
		
		//	if $打刻カード作成結果.isEmpty	
		if(!stampCardCreateResultOpt.isPresent()){
			//	BusinessException: Msg_433			
			throw new BusinessException("Msg_433");
		}
		return stampCardCreateResultOpt.get();
	}

	public static interface Require extends AutoCreateStampCardNumberService.Require, ConvertTimeRecordStampService.Require {
		/**
		 * [R-1] 打刻カード番号を取得する - StampCardRepository
		 * 
		 * @param sid
		 * @return
		 */
		List<StampCard> getLstStampCardBySidAndContractCd(String sid);
	}

	

	


}
