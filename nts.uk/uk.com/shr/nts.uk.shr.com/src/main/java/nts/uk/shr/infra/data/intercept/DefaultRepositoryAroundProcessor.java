package nts.uk.shr.infra.data.intercept;

import nts.arc.layer.infra.data.intercept.RepositoryAroundProcessor;
import nts.arc.layer.infra.data.log.RepositoryLogger;
import nts.arc.validate.Validatable;
import nts.gul.error.FatalLog;

import javax.ejb.Stateless;
import javax.interceptor.InvocationContext;

@Stateless
public class DefaultRepositoryAroundProcessor implements RepositoryAroundProcessor {

	@Override
	public Object process(InvocationContext context) {
		
		// validate domain object if it is parameter of command (not query).
		// If return type is void, this method is probably command.
		if (context.getMethod().getReturnType().getName() == "void") {
			this.validateParameters(context.getParameters());
		}
		
		RepositoryLogger.entering(
				context.getMethod().getDeclaringClass().getName(),
				context.getMethod().getName());
		
		try {
			return context.proceed();
		} catch (Exception e) {
			if (isConnectionError(e)) {
				FatalLog.write(this, "データベースとの接続に障害が起きています。");
			}

			throw new RuntimeException(e);
		} finally {
			RepositoryLogger.exited();
		}
	}

	private static boolean isConnectionError(Exception e) {
		String message = e.getMessage();
		if (message == null) {
			return false;
		}

		boolean connectionError
				= message.contains("Transaction cannot proceed: STATUS_MARKED_ROLLBACK")
				|| message.contains("This connection has been closed")
				|| message.contains("このコネクションは既にクローズされています");
		return connectionError;
	}

	private void validateParameters(Object[] parameters) {
		
		for (Object parameter : parameters) {
			if (parameter instanceof Validatable) {
				((Validatable) parameter).validate();
			}
		}
	}
	
}
