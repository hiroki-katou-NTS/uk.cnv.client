/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.app.insurance.labor.businesstype.command;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.ctx.pr.core.app.insurance.labor.businesstype.InsuranceBusinessTypeUpdateDto;
import nts.uk.ctx.pr.core.dom.insurance.BusinessName;
import nts.uk.ctx.pr.core.dom.insurance.labor.businesstype.BusinessTypeEnum;
import nts.uk.ctx.pr.core.dom.insurance.labor.businesstype.InsuranceBusinessType;
import nts.uk.ctx.pr.core.dom.insurance.labor.businesstype.InsuranceBusinessTypeGetMemento;

@Getter
@Setter
public class InsuranceBusinessTypeUpdateCommand {

	/** The insurance business type. */
	private InsuranceBusinessTypeUpdateDto insuranceBusinessType;

	/**
	 * To domain.
	 *
	 * @return the list
	 */
	public List<InsuranceBusinessType> toDomain(String companyCode) {
		List<InsuranceBusinessType> lsInsuranceBusinessType = new ArrayList<>();
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz1St,
				this.insuranceBusinessType.getBizNameBiz1St(), companyCode));
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz2Nd,
				this.insuranceBusinessType.getBizNameBiz2Nd(), companyCode));
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz3Rd,
				this.insuranceBusinessType.getBizNameBiz3Rd(), companyCode));
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz4Th,
				this.insuranceBusinessType.getBizNameBiz4Th(), companyCode));
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz5Th,
				this.insuranceBusinessType.getBizNameBiz5Th(), companyCode));
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz6Th,
				this.insuranceBusinessType.getBizNameBiz6Th(), companyCode));
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz7Th,
				this.insuranceBusinessType.getBizNameBiz7Th(), companyCode));
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz8Th,
				this.insuranceBusinessType.getBizNameBiz8Th(), companyCode));
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz9Th,
				this.insuranceBusinessType.getBizNameBiz9Th(), companyCode));
		lsInsuranceBusinessType.add(convertInsuranceBusinessType(BusinessTypeEnum.Biz10Th,
				this.insuranceBusinessType.getBizNameBiz10Th(), companyCode));
		return lsInsuranceBusinessType;
	}

	public InsuranceBusinessType convertInsuranceBusinessType(BusinessTypeEnum businessTypeEnum, String bizName,
			String companyCode) {
		InsuranceBusinessTypeUpdateCommand command = this;
		return new InsuranceBusinessType(new InsuranceBusinessTypeGetMemento() {

			@Override
			public CompanyCode getCompanyCode() {
				return new CompanyCode(companyCode);
			}

			@Override
			public BusinessTypeEnum getBizOrder() {
				return businessTypeEnum;
			}

			@Override
			public BusinessName getBizName() {
				return new BusinessName(bizName);
			}
		});
	}
}
