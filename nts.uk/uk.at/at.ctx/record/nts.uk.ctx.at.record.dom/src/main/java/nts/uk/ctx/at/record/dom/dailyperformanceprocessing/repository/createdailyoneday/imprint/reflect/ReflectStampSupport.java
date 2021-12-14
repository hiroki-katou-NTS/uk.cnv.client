/**
 * 
 */
package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflect;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.repo.EmpInfoTerminalRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.WorkInformationTemporary;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.SupportCardRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.StampReflectRangeOutput;

/**
 * alg : 打刻応援反映する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績処理.作成処理.一日の日別実績の作成処理（New）.打刻反映.打刻応援反映する.打刻応援反映する
 * @author laitv
 */
@Stateless
public class ReflectStampSupport {
	
	@Inject
	private SupportCardRepository supportCardRepo;
	
	@Inject
	private EmpInfoTerminalRepository empInfoTerminalRepo;
	
	@Inject
	private SupportWorkReflection supportWorkReflec;

	/**
	 * @param stamp-打刻
	 * @param integrationOfDaily-日別勤怠(Work)
	 * @param stampReflectRangeOutput-打刻反映範囲
	 */
	public void reflect(String cid, Stamp stamp, IntegrationOfDaily integrationOfDaily,
			StampReflectRangeOutput stampReflectRangeOutput) {
		
		// 勤怠打刻を取得する
		WorkTimeInformation workTimeInfor = stamp.convertToAttendanceStamp(integrationOfDaily.getYmd());
		
		// 開始区分を確認する
		StartAtr startAtr = this.checkStartIndicator(stamp);
		
		// 勤務場所と職場を取得する
		WorkInformationTemporary workInfoStampTempo = null;
		if(stamp.getRefActualResults() != null && stamp.getRefActualResults().getWorkInforStamp().isPresent()){
			workInfoStampTempo = stamp.getRefActualResults().getWorkInforStamp().get().getWorkInformation(supportCardRepo, empInfoTerminalRepo, cid);
		}
		
		// 応援作業反映
		SupportParam param = new SupportParam();
		param.setTimePriorityFlag(false); // 時刻優先フラグ＝False
		param.setTimeDay(workTimeInfor);  // 勤怠打刻＝取得した勤怠打刻
		param.setLocationCode(workInfoStampTempo == null ? Optional.empty() : workInfoStampTempo.getWorkLocationCD()); // 場所コード＝勤務先情報Temporary。場所コード		
		param.setWorkplaceId(workInfoStampTempo  == null ? Optional.empty() : workInfoStampTempo.getWorkplaceID()); // 職場ID＝勤務先情報Temporary。職場ID
		param.setStartAtr(startAtr); // 開始区分＝取得した開始区分
		ReflectionAtr reflectionAtr = supportWorkReflec.supportWorkReflect(cid, param, integrationOfDaily, stampReflectRangeOutput);
		
		// 反映状態を確認する
		if (reflectionAtr == ReflectionAtr.REFLECTED) {
			// 打刻は反映済みをする
			stamp.getImprintReflectionStatus().markAsReflected(integrationOfDaily.getYmd());
		}
	}
	
	// 開始区分を確認する -> 開始区分を取得する
	public StartAtr checkStartIndicator(Stamp stamp){
		//打刻。打刻種類。時刻変更区分を確認する
		ChangeClockArt changeClockArt = stamp.getType().getChangeClockArt();
		
		if(changeClockArt == ChangeClockArt.WORKING_OUT || changeClockArt == ChangeClockArt.END_OF_SUPPORT){
			// 退勤OR応援終了の場合
			return StartAtr.END_OF_SUPPORT;
		} else {
			// 以外の場合
			return StartAtr.START_OF_SUPPORT;
		}
	}
}
