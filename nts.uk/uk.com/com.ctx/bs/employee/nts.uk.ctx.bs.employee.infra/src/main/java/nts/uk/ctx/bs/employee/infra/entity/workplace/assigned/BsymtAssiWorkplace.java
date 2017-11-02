package nts.uk.ctx.bs.employee.infra.entity.workplace.assigned;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;
/**
 * 所属職場履歴
 * 
 * @author xuan vinh
 *
 */
@Getter
@Setter
@Entity
@Table(name = "BSYMT_ASSI_WORKPLACE")
public class BsymtAssiWorkplace extends UkJpaEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private BsymtAssiWorkplacePK bsymtAssiWorkplacePK;
	
	/** The emp id. */
	@Column(name = "SID")
	private String empId;
	
	/** the workplace id*/
	@Column(name = "WORKPLACE_ID")
	private String workplaceId;
	
	@Column(name = "HIST_ID")
	private String histId;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="histId", orphanRemoval = true)
	private List<BsymtAssiWorkplaceHist> lstBsymtAssiWorkplaceHist;
	
	@Override
	protected Object getKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
