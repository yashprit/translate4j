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

import static com.linguamathematica.translate4j.Base.ensure;
import static com.linguamathematica.translate4j.Base.ensureNotNull;

import org.apache.http.impl.client.DefaultHttpClient;

/**
 * The Translator.
 */
public abstract class Translator
{
	/** The maximum size of text to be passed to the translator's methods */
	public static final int MAX_TEXT_SIZE = 1968;

	Translator()
	{}

	/**
	 * Detects the language of the given text.
	 * 
	 * @param text
	 *            the text
	 * @return the language
	 * 
	 * @throws NullPointerException
	 *             if the text is null
	 * @throws IllegalArgumentException
	 *             if the text is empty, made up of blank spaces or greater than {@link Translator#MAX_TEXT_SIZE}
	 */
	public abstract Language detect(final String text);

	/**
	 * Call to release resources used by Translator.
	 */
	public abstract void dispose();

	/**
	 * Translates the given text in the source language to the target language.
	 * 
	 * @param text
	 *            the text
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return the string
	 */
	public abstract String translate(final String text, final Language source, final Language target);

	/**
	 * Creates a new translator instance with the given Google API key. Note that this method only does a basic check of
	 * the validity of the key. If it appears valid but isn't, a ResponseException will be thrown when invoking the
	 * service methods.
	 * 
	 * @param apiKey
	 *            the Google API key
	 * 
	 * @return the translator
	 * 
	 * @throws NullPointerException
	 *             if the API key is null
	 * 
	 * @throws IllegalArgumentException
	 *             if the API key is not a sequence of alphanumeric characters
	 * 
	 */
	public static Translator translator(final String apiKey)
	{
		ensureNotNull(apiKey, "API key");
		ensure(apiKey.trim().matches("[a-zA-Z0-9\\-]+"), "API key [%s] must be non-empty and alphanumeric", apiKey);

		final String PATH = "/language/translate/v2";
		final String HOST = "www.googleapis.com";
		final String PROTOCOL = "https";
		final int PORT = -1;

		return new GoogleTranslatorVersion2(apiKey, new HTTPService(PROTOCOL, HOST, PORT, PATH, makeHttpClient()));
	}

	private static DefaultHttpClient makeHttpClient()
	{
		return new DefaultHttpClient();
	}
}
