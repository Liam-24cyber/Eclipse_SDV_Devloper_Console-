/*
 *   ========================================================================
 *  SDV Developer Console
 *
 *   Copyright (C) 2022 - 2023 T-Systems International GmbH
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   SPDX-License-Identifier: Apache-2.0
 *
 *   ========================================================================
 */

package com.tsystems.dco.config;

import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class GraphQLConfiguration {

  @Bean
  public RuntimeWiringConfigurer runtimeWiringConfigurerUpload() {

    GraphQLScalarType uploadScalar = GraphQLScalarType.newScalar()
      .name("Upload")
      .coercing(new UploadCoercing())
      .build();

    return wiringBuilder -> wiringBuilder.scalar(uploadScalar);
  }

  // Note: Spring Cloud Gateway with reactive GraphQL requires different multipart handling
  // For now, we'll disable the multipart router function and rely on standard GraphQL endpoints
  /*
  @Bean
  @Order(1)
  public RouterFunction<ServerResponse> graphQlMultipartRouterFunction(
    GraphQlProperties properties,
    WebGraphQlHandler webGraphQlHandler,
    ObjectMapper objectMapper
  ) {
    String path = properties.getPath();
    var builder = RouterFunctions.route();
    var graphqlMultipartHandler = new GraphqlMultipartHandler(webGraphQlHandler, objectMapper);
    builder = builder.POST(path, RequestPredicates.contentType(MULTIPART_FORM_DATA)
      .and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA)), graphqlMultipartHandler::handleRequest);
    return builder.build();
  }
  */
}

class UploadCoercing implements Coercing<MultipartFile, MultipartFile> {

  @Override
  public MultipartFile serialize(Object dataFetcherResult) throws CoercingSerializeException {
    throw new CoercingSerializeException("Upload is an input-only type");
  }

  @Override
  public MultipartFile parseValue(Object input) throws CoercingParseValueException {
    if (input instanceof MultipartFile) {
      return (MultipartFile) input;
    }
    throw new CoercingParseValueException(
      String.format("Expected a 'MultipartFile' like object but was '%s'.", input != null ? input.getClass() : null)
    );
  }

  @Override
  public MultipartFile parseLiteral(Object input) throws CoercingParseLiteralException {
    throw new CoercingParseLiteralException("Parsing literal of 'MultipartFile' is not supported");
  }
}

// Note: Multipart GraphQL functionality is disabled for Spring Cloud Gateway compatibility
// The reactive stack requires different multipart handling approach
