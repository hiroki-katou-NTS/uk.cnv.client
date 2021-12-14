package nts.uk.ctx.at.function.infra.entity.dailyworkschedule;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.dailyworkschedule.AttendanceItemsDisplay;
import nts.uk.ctx.at.function.dom.dailyworkschedule.FontSizeEnum;
import nts.uk.ctx.at.function.dom.dailyworkschedule.NameWorkTypeOrHourZone;
import nts.uk.ctx.at.function.dom.dailyworkschedule.OutputItemDailyWorkScheduleGetMemento;
import nts.uk.ctx.at.function.dom.dailyworkschedule.OutputItemDailyWorkScheduleSetMemento;
import nts.uk.ctx.at.function.dom.dailyworkschedule.OutputItemSettingCode;
import nts.uk.ctx.at.function.dom.dailyworkschedule.OutputItemSettingName;
import nts.uk.ctx.at.function.dom.dailyworkschedule.PrintRemarksContent;
import nts.uk.ctx.at.function.dom.dailyworkschedule.RemarkInputContent;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * The class KfnmtRptWkDaiOutItem
 * 
 * @author LienPTK
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="KFNMT_RPT_WK_DAI_OUT_ITEM")
@EqualsAndHashCode(callSuper = true)
public class KfnmtRptWkDaiOutItem extends UkJpaEntity
		implements Serializable, OutputItemDailyWorkScheduleGetMemento, OutputItemDailyWorkScheduleSetMemento {

	private static final long serialVersionUID = 1L;

	/** The layoutId. */
	@Id
	@Column(name = "LAYOUT_ID")
	private String layoutId;

	/** The itemSelType. */
	@Column(name = "ITEM_SEL_TYPE")
	private int itemSelType;

	/** The cid. */
	@Column(name="CID")
	private String cid;

	/** The sid */
	@Column(name="SID")
	private String sid;

	/** The item code. */
	@Column(name="ITEM_CD")
	private String itemCode;

	/** The item name. */
	@Column(name="ITEM_NAME")
	private String itemName;

	/** The contract cd. */
	@Column(name="CONTRACT_CD")
	private String contractCd;

	/** The work type name display. */
	@Column(name="WORKTYPE_NAME_DISPLAY")
	private BigDecimal workTypeNameDisplay;

	/** The noteInputNo */
	@Column(name="NOTE_INPUT_NO")
	private BigDecimal noteInputNo;

	/** font size */
	@Column(name="CHAR_SIZE_TYPE")
	private BigDecimal charSizeType;
	
	/** The exclus ver. */
	@Version
	@Column(name = "EXCLUS_VER")
	public int exclusVer;

	/** The lst kfnmt rpt wk dai outatds. */
	@OneToMany(cascade=CascadeType.ALL, mappedBy = "kfnmtRptWkDaiOutItem", orphanRemoval = true, fetch = FetchType.LAZY)
	private List<KfnmtRptWkDaiOutatd> lstKfnmtRptWkDaiOutatds;

	/** The lst kfnmt rpt wk dai outnotes. */
	@OneToMany(cascade=CascadeType.ALL, mappedBy = "kfnmtRptWkDaiOutItem", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name="LAYOUT_ID", referencedColumnName="LAYOUT_ID")
	private List<KfnmtRptWkDaiOutnote> lstKfnmtRptWkDaiOutnotes;

	@Override
	protected Object getKey() {
		return this.layoutId;
	}

	@Override
	public void setItemCode(OutputItemSettingCode itemCode) {
		this.itemCode = itemCode.v();
	}

	@Override
	public void setItemName(OutputItemSettingName itemName) {
		this.itemName = itemName.v();
	}

	@Override
	public void setLstDisplayedAttendance(List<AttendanceItemsDisplay> lstDisplayAttendance) {
		this.lstKfnmtRptWkDaiOutatds = lstDisplayAttendance.stream().map(obj -> {
			KfnmtRptWkDaiOutatd entity = new KfnmtRptWkDaiOutatd();
			KfnmtRptWkDaiOutatdPK key = new KfnmtRptWkDaiOutatdPK();
			key.setLayoutId(this.layoutId);
			key.setOrderNo(obj.getOrderNo());
			entity.setId(key);
			entity.setAtdDisplay(new BigDecimal(obj.getAttendanceDisplay()));
			entity.setCid(this.cid);
			entity.setContractCd(this.contractCd);
			return entity;
		}).collect(Collectors.toList());
	}

	@Override
	public void setLstRemarkContent(List<PrintRemarksContent> lstRemarkContent) {
		this.lstKfnmtRptWkDaiOutnotes  =  lstRemarkContent.stream().map(obj -> {
			KfnmtRptWkDaiOutnote entity = new KfnmtRptWkDaiOutnote();
			KfnmtRptWkDaiOutnotePK key = new KfnmtRptWkDaiOutnotePK();
			key.setLayoutId(this.layoutId);
			key.setPrintItem(BigDecimal.valueOf(obj.getPrintItem().value));
			entity.setId(key);
			entity.setCid(this.cid);
			entity.setContractCd(this.contractCd);
			entity.setUseCls(obj.isUsedClassification() ? BigDecimal.ONE : BigDecimal.ZERO);
			return entity;
		}).collect(Collectors.toList());
	}

	@Override
	public void setWorkTypeNameDisplay(NameWorkTypeOrHourZone workTypeNameDisplay) {
		this.workTypeNameDisplay = BigDecimal.valueOf(workTypeNameDisplay.value);
	}

	@Override
	public void setRemarkInputNo(RemarkInputContent remarkInputNo) {
		this.noteInputNo = BigDecimal.valueOf(remarkInputNo.value);
	}

	@Override
	public void setFontSize(FontSizeEnum fontSize) {
		this.charSizeType = BigDecimal.valueOf(fontSize.value);
	}

	@Override
	public OutputItemSettingCode getItemCode() {
		return new OutputItemSettingCode(this.itemCode);
	}

	@Override
	public OutputItemSettingName getItemName() {
		return new OutputItemSettingName(this.itemName);
	}

	@Override
	public List<AttendanceItemsDisplay> getLstDisplayedAttendance() {
		return this.lstKfnmtRptWkDaiOutatds.stream()
			.map(entity -> new AttendanceItemsDisplay((int) entity.getId().getOrderNo(), entity.getAtdDisplay().intValue()))
			.collect(Collectors.toList());
	}

	@Override
	public List<PrintRemarksContent> getLstRemarkContent() {
		return this.lstKfnmtRptWkDaiOutnotes.stream()
			.map(entity -> new PrintRemarksContent(entity.getUseCls().intValue(), entity.getId().getPrintItem().intValue()))
			.collect(Collectors.toList());
	}

	@Override
	public NameWorkTypeOrHourZone getWorkTypeNameDisplay() {
		return NameWorkTypeOrHourZone.valueOf(this.workTypeNameDisplay.intValue());
	}

	@Override
	public RemarkInputContent getRemarkInputNo() {
		return RemarkInputContent.valueOf(this.noteInputNo.intValue());
	}

	@Override
	public FontSizeEnum getFontSize() {
		return FontSizeEnum.valueOf(this.charSizeType.intValue());
	}
}
