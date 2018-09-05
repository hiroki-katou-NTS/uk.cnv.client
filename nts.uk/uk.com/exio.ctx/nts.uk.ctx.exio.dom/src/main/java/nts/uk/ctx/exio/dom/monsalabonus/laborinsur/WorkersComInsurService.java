package nts.uk.ctx.exio.dom.monsalabonus.laborinsur;

import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.List;
import java.util.Optional;


@Stateless
public class WorkersComInsurService {

    @Inject
    private OccAccInsurBusRepository occAccInsurBusRepository;
    @Inject
    private EmpInsurHisRepository empInsurHisRepository;
    @Inject
    private OccAccIsHisRepository occAccIsHisRepository;



    /*
    * 初期データ取得処理
    * */
    public List<OccAccIsHis> initDataAcquisition(String cId){
        /*
        * ドメインモデル「労災保険事業」を全て取得する
        * */
        Optional<OccAccInsurBus> acceptCode = occAccInsurBusRepository.getOccAccInsurBus(cId);
        /*
        *ドメインモデル「労災保険履歴」を全て取得する
        * */
        List<OccAccIsHis> getEmpInsurHisByCid = occAccIsHisRepository.getAllOccAccIsHisByCid(cId);
        if(acceptCode.get().getEachBusiness() == null || getEmpInsurHisByCid == null ){
            /*選択処理*/
          return null;


        }
        return getEmpInsurHisByCid;


    }




}
