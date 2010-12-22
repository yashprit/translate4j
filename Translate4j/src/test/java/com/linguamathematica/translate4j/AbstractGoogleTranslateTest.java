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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

import static java.lang.Runtime.getRuntime;

import static java.lang.String.format;

import static java.util.EnumSet.complementOf;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static com.linguamathematica.translate4j.Base.newList;
import static com.linguamathematica.translate4j.Fluency.and;
import static com.linguamathematica.translate4j.Fluency.between;
import static com.linguamathematica.translate4j.Fluency.from;
import static com.linguamathematica.translate4j.Fluency.of;
import static com.linguamathematica.translate4j.Fluency.to;
import static com.linguamathematica.translate4j.Language.BASQUE;
import static com.linguamathematica.translate4j.Language.ENGLISH;
import static com.linguamathematica.translate4j.Language.SPANISH;
import static com.linguamathematica.translate4j.Language.UNKNOWN;
import static com.linguamathematica.translate4j.Translator.translator;

import org.apache.log4j.Logger;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;

public abstract class AbstractGoogleTranslateTest
{
	static final String TEST_API_KEY = "AIzaSyD1eR6nJDBVIz9LtFr5V2HZYmuwIYG7YwQ";// "YOUR KEY";//
	static final Language ANY_LANGUAGE = ENGLISH;
	static final Language ANY_OTHER_LANGUAGE = SPANISH;
	static final String ANY_TEXT = "any text";
	static final String MESSAGE = "should have correctly translated [%s] in %s to at least [%5.2f]%% of the languages but was [%5.2f]%%";

	private static final Language[] SUPPORTED = complementOf(EnumSet.of(BASQUE, UNKNOWN)).toArray(new Language[0]);

	private static String[] texts = readTexts();
	private static Translator translator;

	final Logger log;

	public AbstractGoogleTranslateTest()
	{
		log = Logger.getLogger(getClass());
	}

	protected Language detect(final String text)
	{
		return translator.detect(text);
	}

	protected Language detectedLanguage(final String text)
	{
		return detect(text);
	}

	@DataPoints
	public static LanguageWithText[] languagesWithTexts()
	{
		final LanguageWithText[] languagesWithTexts = new LanguageWithText[SUPPORTED.length];

		for (int i = 0; i < texts.length; i++)
		{
			languagesWithTexts[i] = new LanguageWithText(SUPPORTED[i], texts[i]);
		}

		return languagesWithTexts;
	}

	@BeforeClass
	public static void setUpOnce() throws Exception
	{
		translator = translator(TEST_API_KEY);
	}

	@AfterClass
	public static void tearDownOnce()
	{
		translator.dispose();
	}

	protected static String stringOfLength(final int i)
	{
		return new String(new char[i]).replace(((char) 0), 'a');
	}

	protected static String withFailureMessage(final String messageTemplate, final Object... arguments)
	{
		return format(messageTemplate, arguments);
	}

	static <T> void assertThat(final T actual, final Matcher<T> matcher, final String failureMessage)
	{
		Assert.assertThat(failureMessage, actual, matcher);
	}

	static Ongoing newProcess()
	{
		return new Ongoing()
		{
			private final CompletionService<Boolean> paralleliser = new ExecutorCompletionService<Boolean>(
					newFixedThreadPool(getRuntime().availableProcessors() + 1));

			public void startCheckingTranslation(final LanguageWithText source, final LanguageWithText target,
					final double acceptableSimilarity)
			{
				paralleliser.submit(new TranslationSimilarityTask()
				{
					@Override
					public Boolean check()
					{
						return similarity(
								between(translation(of(source.text), from(source.language), to(target.language))),
								and(target.text)) >= acceptableSimilarity;
					}
				});
			}

			public boolean whenReady_nextTextIsCorrectlyTranslated() throws InterruptedException, ExecutionException
			{
				return paralleliser.take().get();
			}
		};
	}

	static double percentage(final double correct)
	{
		return 100 * correct / languagesWithTexts().length;
	}

	static double similarity(final String actualTranslation, final String expectedTranslation)
	{
		final Collection<String> actualWords = parse(actualTranslation);
		final Collection<String> expectedWords = parse(expectedTranslation);

		final double maxSimilarity = expectedWords.size();

		expectedWords.retainAll(actualWords);

		return expectedWords.size() / maxSimilarity;
	}

	static String translation(final String text, final Language fromLanguage, final Language toLanguage)
	{
		return translator.translate(text, fromLanguage, toLanguage);
	}

	private static Collection<String> parse(final String actualTranslation)
	{
		return newList(actualTranslation.replaceAll("\\,|\\.||;", "").split("\\s"));
	}

	private static String[] readTexts()
	{
		final Scanner scanner = new Scanner(AbstractGoogleTranslateTest.class.getResourceAsStream("/texts.txt"),
				"UTF-8");

		final List<String> readTexts = new ArrayList<String>();

		while (scanner.hasNextLine())
		{
			readTexts.add(scanner.nextLine().trim());
		}

		return readTexts.toArray(new String[readTexts.size()]);
	}

	static class LanguageWithText
	{
		final Language language;
		final String text;

		LanguageWithText(final Language language, final String sentence)
		{
			this.language = language;
			text = sentence;
		}
	}

	interface Ongoing
	{
		void startCheckingTranslation(final LanguageWithText source, final LanguageWithText target,
				final double acceptableSimilarity);

		boolean whenReady_nextTextIsCorrectlyTranslated() throws InterruptedException, ExecutionException;

		static abstract class TranslationSimilarityTask implements Callable<Boolean>
		{
			public Boolean call()
			{
				return check();
			}

			public abstract Boolean check();

			String translation(final String text, final Language fromLanguage, final Language toLanguage)
			{
				final Translator translator = translator(TEST_API_KEY);

				final String translation = translator.translate(text, fromLanguage, toLanguage);

				translator.dispose();

				return translation;
			}
		}
	}
}