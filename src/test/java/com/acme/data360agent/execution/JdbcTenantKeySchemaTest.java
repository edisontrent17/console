package com.acme.data360agent.execution;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:tenant-key-schema-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.security.enabled=false"
})
class JdbcTenantKeySchemaTest {
    @Autowired
    private DataSource dataSource;

    @Test
    void coreStateTablesUseCompositeTenantPrimaryKeys() throws Exception {
        assertThat(primaryKeyColumns("plan_drafts")).containsExactly("organization_id", "plan_id");
        assertThat(primaryKeyColumns("plan_runs")).containsExactly("organization_id", "run_id");
        assertThat(primaryKeyColumns("approval_records")).containsExactly("organization_id", "approval_id");
        assertThat(primaryKeyColumns("audit_events")).containsExactly("organization_id", "event_id");
        assertThat(primaryKeyColumns("monitor_definitions")).containsExactly("organization_id", "monitor_id");
        assertThat(primaryKeyColumns("monitor_runs")).containsExactly("organization_id", "monitor_run_id");
        assertThat(primaryKeyColumns("monitor_recommendations")).containsExactly("organization_id", "recommendation_id");
        assertThat(primaryKeyColumns("approved_plans")).containsExactly("organization_id", "plan_id");
    }

    private List<String> primaryKeyColumns(String table) throws Exception {
        try (var connection = dataSource.getConnection();
             var keys = connection.getMetaData().getPrimaryKeys(null, null, table.toUpperCase(Locale.ROOT))) {
            var columns = new ArrayList<KeyColumn>();
            while (keys.next()) {
                columns.add(new KeyColumn(keys.getShort("KEY_SEQ"), keys.getString("COLUMN_NAME").toLowerCase(Locale.ROOT)));
            }
            return columns.stream()
                    .sorted(Comparator.comparingInt(KeyColumn::sequence))
                    .map(KeyColumn::name)
                    .toList();
        }
    }

    private record KeyColumn(int sequence, String name) {
    }
}
