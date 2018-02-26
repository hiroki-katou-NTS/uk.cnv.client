/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.infra.entity.employee.order;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;


/**
 * The Class BsymtEmployeeOrder.
 */
@Entity
@Table(name="BSYMT_EMPLOYEE_ORDER")
@Getter
@Setter
public class BsymtEmployeeOrder extends UkJpaEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@EmbeddedId
	private BsymtEmployeeOrderPK id;

	/** The name. */
	@Column(name="NAME")
	private String name;

	/** The lst bsymt emp order cond. */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false),
			@JoinColumn(name = "NO", referencedColumnName = "NO", insertable = false, updatable = false), 
			@JoinColumn(name = "SEARCH_TYPE", referencedColumnName = "SEARCH_TYPE", insertable = false, updatable = false) 
	})
	private List<BsymtEmpOrderCond> lstBsymtEmpOrderCond;

	/**
	 * Instantiates a new bsymt employee order.
	 *
	 * @param id the id
	 */
	public BsymtEmployeeOrder(BsymtEmployeeOrderPK id) {
		this.id = id;
	}

	/**
	 * Instantiates a new bsymt employee order.
	 */
	public BsymtEmployeeOrder() {
	}
	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.id;
	}

}