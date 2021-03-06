/*
 * Copyright 2014-2018 the original author or authors.
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
package example.db.auditing.test;

import example.db.auditing.AuditableUser;
import example.db.auditing.AuditableUserRepository;
import example.db.auditing.AuditorAwareImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@Slf4j
public class AuditableUserSample {

	@Autowired
	AuditableUserRepository repository;
	@Autowired
	AuditorAwareImpl auditorAware;
	@Autowired AuditingEntityListener listener;

	@Test
	public void auditEntityCreation() throws Exception {

		assertThat(ReflectionTestUtils.getField(listener, "handler"), is(notNullValue()));

		AuditableUser user = new AuditableUser();
		user.setUsername("username");

		auditorAware.setAuditor(0L);

		user = repository.save(user);
		user = repository.save(user);

		assertThat(user.getCreatedBy(), is(0L));
		assertThat(user.getLastModifiedBy(), is(0L));

		log.info("id is: " + user.getId());
	}
}
