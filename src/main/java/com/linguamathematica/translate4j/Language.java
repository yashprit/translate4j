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

import java.util.Map;

import static java.lang.String.format;

import static com.linguamathematica.translate4j.Base.ensure;
import static com.linguamathematica.translate4j.Base.ensureNotNull;
import static com.linguamathematica.translate4j.Base.newMap;

/**
 * Enumeration of supported languages plus a constant representing an unknown language.
 */
public enum Language
{
	//@formatter:off
	AFRIKAANS("af"),
	ALBANIAN("sq"),
	ARABIC("ar"),
	BASQUE("eu"),
	BELARUSIAN("be"),
	BULGARIAN("bg"),
	CATALAN("ca"),
	CHINESE_SIMPLIFIED("zh-CN"),
	CHINESE_TRADITIONAL("zh-TW"),
	CROATIAN("hr"),
	CZECH("cs"),
	DANISH("da"),
	DUTCH("nl"),
	ENGLISH("en"),
	ESTONIAN("et"),
	FILIPINO("fil"),
	FINNISH("fi"),
	FRENCH("fr"),
	GALICIAN("gl"),
	GERMAN("de"),
	GREEK("el"),
	HATIAN_CREOLE("ht"),
	HEBREW("iw"),
	HINDI("hi"),
	HUNGARIAN("hu"),
	ICELANDIC("is"),
	INDONESIAN("id"),
	IRISH("ga"),
	ITALIAN("it"),
	JAPANESE("ja"),
	LATVIAN("lv"),
	LITHUANIAN("lt"),
	MACEDONIAN("mk"),
	MALAY("ms"),
	MALTESE("mt"),
	NORWEGIAN("no"),
	PERSIAN("fa"),
	POLISH("pl"),
	PORTUGUESE("pt"),
	ROMANIAN("ro"),
	RUSSIAN("ru"),
	SERBIAN("sr"),
	SLOVAK("sk"),
	SLOVENIAN("sl"),
	SPANISH("es"),
	SWAHILI("sw"),
	SWEDISH("sv"),
	THAI("th"),
	TURKISH("tr"),
	UKRAINIAN("uk"),
	UNKNOWN("?"),
	VIETNAMESE("vi"),
	WELSH("cy"),
	YIDDISH("yi");
	//@formatter:on

	private final String code;

	private Language(final String code)
	{
		this.code = code;
	}

	/**
	 * Gets the ISO 639-1 language code.
	 * 
	 * @return the ISO 639-1 language code.
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * 
	 * 
	 * @return the code
	 */
	@Override
	public String toString()
	{
		return format("%s [%s]", name(), code);
	}

	/**
	 * Returns a Language constant whose name or language code corresponds with the specified key, ignoring case. If the
	 * key cannot be matched to a constant, the Language.UNKNOWN constant is returned.
	 * 
	 * This is a lenient alternative to {@link Language#valueOf(String)}
	 * 
	 * @param languageKey
	 *            the language key
	 * @return the language
	 * 
	 * @throws NullPointerException
	 *             if the key is null
	 * 
	 * @throws IllegalArgumentException
	 *             if the key is empty or made up of blank spaces
	 */
	public static Language from(final String languageKey)
	{
		return Mapper.map(languageKey);
	}

	private static class Mapper
	{
		private static final Map<String, Language> codeToLanguage = buildMap();

		private static Map<String, Language> buildMap()
		{
			final Map<String, Language> map = newMap();

			for (final Language language : Language.values())
			{
				map.put(language.code.toUpperCase(), language);
				map.put(language.name(), language);
			}

			return map;
		}

		private static Language map(final String key)
		{
			ensureNotNull(key, "Language key");
			ensure(key.trim().length() != 0, "Language key cannot be empty");

			final Language language = codeToLanguage.get(key.toUpperCase());

			return language != null ? language : UNKNOWN;
		}
	}
}