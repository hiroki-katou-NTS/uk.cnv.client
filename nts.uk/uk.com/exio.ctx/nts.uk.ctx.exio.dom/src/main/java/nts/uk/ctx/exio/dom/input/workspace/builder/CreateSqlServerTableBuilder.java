package nts.uk.ctx.exio.dom.input.workspace.builder;

import lombok.Value;
import lombok.val;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.uk.ctx.exio.dom.input.workspace.datatype.DataTypeConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateSqlServerTableBuilder extends CreateTableBuilder {

    private final String tableName;

    private final List<Column> columns = new ArrayList<>();

    private final List<String> pkColumnNames = new ArrayList<>();


    public CreateSqlServerTableBuilder(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public CreateTableBuilder columnPK(String name, DataTypeConfiguration type) {
        columns.add(new Column(name, false, type));
        pkColumnNames.add(name);
        return this;
    }

    @Override
    public CreateTableBuilder column(String name, DataTypeConfiguration type) {
        columns.add(new Column(name, true, type));
        return this;
    }

    @Override
    public String buildSql() {

        val sql = new StringBuilder();

        sql.append("create table ").append(tableName).append(" (");

        for (val column : columns) {
            column.sql(sql);
            sql.append(",");
        }

        primaryKey(sql);

        sql.append(");");

        return sql.toString();
    }


    private void primaryKey(StringBuilder sql) {

        String pkName = "PK_" + tableName;

        String keys = pkColumnNames.stream()
                .collect(Collectors.joining(", "));

        sql.append("constraint ")
                .append(pkName)
                .append(" primary key nonclustered (")
                .append(keys)
                .append(")");
    }

    /**
     * 主キーなら非NULL、そうでなければNULL可とする
     */
    @Value
    private static class Column {

        String name;
        boolean isNullable;
        DataTypeConfiguration type;

        void sql(StringBuilder sql) {

            sql.append(name).append(" ");

            dataType(sql);

            if (isNullable) {
                sql.append(" null ");
            } else {
                sql.append(" not null ");
            }
        }

        void dataType(StringBuilder sql) {

            switch (type.getType()) {
                case DATE:
                case DATETIME:
                    sql.append("datetime2");
                    break;
                case STRING:
                    sql.append("nvarchar(").append(type.getLength()).append(")");
                    break;
                case INT:
                    sql.append("decimal(").append(type.getLength()).append(")");
                    break;
                case REAL:
                    sql.append("decimal(").append(type.getLength()).append(",").append(type.getScale()).append(")");
                    break;
                case AUTONUMBER:
                    sql.append("int IDENTITY(1,1)");
                    break;
                case BOOLEAN:
                    sql.append("decimal(1)");
                    break;
                default:
                    throw new RuntimeException("error: " + type);
            }
        }
    }
}
