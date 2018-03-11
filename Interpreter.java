/*

Copyright (C) 2015-2018 Jan Christian Rohde

This file is part of FunctionalCalculator.

FunctionalCalculator is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or (at your
option) any later version.

FunctionalCalculator is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
License for more details.

You should have received a copy of the GNU General Public License
along with FunctionalCalculator. If not, see http://www.gnu.org/licenses/.

*/

import java.math.BigInteger;

public class Interpreter {
    public Interpreter() {
        parser = new FuParser();
    }

    public MyFunction interprete(String script, java.util.Map<String, MyFunction> args) throws MyParserException {
        String content = load(script);

        commandEnd = 0;
        parser.setVariables(args);
        return execute(content);
    }

    private int commandBegin, commandEnd;

    private String load(String script) {
        try {
            StringBuilder builder = new StringBuilder();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(FunctionalString.completeRelativePath(script)));

            String res = "";
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                res += line;
            }

            reader.close();
            res = res.replace( " ", "");
            return res;
        } catch (java.io.IOException e) {
            return "";
        }
    }

    private MyFunction execute(String content) throws MyParserException {
        commandBegin = commandEnd;

        MyFunction res = null;
        while (commandEnd < content.length()) {
            if (commandEnd == commandBegin+1 && content.substring(commandBegin, commandEnd+1).equals("if")) {
                if (isConditionSatisfied(content)) {
                    res = execute(content);
                    skipRemainingElses(content);
                }
                else {
                    runTillBrackedEnd(content);

                    boolean foundElse = false;
                    while (!foundElse && commandEnd < content.length() - 7 && content.substring(commandEnd + 1, commandEnd + 7).equals("elseif")) {
                        commandEnd += 6;
                        if (isConditionSatisfied(content)) {
                            res = execute(content);
                            skipRemainingElses(content);
                            foundElse = true;
                        }
                        else {
                            runTillBrackedEnd(content);
                        }
                    }
                    if (!foundElse && commandEnd < content.length() - 6 && content.substring(commandEnd + 1, commandEnd + 6).equals("else{")) {
                        commandEnd += 6;
                        res = execute(content);
                    }
                }

                commandBegin = commandEnd + 1;
            }
            else if (commandEnd == commandBegin + 4 && content.substring(commandBegin, commandEnd+1).equals("while")) {
                if (isConditionSatisfied(content)) {
                    int loopBegin = commandBegin;
                    res = execute(content);
                    commandBegin = commandEnd = loopBegin;
                }
                else {
                    runTillBrackedEnd(content);
                    commandBegin = commandEnd + 1;
                }
            }
            else if (content.charAt(commandEnd) == ';' || content.charAt(commandEnd) == ':') {
                String commandLine = content.substring(commandBegin, commandEnd);
                boolean foundComment = false;
                boolean goFurther = false;
                do {
                    foundComment = commandLine.contains("/*");
                    if (foundComment) {
                        int begin = commandLine.indexOf("/*");
                        int end = commandLine.indexOf("*/");
                        if (end < begin)
                            goFurther = true;
                        String help = commandLine.substring(0, begin);
                        begin = commandLine.indexOf("*/") + 2;
                        commandLine = help + commandLine.substring(begin);
                    }
                } while(foundComment);
                if (goFurther) {
                    commandEnd++;
                    continue;
                }
                res = parser.parseFunction(commandLine);
                if (content.charAt(commandEnd) == ':') {
                    FunctionalCalculator.printOut(content.substring(commandBegin, commandEnd), res.print(6));
                }
                commandBegin = commandEnd + 1;
            }
            else if (content.charAt(commandEnd) == '}') break;

            commandEnd++;
        }
        return res;
    }

    private void skipRemainingElses(String content) {
        while (commandEnd < content.length() - 7 && content.substring(commandEnd + 1, commandEnd + 7).equals("elseif")) {
            commandEnd += 7;

            while (content.charAt(commandEnd) != '{') {
                commandEnd++;
            }
            commandEnd++;

            runTillBrackedEnd(content);
        }
        if (commandEnd < content.length() - 6 && content.substring(commandEnd + 1, commandEnd + 6).equals("else{")) {
            commandEnd += 6;
            runTillBrackedEnd(content);
        }
    }

    private boolean isConditionSatisfied(String content)  throws MyParserException{
        commandEnd++;
        int begin = commandEnd;
        while (content.charAt(commandEnd) != '{') {
            commandEnd++;
        }
        commandEnd++;
        return FunctionArithmetics.notEq(parser.parseFunction(content.substring(begin, commandEnd-1)),
                new ConstantFunction(new IntegerStrategy(BigInteger.ZERO)),0.00001).eval(0.0).getDouble() != 0.0;
    }

    private void runTillBrackedEnd(String content) {
        int wgt = 0;
        while (commandEnd < content.length()) {
            if (content.charAt(commandEnd) == '}') wgt++;
            if (content.charAt(commandEnd) == '{') wgt--;
            if (wgt == 1) {
                break;
            }
            commandEnd++;
        }
    }

    private FuParser parser;
}
