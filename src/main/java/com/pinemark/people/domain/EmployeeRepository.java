package com.pinemark.people.domain;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {

    private static final RowMapper<Employee> MAPPER = (rs, rowNum) -> new Employee(
            rs.getLong("id"),
            rs.getString("display_name"),
            rs.getString("department"),
            rs.getString("work_email"));

    private final JdbcTemplate jdbc;

    public EmployeeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Every query below is parameterised. No string concatenation reaches SQL,
     * including the search term, which is wrapped into the LIKE pattern as a bound
     * value rather than spliced into the statement.
     */
    public List<Employee> search(String term, int limit) {
        return jdbc.query(
                "SELECT id, display_name, department, work_email FROM employee "
                        + "WHERE LOWER(display_name) LIKE ? ORDER BY display_name LIMIT ?",
                MAPPER,
                "%" + term.toLowerCase() + "%",
                limit);
    }

    public Optional<Employee> findById(long id) {
        return jdbc.query(
                        "SELECT id, display_name, department, work_email FROM employee WHERE id = ?",
                        MAPPER,
                        id)
                .stream()
                .findFirst();
    }
}
