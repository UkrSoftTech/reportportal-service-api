/*
 * Copyright 2018 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.ta.reportportal.util;

import com.google.common.base.CharMatcher;

import java.util.function.Predicate;

/**
 * Set of common predicates
 *
 * @author Andrei Varabyeu
 */
public class Predicates {

	private static final String SPECIAL_CHARACTERS = "-/@#$%^&_+=()";

	private Predicates() {
		//statics only
	}

	/**
	 * Checker whether string contains special characters only
	 */
	public static final Predicate<String> SPECIAL_CHARS_ONLY = str -> CharMatcher.anyOf(SPECIAL_CHARACTERS).matchesAllOf(str);

	//	/**
	//	 * Checks if the test item is suitable for indexing in analyzer.
	//	 */
	//	public static final Predicate<TestItem> CAN_BE_INDEXED = testItem -> testItem != null && testItem.getIssue() != null
	//			&& !TestItemIssueType.TO_INVESTIGATE.getLocator().equals(testItem.getIssue().getIssueType()) && !testItem.getIssue()
	//			.isIgnoreAnalyzer();
}
