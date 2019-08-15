package nts.uk.ctx.pr.core.ac.person;

import nts.uk.ctx.bs.employee.pub.person.IPersonInfoPub;
import nts.uk.ctx.bs.employee.pub.person.PersonInfoExport;
import nts.uk.ctx.pr.shared.dom.adapter.person.PersonInfoAdapter;
import nts.uk.ctx.pr.shared.dom.adapter.person.PersonInfoExportAdapter;

import javax.ejb.Stateless;
import javax.inject.Inject;


@Stateless
public class PersonInfoPubImpl implements PersonInfoAdapter {
    @Inject
   IPersonInfoPub repo;

    public PersonInfoExportAdapter getPersonInfo(String sID){
        PersonInfoExport domain = repo.getPersonInfo(sID);
        return new PersonInfoExportAdapter(
                domain.getPid(),
                domain.getBusinessName(),
                domain.getEntryDate(),
                domain.getGender(),
                domain.getBirthDay(),
                domain.getEmployeeId(),
                domain.getEmployeeCode(),
                domain.getRetiredDate()
        );
    }
}
