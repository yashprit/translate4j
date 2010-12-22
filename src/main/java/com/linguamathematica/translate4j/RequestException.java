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

// TODO: Auto-generated Javadoc
/**
 * Thrown if there's an error when requesting the service
 */
public class RequestException extends RuntimeException
{
	private static final long serialVersionUID = 359042848448288034L;

	/**
	 * Instantiates a new request exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public RequestException(final String message, final Exception cause)
	{
		super(message, cause);
	}

}
