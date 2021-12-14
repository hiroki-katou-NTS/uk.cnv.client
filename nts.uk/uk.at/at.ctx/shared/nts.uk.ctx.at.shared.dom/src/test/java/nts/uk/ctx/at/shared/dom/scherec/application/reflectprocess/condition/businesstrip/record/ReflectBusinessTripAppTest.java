package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.businesstrip.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.scherec.application.bussinesstrip.BusinessTripInfoShare;
import nts.uk.ctx.at.shared.dom.scherec.application.bussinesstrip.BusinessTripWorkTime;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.businesstrip.ReflectBusinessTripApp;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectWorkInformation;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.workchangeapp.ReflectWorkChangeApp.WorkInfoDto;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktype.DailyWork;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * @author thanh_nx
 * 
 *         出張申請の反映（勤務実績）
 */
@RunWith(JMockit.class)
public class ReflectBusinessTripAppTest {

	@Injectable
	private ReflectBusinessTripApp.Require require;
	
	/*
	 * テストしたい内容
	 * 
	 * →勤務情報の反映がある
	 * 
	 * →出退勤の反映がない
	 * 
	 * 準備するデータ
	 * 
	 * →[出張勤務情報. 勤務時間帯] がない
	 * 
	 * →対象となる日に出張勤務情報を持っている
	 */
	@Test
	public void testNoReflectInfo() {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createDailyRecord(ScheduleRecordClassifi.RECORD,
				2);

		List<Integer> actualResult = new ArrayList<>();
		actualResult.addAll((new ReflectBusinessTripApp("1")).reflectRecord(require,
				createTripInfo(Collections.emptyList()), dailyApp));

		assertThat(actualResult).isEqualTo(Arrays.asList(28, 1292, 1293, 29));

	}

	/*
	 * テストしたい内容
	 * 
	 * 
	 * →出退勤の反映があります
	 * 
	 * 
	 * →始業終業の反映があります
	 * 
	 * 準備するデータ
	 * 
	 * →[出張勤務情報. 勤務時間帯] があります
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReflectInfo(@Mocked ReflectWorkInformation rfInfo) {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createDailyRecord(ScheduleRecordClassifi.RECORD,
				2);
		new Expectations() {
			{
				
				require.getWorkType(anyString);
				result = Optional.of(new WorkType(new WorkTypeCode("001"), null, null, null, null,
						new DailyWork(WorkTypeUnit.OneDay, WorkTypeClassification.HolidayWork, null, null)));
				
				ReflectWorkInformation.reflectInfo(require, anyString, (WorkInfoDto) any, (DailyRecordOfApplication) any, (Optional<Boolean>) any,  (Optional<Boolean>) any);
				result = Arrays.asList(28, 1292, 1293, 29);
		
			}
		};
		
		List<Integer> actualResult = new ArrayList<>();
		actualResult.addAll((new ReflectBusinessTripApp("1")).reflectRecord(require, createTripInfoWorkHour(), dailyApp));

		assertThat(actualResult).isEqualTo(Arrays.asList(28, 1292, 1293, 29, 3, 4, 5, 6, 31, 34, 41, 44));

	}

	private BusinessTripInfoShare createTripInfoWorkHour() {

		List<BusinessTripWorkTime> workingHours = new ArrayList<>();
		workingHours.add(new BusinessTripWorkTime(new WorkNo(1), Optional.of(new TimeWithDayAttr(1)), Optional.of(new TimeWithDayAttr(1))));
		workingHours.add(new BusinessTripWorkTime(new WorkNo(2), Optional.of(new TimeWithDayAttr(2)), Optional.of(new TimeWithDayAttr(2))));

		return createTripInfo(workingHours);
	}

	private BusinessTripInfoShare createTripInfo(List<BusinessTripWorkTime> workingHours) {
		BusinessTripInfoShare info = new BusinessTripInfoShare(
				ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT), new WorkInformation("1", "2"),
				workingHours);

		return info;
	}

}
