package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.reflectcalcategory;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;

/**
 * 計算区分に反映する
 * @author tutk
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ReflectCalCategory {
	@Inject
	private ReflectStampCalCategory reflectStampCalCategory;
	
	public List<EditStateOfDailyAttd> reflect(ChangeCalArt changeCalArt,CalAttrOfDailyAttd calAttr,List<EditStateOfDailyAttd> editState) {
		//打刻.計算区分変更対象を確認する
		AutoCalAtrOvertime overTimeAutoCalAtr = null;
		switch (changeCalArt) {
		case EARLY_APPEARANCE:// 早出
			overTimeAutoCalAtr = calAttr.getOvertimeSetting().getEarlyOtTime().getCalAtr();
			reflectStampCalCategory.reflectStamp(calAttr.getOvertimeSetting().getEarlyOtTime(), editState, 627,overTimeAutoCalAtr);
			reflectStampCalCategory.reflectStamp(calAttr.getOvertimeSetting().getEarlyMidOtTime(), editState, 628,overTimeAutoCalAtr);
			break;
		case FIX:// ﾌﾚｯｸｽ
			overTimeAutoCalAtr = calAttr.getFlexExcessTime().getFlexOtTime().getCalAtr();
			reflectStampCalCategory.reflectStamp(calAttr.getFlexExcessTime().getFlexOtTime(), editState, 635,overTimeAutoCalAtr);
			break;
		case BRARK: // 休出
			overTimeAutoCalAtr = calAttr.getHolidayTimeSetting().getRestTime().getCalAtr();
			reflectStampCalCategory.reflectStamp(calAttr.getHolidayTimeSetting().getRestTime(), editState, 633,overTimeAutoCalAtr);
			reflectStampCalCategory.reflectStamp(calAttr.getHolidayTimeSetting().getLateNightTime(), editState, 634,overTimeAutoCalAtr);
			break;
		case OVER_TIME: // 残業
			overTimeAutoCalAtr = calAttr.getOvertimeSetting().getNormalOtTime().getCalAtr();
			reflectStampCalCategory.reflectStamp(calAttr.getOvertimeSetting().getNormalOtTime(), editState, 629,overTimeAutoCalAtr);
			reflectStampCalCategory.reflectStamp(calAttr.getOvertimeSetting().getNormalMidOtTime(), editState, 630,overTimeAutoCalAtr);
			break;
		default:
			break;
		
		}
		return editState;
	}

}
