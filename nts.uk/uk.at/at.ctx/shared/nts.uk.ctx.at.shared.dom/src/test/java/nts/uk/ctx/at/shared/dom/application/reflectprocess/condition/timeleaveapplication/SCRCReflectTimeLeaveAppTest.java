package nts.uk.ctx.at.shared.dom.application.reflectprocess.condition.timeleaveapplication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import nts.uk.ctx.at.shared.dom.application.common.ApplicationTypeShare;
import nts.uk.ctx.at.shared.dom.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.application.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.application.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.application.timeleaveapplication.TimeDigestApplicationShare;
import nts.uk.ctx.at.shared.dom.application.timeleaveapplication.TimeLeaveApplicationDetailShare;
import nts.uk.ctx.at.shared.dom.application.timeleaveapplication.TimeLeaveApplicationShare;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.AppTimeType;
import nts.uk.ctx.at.shared.dom.workcheduleworkrecord.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectCondition;
import nts.uk.ctx.at.shared.dom.workcheduleworkrecord.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveApplicationReflect;
import nts.uk.ctx.at.shared.dom.workcheduleworkrecord.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveDestination;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.enumcommon.NotUseAtr;

public class SCRCReflectTimeLeaveAppTest {

	/*
	 * テストしたい内容
	 * 
	 * →設定に応じて関連項目のみを反映
	 * 　→出勤前(1回目勤務)：出勤反映、
	 * 　→出勤前(2回目勤務)：出勤２反映、
	 *　→ 退勤後(1回目勤務)：退勤反映、
	 *　→ 退勤後(2回目勤務)：退勤２反映、
	 * 　→私用外出：私用外出反映、
	 * 　→組合外出：組合外出反映
	 * 
	 * 準備するデータ 
	 * 　→出勤前(1回目勤務)　＝　するorしない、
	 * 　→出勤前(2回目勤務)＝　するorしない、
	 *　→ 退勤後(1回目勤務)＝　するorしない、
	 *　→ 退勤後(2回目勤務)＝　するorしない、
	 * 　→私用外出：＝　するorしない、
	 * 　→組合外出：＝　するorしない
	 */
	@Test
	public void test() {
		
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createDailyRecord(ScheduleRecordClassifi.RECORD,
				2);
		
		// 出勤を反映する
		TimeLeaveApplicationShare appTimeLeav = createAppTimeLeav(AppTimeType.ATWORK, 1);// 時間休暇種類 =出勤前
		TimeLeaveApplicationReflect reflectTimeLeav = setting(AppTimeType.ATWORK);//時間休暇の反映先.出勤前 =　する
		List<Integer> lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEqualTo(Arrays.asList(31));
		// 出勤を反映しない
    	reflectTimeLeav = setting(null);//時間休暇の反映先.出勤前 = しない
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEmpty();

		// 出勤２を反映する
        appTimeLeav = createAppTimeLeav(AppTimeType.ATWORK2, 1);// 時間休暇種類 =出勤前
        reflectTimeLeav = setting(AppTimeType.ATWORK2);//時間休暇の反映先.出勤前2 = する
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEqualTo(Arrays.asList(41));

		// 出勤２を反映しない
        appTimeLeav = createAppTimeLeav(AppTimeType.ATWORK2, 1);// 時間休暇種類 =出勤前2
        reflectTimeLeav = setting(null);//時間休暇の反映先.出勤前2 = しない
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEmpty();

		// 退勤を反映する
        appTimeLeav = createAppTimeLeav(AppTimeType.OFFWORK, 1);// 時間休暇種類 =退勤後 
        reflectTimeLeav = setting(AppTimeType.OFFWORK);//時間休暇の反映先.退勤後  = する
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEqualTo(Arrays.asList(34));

		// 退勤を反映しない
        appTimeLeav = createAppTimeLeav(AppTimeType.OFFWORK, 1);// 時間休暇種類 =退勤後 
        reflectTimeLeav = setting(null);//時間休暇の反映先.退勤後  = しない
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEmpty();

		// 退勤２を反映する
        appTimeLeav = createAppTimeLeav(AppTimeType.OFFWORK2, 1);// 時間休暇種類 =退勤後2
        reflectTimeLeav = setting(AppTimeType.OFFWORK2);//時間休暇の反映先.退勤後2  = する
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEqualTo(Arrays.asList(44));

		// 退勤２を反映しない
        appTimeLeav = createAppTimeLeav(AppTimeType.OFFWORK2, 1);// 時間休暇種類 =退勤後2
        reflectTimeLeav = setting(null);//時間休暇の反映先.退勤後2  = しない
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEmpty();

		// 私用外出を反映する
        appTimeLeav = createAppTimeLeav(AppTimeType.PRIVATE, 1);// 時間休暇種類 =私用外出
        reflectTimeLeav = setting(AppTimeType.PRIVATE);//時間休暇の反映先.私用外出 = する
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
		assertThat(dailyApp.getOutingTime().get().getOutingTimeSheets().get(0).getReasonForGoOut()).isEqualTo(GoingOutReason.PRIVATE);
        assertThat(lstResult).isEqualTo(Arrays.asList(88, 91, 86));//外出時刻1,戻り時刻1,外出区分1

		// 私用外出を反映しない
        appTimeLeav = createAppTimeLeav(AppTimeType.PRIVATE, 1);// 時間休暇種類 =私用外出
        reflectTimeLeav = setting(null);//時間休暇の反映先.私用外出 = しない
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEmpty();

		// 組合外出を反映する
        appTimeLeav = createAppTimeLeav(AppTimeType.UNION, 1);// 時間休暇種類 =私用外出
        reflectTimeLeav = setting(AppTimeType.UNION);//時間休暇の反映先.私用外出 = する
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
		assertThat(dailyApp.getOutingTime().get().getOutingTimeSheets().get(0).getReasonForGoOut()).isEqualTo(GoingOutReason.UNION);
        assertThat(lstResult).isEqualTo(Arrays.asList(88, 91, 86));//外出時刻1,戻り時刻1,外出区分1

		// 組合外出を反映しない
        appTimeLeav = createAppTimeLeav(AppTimeType.UNION, 1);// 時間休暇種類 =私用外出
        reflectTimeLeav = setting(null);//時間休暇の反映先.私用外出 = しない
		lstResult = SCRCReflectTimeLeaveApp.reflect(appTimeLeav, dailyApp, reflectTimeLeav);
        assertThat(lstResult).isEmpty();
		
	}
	
	private TimeLeaveApplicationShare createAppTimeLeav(AppTimeType appTimeType, int no) {

		AttendanceTime timeCommon = new AttendanceTime(60);
		TimeDigestApplicationShare digest = new TimeDigestApplicationShare(timeCommon, timeCommon, timeCommon,
				timeCommon, timeCommon, timeCommon, Optional.of(1));
		TimeZoneWithWorkNo timeZone = new TimeZoneWithWorkNo(no, 480, 1080);
		TimeLeaveApplicationDetailShare detail = new TimeLeaveApplicationDetailShare(appTimeType,
				Arrays.asList(timeZone), digest);
		return new TimeLeaveApplicationShare(ReflectApplicationHelper.createAppShare(
				ApplicationTypeShare.ANNUAL_HOLIDAY_APPLICATION, PrePostAtrShare.POSTERIOR), Arrays.asList(detail));
	}
	
	private TimeLeaveApplicationReflect setting(AppTimeType type) {
		return new TimeLeaveApplicationReflect("", NotUseAtr.USE,
				new TimeLeaveDestination(
						type != null && type == AppTimeType.ATWORK ? NotUseAtr.USE : NotUseAtr.NOT_USE, 
						type != null && type == AppTimeType.ATWORK2? NotUseAtr.USE : NotUseAtr.NOT_USE, 
						type != null && type == AppTimeType.PRIVATE? NotUseAtr.USE : NotUseAtr.NOT_USE, 
						type != null && type == AppTimeType.UNION? NotUseAtr.USE : NotUseAtr.NOT_USE, 
						type != null && type == AppTimeType.OFFWORK? NotUseAtr.USE : NotUseAtr.NOT_USE,  
						type != null && type == AppTimeType.OFFWORK2? NotUseAtr.USE : NotUseAtr.NOT_USE), 
				new TimeLeaveAppReflectCondition(NotUseAtr.NOT_USE, NotUseAtr.NOT_USE, NotUseAtr.NOT_USE,
						NotUseAtr.NOT_USE, NotUseAtr.NOT_USE, NotUseAtr.NOT_USE));
	}

}
