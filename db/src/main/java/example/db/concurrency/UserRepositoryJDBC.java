/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.db.concurrency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Optional;

/**
 * @author gavin
 */
@Component
public class UserRepositoryJDBC  {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepositoryJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findById(Long id) {
        User user = jdbcTemplate.queryForObject("select * from user where id=?", new Object[]{id}, new UserRowMapper());
        Optional<User> res = Optional.of(user);
        return res;
    }

    public long count() {
        long total = jdbcTemplate.queryForObject("select count(*) from user", Long.class);
        return total;
    }

    public long countByUsername(String username) {
        long total = jdbcTemplate.queryForObject("select count(*) from user where username=?", new Object[] {username},  Long.class);
        return total;
    }

    public void save(User user) {
        if (user.getId() == null) {
            create(user);
        } else {
            update(user);
        }
    }

    private User create(final User user) {
        final String sql = "insert into user(username) values(?)";

        KeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                return ps;
            }
        }, holder);

        long newUserId = holder.getKey().longValue();
        user.setId(newUserId);
        return user;
    }

    public void delete(final Integer id) {
        final String sql = "delete from user where id=?";
        jdbcTemplate.update(sql,
                new Object[]{id},
                new int[]{Types.INTEGER});
    }

    public void update(final User user) {
        jdbcTemplate.update(
                "update user set username=? where id=?",
                new Object[]{user.getUsername(), user.getId()});
    }

    public int updateNameByUsername(String name, String username) {
        return jdbcTemplate.update(
                "update user set name=? where username=?",
                new Object[]{name, username});
    }
}


class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));

        return user;
    }

}
