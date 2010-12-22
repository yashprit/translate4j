/**
 * Copyright (C) 2010 Jose Llarena
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linguamathematica.translate4j;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;

import static org.junit.Assert.fail;
import static com.linguamathematica.translate4j.Fluency.withA;

import org.apache.http.HttpStatus;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServiceTest extends AbstractGoogleTranslateTest
{
	private static final String ANY_RESPONSE = "";
	private static final String EMPTY_RESPONSE = "";
	private static final String ANY_PATH = "/";
	private static final int TEST_PORT = 8082;
	private static final String LIMIT_EXCEEDED_RESPONSE = "{\"error\":{\"errors\":[{\"domain\":\"usageLimits\",\"reason\":\"dailyLimitExceeded\",\"message\":\"Daily Limit Exceeded\"}],\"code\":403,\"message\":\"Daily Limit Exceeded\"}}";
	private static HTTPServer server;

	@After
	public void tearDown() throws Exception
	{
		server.stop();
	}

	@Test
	public void throwsConnectionExceptionWhenItCantConnectToServer() throws Exception
	{
		server.start(ANY_RESPONSE, HttpStatus.SC_OK);

		try
		{
			aTranslator(withA(HTTPService(withA("wronghost")))).detect(ANY_TEXT);

			fail("should have thrown a ConnectionException");
		}
		catch (final ConnectionException exception)
		{

		}
	}

	@Test
	public void throwsResponseExceptionWhenResponseIsEmpty() throws Exception
	{
		server.start(EMPTY_RESPONSE, HttpStatus.SC_OK);

		try
		{
			aTranslator(withA(HTTPService(withA("localhost")))).detect(ANY_TEXT);

			fail(includeInMessage("Response content was invalid", HttpStatus.SC_OK, "OK"));
		}
		catch (final ResponseException exception)
		{
			Assert.assertThat(exception, reports("Response content was invalid", HttpStatus.SC_OK, "OK"));
		}
	}

	@Test
	public void throwsResponseExceptionWhenServerRespondsWithDailyLimitExceeded() throws Exception
	{
		server.start(LIMIT_EXCEEDED_RESPONSE, HttpStatus.SC_FORBIDDEN);

		try
		{
			aTranslator(withA(HTTPService(withA("localhost")))).detect(ANY_TEXT);

			fail(includeInMessage("Daily Limit Exceeded", HttpStatus.SC_FORBIDDEN, "Forbidden"));
		}
		catch (final ResponseException exception)
		{
			Assert.assertThat(exception, reports("Daily Limit Exceeded", HttpStatus.SC_FORBIDDEN, "Forbidden"));

		}
	}

	@Test
	public void throwsResponseExceptionWhenServerRespondsWithServerError() throws Exception
	{
		server.start(ANY_RESPONSE, HttpStatus.SC_INTERNAL_SERVER_ERROR);

		try
		{
			aTranslator(withA(HTTPService(withA("localhost")))).detect(ANY_TEXT);

			fail(includeInMessage("Response content was invalid", HttpStatus.SC_INTERNAL_SERVER_ERROR, "Server Error"));
		}
		catch (final ResponseException exception)
		{
			Assert.assertThat(exception,
					reports("Response content was invalid", HttpStatus.SC_INTERNAL_SERVER_ERROR, "Server Error"));

		}
	}

	@AfterClass
	public static void tearDownOnce()
	{
		server = null;
	}

	@BeforeClass
	public static void setUpOnce() throws Exception
	{
		server = new HTTPServer();
	}

	private static HTTPService HTTPService(final String host)
	{
		return new HTTPService("http", host, TEST_PORT, ANY_PATH, new DefaultHttpClient());
	}

	private static Translator aTranslator(final HTTPService httpService)
	{
		return new GoogleTranslatorVersion2(TEST_API_KEY, httpService);
	}

	private static String includeInMessage(final String message, final int httpCode, final String reason)
	{
		return format("should have thrown ResponseException reporting %s in message, status %s and reason %s", message,
				httpCode, reason);
	}

	private static <T> Matcher<ResponseException> reports(final String message, final int httpCode, final String reason)
	{
		return new Reports(message, httpCode, reason);
	}

	private static class HTTPServer
	{
		private Server server;

		private void start(final String response, final int code) throws Exception
		{
			server = new Server(TEST_PORT);
			server.setHandler(new TestHandler(response, code));
			server.start();
		}

		private void stop() throws Exception
		{
			if (server != null)
			{
				server.stop();
			}
		}
	}

	private static class Reports extends TypeSafeMatcher<ResponseException>
	{
		private final String message;
		private final int httpCode;
		private final String reason;

		private Reports(final String message, final int httpCode, final String reason)
		{
			super();
			this.message = message;
			this.httpCode = httpCode;
			this.reason = reason;
		}

		public void describeTo(final Description description)
		{
			description.appendText(format("to report [%s] in message, status code [%s] and reason [%s]", message,
					httpCode, reason));
		}

		@Override
		public boolean matchesSafely(final ResponseException exception)
		{
			return exception.getMessage().contains(message) && exception.getStatusCode() == httpCode
					&& exception.getReason().contains(reason);
		}

	}

	private static class TestHandler extends AbstractHandler
	{
		private final String payload;
		private final int HTTPStatus;

		private TestHandler(final String response, final int HTTPStatus)
		{
			payload = response;
			this.HTTPStatus = HTTPStatus;
		}

		public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
				final HttpServletResponse response) throws IOException, ServletException
		{
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HTTPStatus);
			baseRequest.setHandled(true);
			response.getWriter().println(payload);
		}
	}
}