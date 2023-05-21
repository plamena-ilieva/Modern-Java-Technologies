package bg.sofia.uni.fmi.mjt.spotify.command;

import java.util.Arrays;
import java.util.List;

public class CommandCreator {
    private static final String SEPARATOR = " ";

    public static Command newCommand(String clientInput) {
        List<String> tokens = Arrays.stream(clientInput.split(SEPARATOR)).toList();
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        return new Command(tokens.get(0), args);
    }
}