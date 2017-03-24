/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.app.wagetable.certification.command.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.ctx.pr.core.dom.wagetable.certification.Certification;
import nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroup;
import nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroupCode;
import nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroupGetMemento;
import nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroupName;
import nts.uk.ctx.pr.core.dom.wagetable.certification.MultipleTargetSetting;

/**
 * The Class CertifyGroupDto.
 */

/**
 * Instantiates a new certify group dto.
 */
@Data
public class CertifyGroupDto {

	/** The code. */
	private String code;

	/** The name. */
	private String name;

	/** The multi apply set. */
	private Integer multiApplySet;

	/** The certifies. */
	private List<CertificationDto> certifies;

	/**
	 * To domain.
	 *
	 * @param companyCode
	 *            the company code
	 * @return the certify group
	 */
	public CertifyGroup toDomain(String companyCode) {
		return new CertifyGroup(new CertifyGroupGetMementoImpl(companyCode, this));
	}

	/**
	 * The Class CertifyGroupGetMementoImpl.
	 */
	public class CertifyGroupGetMementoImpl implements CertifyGroupGetMemento {

		/** The company code. */
		private String companyCode;

		/** The dto. */
		private CertifyGroupDto dto;

		/**
		 * Instantiates a new certify group get memento impl.
		 *
		 * @param companyCode
		 *            the company code
		 * @param dto
		 *            the dto
		 */
		public CertifyGroupGetMementoImpl(String companyCode, CertifyGroupDto dto) {
			super();
			this.companyCode = companyCode;
			this.dto = dto;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroupGetMemento
		 * #getName()
		 */
		@Override
		public CertifyGroupName getName() {
			return new CertifyGroupName(dto.name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroupGetMemento
		 * #getMultiApplySet()
		 */
		@Override
		public MultipleTargetSetting getMultiApplySet() {
			return MultipleTargetSetting.valueOf(dto.multiApplySet);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroupGetMemento
		 * #getCompanyCode()
		 */
		@Override
		public CompanyCode getCompanyCode() {
			return new CompanyCode(companyCode);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroupGetMemento
		 * #getCode()
		 */
		@Override
		public CertifyGroupCode getCode() {
			return new CertifyGroupCode(dto.code);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * nts.uk.ctx.pr.core.dom.wagetable.certification.CertifyGroupGetMemento
		 * #getCertifies()
		 */
		@Override
		public Set<Certification> getCertifies() {
			Set<Certification> setCertification = new HashSet<>();
			for (CertificationDto certificationDto : dto.certifies) {
				setCertification.add(certificationDto.toDomain(companyCode));
			}
			return setCertification;
		}
	}

}
