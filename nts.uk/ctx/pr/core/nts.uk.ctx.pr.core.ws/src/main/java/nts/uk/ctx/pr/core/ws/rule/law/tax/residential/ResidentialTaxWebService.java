package nts.uk.ctx.pr.core.ws.rule.law.tax.residential;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.pr.core.app.command.rule.law.tax.residential.AddResidentalTaxCommandHandler;
import nts.uk.ctx.pr.core.app.command.rule.law.tax.residential.DeleteResidentalTaxCommandHandler;
import nts.uk.ctx.pr.core.app.command.rule.law.tax.residential.UpdateResidentalTaxCommandHandler;
import nts.uk.ctx.pr.core.app.find.rule.law.tax.residential.ResidentialTaxDto;
import nts.uk.ctx.pr.core.app.find.rule.law.tax.residential.ResidentialTaxFinder;
@Path("pr/core/residential")
@Produces("application/json")
public class ResidentialTaxWebService extends WebService {
 @Inject
 private AddResidentalTaxCommandHandler addData;
 @Inject
 private DeleteResidentalTaxCommandHandler deleleData;
 @Inject 
 private UpdateResidentalTaxCommandHandler updateData;
 @Inject
 private ResidentialTaxFinder find;
 @POST
 @Path("findallresidential")
 public List<ResidentialTaxDto> getAllResidential(){
	 return this.find.getAllResidentialTax();
 }
 
}
