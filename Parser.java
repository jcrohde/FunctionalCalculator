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
import java.util.ArrayList;
import java.util.HashMap;

class FuParser {
    FuParser() {
        defaultArg = new XFunction();

        prec = 1.0;
        for (int i = 0; i < 6; i++) prec *= 0.1;
    }

    public void setVariables(java.util.Map<String, MyFunction> vars) { variables = vars; }

    public MyFunction parseFunction(String Pstr) throws MyParserException {
        str = Pstr;

        position = 0;
        MyFunction res = ternary();
        if (Operation == OperationToken.BRACKCLOSE) {
            throw new MyParserException(MyParserException.type.NOBRACKOP, "Missing \"(\"");
        }
        return res;
    }

    public MyFunction parsePlot(String Pstr) throws MyParserException {
        defaultArg = new XFunction();
        return callEmbedded(Pstr, new XFunction());
    }

    public String parse(String Pstr, int precision) throws MyParserException {
        prec = 1.0;
        for (int i = 0; i < precision; i++) prec *= 0.1;
        defaultArg = new XFunction();

        MyFunction res = parseFunction(Pstr);
        return res.print(precision);
    }

    private ArbitraryPrecisionRational callRationalParsing(String PStr) throws MyParserException {
        String strMem = str;
        int posMem = position;
        MyFunction defaultMem = defaultArg;
        ArbitraryPrecisionRational result = parseRational(PStr);
        str = strMem;
        position = posMem;
        defaultArg = defaultMem;
        return result;
    }

    public ArbitraryPrecisionRational parseRational(String Pstr) throws MyParserException {
        str = Pstr;
        position = 0;
        defaultArg = new XFunction();
        MyFunction res = ternary();
        if (!res.isConstant())
            throw new MyParserException(MyParserException.type.NOTNUMBER, res.print(6) + " is not a number");
        return res.eval(0.0).getRational();
    }

    public void clearVariables() {
        variables.clear();
    }

    private enum OperationToken {
        PLUS, MINUS, MULT, DIV, MOD, POWER, NONE, BRACKOPEN, BRACKCLOSE, COMMA,
        ASSIGNMENT, COMPARISON, TERNARYCONDITION, TERNARYALTERNATIVE, SMALLER, LARGER,
        SMALLEQ, LARGEQ, NOTEQ, AND, OR, NOT
    }

    private ValueStrategyFactory valueFactory = new ValueStrategyFactory();
    private static FunctionArithmetics Arith = new FunctionArithmetics();
    private OperationToken Operation;
    private double prec;

    private java.util.Map<String, MyFunction> variables = new java.util.HashMap<String, MyFunction>();

    private String str;
    private String name;
    private char c;
    private int position;
    private MyFunction defaultArg;
    private int end;
    private boolean found;

    private void setOperation(OperationToken token) {
        Operation = token;
        found = true;
        end = position;
    }

    private boolean findOperation(char c) {
        boolean foundOperation = true;

        if (c == '+') {
            setOperation(OperationToken.PLUS);
        } else if (c == '*') {
            setOperation(Operation = OperationToken.MULT);
        } else if (c == '/') {
            setOperation(OperationToken.DIV);
        } else if (c == '%') {
            setOperation(OperationToken.MOD);
        } else if (c == '^') {
            setOperation(OperationToken.POWER);
        } else if (c == ',') {
            setOperation(OperationToken.COMMA);
        } else if (c == '?') {
            setOperation(OperationToken.TERNARYCONDITION);
        } else if (c == '|') {
            setOperation(OperationToken.TERNARYALTERNATIVE);
        } else if (c == ')') {
            setOperation(OperationToken.BRACKCLOSE);
        } else if (c == '<') {
            if (position < str.length() - 1 && str.charAt(position + 1) == '=') {
                setOperation(OperationToken.SMALLEQ);
                position++;
            } else setOperation(OperationToken.SMALLER);
        } else if (c == '>') {
            if (position < str.length() - 1 && str.charAt(position + 1) == '=') {
                setOperation(OperationToken.LARGEQ);
                position++;
            } else setOperation(OperationToken.LARGER);
        } else if (c == 'O' && position < str.length() - 1 && str.charAt(position + 1) == 'R') {
            setOperation(OperationToken.OR);
            position++;
        } else if (c == 'A' && position < str.length() - 2 && str.charAt(position + 1) == 'N' && str.charAt(position + 2) == 'D') {
            setOperation(OperationToken.AND);
            position++;
            position++;
        } else foundOperation = false;

        return foundOperation;
    }

    private MyFunction primary() throws MyParserException {
        String subString;
        MyFunction res = null;
        int begin = position;
        end = position;
        boolean var = false;
        found = false;

        Operation = OperationToken.NONE;
        int l = str.length();
        while (position < l && !found) {
            c = str.charAt(position);

            if (findOperation(c)) {
                ;
            } else if (c == '-') {
                getMinus(res, begin);
            } else if (c == '!') {
                if (position < str.length() - 1 && str.charAt(position + 1) == '=') {
                    setOperation(OperationToken.NOTEQ);
                    position++;
                } else {
                    res = executeFaculty(begin);
                }
            } else if (c == '=') {
                if (position < str.length() - 1 && str.indexOf("=", position + 1) == position + 1) {
                    setOperation(OperationToken.COMPARISON);
                    position++;
                } else {
                    res = decideBetweenAssignmentAndComparison(begin, var);
                }
            } else if (c == '(') {
                res = findFunction(begin);
            } else if (c != '0' && c != '1' && c != '2' && c != '3' && c != '4' &&
                    c != '5' && c != '6' && c != '7' && c != '8' && c != '9' &&
                    c != '.' && c != 'x' && c != ' ' && c != 'i') {
                var = true;
            }
            position++;
        }
        if (Operation == OperationToken.NONE) end = str.length();

        subString = str.substring(begin, end);
        subString = subString.replace(" ", "");

        if (res == null) {
            res = getRes(subString, var);
        }
        return res;
    }

    private void getMinus(MyFunction res, int begin) throws MyParserException {
        if (position == begin) {
            position++;
            res = primary();
            res = new UnaryFunction(FunctionType.UNARYMINUS, res);
            position -= 1;
        } else {
            setOperation(OperationToken.MINUS);
        }
    }

    private MyFunction executeFaculty(int begin) throws MyParserException {
        MyFunction res = new ConstantFunction(valueFactory.build(str.substring(begin, position)));
        ValueStrategy vStr = res.eval(0.0);
        BigInteger BI = vStr.getInteger();
        BigInteger i = BigInteger.ONE;
        for (int k = 2; BI.compareTo(BigInteger.valueOf(k)) >= 0; k++) {
            i = i.multiply(BigInteger.valueOf(k));
        }
        res = new ConstantFunction(new IntegerStrategy(i));
        return res;
    }

    private MyFunction decideBetweenAssignmentAndComparison(int begin, boolean var) throws MyParserException {
        MyFunction res = defaultArg;

        end = position;
        name = str.substring(begin, end);
        name = name.replace(" ", "");
        if (!var)
            throw new MyParserException(MyParserException.type.NOPRIMARY, name + " denotes a constant and therefore not an admissible name of a variable.");
        if (name.length() == 0)
            throw new MyParserException(MyParserException.type.NOPRIMARY, "Missing primary expression.");
        position++;
        int valueBegin = position;

        runTillEndOfBrackets();

        int valueEnd = position;
        String value = str.substring(valueBegin, valueEnd).replace(" ", "");
        boolean isConst = false;
        MyFunction v = null;
        try {
            v = callEmbedded(value, defaultArg);
            isConst = v.isConstant() && !(v instanceof FunctionalString) && value.indexOf("?") == -1;
        } catch (MyParserException e) {
            v = null;
        }
        if (isConst && v != null) variables.put(name, v);
        else variables.put(name, new FunctionalString(value));
        found = true;
        end = position;

        return res;
    }

    private MyFunction findFunction(int begin) throws MyParserException {
        MyFunction res = null;

        position++;
        if (position > begin + 1) {
            String subString = str.substring(begin, position - 1);
            FunctionOperation Op = Library.getBinaryFunction(subString);
            if (subString.equals("integral")) {
                MyFunction f = ternary();
                MyFunction a = ternary();
                MyFunction b = ternary();

                res = new IntegralFunction(f, a, b);
            } else if (subString.equals("getEntry")) {
                MyFunction m = ternary();
                MyFunction i = ternary();
                MyFunction j = ternary();

                if (m instanceof MatrixAdapter) {
                    res = ((MatrixAdapter) m).value.getEntry(i.eval(0.0).getInteger().intValue(), j.eval(0.0).getInteger().intValue());
                } else
                    throw new MyParserException(MyParserException.type.NOTMATRIX, "First argument is not a matrix");
            } else if (subString.equals("setEntry")) {
                MyFunction m = ternary();
                MyFunction i = ternary();
                MyFunction j = ternary();
                MyFunction f = ternary();

                if (m instanceof MatrixAdapter) {
                    ((MatrixAdapter) m).value.setEntry(i.eval(0.0).getInteger().intValue(), j.eval(0.0).getInteger().intValue(), f);
                    res = m;
                } else
                    throw new MyParserException(MyParserException.type.NOTMATRIX, "First argument is not a matrix");
            } else if (subString.equals("plot")) {
                MyFunction f = ternary();
                MyFunction g = ternary();
                MyFunction h = ternary();
                MyFunction i = ternary();

                MyFunction argMem = defaultArg;
                int memPosition = position;
                String memStr = str;
                OperationToken opMem = Operation;
                FunctionalCalculator.plotGenerator.exec(FunctionalCalculator.MyParser, f.print(6), g.print(6), h.print(6), i.print(6));
                position = memPosition;
                str = memStr;
                Operation = opMem;
                defaultArg = argMem;

                res = defaultArg;
            } else if (subString.equals("interpret")) {
                Interpreter interpreter = new Interpreter();

                int valueBegin = position;
                runTillEndOfBrackets();
                if (Operation == OperationToken.BRACKCLOSE) {
                    String arg = str.substring(valueBegin, position);

                    String scriptName;
                    if (arg.indexOf(",") > -1) {
                        scriptName = arg.substring(0, arg.indexOf(","));
                        arg = arg.substring(arg.indexOf(",")+1, arg.length());
                    }
                    else {
                        scriptName = arg;
                        arg = "";
                    }
                    scriptName = callEmbedded(scriptName, defaultArg).print(6);
                    int count = 0;
                    java.util.Map<String, MyFunction> args = new HashMap<>();
                    while (arg.length() > 0) {
                        String param;
                        if (arg.indexOf(",") > -1) {
                            param = arg.substring(0, arg.indexOf(","));
                            arg = arg.substring(arg.indexOf(",") + 1, arg.length());
                        } else {
                            param = arg;
                            arg = "";
                        }
                        MyFunction f = callEmbedded(param, defaultArg);
                        args.put("arg"+ count, f);
                        count++;
                    }
                    res = interpreter.interprete(scriptName, args);
                    position++;
                }
            } else if (subString.equals("matrix")) {
                res = parseMatrix();
            } else if (subString.equals("string")) {
                int valueBegin = position;
                runTillEndOfBrackets();
                if (Operation == OperationToken.BRACKCLOSE) {
                    res = new FunctionalString(str.substring(valueBegin, position));
                    position++;
                }
            } else if (subString.equals("variable")) {
                int valueBegin = position;
                runTillEndOfBrackets();
                if (Operation == OperationToken.BRACKCLOSE) {
                    String variableName = str.substring(valueBegin, position);

                    variables.put(variableName,
                            new FunctionalString(FunctionalCalculator.scalarDia.exec("value of" + ";" + variableName, FunctionalCalculator.shortProgramName).replace(" ", "")));

                    try {
                        res = (MyFunction) defaultArg.clone();
                    } catch (java.lang.CloneNotSupportedException e) {
                        res = new XFunction();
                    }
                    position++;
                }
            } else if (Op == FunctionOperation.NONE) {
                FunctionType type = Library.getType(subString);
                if (type == null) {
                    res = variables.get(subString);
                    if (res instanceof FunctionalString) {
                        String valString = res.print(6);
                        if (valString == null)
                            throw new MyParserException(MyParserException.type.INCORRECTFCTN, subString + " is not the name of an implemented function");

                        res = callEmbedded(valString, ternary());
                    }
                } else res = new UnaryFunction(type, ternary());
            } else {
                MyFunction first = ternary();
                MyFunction second = ternary();
                res = new BinaryFunction(Op, first, second);
            }
        } else {
            res = ternary();
        }
        if (Operation != OperationToken.BRACKCLOSE) {
            throw new MyParserException(MyParserException.type.NOBRACKCL, "Missing \")\"");
        }
        Operation = OperationToken.NONE;
        position--;
        found = false;

        return res;
    }

    private MyFunction getRes(String subString, boolean var) throws MyParserException {
        MyFunction res;

        if (subString.length() == 0)
            throw new MyParserException(MyParserException.type.NOPRIMARY, "Missing primary expression.");
        if (var) {
            res = variables.get(subString);

            if (res instanceof FunctionalString) {
                String valString = res.print(6);

                if (valString == null)
                    throw new MyParserException(MyParserException.type.INCORRECTINPUT, "Can not parse the expression " + subString + ".");

                res = callEmbedded(valString, new XFunction());
            }
            else if (defaultArg.isConstant()) {
                res = res.compose(defaultArg);
                res = res.simplify();
            }
        } else if (subString.length() == 1 && subString.charAt(0) == 'x') {
            res = defaultArg;
        } else if (subString.length() == 1 && subString.charAt(0) == 'i') {
            res = new ConstantFunction(new ComplexStrategy(0.0, 1.0));
        } else if (subString.indexOf("x") == -1) res = new ConstantFunction(valueFactory.build(subString));
        else
            throw new MyParserException(MyParserException.type.INCORRECTINPUT, "Can not parse the expression " + subString + ".");

        return res;
    }

    private MyFunction callEmbedded(String string, MyFunction argument) throws MyParserException {
        MyFunction argMem = defaultArg;
        int memPosition = position;
        String memStr = str;
        OperationToken opMem = Operation;

        defaultArg = argument;
        MyFunction res = parseFunction(string);

        position = memPosition;
        str = memStr;
        Operation = opMem;
        defaultArg = argMem;

        return res;
    }

    private MatrixAdapter parseMatrix() throws MyParserException {
        char c;
        String number;
        int rows = 0, columns = 0;
        ArrayList<MyFunction> values = new ArrayList<>();
        String numberString = "";
        int count = 0;
        int brackIndex = 0;

        do {
            c = str.charAt(position);
            if (brackIndex == 0 && (c == ',' || c == ')')) {
                if (count == 0) {
                    ArbitraryPrecisionRational r = callRationalParsing(numberString);
                    if (!r.isInteger())
                        throw new MyParserException(MyParserException.type.INCORRECTARGUMENTS, "Row number must be an integer");
                    rows = r.getNumerator().intValue();
                    numberString = "";
                } else if (count == 1) {
                    ArbitraryPrecisionRational r = callRationalParsing(numberString);
                    if (!r.isInteger())
                        throw new MyParserException(MyParserException.type.INCORRECTARGUMENTS, "Column number must be an integer");
                    columns = r.getNumerator().intValue();
                    numberString = "";
                } else {
                    values.add(callEmbedded(numberString, defaultArg));
                    numberString = "";
                }
                count++;
            } else {
                numberString += c;
                if (c == '(') brackIndex++;
            }
            if (c == ')') brackIndex--;
            position++;
        } while (brackIndex >= 0 && position < str.length());

        if (count < 2)
            throw new MyParserException(MyParserException.type.INCORRECTARGUMENTS, "For a matrix one needs to insert rows and columns");
        if (count - 2 != rows * columns)
            throw new MyParserException(MyParserException.type.INCORRECTARGUMENTS, "Incorrect number of matrix entries");

        matrix m = new matrix(rows, columns, values);
        MatrixAdapter res = new MatrixAdapter(m);

        if (c == ')') Operation = OperationToken.BRACKCLOSE;
        return res;
    }

    void runTillEndOfBrackets() {
        int wgt = 0;
        while (position < str.length()) {
            if (str.charAt(position) == ')') wgt++;
            if (str.charAt(position) == '(') wgt--;
            if (wgt == 1) {
                Operation = OperationToken.BRACKCLOSE;
                break;
            }
            position++;
        }
    }

    private MyFunction power() throws MyParserException {
        MyFunction right, left = primary();
        while (true) {
            if (Operation == OperationToken.POWER) {
                right = primary();
                left = Arith.power(left, right);
            } else break;
        }
        return left;
    }

    private MyFunction term() throws MyParserException {
        MyFunction right, left = power();
        while (true) {
            if (Operation == OperationToken.MULT) {
                right = power();
                left = Arith.multiply(left, right);
            } else if (Operation == OperationToken.DIV) {
                right = power();
                if (right.isConstant() && right.eval(0.0).getDouble() == 0.0) {
                    throw new MyParserException(MyParserException.type.DIVBYZERO, "Division by zero");
                }
                left = Arith.divide(left, right);
            } else if (Operation == OperationToken.MOD) {
                right = power();
                left = Arith.mod(left, right);
            } else break;
        }
        return left;
    }

    private MyFunction function() throws MyParserException {
        MyFunction right, left = term();
        while (true) {
            if (Operation == OperationToken.PLUS) {
                right = term();
                left = Arith.add(left, right);
            } else if (Operation == OperationToken.MINUS) {
                right = term();
                left = Arith.subtract(left, right);
            } else break;
        }
        return left;
    }

    private MyFunction relation() throws MyParserException {
        MyFunction right, left = function();
        left = left.simplify();
        while (true) {
            if (Operation == OperationToken.SMALLER) {
                right = function();
                right = right.simplify();
                left = Arith.smaller(left, right, prec);
            } else if (Operation == OperationToken.LARGER) {
                right = function();
                right = right.simplify();
                left = Arith.smaller(right, left, prec);
            } else if (Operation == OperationToken.SMALLEQ) {
                right = function();
                right = right.simplify();
                left = Arith.smallEq(left, right, prec);
            } else if (Operation == OperationToken.LARGEQ) {
                right = function();
                right = right.simplify();
                left = Arith.smallEq(right, left, prec);
            } else break;
        }
        return left;
    }

    private MyFunction comparison() throws MyParserException {
        MyFunction right, left = relation();
        while (true) {
            if (Operation == OperationToken.COMPARISON) {
                right = relation();
                left = Arith.compare(left, right, prec);
            }
            if (Operation == OperationToken.NOTEQ) {
                right = relation();
                left = Arith.notEq(left, right, prec);
            } else break;
        }
        return left;
    }

    private MyFunction andLayer() throws MyParserException {
        MyFunction right, left = comparison();
        while (true) {
            if (Operation == OperationToken.AND) {
                right = comparison();
                left = Arith.and(left, right, prec);
            } else break;
        }
        return left;
    }

    private MyFunction orLayer() throws MyParserException {
        MyFunction right, left = andLayer();
        while (true) {
            if (Operation == OperationToken.OR) {
                right = andLayer();
                left = Arith.or(left, right, prec);
            } else break;
        }
        return left;
    }

    private MyFunction ternary() throws MyParserException {
        int pos = 0;
        String copy = str;
        str = "";
        while (pos < copy.length()) {
            if (copy.charAt(pos) != ' ' && copy.charAt(pos) != '\n') str += copy.charAt(pos);
            pos++;
        }

        if (str == null || str.isEmpty())
            throw new MyParserException(MyParserException.type.INCORRECTINPUT, "Got empty (sub) string to parse");
        MyFunction left = orLayer();
        if (Operation == OperationToken.TERNARYALTERNATIVE)
            throw new MyParserException(MyParserException.type.INCORRECTINPUT, "Ternary conditional syntax must be: \"...?...|...\"");
        else if (Operation == OperationToken.TERNARYCONDITION) {
            MyFunction checkZero = Arith.compare(left, new ConstantFunction(new IntegerStrategy(BigInteger.ZERO)), prec);
            if (checkZero.eval(0.0).getDouble() > 0.5) {
                while (position < str.length() && str.charAt(position) != '|') position++;
                position++;
                return ternary();
            }

            MyFunction res = orLayer();
            runTillEndOfBrackets();
            return res;
        }
        return left;
    }
}