package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.breakouting.reflectgoingoutandreturn.reflecttimeofday;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.breakouting.reflectgoingoutandreturn.TimeFrame;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.EngravingMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.getcommonset.GetCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.common.FontRearSection;
import nts.uk.ctx.at.shared.dom.worktime.common.InstantRounding;
import nts.uk.ctx.at.shared.dom.worktime.common.RoundingSet;
import nts.uk.ctx.at.shared.dom.worktime.common.Superiority;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSet;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 実打刻と打刻反映する (new_2020)
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績作成処理.作成処理.アルゴリズム.一日の日別実績の作成処理（New）.打刻反映.外出・臨時反映する.時間帯反映する.実打刻と打刻反映する.実打刻と打刻反映する
 * @author tutk
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ReflectActualStampAndStamp {
	
//	@Inject
//	private GetCommonSet getCommonSet;
	
	@Inject
	private RecordDomRequireService requireService;
	
	/**
	 * 
	 * @param timeActualStamp 反映先勤怠打刻(実打刻付き)
	 * @param stamp 打刻
	 * @param isReflectTimeStamp 普通打刻反映するか
	 * @param timeFrame 反映先時間帯枠（Temporary）
	 * @param ymd  処理中の年月日
	 * @param workTimeCode 処理中の年月日の就業時間帯コード
	 * @param isStartTime 開始区分
	 */
	public TimeActualStamp reflect(TimeActualStamp timeActualStamp,Stamp stamp,boolean isReflectTimeStamp,TimeFrame timeFrame,GeneralDate ymd,WorkTimeCode workTimeCode, boolean isStartTime) {
		//if not (False←普通打刻反映するか＝False　AND　勤怠打刻(実打刻付き)．実打刻に値が入っている)
		if(!(!isReflectTimeStamp && (timeActualStamp.getActualStamp().isPresent() 
										&& timeActualStamp.getActualStamp().get().getTimeDay().getTimeWithDay().isPresent() ))) {
			
			//日区分付き時刻を求める
			TimeWithDayAttr timeWithDayAttr = TimeWithDayAttr.convertToTimeWithDayAttr(ymd,
					stamp.getStampDateTime().toDate(), stamp.getStampDateTime().clockHourMinute().v());
			//実打刻コピーする
			TimeActualStamp timeStampCopy = new TimeActualStamp(timeActualStamp.getActualStamp(), timeActualStamp.getStamp(), timeActualStamp.getNumberOfReflectionStamp(), timeActualStamp.getOvertimeDeclaration(), timeActualStamp.getTimeVacation());
			//打刻方法を打刻元情報に変換する
			WorkTimeInformation timeDay = new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.REAL_STAMP, Optional.of(EngravingMethod.TIME_RECORD_ID_INPUT)), timeWithDayAttr);
			WorkStamp workStamp = new WorkStamp(timeDay, stamp.getRefActualResults().getWorkInforStamp().isPresent() ? stamp.getRefActualResults().getWorkInforStamp().get().getWorkLocationCD() : null);
			timeStampCopy.setActualStamp(Optional.of(workStamp));
			if(isStartTime || !timeFrame.getGoOutReason().isPresent() ) {
				//時間帯枠（Temporary）。理由←外出理由
				timeFrame.setGoOutReason(stamp.getType().getGoOutArt());
			}
			//普通打刻反映＝True
			if(isReflectTimeStamp) {
				timeStampCopy.setStamp(timeStampCopy.getActualStamp());
			}
			timeActualStamp = timeStampCopy;
		}
		int number = timeActualStamp.getNumberOfReflectionStamp()!=null?timeActualStamp.getNumberOfReflectionStamp()+1:1;
		//打刻反映回数を＋１する
		timeFrame.setNumberOfReflections(number);
		timeActualStamp.setNumberOfReflectionStamp(number);
		//反映済み区分　←　true
		stamp.getImprintReflectionStatus().markAsReflected(ymd);
		
		return timeActualStamp;
	}
}
