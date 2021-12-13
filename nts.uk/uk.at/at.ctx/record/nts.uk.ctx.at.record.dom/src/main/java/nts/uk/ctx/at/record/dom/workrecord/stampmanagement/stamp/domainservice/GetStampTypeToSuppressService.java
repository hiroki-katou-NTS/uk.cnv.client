package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PortalStampSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSettingPerson;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.settingforsmartphone.SettingsSmartphoneStamp;

/**
 * DS : 抑制する打刻種類を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.抑制する打刻種類を取得する
 *
 * @author tutk
 *
 */
@Stateless
public class GetStampTypeToSuppressService {

	/**
	 * [1] 取得する
	 *
	 * @param require
	 * @param employeeId
	 * @param stampMeans
	 * @return
	 */
	public static StampToSuppress get(Require require, String employeeId, StampMeans stampMeans) {
		// if not [prv-1] 打刻ボタンを抑制するか(require, 打刻手段)
		if (!checkOffStampButton(require, stampMeans)) {
			// return 抑制する打刻#[C-1] 全ての打刻を強調しない()
			return StampToSuppress.allStampFalse();
		}
		// $日時期間 = 日時期間#1日範囲を求める(require, 社員ID)
		DateAndTimePeriod dateAndTimePeriod = DateAndTimePeriod.calOneDayRange(require, employeeId);
		// $打刻リスト = [prv-2] 打刻データを取得する(require, 社員ID, 日時期間)
		List<Stamp> listStamp = getDataStamp(require, employeeId, dateAndTimePeriod);
		// return [prv-3] 抑制する打刻を判断する($打刻リスト)
		return judgmentStampToSuppress(listStamp);
	}

	/**
	 * [prv-1] 打刻ボタンを抑制するか
	 *
	 * @param require
	 * @param stampMeans
	 * @return
	 */
	private static boolean checkOffStampButton(Require require, StampMeans stampMeans) {
		// if not 打刻手段.打刻ボタンを抑制する必要か()
		if (!stampMeans.checkIndivition()) {
			return false;
		}
		// if 打刻手段 = 個人打刻
		if (stampMeans.equals(StampMeans.INDIVITION)) {
			// $個人利用の打刻設定 = require.個人利用の打刻設定()
			Optional<StampSettingPerson> optStampSettingPerson = require.getStampSet();
			// if $個人利用の打刻設定.isEmpty
			if (!optStampSettingPerson.isPresent()) {
				return false;
			}
			// return $個人利用の打刻設定.打刻ボタンを抑制する
			return optStampSettingPerson.get().isButtonEmphasisArt();
		}
		// if 打刻手段 = スマホ打刻
		if (stampMeans.equals(StampMeans.SMART_PHONE)) {
			// $スマホ打刻の打刻設定 = require.スマホ打刻の打刻設定()
			Optional<SettingsSmartphoneStamp> optSettingsSmartphoneStamp = require
					.getSettingsSmartphone();
			// if $スマホ打刻の打刻設定.isEmpty

			if (!optSettingsSmartphoneStamp.isPresent()) {
				return false;
			}
			// return $スマホ打刻の打刻設定.打刻ボタンを抑制する
			return optSettingsSmartphoneStamp.get().isButtonEmphasisArt();
		}
		// if 打刻手段 = ポータル打刻
		if (stampMeans.equals(StampMeans.PORTAL)) {
			// $ポータルの打刻設定 = require.ポータルの打刻設定()
			Optional<PortalStampSettings> optPotalSetting = require.getPotalSettings();
			// if $ポータルの打刻設定.isEmpty
			if (!optPotalSetting.isPresent()) {
				return false;
			}
			// return $個人利用の打刻設定.打刻ボタンを抑制する
			return optPotalSetting.get().isButtonEmphasisArt();
		}

		return false;
	}

	/**
	 * [prv-2] 打刻データを取得する
	 *
	 * @param require
	 * @param employeeId
	 * @param dateAndTimePeriod
	 * @return
	 */
	private static List<Stamp> getDataStamp(Require require, String employeeId, DateAndTimePeriod dateAndTimePeriod) {
		List<Stamp> listStamp = new ArrayList<>();

		GeneralDate startDate = GeneralDate.ymd(dateAndTimePeriod.getStatDateTime().year(),
				dateAndTimePeriod.getStatDateTime().month(), dateAndTimePeriod.getStatDateTime().day());
		Optional<StampDataOfEmployees> optStampDataOfEmployeesByStartDate = GetEmpStampDataService.get(require,
				employeeId, startDate);
		if (optStampDataOfEmployeesByStartDate.isPresent()) {
			listStamp.addAll(optStampDataOfEmployeesByStartDate.get().getListStamp().stream()
					.filter(c -> c.getStampDateTime().afterOrEquals(dateAndTimePeriod.getStatDateTime()))
					.collect(Collectors.toList()));
		}
		GeneralDate endDate = GeneralDate.ymd(dateAndTimePeriod.getEndDateTime().year(),
				dateAndTimePeriod.getEndDateTime().month(), dateAndTimePeriod.getEndDateTime().day());
		Optional<StampDataOfEmployees> optStampDataOfEmployeesByEndDate = GetEmpStampDataService.get(require,
				employeeId, endDate);
		if (optStampDataOfEmployeesByEndDate.isPresent()) {
			listStamp.addAll(optStampDataOfEmployeesByEndDate.get().getListStamp().stream()
					.filter(c -> c.getStampDateTime().afterOrEquals(dateAndTimePeriod.getStatDateTime()))
					.collect(Collectors.toList()));
		}
		return listStamp;
	}

	/**
	 * [prv-3] 抑制する打刻を判断する
	 * 
	 * @param listStamp
	 * @return
	 */
	private static StampToSuppress judgmentStampToSuppress(List<Stamp> listStamp) {

		Optional<Stamp> oStamp = listStamp.stream()
				.filter(c -> c.getType().getChangeClockArt() == ChangeClockAtr.GOING_TO_WORK
						|| c.getType().getChangeClockArt() == ChangeClockAtr.GO_OUT
						|| c.getType().getChangeClockArt() == ChangeClockAtr.RETURN
						|| c.getType().getChangeClockArt() == ChangeClockAtr.WORKING_OUT)
				.sorted((x, y) -> y.getStampDateTime().compareTo(x.getStampDateTime()))
				.findFirst();

		if(!oStamp.isPresent()) {
			return StampToSuppress.highlightAttendance();
		}

		Stamp stamp = oStamp.get();

		if(stamp.getType().getChangeClockArt() == ChangeClockAtr.GOING_TO_WORK || stamp.getType().getChangeClockArt() == ChangeClockAtr.RETURN) {
			return new StampToSuppress(true, false, false, true);
		}

		if(stamp.getType().getChangeClockArt() == ChangeClockAtr.GO_OUT) {
			return new StampToSuppress(true, true, true, false);
		}

		return new StampToSuppress(true, true, true, true);
	}

	public static interface Require extends DateAndTimePeriod.Require {

		/**
		 * [R-1] 個人利用の打刻設定
		 *
		 * 個人利用の打刻設定Repository.取得する(会社ID)
		 *
		 * @param companyId
		 * @return
		 */
		Optional<StampSettingPerson> getStampSet();

		/**
		 * [R-2] スマホ打刻の打刻設定
		 *
		 * スマホ打刻の打刻設定Repository.取得する(会社ID)
		 *
		 * @param companyId
		 * @return
		 */
		Optional<SettingsSmartphoneStamp> getSettingsSmartphone();

		/**
		 * [R-3] ポータルの打刻設定
		 *
		 * ポータルの打刻設定Repository.取得する(会社ID)
		 *
		 * @param companyId
		 * @param criteriaDate
		 * @return
		 */
		Optional<PortalStampSettings> getPotalSettings();
	}

}
