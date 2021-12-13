package nts.uk.ctx.sys.assist.infra.entity.categoryfieldmtfordelete;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.assist.dom.categoryfieldmt.CategoryFieldMt;
import nts.uk.ctx.sys.assist.dom.categoryfieldmtfordelete.CategoryFieldMtForDelete;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 *  データ削除カテゴリ項目マスタ
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SSPCT_CTG_FIELD_MT_FORDEL")
public class SspctCategoryFieldMtForDelete extends UkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@EmbeddedId
	public SspctCategoryFieldMtForDeletePk categoryFieldMtPk;

	/**
	 * テーブル日本語名
	 */
	@Basic(optional = false)
	@Column(name = "TABLE_JAPAN_NAME")
	public String tableJapanName;

	/**
	 * テーブル物理名
	 */
	@Basic(optional = false)
	@Column(name = "TABLE_ENGLISH_NAME")
	public String tableEnglishName;

	/**
	 * 削除禁止期間
	 */
	@Basic(optional = true)
	@Column(name = "TIME_STOP_DELETE")
	public String timeStopDelete;

	/**
	 * 抽出キー区分1
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_1")
	public String clsKeyQuery1;

	/**
	 * 抽出キー区分2
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_2")
	public String clsKeyQuery2;

	/**
	 * 抽出キー区分3
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_3")
	public String clsKeyQuery3;

	/**
	 * 抽出キー区分4
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_4")
	public String clsKeyQuery4;

	/**
	 * 抽出キー区分5
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_5")
	public String clsKeyQuery5;

	/**
	 * 抽出キー区分6
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_6")
	public String clsKeyQuery6;

	/**
	 * 抽出キー区分7
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_7")
	public String clsKeyQuery7;

	/**
	 * 抽出キー区分8
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_8")
	public String clsKeyQuery8;

	/**
	 * 抽出キー区分9
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_9")
	public String clsKeyQuery9;

	/**
	 * 抽出キー区分10
	 */
	@Basic(optional = true)
	@Column(name = "CLS_KEY_QUERY_10")
	public String clsKeyQuery10;

	/**
	 * 抽出キー条件固定
	 */
	@Basic(optional = true)
	@Column(name = "DEFAULT_COND_KEY_QUERY")
	public String defaultCondKeyQuery;

	/**
	 * 抽出キー項目1
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_1")
	public String fieldKeyQuery1;

	/**
	 * 抽出キー項目2
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_2")
	public String fieldKeyQuery2;

	/**
	 * 抽出キー項目3
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_3")
	public String fieldKeyQuery3;

	/**
	 * 抽出キー項目4
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_4")
	public String fieldKeyQuery4;

	/**
	 * 抽出キー項目5
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_5")
	public String fieldKeyQuery5;

	/**
	 * 抽出キー項目6
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_6")
	public String fieldKeyQuery6;

	/**
	 * 抽出キー項目7
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_7")
	public String fieldKeyQuery7;

	/**
	 * 抽出キー項目8
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_8")
	public String fieldKeyQuery8;

	/**
	 * 抽出キー項目9
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_9")
	public String fieldKeyQuery9;

	/**
	 * 抽出キー項目10
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_KEY_QUERY_10")
	public String fieldKeyQuery10;

	/**
	 * 日付項目1
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_1")
	public String fieldDate1;

	/**
	 * 日付項目2
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_2")
	public String fieldDate2;

	/**
	 * 日付項目3
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_3")
	public String fieldDate3;

	/**
	 * 日付項目4
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_4")
	public String fieldDate4;

	/**
	 * 日付項目5
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_5")
	public String fieldDate5;

	/**
	 * 日付項目6
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_6")
	public String fieldDate6;

	/**
	 * 日付項目7
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_7")
	public String fieldDate7;

	/**
	 * 日付項目8
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_8")
	public String fieldDate8;

	/**
	 * 日付項目9
	 */
	@Basic(optional = true)
	@Column(name = " FIELD_DATE_9")
	public String fieldDate9;

	/**
	 * 日付項目10
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_10")
	public String fieldDate10;

	/**
	 * 日付項目11
	 */
	@Basic(optional = true)
	@Column(name = " FIELD_DATE_11")
	public String fieldDate11;

	/**
	 * 日付項目12
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_12")
	public String fieldDate12;

	/**
	 * 日付項目13
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_13")
	public String fieldDate13;

	/**
	 * 日付項目14
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_14")
	public String fieldDate14;

	/**
	 * 日付項目15
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_15")
	public String fieldDate15;

	/**
	 * 日付項目16
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_16")
	public String fieldDate16;

	/**
	 * 日付項目17
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_17")
	public String fieldDate17;

	/**
	 * 日付項目18
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_18")
	public String fieldDate18;

	/**
	 * 日付項目19
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_19")
	public String fieldDate19;

	/**
	 * 日付項目20
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_DATE_20")
	public String fieldDate20;

	/**
	 * 更新キー項目1
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_1")
	public String filedKeyUpdate1;

	/**
	 * 更新キー項目2
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_2")
	public String filedKeyUpdate2;

	/**
	 * 更新キー項目3
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_3")
	public String filedKeyUpdate3;

	/**
	 * 更新キー項目4
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_4")
	public String filedKeyUpdate4;

	/**
	 * 更新キー項目5
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_5")
	public String filedKeyUpdate5;

	/**
	 * 更新キー項目6
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_6")
	public String filedKeyUpdate6;

	/**
	 * 更新キー項目7
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_7")
	public String filedKeyUpdate7;

	/**
	 * 更新キー項目8
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_8")
	public String filedKeyUpdate8;

	/**
	 * 更新キー項目9
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_9")
	public String filedKeyUpdate9;

	/**
	 * 更新キー項目10
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_10")
	public String filedKeyUpdate10;

	/**
	 * 更新キー項目11
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_11")
	public String filedKeyUpdate11;

	/**
	 * 更新キー項目12
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_12")
	public String filedKeyUpdate12;

	/**
	 * 更新キー項目13
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_13")
	public String filedKeyUpdate13;

	/**
	 * 更新キー項目14
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_14")
	public String filedKeyUpdate14;

	/**
	 * 更新キー項目15
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_15")
	public String filedKeyUpdate15;

	/**
	 * 更新キー項目16
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_16")
	public String filedKeyUpdate16;

	/**
	 * 更新キー項目17
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_17")
	public String filedKeyUpdate17;

	/**
	 * 更新キー項目18
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_18")
	public String filedKeyUpdate18;

	/**
	 * 更新キー項目19
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_19")
	public String filedKeyUpdate19;

	/**
	 * 更新キー項目20
	 */
	@Basic(optional = true)
	@Column(name = "FILED_KEY_UPDATE_20")
	public String filedKeyUpdate20;

	/**
	 * 履歴区分
	 */
	@Basic(optional = false)
	@Column(name = "HISTORY_CLS")
	public int historyCls;

	/**
	 * 親テーブル日本語名
	 */
	@Basic(optional = true)
	@Column(name = "PARENT_TBL_JP_NAME")
	public String parentTblJpName;

	/**
	 * 親テーブル有無
	 */
	@Basic(optional = true)
	@Column(name = "HAS_PARENT_TBL_FLG")
	public boolean hasParentTblFlg;

	/**
	 * 親テーブル物理名
	 */
	@Basic(optional = true)
	@Column(name = "PARENT_TBL_NAME")
	public String parentTblName;

	/**
	 * 親側結合キー1
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_1")
	public String fieldParent1;

	/**
	 * 親側結合キー2
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_2")
	public String fieldParent2;

	/**
	 * 親側結合キー3
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_3")
	public String fieldParent3;

	/**
	 * 親側結合キー4
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_4")
	public String fieldParent4;

	/**
	 * 親側結合キー5
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_5")
	public String fieldParent5;

	/**
	 * 親側結合キー6
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_6")
	public String fieldParent6;

	/**
	 * 親側結合キー7
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_7")
	public String fieldParent7;

	/**
	 * 親側結合キー8
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_8")
	public String fieldParent8;

	/**
	 * 親側結合キー9
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_9")
	public String fieldParent9;

	/**
	 * 親側結合キー10
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_PARENT_10")
	public String fieldParent10;

	/**
	 * 子側結合キー1
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_1")
	public String fieldChild1;

	/**
	 * 子側結合キー2
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_2")
	public String fieldChild2;

	/**
	 * 子側結合キー3
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_3")
	public String fieldChild3;

	/**
	 * 子側結合キー4
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_4")
	public String fieldChild4;

	/**
	 * 子側結合キー5
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_5")
	public String fieldChild5;

	/**
	 * 子側結合キー6
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_6")
	public String fieldChild6;

	/**
	 * 子側結合キー7
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_7")
	public String fieldChild7;

	/**
	 * 子側結合キー8
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_8")
	public String fieldChild8;

	/**
	 * 子側結合キー9
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_9")
	public String fieldChild9;

	/**
	 * 子側結合キー10
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_CHILD_10")
	public String fieldChild10;

	/**
	 * 付加取得項目_会社ID
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_ACQ_CID")
	public String fieldAcqCid;

	/**
	 * 付加取得項目_日付
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_ACQ_DATE_TIME")
	public String fieldAcqDateTime;

	/**
	 * 付加取得項目_社員ID
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_ACQ_EMPLOYEE_ID")
	public String fieldAcqEmployeeId;

	/**
	 * 付加取得項目_終了日付
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_ACQ_END_DATE")
	public String fieldAcqEndDate;

	/**
	 * 付加取得項目_開始日付
	 */
	@Basic(optional = true)
	@Column(name = "FIELD_ACQ_START_DATE")
	public String fieldAcqStartDate;

	@Override
	protected Object getKey() {
		return categoryFieldMtPk;
	}

	public CategoryFieldMtForDelete toDomain() {
		return new CategoryFieldMtForDelete(this.categoryFieldMtPk.categoryId, this.categoryFieldMtPk.systemType, this.categoryFieldMtPk.tableNo,
				this.tableJapanName, this.tableEnglishName, this.timeStopDelete, this.clsKeyQuery1, this.clsKeyQuery2,
				this.clsKeyQuery3, this.clsKeyQuery4, this.clsKeyQuery5, this.clsKeyQuery6, this.clsKeyQuery7,
				this.clsKeyQuery8, this.clsKeyQuery9, this.clsKeyQuery10, this.defaultCondKeyQuery, this.fieldKeyQuery1,
				this.fieldKeyQuery2, this.fieldKeyQuery3, this.fieldKeyQuery4, this.fieldKeyQuery5, this.fieldKeyQuery6,
				this.fieldKeyQuery7, this.fieldKeyQuery8, this.fieldKeyQuery9, this.fieldKeyQuery10, this.fieldDate1,
				this.fieldDate2, this.fieldDate3, this.fieldDate4, this.fieldDate5, this.fieldDate6, this.fieldDate7,
				this.fieldDate8, this.fieldDate9, this.fieldDate10, this.fieldDate11, this.fieldDate12,
				this.fieldDate13, this.fieldDate14, this.fieldDate15, this.fieldDate16, this.fieldDate17,
				this.fieldDate18, this.fieldDate19, this.fieldDate20, this.filedKeyUpdate1, this.filedKeyUpdate2,
				this.filedKeyUpdate3, this.filedKeyUpdate4, this.filedKeyUpdate5, this.filedKeyUpdate6,
				this.filedKeyUpdate7, this.filedKeyUpdate8, this.filedKeyUpdate9, this.filedKeyUpdate10,
				this.filedKeyUpdate11, this.filedKeyUpdate12, this.filedKeyUpdate13, this.filedKeyUpdate14,
				this.filedKeyUpdate15, this.filedKeyUpdate16, this.filedKeyUpdate17, this.filedKeyUpdate18,
				this.filedKeyUpdate19, this.filedKeyUpdate20, this.historyCls, this.parentTblJpName,
				BooleanUtils.toInteger(this.hasParentTblFlg), this.parentTblName, this.fieldParent1, this.fieldParent2, this.fieldParent3,
				this.fieldParent4, this.fieldParent5, this.fieldParent6, this.fieldParent7, this.fieldParent8,
				this.fieldParent9, this.fieldParent10, this.fieldChild1, this.fieldChild2, this.fieldChild3,
				this.fieldChild4, this.fieldChild5, this.fieldChild6, this.fieldChild7, this.fieldChild8,
				this.fieldChild9, this.fieldChild10, this.fieldAcqCid, this.fieldAcqDateTime, this.fieldAcqEmployeeId,
				this.fieldAcqEndDate, this.fieldAcqStartDate);
	}

	public static SspctCategoryFieldMtForDelete toEntity(CategoryFieldMt domain) {
		return new SspctCategoryFieldMtForDelete(
				new SspctCategoryFieldMtForDeletePk(domain.getCategoryId(), domain.getTableNo(), domain.getSystemType().value),
				domain.getTableJapanName(), domain.getTableEnglishName(),
				domain.getTimeStopDelete(), domain.getClsKeyQuery1(), domain.getClsKeyQuery2(),
				domain.getClsKeyQuery3(), domain.getClsKeyQuery4(), domain.getClsKeyQuery5(), domain.getClsKeyQuery6(),
				domain.getClsKeyQuery7(), domain.getClsKeyQuery8(), domain.getClsKeyQuery9(), domain.getClsKeyQuery10(),
				domain.getDefaultCondKeyQuery(), domain.getFieldKeyQuery1(), domain.getFieldKeyQuery2(),
				domain.getFieldKeyQuery3(), domain.getFieldKeyQuery4(), domain.getFieldKeyQuery5(),
				domain.getFieldKeyQuery6(), domain.getFieldKeyQuery7(), domain.getFieldKeyQuery8(),
				domain.getFieldKeyQuery9(), domain.getFieldKeyQuery10(), domain.getFieldDate1(), domain.getFieldDate2(),
				domain.getFieldDate3(), domain.getFieldDate4(), domain.getFieldDate5(), domain.getFieldDate6(),
				domain.getFieldDate7(), domain.getFieldDate8(), domain.getFieldDate9(), domain.getFieldDate10(),
				domain.getFieldDate11(), domain.getFieldDate12(), domain.getFieldDate13(), domain.getFieldDate14(),
				domain.getFieldDate15(), domain.getFieldDate16(), domain.getFieldDate17(), domain.getFieldDate18(),
				domain.getFieldDate19(), domain.getFieldDate20(), domain.getFiledKeyUpdate1(),
				domain.getFiledKeyUpdate2(), domain.getFiledKeyUpdate3(), domain.getFiledKeyUpdate4(),
				domain.getFiledKeyUpdate5(), domain.getFiledKeyUpdate6(), domain.getFiledKeyUpdate7(),
				domain.getFiledKeyUpdate8(), domain.getFiledKeyUpdate9(), domain.getFiledKeyUpdate10(),
				domain.getFiledKeyUpdate11(), domain.getFiledKeyUpdate12(), domain.getFiledKeyUpdate13(),
				domain.getFiledKeyUpdate14(), domain.getFiledKeyUpdate15(), domain.getFiledKeyUpdate16(),
				domain.getFiledKeyUpdate17(), domain.getFiledKeyUpdate18(), domain.getFiledKeyUpdate19(),
				domain.getFiledKeyUpdate20(), domain.getHistoryCls().value, domain.getParentTblJpName(),
				BooleanUtils.toBoolean(domain.getHasParentTblFlg().value), domain.getParentTblName(), domain.getFieldParent1(),
				domain.getFieldParent2(), domain.getFieldParent3(), domain.getFieldParent4(), domain.getFieldParent5(),
				domain.getFieldParent6(), domain.getFieldParent7(), domain.getFieldParent8(), domain.getFieldParent9(),
				domain.getFieldParent10(), domain.getFieldChild1(), domain.getFieldChild2(), domain.getFieldChild3(),
				domain.getFieldChild4(), domain.getFieldChild5(), domain.getFieldChild6(), domain.getFieldChild7(),
				domain.getFieldChild8(), domain.getFieldChild9(), domain.getFieldChild10(), domain.getFieldAcqCid(),
				domain.getFieldAcqDateTime(), domain.getFieldAcqEmployeeId(), domain.getFieldAcqEndDate(),
				domain.getFieldAcqStartDate());
	}

}
