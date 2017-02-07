/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.ws.insurance.social;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.pr.core.app.command.insurance.social.DeleteSocialOfficeCommand;
import nts.uk.ctx.pr.core.app.command.insurance.social.DeleteSocialOfficeCommandHandler;
import nts.uk.ctx.pr.core.app.command.insurance.social.RegisterSocialOfficeCommand;
import nts.uk.ctx.pr.core.app.command.insurance.social.RegisterSocialOfficeCommandHandler;
import nts.uk.ctx.pr.core.app.command.insurance.social.UpdateSocialOfficeCommand;
import nts.uk.ctx.pr.core.app.command.insurance.social.UpdateSocialOfficeCommandHandler;
import nts.uk.ctx.pr.core.app.command.insurance.social.pension.RegisterPensionCommandHandler;
import nts.uk.ctx.pr.core.app.find.insurance.social.dto.SocialInsuranceOfficeItemDto;

/**
 * The Class SocialInsuranceOfficeService.
 */
@Path("pr/insurance/social")
@Produces("application/json")
@Stateless
public class SocialInsuranceOfficeService extends WebService {

	@Inject
	private RegisterSocialOfficeCommandHandler registerSocialOfficeCommandHandler;
	@Inject
	private UpdateSocialOfficeCommandHandler updateSocialOfficeCommandHandler;
	@Inject
	private DeleteSocialOfficeCommandHandler deleteSocialOfficeCommandHandler;
	@Inject
	private RegisterPensionCommandHandler registerPensionCommandHandler;

	// Find all SocialInsuranceOffice conection data
	@POST
	@Path("findall")
	public List<SocialInsuranceOfficeItemDto> findAll() {
		List<SocialInsuranceOfficeItemDto> lstSocialInsuranceOfficeIn = new ArrayList<SocialInsuranceOfficeItemDto>();
		SocialInsuranceOfficeItemDto socialInsuranceOffice001 = new SocialInsuranceOfficeItemDto();
		socialInsuranceOffice001.setCode("000000000001");
		socialInsuranceOffice001.setName("A 事業所");
		lstSocialInsuranceOfficeIn.add(socialInsuranceOffice001);
		SocialInsuranceOfficeItemDto socialInsuranceOffice002 = new SocialInsuranceOfficeItemDto();
		socialInsuranceOffice002.setCode("000000000002");
		socialInsuranceOffice002.setName("b 事業所");
		lstSocialInsuranceOfficeIn.add(socialInsuranceOffice002);
		SocialInsuranceOfficeItemDto socialInsuranceOffice003 = new SocialInsuranceOfficeItemDto();
		socialInsuranceOffice003.setCode("000000000003");
		socialInsuranceOffice003.setName("c 事業所");
		lstSocialInsuranceOfficeIn.add(socialInsuranceOffice003);
		return lstSocialInsuranceOfficeIn;
	}

	@POST
	@Path("find/{officeName}")
	public SocialInsuranceOfficeItemDto findHistory(@PathParam("officeName") String officeName) {
		SocialInsuranceOfficeItemDto socialInsuranceOfficeDtoResult = new SocialInsuranceOfficeItemDto();
		List<SocialInsuranceOfficeItemDto> listOffice = this.findAll();
		for (SocialInsuranceOfficeItemDto SocialInsuranceOfficeDto : listOffice) {
			if (SocialInsuranceOfficeDto.getName().equals(officeName)) {
				socialInsuranceOfficeDtoResult = SocialInsuranceOfficeDto;
			}
		}
		return socialInsuranceOfficeDtoResult;
	}

	@POST
	@Path("create")
	public void createOffice(RegisterSocialOfficeCommand command) {
		this.registerSocialOfficeCommandHandler.handle(command);
		return;
	}

	@POST
	@Path("update")
	public void updateOffice(UpdateSocialOfficeCommand command) {
		this.updateSocialOfficeCommandHandler.handle(command);
		return;
	}

	@POST
	@Path("remove")
	public void removeOffice(DeleteSocialOfficeCommand command) {
		this.deleteSocialOfficeCommandHandler.handle(command);
		return;
	}
	@POST
	@Path("list/office")
	public void listOffice()
	{
		return;
	}

}
