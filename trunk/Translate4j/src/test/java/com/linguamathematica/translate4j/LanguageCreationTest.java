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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class LanguageCreationTest
{
	@DataPoints
	public static final Language[] languages = Language.values();

	@Theory
	public void createsLanguageFromCode(final Language language) throws Exception
	{
		assertThat(Language.from(language.getCode()), is(language));
	}

	@Theory
	public void createsLanguageFromCodeInUpperCase(final Language language) throws Exception
	{
		assertThat(Language.from(language.getCode().toUpperCase()), is(language));
	}

	@Theory
	public void createsLanguageFromNameInLowerCase(final Language language) throws Exception
	{
		assertThat(Language.from(language.name().toLowerCase()), is(language));
	}

	@Test
	public void createsUnknownLanguageWhenKeyMatchesNoSupportedLanguage() throws Exception
	{
		assertThat(Language.from("eskimo"), is(equalTo(Language.UNKNOWN)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEWhenKeyIsEmpty() throws Exception
	{
		Language.from("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEWhenKeyIsMadeUpOfBlankSpaces() throws Exception
	{
		Language.from("    ");
	}

	@Test(expected = NullPointerException.class)
	public void throwsNPEWhenKeyIsNull() throws Exception
	{
		Language.from(null);
	}
}
