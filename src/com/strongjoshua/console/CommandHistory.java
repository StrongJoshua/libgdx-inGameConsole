package com.strongjoshua.console;

import com.badlogic.gdx.utils.Array;

class CommandHistory {
    private final Array<String> commands = new Array<>(true, 20);
    private int index;

    public void store(String command) {
        if (commands.size > 0 && isLastCommand(command)) {
            return;
        }
        commands.insert(0, command);
        indexAtBeginning();
    }

    public String getPreviousCommand() {
        if (commands.size == 0) {
            return "";
        } else if (index >= commands.size) {
            indexAtEnd();
            return commands.get(commands.size - 1);
        }
        return commands.get(index++);
    }

    public String getNextCommand() {
        index--;
        if (commands.size <= 1 || index < 0) {
            indexAtBeginning();
            return "";
        }
        return commands.get(index);
    }

    private boolean isLastCommand(String command) {
        return command.equals(commands.first());
    }

    private void indexAtEnd() {
        index = commands.size - 1;
    }

    private void indexAtBeginning() {
        index = 0;
    }
}
