/**
 * Copyright (C) 2010 the author
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

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

import static java.net.URLEncoder.encode;
import static java.util.regex.Pattern.compile;
import static com.linguamathematica.translate4j.Base.ensure;
import static com.linguamathematica.translate4j.Base.ensureNotNull;
import static com.linguamathematica.translate4j.Fluency.from;

class GoogleTranslatorVersion2 extends Translator
{
	private final String apiKey;
	private final HTTPService service;
	private static final Pattern detect = compile(asRegex("{'data':{'translations':[{'translatedText':'?','detectedSourceLanguage':'?'}]}}"));
	private static final Pattern translate = compile(asRegex("{'data':{'translations':[{'translatedText':'?'}]}}"));

	GoogleTranslatorVersion2(final String apiKey, final HTTPService service)
	{
		this.service = service;
		this.apiKey = apiKey;
	}

	@Override
	public Language detect(final String text)
	{
		ensureIsValid(text);

		final String responseBody = service.query(buildQuery(text, Language.UNKNOWN, Language.ENGLISH));

		return extractLanguage(from(responseBody));
	}

	@Override
	public void dispose()
	{
		service.shutDown();
	}

	@Override
	public String translate(final String text, final Language source, final Language target)
	{
		ensureIsValid(text);
		ensureNotNull(source, "source language");
		ensureNotNull(target, "target language");
		ensure(target != Language.UNKNOWN, "target language must be known");

		final String responseBody = service.query(buildQuery(text, source, target));

		return extractTranslation(from(responseBody));

	}

	private String buildQuery(final String text, final Language source, final Language target)
	{
		try
		{
			return source == Language.UNKNOWN ? format("key=%s&target=%s&q=%s", apiKey, target.getCode(),
					encode(text, "UTF-8")) : format("key=%s&source=%s&target=%s&q=%s", apiKey, source.getCode(),
					target.getCode(), encode(text, "UTF-8"));
		}
		catch (final UnsupportedEncodingException exception)
		{
			throw new RequestException(format("Error while building query for text [%s] . Report as bug", text),
					exception);
		}
	}

	private static String asRegex(final String string)
	{
		return string.replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]")
				.replace("'", "\\\"").replace("?", "(.+)");
	}

	private static void ensureIsValid(final String text)
	{
		ensureNotNull(text, "text");
		ensure(text.trim().length() != 0, "text cannot be empty");
		ensure(text.trim().length() <= MAX_TEXT_SIZE, "text size [%s] is greater than limit [%s]",
				text.trim().length(), MAX_TEXT_SIZE);
	}

	private static Language extractLanguage(final String responseBody)
	{
		final Matcher matcher = detect.matcher(responseBody);

		matcher.find();

		return Language.from(matcher.group(2));
	}

	private static String extractTranslation(final String responseBody)
	{
		final String withoutEncoding = responseBody.replace("&#39;", "'");

		final Matcher matcher = responseBody.contains("detectedSourceLanguage") ? detect.matcher(withoutEncoding)
				: translate.matcher(withoutEncoding);

		matcher.find();

		return matcher.group(1);
	}
}