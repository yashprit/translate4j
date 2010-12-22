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
import static com.linguamathematica.translate4j.Fluency.of;
import static com.linguamathematica.translate4j.Language.ENGLISH;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class DetectTest extends AbstractGoogleTranslateTest
{
	@Test
	public void defaultsToEnglishIfTextIsInUnsupportedLanguage() throws Exception
	{
		final String inuitText = "tusaatsiarunnanngittualuujunga";

		assertThat(detectedLanguage(of(inuitText)), is(ENGLISH),
				withFailureMessage("should have been detected as English"));
	}

	@Test
	// NOTE: SLOW TEST
	public void detectsLanguageWhenItsKnown_MostOfTheTime() throws Exception
	{
		final double acceptablePercentage = 90;
		double detectedLanguages = 0;

		for (final LanguageWithText source : languagesWithTexts())
		{
			detectedLanguages += detectedLanguage(of(source.text)) == source.language ? 1 : 0;
		}

		final double percentage = percentage(detectedLanguages);

		assertThat(
				percentage,
				is(greaterThanOrEqualTo(acceptablePercentage)),
				withFailureMessage(
						"should have correctly detected at least [%5.2f]%% of the languages but was [%5.2f]%%",
						acceptablePercentage, percentage));

	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEIfTextIsAllSpaces() throws Exception
	{
		detect("            ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEIfTextIsEmpty() throws Exception
	{
		detect("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEWhenTextSizeIsGreaterThanLimit() throws Exception
	{
		detect(stringOfLength(Translator.MAX_TEXT_SIZE + 1));
	}

	@Test(expected = NullPointerException.class)
	public void throwsNPEIfTextIsNull() throws Exception
	{
		detect(null);
	}
}
