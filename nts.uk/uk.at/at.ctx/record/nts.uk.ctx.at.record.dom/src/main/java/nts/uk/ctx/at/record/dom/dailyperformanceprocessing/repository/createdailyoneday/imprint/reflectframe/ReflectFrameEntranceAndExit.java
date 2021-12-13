package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflectframe;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflect.ReflectEntranceAndExit;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflectondomain.ReflectionInformation;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.attendancetime.reflectleavingwork.CheckRangeReflectLeavingWork;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.attendancetime.reflectwork.CheckRangeReflectAttd;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.attendancetime.reflectwork.OutputCheckRangeReflectAttd;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.StampReflectRangeOutput;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSet;

/**
 * 枠反映する (new_2020) in 入退門反映する
 * 
 * @author tutk
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ReflectFrameEntranceAndExit {

	@Inject
	private CheckRangeReflectAttd checkRangeReflectAttd;

	@Inject
	private ReflectEntranceAndExit reflectEntranceAndExit;
	
	@Inject
	private CheckRangeReflectLeavingWork checkRangeReflectLeavingWork;

	/**
	 * 
	 * @param listReflectionInformation
	 * @param stamp
	 * @param stampReflectRangeOutput
	 * @param integrationOfDaily
	 */
	public List<ReflectionInformation> reflect(List<ReflectionInformation> listReflectionInformation, Stamp stamp,
			StampReflectRangeOutput stampReflectRangeOutput, IntegrationOfDaily integrationOfDaily,WorkTimezoneStampSet workTimezoneStampSet) {
		// 反映範囲か確認する
		OutputCheckRangeReflectAttd outputCheckRangeReflectAttd = OutputCheckRangeReflectAttd.OUT_OF_RANGE;
		if(stamp.getType().getChangeClockArt() == ChangeClockAtr.BRARK || 
					   stamp.getType().getChangeClockArt() == ChangeClockAtr.PC_LOG_OFF) {
			outputCheckRangeReflectAttd = checkRangeReflectLeavingWork.checkRangeReflectAttd(stamp,
					stampReflectRangeOutput, integrationOfDaily);
		}else if(stamp.getType().getChangeClockArt() == ChangeClockAtr.OVER_TIME ||  //入門ORPCログオンの場合 
				   stamp.getType().getChangeClockArt() == ChangeClockAtr.PC_LOG_ON ) {
			outputCheckRangeReflectAttd = checkRangeReflectAttd.checkRangeReflectAttd(stamp,
					stampReflectRangeOutput, integrationOfDaily);
		}
				

		switch (outputCheckRangeReflectAttd) {
		case FIRST_TIME:
			// 反映情報（Temporary）を求める
			Optional<ReflectionInformation> reflectionInformation1 = listReflectionInformation.stream()
					.filter(x -> x.getFrameNo() == 1).findFirst();
			// 反映する
			Optional<WorkStamp> wt1 = reflectEntranceAndExit.reflect(reflectionInformation1, stamp,
					integrationOfDaily.getYmd(),workTimezoneStampSet);
			
			if(stamp.getType().getChangeClockArt() == ChangeClockAtr.BRARK ||   //退門ORPCログオフの場合
					   stamp.getType().getChangeClockArt() == ChangeClockAtr.PC_LOG_OFF ) {
				if (reflectionInformation1.isPresent()) {
					reflectionInformation1.get().setEnd(wt1);
				} else {
					listReflectionInformation.add(new ReflectionInformation(1, Optional.empty(),wt1));
				}
			
			}else if(stamp.getType().getChangeClockArt() == ChangeClockAtr.OVER_TIME ||  //入門ORPCログオンの場合 
					   stamp.getType().getChangeClockArt() == ChangeClockAtr.PC_LOG_ON ) {
				if (reflectionInformation1.isPresent()) {
					reflectionInformation1.get().setStart(wt1);
				} else {
					listReflectionInformation.add(new ReflectionInformation(1, wt1, Optional.empty()));
				}
			}
			break;

		case SECOND_TIME:
			// 反映情報（Temporary）を求める
			Optional<ReflectionInformation> reflectionInformation2 = listReflectionInformation.stream()
					.filter(x -> x.getFrameNo() == 2).findFirst();
			// 反映する
			Optional<WorkStamp> wt2 = reflectEntranceAndExit.reflect(reflectionInformation2, stamp,
					integrationOfDaily.getYmd(),workTimezoneStampSet);
			if(stamp.getType().getChangeClockArt() == ChangeClockAtr.BRARK ||   //退門ORPCログオフの場合
					   stamp.getType().getChangeClockArt() == ChangeClockAtr.PC_LOG_OFF ) {
				if (reflectionInformation2.isPresent()) {
					reflectionInformation2.get().setEnd(wt2);
				} else {
					listReflectionInformation.add(new ReflectionInformation(2, Optional.empty(),wt2));
				}
			
			}else if(stamp.getType().getChangeClockArt() == ChangeClockAtr.OVER_TIME ||  //入門ORPCログオンの場合 
					   stamp.getType().getChangeClockArt() == ChangeClockAtr.PC_LOG_ON ) {
				if (reflectionInformation2.isPresent()) {
					reflectionInformation2.get().setStart(wt2);
				} else {
					listReflectionInformation.add(new ReflectionInformation(2, wt2, Optional.empty()));
				}
			}
			break;

		default:// OUT_OF_RANGE
			return listReflectionInformation;
		}
		return listReflectionInformation;
	}

}
