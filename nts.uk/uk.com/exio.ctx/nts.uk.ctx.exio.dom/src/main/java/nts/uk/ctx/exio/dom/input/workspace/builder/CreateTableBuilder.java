package nts.uk.ctx.exio.dom.input.workspace.builder;

import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.uk.ctx.exio.dom.input.workspace.datatype.DataTypeConfiguration;

public abstract class CreateTableBuilder {

    public static CreateTableBuilder newInstance(DatabaseProduct database, String tableName){
        switch (database){
            case MSSQLSERVER:
                return new CreateSqlServerTableBuilder(tableName);
            case POSTGRESQL:
                return new CreatePostgresqlTableBuilder(tableName);
        }
        throw new RuntimeException("Database product'" + database.name() + "' is unsupported.");
    }

    public abstract CreateTableBuilder columnPK(String name, DataTypeConfiguration type);

    public abstract CreateTableBuilder column(String name, DataTypeConfiguration type);

    public abstract String buildSql();
}
