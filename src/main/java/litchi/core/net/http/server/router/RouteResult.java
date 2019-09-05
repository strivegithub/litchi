/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package litchi.core.net.http.server.router;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.ObjectUtil;

/**
 * from: https://github.com/sinetja/netty-router
 * Result of calling {@link Router#route(HttpMethod, String)}.
 */
public class RouteResult<T> {
	private final String uri;
	private final String decodedPath;

	private final Map<String, String> pathParams;
	private final Map<String, List<String>> queryParams;

	private final T target;

	/**
	 * The maps will be wrapped in Collections.unmodifiableMap.
	 */
	public RouteResult(String uri, String decodedPath, Map<String, String> pathParams, Map<String, List<String>> queryParams, T target) {
		this.uri = ObjectUtil.checkNotNull(uri, "uri");
		this.decodedPath = ObjectUtil.checkNotNull(decodedPath, "decodedPath");
		this.pathParams = Collections.unmodifiableMap(ObjectUtil.checkNotNull(pathParams, "pathParams"));
		this.queryParams = Collections.unmodifiableMap(ObjectUtil.checkNotNull(queryParams, "queryParams"));
		this.target = ObjectUtil.checkNotNull(target, "target");
	}



	/**
	 * Returns the original request URI.
	 */
	public String uri() {
		return uri;
	}

	/**
	 * Returns the decoded request path.
	 */
	public String decodedPath() {
		return decodedPath;
	}

	/**
	 * Returns all params embedded in the request path.
	 */
	public Map<String, String> pathParams() {
		return pathParams;
	}

	/**
	 * Returns all params in the query part of the request URI.
	 */
	public Map<String, List<String>> queryParams() {
		return queryParams;
	}

	public T target() {
		return target;
	}

	// ----------------------------------------------------------------------------
	// Utilities to call params.

	/**
	 * Extracts the first matching param in {@code queryParams}.
	 *
	 * @return {@code null} if there's no match
	 */
	public String queryParam(String name) {
		List<String> values = queryParams.get(name);
		return (values == null) ? null : values.get(0);
	}

	/**
	 * Extracts the param in {@code pathParams} first, then falls back to the first matching
	 * param in {@code queryParams}.
	 *
	 * @return {@code null} if there's no match
	 */
	public String param(String name) {
		String pathValue = pathParams.get(name);
		return (pathValue == null) ? queryParam(name) : pathValue;
	}

	/**
	 * Extracts all params in {@code pathParams} and {@code queryParams} matching the name.
	 *
	 * @return Unmodifiable list; the list is empty if there's no match
	 */
	public List<String> params(String name) {
		List<String> values = queryParams.get(name);
		String value = pathParams.get(name);

		if (values == null) {
			return (value == null) ? Collections.emptyList() : Collections.singletonList(value);
		}

		if (value == null) {
			return Collections.unmodifiableList(values);
		} else {
			List<String> aggregated = new ArrayList<>(values.size() + 1);
			aggregated.addAll(values);
			aggregated.add(value);
			return Collections.unmodifiableList(aggregated);
		}
	}
}
