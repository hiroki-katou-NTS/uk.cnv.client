/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.app.insurance.labor.find;

import lombok.Data;
import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.ctx.pr.core.dom.insurance.Address;
import nts.uk.ctx.pr.core.dom.insurance.KanaAddress;
import nts.uk.ctx.pr.core.dom.insurance.OfficeCode;
import nts.uk.ctx.pr.core.dom.insurance.OfficeName;
import nts.uk.ctx.pr.core.dom.insurance.PicName;
import nts.uk.ctx.pr.core.dom.insurance.PicPosition;
import nts.uk.ctx.pr.core.dom.insurance.PotalCode;
import nts.uk.ctx.pr.core.dom.insurance.ShortName;
import nts.uk.ctx.pr.core.dom.insurance.labor.LaborInsuranceOfficeSetMemento;
import nts.uk.shr.com.primitive.Memo;

/**
 * The Class LaborInsuranceOfficeDto.
 */
@Data
public class LaborInsuranceOfficeDto implements LaborInsuranceOfficeSetMemento {
	/** The code. officeCode */
	private String code;
	/** The name. officeName */
	private String name;
	/** The short name. */
	private String shortName;
	/** The pic name. */
	private String picName;
	/** The pic position. */
	private String picPosition;
	/** The potal code. */
	private String potalCode;
	/** The prefecture. */
	private String prefecture;
	/** The address 1 st. */
	private String address1st;
	/** The address 2 nd. */
	private String address2nd;
	/** The kana address 1 st. */
	private String kanaAddress1st;
	/** The kana address 2 nd. */
	private String kanaAddress2nd;
	/** The phone number. */
	private String phoneNumber;
	/** The city sign. */
	private String citySign;
	/** The office mark. */
	private String officeMark;
	/** The office no A. */
	private String officeNoA;
	/** The office no B. */
	private String officeNoB;
	/** The office no C. */
	private String officeNoC;
	/** The memo. */
	private String memo;

	/** The version. */
	private long version;

	@Override
	public void setCompanyCode(CompanyCode companyCode) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCode(OfficeCode code) {
		// TODO Auto-generated method stub
		this.code = code.v();

	}

	@Override
	public void setName(OfficeName name) {
		// TODO Auto-generated method stub
		this.name = name.v();

	}

	@Override
	public void setShortName(ShortName shortName) {
		// TODO Auto-generated method stub
		this.shortName = shortName.v();
	}

	@Override
	public void setPicName(PicName picName) {
		// TODO Auto-generated method stub
		this.picName = picName.v();

	}

	@Override
	public void setPicPosition(PicPosition picPosition) {
		// TODO Auto-generated method stub
		this.picPosition = picPosition.v();
	}

	@Override
	public void setPotalCode(PotalCode potalCode) {
		// TODO Auto-generated method stub
		this.potalCode = potalCode.v();

	}

	@Override
	public void setAddress1st(Address address1st) {
		// TODO Auto-generated method stub
		this.address1st = address1st.v();

	}

	@Override
	public void setAddress2nd(Address address2nd) {
		// TODO Auto-generated method stub
		this.address2nd = address2nd.v();
	}

	@Override
	public void setKanaAddress1st(KanaAddress kanaAddress1st) {
		// TODO Auto-generated method stub
		this.kanaAddress1st = kanaAddress1st.v();

	}

	@Override
	public void setKanaAddress2nd(KanaAddress kanaAddress2nd) {
		// TODO Auto-generated method stub
		this.kanaAddress2nd = kanaAddress2nd.v();
	}

	@Override
	public void setMemo(Memo memo) {
		// TODO Auto-generated method stub
		this.memo = memo.v();

	}

	@Override
	public void setVersion(Long version) {
		// TODO Auto-generated method stub
		this.version = version;
	}
}
