package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.at.record.dom.jobmanagement.usagesetting.ManHrInputUsageSetting;
import nts.uk.ctx.at.record.dom.jobmanagement.usagesetting.ManHrInputUsageSettingRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.GetSupportDataJudgedSameDS;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.JudCriteriaSameStampOfSupportRepo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.JudgmentCriteriaSameStampOfSupport;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.enu.DailyDomainGroup;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.StampReflectRangeOutput;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.operationsettings.TaskOperationSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 応援作業反映
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績処理.作成処理.一日の日別実績の作成処理（New）.打刻反映.打刻応援反映する.応援作業反映.応援作業反映
 * 
 * @author phongtq
 *
 */
@Stateless
public class SupportWorkReflection {

	@Inject
	private JudCriteriaSameStampOfSupportRepo ofSupportRepo;
	
	@Inject
	private ManHrInputUsageSettingRepository manHrInputUsageSettingRepo;
	
	@Inject
	private TaskOperationSettingRepository taskOperationSettingRepo;
	@Inject 
	private StampCardRepository stampCardRepo;
	
	@Inject
	private StampDakokuRepository stampRepo;

	public ReflectionAtr supportWorkReflect(String cid, SupportParam param, IntegrationOfDaily integrationOfDaily,
			StampReflectRangeOutput stampReflectRangeOutput) {
		
		/** 工数入力の利用設定を取得する */
		val manHrInputUsageSet = manHrInputUsageSettingRepo.get(cid);
		
		val require = new ManHrInputUsageSetting.Require() {
			
			@Override
			public Optional<TaskOperationSetting> taskOperationSetting(String cid) {
				return taskOperationSettingRepo.getTasksOperationSetting(cid);
			}
		};
		
		/** 作業実績の補正処理を行っても良いか判断する */
		if (!manHrInputUsageSet.map(c -> c.decideCanCorrectTaskRecord(require)).orElse(false)) 
			/** 反映状態＝反映失敗を返す */		
			return ReflectionAtr.REFLECT_FAIL;

		// 打刻データが応援開始・終了反映時間内かの確認を行う
		boolean startAtr = this.checkStarEndSupport(param.getTimeDay(), stampReflectRangeOutput);
		if (true || !startAtr) { /** TODO：　一旦動かないようにする、タスクデモで参照するように　*/
			// 反映状態＝反映失敗を返す
			return ReflectionAtr.REFLECT_FAIL;
		}

		// 「応援の同一打刻の判断基準」を取得する TODO - còn phần lấy từ cache ra
		JudgmentCriteriaSameStampOfSupport judgmentSupport = ofSupportRepo.get(cid);

		// 職場、場所を取得する - lấy workplace và nơi làm việc
		WorkInformationWork informationWork = this.getWorkplaceWorkCDData(integrationOfDaily.getEmployeeId(),
				integrationOfDaily.getAffiliationInfor(), param.getStartAtr(), param.getTimeDay(),
				param.getLocationCode(), param.getWorkplaceId());

		// 日別勤怠（Work）から応援時間帯を取得する - 応援データ一覧 - lấy dữ liệu support từ 日別勤怠（Work）
		List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWorkTime = integrationOfDaily.getOuenTimeSheet();
		// 反映前状態の応援データ一覧を作る TODO - 反映前の応援データ一覧 - tạo ra list data support trước khi phản ánh
		List<OuenWorkTimeSheetOfDailyAttendance> lstOuenBefore = new ArrayList<>();
		lstOuenBefore.addAll(lstOuenWorkTime);
		// 勤怠打刻から応援データを作る - 打刻応援データ - tạo dữ liệu daily support từ check tay (stamp) lấy được
		OuenWorkTimeSheetOfDailyAttendance ouenStamp = this.convertStampingSupportData(informationWork,
				param.getTimeDay(), informationWork.getStartAtr(), param.getWorkGroup());

		// 自動セットの応援データをクリアして、応援データをフラットする - clear dữ liệu support tự động được tạo
		List<OuenWorkTimeSheetOfDailyAttendance> lstClearOuen = this.clearSupportDataAutoFlatData(lstOuenWorkTime);

		// 打刻応援データを応援データ一覧にいれる - 打刻セット済み応援データ一覧 - cho data support check tay vào list data support
		List<OuenWorkTimeSheetOfDailyAttendance> lstStampedSupport = this.getStampedSupportData(
				param.isTimePriorityFlag(), judgmentSupport, lstClearOuen, ouenStamp, param.getStartAtr());

		// 出退勤で応援データを補正する - 補正済みの応援データ一覧 & 勤務Temporary - chỉnh sửa data support lúc đến lúc về
		CorrectSupportData correctSupportData = this.correctSupportDataFromWork(integrationOfDaily.getEmployeeId(),
				integrationOfDaily.getYmd(), integrationOfDaily.getAttendanceLeave(),
				lstStampedSupport, informationWork, judgmentSupport, ouenStamp);

		// 応援データを自動セットしてマージする - セット済み応援データ - set tự động data support rồi merge dữ liệu
		List<OuenWorkTimeSheetOfDailyAttendance> dataAutoSet = this
				.supportDataAutoSetMerged(correctSupportData.getInformationWork(), correctSupportData.getAttendance());

		// 最大応援回数を補正する - 補正済みの応援データ一覧 - chỉnh sửa số lần support tối đa
		List<OuenWorkTimeSheetOfDailyAttendance> lstCorrectMaximum = this.correctMaximumCheering(judgmentSupport,
				dataAutoSet);

		// 応援項目の編集状態補正する - chỉnh sửa trạng thái edit của support
		this.correctEditStatusSupportItem(integrationOfDaily, lstCorrectMaximum, lstOuenBefore);

		// 日別勤怠（Work）にデータ入れる
		integrationOfDaily.setOuenTimeSheet(lstCorrectMaximum);

		// 反映状態＝反映済みを返す
		return ReflectionAtr.REFLECTED;
	}

	/**
	 * 打刻データが応援開始・終了反映時間内かの確認を行う
	 * 
	 * @param timeDay
	 * @param stampReflectRangeOutput
	 * @return
	 */
	public boolean checkStarEndSupport(WorkTimeInformation timeDay, StampReflectRangeOutput stampReflectRangeOutput) {
		// パラメータ。勤怠打刻を反映範囲ないか確認する
		// 打刻反映範囲。外出。開始＜＝勤怠打刻。時刻。時刻＜＝打刻反映範囲。外出。終了
        if (timeDay != null && timeDay.getTimeWithDay().isPresent() && stampReflectRangeOutput.getGoOut().getStart().v() <= timeDay.getTimeWithDay().get().v()
				&& timeDay.getTimeWithDay().get().v() <= stampReflectRangeOutput.getGoOut().getEnd().v()) {
			return true;
		}
		return false;
	}

	/**
	 * 職場、作業CDデータを取得する
	 * 
	 * @param sid
	 * @param affiliationInfor
	 * @param startAtr
	 * @param timeDay
	 * @param locationCode
	 * @param workplaceId
	 * @return
	 */
	public WorkInformationWork getWorkplaceWorkCDData(String sid, AffiliationInforOfDailyAttd affiliationInfor,
			StartAtr startAtr, WorkTimeInformation timeDay, Optional<WorkLocationCD> locationCode,
			Optional<String> workplaceId) {
		// 勤務先情報Workを作成する
		WorkInformationWork informationWork = new WorkInformationWork(null, null, startAtr);

		// 個人情報を取得する
		WorkInformationWork informationWork2 = this.getPersonalInformation(affiliationInfor.getWplID(), informationWork);

		// パラメータ。場所コードを確認する
		if (locationCode.isPresent()) {
			// Emptyじゃない場合
			informationWork2.setLocationCD(new WorkLocationCD(locationCode.get().v()));
		}

		// パラメータ。職場IDを確認する
		if (workplaceId.isPresent()) {
			// Emptyじゃない
			informationWork2.setWorkplaceId(new WorkplaceId(workplaceId.get()));
		}

		return informationWork2;
	}

	/**
	 * 個人情報を取得する
	 * 
	 * @param sid
	 * @param informationWork
	 * @return
	 */
	public WorkInformationWork getPersonalInformation(String wplId, WorkInformationWork informationWork) {

		if (wplId != null) {
			// 勤務先情報Workを上書きする
			// 勤務先情報Work。職場ID＝取得した「社員所属職場履歴」。職場ID
			informationWork.setWorkplaceId(new WorkplaceId(wplId));
		}

		return informationWork;
	}

	/**
	 * 打刻応援データに変換する
	 * 
	 * @param informationWork
	 * @param information
	 * @param startAtr
	 * @return
	 */
	public OuenWorkTimeSheetOfDailyAttendance convertStampingSupportData(WorkInformationWork informationWork,
			WorkTimeInformation information, StartAtr startAtr, Optional<WorkGroup> workGroup) {
		// 新しい「日別実績の応援作業別勤怠時間帯」を作成する
		OuenWorkTimeSheetOfDailyAttendance sheetOfDaily = null;

		// 作業Temporarｙから「日別実績の応援作業別勤怠時間帯」にデータ入れる
		WorkplaceOfWorkEachOuen eachOuen = null;
		if (informationWork.getLocationCD() != null) {
			eachOuen = WorkplaceOfWorkEachOuen.create(new WorkplaceId(informationWork.getWorkplaceId().v()),
					new WorkLocationCD(informationWork.getLocationCD().v()));
		} else {
			eachOuen = WorkplaceOfWorkEachOuen.create(new WorkplaceId(informationWork.getWorkplaceId().v()), null);
		}

		WorkContent workContent = WorkContent.create(eachOuen, workGroup, Optional.empty());

		TimeSheetOfAttendanceEachOuenSheet timeSheet = null;

		// パラメータ。開始区分を確認する
		if (startAtr == StartAtr.START_OF_SUPPORT) {
			// 応援開始の場合
			timeSheet = TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(0), Optional.ofNullable(information),
					Optional.empty());
		}

		if (startAtr == StartAtr.END_OF_SUPPORT) {
			// 応援終了の場合
			timeSheet = TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(0), Optional.empty(),
					Optional.ofNullable(information));
		}
		sheetOfDaily = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1), workContent, timeSheet, Optional.empty());

		return sheetOfDaily;
	}

	/**
	 * 自動セットの応援データをクリアして、応援データをフラットする
	 * 
	 * @param lstOuenWorkTime
	 * @return
	 */
	public List<OuenWorkTimeSheetOfDailyAttendance> clearSupportDataAutoFlatData(
			List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWorkTime) {
		// 空の応援データ一覧を作成する
		List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWork = new ArrayList<>();

		// 応援データ一覧をループする
		for (OuenWorkTimeSheetOfDailyAttendance ouen : lstOuenWorkTime) {
			// 「開始の応援データ」が自動セットかを確認する
			// 応援データ。時間帯。開始。実打刻。時刻。時刻変更理由。時刻変更手段
			if (ouen.getTimeSheet().getStart().isPresent() && ouen.getTimeSheet().getStart().get().getReasonTimeChange()
					.getTimeChangeMeans() == TimeChangeMeans.AUTOMATIC_SET) {
				// 「応援データ。時間帯。開始」をクリアする
				ouen.getTimeSheet().setStart(null);
			}
			// 「終了の応援データ」が自動セットかを確認する
			// 応援データ。時間帯。終了。実打刻。時刻。時刻変更理由。時刻変更手段
			if (ouen.getTimeSheet().getEnd().isPresent() && ouen.getTimeSheet().getEnd().get().getReasonTimeChange()
					.getTimeChangeMeans() == TimeChangeMeans.AUTOMATIC_SET) {
				// 「応援データ。時間帯。終了」をクリアする
				ouen.getTimeSheet().setEnd(null);
			}

			// 開始と終了を確認する
			// どっちか一方ある場合 - start or end
			if ((ouen.getTimeSheet().getStart().isPresent() && !ouen.getTimeSheet().getEnd().isPresent())
					|| (!ouen.getTimeSheet().getStart().isPresent() && ouen.getTimeSheet().getEnd().isPresent())) {
				lstOuenWork.add(ouen);
			}

			// 両方がある場合 - both start and end
			if (ouen.getTimeSheet().getStart().isPresent() && ouen.getTimeSheet().getEnd().isPresent()) {
				TimeSheetOfAttendanceEachOuenSheet eachOuenSheet = TimeSheetOfAttendanceEachOuenSheet
						.create(ouen.getTimeSheet().getWorkNo(), Optional.empty(), ouen.getTimeSheet().getEnd());
				OuenWorkTimeSheetOfDailyAttendance dailyAttendance = OuenWorkTimeSheetOfDailyAttendance
						.create(ouen.getWorkNo(), ouen.getWorkContent(), eachOuenSheet, Optional.empty());

				TimeSheetOfAttendanceEachOuenSheet eachOuenSheet2 = TimeSheetOfAttendanceEachOuenSheet
						.create(ouen.getTimeSheet().getWorkNo(), ouen.getTimeSheet().getStart(), Optional.empty());
				OuenWorkTimeSheetOfDailyAttendance dailyAttendance2 = OuenWorkTimeSheetOfDailyAttendance
						.create(ouen.getWorkNo(), ouen.getWorkContent(), eachOuenSheet2, Optional.empty());

				lstOuenWork.add(dailyAttendance2);
				lstOuenWork.add(dailyAttendance);
			}
		}

		return lstOuenWork;
	}

	/**
	 * 打刻応援データを応援データ一覧にいれる
	 * 
	 * @param timePriorityFlag
	 * @param judgmentSupport
	 * @param lstClearOuen
	 * @param ouenStamp
	 * @param startAtr
	 * @return
	 */
	public List<OuenWorkTimeSheetOfDailyAttendance> getStampedSupportData(boolean timePriorityFlag,
			JudgmentCriteriaSameStampOfSupport judgmentSupport, List<OuenWorkTimeSheetOfDailyAttendance> lstClearOuen, 
			OuenWorkTimeSheetOfDailyAttendance ouenStamp, StartAtr startAtr) {
		
		val time = startAtr == StartAtr.START_OF_SUPPORT ? ouenStamp.getTimeSheet().getStart().get().getTimeWithDay().get()
				: ouenStamp.getTimeSheet().getEnd().get().getTimeWithDay().get();
		
		/** 同一と判断された応援データを取得する */
		val ouenStampDS = GetSupportDataJudgedSameDS.getSupportDataJudgedSame(new GetSupportDataJudgedSameDS.Required() {
					
					@Override
					public JudgmentCriteriaSameStampOfSupport getCriteriaSameStampOfSupport() {
						return judgmentSupport;
					}
				}, lstClearOuen, time, startAtr);
		
		if (!ouenStampDS.isPresent()) {
			/** 打刻応援データを応援データ一覧に入れる */ 
			lstClearOuen.add(ouenStamp);
			
			/** 「応援データ一覧」を返す */
			return lstClearOuen;
		}
		
		/** 上書き対象の時刻を取得する */
		val timeOverWritten = this.getTimeOverwritten(timePriorityFlag, ouenStampDS.get(), startAtr, ouenStamp);
		
		if (timeOverWritten.isPresent()) {
			// 取得した勤怠打刻＜＞Empty
			// 時刻上書きする
			this.overwriteTime(ouenStampDS.get(), startAtr, timeOverWritten);
		} else {
			
			/** 検出できた応援データの勤務先を確認する */
			if (ouenStampDS.get().getWorkContent().getWorkplace().getWorkplaceId() != null)
				return lstClearOuen;
		}
		// 応援・作業を補正する
		this.correctSupportWork(ouenStamp, ouenStampDS.get());
		return lstClearOuen;
	}

	/**
	 * 応援・作業を補正する
	 * 
	 * @param ouenConvert
	 * @param ouenStamp
	 */
	public void correctSupportWork(OuenWorkTimeSheetOfDailyAttendance ouenStamp,
			OuenWorkTimeSheetOfDailyAttendance ouenStampNew) {
		// 作業データを上書きする
		ouenStamp.getWorkContent().setWork(ouenStampNew.getWorkContent().getWork());
		// 勤務先を上書きする
		ouenStamp.getWorkContent().setWorkplace(ouenStampNew.getWorkContent().getWorkplace());
	}

	/**
	 * 時刻上書きする
	 * 
	 * @param ouenStamp
	 * @param startAtr
	 * @param timeOverWritten
	 */
	public void overwriteTime(OuenWorkTimeSheetOfDailyAttendance ouenStamp, StartAtr startAtr,
			Optional<TimeWithDayAttr> timeOverWritten) {
		// 開始区分を確認する
		if (startAtr == StartAtr.START_OF_SUPPORT) {
			ouenStamp.getTimeSheet().getStart().get().setTimeWithDay(timeOverWritten);
		}

		if (startAtr == StartAtr.END_OF_SUPPORT) {
			ouenStamp.getTimeSheet().getEnd().get().setTimeWithDay(timeOverWritten);
		}
	}

	/**
	 * 上書き対象の時刻を取得する
	 * 
	 * @param timePriorityFlag
	 * @param ouenStampNew
	 * @param startAtr
	 * @param ouenStamp
	 * @return
	 */
	public Optional<TimeWithDayAttr> getTimeOverwritten(boolean timePriorityFlag,
			OuenWorkTimeSheetOfDailyAttendance ouenStampNew, StartAtr startAtr,
			OuenWorkTimeSheetOfDailyAttendance ouenStamp) {
		if (startAtr == StartAtr.START_OF_SUPPORT) {
			// 応援開始の場合
			// 時刻上書きか確認する
			// 「打刻応援データ。開始。時刻 ＞ 応援データ。開始。時刻」 OR 時刻優先フラグ＝True
			if ((ouenStamp.getTimeSheet().getStart().get().getTimeWithDay().isPresent()
					&& ouenStampNew.getTimeSheet().getStart().get().getTimeWithDay().isPresent()
					&& ouenStamp.getTimeSheet().getStart().get().getTimeWithDay().get().v() < ouenStampNew.getTimeSheet()
							.getStart().get().getTimeWithDay().get().v())
					|| timePriorityFlag == true) {
				return ouenStamp.getTimeSheet().getStart().get().getTimeWithDay();
			}
		}
		if (startAtr == StartAtr.END_OF_SUPPORT) {
			// 上書き対象の時刻を取得する
			// 「打刻応援データ。終了。時刻 ＞ 応援データ。終了。時刻」 OR 時刻優先フラグ＝True
			if ((ouenStamp.getTimeSheet().getEnd().get().getTimeWithDay().isPresent()
					&& ouenStampNew.getTimeSheet().getEnd().get().getTimeWithDay().isPresent()
					&& ouenStamp.getTimeSheet().getEnd().get().getTimeWithDay().get().v() > ouenStampNew.getTimeSheet()
							.getEnd().get().getTimeWithDay().get().v())
					|| timePriorityFlag == true) {
				return ouenStamp.getTimeSheet().getEnd().get().getTimeWithDay();
			}
		}
		return Optional.empty();
	}

	/**
	 * 出退勤で応援データを補正する
	 * 
	 * @param attendanceLeave
	 * @param lstOuenWorkTime
	 * @param informationWork
	 * @param judgmentSupport
	 * @return
	 */
	public CorrectSupportData correctSupportDataFromWork(String sid, GeneralDate ymd, 
			Optional<TimeLeavingOfDailyAttd> attendanceLeave,
			List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWorkTime, WorkInformationWork informationWork,
			JudgmentCriteriaSameStampOfSupport judgmentSupport, OuenWorkTimeSheetOfDailyAttendance ouenStamp) {

		// 最初の出勤と最後の退勤を検出する
		WorkTemporary workTemporary = this.detectAttendance(attendanceLeave);

		val require = new Require() {
			
			@Override
			public List<StampCard> stampCard(String contractCode, String sid) {
				return stampCardRepo.getLstStampCardBySidAndContractCd(contractCode, sid);
			}
			
			@Override
			public List<Stamp> stamp(List<String> listCard, String companyId, GeneralDateTime startDate,
					GeneralDateTime endDate) {
				return stampRepo.getByDateTimeperiod(listCard, startDate, endDate);
			}
		};
		
		// ほかの出退勤を補正する（出勤2と退勤1）
		this.correctOtherAttendance(require, sid, ymd, informationWork, workTemporary, lstOuenWorkTime, judgmentSupport);

		// 応援データを並びかえる （時刻の昇順） - ソート済み応援データ
		List<OuenWorkTimeSheetOfDailyAttendance> lstOuenSort = this.rearrangeSupportData(lstOuenWorkTime);
		
		/** 最初の出勤の応援データと最後の退勤の応援データを補正する */
		this.getSupportFataFirstAttendanceLastDeparture(require, sid, ymd, lstOuenSort, workTemporary, 
				judgmentSupport, ouenStamp, informationWork);

		// 補正済みの応援データ一覧を返す
		return new CorrectSupportData(lstOuenSort, workTemporary);
	}

	/**
	 * 応援データを自動セットしてマージする
	 * 
	 * @param informationWork
	 * @param lstOuenWorkTime
	 * @return
	 */
	public List<OuenWorkTimeSheetOfDailyAttendance> supportDataAutoSetMerged(WorkTemporary informationWork,
			List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWorkTime) {
		// セット済み応援データ一覧＝Empty
		List<OuenWorkTimeSheetOfDailyAttendance> lstSetSupportData = new ArrayList<>();
		for (int i = 0; i < lstOuenWorkTime.size(); i++) {
			// 処理中の応援データの開始データを確認する
			if (lstOuenWorkTime.get(i).getTimeSheet().getStart().isPresent()) {
				OuenWorkTimeSheetOfDailyAttendance attendance = null;
				if (lstOuenWorkTime.size() > (i + 1)) {
					// 後ろの応援データを取得する
					attendance = lstOuenWorkTime.get(i + 1);
				}
				// 取得できない
				// ・勤務Temporary＝パラメータ。勤務Temporary ・応援データ一覧＝補正済み応援データ一覧 ・処理中の応援データ＝処理中の応援データ
				if (attendance == null) {
					this.correctSupportDataPutInList(informationWork, lstSetSupportData, lstOuenWorkTime.get(i));
					return lstSetSupportData;
				} else {
					// 後ろの応援データの開始データを確認する
					if (attendance.getTimeSheet().getStart().isPresent()) {
						// 存在する場合
						// 処理中の応援データを自動セットする
						OuenWorkTimeSheetOfDailyAttendance ouenWorkTime = this
								.automaticallySetSupportDataBeingProcessed(StartAtr.END_OF_SUPPORT,
										lstOuenWorkTime.get(i), attendance, informationWork);
						// 応援データを補正して一覧に入れる
						this.correctSupportDataPutInList(informationWork, lstSetSupportData, ouenWorkTime);
					} else {
						// 処理中の応援データに終了データをセットする
						if(attendance.getTimeSheet().getEnd().isPresent())
						lstOuenWorkTime.get(i).getTimeSheet().setEnd(attendance.getTimeSheet().getEnd().get());
						// 応援データを補正して一覧に入れる
						this.correctSupportDataPutInList(informationWork, lstSetSupportData, lstOuenWorkTime.get(i));
						i += 1;
					}
				}
			} else {
				// 前のセット済みの応援データを取得する
				OuenWorkTimeSheetOfDailyAttendance attendance = null;
				if (lstOuenWorkTime.size() > 0) {
					// 後ろの応援データを取得する
					if (lstSetSupportData.size() > 0)
					attendance = lstSetSupportData.get(lstSetSupportData.size() - 1);
					
					if (attendance == null) {
						// 応援データを補正して一覧に入れる
						this.correctSupportDataPutInList(informationWork, lstSetSupportData, lstOuenWorkTime.get(i));
					} else {
						// 取得できます
						// 処理中の応援データを自動セットする
						OuenWorkTimeSheetOfDailyAttendance ouenWorkTime = this
								.automaticallySetSupportDataBeingProcessed(StartAtr.START_OF_SUPPORT,
										lstOuenWorkTime.get(i), attendance, informationWork);
						// 応援データを補正して一覧に入れる
						this.correctSupportDataPutInList(informationWork, lstSetSupportData, ouenWorkTime);
					}
				}
			}
		}
		return lstSetSupportData;
	}

	/**
	 * 処理中の応援データを自動セットする
	 * 
	 * @param startAtr
	 * @param ouenWorkTime
	 * @param attendance
	 * @param informationWork
	 * @return
	 */
	public OuenWorkTimeSheetOfDailyAttendance automaticallySetSupportDataBeingProcessed(StartAtr startAtr,
			OuenWorkTimeSheetOfDailyAttendance ouenWorkTime, OuenWorkTimeSheetOfDailyAttendance attendance,
			WorkTemporary informationWork) {
		if (startAtr == StartAtr.START_OF_SUPPORT) {
			if (attendance.getTimeSheet().getEnd().isPresent()) { // check để không lỗi
				ReasonTimeChange reasonTimeChange = new ReasonTimeChange(TimeChangeMeans.valueOf(attendance.getTimeSheet().getEnd().get().getReasonTimeChange().getTimeChangeMeans() == null ? null : 
					attendance.getTimeSheet().getEnd().get().getReasonTimeChange().getTimeChangeMeans().value), 
						Optional.ofNullable(attendance.getTimeSheet().getEnd().get().getReasonTimeChange().getEngravingMethod().isPresent() ? 
								attendance.getTimeSheet().getEnd().get().getReasonTimeChange().getEngravingMethod().get() : null));
				Optional<TimeWithDayAttr> timeWithDay = attendance.getTimeSheet().getEnd().get().getTimeWithDay().isPresent() ? 
						Optional.of(new TimeWithDayAttr(attendance.getTimeSheet().getEnd().get().getTimeWithDay().get().v())) : Optional.empty();
				WorkTimeInformation end = new WorkTimeInformation(reasonTimeChange, timeWithDay.isPresent() ? timeWithDay.get() : null);
				
				ouenWorkTime.getTimeSheet().setStart(end);
				ouenWorkTime.getTimeSheet().getStart().get().getReasonTimeChange()
						.setTimeChangeMeans(TimeChangeMeans.AUTOMATIC_SET);
			} else {
				ouenWorkTime.getTimeSheet().setStart(WorkTimeInformation.createByAutomaticSet(null));
			}
		} else {
			if (attendance.getTimeSheet().getStart().isPresent()) { // check để không lỗi
				ReasonTimeChange reasonTimeChange = new ReasonTimeChange(TimeChangeMeans.valueOf(attendance.getTimeSheet().getStart().get().getReasonTimeChange().getTimeChangeMeans() == null ? null : 
					attendance.getTimeSheet().getStart().get().getReasonTimeChange().getTimeChangeMeans().value), 
						Optional.ofNullable(attendance.getTimeSheet().getStart().get().getReasonTimeChange().getEngravingMethod().isPresent() ? 
								attendance.getTimeSheet().getStart().get().getReasonTimeChange().getEngravingMethod().get() : null));
				Optional<TimeWithDayAttr> timeWithDay = attendance.getTimeSheet().getStart().get().getTimeWithDay().isPresent() ? 
						Optional.of(new TimeWithDayAttr(attendance.getTimeSheet().getStart().get().getTimeWithDay().get().v())) : Optional.empty();
				WorkTimeInformation start = new WorkTimeInformation(reasonTimeChange, timeWithDay.isPresent() ? timeWithDay.get() : null);
				
				ouenWorkTime.getTimeSheet().setEnd(start);
				ouenWorkTime.getTimeSheet().getEnd().get().getReasonTimeChange()
						.setTimeChangeMeans(TimeChangeMeans.AUTOMATIC_SET);
			} else {
				ouenWorkTime.getTimeSheet().setEnd(WorkTimeInformation.createByAutomaticSet(null));
			}
		}
		return ouenWorkTime;
	}

	/**
	 * 応援データを補正して一覧に入れる
	 * 
	 * @param informationWork
	 * @param lstSetSupportData
	 * @param ouenWorkTime
	 */
	public void correctSupportDataPutInList(WorkTemporary informationWork,
			List<OuenWorkTimeSheetOfDailyAttendance> lstSetSupportData,
			OuenWorkTimeSheetOfDailyAttendance ouenWorkTime) {
		this.correctWorkFrameNumber(informationWork, ouenWorkTime);
		ouenWorkTime.setWorkNo(lstSetSupportData.size()+1);
		lstSetSupportData.add(ouenWorkTime);
	}

	/**
	 * 勤務枠Noを補正する
	 * 
	 * @param informationWork
	 * @param ouenWorkTime
	 */
	public void correctWorkFrameNumber(WorkTemporary informationWork, OuenWorkTimeSheetOfDailyAttendance ouenWorkTime) {
		// 勤務Temporary。出勤２時刻のデータがあるか確認する
		if (informationWork.getTwoHoursWork().isPresent()) {
			// Nullじゃない場合
			// 応援データは出勤２時刻以内か確認する - Kiểm tra nếu có start thì dùng start ko thì dùng end để
			// so sánh
			// 出勤２時刻より大きい場合 - larger
			boolean checkTimeLarger = false;
			if (ouenWorkTime.getTimeSheet().getStart().isPresent()) {
				if(ouenWorkTime.getTimeSheet().getStart().get().getTimeWithDay().isPresent() && informationWork
						.getTwoHoursWork().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
					if (ouenWorkTime.getTimeSheet().getStart().get().getTimeWithDay().get().v() > informationWork
							.getTwoHoursWork().get().getStamp().get().getTimeDay().getTimeWithDay().get().v()) {
						checkTimeLarger = true;
					}
				}
				
			} else {
				if (ouenWorkTime.getTimeSheet().getEnd().get().getTimeWithDay().isPresent() && informationWork
						.getTwoHoursWork().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
					if (ouenWorkTime.getTimeSheet().getEnd().get().getTimeWithDay().get().v() > informationWork
							.getTwoHoursWork().get().getStamp().get().getTimeDay().getTimeWithDay().get().v()) {
						checkTimeLarger = true;
					}
				}
			}

			if (checkTimeLarger == true) {
				// 応援データの勤務枠Noをセットする
				ouenWorkTime.getTimeSheet().setWorkNo(new WorkNo(2));
				return;
			}
		}
		ouenWorkTime.getTimeSheet().setWorkNo(new WorkNo(1));
	}

	/**
	 * 最大応援回数を補正する
	 * 
	 * @param judgmentSupport
	 * @param dataAutoSet
	 * @return
	 */
	public List<OuenWorkTimeSheetOfDailyAttendance> correctMaximumCheering(
			JudgmentCriteriaSameStampOfSupport judgmentSupport, List<OuenWorkTimeSheetOfDailyAttendance> dataAutoSet) {
		List<OuenWorkTimeSheetOfDailyAttendance> dataAutoSetNew = new ArrayList<>();
		// パラメータ。応援データ一覧のsizeを確認する
		if (dataAutoSet.size() <= 1 || dataAutoSet.size() <= (judgmentSupport == null ? 0 : judgmentSupport.getSupportMaxFrame().v())) {
			// １以下の場合 - パラメータ。応援データ一覧を返す
			return dataAutoSet;
		} else {
			// ２以上の場合
			// パラメータ。応援データ一覧の末尾の応援データを抜き出す
			OuenWorkTimeSheetOfDailyAttendance lastData = dataAutoSet.get(dataAutoSet.size() - 1);
			// パラメータ。応援データ一覧の先頭の応援データを取得する
			OuenWorkTimeSheetOfDailyAttendance firstData = dataAutoSet.get(0);
			// 最大応援回数で補正する
			dataAutoSetNew = this.correctWithMaxNumberCheers(judgmentSupport != null && judgmentSupport.getSupportMaxFrame() != null 
					? judgmentSupport.getSupportMaxFrame().v() - 1 : 0, dataAutoSet);
			if (judgmentSupport!=null && judgmentSupport.getSupportMaxFrame().v() == 1) {
				// 最後の退勤の応援データを補正する
				if(lastData.getTimeSheet().getStart().isPresent() && lastData.getTimeSheet().getEnd().isPresent()) {
					lastData.getTimeSheet()
					.setStart(firstData.getTimeSheet().getStart().isPresent()
							? firstData.getTimeSheet().getStart().get()
							: null);
					// 最後の退勤の応援データを補正済みの応援データ一覧に入れる
					dataAutoSetNew.add(lastData);
				}
			}
			
			if (judgmentSupport != null && judgmentSupport.getSupportMaxFrame().v() >= 2) {
				// 補正で使う応援データを探す
				Optional<WorkTimeInformation> endOuenLast = dataAutoSetNew.get(dataAutoSetNew.size() - 1).getTimeSheet().getEnd();
				
				if(lastData.getTimeSheet().getStart().get().getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.AUTOMATIC_SET) {
					// 最後の退勤の応援データを補正する
					WorkTimeInformation information = WorkTimeInformation.createByAutomaticSet(endOuenLast.get().getTimeWithDay().isPresent()?endOuenLast.get().getTimeWithDay().get():null);
					lastData.getTimeSheet()
							.setStart(information);
				}

				// 最後の退勤の応援データを補正済みの応援データ一覧の末尾に入れる
					dataAutoSetNew.add(lastData);
			}
		}
		return dataAutoSetNew;
	}
	
	// 補正で使う応援データを探す
	public WorkTimeInformation findSupportCorrection(Optional<WorkTimeInformation> endOuenLast,
			List<OuenWorkTimeSheetOfDailyAttendance> dataAutoSet, StartAtr startAtr) {
		WorkTimeInformation timeInformation = null;
		if(endOuenLast.get().getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.AUTOMATIC_SET) {
			if(startAtr == StartAtr.START_OF_SUPPORT) {
				Optional<OuenWorkTimeSheetOfDailyAttendance> ouenTime = dataAutoSet.stream().filter(x -> {
					val start = x.getTimeSheet().getStart().flatMap(c -> c.getTimeWithDay()).map(c -> c.v()).orElse(null);
					if (start == null || !endOuenLast.get().getTimeWithDay().isPresent()) return false;
					return start.equals(endOuenLast.get().getTimeWithDay().get().v());
				}).findFirst();
				
				if(ouenTime.isPresent()) {
					timeInformation = ouenTime.get().getTimeSheet().getStart().get();
					return timeInformation;
				}
			}
			
			if(startAtr == StartAtr.END_OF_SUPPORT) {
				Optional<OuenWorkTimeSheetOfDailyAttendance> ouenTime = dataAutoSet.stream().filter(x -> {
					val end = x.getTimeSheet().getEnd().flatMap(c -> c.getTimeWithDay()).map(c -> c.v()).orElse(null);
					if (end == null || !endOuenLast.get().getTimeWithDay().isPresent()) return false;
					return end.equals(endOuenLast.get().getTimeWithDay().get().v());
				}).findFirst();
				
				if(ouenTime.isPresent()) {
					timeInformation = ouenTime.get().getTimeSheet().getEnd().get();
					return timeInformation;
				}
			}
		}
		return endOuenLast.isPresent() ? endOuenLast.get() : null;
	}

	/**
	 * 最大応援回数で補正する
	 * 
	 * @param support
	 * @param dataAutoSet
	 * @return
	 */
	public List<OuenWorkTimeSheetOfDailyAttendance> correctWithMaxNumberCheers(Integer support,
			List<OuenWorkTimeSheetOfDailyAttendance> dataAutoSet) {
		// 応援データ一覧のsizeは最大応援回数まで守って、残りの応援データは消す
		List<OuenWorkTimeSheetOfDailyAttendance> dataAutoSetNew = new ArrayList<>();
		if (dataAutoSet.size() > support) {
			for (int i = 0; i < support; i++) {
				dataAutoSetNew.add(dataAutoSet.get(i));
			}
			return dataAutoSetNew;
		}
		return dataAutoSet;
	}

	/**
	 * 編集状態を補正する
	 * 
	 * @param integrationOfDaily
	 * @param lstCorrectMaximum
	 * @param lstOuenBefore
	 */
	public void correctEditStatusSupportItem(IntegrationOfDaily integrationOfDaily,
			List<OuenWorkTimeSheetOfDailyAttendance> lstCorrectMaximum,
			List<OuenWorkTimeSheetOfDailyAttendance> lstOuenBefore) {
		// 応援開始時刻、応援終了時刻以外の編集状態補正する
		Map<Integer, List<ItemValue>> mapItemValue = new HashMap<>();
		this.correctEditStatusOtherCheering(integrationOfDaily, lstCorrectMaximum, lstOuenBefore, mapItemValue);

		// 応援開始時刻、応援終了時刻の編集状態を補正する
		this.correctEditStatusCheeringStarEndYime(integrationOfDaily, lstCorrectMaximum, mapItemValue);
	}

	// 応援開始時刻、応援終了時刻以外の編集状態補正する
	public void correctEditStatusOtherCheering(IntegrationOfDaily integrationOfDaily,
			List<OuenWorkTimeSheetOfDailyAttendance> lstCorrectMaximum,
			List<OuenWorkTimeSheetOfDailyAttendance> lstOuenBefore, Map<Integer, List<ItemValue>> mapItemValue) {

		// パラメータ。日別勤怠（Work）。編集状態から応援時間帯の編集状態一覧を取得する
		List<Integer> lstIdState = AttendanceItemIdContainer.getItemIdByDailyDomains(DailyDomainGroup.SUPPORT_TIMESHEET);
		List<EditStateOfDailyAttd> lstEditState = integrationOfDaily.getEditState().stream()
				.filter(x -> lstIdState.contains(x.getAttendanceItemId())).collect(Collectors.toList());
		List<Integer> lstIdByState = lstEditState.stream().map(mapper -> mapper.getAttendanceItemId()).collect(Collectors.toList());
		// 取得できない
		if (lstEditState.isEmpty()) {
			return;
		}

		// Emptyの補正済みの編集状態一覧を作る - 補正済みの編集状態：日別実績の編集状態＜List＞
		List<EditStateOfDailyAttd> lstEditStated = new ArrayList<>();

		// 取得できた編集状態一覧は応援勤務枠Noでグループする
		Map<Integer, List<EditStateOfDailyAttd>> mapGroupEdits = new HashMap<>();
		List<ItemValue> lstItemValue = AttendanceItemIdContainer.getIds(lstIdByState, AttendanceItemType.DAILY_ITEM);
		mapItemValue = AttendanceItemIdContainer.mapWorkNoItemsValue(lstItemValue);

		mapItemValue.entrySet().stream().forEach(x -> {
			List<Integer> ids = x.getValue().stream().map(i -> i.getItemId()).collect(Collectors.toList());
			List<EditStateOfDailyAttd> lstEditStatefilter = lstEditState.stream()
					.filter(z -> ids.contains(z.getAttendanceItemId())).collect(Collectors.toList());

			mapGroupEdits.put(x.getKey(), lstEditStatefilter);
		});

		// グループした編集状態グループをループする
		mapGroupEdits.entrySet().stream().forEach(x -> {
			// 反映後の応援勤務枠Noを取得する
			int frameNoAfterRelect = this.getSupportWorkFrameNoAfterRelect(x.getKey(), lstOuenBefore,
					lstCorrectMaximum);
			// －１じゃない場合
			if (frameNoAfterRelect != -1) {
				// 反映後の応援勤務枠Noを取得する
				if (frameNoAfterRelect != x.getKey()) {
					// 処理中のグループの編集状態をベースして取得した応援勤務枠Noの項目の編集状態を作る
					List<EditStateOfDailyAttd> dailyAttd = mapGroupEdits.get(x.getKey() + 1);
					// 作った編集状態一覧を補正済みの編集状態一覧に入れる
					lstEditStated.addAll(dailyAttd);
				} else {
					// 処理中のグループの編集状態を補正済みの編集状態一覧に入れる
					lstEditStated.addAll(x.getValue());
				}
			}
		});

		// パラメータ。日別勤怠（Work）。編集状態から応援項目の編集状態を消す
		List<Integer> lstId = lstEditState.stream().map(i -> i.getAttendanceItemId()).collect(Collectors.toList());
		List<EditStateOfDailyAttd> editStateOfDailyAttds = integrationOfDaily.getEditState().stream()
				.filter(del -> !lstId.contains(del.getAttendanceItemId())).collect(Collectors.toList());

		integrationOfDaily.setEditState(editStateOfDailyAttds);
		// パラメータ。日別勤怠（Work）。編集状態に補正済みの編集状態を追加する
		integrationOfDaily.getEditState().addAll(lstEditStated);
	}

	/**
	 * 反映後の応援勤務枠Noを取得する
	 * 
	 * @param workFrameNo
	 * @param lstOuenBefore
	 * @param lstCorrectMaximum
	 * @return
	 */
	public int getSupportWorkFrameNoAfterRelect(int workFrameNo, List<OuenWorkTimeSheetOfDailyAttendance> lstOuenBefore,
			List<OuenWorkTimeSheetOfDailyAttendance> lstCorrectMaximum) {
		// 反映前の応援データを取得する
		Optional<OuenWorkTimeSheetOfDailyAttendance> ouenBeforeNew = lstOuenBefore.stream()
				.filter(x -> x.getWorkNo().v() == workFrameNo).findFirst();
		Optional<OuenWorkTimeSheetOfDailyAttendance> correctMaximumNew = Optional.empty();
		
		// 取得できる応援データの開始データが自動セットかどうか確認する
		if( !ouenBeforeNew.isPresent()) return -1;
		if (ouenBeforeNew.get().getTimeSheet().getStart().isPresent() && ouenBeforeNew.get().getTimeSheet().getStart().get().getReasonTimeChange()

				.getTimeChangeMeans() == TimeChangeMeans.AUTOMATIC_SET) {
			// 自動セットの場合
			// 反映前の応援時刻を取得する
			Optional<WorkTimeInformation> end = ouenBeforeNew.get().getTimeSheet().getEnd();

			correctMaximumNew = lstCorrectMaximum.stream().filter(x -> {
				if(!x.getTimeSheet().getEnd().isPresent() || !end.isPresent()) return false;
				return x.getTimeSheet().getEnd().get().equals(end.get());
			}).findFirst();
		} else {
			Optional<WorkTimeInformation> start = ouenBeforeNew.get().getTimeSheet().getStart();
			
			correctMaximumNew = lstCorrectMaximum.stream().filter(x -> {
				if(!x.getTimeSheet().getStart().isPresent() || !start.isPresent()) return false;
				
				return x.getTimeSheet().getStart().get().equals(start.get());				
			}).findFirst();
		}

		if (correctMaximumNew.isPresent()) {
			// 応援データの応援勤務枠Noを返す
			return ouenBeforeNew.get().getWorkNo().v();
		}
		// －１を返す
		return -1;
	}

	/**
	 * 応援開始時刻、応援終了時刻の編集状態を補正する
	 * 
	 * @param integrationOfDaily
	 * @param lstCorrectMaximum
	 * @param mapItemValue
	 */
	public void correctEditStatusCheeringStarEndYime(IntegrationOfDaily integrationOfDaily,
			List<OuenWorkTimeSheetOfDailyAttendance> lstCorrectMaximum, Map<Integer, List<ItemValue>> mapItemValue) {
		for (OuenWorkTimeSheetOfDailyAttendance attendance : lstCorrectMaximum) {
			// 処理中の応援データの開始の変更手段を確認する
			TimeChangeMeans changeMeansStart = attendance.getTimeSheet().getStart().isPresent() ? 
					attendance.getTimeSheet().getStart().get().getReasonTimeChange().getTimeChangeMeans() : null;

			if ((changeMeansStart == TimeChangeMeans.HAND_CORRECTION_PERSON)
					|| (changeMeansStart == TimeChangeMeans.HAND_CORRECTION_OTHERS)
					|| (changeMeansStart == TimeChangeMeans.APPLICATION)) {
				if (!mapItemValue.values().isEmpty()) {
					// 編集状態追加する
					Optional<ItemValue> itemStart = mapItemValue.get(attendance.getWorkNo()).stream()
							.filter(x -> x.getPathLink().toString().contains(ItemConst.START)).findFirst();
					if(itemStart.isPresent())
					this.addEditStatus(itemStart.get().getItemId(), integrationOfDaily, changeMeansStart);
				}

			}
			TimeChangeMeans changeMeansEnd = attendance.getTimeSheet().getEnd().isPresent()
					? attendance.getTimeSheet().getEnd().get().getReasonTimeChange().getTimeChangeMeans()
					: null; // check để không lỗi
			if ((changeMeansEnd == TimeChangeMeans.HAND_CORRECTION_PERSON)
					|| (changeMeansEnd == TimeChangeMeans.HAND_CORRECTION_OTHERS)
					|| (changeMeansEnd == TimeChangeMeans.APPLICATION)) {
				if (!mapItemValue.values().isEmpty()) {
					// 編集状態追加する
					Optional<ItemValue> itemEnd = mapItemValue.get(attendance.getWorkNo()).stream()
							.filter(x -> x.getPathLink().toString().contains(ItemConst.END)).findFirst();
					if(itemEnd.isPresent())
					this.addEditStatus(itemEnd.get().getItemId(), integrationOfDaily, changeMeansEnd);
				}
			}
		}
	}

	/**
	 * 編集状態追加する
	 * 
	 * @param itemId
	 * @param integrationOfDaily
	 * @param changeMeans
	 */
	public void addEditStatus(int itemId, IntegrationOfDaily integrationOfDaily, TimeChangeMeans changeMeans) {
		EditStateSetting stateSetting = null;
		if (changeMeans == TimeChangeMeans.APPLICATION || changeMeans == TimeChangeMeans.DIRECT_BOUNCE_APPLICATION) {
			stateSetting = EditStateSetting.REFLECT_APPLICATION;
		}
		// 応援開始時刻の編集状態を作る
		EditStateOfDailyAttd attd = new EditStateOfDailyAttd(itemId, stateSetting);
		// パラメータ。日別勤怠（Work）。編集状態に作った編集状態を入れる
		integrationOfDaily.getEditState().add(attd);
	}

	/**
	 * 最初の出勤と最後の退勤を検出する
	 * 
	 * @param attendanceLeave
	 * @return
	 */
	public WorkTemporary detectAttendance(Optional<TimeLeavingOfDailyAttd> attendanceLeave) {
		// Emptyの勤務Temporaryを作成する
		WorkTemporary workTemporary = new WorkTemporary(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

		// 出退勤１を取得する
		Optional<TimeLeavingWork> leavingWork = attendanceLeave.flatMap(c -> c.getAttendanceLeavingWork(1));
		Optional<TimeLeavingWork> leavingWork2 = attendanceLeave.flatMap(c -> c.getAttendanceLeavingWork(2));

		if (leavingWork.isPresent()) {
			// 最初の出勤をセットする - 勤務Temporary。最初の出勤＝取得できる出退勤．出勤
			if(leavingWork.get().getAttendanceStamp().isPresent() && leavingWork.get().getAttendanceStamp().get().getStamp().isPresent())
				workTemporary.setFirstAttendance(leavingWork.get().getAttendanceStamp());

			// 最後の退勤をセットする
			// 勤務Temporary。最後の退勤＝取得できる出退勤．退勤
			if(leavingWork.get().getLeaveStamp().isPresent() && leavingWork.get().getLeaveStamp().get().getStamp().isPresent())
				workTemporary.setLastLeave(leavingWork.get().getLeaveStamp());
		}
		
		if (leavingWork2.isPresent()) {
			// 勤務Temporary。退勤1時刻＝勤務Temporary。最後の退勤
			workTemporary.setOneHourLeavingWork(workTemporary.getLastLeave());
			
			// 勤務Temporary。最後の退勤＝取得できる出退勤．退勤
			if(leavingWork2.get().getLeaveStamp().isPresent() && leavingWork2.get().getLeaveStamp().get().getStamp().isPresent())
				workTemporary.setLastLeave(leavingWork2.get().getLeaveStamp());
			// 勤務Temporary。出勤２時刻＝取得できる出退勤．出勤
			if(leavingWork2.get().getAttendanceStamp().isPresent() && leavingWork2.get().getAttendanceStamp().get().getStamp().isPresent())
				workTemporary.setTwoHoursWork(leavingWork2.get().getAttendanceStamp());
		}

		return workTemporary;
	}

	/**
	 * 最初の出勤の応援データと最後の退勤の応援データを補正する
	 * 
	 * @param lstOuenWorkTime
	 * @param detectAttendance
	 * @param judgmentSupport
	 * @param informationWork
	 * @return
	 */
	public void getSupportFataFirstAttendanceLastDeparture(Require require, String sid, 
			GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWorkTime, WorkTemporary detectAttendance,
			JudgmentCriteriaSameStampOfSupport judgmentSupport, OuenWorkTimeSheetOfDailyAttendance ouenStamp, 
			WorkInformationWork informationWork) {

		/** 最初の出勤を確認する */
		detectAttendance.getFirstAttendance().map(at -> {
			return at.getStamp().map(s -> {
				/** 出退勤の打刻を応援打刻に変換する */
				return this.convertTimeLeaveToOuen(require, sid, ymd, judgmentSupport,
						lstOuenWorkTime, s.getTimeDay(), informationWork, StartAtr.START_OF_SUPPORT);
			});
		}).orElseGet(() -> { 
			/** 最初の応援終了を自動セットする */
			return this.automaticSetFirstSupport(lstOuenWorkTime); 
		}).ifPresent(firstAttendance -> {
			
			/** 応援データ一覧の先頭に作った出退勤応援打刻を入れる */
			lstOuenWorkTime.add(0, firstAttendance);
		});
		

		/** 最後の退勤を確認する */
		detectAttendance.getLastLeave().map(at -> {
			return at.getStamp().map(s -> {
				/** 出退勤の打刻を応援打刻に変換する */
				return this.convertTimeLeaveToOuen(require, sid, ymd, judgmentSupport,
						lstOuenWorkTime, s.getTimeDay(), informationWork, StartAtr.END_OF_SUPPORT);
			});
		}).orElseGet(() -> {
			/** 最後の応援開始を自動セットする */
			return this.automaticSetLastSupport(lstOuenWorkTime);
		}).ifPresent(lastLeave -> {
			
			/** 応援データ一覧の末尾に作った出退勤応援打刻を入れる */
			lstOuenWorkTime.add(lastLeave);
		});
	}

	/**
	 * 打刻応援データに変換する
	 * 
	 * @param informationWork
	 * @param timeDay
	 * @param startAtr
	 * @return
	 */
	public OuenWorkTimeSheetOfDailyAttendance convertStampingSupport(WorkInformationWork informationWork,
			WorkTimeInformation timeDay, StartAtr startAtr, Optional<WorkGroup> workGroup) {
		/** 勤務先情報Workから「日別実績の応援作業別勤怠時間帯」にデータ入れる */
		WorkContent workContent = WorkContent.create(
				WorkplaceOfWorkEachOuen.create(informationWork.getWorkplaceId(),
												informationWork.getLocationCD()), 
				workGroup, Optional.empty());
		
		/** パラメータ。開始区分を確認する */
		if (startAtr == StartAtr.START_OF_SUPPORT) {
			/** 日別実績の応援作業別勤怠時間帯。時間帯。開始＝パラメータ。勤怠打刻 */
			TimeSheetOfAttendanceEachOuenSheet timeSheet = TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(0),
					Optional.ofNullable(timeDay), Optional.empty());
			return OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1), workContent, timeSheet, Optional.empty());
		}
		
		/** 日別実績の応援作業別勤怠時間帯。時間帯。終了＝パラメータ。勤怠打刻 */
		TimeSheetOfAttendanceEachOuenSheet timeSheet = TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(0),
				Optional.empty(), Optional.ofNullable(timeDay));
		return OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1), workContent, timeSheet, Optional.empty());
	}

	/**
	 * 最初の出勤を自動セットする
	 * 
	 * @param lstOuenWorkTime
	 */
	public Optional<OuenWorkTimeSheetOfDailyAttendance> automaticSetFirstSupport(List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWorkTime) {
		/** 応援データ一覧の先頭の応援データを取得する */
		if (lstOuenWorkTime.isEmpty()) {
			return Optional.empty();
		}
		
		/** 開始応援かを確認する */
		if (lstOuenWorkTime.get(0).getTimeSheet().getStart().isPresent()) {

			// 開始の場合
			WorkContent workContent = lstOuenWorkTime.get(0).getWorkContent();
			WorkTimeInformation ouenSpNew = WorkTimeInformation.createByAutomaticSet(lstOuenWorkTime.get(0).getTimeSheet().getStart().get().getTimeWithDay().get());
			TimeSheetOfAttendanceEachOuenSheet timeSheet = TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(0),
					Optional.empty(), Optional.ofNullable(ouenSpNew));
			
			/** 取得した応援データをベースして終了の応援データ作る */
			OuenWorkTimeSheetOfDailyAttendance dailyAttendance = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1),
					workContent, timeSheet, Optional.empty());
			
			/** 作成した応援データを返す */
			return Optional.ofNullable(dailyAttendance);
		}
		
		return Optional.empty();
	}

	/**
	 * 最後の退勤を自動セットする
	 * 
	 * @param lstOuenWorkTime
	 */
	public Optional<OuenWorkTimeSheetOfDailyAttendance> automaticSetLastSupport(List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWorkTime) {
		// 応援データ一覧の末尾の応援データを取得する
		if (lstOuenWorkTime.isEmpty()) {
			return Optional.empty();
		}
		// 終了応援かを確認する
		// 終了の場合
		if (lstOuenWorkTime.get(lstOuenWorkTime.size() - 1).getTimeSheet().getEnd().isPresent()) {
			
			WorkContent workContent = lstOuenWorkTime.get(lstOuenWorkTime.size() - 1).getWorkContent();
			WorkTimeInformation ouenSpNew = WorkTimeInformation.createByAutomaticSet(lstOuenWorkTime.get(lstOuenWorkTime.size() - 1).getTimeSheet().getEnd().get().getTimeWithDay().isPresent() ?
					lstOuenWorkTime.get(lstOuenWorkTime.size() - 1).getTimeSheet().getEnd().get().getTimeWithDay().get() : null);
			TimeSheetOfAttendanceEachOuenSheet timeSheet = TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(0),
					Optional.ofNullable(ouenSpNew), Optional.empty());
			// 取得した応援データをベースして終了の応援データ作る
			OuenWorkTimeSheetOfDailyAttendance dailyAttendance = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1),
					workContent, timeSheet, Optional.empty());
			// 作成した応援データを応援データ一覧の先頭に入れる
			return Optional.ofNullable(dailyAttendance);
		}
		
		return Optional.empty();
	}

	/**
	 * ほかの出退勤を補正する（出勤2と退勤1）
	 * 
	 * @param informationWork
	 * @param workTemporary
	 * @param lstOuenWorkTime
	 * @param judgmentSupport
	 */
	public void correctOtherAttendance(Require require, String sid, GeneralDate ymd, 
			WorkInformationWork informationWork, WorkTemporary workTemporary,
			List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWorkTime,
			JudgmentCriteriaSameStampOfSupport judgmentSupport) {
		
		/** 勤務Temporary。退勤１時刻を確認する */
		workTemporary.getOneHourLeavingWork().flatMap(c -> c.getStamp()).map(c -> c.getTimeDay()).ifPresent(attendance -> {
			
			/** 出退勤の打刻を応援打刻に変換する */
			OuenWorkTimeSheetOfDailyAttendance convertStamp = this.convertTimeLeaveToOuen(require, sid, ymd, judgmentSupport,
					lstOuenWorkTime, attendance, informationWork, StartAtr.END_OF_SUPPORT);
			
			/** 打刻応援データを応援データ一覧にいれる */
			this.getStampedSupportData(true, judgmentSupport, lstOuenWorkTime, convertStamp, StartAtr.END_OF_SUPPORT);
		});
		

		/** 勤務Temporary。出勤２時刻を確認する */
		workTemporary.getOneHourLeavingWork().flatMap(c -> c.getStamp()).map(c -> c.getTimeDay()).ifPresent(attendance -> {
			
			/** 出退勤の打刻を応援打刻に変換する */
			OuenWorkTimeSheetOfDailyAttendance convertStamp = this.convertTimeLeaveToOuen(require, sid, ymd, judgmentSupport,
					lstOuenWorkTime, attendance, informationWork, StartAtr.START_OF_SUPPORT);
			
			/** 打刻応援データを応援データ一覧にいれる */
			this.getStampedSupportData(true, judgmentSupport, lstOuenWorkTime, convertStamp, StartAtr.START_OF_SUPPORT);
		});
	}
	
	/** 出退勤の打刻を応援打刻に変換する */
	private OuenWorkTimeSheetOfDailyAttendance convertTimeLeaveToOuen(Require require, String sid, GeneralDate ymd,
			JudgmentCriteriaSameStampOfSupport judgmentSupport, List<OuenWorkTimeSheetOfDailyAttendance> ouens, 
			WorkTimeInformation timeLeave, WorkInformationWork workInfo, StartAtr startAtr) {
		
		val timeLeaveTime = timeLeave.getTimeWithDay().get(); 
		
		/** 同一と判断された応援データを取得する */
		val sameTimeOuen = GetSupportDataJudgedSameDS.getSupportDataJudgedSame(new GetSupportDataJudgedSameDS.Required() {
			
			@Override
			public JudgmentCriteriaSameStampOfSupport getCriteriaSameStampOfSupport() {
				return judgmentSupport;
			}
		}, ouens, timeLeaveTime, startAtr);
		
		val ouenTime = sameTimeOuen.map(c -> {
			
			/** 検索できた応援データを応援データ一覧から削除する */
			ouens.remove(c);
			
			/** 出退勤打刻の時刻を補正する */
			if (startAtr == StartAtr.START_OF_SUPPORT) {
				c.getTimeSheet().getStart().ifPresent(s -> s.setTimeWithDay(Optional.of(timeLeaveTime)));
			} else {
				c.getTimeSheet().getEnd().ifPresent(s -> s.setTimeWithDay(Optional.of(timeLeaveTime)));
			}
			return c;
		}).orElseGet(() -> {
			
			/** 打刻応援データに変換する */
			return this.convertStampingSupport(workInfo, timeLeave, startAtr, Optional.empty());
		});
		
		/** 打刻で出退勤応援打刻の補正する */
		correctTimeLeaveOuen(require, sid, ymd, startAtr, ouenTime, timeLeaveTime);
		
		/** 出退勤応援打刻を返す */
		return ouenTime;
	}
	
	/** 打刻で出退勤応援打刻の補正する */
	private void correctTimeLeaveOuen(Require require, String sid, GeneralDate ymd, StartAtr startAtr,
			OuenWorkTimeSheetOfDailyAttendance ouen, TimeWithDayAttr timeLeave) {
		
		/** 該当する打刻を取得する */
		val stamp = GetStampByTimeStampService.get(require, sid, ymd, startAtr == StartAtr.START_OF_SUPPORT, timeLeave).orElse(null);
		
		if (stamp == null) return;
		
		/** 取得できた打刻の作業があるかを確認する */
		/** パラメータ。打刻応援の作業を補正する */
		stamp.getRefActualResults().getWorkGroup().ifPresent(wg -> ouen.getWorkContent().setWork(Optional.of(wg)));
		
		/** 取得できた打刻に職場があるかを確認する */
		/** 職場を上書きする */
		stamp.getRefActualResults().getWorkInforStamp().flatMap(c -> c.getWorkplaceID())
			.ifPresent(wp -> ouen.getWorkContent().getWorkplace().setWorkplaceId(new WorkplaceId(wp)));
		
		/** 取得できた打刻に場所があるかを確認する */
		/** 場所を上書きする */
		stamp.getRefActualResults().getWorkInforStamp().flatMap(c -> c.getWorkLocationCD())
			.ifPresent(wl -> ouen.getWorkContent().getWorkplace().setWorkLocationCD(Optional.of(wl)));
	}
	
	public static interface Require extends GetStampByTimeStampService.Require {
		
	}

	/**
	 * 応援データを並びかえる （時刻の昇順）
	 * 
	 * @param lstOuenWorkTime
	 * @return
	 */
	public List<OuenWorkTimeSheetOfDailyAttendance> rearrangeSupportData(
			List<OuenWorkTimeSheetOfDailyAttendance> lstOuenWorkTime) {		
		Comparator<OuenWorkTimeSheetOfDailyAttendance> comparator = new Comparator<OuenWorkTimeSheetOfDailyAttendance>() {

			@Override
			public int compare(OuenWorkTimeSheetOfDailyAttendance arg0, OuenWorkTimeSheetOfDailyAttendance arg1) {
				Optional<TimeWithDayAttr> start1 = arg0.getTimeSheet().getStart().isPresent()
						? arg0.getTimeSheet().getStart().get().getTimeWithDay()
						: Optional.empty();
				Optional<TimeWithDayAttr> end1 = arg0.getTimeSheet().getEnd().isPresent()
						? arg0.getTimeSheet().getEnd().get().getTimeWithDay()
						: Optional.empty();

				Optional<TimeWithDayAttr> start2 = arg1.getTimeSheet().getStart().isPresent()
						? arg1.getTimeSheet().getStart().get().getTimeWithDay()
						: Optional.empty();
				Optional<TimeWithDayAttr> end2 = arg1.getTimeSheet().getEnd().isPresent()
						? arg1.getTimeSheet().getEnd().get().getTimeWithDay()
						: Optional.empty();
				// 両方が終了の場合、終了使う
				if (end1.isPresent() && end2.isPresent()) {
					return end1.get().v().compareTo(end2.get().v());
				}
						
				// 両方が開始の場合開始使う
				if (start1.isPresent() && start2.isPresent()) {
					return start1.get().v().compareTo(start2.get().v());
				}
				// 一方が開始、一方が終了の場合、開始と終了を使う ・同じ時刻の場合、終了は前にする
				if (start1.isPresent() && end2.isPresent()) {
					if (start1.get().v() != end2.get().v())
						return start1.get().v().compareTo(end2.get().v());
					else
						return 1;
				}

				if (start2.isPresent() && end1.isPresent()) {
					if (start2.get().v() != end1.get().v())
						return end1.get().v().compareTo(start2.get().v());
					else
						return -1;
				}
				
				return 0;
			}
		};

		Collections.sort(lstOuenWorkTime, comparator);
		return lstOuenWorkTime;
	}

	/**
	 * 最初の出勤と最後の退勤を補正する
	 * 
	 * @param lstOuenSort
	 * @param suportDataFirtAndLast
	 * @return
	 */
	public List<OuenWorkTimeSheetOfDailyAttendance> compensateFirstLastAttendance(
			List<OuenWorkTimeSheetOfDailyAttendance> lstOuenSort,
			SupportAttendanceDepartureTempo suportDataFirtAndLast) {
		// 出退勤の応援。最初の出勤を確認する
		if (suportDataFirtAndLast.getFirstAttendance().isPresent()) {
			lstOuenSort.add(0, suportDataFirtAndLast.getFirstAttendance().get());
		}
		// 出退勤の応援。最後の退勤を確認する
		if (suportDataFirtAndLast.getLastLeave().isPresent()) {
			lstOuenSort.add(suportDataFirtAndLast.getLastLeave().get());
		}
		return lstOuenSort;
	}
}
