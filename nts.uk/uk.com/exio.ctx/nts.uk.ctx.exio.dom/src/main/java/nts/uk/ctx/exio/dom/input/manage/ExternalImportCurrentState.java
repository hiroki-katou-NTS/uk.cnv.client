package nts.uk.ctx.exio.dom.input.manage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrorsRequire;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;

/**
 * 外部受入の現在の状態
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Slf4j
public class ExternalImportCurrentState implements DomainAggregate {

	/** 会社ID */
	private final String companyId;
	
	/** 実行状態 */
	private ExecutionState executionState;
	
	public static ExternalImportCurrentState createNew(String companyId) {
		return new ExternalImportCurrentState(companyId, ExecutionState.IDLE);
	}
	
	/**
	 * 「受入処理の準備」の状態を制御する
	 * @param require
	 * @param prepare
	 * @throws ExternalImportStateException 
	 */
	public void prepare(Require require, ExternalImportSetting setting, Runnable prepare)
			throws ExternalImportStateException {

		preparing(require);
		
		handle(require, setting, () -> {
			prepare.run();
			prepared(require);
		});
	}
	
	/**
	 * 「受入処理の実行」の状態を制御する
	 * @param require
	 * @param prepare
	 * @throws ExternalImportStateException 
	 */
	public void execute(Require require, ExternalImportSetting setting, Runnable execute)
			throws ExternalImportStateException {

		executing(require);
		
		handle(require, setting, () -> {
			execute.run();
			executed(require);
		});
	}
	
	private void handle(Require require, ExternalImportSetting setting, Runnable mainProcess) {
		
		val context = ExecutionContext.create(setting);
		
		try {
			
			mainProcess.run();
			
		} catch (BusinessException ex) {
			
			require.add(context, ExternalImportError.execution(ex.getMessage()));
			abortedByBusinessError(require);
			
		} catch (Exception ex) {
			
			log.error("外部受入システムエラー: " + context, ex);
			abortedBySystemError(require);
			throw ex;
		}
	}
	
	public static interface Require extends ExternalImportErrorsRequire {
		
		void update(ExternalImportCurrentState currentState);
	}
	
	private void preparing(Require require) throws ExternalImportStateException {
		executionState.checkIfCanPrepare();
		changeState(require, ExecutionState.ON_PREPARE);
	}
	
	private void prepared(Require require) {
		changeState(require, ExecutionState.PREPARED);
	}
	
	private void executing(Require require) throws ExternalImportStateException {
		executionState.checkIfCanExecute();
		changeState(require, ExecutionState.ON_EXECUTE);
	}
	
	private void executed(Require require) {
		changeState(require, ExecutionState.IDLE);
	}
	
	private void abortedByBusinessError(Require require) {
		changeState(require, getNextStateOnAbort());
	}
	
	private void abortedBySystemError(Require require) {
		changeState(require, getNextStateOnAbort());
	}

	private ExecutionState getNextStateOnAbort() {
		if (executionState == ExecutionState.ON_EXECUTE) {
			return ExecutionState.PREPARED;
		} else {
			return ExecutionState.IDLE;
		}
	}
	
	private void changeState(Require require, ExecutionState nextState) {
		executionState = nextState;
		require.update(this);
	}
}
