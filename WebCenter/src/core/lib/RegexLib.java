package core.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.exceptions.NoElementFoundException;

public abstract class RegexLib {
	
	/**
	 * 
	 * @param regex
	 * @param testString
	 * @return
	 * @throws NoElementFoundException 
	 */
	public static String getMatch(final String regex, final String testString) throws NoElementFoundException {
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(testString);
		
		if (matcher.find()) {
			return matcher.group();
		} else {
			throw new NoElementFoundException("Regex: " + regex + " String: " + testString);
		}
	}
	
	/**
	 * 
	 * @param regex
	 * @param testString
	 * @param group
	 * @return
	 * @throws NoElementFoundException
	 */
	public static String getGroupMatch(final String regex, final String testString, final int group) throws NoElementFoundException {
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(testString);
		
		if (matcher.find()) {
			return matcher.group(group);
		} else {
			throw new NoElementFoundException("Regex: " + regex + " Group: " + group + " String: " + testString);
		}
	}
	
	/**
	 * 
	 * @param regex
	 * @param testString
	 * @return
	 * @throws NoElementFoundException
	 */
	public static List<String> getAllMatches(final String regex, final String testString) throws NoElementFoundException {
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(testString);
		
		if (matcher.find()) {
			final List<String> results = new ArrayList<String>();
			results.add(matcher.group());
			
			while (matcher.find()) {
				results.add(matcher.group());
			}
			
			return results;
		} else {
			throw new NoElementFoundException("Regex: " + regex + " String: " + testString);
		}
	}
	
	/**
	 * 
	 * @param regex
	 * @param testString
	 * @param group
	 * @return
	 * @throws NoElementFoundException
	 */
	public static List<String> getAllGroupMatches(final String regex, final String testString, final int group) throws NoElementFoundException {
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(testString);
		
		if (matcher.find()) {
			final List<String> results = new ArrayList<String>();
			results.add(matcher.group(group));
			
			while (matcher.find()) {
				results.add(matcher.group(group));
			}
			
			return results;
		} else {
			throw new NoElementFoundException("Regex: " + regex + " Group: " + group + " String: " + testString);
		}
	}
	
	/**
	 * 
	 * @param regex
	 * @param testString
	 * @param groups
	 * @return
	 * @throws NoElementFoundException
	 */
	public static List<String[]> getAllGroupMatches(final String regex, final String testString, final int... groups) throws NoElementFoundException {
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(testString);
		
		final List<String[]> results = new ArrayList<String[]>();
		
		while (matcher.find()) {
			String[] groupResults = new String[groups.length];
			
			for (int i = 0; i < groups.length; i++) {
				groupResults[i] = matcher.group(groups[i]);
			}
			
			results.add(groupResults);			
		}
		
		if (results.size() > 0) {
			return results;
		} else {
			String groupString = "";
			for (int group : groups) {
				groupString += group + ", ";
			}
			throw new NoElementFoundException("Regex: " + regex + " Groups: " + groupString.trim() + " String: " + testString);
		}
	}
	
	
	
	

}
