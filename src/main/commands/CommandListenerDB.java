package main.commands;

import main.db.RegisterEvent;

import java.util.Map;

public class CommandListenerDB {

    public static void registerEventOnDB(String command, String optionName, String optionValue, Map<String, String> info) {
            info.remove("Type");
            info.put("Command", command);
            info.put("OptionName", optionName);
            info.put("OptionValue", optionValue);

//        info.forEach((key, value) -> System.out.println(key + " " + value));
            RegisterEvent.registerEventOnDB(info);
        }
    }
