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

abstract class MyFunction implements Cloneable{
    public abstract ValueStrategy eval(double d) throws MyParserException;
    public abstract ComplexStrategy eval(double re, double im) throws MyParserException;
    protected abstract String printFormat(int precision) throws MyParserException;
    public abstract boolean isConstant();
    public abstract MyFunction simplify() throws MyParserException;
    public abstract MyFunction getDerivative() throws MyParserException ;
    public abstract int precedense();
    public abstract MyFunction symmetry() throws MyParserException;
    public abstract MyFunction extractNumber(FunctionOperation A, FunctionOperation B);
    public abstract MyFunction extractNonConstant(FunctionOperation A, FunctionOperation B);
    protected abstract FunctionOperation ConstantOp(FunctionOperation A, FunctionOperation B);
    protected abstract FunctionOperation NonConstantOp(FunctionOperation A, FunctionOperation B);
    protected abstract MyFunction extractNumbers(FunctionOperation A,FunctionOperation B) throws MyParserException;
    public String print(int precision) throws MyParserException {
        return printFormat(precision);
    }
    public abstract MyFunction compose(MyFunction f) throws MyParserException;
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

class ConstantFunction extends MyFunction {
    private ValueStrategy value;

    public ValueStrategy eval(double d) {return value;}
    public ComplexStrategy eval(double re, double im) {
        if (value instanceof ComplexStrategy) return (ComplexStrategy)value;
        else return new ComplexStrategy(value.getDouble(), 0.0);
    }
    public ConstantFunction(ValueStrategy value) {
        this.value = value;
    }

    public MyFunction symmetry() throws MyParserException {
        return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
    }

    public String printFormat(int precision) throws MyParserException {
        return value.print(precision);
    }

    public boolean isConstant() {return true;}
    public MyFunction simplify() {return this;}
    public MyFunction getDerivative() throws MyParserException {return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));}
    public int precedense() {return 0;}

    public MyFunction extractNonConstant(FunctionOperation A, FunctionOperation B){
        return null;
    }
    public MyFunction extractNumber(FunctionOperation A, FunctionOperation B){
        return this;
    }

    protected FunctionOperation ConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }

    protected FunctionOperation NonConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }

    protected MyFunction extractNumbers(FunctionOperation A,FunctionOperation B) throws MyParserException{
        return this;
    }

    public MyFunction compose(MyFunction f) throws MyParserException {return this;}
}

class XFunction extends MyFunction {
    public ValueStrategy eval(double d) throws MyParserException {return new DoubleStrategy(d);}
    public ComplexStrategy eval(double re, double im) {return new ComplexStrategy(re, im);}
    public XFunction() {}
    public MyFunction symmetry() {return new ConstantFunction(new IntegerStrategy(new BigInteger("-1")));}
    public String printFormat(int precision) throws MyParserException {
        return "x";
    }
    public boolean isConstant() {return false;}
    public MyFunction simplify() {return this;}
    public MyFunction getDerivative() throws MyParserException {return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));}
    public int precedense() {return 0;}
    public MyFunction extractNonConstant(FunctionOperation A, FunctionOperation B){
        return this;
    }
    public MyFunction extractNumber(FunctionOperation A, FunctionOperation B){
        return null;
    }
    protected FunctionOperation ConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }
    protected FunctionOperation NonConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }

    protected MyFunction extractNumbers(FunctionOperation A,FunctionOperation B) throws MyParserException {
        return this;
    }

    public MyFunction compose(MyFunction f) throws MyParserException {return f;}
}

class BinaryFunction extends MyFunction {
    private FunctionOperation Op;
    private MyFunction Left;
    private MyFunction Right;

    public BinaryFunction(FunctionOperation Op,MyFunction Left,MyFunction Right) {
        this.Op    = Op;
        this.Left  = Left;
        this.Right = Right;
    }

    @Override
    public MyFunction symmetry() throws MyParserException {
        if (this.Op == FunctionOperation.PLUS || this.Op == FunctionOperation.MINUS) {
            if (Left.symmetry().eval(0.0).getDouble() > 0.5 && Right.symmetry().eval(0.0).getDouble() > 0.5) {
                return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
            }
            else if (Left.symmetry().eval(0.0).getDouble() < -0.5 && Right.symmetry().eval(0.0).getDouble() < -0.5) {
                return new ConstantFunction(new IntegerStrategy(new BigInteger("-1")));
            }
        }
        else if(this.Op == FunctionOperation.MULT || this.Op == FunctionOperation.DIV) {
            if (Left.symmetry().eval(0.0).getDouble() * Right.symmetry().eval(0.0).getDouble() > 0.5) {
                return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
            }
            else if (Left.symmetry().eval(0.0).getDouble() * Right.symmetry().eval(0.0).getDouble() < -0.5) {
                return new ConstantFunction(new IntegerStrategy(new BigInteger("-1")));
            }
        }
        else if (this.Op == FunctionOperation.POWER) {
            if (Right.isConstant()) {
                if (Right.eval(0.0).isInteger()) {
                    if (Right.eval(0.0).getInteger().mod(new BigInteger("2")).equals(BigInteger.ZERO)) {
                        return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
                    }
                    else if (!Left.symmetry().eval(0.0).getInteger().equals(BigInteger.ZERO)) {
                        return Left.symmetry();
                    }
                }
            }
            return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
        }
        return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
    }

    public ValueStrategy eval(double d) throws MyParserException {
        ValueStrategy LS = Left.eval(d), RS = Right.eval(d);
        ValueStrategy result = LS.performOperation(Op,RS);
        if (result.isInteger()) return new IntegerStrategy(result.getInteger());
        else return result;
    }

    public ComplexStrategy eval(double re, double im) throws MyParserException {
        ComplexStrategy lhs = Left.eval(re, im), rhs = Right.eval(re, im);
        return lhs.performComplexOperation(Op, rhs);
    }

    private boolean needBrackets() {
        if (Right.precedense()>this.precedense()) return true;
        else if (Right.precedense()==this.precedense()) {
            return Op == FunctionOperation.MINUS || Op == FunctionOperation.DIV;
        }
        else return false;
    }

    public String printFormat(int precision) throws MyParserException {
        String str = "";
        if (Left.precedense()>this.precedense()) str = str + "(";
        str = str + Left.printFormat(precision);
        if (Left.precedense()>this.precedense()) str = str + ")";
        if (Op == FunctionOperation.PLUS) str = str + "+";
        else if (Op == FunctionOperation.MINUS) str = str + "-";
        else if (Op == FunctionOperation.MULT) str = str + "*";
        else if (Op == FunctionOperation.DIV) str = str + "/";
        else if (Op == FunctionOperation.POWER) str = str + "^";
        if (needBrackets()) str = str + "(";
        str = str + Right.printFormat(precision);
        if (needBrackets()) str = str + ")";
        if (Op == FunctionOperation.LOG || Op == FunctionOperation.BESSEL1 || Op == FunctionOperation.BESSEL2) {
            if (Op == FunctionOperation.LOG) str = "log";
            else if (Op == FunctionOperation.BESSEL1) str = "bessel1";
            else if (Op == FunctionOperation.BESSEL2) str = "bessel2";
            str += "(" + Left.printFormat(precision) + "," + Right.printFormat(precision) + ")";
        }
        return str;
    }

    public boolean isConstant() {return false;}

    private MyFunction getLine(MyFunction m, MyFunction b) throws MyParserException {
        return FunctionArithmetics.add(FunctionArithmetics.multiply(m, new XFunction()), b);
    }

    public MyFunction extractNumber(FunctionOperation A, FunctionOperation B) {
        if (Op == A || Op == B) {
            if (Left.isConstant()) {
                return Left;
            }
            else if (Right.isConstant()) {
                return Right;
            }
        }
        return null;
    }

    public MyFunction extractNonConstant(FunctionOperation A, FunctionOperation B) {
        if (Op == A || Op == B) {
            if (Left.isConstant()) {
                return Right;
            }
            else if (Right.isConstant()) {
                return Left;
            }
        }

        return this;
    }

    private MyFunction combine(FunctionOperation A,FunctionOperation B,FunctionOperation Op1,FunctionOperation Op2,MyFunction f,MyFunction g) throws MyParserException {
        if (f != null) {
            if (g != null) {
                MyFunction C;
                if (Op1 == Op2) C = new BinaryFunction(A,f,g);
                else if (Op1 == A) C = new BinaryFunction(B,f,g);
                else C = new BinaryFunction(B,g,f);
                return C;
            }
            else return f;
        }
        else if (g != null) return g;
        else return null;
    }

    protected FunctionOperation ConstantOp(FunctionOperation A, FunctionOperation B) {
        if (Op==B && Right.isConstant()) return B;
        else return A;
    }

    protected FunctionOperation NonConstantOp(FunctionOperation A, FunctionOperation B) {
        if (Op==B && Left.isConstant()) return B;
        else return A;
    }

    private FunctionOperation getCombiOp(FunctionOperation A, FunctionOperation B,FunctionOperation Op1,FunctionOperation Op2,MyFunction f,MyFunction g) {
        if (Op1==Op2 && Op2 == B) return B;
        else if (f == null && g != null) return Op2;
        else if (f != null && g == null) return Op1;
        else return A;
    }

    protected MyFunction extractNumbers(FunctionOperation A, FunctionOperation B) throws MyParserException {
        MyFunction number1 = null, number2 = null;
        MyFunction result = null, remnant = null;
        FunctionOperation OpN1 = A, OpN2 = A, OpF1 = A, OpF2 = A, OpR1 = A, OpR2 = A;

        if (Op == A || Op == B) {
            number1 = Left.extractNumber(A,B);
            OpN1 = Left.ConstantOp(A,B);
            number2 = Right.extractNumber(A,B);
            OpN2 = Right.ConstantOp(A,B);
            OpF1 = Left.NonConstantOp(A,B);
            Left = Left.extractNonConstant(A,B);
            OpF2 = Right.NonConstantOp(A,B);
            Right = Right.extractNonConstant(A,B);

            if (Op == B) {
                if (OpN2 == A) OpN2 = B;
                else OpN2 = A;
                if (OpF2 == A) OpF2 = B;
                else OpF2 = A;
            }

            OpR1 = getCombiOp(A,B,OpN1,OpN2,number1,number2);
            OpR2 = getCombiOp(A,B,OpF1,OpF2,Left,Right);
            result=combine(A,B,OpN1,OpN2,number1,number2);
            if (result!=null) result = result.simplify();
            remnant=combine(A,B,OpF1,OpF2,Left,Right);
            result=combine(A,B,OpR1,OpR2,result,remnant);
        }
        else result = this;
        return result;
    }

    public MyFunction simplify() throws MyParserException {
        Left = Left.simplify();
        Right = Right.simplify();
        if (Op == FunctionOperation.MULT && Left instanceof MatrixAdapter && Right instanceof MatrixAdapter) {
            return ((MatrixAdapter)Left).multiply((MatrixAdapter)Right);
        }
        else if (Op == FunctionOperation.WRITEFILE) {
            try {
                java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(FunctionalString.completeRelativePath(Left.print(6))));
                writer.write(Right.print(6));

                writer.close();
                return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
            }
            catch (java.io.IOException e) {
                return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
            }
        }
        else if (Op == FunctionOperation.MULT && (
                (Left.isConstant() && Left.eval(0.0).getDouble()==0.0)
                        || (Right.isConstant() && Right.eval(0.0).getDouble()==0.0))) {
            return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
        }
        else if (Op == FunctionOperation.MAX) {
            if (FunctionArithmetics.smaller(Left, Right, 0.00001).eval(0.0).getDouble() > 0.5) return Right;
            else return  Left;
        }
        else if (Op == FunctionOperation.BESSEL1) {
            if (Right.isConstant() && Left.isConstant()) {
                return new ConstantFunction(new DoubleStrategy(cern.jet.math.Bessel.jn(Left.eval(0.0).getInteger().intValue(), Right.eval(0.0).getDouble())));
            } else return this;
        }
        else if (Op == FunctionOperation.BESSEL2) {
            if (Right.isConstant() && Left.isConstant()){
                return new ConstantFunction(new DoubleStrategy(cern.jet.math.Bessel.yn(Left.eval(0.0).getInteger().intValue(), Right.eval(0.0).getDouble())));
            }
            else return this;
        }
        else if (Op == FunctionOperation.MIN) {
            if (FunctionArithmetics.smaller(Left, Right, 0.00001).eval(0.0).getDouble() > 0.5) return Left;
            else return Right;
        }
        else if (Op == FunctionOperation.MULT && Left.isConstant() && !(Left.eval(0.0) instanceof ComplexStrategy) && Left.eval(0.0).getDouble() == 1.0) {
            return Right;
        }
        else if (Op == FunctionOperation.MULT && Right.isConstant() && !(Right.eval(0.0) instanceof ComplexStrategy) && Right.eval(0.0).getDouble() == 1.0) {
            return Left;
        }
        else if (Op == FunctionOperation.PLUS && Left instanceof MatrixAdapter && Right instanceof MatrixAdapter) {
            return ((MatrixAdapter)Left).add((MatrixAdapter)Right);
        }
        else if (Op == FunctionOperation.PLUS && Left.isConstant() && Left.eval(0.0).getDouble()==0.0) {
            return Right;
        }
        else if (Op == FunctionOperation.PLUS && Right.isConstant() && Right.eval(0.0).getDouble()==0.0) {
            return Left;
        }
        else if (Op == FunctionOperation.PLUS && Right.isConstant() && Right.eval(0.0).getDouble()<0.0) {
            ValueStrategy vStr = Right.eval(0.0);
            vStr = vStr.negate();
            Op = FunctionOperation.MINUS; Right = new ConstantFunction(vStr);
            return this.simplify();
        }
        else if (Op == FunctionOperation.MINUS && Left instanceof MatrixAdapter && Right instanceof MatrixAdapter) {
            return ((MatrixAdapter)Left).subtract((MatrixAdapter)Right);
        }
        else if (Op == FunctionOperation.MINUS && Right.isConstant() && Right.eval(0.0).getDouble()==0.0) {
            return Left;
        }
        else if (Op == FunctionOperation.MINUS && Left.isConstant() && Left.eval(0.0).getDouble()==0.0) {
            return new UnaryFunction(FunctionType.UNARYMINUS,Right);
        }
        else if (Op == FunctionOperation.MINUS  && Right.isConstant() && Right.eval(0.0).getDouble()<0.0) {
            ValueStrategy vStr = Right.eval(0.0);
            vStr = vStr.negate();
            Op = FunctionOperation.PLUS; Right = new ConstantFunction(vStr);
            return this.simplify();
        }
        else if (Op == FunctionOperation.POWER && Right.isConstant() && Right.eval(0.0).getDouble()==1.0) {
            return Left;
        }
        else if (Op == FunctionOperation.POWER && Right.isConstant() && Right.eval(0.0).getDouble()==0.0) {
            return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
        }
        else if (Left.isConstant() && Right.isConstant() && Right.eval(0.0) instanceof ComplexStrategy) {
            if (Left.eval(0.0) instanceof ComplexStrategy) return new ConstantFunction(((ComplexStrategy) Left.eval(0.0)).performComplexOperation(Op, (ComplexStrategy) Right.eval(0.0)));
            else return new ConstantFunction((new ComplexStrategy(Left.eval(0.0).getDouble(), 0.0).performOperation(Op, Right.eval(0.0))));
        }
        else if (Left.isConstant() && Right.isConstant()) {
            return new ConstantFunction(eval(0.0));
        }
        else if (Op == FunctionOperation.EVAL) {
            return new ConstantFunction(Left.eval(Right.eval(0.0).getDouble()));
        }
        else if (Op == FunctionOperation.TANGENT) {
            if (!Right.isConstant()) throw new MyParserException(MyParserException.type.NOTNUMBER, Right.print(6) + " is not a number.");
            MyFunction m, b;
            MyFunction der = Left.getDerivative();
            m = der.compose(Right);
            b = FunctionArithmetics.subtract(Left.compose(Right),FunctionArithmetics.multiply(m,Right));
            if (nanCheck(m.eval(0.0).getDouble()) || nanCheck(b.eval(0.0).getDouble())) throw new MyParserException(MyParserException.type.NOTDEFINEDATPLACE, Left.print(6) + " does not have a tangent line at " + Right.print(6) + ".");
            return getLine(m,b);
        }
        else if (Op == FunctionOperation.NORMAL) {
            if (!Right.isConstant()) throw new MyParserException(MyParserException.type.NOTNUMBER, Right.print(6) + " is not a number.");
            MyFunction m, b;
            MyFunction der = Left.getDerivative();
            m = der.compose(Right);
            m= FunctionArithmetics.multiply(new ConstantFunction(new DoubleStrategy(-1.0)), FunctionArithmetics.power(m,new ConstantFunction(new DoubleStrategy(-1.0))));
            b = FunctionArithmetics.subtract(Left.compose(Right),FunctionArithmetics.multiply(m,Right));
            if (nanCheck(m.eval(0.0).getDouble()) || nanCheck(b.eval(0.0).getDouble())) throw new MyParserException(MyParserException.type.NOTDEFINEDATPLACE, Left.print(6) + " does not have a normal line at " + Right.print(6) + ".");
            return getLine(m,b);
        }
        else {
            MyFunction f = extractNumbers(FunctionOperation.PLUS,FunctionOperation.MINUS);
            return f.extractNumbers(FunctionOperation.MULT,FunctionOperation.DIV);
        }
    }

    private boolean nanCheck(double d) {
        return d != d;
    }

    public MyFunction getDerivative() throws MyParserException {
        MyFunction LeftDer = Left.getDerivative(), RightDer = Right.getDerivative();
        MyFunction res = null;
        if (Op == FunctionOperation.PLUS || Op == FunctionOperation.MINUS) {
            res = new BinaryFunction(Op, LeftDer, RightDer);
        }
        else if (Op == FunctionOperation.MULT) {
            MyFunction L = new BinaryFunction(FunctionOperation.MULT,Left,RightDer), R = new BinaryFunction(FunctionOperation.MULT,LeftDer,Right);
            res = new BinaryFunction(FunctionOperation.PLUS,L,R);
        }
        else if (Op == FunctionOperation.DIV) {
            MyFunction R = new BinaryFunction(FunctionOperation.MULT,Left,RightDer), L = new BinaryFunction(FunctionOperation.MULT,LeftDer,Right);
            MyFunction P = new BinaryFunction(FunctionOperation.MINUS,L,R), Q = new BinaryFunction(FunctionOperation.POWER,Right,new ConstantFunction(new IntegerStrategy(BigInteger.ONE.add(BigInteger.ONE))));
            res = new BinaryFunction(FunctionOperation.DIV,P,Q);
        }
        else if (Op == FunctionOperation.POWER) {
            try {
                MyFunction a = new BinaryFunction(FunctionOperation.MULT, (MyFunction)Right.clone(), new BinaryFunction(FunctionOperation.MULT, new BinaryFunction(FunctionOperation.POWER, (MyFunction)Left.clone(), new BinaryFunction(FunctionOperation.MINUS, (MyFunction)Right.clone(), new ConstantFunction(new IntegerStrategy(BigInteger.ONE)))), Left.getDerivative()));
                MyFunction b = new BinaryFunction(FunctionOperation.MULT, new BinaryFunction(FunctionOperation.POWER, (MyFunction)Left.clone(), (MyFunction)Right.clone()), new BinaryFunction(FunctionOperation.MULT, Right.getDerivative(), new UnaryFunction(FunctionType.LN, (MyFunction)Left.clone())));
                res = new BinaryFunction(FunctionOperation.PLUS, a, b);
            }
            catch (CloneNotSupportedException e) {
                throw new MyParserException(MyParserException.type.INCORRECTINPUT,"can not get derivative");
            }
        }
        else if (Op == FunctionOperation.LOG) {
            MyFunction translate = new BinaryFunction(FunctionOperation.DIV,new UnaryFunction(FunctionType.LN,Right),new UnaryFunction(FunctionType.LN,Left));
            return translate.getDerivative();
        }
        if ((Op == FunctionOperation.GCD || Op == FunctionOperation.LCM) && (!Left.isConstant() || !Right.isConstant())) {
            throw new MyParserException(MyParserException.type.INCORRECTARGUMENTS,"lcm and gcd accept only integers as arguments.");
        }
        if ((Op == FunctionOperation.MOD) && (!Left.isConstant() || !Right.isConstant())) {
            throw new MyParserException(MyParserException.type.INCORRECTARGUMENTS,"% accepts only integers as arguments.");
        }
        return res;
    }

    public int precedense() {
        if (Op==FunctionOperation.POWER) return 1;
        else if (Op == FunctionOperation.DIV || Op == FunctionOperation.MULT) return 2;
        else return 3;
    }

    public MyFunction compose(MyFunction f) throws MyParserException {
        return new BinaryFunction(Op, Left.compose(f),Right.compose(f)).simplify();
    }

}

class IntegralFunction extends MyFunction {
    private MyFunction a,b,f;

    public IntegralFunction(MyFunction f, MyFunction a, MyFunction b) {
        this.f = f; this.a = a; this.b = b;
    }

    @Override
    public MyFunction symmetry() throws MyParserException {
        return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
    }

    private double simpsonRule(double a, double b) throws MyParserException  {
        if (f.eval(a) instanceof ComplexStrategy) throw new MyParserException(MyParserException.type.NOTREAL, f.print(6) + " is not a real function");
        double result = f.eval(a).getDouble();
        result += 4.0*f.eval((a+b)/2).getDouble();
        result += f.eval(b).getDouble();
        result *= (b-a)/6.0;

        return result;
    }

    public ValueStrategy eval(double d) throws MyParserException {
        if (a.eval(0.0) instanceof ComplexStrategy) throw new MyParserException(MyParserException.type.NOTREAL, a.print(6) + " is not a real number");
        if (b.eval(0.0) instanceof ComplexStrategy) throw new MyParserException(MyParserException.type.NOTREAL, b.print(6) + " is not a real number");
        double aVal=a.eval(d).getDouble(), bVal=b.eval(d).getDouble(), result = 0.0, x = aVal, y = aVal+0.1;
        while(y<bVal) {
            result+=simpsonRule(x,y);
            x = y;
            y+=0.1;
        }
        result+=simpsonRule(x,bVal);
        return new DoubleStrategy(result);
    }

    public ComplexStrategy eval(double re, double im) {return new ComplexStrategy(0.0, 0.0);}

    protected String printFormat(int precision) throws MyParserException {
        String str = "integral(";
        f.printFormat(precision);
        str = str + ",";
        str = str + a.printFormat(precision);
        str = str + ",";
        str = str + b.printFormat(precision);
        str = str + ")";
        return str;
    }

    public boolean isConstant() {
        return a.isConstant() && b.isConstant();
    }

    public MyFunction simplify() throws MyParserException {
        f = f.simplify(); a = a.simplify(); b = b.simplify();
        if (!a.isConstant()) throw new MyParserException(MyParserException.type.NOTNUMBER,a.print(6) + " is not a number.");
        if (!b.isConstant()) throw new MyParserException(MyParserException.type.NOTNUMBER,b.print(6) + " is not a number.");
        return new ConstantFunction(eval(0));
    }

    public MyFunction getDerivative() throws MyParserException {return f;}

    public int precedense() {return 0;}

    public MyFunction extractNonConstant(FunctionOperation A, FunctionOperation B){
        return this;
    }
    public MyFunction extractNumber(FunctionOperation A, FunctionOperation B){
        return null;
    }

    protected FunctionOperation ConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }
    protected FunctionOperation NonConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }

    protected MyFunction extractNumbers(FunctionOperation A,FunctionOperation B) throws MyParserException {
        return this;
    }

    public MyFunction compose(MyFunction f) throws MyParserException {
        return new IntegralFunction(this.f.compose(f), a.compose(f), b.compose(f)).simplify();
    }

}

class FunctionArithmetics {

    public static MyFunction add(MyFunction a, MyFunction b) throws MyParserException {
        return new BinaryFunction(FunctionOperation.PLUS,a,b).simplify();
    }

    public static MyFunction subtract(MyFunction a, MyFunction b) throws MyParserException {
        return new BinaryFunction(FunctionOperation.MINUS,a,b).simplify();
    }

    public static MyFunction multiply(MyFunction a, MyFunction b) throws MyParserException {
        return new BinaryFunction(FunctionOperation.MULT,a,b).simplify();
    }

    public static MyFunction divide(MyFunction a, MyFunction b) throws MyParserException {
        return new BinaryFunction(FunctionOperation.DIV,a,b).simplify();
    }

    public static MyFunction power(MyFunction a, MyFunction b) throws MyParserException {
        return new BinaryFunction(FunctionOperation.POWER,a,b).simplify();
    }

    public static MyFunction mod(MyFunction a, MyFunction b) {
        return new BinaryFunction(FunctionOperation.MOD,a,b);
    }

    public static MyFunction compare(MyFunction a, MyFunction b, double precision) throws MyParserException {
        if (a.isConstant() && b.isConstant()) {
            if (Math.abs(a.eval(0.0).getDouble() - b.eval(0.0).getDouble()) <  precision)
                return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
        }
        return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
    }

    public static MyFunction notEq(MyFunction a, MyFunction b, double precision) throws MyParserException {
        if (a.isConstant() && b.isConstant()) {
            if (Math.abs(a.eval(0.0).getDouble() - b.eval(0.0).getDouble()) >  precision)
                return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
        }
        return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
    }

    public static MyFunction smaller(MyFunction a, MyFunction b, double precision) throws MyParserException {
        if (a.isConstant() && b.isConstant()) {
            if ((b.eval(0.0).getDouble() - a.eval(0.0).getDouble()) > precision)
                return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
        }
        return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
    }

    public static MyFunction smallEq(MyFunction a, MyFunction b, double precision) throws MyParserException {
        if (a.isConstant() && b.isConstant()) {
            if ((b.eval(0.0).getDouble() - a.eval(0.0).getDouble()) > -precision)
                return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
        }
        return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
    }

    public static MyFunction and(MyFunction a, MyFunction b, double precision) throws MyParserException {
        if (a.isConstant() && b.isConstant()) {
            if (Math.abs(b.eval(0.0).getDouble()) < precision || Math.abs(a.eval(0.0).getDouble()) < precision)
                return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
        }
        return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
    }

    public static MyFunction or(MyFunction a, MyFunction b, double precision) throws MyParserException {
        if (a.isConstant() && b.isConstant()) {
            if (Math.abs(b.eval(0.0).getDouble()) < precision && Math.abs(a.eval(0.0).getDouble()) < precision)
                return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
        }
        return new ConstantFunction(new IntegerStrategy(BigInteger.ONE));
    }

}

