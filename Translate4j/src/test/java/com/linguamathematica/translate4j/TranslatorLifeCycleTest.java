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

import static com.linguamathematica.translate4j.Translator.translator;

import org.junit.Test;

public class TranslatorLifeCycleTest extends AbstractGoogleTranslateTest
{
	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEWhenProvidedWithAnEmptyKey() throws Exception
	{
		translator("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIAEWhenProvidedWithANonAlphanumericKey() throws Exception
	{
		translator("wr0ng k3y;");
	}

	@Test(expected = IllegalStateException.class)
	public void throwsISEWhenAttemptingToUseAfterDisposed() throws Exception
	{
		final Translator translator = translator(TEST_API_KEY);
		translator.dispose();
		translator.detect(ANY_TEXT);
	}

	@Test(expected = NullPointerException.class)
	public void throwsNPEWhenProvidedWithANullKey() throws Exception
	{
		translator(null);
	}
}
