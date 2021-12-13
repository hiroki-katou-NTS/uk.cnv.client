package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflect;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.EndStatus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.OutputTimeReflectForWorkinfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.StampReflectRangeOutput;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.TimeReflectFromWorkinfo;
import nts.uk.shr.com.context.AppContexts;

/**
 * 出退勤で応援データ補正する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績処理.作成処理.一日の日別実績の作成処理（New）.打刻反映.打刻応援反映する.出退勤で応援データ補正する.出退勤で応援データ補正する
 * @author phongtq
 *
 */
@Stateless
public class CorrectSupportDataWork {
	
	@Inject
	private TimeReflectFromWorkinfo fromWorkinfo;
	
	@Inject
	private SupportWorkReflection workReflection;
	
	public SupportDataWork correctSupportDataWork(IntegrationOfDaily integrationOfDaily) {
		String companyId = AppContexts.user().companyId();
		
		// 勤務情報から打刻反映時間帯を取得する
		// output : 終了状態, 打刻反映範囲
		OutputTimeReflectForWorkinfo workinfo = fromWorkinfo.get(companyId, integrationOfDaily.getEmployeeId(), integrationOfDaily.getYmd(), integrationOfDaily.getWorkInformation());
		
		// Check list lỗi (Không có trong EA nhưng đã comfirm vs Tín)
		if(!workinfo.getError().isEmpty())
			return new SupportDataWork(Optional.empty(), Optional.of(workinfo.getError().get(0).getMessageError().v()));
		
		// 終了状態が正常以外の場合
		if(workinfo.getEndStatus() != EndStatus.NORMAL) {
			switch(workinfo.getEndStatus()) {
			// 就業時間帯なしの場合：「Msg_591」
			case NO_WORK_TIME: 
				return new SupportDataWork(Optional.empty(), Optional.of("Msg_591"));
			// 勤務種類なしの場合：「Msg_590」
			case NO_WORK_TYPE: 
				return new SupportDataWork(Optional.empty(), Optional.of("Msg_590"));
			// 休日出勤設定なしの場合：「Msg_1678」
			case NO_HOLIDAY_SETTING: 
				return new SupportDataWork(Optional.empty(), Optional.of("Msg_1678"));
			// 労働条件なしの場合：「Msg_430」
			case NO_WORK_CONDITION: 
				return new SupportDataWork(Optional.empty(), Optional.of("Msg_430"));
			default:
				break;
			}
		} else {
			//終了状態は正常の場合
			// 1回目の出退勤を取得する
			// 日別勤怠（Work）。出退勤。時間帯（勤務枠No１）
			Optional<TimeLeavingWork> leavingWork = Optional.empty();
			
			if(integrationOfDaily.getAttendanceLeave().isPresent())
			leavingWork = integrationOfDaily.getAttendanceLeave().get().getTimeLeavingWorks().stream().filter(x -> x.getWorkNo().v() == 1).findFirst();
			
			// 取得できる
			if(leavingWork.isPresent()) {
				ReflectionAtr reflectionAtr = this.supportCorrectWork(companyId, leavingWork, workinfo.getStampReflectRangeOutput(), integrationOfDaily);
				// 反映状態を確認する - 反映済みの場合
				if(reflectionAtr == ReflectionAtr.REFLECTED) {
					return new SupportDataWork(Optional.of(integrationOfDaily), Optional.empty());
				}
			}
			
			// 取得できない
			// 反映状態を確認する - 未反映の場合
			// 2回目の出退勤を取得する
			Optional<TimeLeavingWork> leavingWork2 = Optional.empty();
			
			if(integrationOfDaily.getAttendanceLeave().isPresent())
			leavingWork2 = integrationOfDaily.getAttendanceLeave().get().getTimeLeavingWorks().stream().filter(x -> x.getWorkNo().v() == 2).findFirst();
			
			if(leavingWork2.isPresent()) {
				this.supportCorrectWork(companyId, leavingWork2, workinfo.getStampReflectRangeOutput(), integrationOfDaily);
			}
			
		}
		return new SupportDataWork(Optional.of(integrationOfDaily), Optional.empty());
	}
	
	// 出退勤で応援補正する
	public ReflectionAtr supportCorrectWork(String cid, Optional<TimeLeavingWork> leavingWork,
			StampReflectRangeOutput stampReflectRangeOutput, IntegrationOfDaily integrationOfDaily) {
		// 出退勤の出勤を確認する
		// Nullじゃない場合
		if (leavingWork.get().getAttendanceStamp().isPresent()) {
			// 応援作業反映
			SupportParam param = new SupportParam(true,
					StartAtr.START_OF_SUPPORT,
					leavingWork.get().getAttendanceStamp().get().getStamp().isPresent() ?
							leavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay() : null, 
					Optional.empty(), Optional.empty(), Optional.empty());
			workReflection.supportWorkReflect(cid, param, integrationOfDaily, stampReflectRangeOutput);
			// 「反映状態＝反映済み」を返す
			return ReflectionAtr.REFLECTED;
			// Nullの場合
		} else {
			// 出退勤の退勤を確認する
			// Nullじゃない場合
			if (leavingWork.get().getLeaveStamp().isPresent()) {
				// 応援作業反映
				SupportParam param = new SupportParam(true,
						StartAtr.END_OF_SUPPORT, 
						leavingWork.get().getLeaveStamp().get().getStamp().isPresent() ? 
								leavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay() : null,
						Optional.empty(), Optional.empty(), Optional.empty()); // TODO
				workReflection.supportWorkReflect(cid, param, integrationOfDaily, stampReflectRangeOutput);
				// 「反映状態＝反映済み」を返す
				return ReflectionAtr.REFLECTED;
				// Nullの場合
			} else {
				return ReflectionAtr.NOTREFLECTED;
			}
		}
	}
}
