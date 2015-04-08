## Overview ##

Translate4j is a Java client for Version 2 of the
[Google Translate API](http://code.google.com/apis/language/translate/overview.html) for use on server side.

It needs a [Google API key](http://code.google.com/apis/language/translate/v2/getting_started.html#intro) to be used, this is [free and takes a couple of minutes](https://www.google.com/accounts/NewAccount) to obtain; if you've got a [Google account](https://www.google.com/accounts/NewAccount) this is just [two clicks](https://code.google.com/apis/console/) away.

## Installation ##

Add [translate4j-all-x.x.x.jar](http://code.google.com/p/translate4j/downloads/list) to your classpath, [Java 1.5+](http://www.oracle.com/technetwork/java/javase/downloads/index.html) is required.

## Usage ##

```
import static java.lang.System.out;
import static com.linguamathematica.translate4j.Language.ENGLISH;
import static com.linguamathematica.translate4j.Language.SPANISH;
import static com.linguamathematica.translate4j.Language.UNKNOWN;
import static com.linguamathematica.translate4j.Translator.translator;

import com.linguamathematica.translate4j.Translator;

public class Main
{
	static final String YOUR_API_KEY = "YOUR API KEY";

	public static void main(final String[] args)
	{
		// Creates a translation instance
		final Translator translator = translator(YOUR_API_KEY);

		// Translates a text from English To Spanish
		out.println(translator.translate("Hello World", ENGLISH, SPANISH));

		// Translates a text from English To Spanish, auto detecting the text's language
		out.println(translator.translate("Hello World", UNKNOWN, SPANISH));

		// Detects text's language
		out.println(translator.detect("Hello World"));

		// Releases resources when done
		translator.dispose();
	}
}
```