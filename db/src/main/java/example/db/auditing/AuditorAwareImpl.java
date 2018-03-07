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
package example.db.auditing;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Dummy implementation of {@link AuditorAware}. It will return the configured {@link AuditableUser} as auditor on every
 * call to {@link #getCurrentAuditor()}. Normally you would access the applications security subsystem to return the
 * current user.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public class AuditorAwareImpl implements AuditorAware<Long> {

	private Optional<Long> auditorId = Optional.empty();

	/**
	 * @param auditorId the auditor to set
	 */
	public void setAuditor(Long auditorId) {
		this.auditorId = Optional.of(auditorId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.AuditorAware#getCurrentAuditor()
	 */
	public Optional<Long> getCurrentAuditor() {
		return auditorId;
	}
}
