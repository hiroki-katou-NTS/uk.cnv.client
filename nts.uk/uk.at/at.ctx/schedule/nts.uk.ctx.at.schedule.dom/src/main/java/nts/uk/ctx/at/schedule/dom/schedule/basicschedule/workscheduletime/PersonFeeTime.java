/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.dom.schedule.basicschedule.workscheduletime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;

/**
 * The Class PersonFeeTime.
 */
// 勤務予定人件費時間
@Getter
@AllArgsConstructor
public class PersonFeeTime extends DomainObject {

	/** The no. */
	// NO
	private ExtraTimeItemNo no;

	/** The person fee time. */
	// 人件費時間
	private AttendanceTime personFeeTime;

	public static PersonFeeTime createFromJavaType(int no, int personFeeTime) {
		return new PersonFeeTime(EnumAdaptor.valueOf(no, ExtraTimeItemNo.class),
				new AttendanceTime(Integer.valueOf(personFeeTime)));
	}
	
	public static PersonFeeTime createFromJavaType(int no, AttendanceTime personFeeTime) {
		return new PersonFeeTime(EnumAdaptor.valueOf(no, ExtraTimeItemNo.class),
				personFeeTime);
	}
	
	public boolean diffPersonFeeTime(AttendanceTime attendanceTime){
		return personFeeTime.v().intValue() != attendanceTime.v().intValue();
	}
}
