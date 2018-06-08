package nts.uk.ctx.pereg.infra.repository.roles.functionauth;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import nts.uk.ctx.pereg.dom.roles.functionauth.authsettingdesc.PerInfoAuthDescRepository;
import nts.uk.ctx.pereg.dom.roles.functionauth.authsettingdesc.PersonInfoAuthDescription;

@Stateless
public class PerInfoAuthDescRepoImpl implements PerInfoAuthDescRepository{

	@Override
	public List<PersonInfoAuthDescription> getListDesc() {
		List<PersonInfoAuthDescription> result = new ArrayList<>();
		result.add(PersonInfoAuthDescription.createFromJavaType(1, "No1", "Description of No1", 1, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(2, "No2", "Description of No2", 2, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(3, "No3", "Description of No3", 3, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(4, "No4", "Description of No4", 4, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(5, "No5", "Description of No5", 5, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(6, "No6", "Description of No6", 6, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(7, "No7", "Description of No7", 7, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(8, "No8", "Description of No8", 8, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(9, "No0", "Description of No9", 9, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(10, "No10", "Description of No10", 10, false));
		result.add(PersonInfoAuthDescription.createFromJavaType(11, "No11", "Description of No11", 11, false));
		return result;
	}
	
	

}
