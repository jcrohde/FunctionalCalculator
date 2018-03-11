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


import java.awt.Point;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.net.URL;
import java.io.IOException;
import javax.swing.text.Document;

class HelpEditorPane extends JEditorPane {

    private String setTitle(String programName, String title) {
        String content = "<html><h1><font color=\"#0000FF\">" + programName + "</font></h1>";
        content = content + "<h2>" + title + "</h2>";
        return content;
    }

    private String setLinks(String link1, String link2, String link3) {
        String res = "<br><br>";
        res = res + "<a href=\"" + link1 + "\">" + link1 + "</a>";
        res = res + "<br>";
        res = res + "<a href=\"" + link2 + "\">" + link2 + "</a>";
        res = res + "<br>";
        res = res + "<a href=\"" + link3 + "\">" + link3 + "</a>";
        return res;
    }

    public String getDocument(String name, String programName) {
        String content = null;
        if (name.equals("Help")) {
            content = "<html><h1><font color=\"#0000FF\">" + programName + "</font></h1>";
            content = content + programName + " is a Mathematical program, which calculates with functions and which is designed to be functional in the sense of convenience and usability.";
            content = content + " It is designed to be as easy to run as a hand-held calculator. The goal of the project is a neat tool covering the ";
            content = content + "Mathematics at university level and advanced high-school level.<br><br>";
            content = content + programName + " is free software and may be freely copied under the terms of the GNU General Public License 3.<br><br>";
            content = content + programName + " has 75 <a href=\"User-Functions\">User-Functions</a> and boolean / comparision operators in its current release. The User-Functions can have arguments ";
            content = content + "given by matrices, infinitely / complex differentiable functions, complex numbers or arbitrary precision integers. Among the User-Functions you find for example: <ul type=\"bullet\">";
            content = content + "<li>The integral of f(x) from a to b</li>";
            content = content + "<li>The derivative of f(x)</li>";
            content = content + "<li>Ternary Conditional</li>";
            content = content + "<li>The gcd of two integers</li>";
            content = content + "<li>Solve a System of Linear Equations</li>";
            content = content + "</ul>";
            content = content + programName + " has a plotter for complex functions and a plotter for real functions.<br><br>";
            content = content + "Moreover " + programName + " has a <a href=\"Grammar\">Grammar</a>. Thus you can store variables";
            content = content + " and compose and combine several operations and User-functions. In particular you can define your own recursive (meta) functions by using the Ternary Conditional.<br><br>";

            content = content + programName + " has a scripting language based on its grammar. This language is functional and procedural.<br><br>";
            content = content + "In the <a href=\"Settings\">Settings</a> you can select the precision of printed result and the numbers of ";
            content = content + "rows and columns of matrices and systems of linear equations you like to use.<br><br>";
            content = content + "By pressing the enter key, you can use the result of your previous calculations for further computations. Moreover by using &#8593; and the &#8595; tabs, you scroll through your previous entries.";
            content = content + "<br><br>";
            content = content + programName + " uses the mathematical libraries colt and apaches common-math3. Moreover apaches common-lang3 is used.";
            content = content + "<br><br>";
            content = content + "If you find a bug in " + programName + ", please make sure to tell us about it!<br>";
            content = content + "Report bugs to jan-christian.rohde@gmx.de.";
        } else if (name.equals("Grammar")) {
            content = setTitle(programName, name);
            content = content + "Internally " + programName + " knows only one type: Function. Matrices, numbers and strings are considered as special functions. Matrices can be inserted in the way <i>matrix(rows, columns, &hellip; comma separated entries ordered by rows &hellip;)</i> ";
            content = content + "and might be stored as variables.<br><br>";
            content = content + "Internally numbers are constant Functions. Constant Functions can be of the type arbitrary precision integer, arbitrary precision rational, double or (double, double) complex numbers. " + programName + " decides automatically which of these datatypes it uses for ";
            content = content + "a constant function. Moreover matrices may have arbitrary real or complex functions as entries or other matrices as entries.<br><br>";

            content = content + "By the assignment operator \"=\" you can store Function variables. For example insert \"a =10\". Then you may try \"gcd(a,25)\", \"sin(a)\" or \"a+5\" for example.<br>";
            content = content + "If you use the assignment operator, the expression on the right hand side is <b>not</b> evaluated, if it is not constant. Instead it is just stored as the value of the left hand side variable name in this case. ";
            content = content + "Later if you insert the variable name <i>a</i> it will be evaluated with the default argument <i>x</i>. You can also evaluate it with an argument <i>arg</i> of your choice by inserting <i>a(arg)</i>. ";
            content = content + "Thus the grammar of " + programName + " follows the principle of lazy evaluation. By using the Ternary Conditional, this allows you to define recursive (meta) functions. (see <a href=\"User-Functions\">User-Functions</a> for example) However, since the assignment operator needs to return a value, it returns the default argument x.<br><br>";
            content = content + "On the other hand constant expressions are evaluated directly. Thus " + programName + " has both paradigms: functional and procedural. For procedural programming " + programName + " has while-loops and if / elseif / else as controll structures. ";
            content = content + "A command-line ends by \":\" or \";\". In the first case the result of the line is printed out, in the second one not. Scripts can call other scripts, plotting dialogs, read / write /delete files. For details see the example scripts. Script are called by the function <i>interpret</i>. The argument is the name of the script as string. ";
            content = content + "Thus insert \"interpret(string(<i>name of script with full path</i>))\" to run a script. Scripts return automatically the value of the last executed line.<br><br>";

            content = content + "Arbitrary arithmetic operations on Functions can be performed and arbitrary functions can be composed with other Functions with ";
            content = content + "three restrictions. First a double can never be used as an arbitrary precision variable. Second arithmetic User-functions accept only constant integer Functions as arguments. ";
            content = content + "Third boolean and comparision operators accept only constant arguments. Otherwise they return false. Moreover in the case of complex numbers the norm is used for comparision instead of the number itself.";
            content = content + " 0 counts as false, every other constant value counts as true.<br><br>";

            content = content + "The inserted Functions are composed by the following precedence rules:<br>";
            content = content + "<table border=1><tr>";
            content = content + "<th><span style=\"font-weight:200\">Precedence</span></th><th><span style=\"font-weight:200\">Name</span></th><th><span style=\"font-weight:200\">Associativity</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">1</span></th><th><span style=\"font-weight:200\">,</span></th><th><span style=\"font-weight:200\">left to right</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">2</span></th><th><span style=\"font-weight:200\">=</span></th><th><span style=\"font-weight:200\">right to left</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">3</span></th><th><span style=\"font-weight:200\">ternary conditional (&hellip;?&hellip;|&hellip;)</span></th><th><span style=\"font-weight:200\">right to left</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">4</span></th><th><span style=\"font-weight:200\">OR</span></th><th><span style=\"font-weight:200\">left to right</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">5</span></th><th><span style=\"font-weight:200\">AND</span></th><th><span style=\"font-weight:200\">left to right</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">6</span></th><th><span style=\"font-weight:200\">==, !=</span></th><th><span style=\"font-weight:200\">left to right</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">7</span></th><th><span style=\"font-weight:200\">&lt;, &lt;=, &gt;,&gt;=</span></th><th><span style=\"font-weight:200\">left to right</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">8</span></th><th><span style=\"font-weight:200\">+, -</span></th><th><span style=\"font-weight:200\">left to right</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">9</span></th><th><span style=\"font-weight:200\">*, /, %</span></th><th><span style=\"font-weight:200\">left to right</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">10</span></th><th><span style=\"font-weight:200\">^</span></th><th><span style=\"font-weight:200\">left to right</span></th>";
            content = content + "</tr><tr>";
            content = content + "<th><span style=\"font-weight:200\">11</span></th><th><span style=\"font-weight:200\"> unary -, (, ) </span></th><th><span style=\"font-weight:200\">right to left</th>";
            content = content + "</tr></table><br>";

            content = content + "The modulo operator \"%\" is defined for integers or more precisely for constant Functions with integer value.<br><br>";

            content = content + "Operators with precedence 8 or higher compose the input to functions (parse trees). By the latter and the fact that the differentiable User-functions are infinitely differentiable, each Function variable is infinitely differentiable. ";
            content = content + "By simplification and evaluation of constant Functions, arithmetic operations on the inserted terms are performed.";
            content = content + "<br><br>Operations with smaller precedence treat the input as variables.";

            content = content + setLinks("Help", "User-Functions", "Settings");
            content = content + "</html>";
        } else if (name.equals("User-Functions")) {
            content = setTitle(programName, name);
            content = content + "The names of many User-Functions should be self-explaining. Despite some comments should be made and some User-functions should be explained.";
            content = content + "<br><br>";
            content = content + "<h3>Arithmetic Functions</h3>";
            content = content + "The User-Functions <i>nextPrime</i>, the <i>(Euler totient) phi</i> and <i>prime</i> are only defined for positive integers <i>i&ge;0</i>. ";
            content = content + "If <i>i</i> is a prime number, <i>prime(i) = 1</i>. Otherwise <i>prime(i) = 0</i>.";
            content = content + "<br><br>";
            content = content + "<h3>Ternary Conditional</h3>";
            content = content + "The ternary conditional is a right associative operator of the build in grammar defined in the way<br><i>ternary -&gt; orExpression | (orExpression \"?\" orExpression \"|\" ternary)</i><br>";
            content = content + "You can use it to define your own recursive functions as in the examples below.<br><br>Faculty:<br><i>fac=x==1?1|fac(x-1)*x</i><br>This gives you \"fac(1)\" or \"fac(5)\", when you insert it in the next step.<br><br>";
            content = content + "Fibonacci numbers:<br><i>fib=x==0?0|x==1?1|fib(x-1)+fib(x-2)</i><br>In the next step you just to have to insert \"fib(n)\" for a positive integer n to get it.<br><br>";
            content = content + "Composition of Functions: Suppose you have defined a function variable <i>f</i> and you would like to compose it different times with it self. Then insert:";
            content = content + "<br><i>comp=x==1?f|f(comp(x-1))</i><br>For a positive integer <i>n</i> insert \"comp(n)\" to compose <i>f n</i> times with itself.<br><br>";
            content = content + "Higher derivatives: You can not use the Conditional directly for build in functions. Instead you can use build in functions to assign values to function variables. Thus define the higher derivative for a variable y:";
            content = content + "<br><i>d=x==1?derivative(y)|derivative(d(x-1))</i><br>The variable <i>y</i> might be defined at this point or not. Now you may insert \"y=cos(x)\" ";
            content = content + "for example. Then you can evaluate the higher derivatives of this function. For example insert \"d(2)\".";
            content = content + "<br><br>";
            content = content + "<h3>LCM and GCD</h3>";
            content = content + "The User-Functions <i>gcd</i> and <i>lcm</i> compute the greatest common divisor and the least common multiple of two integers.";
            content = content + "<br><br>";
            content = content + "<h3>Derivative</h3>";
            content = content + "In the case of the derivative you can compute the derivative of a Function in the variable <i>x</i>. ";
            content = content + "The differentiable Function can be composed by arbitrary User-functions of " + programName + ", numbers ";
            content = content + "and the arithmetic operations +, -, *, /, ^ and % as explained in <a href=\"Grammar\">Grammar</a>. Internally each variable, which is not a matrix, ";
            content = content + " is an infinitely differentiable function. For example insert \"derivative(cos(x))\" or \"derivative(exp(x^2))\".";
            content = content + "<br><br>";
            content = content + "<h3>Integral</h3>";
            content = content + "The Integral User-Function is a numerical function defined by the Simpson Integral of <i>f(x)</i> from <i>a</i> to <i>b</i> with maximal step size <i>0.1</i>. ";
            content = content + "Thus the error is smaller than <i>|f<sup> (4)</sup>(&xi;)|*(b-a)/180000</i> for some <i>&xi;</i> between <i>a</i> and <i>b</i>. The user is responsible that <i>f(x)</i> is ";
            content = content + "defined over the integral from <i>a</i> to <i>b</i>, since this is not checked by the program. For <i>a</i> and <i>b</i> only numbers are allowed as ";
            content = content + "arguments. For example insert \"integral(x^2,0,1)\".";
            content = content + "<br><br>";
            content = content + "<h3>Tangent</h3>";
            content = content + "The Tangent User-Function computes <i>t(x)</i> for the equation <i>y=t(x)</i> of the tangent line of a differentiable function in the variable <i>x</i> at <i>x0</i>. ";
            content = content + "For the trigonometric tangent function use the User-Function tan. For example insert \"tangent(x^2,1)\".";
            content = content + "<br><br>";
            content = content + "<h3>Normal</h3>";
            content = content + "The Normal User-Function computes <i>n(x)</i> for the equation <i>y=n(x)</i> of the normal line of a differentiable function in the variable <i>x</i> at <i>x0</i>. ";
            content = content + "This is the line, which has a right angle to the tangent line. For example insert \"normal(x^2,1)\".";
            content = content + "<br><br>";
            content = content + "<h3>System of Linear Equations</h3>";
            content = content + "The number of equations is the selected number of rows and the number of variables is the selected number of columns in the Settings.";
            content = content + setLinks("Help", "Grammar", "Settings");
            content = content + "</html>";
        } else if (name.equals("Settings")) {
            content = setTitle(programName, name);
            content = content + "<h3>Precision</h3>";
            content = content + "You can select the precision of the output of doubles in the result from 2 to 16. ";
            content = content + "This does not have any effect on arbitrary precision integers and the output of matrix entries. For arbitrary precision integers the output has always arbitrary precision and for the output of matrices the precision is always 6.";
            content = content + "<h3>Rows and Columns of Matrices</h3>";
            content = content + "Here you can select the number of rows and columns of matrices you like to use. This can vary from 1 to 5. ";
            content = content + "If the function needs a quadratic matrix, the number of columns of the matrix you can insert will be as the same as the selected number of rows.<br>";
            content = content + "In the case of a system of linear equations the number of equations is the selected number of rows and the number of variables is the selected number of columns.";
            content = content + setLinks("Help", "User-Functions", "Grammar");
            content = content + "</html>";
        }
        return content;
    }

    public void loadDocument(String name, String programName) {
        setText(getDocument(name, programName));
    }
}

class ActivatedHyperlinkListener implements HyperlinkListener {
    HelpEditorPane editorPane;
    String ProgramName;

    public ActivatedHyperlinkListener(HelpEditorPane editorPane, String ProgramName) {
        this.editorPane = editorPane;
        this.ProgramName = ProgramName;
    }

    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
        final String url = hyperlinkEvent.getDescription();
        if (type == HyperlinkEvent.EventType.ACTIVATED) {
            editorPane.loadDocument(url, ProgramName);
        }
    }
}

abstract class infoDialogGenerator {
    public void exec(String longProgramName, String licenseName) {
        final CalcDialog dialog = new CalcDialog();
        dialog.setLayout(new BorderLayout());
        setTitle(dialog);
        dialog.setIconImage(Toolkit.getDefaultToolkit().getImage("icons/FCLogo.png"));
        final HelpEditorPane helpTextPane = new HelpEditorPane();
        helpTextPane.setEditable(false);

        String aboutText = generateText(helpTextPane, longProgramName, licenseName);
        helpTextPane.setContentType("text/html");
        helpTextPane.setText(aboutText);

        JScrollPane scrollPane = new JScrollPane(helpTextPane);

        HyperlinkListener hyperlinkListener = new ActivatedHyperlinkListener(helpTextPane, longProgramName);
        helpTextPane.addHyperlinkListener(hyperlinkListener);

        dialog.add(scrollPane, BorderLayout.CENTER);

        Panel closePanel = new Panel();
        closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.LINE_AXIS));
        closePanel.add(Box.createHorizontalGlue());
        JButton okButton = new JButton("Ok");
        closePanel.add(okButton);

        dialog.add(closePanel, BorderLayout.SOUTH);

        scrollPane.getVerticalScrollBar().setValue(0);

        okButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent event) {
                                           dialog.dispose();
                                       }
                                   }
        );

        setSize(dialog);
        dialog.setVisible(true);
    }

    protected abstract void setTitle(final JDialog dialog);

    protected abstract String generateText(final HelpEditorPane helpTextPane, String longProgramName, String licenseName);

    protected abstract void setSize(final JDialog dialog);
}

class helpDialogGenerator extends infoDialogGenerator {
    protected void setTitle(final JDialog dialog) {
        dialog.setTitle("help");
    }

    protected String generateText(final HelpEditorPane helpTextPane, String longProgramName, String licenseName) {
        return helpTextPane.getDocument("Help", longProgramName);
    }

    protected void setSize(final JDialog dialog) {
        dialog.setSize(500, 300);
    }
}

class aboutDialogGenerator extends infoDialogGenerator {

    protected void setTitle(final JDialog dialog) {
        dialog.setTitle("about");
    }

    protected String generateText(final HelpEditorPane helpTextPane, String longProgramName, String licenseName) {
        String aboutText = "<html><h1><font color=\"#0000FF\">" + longProgramName + "</font></h1>";
        aboutText = aboutText + "&copy; 2015 - 2018 Jan Christian Rohde<br>";
        aboutText = aboutText + "jan-christian.rohde@gmx.de<br><br>License: ";
        aboutText = aboutText + licenseName;
        aboutText = aboutText + "</html>";
        return aboutText;
    }

    protected void setSize(final JDialog dialog) {
        dialog.setSize(400, 200);
    }

}

class licenseDialogGenerator extends infoDialogGenerator {

    protected void setTitle(final JDialog dialog) {
        dialog.setTitle("license");
    }

    protected String generateText(final HelpEditorPane helpTextPane, String longProgramName, String licenseName) {
        String aboutText = "<html>";
        aboutText = aboutText + longProgramName;
        aboutText = aboutText + " is free software; you can redistribute it and/or modify ";
        aboutText = aboutText + "it under the terms of the GNU General Public License as published by ";
        aboutText = aboutText + " the Free Software Foundation; either version 3 of the License, or ";
        aboutText = aboutText + "(at your option) any later version. ";
        aboutText = aboutText + "<br><br>";
        aboutText = aboutText + longProgramName;
        aboutText = aboutText + " is distributed in the hope that it will be useful, ";
        aboutText = aboutText + "but WITHOUT ANY WARRANTY; without even the implied warranty of ";
        aboutText = aboutText + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the ";
        aboutText = aboutText + "GNU General Public License for more details. ";
        aboutText = aboutText + "<br><br>";
        aboutText = aboutText + "You should have received a copy of the GNU General Public License ";
        aboutText = aboutText + "along with ";
        aboutText = aboutText + longProgramName;
        aboutText = aboutText + "; if not, see http://www.gnu.org/licenses.";
        aboutText = aboutText + "</html>";
        return aboutText;
    }

    protected void setSize(final JDialog dialog) {
        dialog.setSize(400, 350);
    }

}
