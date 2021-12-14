package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.UpdateEditSttCreateBeforeAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.CancelAppStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

/**
 * @author thanh_nx
 *
 *         応援開始・終了の反映
 */
public class ReflectSupportStartEnd {

	public static List<Integer> reflect(Require require, DailyRecordOfApplication dailyApp,
			List<TimeStampAppShare> listTimeStampApp) {
		List<Integer> lstItemId = new ArrayList<>();
		//input.応援(List）でループ
		listTimeStampApp.stream().forEach(data -> {

			Optional<OuenWorkTimeSheetOfDailyAttendance> ouenOpt = dailyApp.getOuenTimeSheet().stream().filter(
					x -> x.getWorkNo() == data.getDestinationTimeApp().getSupportWork().orElse(Integer.MAX_VALUE))
					.findFirst();

			if (ouenOpt.isPresent()) {
				val result = update(require, ouenOpt.get(), data);
				OuenWorkTimeSheetOfDailyAttendance update = result.getLeft();

				dailyApp.getOuenTimeSheet().remove(ouenOpt.get());
				dailyApp.getOuenTimeSheet().add(update);
				lstItemId.addAll(result.getRight());
			} else {
				//処理中の応援枠NOをキーに[日別勤怠の応援作業時間帯]を作成する
				val result = create(require, dailyApp, data);
				dailyApp.getOuenTimeSheet().add(result.getLeft());
				lstItemId.addAll(result.getRight());
			}
		});

		// 申請反映状態にする
		UpdateEditSttCreateBeforeAppReflect.update(dailyApp, lstItemId);
		return lstItemId;
	}

	private static Pair<OuenWorkTimeSheetOfDailyAttendance, List<Integer>> create(Require require,
			DailyRecordOfApplication dailyApp, TimeStampAppShare data) {

		List<Integer> lstItemId = new ArrayList<>();
		TimeSheetOfAttendanceEachOuenSheet sheet = null;
		if (data.getDestinationTimeApp().getStartEndClassification() == StartEndClassificationShare.START) {
			sheet = TimeSheetOfAttendanceEachOuenSheet.create(
					new WorkNo(data.getDestinationTimeApp().getSupportWork().orElse(null)),
					Optional.of(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.APPLICATION, Optional.empty()),
							data.getTimeOfDay())),
					Optional.empty());
			lstItemId.add(CancelAppStamp.createItemId(929, data.getDestinationTimeApp().getEngraveFrameNo(), 10));
		} else {
			sheet = TimeSheetOfAttendanceEachOuenSheet.create(
					new WorkNo(data.getDestinationTimeApp().getSupportWork().orElse(null)), Optional.empty(),
					Optional.of(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.APPLICATION, null),
							data.getTimeOfDay())));
			lstItemId.add(CancelAppStamp.createItemId(930, data.getDestinationTimeApp().getEngraveFrameNo(), 10));
		}

		WorkplaceOfWorkEachOuen workplace = WorkplaceOfWorkEachOuen.create(
				new WorkplaceId(data.getWorkPlaceId().map(x -> x.v()).orElse(dailyApp.getAffiliationInfor().getWplID())),
				data.getWorkLocationCd().orElse(null));
		lstItemId.add(CancelAppStamp.createItemId(921, data.getDestinationTimeApp().getEngraveFrameNo(), 10));

		WorkContent workContent = WorkContent.create(workplace, Optional.empty(), Optional.empty());
		return Pair.of(
				OuenWorkTimeSheetOfDailyAttendance.create(
						data.getDestinationTimeApp().getSupportWork().orElse(Integer.MAX_VALUE), workContent, sheet),
				lstItemId);

	}

	private static Pair<OuenWorkTimeSheetOfDailyAttendance, List<Integer>> update(Require require, 
			OuenWorkTimeSheetOfDailyAttendance old,
			TimeStampAppShare data) {
		List<Integer> lstItemId = new ArrayList<>();
		TimeSheetOfAttendanceEachOuenSheet sheet = null;
		if (data.getDestinationTimeApp().getStartEndClassification() == StartEndClassificationShare.START) {
			sheet = TimeSheetOfAttendanceEachOuenSheet.create(old.getTimeSheet().getWorkNo(),
					Optional.of(new WorkTimeInformation(
							new ReasonTimeChange(TimeChangeMeans.APPLICATION, old.getTimeSheet().getStart()
									.flatMap(x -> x.getReasonTimeChange().getEngravingMethod())),
							data.getTimeOfDay())),
					old.getTimeSheet().getEnd());
			lstItemId.add(CancelAppStamp.createItemId(929, data.getDestinationTimeApp().getEngraveFrameNo(), 10));
		} else {
			sheet = TimeSheetOfAttendanceEachOuenSheet.create(
					new WorkNo(data.getDestinationTimeApp().getEngraveFrameNo()), old.getTimeSheet().getStart(),
					Optional.of(new WorkTimeInformation(
							new ReasonTimeChange(TimeChangeMeans.APPLICATION, old.getTimeSheet().getEnd()
									.map(x -> x.getReasonTimeChange().getEngravingMethod()).orElse(null)),
							data.getTimeOfDay())));
			lstItemId.add(CancelAppStamp.createItemId(930, data.getDestinationTimeApp().getEngraveFrameNo(), 10));
		}

		WorkplaceOfWorkEachOuen workplace = null;
		if (data.getWorkLocationCd().isPresent()) {
			workplace = WorkplaceOfWorkEachOuen.create(old.getWorkContent().getWorkplace().getWorkplaceId(),
					data.getWorkLocationCd().get());
			lstItemId.add(CancelAppStamp.createItemId(921, data.getDestinationTimeApp().getEngraveFrameNo(), 10));
		} else {
			workplace = old.getWorkContent().getWorkplace();
		}

		WorkContent workContent = WorkContent.create(workplace, old.getWorkContent().getWork(), Optional.empty());
		return Pair.of(OuenWorkTimeSheetOfDailyAttendance.create(old.getWorkNo(), workContent, sheet), lstItemId);

	}

	public static interface Require {
	}
}
