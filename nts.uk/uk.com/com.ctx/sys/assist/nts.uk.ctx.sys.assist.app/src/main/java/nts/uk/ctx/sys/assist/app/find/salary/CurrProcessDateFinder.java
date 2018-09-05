package nts.uk.ctx.sys.assist.app.find.salary;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import nts.uk.ctx.sys.assist.dom.salary.CurrProcessDateRepository;

@Stateless
/**
* 現在処理年月
*/
public class CurrProcessDateFinder
{

    @Inject
    private CurrProcessDateRepository finder;

    public List<CurrProcessDateDto> getAllCurrProcessDate(){
        return finder.getAllCurrProcessDate().stream().map(item -> CurrProcessDateDto.fromDomain(item))
                .collect(Collectors.toList());
    }

}
