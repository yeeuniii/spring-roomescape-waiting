package roomescape.theme.domain;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.theme.domain.entity.Theme;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class ThemeJdbcTemplateRepository implements ThemeRepository {
    private final JdbcTemplate jdbcTemplate;

    public ThemeJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Theme> rowMapper = (resultSet, rowNum) -> {
        return Theme.of(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("thumbnail")
        );
    };

    @Override
    public List<Theme> findAll() {
        String sql = """
                    select *
                    from theme
                    """;
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Theme> findById(Long id) {
        String sql = """
                    select *
                    from theme
                    where id = ?""";

        try {
            Theme theme = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(theme);
        }
        catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Theme> findByName(String name) {
        String sql = """
                    select *
                    from theme
                    where name = ?
                    """;
        try {
            Theme theme = jdbcTemplate.queryForObject(sql, rowMapper, name);
            return Optional.ofNullable(theme);
        }
        catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Long countReservationMatchWith(Long id) {
        String sql = """
                    select count(*)
                    from reservation as r
                    inner join theme as t
                    on r.theme_id = t.id
                    where t.id = ?
                    """;
        return jdbcTemplate.queryForObject(sql, Long.class, id);
    }

    @Override
    public long save(Theme theme) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            String sql = """
                        insert into theme
                        (name, description, thumbnail)
                        values (?, ?, ?)
                        """;
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, theme.getName());
            ps.setString(2, theme.getDescription());
            ps.setString(3, theme.getThumbnail());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public long deleteById(Long id) {
        String sql = """
                    delete from theme
                    where id = ?
                    """;
        return jdbcTemplate.update(sql, id);
    }
}
