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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

class arguments {
    public String arg0;
    public String arg1;
    public String arg2;
    public String arg3;
}

class functionMapGenerator {
    private static Map<String, arguments> functionMap = new HashMap<String, arguments>();

    private void insertFunction(String name, String arg0, String arg1, String arg2, String arg3) {
        arguments arg = new arguments();
        arg.arg0 = arg0;
        arg.arg1 = arg1;
        arg.arg2 = arg2;
        arg.arg3 = arg3;
        functionMap.put(name, arg);
    }

    public functionMapGenerator() {
        insertFunction("acos", "x", "", "", "");
        insertFunction("asin", "x", "", "", "");
        insertFunction("atan", "x", "", "", "");
        insertFunction("cos", "x", "", "", "");
        insertFunction("cosh", "x", "", "", "");
        insertFunction("cot", "x", "", "", "");
        insertFunction("cubeRoot", "x", "", "", "");
        insertFunction("derivative", "f(x)", "", "", "");
        insertFunction("exp", "x", "", "", "");
        insertFunction("gcd", "a", "b", "", "");
        insertFunction("integral", "f(x)", "a", "b", "");
        insertFunction("lcm", "a", "b", "", "");
        insertFunction("ln", "x", "", "", "");
        insertFunction("log", "a", "b", "", "");
        insertFunction("matrix", "rows", "columns", "", "");
        insertFunction("nextPrime", "n", "", "", "");
        insertFunction("normal", "f(x)", "x0", "", "");
        insertFunction("phi", "n", "", "", "");
        insertFunction("precision", "precision", "", "", "");
        insertFunction("prime", "n", "", "", "");
        insertFunction("sin", "x", "", "", "");
        insertFunction("sinh", "x", "", "", "");
        insertFunction("sqrt", "x", "", "", "");
        insertFunction("tan", "x", "", "", "");
        insertFunction("tanh", "x", "", "", "");
        insertFunction("tangent", "f(x)", "x0", "", "");
        insertFunction("<=", "a", "b", "", "");
        insertFunction("<", "a", "b", "", "");
        insertFunction("==", "a", "b", "", "");
        insertFunction("!=", "a", "b", "", "");
        insertFunction(">", "a", "b", "", "");
        insertFunction(">=", "a", "b", "", "");
        insertFunction("faculty", "a", "", "", "");
        insertFunction("NOT", "x", "", "", "");
        insertFunction("AND", "a", "b", "", "");
        insertFunction("OR", "a", "b", "", "");
        insertFunction("condition", "case", "conclusion", "alternative", "");
        insertFunction("getEntry of a matrix", "matrix", "row", "column", "");
        insertFunction("setEntry of a matrix", "matrix", "row", "column", "value");
        insertFunction("writeFile", "file name", "content", "", "");
        insertFunction("readFile", "a", "", "", "");
        insertFunction("deleteFile", "a", "", "", "");
        insertFunction("interpret", "script", "", "", "");
        insertFunction("plot", "f(x)", "g(x)", "h(x)", "i(x)");
        insertFunction("plotComplex", "f(x)", "", "", "");
        insertFunction("variable", "name", "", "", "");
        insertFunction("value of", "value", "", "", "");
        insertFunction("symmetry", "f(x)", "", "", "");
        insertFunction("gamma", "x", "", "", "");
        insertFunction("digamma", "x", "", "", "");
        insertFunction("trigamma", "x", "", "", "");
        insertFunction("max", "a", "b", "", "");
        insertFunction("min", "a", "b", "", "");
        insertFunction("bessel1", "n", "x", "", "");
        insertFunction("bessel2", "n", "x", "", "");
        insertFunction("ceil", "x", "", "", "");
        insertFunction("floor", "x", "", "", "");
        insertFunction("round", "x", "", "", "");
        insertFunction("abs", "x", "", "", "");
        insertFunction("frac", "x", "", "", "");
        insertFunction("conj", "z", "", "", "");
        insertFunction("arg", "z", "", "", "");
        insertFunction("re", "z", "", "", "");
        insertFunction("im", "z", "", "", "");
        insertFunction("log2", "x", "", "", "");
        insertFunction("isconst", "x", "", "", "");
        insertFunction("binomial", "n", "m", "", "");
    }

    public ArrayList<String> getArguments(String name) {
        arguments arg = functionMap.get(name);
        ArrayList<String> argList = new ArrayList<String>();
        argList.add(arg.arg0);
        if (arg.arg1.length() > 0) {
            argList.add(arg.arg1);
            if (arg.arg2.length() > 0) {
                argList.add(arg.arg2);
                if (arg.arg3.length() > 0) {
                    argList.add(arg.arg3);
                }
            }
        }
        return argList;
    }
}

abstract class scalarDialogGenerator {
    protected functionMapGenerator functionGen;
    protected String result;
    protected String fName;
    protected String attribute;
    protected int argSize = 0;

    protected abstract void setCenterPanel(final JDialog dialog, final ArrayList<String> arguments);

    protected abstract void writeResult();

    private String printArguments(String functionName, final ArrayList<String> arguments) {
        String labelStr = "";
        if (functionName.equals("integral")) labelStr = " of f(x) from a to b";
        else if (functionName.equals("derivative")) labelStr = " of f(x)";
        else if (functionName.equals("normal") || functionName.equals("tangent")) labelStr = " of f(x) at x0";
        else if (functionName.equals("log")) labelStr = "<sub>a</sub>(b)";
        else {
            labelStr = "(";
            for (int i = 0; i < arguments.size() - 1; i++) {
                labelStr = labelStr + arguments.get(i) + ",";
            }
            labelStr = labelStr + arguments.get(arguments.size() - 1) + ")";
        }
        return labelStr;
    }

    public String exec(String functionName, String programName) {
        int separator = functionName.indexOf(";");
        if (functionName.indexOf(";") > -1) {
            attribute = functionName.substring(separator + 1, functionName.length());
            functionName = functionName.substring(0, separator);
        } else {
            attribute = "";
        }
        fName = functionName;
        result = "";
        final ArrayList<String> arguments = functionGen.getArguments(functionName);
        argSize = arguments.size();
        final CalcDialog dialog = new CalcDialog();
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setTitle(programName);

        String labelStr = "<html>";
        if (isLogicalOperation(functionName)) {
            labelStr += "a " + CalcDialog.escapeHTML(functionName) + " b";
        } else if (functionName.equals("faculty")) {
            labelStr += "a!";
        } else {
            if (functionName.equals("matrix") || functionName.equals("precision")) labelStr = labelStr + "Set ";
            else if (functionName.equals("getEntry of a matrix")) labelStr = labelStr + "getEntry";
            else if (functionName.equals("setEntry of a matrix")) labelStr = labelStr + "setEntry";
            else if (!functionName.contains("File")
                    && !functionName.equals("interpret")
                    && !functionName.contains("plot")
                    && !functionName.equals("variable")
                    && !functionName.contains("value"))
                labelStr = labelStr + "Compute ";
            if (!functionName.contains("getEntry") && !functionName.contains("setEntry")) {
                labelStr = labelStr + CalcDialog.escapeHTML(functionName);
            }
            if (!functionName.equals("precision")
                    && !functionName.contains("plot")
                    && !functionName.contains("variable")
                    && !functionName.contains("value")) {
                labelStr = labelStr + printArguments(functionName, arguments);
            }
        }
        if (attribute.length() > 0) {
            labelStr = labelStr + " " + attribute;
        }
        labelStr = labelStr + " for</html>";
        JLabel label = new JLabel(labelStr);

        Panel topPanel = new Panel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(label);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        dialog.add(topPanel, BorderLayout.NORTH);

        setCenterPanel(dialog, arguments);


        Panel closePanel = new Panel();
        closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.LINE_AXIS));
        closePanel.add(Box.createHorizontalGlue());
        JButton okButton = new JButton("Ok");
        closePanel.add(okButton);
        closePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        JButton noButton = new JButton("No");
        closePanel.add(noButton);
        closePanel.add(Box.createRigidArea(new Dimension(10, 0)));

        Panel BottomPanel = new Panel();
        BottomPanel.setLayout(new BorderLayout());
        BottomPanel.add(closePanel, BorderLayout.CENTER);
        BottomPanel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.SOUTH);

        dialog.add(BottomPanel, BorderLayout.SOUTH);
        dialog.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.WEST);
        dialog.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.EAST);

        noButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent event) {
                                           dialog.dispose();
                                       }
                                   }
        );

        okButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent event) {
                                           writeResult();
                                           dialog.dispose();
                                       }
                                   }
        );

        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);
        return result;
    }

    protected boolean isLogicalOperation(String function) {
        return function == "<" || function == "<=" || function == ">" || function == ">="
                || function == "==" || function == "!=" || function == "AND" || function == "OR";
    }
}

class scalarDialogEditGenerator extends scalarDialogGenerator {
    private final ArrayList<JTextField> entry;

    public scalarDialogEditGenerator() {
        entry = new ArrayList<JTextField>();
        functionGen = new functionMapGenerator();
        result = "";
    }

    protected void setCenterPanel(final JDialog dialog, final ArrayList<String> arguments) {
        Panel Tabs = new Panel();
        Tabs.setLayout(new GridLayout(arguments.size(), 2));
        entry.clear();
        for (int i = 0; i < arguments.size(); i++) {
            entry.add(new JTextField());
        }

        String str;

        for (int i = 0; i < arguments.size(); i++) {
            str = arguments.get(i);
            Tabs.add(new JLabel(str + " = ", SwingConstants.RIGHT));
            Tabs.add(entry.get(i));
        }
        dialog.add(Tabs, BorderLayout.CENTER);
    }

    protected void writeResult() {
        if (isLogicalOperation(fName)) {
            result = entry.get(0).getText() + fName + entry.get(1).getText();
        } else if (fName.equals("value of")) {
            result = entry.get(0).getText();
        } else if (fName == "faculty") {
            result = entry.get(0).getText() + "!";
        } else if (fName == "condition") {
            result = entry.get(0).getText() + "?" + entry.get(1).getText() + "|" + entry.get(2).getText();
        } else {
            if (fName == "getEntry of a matrix") result = "getEntry";
            else if (fName == "setEntry of a matrix") result = "setEntry";
            else result = fName;
            result += "(";
            for (int i = 0; i < argSize - 1; i++) {
                result = result + entry.get(i).getText();
                result = result + ",";
            }
            result = result + entry.get(argSize - 1).getText() + ")";
        }
    }
}

class scalarDialogSpinGenerator extends scalarDialogGenerator {
    private final ArrayList<JSpinner> entry;
    private int startVal, minVal, maxVal;

    public scalarDialogSpinGenerator() {
        entry = new ArrayList<JSpinner>();
        functionGen = new functionMapGenerator();
        result = "";
    }

    protected void setCenterPanel(final JDialog dialog, final ArrayList<String> arguments) {
        Panel Tabs = new Panel();
        Tabs.setLayout(new GridLayout(arguments.size(), 2));
        entry.clear();
        for (int i = 0; i < arguments.size(); i++) {
            entry.add(new JSpinner(new SpinnerNumberModel(startVal, minVal, maxVal, 1)));
        }

        String str;

        for (int i = 0; i < arguments.size(); i++) {
            str = arguments.get(i);
            Tabs.add(new JLabel(str + " = ", SwingConstants.RIGHT));
            Tabs.add(entry.get(i));
        }
        dialog.add(Tabs, BorderLayout.CENTER);
    }

    protected void writeResult() {
        for (int i = 0; i < argSize - 1; i++) {
            result = result + String.valueOf((Integer) entry.get(i).getValue());
            result = result + ",";
        }
        result = result + String.valueOf((Integer) entry.get(argSize - 1).getValue());
    }

    public String generate(String functionName, String programName, int sVal, int miVal, int maVal) {
        startVal = sVal;
        minVal = miVal;
        maxVal = maVal;
        return exec(functionName, programName);
    }

}
