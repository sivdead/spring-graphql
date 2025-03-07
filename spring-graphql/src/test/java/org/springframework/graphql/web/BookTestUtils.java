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

package org.springframework.graphql.web;

import java.util.Arrays;

import graphql.schema.idl.TypeRuntimeWiring;
import reactor.core.publisher.Flux;

import org.springframework.core.io.ClassPathResource;
import org.springframework.graphql.BookSource;
import org.springframework.graphql.execution.ExecutionGraphQlService;
import org.springframework.graphql.execution.GraphQlSource;

public abstract class BookTestUtils {

	public static final String SUBSCRIPTION_ID = "1";

	public static final String BOOK_QUERY = "{" +
			"\"id\":\"" + BookTestUtils.SUBSCRIPTION_ID + "\"," +
			"\"type\":\"subscribe\"," +
			"\"payload\":{\"query\": \"" +
			"  query TestQuery {" +
			"    bookById(id: \\\"1\\\"){ " +
			"      id" +
			"      name" +
			"      author {" +
			"        firstName" +
			"        lastName" +
			"      }" +
			"  }}\"}" +
			"}";

	public static final String BOOK_SUBSCRIPTION = "{" +
			"\"id\":\"" + SUBSCRIPTION_ID + "\"," +
			"\"type\":\"subscribe\"," +
			"\"payload\":{\"query\": \"" +
			"  subscription TestSubscription {" +
			"    bookSearch(author: \\\"George\\\") {" +
			"      id" +
			"      name" +
			"      author {" +
			"        firstName" +
			"        lastName" +
			"      }" +
			"  }}\"}" +
			"}";

	public static WebGraphQlHandler initWebGraphQlHandler(WebInterceptor... interceptors) {
		return WebGraphQlHandler.builder(new ExecutionGraphQlService(graphQlSource()))
				.interceptors(Arrays.asList(interceptors))
				.build();
	}

	private static GraphQlSource graphQlSource() {
		return GraphQlSource.builder()
				.schemaResources(new ClassPathResource("books/schema.graphqls"))
				.configureRuntimeWiring(builder -> builder.type(TypeRuntimeWiring.newTypeWiring("Query")
						.dataFetcher("bookById", (env) -> {
							Long id = Long.parseLong(env.getArgument("id"));
							return BookSource.getBook(id);
						}))
						.type(TypeRuntimeWiring.newTypeWiring("Subscription")
						.dataFetcher("bookSearch", (env) -> {
							String author = env.getArgument("author");
							return Flux.fromIterable(BookSource.books())
									.filter((book) -> book.getAuthor().getFullName().contains(author));
						})))
				.build();
	}

}
