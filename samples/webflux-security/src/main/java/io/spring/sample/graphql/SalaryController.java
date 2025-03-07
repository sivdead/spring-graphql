/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.sample.graphql;

import java.math.BigDecimal;
import java.util.List;

import reactor.core.publisher.Mono;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.GraphQlController;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;

@GraphQlController
public class SalaryController {

	private final EmployeeService employeeService;

	private final SalaryService salaryService;

	public SalaryController(EmployeeService employeeService, SalaryService salaryService) {
		this.employeeService = employeeService;
		this.salaryService = salaryService;
	}

	@QueryMapping
	public List<Employee> employees() {
		return this.employeeService.getAllEmployees();
	}

	@SchemaMapping
	public Mono<BigDecimal> salary(Employee employee) {
		return this.salaryService.getSalaryForEmployee(employee);
	}

	@MutationMapping
	public void updateSalary(@Argument("input") SalaryInput salaryInput) {
		String employeeId = salaryInput.getEmployeeId();
		BigDecimal salary = salaryInput.getNewSalary();
		this.salaryService.updateSalary(employeeId, salary);
	}

}
