package nts.uk.ctx.sys.portal.dom.toppagealarm;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDateTime;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.ポータル.トップページアラーム（ver4～）.トップページアラーム
 * AR_トップページアラームデータ
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class ToppageAlarmData extends AggregateRoot {

	/**
	 * 会社ID
	 */
	private String cid;
	
	/**
	 * アラーム分類
	 */
	private AlarmClassification alarmClassification;
	
	
	/**
	 * 表示社員ID
	 */
	private String displaySId;
	
	/**
	 * 表示社員区分
	 */
	private DisplayAtr displayAtr;
	
	/**
	 * 解消済である
	 */
	private Boolean isResolved;
	
	/**
	 * 発生日時
	 */
	private GeneralDateTime occurrenceDateTime;
	
	/**
	 * 表示メッセージ
	 */
	private DisplayMessage displayMessage;
	
	
	//部下の社員ID
	private List<String> subSids; //#116503

	/**
	 * リンクURL
	 */
	private Optional<LinkURL> linkUrl;
	
	//既読日時
	private Optional<GeneralDateTime> readDateTime;
	
	//パターンコード
	private Optional<AlarmListPatternCode> patternCode;
	
	//通知ID
	private Optional<NotificationId> notificationId;
	
	/**
	 * [1] 解消済みに状態を変更する																									
	 */
	public void changeResolvedStatus() {
		this.isResolved = true;
	}
	
	/**
	 * [2] 発生日時を更新する
	 */
	public void updateOccurrenceDateTime(GeneralDateTime dateTime) {
		this.isResolved = false;
		this.occurrenceDateTime = dateTime;
		this.readDateTime = Optional.empty();
	}
	
	/*
	 * [3] 部下の社員IDを変更する
	 */
	public void changeSubSids(List<String> newSubSid, List<String> noErrSids) {
		if (this.displayAtr == DisplayAtr.SUPERIOR) {
			this.subSids.addAll(newSubSid);
			this.subSids = this.subSids.stream()
					.filter(sid -> !noErrSids.contains(sid))
					.distinct()
					.collect(Collectors.toList());
		}
	}
	
	/*
	 * [4]上長の場合はエラーが解消済みになるか
	 */
	public boolean isErrorResolved(List<String> noErrSids) {
		if (this.displayAtr != DisplayAtr.SUPERIOR) {
			return false;
		}
		this.subSids = this.subSids.parallelStream()
				.filter(sid -> !noErrSids.contains(sid))
				.distinct()
				.collect(Collectors.toList());
		return this.subSids.isEmpty();
	}
	
	/**
	 * 既読日時を更新する
	 */
	public void updateReadDateTime(GeneralDateTime dateTime) {
		this.readDateTime = Optional.ofNullable(dateTime);
	}
}
