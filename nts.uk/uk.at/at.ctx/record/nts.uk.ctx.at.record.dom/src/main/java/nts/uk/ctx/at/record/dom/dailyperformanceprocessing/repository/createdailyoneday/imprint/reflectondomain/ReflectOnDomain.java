package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflectondomain;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.EngravingMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * ドメインに反映する(new_2020)
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績作成処理.作成処理.アルゴリズム.一日の日別実績の作成処理（New）.打刻反映.入退門・PC反映する.ドメインに反映する.ドメインに反映する
 * @author tutk
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ReflectOnDomain {
	/**
	 * 
	 * @param workStamp 反映先勤怠打刻
	 * @param stamp 打刻
	 * @param ymd  処理中の年月日
	 */
	public WorkStamp reflect(WorkStamp workStamp,Stamp stamp,GeneralDate ymd) {
		//日区分付き時刻を求める
		TimeWithDayAttr timeWithDayAttr = TimeWithDayAttr.convertToTimeWithDayAttr(ymd,
				stamp.getStampDateTime().toDate(), stamp.getStampDateTime().clockHourMinute().v());
		//打刻方法を打刻元情報に変換する
		ReasonTimeChange reasonTimeChange =  new ReasonTimeChange(TimeChangeMeans.REAL_STAMP, Optional.of(EngravingMethod.TIME_RECORD_ID_INPUT));
		workStamp.getTimeDay().setTimeWithDay(Optional.ofNullable(timeWithDayAttr));
		workStamp.getTimeDay().setReasonTimeChange(reasonTimeChange);
		
		Optional<WorkLocationCD> workLocationCD = Optional.empty();
		if(stamp.getRefActualResults() != null && stamp.getRefActualResults().getWorkInforStamp().isPresent()){
			workLocationCD = stamp.getRefActualResults().getWorkInforStamp().get().getWorkLocationCD();
		}
		workStamp.setLocationCode(workLocationCD);
		//「打刻．反映済み区分」をtrueにする
		stamp.getImprintReflectionStatus().markAsReflected(ymd);
		return workStamp;
		
	}
}
