package nts.uk.ctx.at.function.infra.entity.attendancerecord.export.setting;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExport;
import nts.uk.ctx.at.function.dom.attendancerecord.export.ExportAtr;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.AttendanceRecordExportSettingGetMemento;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.AttendanceRecordExportSettingSetMemento;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.ExportFontSize;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.ExportSettingCode;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.ExportSettingName;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.MonthlyConfirmedDisplay;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.SealColumnName;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.KfnmtRptWkAtdOutframe;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.KfnmtRptWkAtdOutseal;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.item.KfnmtRptWkAtdOutatd;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@Data
@Entity
@Table(name = "KFNMT_RPT_WK_ATD_OUT")
@EqualsAndHashCode(callSuper = true)
public class KfnmtRptWkAtdOut extends UkJpaEntity
		implements Serializable, AttendanceRecordExportSettingGetMemento, AttendanceRecordExportSettingSetMemento {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@Column(name = "LAYOUT_ID")
	@Basic(optional = false)
	private String layoutId;

	@Basic(optional = false)
	@Column(name = "EXCLUS_VER")
	public int exclusVer;

	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	public String contractCd;

	@Basic(optional = false)
	@Column(name = "ITEM_SEL_TYPE")
	private int itemSelType;

	@Basic(optional = false)
	@Column(name = "CID")
	private String cid;

	@Column(name = "SID")
	private String sid;

	@Basic(optional = false)
	@Column(name = "EXPORT_CD")
	private String exportCD;

	@Basic(optional = false)
	@Column(name = "NAME")
	private String name;

	@Basic(optional = false)
	@Column(name = "SEAL_USE_ATR")
	private BigDecimal sealUseAtr;

	@Basic(optional = false)
	@Column(name = "NAME_USE_ATR")
	private BigDecimal nameUseAtr;

	@Basic(optional = false)
	@Column(name = "CHAR_SIZE_TYPE")
	private BigDecimal charSizeType;

	@Basic(optional = false)
	@Column(name = "MONTH_APP_DISP_ATR")
	private BigDecimal monthAppDispAtr;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "LAYOUT_ID", referencedColumnName = "LAYOUT_ID")
	private List<KfnmtRptWkAtdOutatd> lstKfnmtRptWkAtdOutatd;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "LAYOUT_ID", referencedColumnName = "LAYOUT_ID")
	private List<KfnmtRptWkAtdOutseal> lstKfnmtRptWkAtdOutseal;

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.layoutId;
	}

	@Override
	public void setDailyExportItem(List<AttendanceRecordExport> attendanceList) {
		this.lstKfnmtRptWkAtdOutatd = attendanceList.stream()
				.filter(r -> r.getExportAtr() == ExportAtr.DAILY)
				.map(t -> {
					KfnmtRptWkAtdOutatd kfnmtRptWkAtdOut = new KfnmtRptWkAtdOutatd();
					kfnmtRptWkAtdOut.setCid(this.cid);
					kfnmtRptWkAtdOut.setLayoutId(this.layoutId);
					kfnmtRptWkAtdOut.setColumnIndex(t.getColumnIndex());
					return kfnmtRptWkAtdOut;
				}).collect(Collectors.toList());
	}

	@Override
	public void setMonthlyExportItem(List<AttendanceRecordExport> attendanceList) {
		this.lstKfnmtRptWkAtdOutatd = attendanceList.stream()
				.filter(r -> r.getExportAtr() == ExportAtr.MONTHLY)
				.map(t -> {
					KfnmtRptWkAtdOutatd kfnmtRptWkAtdOut = new KfnmtRptWkAtdOutatd();
					// TODO
					return kfnmtRptWkAtdOut;
				}).collect(Collectors.toList());
	}

	@Override
	public void setSealUseAtr(Boolean atr) {
		this.sealUseAtr = atr ? BigDecimal.ONE : BigDecimal.ZERO;
	}

	@Override
	public void setCode(ExportSettingCode code) {
		this.exportCD = code.v();
	}

	@Override
	public void setName(ExportSettingName name) {
		this.name = name.v();
	}

	@Override
	public void setSealStamp(List<SealColumnName> seals) {
		this.lstKfnmtRptWkAtdOutseal = new ArrayList<>();
		if (seals != null) {
			int order = 1;
			for (SealColumnName seal : seals) {
				UUID columnId = UUID.randomUUID();
				this.lstKfnmtRptWkAtdOutseal.add(new KfnmtRptWkAtdOutseal(columnId.toString(),
						AppContexts.user().contractCode(),
						this.cid,
						this.layoutId,
						seal.v(),
						new BigDecimal(order++)));
			}
		}
	}

	@Override
	public void setNameUseAtr(Integer nameUseAtr) {
		this.nameUseAtr = BigDecimal.valueOf(nameUseAtr);
	}

	@Override
	public void setExportFontSize(ExportFontSize exportFontSize) {
		this.charSizeType = BigDecimal.valueOf(exportFontSize.value);
	}

	@Override
	public void setMonthlyConfirmedDisplay(MonthlyConfirmedDisplay monthlyConfirmedDisplay) {
		this.monthAppDispAtr = BigDecimal.valueOf(monthlyConfirmedDisplay.value);
	}

	@Override
	public List<AttendanceRecordExport> getDailyExportItem() {
		return this.lstKfnmtRptWkAtdOutatd != null
			 ? this.lstKfnmtRptWkAtdOutatd.stream()
				.filter(r -> r.getOutputAtr() == ExportAtr.DAILY.value)
				.map(t -> {
					AttendanceRecordExport atd = new AttendanceRecordExport();
					// TODO
					return atd;
				}).collect(Collectors.toList())
			 : new ArrayList<>();
	}

	@Override
	public List<AttendanceRecordExport> getMonthlyExportItem() {
		return this.lstKfnmtRptWkAtdOutatd != null
				 ? this.lstKfnmtRptWkAtdOutatd.stream()
					.filter(r -> r.getOutputAtr() == ExportAtr.MONTHLY.value)
					.map(t -> {
						AttendanceRecordExport atd = new AttendanceRecordExport();
						// TODO
						return atd;
					}).collect(Collectors.toList())
				 : new ArrayList<>();
	}

	@Override
	public Boolean getSealUseAtr() {
		return this.itemSelType == 1;
	}

	@Override
	public ExportSettingCode getCode() {
		return new ExportSettingCode(this.exportCD);
	}

	@Override
	public ExportSettingName getName() {
		return new ExportSettingName(this.name);
	}

	@Override
	public List<SealColumnName> getSealStamp() {
		return this.lstKfnmtRptWkAtdOutseal.stream().map(t -> {
			SealColumnName sealColumnName = new SealColumnName(t.getSealStampName());
			return sealColumnName;
		}).collect(Collectors.toList());
	}

	@Override
	public Integer getNameUseAtr() {
		return this.nameUseAtr.intValue();
	}

	@Override
	public ExportFontSize getExportFontSize() {
		return ExportFontSize.valueOf(this.charSizeType.intValue());
	}

	@Override
	public MonthlyConfirmedDisplay getMonthlyConfirmedDisplay() {
		return MonthlyConfirmedDisplay.valueOf(this.monthAppDispAtr.intValue());
	}
}