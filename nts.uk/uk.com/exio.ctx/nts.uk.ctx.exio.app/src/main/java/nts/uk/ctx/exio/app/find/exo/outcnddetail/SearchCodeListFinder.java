package nts.uk.ctx.exio.app.find.exo.outcnddetail;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.exio.dom.exo.outcnddetail.SearchCodeListRepository;

/**
* 検索コードリスト
*/
@Stateless
public class SearchCodeListFinder{

    @Inject
    private SearchCodeListRepository finder;

    

}

