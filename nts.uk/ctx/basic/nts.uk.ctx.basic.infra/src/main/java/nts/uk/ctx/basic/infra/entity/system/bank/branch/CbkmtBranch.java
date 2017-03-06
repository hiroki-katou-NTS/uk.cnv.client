package nts.uk.ctx.basic.infra.entity.system.bank.branch;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.TableEntity;
/**
 * 
 * @author sonnh
 *
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="CBKMT_BRANCH")
public class CbkmtBranch extends TableEntity {
   @EmbeddedId
   public CbkmtBranchPK ckbmtBranchPK;
   
   @Column(name="BRANCH_NAME")
   public String branchName;
   
   @Column(name="BRANCH_KN_NAME")
   public String branchKnName;
   
   @Column(name="MEMO")
   public String memo;
}
