package nts.uk.ctx.at.function.infra.entity.alarm.checkcondition;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author HungTT
 *
 */

@NoArgsConstructor
@Entity
@Table(name = "KFNMT_ALST_CHK_TGT")
public class KfnmtAlarmCheckTargetCondition extends ContractUkJpaEntity implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	public String id;
	
	@Basic
	@Column(name = "FILTER_BY_EMP")
	public boolean filterByEmployment;
	
	@Basic
	@Column(name = "FILTER_BY_CLS")
	public boolean filterByClassification;
	
	@Basic
	@Column(name = "FILTER_BY_JOB")
	public boolean filterByJobTitle;
	
	@Basic
	@Column(name = "FILTER_BY_BUSINESSTYPE")
	public boolean filterByBusinessType;
	
	@OneToOne(mappedBy = "targetCondition")
	public KfnmtAlarmCheckConditionCategory alarmCheckConditionByCategory;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="targetCondition", orphanRemoval = true)
	public List<KfnmtAlarmCheckTargetEmployment> listEmployment;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="targetCondition", orphanRemoval = true)
	public List<KfnmtAlarmCheckTargetClassification> listClassification;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="targetCondition", orphanRemoval = true)
	public List<KfnmtAlarmCheckTargetJobTitle> listJobTitle;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="targetCondition", orphanRemoval = true)
	public List<KfnmtAlarmCheckTargetBusinessType> listBusinessType;
	
	@Override
	protected Object getKey() {
		return this.id;
	}

	public KfnmtAlarmCheckTargetCondition(String id, int filterByEmployment, int filterByClassification,
			int filterByJobTitle, int filterByBusinessType,
			List<KfnmtAlarmCheckTargetEmployment> listEmployment,
			List<KfnmtAlarmCheckTargetClassification> listClassification,
			List<KfnmtAlarmCheckTargetJobTitle> listJobTitle,
			List<KfnmtAlarmCheckTargetBusinessType> listBusinessType) {
		super();
		this.id = id;
		this.filterByEmployment = BooleanUtils.toBoolean(filterByEmployment);
		this.filterByClassification = BooleanUtils.toBoolean(filterByClassification);
		this.filterByJobTitle = BooleanUtils.toBoolean(filterByJobTitle);
		this.filterByBusinessType = BooleanUtils.toBoolean(filterByBusinessType);
		this.listEmployment = listEmployment;
		this.listClassification = listClassification;
		this.listJobTitle = listJobTitle;
		this.listBusinessType = listBusinessType;
	}
	

}
