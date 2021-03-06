/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package as2.app;

import as2.list.LinkedList;

import static as2.utilities.StringUtils.join;
import static as2.utilities.StringUtils.split;
import static as2.app.MessageUtils.getMessage;

import org.apache.commons.text.WordUtils;

public class App {
    public static void main(String[] args) {
        LinkedList tokens;
        tokens = split(getMessage());
        String result = join(tokens);
        System.out.println(WordUtils.capitalize(result));
    }
}
