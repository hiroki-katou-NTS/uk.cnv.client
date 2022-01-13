package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.LeaveOccurrDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SeqVacationAssociationInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.UnoffsetNumSeqVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 残数確認情報を調整 UKDesign.UniversalK.就業.KDL_ダイアログ.残数確認ダイアログ共通.代休・振休.アルゴリズム.残数確認情報を調整
 * 
 * @author phongtq
 *
 */

@Stateless
public class GetRemainNumberConfirmInfo {
	// List＜逐次休暇の紐付け情報＞, List＜逐次発生の休暇明細一覧＞
	public RemainNumberConfirmInfo getRemainNumberConfirmInfo(List<SeqVacationAssociationInfo> lstSeqVacation,
			VacationDetails vacationDetails, boolean unit) {
		// List＜逐次発生の休暇明細一覧＞

		List<AccumulationAbsenceDetail> lstAcctAbsenDetail = vacationDetails.getLstAcctAbsenDetail();
		// Input．List＜逐次発生の休暇明細＞をソートする
		lstAcctAbsenDetail.sort((a, b) -> {
			if (a.getDateOccur().getDayoffDate().isPresent() && b.getDateOccur().getDayoffDate().isPresent())
				return a.getDateOccur().getDayoffDate().get().compareTo(b.getDateOccur().getDayoffDate().get());

			return 0;
		});
		
		lstAcctAbsenDetail.sort((a, b) -> {
			boolean b1 = a.getDateOccur().isUnknownDate();
			boolean b2 = b.getDateOccur().isUnknownDate();
			return Boolean.compare(b2, b1);
		});

		// List<残数詳細情報>を作成
		List<RemainNumberDetailedInfo> detailedInfos = new ArrayList<>();

		// List<消化日>を作成
		List<String> lstDigestionDate = new ArrayList<>();
		
		// 最初の値をセット
		// 1ヶ月以内期限切れ数 ＝ ０
		String expiredWithinMonth = "0" + (!unit ? TextResource.localize("KDL005_47") : "");

		// 期限の一番近い日 ＝ Empty
		String dayCloseDeadline = "";

		for (AccumulationAbsenceDetail detail : lstAcctAbsenDetail) {
			// 残数詳細情報を作成
			RemainNumberDetailedInfo detailedInfo = new RemainNumberDetailedInfo("", "", "", "", "", "", new ArrayList<>());

			boolean checkDate = false;

			if (detail.getDateOccur().getDayoffDate().isPresent()) {
				List<GeneralDate> listDateUsedSeqVacation = lstSeqVacation.stream().map(c->c.getDateOfUse()).collect(Collectors.toList());
				if(lstDigestionDate.contains(detail.getDateOccur().getDayoffDate().get().toString())
						||(listDateUsedSeqVacation.contains(detail.getDateOccur().getDayoffDate().get()) && !unit)
						) {
					checkDate = true;
				}
			}
			// Trueの場合
			if (detail.getOccurrentClass() == OccurrenceDigClass.DIGESTION && checkDate == true) {
			} else {
				// 以外の場合
				if (detail.getOccurrentClass() == OccurrenceDigClass.DIGESTION) {
					
					DigestionItem digestionItem = new DigestionItem(); 

					// ループ中の逐次発生の休暇明細一覧．年月日．日付不明 ＝＝ True
					if (detail.getDateOccur().isUnknownDate()) {
						// 残数詳細情報．消化日をセットする - 消化日
						digestionItem.setDigestionDate(TextResource.localize("KDL005_52"));
					} else {

						// ループ中の逐次発生の休暇明細一覧．年月日．年月日 ＞ システム日付
						if (detail.getDateOccur().getDayoffDate().get().after(GeneralDate.today())) {
							// 残数詳細情報．消化日状況をセットする - 消化日状況
							digestionItem.setDigestionDateStatus(TextResource.localize("KDL005_40"));
						}
						String textDayoff = this.getDayOfJapan(detail.getDateOccur().getDayoffDate().get().dayOfWeek());
						// 残数詳細情報．消化日をセットする - 消化日
						digestionItem.setDigestionDate(TextResource.localize("KDL005_41",
								detail.getDateOccur().getDayoffDate().get().toString(), // ループ中の逐次発生の休暇明細一覧．年月日．年月日
								textDayoff)); // ループ中の逐次発生の休暇明細一覧．年月日．年月日の曜日
					}
					
					//120400
					//逐次休暇の紐付け情報を絞り込む
					boolean checkExist = false;
					for(SeqVacationAssociationInfo svai :lstSeqVacation) {
						if(!unit && detail.getDateOccur().getDayoffDate().isPresent() && svai.getDateOfUse().equals(detail.getDateOccur().getDayoffDate().get())) {
							checkExist = true;
							//データをセットする
							digestionItem.setDigestionCount(TextResource.localize("KDL005_27", svai.getDayNumberUsed().v().toString()));
							digestionItem.setDigestionDateStatus(TextResource.localize("KDL005_49"));
							break;
						}
					}
					
					if(!checkExist) {
						// ループ中の逐次発生の休暇明細一覧．発生数．時間 ！＝ Empty
						if (unit) {
							// 残数詳細情報．消化数をセットする
							String minu = String.valueOf(detail.getNumberOccurren().getTime().get().minute()).length() > 1
									? String.valueOf(detail.getNumberOccurren().getTime().get().minute())
									: 0 + String.valueOf(detail.getNumberOccurren().getTime().get().minute());
							String hoursMinu = String.valueOf(detail.getNumberOccurren().getTime().get().hour()) + ":"
									+ minu;
							digestionItem.setDigestionCount(hoursMinu);
						} else {
							digestionItem.setDigestionCount(
									TextResource.localize("KDL005_27", detail.getNumberOccurren().getDay().toString())); // ループ中の逐次発生の休暇明細一覧．発生数．日数
						}
					}
					
					detailedInfo.setListDigestion(Arrays.asList(digestionItem));
				} else {// 発生の場合

					// ループ中の逐次発生の休暇明細一覧．年月日．日付不明 ＝＝ True
					if (detail.getDateOccur().isUnknownDate()) {
						// 残数詳細情報．発生日をセットする - 発生日
						detailedInfo.setAccrualDate(TextResource.localize("KDL005_52"));
					} else {

						// ループ中の逐次発生の休暇明細一覧．年月日．年月日 ＞ システム日付
						if (detail.getDateOccur().getDayoffDate().get().after(GeneralDate.today())) {
							// 残数詳細情報．発生日状況をセットする - 発生日状況
							detailedInfo.setOccurrenceDateStatus(TextResource.localize("KDL005_40"));
						}
						
						
						String textDay = this.getDayOfJapan(detail.getDateOccur().getDayoffDate().get().dayOfWeek());
						// 残数詳細情報．発生日をセットする - 発生日
						detailedInfo.setAccrualDate(TextResource.localize("KDL005_41",
								detail.getDateOccur().getDayoffDate().get().toString(), // ループ中の逐次発生の休暇明細一覧．年月日．年月日
								textDay)); // ループ中の逐次発生の休暇明細一覧．年月日．年月日の曜日;
					}
					// ループ中の逐次発生の休暇明細一覧．発生数．時間 ！＝ Empty
					if (unit) {
						// 残数詳細情報．発生数をセットする
						String minu = String.valueOf(detail.getNumberOccurren().getTime().get().minute()).length() > 1
								? String.valueOf(detail.getNumberOccurren().getTime().get().minute())
								: 0 + String.valueOf(detail.getNumberOccurren().getTime().get().minute());
						String hoursMinu = String.valueOf(detail.getNumberOccurren().getTime().get().hour()) + ":"
								+ minu;
						detailedInfo.setNumberOccurrences(hoursMinu);

					} else {
						detailedInfo.setNumberOccurrences(
								TextResource.localize("KDL005_27", detail.getNumberOccurren().getDay().toString())); // ループ中の逐次発生の休暇明細一覧．発生数．日数
					}
					// ループ中の逐次発生の休暇明細．休暇発生明細
					LeaveOccurrDetail occurrDetail = (LeaveOccurrDetail) detail; // get LeaveOccurrDetail extends AccumulationAbsenceDetail
					// 残数詳細情報．消化状況をセットする
					// ループ中の逐次発生の休暇明細一覧．休暇発生明細．消化区分 ＝ 消化済
					if (occurrDetail.getDigestionCate() == DigestionAtr.USED) {
						detailedInfo.setDigestionStatus(TextResource.localize("KDL005_44"));
					}
					
					//逐次発生の休暇明細一覧の消化状態を判断（システム日付）
					DigestionAtr dataDigestionAtr = occurrDetail.judgeDigestiveStatus(GeneralDate.today());
					
					// ループ中の逐次発生の休暇明細一覧．休暇発生明細．消化区分　＝　消滅　＃119869
					if (occurrDetail.getDigestionCate() == DigestionAtr.EXPIRED) {
						
						if(dataDigestionAtr == DigestionAtr.EXPIRED ) {
							detailedInfo.setDigestionStatus(TextResource.localize("KDL005_42"));
						}else { //未消化
							// Input．時間管理区分　＝　False
							String text = detail.getUnbalanceNumber().getDay() + TextResource.localize("KDL005_47");
							// Input．時間管理区分　＝　True
							if (unit) {
								String minu = String.valueOf(detail.getUnbalanceNumber().getTime().get().minute()).length() > 1
										? String.valueOf(detail.getUnbalanceNumber().getTime().get().minute())
										: 0 + String.valueOf(detail.getUnbalanceNumber().getTime().get().minute());
								String hoursMinu = String.valueOf(detail.getUnbalanceNumber().getTime().get().hour()) + ":"
										+ minu;
								text = hoursMinu;
							} 

							detailedInfo.setDigestionStatus(TextResource.localize("KDL005_51", text));
						}
					}

					// ループ中の逐次発生の休暇明細一覧．休暇発生明細．消化区分 ＝ 未消化
					if (occurrDetail.getDigestionCate() == DigestionAtr.UNUSED) {
							// Input．時間管理区分　＝　False
							String text = detail.getUnbalanceNumber().getDay() + TextResource.localize("KDL005_47");
							
							// Input．時間管理区分　＝　True
							if (unit) {
								String minu = String.valueOf(detail.getUnbalanceNumber().getTime().get().minute()).length() > 1
										? String.valueOf(detail.getUnbalanceNumber().getTime().get().minute())
										: 0 + String.valueOf(detail.getUnbalanceNumber().getTime().get().minute());
								String hoursMinu = String.valueOf(detail.getUnbalanceNumber().getTime().get().hour()) + ":"
										+ minu;
								text = hoursMinu;
							} 

							detailedInfo.setDigestionStatus(TextResource.localize("KDL005_51", text));
					}

					// 残数詳細情報．期限日状況をセットする
					LeaveOccurrDetail leaveOccurrDetail = (LeaveOccurrDetail)detail;
					//ループ中の逐次発生の休暇明細一覧．休暇発生明細．消化区分　＝＝　未消化  || 逐次発生の休暇明細一覧の消化状態を判断（システム日付）　＝＝　未消化
					if((leaveOccurrDetail.getDigestionCate() == DigestionAtr.UNUSED || leaveOccurrDetail.judgeDigestiveStatus(GeneralDate.today()) == DigestionAtr.UNUSED)
							&& leaveOccurrDetail.getDeadline().beforeOrEquals(GeneralDate.today().addMonths(1)) //ループ中の逐次発生の休暇明細一覧．休暇発生明細．期限日．年月日 <= システム日付．Add（１Month）の場合
							) {
						detailedInfo.setDueDateStatus(TextResource.localize("KDL005_45"));
					}
					
					if(occurrDetail.getDigestionCate()  == DigestionAtr.UNUSED || dataDigestionAtr == DigestionAtr.UNUSED) { 
						String textDayDe = this.getDayOfJapan(occurrDetail.getDeadline().dayOfWeek());
						// 残数詳細情報．期限日をセットする
						detailedInfo.setDeadline(TextResource.localize("KDL005_41", occurrDetail.getDeadline().toString(), textDayDe));
					}
						
					// 逐次休暇の紐付け情報を絞り込む
					List<SeqVacationAssociationInfo> seqVacationFils = lstSeqVacation.stream()
							.filter(x -> detail.getDateOccur().getDayoffDate().isPresent()
									&& x.getOutbreakDay().equals(detail.getDateOccur().getDayoffDate().get())).collect(Collectors.toList());
					
					List<DigestionItem> listDigestionItem = new ArrayList<>();
					// 残数詳細情報の消化をセットする
					for(SeqVacationAssociationInfo seqVacationFil : seqVacationFils) {
						if(!unit) {
							DigestionItem digestionItem = new DigestionItem();
							// 残数詳細情報．消化日状態
							digestionItem.setDigestionDateStatus(TextResource.localize("KDL005_49"));
							// 残数詳細情報．消化数
							digestionItem.setDigestionCount(
									TextResource.localize("KDL005_27", seqVacationFil.getDayNumberUsed().toString())); // 絞り込んだ逐次休暇の紐付け情報．使用日数
							// 残数詳細情報．消化日
							String textDay = this.getDayOfJapan(seqVacationFil.getDateOfUse().dayOfWeek());
							digestionItem.setDigestionDate(
									TextResource.localize("KDL005_41", seqVacationFil.getDateOfUse().toString(), textDay));
							listDigestionItem.add(digestionItem);
							lstDigestionDate.add(seqVacationFil.getDateOfUse().toString());
						}
					}
					listDigestionItem = listDigestionItem.stream()
							.sorted((a, b) -> a.getDigestionDate().compareTo(b.getDigestionDate()))
							.collect(Collectors.toList());
					detailedInfo.setListDigestion(listDigestionItem);
				}
				// List<残数詳細情報>に作成した残数詳細情報を追加
				detailedInfos.add(detailedInfo);
			}
		}
		// Input．逐次発生の休暇明細一覧．指定した期間内に未消化となる情報を取得するを呼び出す
		DatePeriod dateperiod = new DatePeriod(GeneralDate.today(), GeneralDate.today().addMonths(1));
		List<AccumulationAbsenceDetail> undigestInfoInPeriod = vacationDetails.getUndigestInfoInPeriod(dateperiod);
		VacationDetails vacationDetailsNew = new VacationDetails(undigestInfoInPeriod);
		// 1ヶ月以内期限切れ数をセットする
		UnoffsetNumSeqVacation totalUnoffset = vacationDetailsNew.getTotalUnoffset();
		if (!unit) {
			expiredWithinMonth = totalUnoffset.getDays() + TextResource.localize("KDL005_47");
		} else {
			String minu = String.valueOf(totalUnoffset.getRemainTime().v() % 60).length() > 1 ? String.valueOf(totalUnoffset.getRemainTime().v() % 60)
					: 0 + String.valueOf(totalUnoffset.getRemainTime().v() % 60);
			expiredWithinMonth = String.valueOf(totalUnoffset.getRemainTime().v() / 60) + ":" + minu;
		}

		// 期限の一番近い日をセットする
		List<AccumulationAbsenceDetail> accAbsDetail = vacationDetails.sortAccAbsDetailASC();
		if (!accAbsDetail.isEmpty()) {

			List<AccumulationAbsenceDetail> undigestInfoFils = accAbsDetail.stream().filter(x -> {
				return x.getOccurrentClass() == OccurrenceDigClass.OCCURRENCE && // 逐次発生の休暇明細一覧．発生消化区分 ＝＝ 発生
				(((LeaveOccurrDetail) x).getDigestionCate() == DigestionAtr.UNUSED || // 逐次発生の休暇明細一覧．発生数．消化区分 ＝＝ 未消化 -
				((LeaveOccurrDetail) x).judgeDigestiveStatus(GeneralDate.today()) == DigestionAtr.UNUSED )&& 
				!x.getDateOccur().isUnknownDate(); // 逐次発生の休暇明細一覧．年月日．日付不明 ＝＝ False
			}).collect(Collectors.toList());
			
			
			if (!undigestInfoFils.isEmpty()) {
				List<GeneralDate> listDateLine = undigestInfoFils.stream().map( x->((LeaveOccurrDetail) x).getDeadline() ).collect(Collectors.toList());
				listDateLine = listDateLine.stream().sorted((a, b) -> a.compareTo(b)).collect(Collectors.toList());
				GeneralDate dateMin = listDateLine.get(0);
				
				// 2.取得した逐次発生の休暇明細一覧　があるの場合
				String text = "";
				List<LeaveOccurrDetail> occurrDetailNews = undigestInfoFils.stream().map(c-> (LeaveOccurrDetail)c ).collect(Collectors.toList());
				
				// 2.1.↑の条件で探した逐次発生の休暇明細一覧があるの場合
				String textDay = this.getDayOfJapan(occurrDetailNews.get(0).getDeadline().dayOfWeek());
				text = TextResource.localize("KDL005_41", occurrDetailNews.get(0).getDeadline().toString(), textDay);
				// Input．時間管理区分　＝　Trueの場合　＃119874
				if (unit) {
					Integer time = 0;
					// 探した逐次発生の休暇明細一覧．発生数．時間数 - 探した逐次発生の休暇明細一覧．未相殺数．時間数
					for (LeaveOccurrDetail x : occurrDetailNews) {
						if(x.getDeadline().equals(dateMin)) {
							time = time + x.getUnbalanceNumber().getTime().get().v();
						}
					}

					String minu = String.valueOf(time % 60).length() > 1 ? String.valueOf(time % 60)
							: 0 + String.valueOf(time % 60);
					text = text + " " + String.valueOf(time / 60) + ":" + minu;
					// 探した逐次発生の休暇明細一覧．発生数．日数 - 探した逐次発生の休暇明細一覧．未相殺数．日数
					dayCloseDeadline = text;
				} else {
					double day = 0.0;
					for (LeaveOccurrDetail x : occurrDetailNews) {
						if(x.getDeadline().equals(dateMin)) {
							day = day + x.getUnbalanceNumber().getDay().v();
						}
					}
					dayCloseDeadline = text + " " + day + TextResource.localize("KDL005_47");
				}
			}
		}

		RemainNumberConfirmInfo confirmInfo = new RemainNumberConfirmInfo(detailedInfos, expiredWithinMonth,
				dayCloseDeadline);
		return confirmInfo;
	}
	
	public String getDayOfJapan(int day) {
		switch (day) {
		case 1:
			return "月";
		case 2:
			return "火";
		case 3:
			return "水";
		case 4:
			return "木";
		case 5:
			return "金";
		case 6:
			return "土";
		default:
			return "日";
		}
	}
}
