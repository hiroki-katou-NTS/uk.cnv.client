package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.breakouting.reflectgoingoutandreturn.reflecttimeofday.imprint;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.breakouting.reflectgoingoutandreturn.TimeFrame;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.breakouting.reflectgoingoutandreturn.reflecttimeofday.ReflectActualStampAndStamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;

/**
 * 打刻反映する (new_2020)
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績作成処理.作成処理.アルゴリズム.一日の日別実績の作成処理（New）.打刻反映.外出・臨時反映する.時間帯反映する.打刻反映する.打刻反映する
 * @author tutk
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ImprintReflectTimeOfDay {
	
	@Inject
	private ReflectActualStampAndStamp reflectActualStampAndStamp;
	/**
	 * 
	 * @param isStartTime 開始時刻か
	 * @param timeFrame 反映先時間帯枠（Temporary）
	 * @param timeFrameNext 次の時間帯枠（Temporary）　（Optional）
	 * @param stamp 打刻
	 * @param end 反映先勤怠打刻(実打刻付き)　（Optional）
	 * @param workTimeCode 処理中の年月日の就業時間帯コード
	 * @param ymd  処理中の年月日
	 */
	public TimeActualStamp imprint(boolean isStartTime,TimeFrame timeFrame,Optional<TimeFrame> timeFrameNext,Stamp stamp,
			Optional<TimeActualStamp> end,WorkTimeCode workTimeCode,GeneralDate ymd) {
		if(end.isPresent()) {
			boolean isOverWritten =  false;
			if(end.get().getStamp().isPresent() 
					&& end.get().getStamp().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.AUTOMATIC_SET  ) {
				isOverWritten =  true;
			}
			if(isOverWritten) {
				//反映先勤怠打刻(実打刻付き)の連携時刻と打刻の時刻を比較する
				boolean check = false;
				if(isStartTime) {
					//⁂開始時刻か＝True
					//True＝反映先時間帯枠（Temporary）。終了。時刻＞＝打刻。時刻
					if(!timeFrame.getEnd().isPresent() || !timeFrame.getEnd().get().getStamp().isPresent()
							|| !timeFrame.getEnd().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
							|| timeFrame.getEnd().get().getStamp().get().getTimeDay().getTimeWithDay().get().valueAsMinutes() 
								>= stamp.getStampDateTime().clockHourMinute().valueAsMinutes()) {
						check = true;
					}
				}else {
					//⁂開始時刻か＝False
					//True＝次の時間帯枠（Temporary）。開始。時刻＞打刻。時刻
					if(!timeFrameNext.isPresent() || !timeFrameNext.get().getStart().isPresent() || !timeFrameNext.get().getStart().get().getStamp().isPresent()
							|| !timeFrameNext.get().getStart().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
							|| timeFrameNext.get().getStart().get().getStamp().get().getTimeDay().getTimeWithDay().get().valueAsMinutes() 
								> stamp.getStampDateTime().clockHourMinute().valueAsMinutes()) {
						check = true;
					}
				}
				if(check) {
					//実打刻と打刻反映する
					return reflectActualStampAndStamp.reflect(end.get(), stamp, true, timeFrame, ymd, workTimeCode,isStartTime);
				}
				
			}else {
				//勤怠打刻(実打刻付き)の時刻と打刻の時刻を比較する
				if(end.get().getStamp().isPresent()
						&& end.get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
						&& end.get().getStamp().get().getTimeDay().getTimeWithDay().get().valueAsMinutes() 
							== stamp.getStampDateTime().clockHourMinute().valueAsMinutes()) {
					//実打刻と打刻反映する
					return reflectActualStampAndStamp.reflect(end.get(), stamp, false, timeFrame, ymd, workTimeCode,isStartTime);
				}
			}
			
			
		}
			//勤怠打刻(実打刻付き)を作成する
			TimeActualStamp newTimeActualStamp = new TimeActualStamp(Optional.empty(), Optional.empty(), 0, Optional.empty(), Optional.empty());
			end = Optional.of(newTimeActualStamp);
			//実打刻と打刻反映する
			return reflectActualStampAndStamp.reflect(end.get(), stamp, true, timeFrame, ymd, workTimeCode,isStartTime);
		
	}

}
