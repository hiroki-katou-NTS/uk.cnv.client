package nts.uk.ctx.health.infra.api.entity.emoji.manage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.uk.ctx.health.dom.emoji.manage.EmijiName;
import nts.uk.ctx.health.dom.emoji.manage.EmojiStateDetail;
import nts.uk.ctx.health.dom.emoji.manage.EmojiStateMng;
import nts.uk.ctx.health.dom.emoji.manage.EmojiType;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/*
 * UKDesign.データベース.ER図.ヘルスライフ.感情状態管理.感情状態管理.HHLMT_MOOD_MGT
 * 感情状態管理
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "HHLMT_MOOD_MGT")
public class HhlmtMoodMgt extends UkJpaEntity
		implements EmojiStateMng.MementoGetter, EmojiStateMng.MementoSetter, Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	// column 排他バージョン
	@Column(name = "EXCLUS_VER")
	private long version;

	// column 契約コード
	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	private String contractCd;

	// Embedded primary key 会社ID
	@EmbeddedId
	private HhlmtMoodMgtPK pk;

	// column 管理区分
	@NotNull
	@Column(name = "MGT_ATR")
	private Integer manageEmojiState;

	// column どんより名称
	@NotNull
	@Column(name = "WEARY_MD_NAME")
	private String wearyMoodName;

	// column ゆううつ名称
	@NotNull
	@Column(name = "SAD_MD_NAME")
	private String sadMoodName;

	// column 普通名称
	@NotNull
	@Column(name = "AVERAGE_MD_NAME")
	private String averageMoodName;

	// column ぼちぼち名称
	@NotNull
	@Column(name = "GOOD_MD_NAME")
	private String goodMoodName;

	// column いい感じ名称
	@NotNull
	@Column(name = "HAPPY_MD_NAME")
	private String happyMoodName;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	@Override
	public void setCid(String cid) {
		if (this.pk == null) {
			this.pk = new HhlmtMoodMgtPK();
		}
		this.pk.setCid(cid);
	}

	@Override
	public void setEmojiStateSetting(List<EmojiStateDetail> emojiStateSettings) {
		for (EmojiStateDetail emojiStateSetting : emojiStateSettings) {
			
			switch (emojiStateSetting.getEmojiType()) {
			case WEARY:
				this.wearyMoodName = emojiStateSetting.getEmijiName().v();
				break;
			case SAD:
				this.sadMoodName = emojiStateSetting.getEmijiName().v();
				break;
			case AVERAGE:
				this.averageMoodName = emojiStateSetting.getEmijiName().v();
				break;
			case GOOD:
				this.goodMoodName = emojiStateSetting.getEmijiName().v();
				break;
			case HAPPY:
				this.happyMoodName = emojiStateSetting.getEmijiName().v();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public String getCid() {
		return this.pk.getCid();
	}

	@Override
	public List<EmojiStateDetail> getEmojiStateSettings() {
		
		List<EmojiStateDetail> emojiStateDetails = new ArrayList<>();
		
		EmojiStateDetail weary = EmojiStateDetail.builder()
				.emijiName(new EmijiName(this.wearyMoodName))
				.emojiType(EmojiType.WEARY)
				.build();
		
		EmojiStateDetail sad = EmojiStateDetail.builder()
				.emijiName(new EmijiName(this.sadMoodName))
				.emojiType(EmojiType.SAD)
				.build();
		
		EmojiStateDetail average = EmojiStateDetail.builder()
				.emijiName(new EmijiName(this.averageMoodName))
				.emojiType(EmojiType.AVERAGE)
				.build();
		
		EmojiStateDetail good = EmojiStateDetail.builder()
				.emijiName(new EmijiName(this.goodMoodName))
				.emojiType(EmojiType.GOOD)
				.build();
		
		EmojiStateDetail happy = EmojiStateDetail.builder()
				.emijiName(new EmijiName(this.happyMoodName))
				.emojiType(EmojiType.HAPPY)
				.build();
		
		emojiStateDetails.add(weary);
		emojiStateDetails.add(sad);
		emojiStateDetails.add(average);
		emojiStateDetails.add(good);
		emojiStateDetails.add(happy);
		
		return emojiStateDetails;
	}
	
	@Override
	public Integer getManageEmojiState() {
		return this.manageEmojiState;
	}
	
	@Override
	public void setManageEmojiState(Integer manageEmojiState) {
		this.manageEmojiState = manageEmojiState;
	}
}
