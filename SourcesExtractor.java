import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a way to parse text in order to extract inline source
 * references. It supports creation of a list of references from several texts
 * by reusing the same object with multiple calls to
 * {@link #parse(String, Function)}. <br/>
 * <br/>
 * <b>Example of usage:</b><br/>
 * <code>
 * String text = "This is some dummy text. Source: my own code.";<br/>
 * SourcesExtractor src = new SourcesExtractor();<br/>
 * String editedText = src.parse(text, n -> " (" + n + ")");<br/>
 * String sources = src.formatSources(n -> "(" + n + ") ", Function.identity(), "\n");<br/>
 * </code>
 * 
 * @author Alexis DEPREZ (contact@adeprez.com)
 * @version 1.0
 *
 */
public class SourcesExtractor {

	/**
	 * Pattern used to match sources from text.
	 */
	private static final Pattern PATTERN = Pattern.compile("(?:^|[\\s\\.(]+)sources?\\s*:\\s*([^.\\n]*)",
			Pattern.CASE_INSENSITIVE);

	/**
	 * List of sources extracted from given texts.
	 */
	private final List<String> sources;

	/**
	 * Constructor for source extractor. You can parse several texts with the
	 * same {@link SourcesExtractor} object in order to build an unified source
	 * table with increasing numbers.
	 */
	public SourcesExtractor() {
		sources = new ArrayList<>();
	}

	/**
	 * Parse the given text and extract the sources to populate the internal
	 * {@link #sources} list.
	 * 
	 * @param text
	 *            the text to process.
	 * @param referenceFormatter
	 *            a function used to generate a chain used to replace the source
	 *            by a new text generated from the source number (starting from
	 *            1). It allows you to format your reference numbers freely.
	 * @return a new version of the text with sources replaced with the
	 *         formatted reference number.
	 * @see #htmlReferenceFormat(Integer)
	 */
	public String parse(String text, Function<Integer, String> referenceFormatter) {

		StringBuilder edited = new StringBuilder();

		// Create a matcher for the given text
		Matcher match = PATTERN.matcher(text);

		// Keep the index of the end of last parsed source
		int lastEndIndex = 0;

		// For each source found
		while (match.find()) {
			
			// Append text before the source
			edited.append(text.substring(lastEndIndex, match.start()));

			// Append formatted source number
			edited.append(referenceFormatter.apply(sources.size() + 1));
			
			// Store the index of the end of parsed source
			lastEndIndex = match.end();
			
			// Add extracted source text to internal source list, and replace
			// ')' to stay consistent with '(' escaped by the pattern
			sources.add(text.substring(match.start(1), match.end(1)).replaceAll("\\)", ""));
		}
		
		// Add the remaining text
		edited.append(text.substring(lastEndIndex));
		
		return edited.toString();
	}

	/**
	 * @return the list of references parsed by this object.
	 * @see #parse(String, Function)
	 */
	public List<String> getSources() {
		return sources;
	}

	/**
	 * Build a formatted text of sources parsed by this object.
	 * 
	 * @param referenceFormatter
	 *            a function used to generate a chain used to format the source
	 *            number (starting from 1). It allows you to format your
	 *            reference numbers freely.
	 * @param sourceFormatter
	 *            a function used to transform the source text. Use
	 *            {@link Function#identity()} supplier to keep it as it is.
	 * @param joinDelimiter
	 *            the delimiter chain inserted between each source.
	 * @return a chain with all sources formatted with the given arguments.
	 * @see #htmlReferenceFormat(Integer)
	 */
	public String formatSources(Function<Integer, String> referenceFormatter, Function<String, String> sourceFormatter,
			CharSequence joinDelimiter) {

		StringBuilder formattedSources = new StringBuilder();

		// For each reference
		for (int i = 0; i < sources.size(); i++) {

			// Append separator if needed
			if (i > 0) {
				formattedSources.append(joinDelimiter);
			}

			// Append formatted reference number
			formattedSources.append(referenceFormatter.apply(i + 1));

			// Append source text
			formattedSources.append(sourceFormatter.apply(sources.get(i)));
		}

		return formattedSources.toString();
	}

	/**
	 * Format a given reference number as an HTML exponent
	 * 
	 * @param reference
	 *            the reference number
	 * @return the number as an HTML exponent
	 * @see #formatSources(Function, Function, CharSequence)
	 */
	public static String htmlReferenceFormat(Integer reference) {
		return String.format("<sup>%d</sup>", reference);
	}

}
