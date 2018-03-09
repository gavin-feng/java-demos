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
package example.db.lock.pessimistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @author gavin
 */
@Component
public class PessimisticUserRepositoryJDBC {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PessimisticUserRepositoryJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<PessimisticUser> findByIdNormal(Long id) {
        PessimisticUser user = jdbcTemplate.queryForObject("select * from user where id=? ", new Object[]{id}, new UserRowMapper());
        Optional<PessimisticUser> res = Optional.of(user);
        return res;
    }
    public Optional<PessimisticUser> findByIdPessimisticRead(Long id) {
        PessimisticUser user = jdbcTemplate.queryForObject("select * from user where id=? lock in share mode", new Object[]{id}, new UserRowMapper());
        Optional<PessimisticUser> res = Optional.of(user);
        return res;
    }
    public Optional<PessimisticUser> findByIdPessimisticWrite(Long id) {
        PessimisticUser user = jdbcTemplate.queryForObject("select * from user where id=? for update", new Object[]{id}, new UserRowMapper());
        Optional<PessimisticUser> res = Optional.of(user);
        return res;
    }

    public void save(final PessimisticUser user) {
        jdbcTemplate.update(
                "update user set username=? where id=?",
                new Object[]{user.getUsername(), user.getId()});
    }
}


class UserRowMapper implements RowMapper<PessimisticUser> {

    @Override
    public PessimisticUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        PessimisticUser user = new PessimisticUser();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));

        return user;
    }

}
