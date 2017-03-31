/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.app.insurance.social.office.find;

import lombok.Builder;
import nts.uk.ctx.pr.core.dom.insurance.Address1;
import nts.uk.ctx.pr.core.dom.insurance.Address2;
import nts.uk.ctx.pr.core.dom.insurance.AddressKana1;
import nts.uk.ctx.pr.core.dom.insurance.AddressKana2;
import nts.uk.ctx.pr.core.dom.insurance.CityCode;
import nts.uk.ctx.pr.core.dom.insurance.HealthInsuAssoCode;
import nts.uk.ctx.pr.core.dom.insurance.HealthInsuOfficeCode;
import nts.uk.ctx.pr.core.dom.insurance.OfficeCode;
import nts.uk.ctx.pr.core.dom.insurance.OfficeName;
import nts.uk.ctx.pr.core.dom.insurance.OfficePensionFundCode;
import nts.uk.ctx.pr.core.dom.insurance.OfficeRefCode1;
import nts.uk.ctx.pr.core.dom.insurance.OfficeRefCode2;
import nts.uk.ctx.pr.core.dom.insurance.OfficeSign;
import nts.uk.ctx.pr.core.dom.insurance.PhoneNumber;
import nts.uk.ctx.pr.core.dom.insurance.PicName;
import nts.uk.ctx.pr.core.dom.insurance.PicPosition;
import nts.uk.ctx.pr.core.dom.insurance.PotalCode;
import nts.uk.ctx.pr.core.dom.insurance.ShortName;
import nts.uk.ctx.pr.core.dom.insurance.WelfarePensionFundCode;
import nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento;
import nts.uk.shr.com.primitive.Memo;

/**
 * The Class SocialInsuranceOfficeDto.
 */
@Builder
public class SocialInsuranceOfficeDto implements SocialInsuranceOfficeSetMemento {

	/** The company code. */
	public String companyCode;

	/** The code. */
	public String code;

	/** The name. */
	public String name;

	/** The short name. */
	public String shortName;

	/** The pic name. */
	public String picName;

	/** The pic position. */
	public String picPosition;

	/** The potal code. */
	public String potalCode;

	/** The address 1 st. */
	public String address1st;

	/** The address 2 nd. */
	public String address2nd;

	/** The kana address 1 st. */
	public String kanaAddress1st;

	/** The kana address 2 nd. */
	public String kanaAddress2nd;

	/** The phone number. */
	public String phoneNumber;

	/** The health insu office ref code 1 st. */
	public String healthInsuOfficeRefCode1st;

	/** The health insu office ref code 2 nd. */
	public String healthInsuOfficeRefCode2nd;

	/** The pension office ref code 1 st. */
	public String pensionOfficeRefCode1st;

	/** The pension office ref code 2 nd. */
	public String pensionOfficeRefCode2nd;

	/** The welfare pension fund code. */
	public String welfarePensionFundCode;

	/** The office pension fund code. */
	public String officePensionFundCode;

	/** The health insu city code. */
	public String healthInsuCityCode;

	/** The health insu office sign. */
	public String healthInsuOfficeSign;

	/** The pension city code. */
	public String pensionCityCode;

	/** The pension office sign. */
	public String pensionOfficeSign;

	/** The health insu office code. */
	public String healthInsuOfficeCode;

	/** The health insu asso code. */
	public String healthInsuAssoCode;

	/** The memo. */
	public String memo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setCompanyCode(nts.uk.ctx.core.dom.company.CompanyCode)
	 */
	@Override
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setCode(nts.uk.ctx.pr.core.dom.insurance.OfficeCode)
	 */
	@Override
	public void setCode(OfficeCode code) {
		this.code = code.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setName(nts.uk.ctx.pr.core.dom.insurance.OfficeName)
	 */
	@Override
	public void setName(OfficeName name) {
		this.name = name.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setShortName(nts.uk.ctx.pr.core.dom.insurance.ShortName)
	 */
	@Override
	public void setShortName(ShortName shortName) {
		this.shortName = shortName.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setPicName(nts.uk.ctx.pr.core.dom.insurance.PicName)
	 */
	@Override
	public void setPicName(PicName picName) {
		this.picName = picName.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setPicPosition(nts.uk.ctx.pr.core.dom.insurance.PicPosition)
	 */
	@Override
	public void setPicPosition(PicPosition picPosition) {
		this.picPosition = picPosition.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setPotalCode(nts.uk.ctx.pr.core.dom.insurance.PotalCode)
	 */
	@Override
	public void setPotalCode(PotalCode potalCode) {
		this.potalCode = potalCode.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setAddress1st(nts.uk.ctx.pr.core.dom.insurance.Address)
	 */
	@Override
	public void setAddress1st(Address1 address1st) {
		this.address1st = address1st.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setAddress2nd(nts.uk.ctx.pr.core.dom.insurance.Address)
	 */
	@Override
	public void setAddress2nd(Address2 address2nd) {

		this.address2nd = address2nd.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setKanaAddress1st(nts.uk.ctx.pr.core.dom.insurance.KanaAddress)
	 */
	@Override
	public void setKanaAddress1st(AddressKana1 kanaAddress1st) {

		this.kanaAddress1st = kanaAddress1st.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setKanaAddress2nd(nts.uk.ctx.pr.core.dom.insurance.KanaAddress)
	 */
	@Override
	public void setKanaAddress2nd(AddressKana2 kanaAddress2nd) {

		this.kanaAddress2nd = kanaAddress2nd.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setPhoneNumber(java.lang.String)
	 */
	@Override
	public void setPhoneNumber(PhoneNumber phoneNumber) {

		this.phoneNumber = phoneNumber.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setHealthInsuOfficeRefCode1st(java.lang.String)
	 */
	@Override
	public void setHealthInsuOfficeRefCode1st(OfficeRefCode1 healthInsuOfficeRefCode1st) {

		this.healthInsuOfficeRefCode1st = healthInsuOfficeRefCode1st.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setHealthInsuOfficeRefCode2nd(java.lang.String)
	 */
	@Override
	public void setHealthInsuOfficeRefCode2nd(OfficeRefCode2 healthInsuOfficeRefCode2nd) {

		this.healthInsuOfficeRefCode2nd = healthInsuOfficeRefCode2nd.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setPensionOfficeRefCode1st(java.lang.String)
	 */
	@Override
	public void setPensionOfficeRefCode1st(OfficeRefCode1 pensionOfficeRefCode1st) {

		this.pensionOfficeRefCode1st = pensionOfficeRefCode1st.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setPensionOfficeRefCode2nd(java.lang.String)
	 */
	@Override
	public void setPensionOfficeRefCode2nd(OfficeRefCode2 pensionOfficeRefCode2nd) {

		this.pensionOfficeRefCode2nd = pensionOfficeRefCode2nd.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setWelfarePensionFundCode(java.lang.String)
	 */
	@Override
	public void setWelfarePensionFundCode(WelfarePensionFundCode welfarePensionFundCode) {
		if (welfarePensionFundCode == null) {
			this.welfarePensionFundCode = null;
		} else {
			this.welfarePensionFundCode = welfarePensionFundCode.v().toString();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setOfficePensionFundCode(java.lang.String)
	 */
	@Override
	public void setOfficePensionFundCode(OfficePensionFundCode officePensionFundCode) {

		this.officePensionFundCode = officePensionFundCode.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setHealthInsuCityCode(java.lang.String)
	 */
	@Override
	public void setHealthInsuCityCode(CityCode healthInsuCityCode) {

		this.healthInsuCityCode = healthInsuCityCode.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setHealthInsuOfficeSign(java.lang.String)
	 */
	@Override
	public void setHealthInsuOfficeSign(OfficeSign healthInsuOfficeSign) {

		this.healthInsuOfficeSign = healthInsuOfficeSign.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setPensionCityCode(java.lang.String)
	 */
	@Override
	public void setPensionCityCode(CityCode pensionCityCode) {

		this.pensionCityCode = pensionCityCode.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setPensionOfficeSign(java.lang.String)
	 */
	@Override
	public void setPensionOfficeSign(OfficeSign pensionOfficeSign) {

		this.pensionOfficeSign = pensionOfficeSign.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setHealthInsuOfficeCode(java.lang.String)
	 */
	@Override
	public void setHealthInsuOfficeCode(HealthInsuOfficeCode healthInsuOfficeCode) {
		if (healthInsuOfficeCode == null) {
			this.healthInsuOfficeCode = null;
		} else {
			this.healthInsuOfficeCode = healthInsuOfficeCode.v().toString();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setHealthInsuAssoCode(java.lang.String)
	 */
	@Override
	public void setHealthInsuAssoCode(HealthInsuAssoCode healthInsuAssoCode) {

		this.healthInsuAssoCode = healthInsuAssoCode.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.dom.insurance.social.SocialInsuranceOfficeSetMemento#
	 * setMemo(nts.uk.shr.com.primitive.Memo)
	 */
	@Override
	public void setMemo(Memo memo) {

		this.memo = memo.v();
	}

}
