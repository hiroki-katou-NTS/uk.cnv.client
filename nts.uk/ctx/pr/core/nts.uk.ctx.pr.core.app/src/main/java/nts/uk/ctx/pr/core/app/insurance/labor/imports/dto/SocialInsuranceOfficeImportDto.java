/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.app.insurance.labor.imports.dto;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.pr.core.dom.insurance.Address1;
import nts.uk.ctx.pr.core.dom.insurance.Address2;
import nts.uk.ctx.pr.core.dom.insurance.AddressKana1;
import nts.uk.ctx.pr.core.dom.insurance.AddressKana2;
import nts.uk.ctx.pr.core.dom.insurance.CitySign;
import nts.uk.ctx.pr.core.dom.insurance.OfficeCode;
import nts.uk.ctx.pr.core.dom.insurance.OfficeMark;
import nts.uk.ctx.pr.core.dom.insurance.OfficeName;
import nts.uk.ctx.pr.core.dom.insurance.OfficeNoA;
import nts.uk.ctx.pr.core.dom.insurance.OfficeNoB;
import nts.uk.ctx.pr.core.dom.insurance.OfficeNoC;
import nts.uk.ctx.pr.core.dom.insurance.PhoneNumber;
import nts.uk.ctx.pr.core.dom.insurance.PicName;
import nts.uk.ctx.pr.core.dom.insurance.PicPosition;
import nts.uk.ctx.pr.core.dom.insurance.PotalCode;
import nts.uk.ctx.pr.core.dom.insurance.ShortName;
import nts.uk.ctx.pr.core.dom.insurance.labor.LaborInsuranceOffice;
import nts.uk.ctx.pr.core.dom.insurance.labor.LaborInsuranceOfficeGetMemento;
import nts.uk.shr.com.primitive.Memo;

/**
 * The Class SocialInsuranceOfficeImportDto.
 */
@Getter
@Setter
public class SocialInsuranceOfficeImportDto {

	/** The code. */
	private String code;

	/** The name. */
	private String name;

	/** The short name. */
	private String shortName;

	/** The pic name. */
	private String picName;

	/** The pic position. */
	private String picPosition;

	/** The potal code. */
	private String potalCode;

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

	/** The health insu office ref code 1 st. */
	private String healthInsuOfficeRefCode1st;

	/** The health insu office ref code 2 nd. */
	private String healthInsuOfficeRefCode2nd;

	/** The pension office ref code 1 st. */
	private String pensionOfficeRefCode1st;

	/** The pension office ref code 2 nd. */
	private String pensionOfficeRefCode2nd;

	/** The welfare pension fund code. */
	private String welfarePensionFundCode;

	/** The office pension fund code. */
	private String officePensionFundCode;

	/** The health insu city code. */
	private String healthInsuCityCode;

	/** The health insu office sign. */
	private String healthInsuOfficeSign;

	/** The pension city code. */
	private String pensionCityCode;

	/** The pension office sign. */
	private String pensionOfficeSign;

	/** The health insu office code. */
	private String healthInsuOfficeCode;

	/** The health insu asso code. */
	private String healthInsuAssoCode;

	/** The memo. */
	private String memo;

	/**
	 * To domain.
	 *
	 * @param companyCode
	 *            the company code
	 * @return the labor insurance office
	 */
	public LaborInsuranceOffice toDomain(String companyCode) {
		SocialInsuranceOfficeImportDto dto = this;
		return new LaborInsuranceOffice(new LaborInsuranceOfficeGetMemento() {

			@Override
			public ShortName getShortName() {
				return new ShortName(dto.shortName);
			}

			@Override
			public PotalCode getPotalCode() {
				return new PotalCode(dto.potalCode);
			}

			@Override
			public PicPosition getPicPosition() {
				return new PicPosition(dto.picPosition);
			}

			@Override
			public PicName getPicName() {
				return new PicName(dto.picName);
			}

			@Override
			public PhoneNumber getPhoneNumber() {
				return new PhoneNumber(dto.phoneNumber);
			}

			@Override
			public OfficeNoC getOfficeNoC() {
				return new OfficeNoC("");
			}

			@Override
			public OfficeNoB getOfficeNoB() {
				return new OfficeNoB("");
			}

			@Override
			public OfficeNoA getOfficeNoA() {
				return new OfficeNoA("");
			}

			@Override
			public OfficeMark getOfficeMark() {
				return new OfficeMark(dto.healthInsuAssoCode);
			}

			@Override
			public OfficeName getName() {
				return new OfficeName(dto.name);
			}

			@Override
			public Memo getMemo() {
				return new Memo(dto.memo);
			}

			@Override
			public AddressKana2 getKanaAddress2nd() {
				return new AddressKana2(dto.kanaAddress2nd);
			}

			@Override
			public AddressKana1 getKanaAddress1st() {
				return new AddressKana1(dto.kanaAddress1st);
			}

			@Override
			public String getCompanyCode() {
				return companyCode;
			}

			@Override
			public OfficeCode getCode() {
				return new OfficeCode(dto.code);
			}

			@Override
			public CitySign getCitySign() {
				return new CitySign(dto.healthInsuCityCode);
			}

			@Override
			public Address2 getAddress2nd() {
				return new Address2(dto.address2nd);
			}

			@Override
			public Address1 getAddress1st() {
				return new Address1(dto.address1st);
			}
		});
	}
}
