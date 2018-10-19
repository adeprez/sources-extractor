# SourcesExtractor.java
A Java class to extract sources from some text and replace it with reference number. A table of references can then be generated.



Find below a few examples of the usage of this class:

```java
public static void main(String[] args) {

	String text = "This is some dummy text. Source: a nice book. Here is more text.\n"
				+ "This class provide a way to parse text in order to extract inline source references (Source : SourcesExtractor Javadoc)";

	SourcesExtractor src = new SourcesExtractor();

	String editedText = src.parse(text, SourcesExtractor::htmlReferenceFormat);
		
	String sources = src.formatSources(SourcesExtractor::htmlReferenceFormat, Function.identity(),
				"<br/>");

	System.out.println(text);
	System.out.println();

	System.out.println(editedText);
	System.out.println();

	System.out.println(sources);

	String text2 = "Here is some other text...source:just an inline source";

	System.out.println(text2);
	System.out.println();

	System.out.println(src.parse(text2, i -> " (" + i + ")"));
	System.out.println();

	System.out.println(src.formatSources(i -> i + ". ", Function.identity(), "\n"));

}
```
