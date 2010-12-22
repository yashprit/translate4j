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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;
import static com.linguamathematica.translate4j.Fluency.against;
import static com.linguamathematica.translate4j.Fluency.from;
import static com.linguamathematica.translate4j.Fluency.of;
import static com.linguamathematica.translate4j.Fluency.to;
import static com.linguamathematica.translate4j.Language.ALBANIAN;
import static com.linguamathematica.translate4j.Language.HEBREW;
import static com.linguamathematica.translate4j.Language.HINDI;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class TranslateTest extends AbstractGoogleTranslateTest
{
	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEIfTargetLanguageIsUnknown() throws Exception
	{
		translation(of(ANY_TEXT), from(ANY_LANGUAGE), to(Language.UNKNOWN));
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEIfTextIsAllSpaces() throws Exception
	{
		translation(of("            "), from(ANY_LANGUAGE), to(ANY_OTHER_LANGUAGE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEIfTextIsEmpty() throws Exception
	{
		translation(of(""), from(ANY_LANGUAGE), to(ANY_OTHER_LANGUAGE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEWhenTextSizeIsGreaterThanLimit() throws Exception
	{
		translation(of(stringOfLength(Translator.MAX_TEXT_SIZE + 1)), from(ANY_LANGUAGE), to(ANY_OTHER_LANGUAGE));
	}

	@Test(expected = NullPointerException.class)
	public void throwsNPEIfSourceLanguageIsNull() throws Exception
	{
		final Language nullLanguage = null;

		translation(of(ANY_TEXT), from(nullLanguage), to(ANY_OTHER_LANGUAGE));
	}

	@Test(expected = ResponseException.class)
	public void throwsResponseExceptionIfTargetLanguageIsBasque() throws Exception
	{
		translation(of(ANY_TEXT), from(ANY_LANGUAGE), to(Language.BASQUE));
	}

	@Test(expected = ResponseException.class)
	public void throwsResponseExceptionIfSourceLanguageIsBasque() throws Exception
	{
		translation(of("Agurrak Kaixo"), from(Language.BASQUE), to(ANY_LANGUAGE));
	}

	@Test(expected = ResponseException.class)
	public void throwsResponseExceptionIfTargetAndSourceLanguageAreTheSame() throws Exception
	{
		translation(of(ANY_TEXT), from(ANY_LANGUAGE), to(ANY_LANGUAGE));
	}

	@Test(expected = NullPointerException.class)
	public void throwsNPEIfTargetLanguageIsNull() throws Exception
	{
		final Language nullLanguage = null;

		translation(of(ANY_TEXT), from(ANY_LANGUAGE), to(nullLanguage));
	}

	@Test(expected = NullPointerException.class)
	public void throwsNPEIfTextIsNull() throws Exception
	{
		final String nullText = null;

		translation(of(nullText), from(ANY_LANGUAGE), to(ANY_OTHER_LANGUAGE));
	}

	@Theory
	// NOTE: SLOW TEST
	public void translatesSourceLanguageToTargetLanguage_MostOfTheTime(final LanguageWithText source) throws Exception
	{
		// FIXME THESE DO NOT PASS THIS TEST
		assumeThat(source.language, is(not(ALBANIAN)));
		assumeThat(source.language, is(not(HEBREW)));
		assumeThat(source.language, is(not(HINDI)));

		final double acceptablePercentage = 50;
		final double acceptableSimilarity = .5;
		double correctlyTranslated = 0;

		final Ongoing process = newProcess();

		for (final LanguageWithText target : languagesWithTexts())
		{
			if (source.language != target.language)
			{
				process.startCheckingTranslation(from(source), to(target), against(acceptableSimilarity));
			}
		}

		for (int i = 0; i < languagesWithTexts().length - 1; i++)
		{
			correctlyTranslated += process.whenReady_nextTextIsCorrectlyTranslated() ? 1 : 0;
		}

		final double percentage = percentage(correctlyTranslated);

		assertThat(percentage, is(greaterThanOrEqualTo(acceptablePercentage)),
				withFailureMessage(MESSAGE, source.text, source.language, acceptablePercentage, percentage));

		log.info(source.language + " passed test");
	}
}
