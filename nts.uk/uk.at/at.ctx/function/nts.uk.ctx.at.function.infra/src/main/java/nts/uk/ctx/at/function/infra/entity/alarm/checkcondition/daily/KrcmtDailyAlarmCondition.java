package nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.daily;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionCode;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.daily.DailyAlarmCondition;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlarmCheckConditionCategory;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "KFNMT_ALST_CHKDAY")
public class KrcmtDailyAlarmCondition extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "DAILY_ALARM_CON_ID")
	public String dailyAlarmConID;

	@Column(name = "CID")
	public String companyId;

	@Column(name = "CATEGORY_CODE")
	public String code;

	@Column(name = "CATEGORY")
	public int category;

	@Column(name = "CON_EXTRACTED_DAILY")
	public int conExtractedDaily;

	@Column(name = "ADD_APPLICATION")
	public boolean addApplication;

	@OneToOne
	@JoinColumns({ @JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false),
			@JoinColumn(name = "CATEGORY", referencedColumnName = "CATEGORY", insertable = false, updatable = false),
			@JoinColumn(name = "CATEGORY_CODE", referencedColumnName = "CD", insertable = false, updatable = false) })
	public KfnmtAlarmCheckConditionCategory condition;

	@OneToMany(mappedBy = "dailyAlarmCondition", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<KrcmtDailyErrorCode> listErrorAlarmCode;

	@OneToMany(mappedBy = "dailyAlarmCondition", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<KrcmtDailyWkRecord> listExtractConditionWorkRecord;

	public KrcmtDailyAlarmCondition(String dailyAlarmConID, String companyId, String code, int category,
			int conExtractedDaily, int addApplication, List<KrcmtDailyErrorCode> dailyErrorCode,
			List<KrcmtDailyWkRecord> dailyWkRecord) {
		super();
		this.dailyAlarmConID = dailyAlarmConID;
		this.companyId = companyId;
		this.code = code;
		this.category = category;
		this.conExtractedDaily = conExtractedDaily;
		this.addApplication = BooleanUtils.toBoolean(addApplication);
		this.listErrorAlarmCode = dailyErrorCode;
//		this.listFixedExtractConditionWorkRecord = dailyFixExtra;
		this.listExtractConditionWorkRecord = dailyWkRecord;
	}

	@Override
	protected Object getKey() {
		return dailyAlarmConID;
	}

	public static KrcmtDailyAlarmCondition toEntity(String companyId, AlarmCheckConditionCode code,
			AlarmCategory category, DailyAlarmCondition domain) {
		return new KrcmtDailyAlarmCondition(domain.getDailyAlarmConID(), companyId, code.v(), category.value,
				domain.getConExtractedDaily().value, domain.isAddApplication() ? 1 : 0,
				KrcmtDailyErrorCode.toEntity(domain.getDailyAlarmConID(), domain.getErrorAlarmCode()),
				KrcmtDailyWkRecord.toEntity(domain.getDailyAlarmConID(), domain.getExtractConditionWorkRecord()));
	}

	public DailyAlarmCondition toDomain() {
		return new DailyAlarmCondition(this.dailyAlarmConID, this.conExtractedDaily,
				this.addApplication,
				this.listExtractConditionWorkRecord.stream().map(c -> c.krcmtDailyWkRecordPK.errorAlarmID).collect(Collectors.toList()),
				this.listErrorAlarmCode.stream().map(c -> c.krcmtDailyErrorCodePK.errorAlarmCode).collect(Collectors.toList()));
	}

}
