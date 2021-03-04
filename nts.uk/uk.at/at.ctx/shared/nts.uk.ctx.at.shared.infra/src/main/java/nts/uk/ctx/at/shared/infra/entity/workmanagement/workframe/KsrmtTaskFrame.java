package nts.uk.ctx.at.shared.infra.entity.workmanagement.workframe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.workmanagement.aggregateroot.workframe.WorkFrameUsageSetting;
import nts.uk.ctx.at.shared.infra.entity.workmanagement.worknarrowingdown.KsrmtTaskAssignWkp;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Setter
@Table(name = "KSRMT_TASK_FRAME")
public class KsrmtTaskFrame extends ContractUkJpaEntity implements Serializable {

    public static final long serialVersionUID = 1L;
    /**
     * 職場ID:職場別作業の絞込->職場ID
     */
    @Id
    @Column(name = "CID")
    public String CID;

    /**
     * 	作業枠1利用区分: 作業枠利用設定.枠設定	->	利用区分
     */
    @Column(name = "FRAME1_USE_ATR")
    public int FRAME1USEATR;

    /**
     * 	作業枠1名称 :	作業枠利用設定.枠設定->	作業枠名
     */
    @Column(name = "FRAME1_NAME")
    public String 	FRAME1NAME;

    /**
     * 	作業枠1利用区分: 作業枠利用設定.枠設定	->	利用区分
     */
    @Column(name = "FRAME2_USE_ATR")
    public int FRAME2USEATR;

    /**
     * 	作業枠1名称 :	作業枠利用設定.枠設定->	作業枠名
     */
    @Column(name = "FRAME2_NAME")
    public String 	FRAME2NAME;
    /**
     * 	作業枠1利用区分: 作業枠利用設定.枠設定	->	利用区分
     */
    @Column(name = "FRAME3_USE_ATR")
    public int FRAME3USEATR;

    /**
     * 	作業枠1名称 :	作業枠利用設定.枠設定->	作業枠名
     */
    @Column(name = "FRAME3_NAME")
    public String 	FRAME3NAME;
    /**
     * 	作業枠1利用区分: 作業枠利用設定.枠設定	->	利用区分
     */
    @Column(name = "FRAME4_USE_ATR")
    public int FRAME4USEATR;

    /**
     * 	作業枠1名称 :	作業枠利用設定.枠設定->	作業枠名
     */
    @Column(name = "FRAME4_NAME")
    public String 	FRAME4NAME;
    /**
     * 	作業枠1利用区分: 作業枠利用設定.枠設定	->	利用区分
     */
    @Column(name = "FRAME5_USE_ATR")
    public int FRAME5USEATR;

    /**
     * 	作業枠1名称 :	作業枠利用設定.枠設定->	作業枠名
     */
    @Column(name = "FRAME5_NAME")
    public String 	FRAME5NAME;


    @Override
    protected Object getKey() {
        return CID;
    }


    public static KsrmtTaskFrame toEntity(WorkFrameUsageSetting domain){
        KsrmtTaskFrame entity = new KsrmtTaskFrame();
        entity.setCID(AppContexts.user().companyId());
        domain.getFrameSettingList().forEach(e->{
            if(e.getTaskFrameNo().v() ==1 ){
                entity.setFRAME1NAME(e.getWorkFrameName().v());
                entity.setFRAME1USEATR( e.getUseAtr().value);
            }else if(e.getTaskFrameNo().v() ==2 ){
                entity.setFRAME2NAME(e.getWorkFrameName().v());
                entity.setFRAME2USEATR( e.getUseAtr().value);
            }else if(e.getTaskFrameNo().v() ==3 ){
                entity.setFRAME3NAME(e.getWorkFrameName().v());
                entity.setFRAME3USEATR( e.getUseAtr().value);
            }else if(e.getTaskFrameNo().v() ==4 ){
                entity.setFRAME4NAME(e.getWorkFrameName().v());
                entity.setFRAME4USEATR( e.getUseAtr().value);
            }else if(e.getTaskFrameNo().v() ==5 ){
                entity.setFRAME5NAME(e.getWorkFrameName().v());
                entity.setFRAME5USEATR( e.getUseAtr().value);
            }
        });
        return entity;
    }
}
