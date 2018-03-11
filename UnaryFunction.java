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
import java.util.Vector;

enum FunctionType {
    SIN, COS, EXP, LN, TAN, COT, ASIN, ACOS, ATAN, SQRT, DER, NEXTPRIME, PRIME, PHI,
    UNARYMINUS, COSH, SINH, TANH, CUBEROOT, DET, CHARPOLY, MINIPOLY, RANK, TRACE,
    INVERTMATRIX, GAUSS, READFILE, DELETEFILE, SYMMETRY, PLOTCOMPLEX, GAMMA, DIGAMMA, TRIGAMMA,
    CEIL, FLOOR, ROUND, ABS, FRAC, CONJ, ARG, RE, IM, LOG2, ISCONST
}

class UnaryFunction extends MyFunction {
    private FunctionType type;
    private MyFunction argument;

    @Override
    public MyFunction symmetry() throws MyParserException {
        if (argument.symmetry().eval(0.0).getDouble() > -0.5) {
            return argument.symmetry();
        }
        else {
            int sym = 0;
            if (type == FunctionType.SIN || type == FunctionType.TAN || type == FunctionType.COT || type == FunctionType.ASIN ||
                    type == FunctionType.ATAN || type == FunctionType.UNARYMINUS || type == FunctionType.SINH) sym = -1;
            else if (type == FunctionType.COS || type == FunctionType.COSH) sym = 1;
            if (sym == 1) return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
            else if (sym == -1) return new ConstantFunction(new IntegerStrategy(new BigInteger("-1")));
            else return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
        }
    }

    public UnaryFunction(FunctionType type, MyFunction argument) {
        this.type = type;
        this.argument = argument;
    }

    private Vector getPrimeFactors(BigInteger i) {
        Vector vec = new Vector();
        BigInteger prime = BigInteger.ONE.add(BigInteger.ONE);
        while (prime.compareTo(i) <= 0) {
            while (i.mod(prime).equals(BigInteger.ZERO)) {
                vec.add(prime);
                i = i.divide(prime);
            }
            prime = prime.nextProbablePrime();
        }
        return vec;
    }

    private ValueStrategy complexEval(double d) throws MyParserException {
        ComplexStrategy c = (ComplexStrategy) argument.eval(d);
        return complexEval(c);
    }

    public ComplexStrategy eval(double re, double im) throws MyParserException {
        return complexEval(argument.eval(re, im));
    }

    private ComplexStrategy complexEval(ComplexStrategy c) throws MyParserException {
        if (type == FunctionType.EXP) {
            double norm = Math.exp(c.re());
            return new ComplexStrategy(norm * Math.cos(c.im()), norm * Math.sin(c.im()));
        }
        if (type == FunctionType.SIN) {
            return c.sin();
        }
        if (type == FunctionType.ABS) {
            return new ComplexStrategy(c.getDouble(), 0.0);
        }
        if (type == FunctionType.ARG) {
            return new ComplexStrategy(c.ln().im(), 0.0);
        }
        if (type == FunctionType.RE) {
            return new ComplexStrategy(c.re(), 0.0);
        }
        if (type == FunctionType.IM) {
            return new ComplexStrategy(c.im(), 0.0);
        }
        if (type == FunctionType.COS) {
            return c.cos();
        }
        if (type == FunctionType.LN) {
            return c.ln();
        }
        if (type == FunctionType.LOG2) {
            ComplexStrategy two = new ComplexStrategy(2.0, 0.0);
            return c.ln().divide(two.ln());
        }
        if (type == FunctionType.COSH) {
            return c.cosh();
        }
        if (type == FunctionType.SINH) {
            return c.sinh();
        }
        if (type == FunctionType.CONJ) {
            return new ComplexStrategy(c.re(), -c.im());
        }
        if (type == FunctionType.TAN) {
            return c.sin().divide(c.cos());
        }
        if (type == FunctionType.COT) {
            return c.cos().divide(c.sin());
        }
        if (type == FunctionType.TANH) {
            return c.sinh().divide(c.cosh());
        }
        if (type == FunctionType.ATAN) {
            ComplexStrategy s1 = new ComplexStrategy(1.0, 0.0).minus(new ComplexStrategy(0.0, 1.0).multiply(c));
            s1 = s1.ln();
            ComplexStrategy s2 = new ComplexStrategy(1.0, 0.0).add(new ComplexStrategy(0.0, 1.0).multiply(c));
            s2 = s2.ln();
            s1 = s1.minus(s2);
            return s1.multiply(new ComplexStrategy(0.0, 1.0)).divide(new ComplexStrategy(2.0, 0.0));
        }
        if (type == FunctionType.ACOS) {
            ComplexStrategy s = new ComplexStrategy(Math.PI / 2.0, 0.0);
            ComplexStrategy t = c.asin();
            return s.minus(t);
        }
        if (type == FunctionType.ASIN) {
            return c.asin();
        }
        if (type == FunctionType.SQRT) {
            return c.sqrt();
        }
        if (type == FunctionType.CUBEROOT) {
            double lnaRe = Math.log(Math.sqrt(c.re() * c.re() + c.im() * c.im())), lnaIm = Math.atan2(c.im(), c.re());
            double norm = Math.exp(lnaRe / 3), expIm = lnaIm / 3;
            return new ComplexStrategy(norm * Math.cos(expIm), norm * Math.sin(expIm));
        }
        if (type == FunctionType.UNARYMINUS) {
            return new ComplexStrategy(-c.re(), -c.im());
        }
        return null;
    }

    public ValueStrategy eval(double d) throws MyParserException {
        ValueStrategy VS = argument.eval(d);
        if (argument.eval(d) instanceof ComplexStrategy) return complexEval(d);
        double res, arg = VS.getDouble();
        if (type == FunctionType.SIN) res = Math.sin(arg);
        else if (type == FunctionType.EXP) res = Math.exp(arg);
        else if (type == FunctionType.LN) res = Math.log(arg);
        else if (type == FunctionType.LOG2) res = Math.log(arg) / Math.log(2.0);
        else if (type == FunctionType.TAN) res = Math.tan(arg);
        else if (type == FunctionType.COT) res = 1.0 / Math.tan(arg);
        else if (type == FunctionType.SQRT) res = Math.sqrt(arg);
        else if (type == FunctionType.ATAN) res = Math.atan(arg);
        else if (type == FunctionType.ACOS) res = Math.acos(arg);
        else if (type == FunctionType.ASIN) res = Math.asin(arg);
        else if (type == FunctionType.COSH) res = Math.cosh(arg);
        else if (type == FunctionType.SINH) res = Math.sinh(arg);
        else if (type == FunctionType.CUBEROOT) res = Math.cbrt(arg);
        else if (type == FunctionType.TANH) res = Math.tanh(arg);
        else if (type == FunctionType.GAMMA) res = org.apache.commons.math3.special.Gamma.gamma(arg);
        else if (type == FunctionType.DIGAMMA) res = org.apache.commons.math3.special.Gamma.digamma(arg);
        else if (type == FunctionType.TRIGAMMA) res = org.apache.commons.math3.special.Gamma.trigamma(arg);
        else if (type == FunctionType.CEIL) {
            return new IntegerStrategy(BigInteger.valueOf((int) Math.ceil(arg)));
        }
        else if (type == FunctionType.FLOOR) {
            return new IntegerStrategy(BigInteger.valueOf((int) Math.floor(arg)));
        }
        else if (type == FunctionType.ROUND) {
            return new IntegerStrategy(BigInteger.valueOf((int) Math.round(arg)));
        }
        else if (type == FunctionType.FRAC) {
            res = arg - Math.floor(arg);
        }
        else if (type == FunctionType.UNARYMINUS) {
            IntegerStrategy MinusOne = new IntegerStrategy(BigInteger.valueOf(-1));
            return MinusOne.performOperation(FunctionOperation.MULT, VS);
        }
        else if (type == FunctionType.DER) {
            MyFunction f = argument.getDerivative();
            res = f.eval(d).getDouble();
        }
        else if (type == FunctionType.COS) res = Math.cos(arg);
        else if (type == FunctionType.ABS) res = Math.abs(arg);
        else if (type == FunctionType.RE) res = arg;
        else if (type == FunctionType.IM) res = 0.0;
        else {
            if (!VS.isInteger())
                throw new MyParserException(MyParserException.type.NOTINT, VS.print(6) + " is not an integer.");
            BigInteger iRes, iArg = VS.getInteger();
            if (iArg.compareTo(BigInteger.ZERO) < 0)
                throw new MyParserException(MyParserException.type.INTNEG, "Negative integer.");
            if (type == FunctionType.NEXTPRIME) iRes = iArg.nextProbablePrime();
            else if (type == FunctionType.PRIME) {
                if (iArg.equals(BigInteger.ZERO)) iRes = BigInteger.ZERO;
                else if (iArg.subtract(BigInteger.ONE).nextProbablePrime().equals(iArg)) {
                    iRes = BigInteger.ONE;
                } else {
                    iRes = BigInteger.ZERO;
                }
            } else if (type == FunctionType.PHI) {
                Vector primeVector = getPrimeFactors(iArg);
                iRes = BigInteger.ONE;
                BigInteger currentPrime = BigInteger.ONE;
                for (int i = 0; i < primeVector.size(); i++) {
                    if (primeVector.elementAt(i).equals(currentPrime)) {
                        iRes = iRes.multiply((BigInteger) primeVector.elementAt(i));
                    } else {
                        iRes = ((BigInteger) primeVector.elementAt(i)).subtract(BigInteger.ONE).multiply(iRes);
                        currentPrime = ((BigInteger) primeVector.elementAt(i));
                    }
                }
            } else iRes = BigInteger.ZERO;
            return new IntegerStrategy(iRes);
        }
        return new DoubleStrategy(res);
    }

    protected String printFormat(int precision) throws MyParserException {
        String str;
        if (type == FunctionType.SIN) str = "sin";
        else if (type == FunctionType.EXP) str = "exp";
        else if (type == FunctionType.LN) str = "ln";
        else if (type == FunctionType.TAN) str = "tan";
        else if (type == FunctionType.COT) str = "cot";
        else if (type == FunctionType.SQRT) str = "sqrt";
        else if (type == FunctionType.DER) str = "derivative";
        else if (type == FunctionType.COSH) str = "cosh";
        else if (type == FunctionType.SINH) str = "sinh";
        else if (type == FunctionType.TANH) str = "tanh";
        else if (type == FunctionType.ACOS) str = "acos";
        else if (type == FunctionType.ASIN) str = "asin";
        else if (type == FunctionType.ATAN) str = "atan";
        else if (type == FunctionType.CUBEROOT) str = "cubeRoot";
        else if (type == FunctionType.UNARYMINUS) str = "-";
        else if (type == FunctionType.COS) str = "cos";
        else
            throw new MyParserException(MyParserException.type.NOTNUMBER, "The argument of a number theoretic function must be a number.");
        str = str + "(";
        str = str + argument.printFormat(precision);
        str = str + ")";
        return str;
    }

    public boolean isConstant() {
        return argument.isConstant();
    }

    public MyFunction simplify() throws MyParserException {
        argument = argument.simplify();
        if (type == FunctionType.DER) {
            MyFunction d = argument.getDerivative();
            return d.simplify();
        } else if (type == FunctionType.READFILE) {
            try {
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(FunctionalString.completeRelativePath(argument.print(6))));

                String r = "";
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    r += line;
                }
                FunctionalString res = new FunctionalString(r);

                reader.close();
                return res;
            } catch (java.io.IOException e) {
                return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
            }
        } else if (type == FunctionType.DELETEFILE) {
            try {
                java.nio.file.Path path = java.nio.file.Paths.get(FunctionalString.completeRelativePath(argument.print(6)));
                java.nio.file.Files.delete(path);

                return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
            } catch (java.io.IOException e) {
                return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
            }
        } else if (type == FunctionType.ISCONST) {
            if (isConstant()) return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
            else return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
        } else if (type == FunctionType.PLOTCOMPLEX) {
            FunctionalCalculator.complexPlotGenerator.exec(FunctionalCalculator.MyParser, argument.print(6));
            return new XFunction();
        } else if (type == FunctionType.SYMMETRY) {
            return argument.symmetry();
        } else if (type == FunctionType.CHARPOLY) {
            if (argument instanceof MatrixAdapter) {
                return ((MatrixAdapter) argument).value.charPoly();
            } else
                throw new MyParserException(MyParserException.type.NOTMATRIX, argument.print(6) + " is not a matrix");
        } else if (type == FunctionType.MINIPOLY) {
            if (argument instanceof MatrixAdapter) {
                return ((MatrixAdapter) argument).value.miniPoly();
            } else
                throw new MyParserException(MyParserException.type.NOTMATRIX, argument.print(6) + " is not a matrix");
        } else if (type == FunctionType.DET) {
            if (argument instanceof MatrixAdapter) {
                return ((MatrixAdapter) argument).value.det();
            } else
                throw new MyParserException(MyParserException.type.NOTMATRIX, argument.print(6) + " is not a matrix");
        } else if (type == FunctionType.TRACE) {
            if (argument instanceof MatrixAdapter) {
                return ((MatrixAdapter) argument).value.trace();
            } else
                throw new MyParserException(MyParserException.type.NOTMATRIX, argument.print(6) + " is not a matrix");
        } else if (type == FunctionType.INVERTMATRIX) {
            if (argument instanceof MatrixAdapter) {
                return new MatrixAdapter(((MatrixAdapter) argument).value.invert());
            } else
                throw new MyParserException(MyParserException.type.NOTMATRIX, argument.print(6) + " is not a matrix");
        } else if (type == FunctionType.GAUSS) {
            if (argument instanceof MatrixAdapter) {
                return new MatrixAdapter(((MatrixAdapter) argument).value.gauss());
            } else
                throw new MyParserException(MyParserException.type.NOTMATRIX, argument.print(6) + " is not a matrix");
        } else if (type == FunctionType.RANK) {
            if (argument instanceof MatrixAdapter) {
                return new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(
                        BigInteger.valueOf(((MatrixAdapter) argument).value.rank()),
                        BigInteger.ONE)
                ));
            } else
                throw new MyParserException(MyParserException.type.NOTMATRIX, argument.print(6) + " is not a matrix");
        } else if (type == FunctionType.UNARYMINUS
                && argument instanceof UnaryFunction
                && ((UnaryFunction) argument).type == FunctionType.UNARYMINUS) {
            return ((UnaryFunction) argument).argument.simplify();
        } else if (argument.isConstant()) {
            return new ConstantFunction(eval(0.0));
        } else return this;
    }

    public MyFunction getDerivative() throws MyParserException {
        MyFunction outer = null, inner = argument.getDerivative();
        if (type == FunctionType.EXP) outer = this;
        else if (type == FunctionType.SIN) {
            outer = new UnaryFunction(FunctionType.COS, argument);
        } else if (type == FunctionType.COS) {
            outer = new UnaryFunction(FunctionType.UNARYMINUS, new UnaryFunction(FunctionType.SIN, argument));
        } else if (type == FunctionType.LN) {
            outer = new BinaryFunction(FunctionOperation.DIV, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), argument);
        } else if (type == FunctionType.TAN) {
            MyFunction P = new BinaryFunction(FunctionOperation.POWER, this, new ConstantFunction(new IntegerStrategy(BigInteger.ONE.add(BigInteger.ONE))));
            outer = new BinaryFunction(FunctionOperation.PLUS, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), P);
        } else if (type == FunctionType.COT) {
            MyFunction S = new UnaryFunction(FunctionType.SIN, argument);
            MyFunction Q = new BinaryFunction(FunctionOperation.POWER, S, new ConstantFunction(new IntegerStrategy(BigInteger.ONE.add(BigInteger.ONE))));
            outer = new BinaryFunction(FunctionOperation.DIV, new ConstantFunction(new IntegerStrategy(BigInteger.ONE.negate())), Q);
        } else if (type == FunctionType.SQRT) {
            MyFunction Q = new BinaryFunction(FunctionOperation.MULT, new ConstantFunction(new IntegerStrategy(BigInteger.ONE.add(BigInteger.ONE))), this);
            outer = new BinaryFunction(FunctionOperation.DIV, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), Q);
        } else if (type == FunctionType.ATAN) {
            MyFunction S = new BinaryFunction(FunctionOperation.PLUS, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), new BinaryFunction(FunctionOperation.POWER, argument, new ConstantFunction(new IntegerStrategy(BigInteger.ONE.add(BigInteger.ONE)))));
            outer = new BinaryFunction(FunctionOperation.DIV, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), S);
        } else if (type == FunctionType.ACOS) {
            MyFunction S = new BinaryFunction(FunctionOperation.MINUS, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), new BinaryFunction(FunctionOperation.POWER, argument, new ConstantFunction(new IntegerStrategy(BigInteger.ONE.add(BigInteger.ONE)))));
            outer = new BinaryFunction(FunctionOperation.DIV, new ConstantFunction(new IntegerStrategy(BigInteger.ONE.negate())), new UnaryFunction(FunctionType.SQRT, S));
        } else if (type == FunctionType.ASIN) {
            MyFunction S = new BinaryFunction(FunctionOperation.MINUS, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), new BinaryFunction(FunctionOperation.POWER, argument, new ConstantFunction(new IntegerStrategy(BigInteger.ONE.add(BigInteger.ONE)))));
            outer = new BinaryFunction(FunctionOperation.DIV, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), new UnaryFunction(FunctionType.SQRT, S));
        } else if (type == FunctionType.SINH) {
            outer = new UnaryFunction(FunctionType.COSH, argument);
        } else if (type == FunctionType.COSH) {
            outer = new UnaryFunction(FunctionType.SINH, argument);
        } else if (type == FunctionType.UNARYMINUS) {
            outer = new ConstantFunction(new IntegerStrategy(BigInteger.valueOf(-1)));
        } else if (type == FunctionType.CUBEROOT) {
            MyFunction f = new BinaryFunction(FunctionOperation.POWER, argument, new BinaryFunction(FunctionOperation.DIV, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), new ConstantFunction(new DoubleStrategy(3.0))));
            return f.getDerivative();
        } else if (type == FunctionType.TANH) {
            MyFunction P = new BinaryFunction(FunctionOperation.POWER, this, new ConstantFunction(new IntegerStrategy(BigInteger.ONE.add(BigInteger.ONE))));
            outer = new BinaryFunction(FunctionOperation.MINUS, new ConstantFunction(new IntegerStrategy(BigInteger.ONE)), P);
        } else
            throw new MyParserException(MyParserException.type.INCORRECTARGUMENTS, "Incorrect type of arguments.");
        return new BinaryFunction(FunctionOperation.MULT, outer, inner);
    }

    public int precedense() {
        return 0;
    }

    public MyFunction extractNonConstant(FunctionOperation A, FunctionOperation B) {
        return this;
    }

    public MyFunction extractNumber(FunctionOperation A, FunctionOperation B) {
        return null;
    }

    protected FunctionOperation ConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }

    protected FunctionOperation NonConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }

    protected MyFunction extractNumbers(FunctionOperation A, FunctionOperation B) throws MyParserException {
        return this;
    }

    public MyFunction compose(MyFunction f) throws MyParserException {
        return new UnaryFunction(type, argument.compose(f)).simplify();
    }
}

