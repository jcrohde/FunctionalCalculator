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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListCellRenderer;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import java.awt.event.KeyEvent;
import java.net.URL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.nio.file.Files;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.imageio.ImageIO;

class MyCellRenderer extends JLabel implements ListCellRenderer {

    public MyCellRenderer() {
        setOpaque(true);
    }

    public JComponent getListCellRendererComponent(JList list, Object value, int index,
                                                   boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            if (index%2 == 0) {
                setBackground(list.getBackground());
            }
            else {
                setBackground(new java.awt.Color(230,230,255));
            }
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        return this;
    }
}

class CalcFrame extends JFrame {
    public CalcFrame() {

        ImageIcon image = new ImageIcon(getClass().getResource("icons/FCLogo.png"));
        setIconImage(image.getImage());
    }
}

public class FunctionalCalculator {

    static public FuParser MyParser = new FuParser();
    static public JEditorPane OutPut;
    static private DialogGenerator matDia = new DialogGenerator();
    static public scalarDialogGenerator scalarDia = new scalarDialogEditGenerator();
    static private scalarDialogSpinGenerator settingDia = new scalarDialogSpinGenerator();
    static private aboutDialogGenerator aboutGenerator = new aboutDialogGenerator();
    static private licenseDialogGenerator licenseGenerator = new licenseDialogGenerator();
    static public plotDialogGenerator plotGenerator = new plotDialogGenerator();
    static public ComplexPlotDialogGenerator complexPlotGenerator = new ComplexPlotDialogGenerator();
    static private helpDialogGenerator helpGenerator = new helpDialogGenerator();

    static private boolean atBegin=true;
    static private String result="";
    static private String pfad="";

    static public String shortProgramName = "FunctionalCalculator";
    static private String longProgramName = "FunctionalCalculator 1.3";
    static private String licenseName = "GNU General Public License 3";

    static private int rows;
    static private int columns;
    static private int precision;

    static private int inputIndex;

    private static void showErrorMessage(final JFrame f,String Message) {
        JOptionPane.showMessageDialog(f,
                Message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private static void saveOutPut(final JFrame f,final JEditorPane OutPut) {
        JFileChooser chooser;
        pfad = System.getProperty("user.home");
        File file = new File(pfad.trim());
        chooser = new JFileChooser(pfad);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        FileNameExtensionFilter plainFilter = new FileNameExtensionFilter(
                "Plaintext: txt", "txt");
        FileNameExtensionFilter markUpFilter = new FileNameExtensionFilter(
                "Markup: html", "html");
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileFilter(plainFilter);
        chooser.setFileFilter(markUpFilter);
        chooser.setDialogTitle("Save as...");
        chooser.setVisible(true);

        int result = chooser.showSaveDialog(f);

        if (result == JFileChooser.APPROVE_OPTION) {

            pfad = chooser.getSelectedFile().toString();
            file = new File(pfad);
            if (plainFilter.accept(file) || markUpFilter.accept(file)) {
                try {
                    PrintWriter writer = new PrintWriter(file);

                    String txt = OutPut.getText();
                    txt = txt.substring(txt.indexOf("<body>")+6,txt.indexOf("</body>"));
                    txt = txt.replace("<span>","<span style=\"font-weight:200\">");
                    txt = "<head><title>" + pfad + "</title></head><body>" + txt
                            + "<br><br><br><i>This file has been edited with " + longProgramName + ".</i></body>";

                    writer.printf(txt);
                    writer.close();
                }
                catch (IOException e) {
                    showErrorMessage(f,"IOException");
                }
            }
            else {
                showErrorMessage(f,pfad + " is of wrong file type.");
                pfad = "";
            }
            chooser.setVisible(false);

        }
        else {
            chooser.setVisible(false);
            pfad = "";
        }

    }

    private static void insert(final JTextField InPut, String insertText) {
        InPut.setText(InPut.getText()+insertText);
        InPut.requestFocus();
    }

    private static void performComputationsOnVariables(final JFrame f, final String txt) {
        String subString;
        int index, end, fromIndex = 0;

        try {
            while (fromIndex>-1) {
                index = txt.indexOf("input:",fromIndex);
                fromIndex = index;
                if (index>-1) {
                    fromIndex++;
                    end = txt.indexOf("<br>",index);
                    if (end>-1) {
                        subString=txt.substring(index+7,end);
                        if (subString.indexOf("=")>-1) MyParser.parse(subString,6);
                    }
                }
            }
        }
        catch (MyParserException Exception) {
            showErrorMessage(f,Exception.message);
        }
    }

    private static void setResult(String txt) {
        int beg,end;

        beg = txt.lastIndexOf("=");
        if (beg>-1) {
            beg = beg + 2;
            end = txt.indexOf("<br>");
            if (end>-1 && end>beg) result=txt.substring(beg,end);
        }
    }

    private static void performSLE (matrix m,final JEditorPane OutPut) throws MyParserException {
        if (m != null) {
            String matrixStr = m.printAsSLE();
            matrixStr = matrixStr+"after Gauss algorithm:";
            m = m.gauss();
            matrixStr = matrixStr+m.printAsSLE();
            matrixStr = matrixStr+"Interpretation: The set of solution is given by:";
            matrixStr = matrixStr + m.interpreteAsSolution();
            if (atBegin) atBegin=false;
            else {
                String prev = OutPut.getText(); prev = prev.substring(prev.indexOf("<body>")+6,prev.indexOf("</body>"));
                prev = prev.replace("<span>","<span style=\"font-weight:200\">");
                matrixStr = prev + "<br><br>"+matrixStr;
            }
            OutPut.setText(matrixStr);
            inputIndex = matrixStr.length();
        }
    }

    public static void printOut(String name, String value) {

        String prev = OutPut.getText(), str = "input: " + CalcDialog.escapeHTML(name) + "<br> = <font color=\"#0000FF\">" + value + "</font>";
        if (atBegin) atBegin = false;
        else {
            prev = prev.substring(prev.indexOf("<body>") + 6, prev.indexOf("</body>"));
            prev = prev.replace("<span>", "<span style=\"font-weight:200\">");
            str = prev + "<br><br>" + str;
        }
        OutPut.setText(str);
        inputIndex = str.length();
    }

    public static void main (String[] args) {
        rows = 2;
        columns = 2;
        precision = 6;

        final CalcFrame f = new CalcFrame();
        f.setTitle(shortProgramName);

        String rootPath = System.getProperty("user.dir");
        String imgPath = rootPath + File.separator;

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());

        inputIndex = 0;
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        Image im = Toolkit.getDefaultToolkit().getImage("icons/new.png");
        JMenuItem newItem = new JMenuItem("new",new ImageIcon(im));
        fileMenu.add(newItem);
        im = Toolkit.getDefaultToolkit().getImage("icons/open.png");
        JMenuItem openItem = new JMenuItem("open",new ImageIcon(im));
        fileMenu.add(openItem);
        im = Toolkit.getDefaultToolkit().getImage("icons/save.png");
        JMenuItem saveItem = new JMenuItem("save",new ImageIcon(im));
        fileMenu.add(saveItem);
        im = Toolkit.getDefaultToolkit().getImage("icons/saveAs.png");
        JMenuItem saveAsItem = new JMenuItem("saveAs",new ImageIcon(im));
        fileMenu.add(saveAsItem);
        fileMenu.add(new JSeparator());
        JMenuItem quitItem = new JMenuItem("quit");
        fileMenu.add(quitItem);
        menu.add(fileMenu);

        JMenu settingMenu = new JMenu("Settings");
        JMenuItem precisionItem = new JMenuItem("precision");
        settingMenu.add(precisionItem);
        JMenuItem rowColumnItem = new JMenuItem("rows / columns of matrices");
        settingMenu.add(rowColumnItem);
        menu.add(settingMenu);

        JMenu plotMenu = new JMenu("Plot");
        im = Toolkit.getDefaultToolkit().getImage("icons/plot.png");
        JMenuItem plotItem = new JMenuItem("plot real functions",new ImageIcon(im));
        plotMenu.add(plotItem);
        JMenuItem plotComplexItem = new JMenuItem("plot complex functions",new ImageIcon(im));
        plotMenu.add(plotComplexItem);
        menu.add(plotMenu);

        JMenu helpMenu = new JMenu("Help");
        im = Toolkit.getDefaultToolkit().getImage("icons/help.png");
        JMenuItem helpItem = new JMenuItem(shortProgramName + " Help",new ImageIcon(im));
        helpMenu.add(helpItem);
        helpMenu.add(new JSeparator());
        JMenuItem licenseItem = new JMenuItem("license");
        helpMenu.add(licenseItem);
        JMenuItem aboutItem = new JMenuItem("about " + shortProgramName);
        helpMenu.add(aboutItem);
        menu.add(helpMenu);
        f.setJMenuBar(menu);

        JToolBar tbar = new JToolBar();
        tbar.setSize(330, 30);

        JButton integralButton = new JButton("integral");
        tbar.add(integralButton);
        JButton derivativeButton = new JButton("derivative");
        tbar.add(derivativeButton);
        JButton conditionButton = new JButton("condition");
        tbar.add(conditionButton);
        JButton gcdButton = new JButton("gcd");
        tbar.add(gcdButton);
        JButton matrixButton = new JButton("system of linear equations");
        tbar.add(matrixButton);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());


        String labels[] = {"+ for matrices", "- for matrices", "* for matrices","abs","AND","arg",
                "acos","asin","atan","bessel1", "bessel2", "binomial", "characteristic polynomial of a matrix","ceil","condition","conj",
                "cos","cosh","cot","cubeRoot","digamma","declare a matrix","deleteFile","derivative","determinant of a matrix",
                "exp","faculty","floor","frac","Gauss algorithm for a matrix","gamma", "gcd", "getEntry of a matrix",
                "im", "integral", "interpret","inverse matrix", "isconst","lcm","ln","log","log2","max", "min",
                "minimal polynomial of a matrix","nextPrime","normal", "OR","phi","plot","plotComplex","prime",
                "rank of a matrix","re","readFile","round","setEntry of a matrix","sin","sinh","sqrt","symmetry",
                "system of linear equations","tan","tanh","tangent", "trace of a matrix","trigamma","variable","writeFile",
                "==", "!=","<","<=",">",">="};

        final JList functionList = new JList(labels);

        functionList.setCellRenderer(new MyCellRenderer());
        JScrollPane scrollPane = new JScrollPane(functionList);

        JPanel Tabs = new JPanel();
        Tabs.setLayout(new java.awt.GridLayout(6,4));

        JButton CButton = new JButton("C");
        Tabs.add(CButton);
        JButton facButton = new JButton("!");
        Tabs.add(facButton);
        JButton ModButton = new JButton("%");
        Tabs.add(ModButton);
        JButton PowerButton = new JButton("^");
        Tabs.add(PowerButton);
        JButton SevenButton = new JButton("7");
        Tabs.add(SevenButton);
        JButton EightButton = new JButton("8");
        Tabs.add(EightButton);
        JButton NineButton = new JButton("9");
        Tabs.add(NineButton);
        JButton DivButton = new JButton("/");
        Tabs.add(DivButton);
        JButton FourButton = new JButton("4");
        Tabs.add(FourButton);
        JButton FiveButton = new JButton("5");
        Tabs.add(FiveButton);
        JButton SixButton = new JButton("6");
        Tabs.add(SixButton);
        JButton MultButton = new JButton("*");
        Tabs.add(MultButton);
        JButton OneButton = new JButton("1");
        Tabs.add(OneButton);
        JButton TwoButton = new JButton("2");
        Tabs.add(TwoButton);
        JButton ThreeButton = new JButton("3");
        Tabs.add(ThreeButton);
        JButton MinusButton = new JButton("-");
        Tabs.add(MinusButton);
        JButton PointButton = new JButton(".");
        Tabs.add(PointButton);
        JButton ZeroButton = new JButton("0");
        Tabs.add(ZeroButton);
        JButton AssignmentButton = new JButton("=");
        Tabs.add(AssignmentButton);
        JButton PlusButton = new JButton("+");
        Tabs.add(PlusButton);
        JButton XButton = new JButton("x");
        Tabs.add(XButton);
        JButton iButton = new JButton("i");
        Tabs.add(iButton);
        JButton BrackOpButton = new JButton("(");
        Tabs.add(BrackOpButton);
        JButton BrackClButton = new JButton(")");
        Tabs.add(BrackClButton);

        Tabs.setBorder(BorderFactory.createRaisedBevelBorder());

        Tabs.setPreferredSize(new java.awt.Dimension(160, 240));
        Tabs.setMinimumSize(new java.awt.Dimension(160, 240));
        Tabs.setMaximumSize(new java.awt.Dimension(160, 240));

        JPanel generalTabs = new JPanel();
        generalTabs.setLayout(new BorderLayout());
        generalTabs.add(Tabs, BorderLayout.CENTER);

        JPanel Texts = new JPanel();
        Texts.setLayout(new BorderLayout());

        OutPut = new JEditorPane();
        OutPut.setBackground(new java.awt.Color(220,230,230));
        OutPut.setBorder(BorderFactory.createLoweredBevelBorder());
        OutPut.setContentType("text/html");
        OutPut.setEditable(false);
        JScrollPane outPutScrollPane = new JScrollPane(OutPut);

        final JTextField InPut = new JTextField();
        InPut.setPreferredSize(new java.awt.Dimension(200, 30));
        InPut.setMinimumSize(new java.awt.Dimension(0, 30));
        InPut.setMaximumSize(new java.awt.Dimension(1000, 30));

        Texts.add(outPutScrollPane, BorderLayout.CENTER);
        Texts.add(InPut, BorderLayout.SOUTH);

        leftPanel.add(generalTabs, BorderLayout.SOUTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        centerPanel.add(leftPanel, BorderLayout.WEST);
        centerPanel.add(Texts, BorderLayout.CENTER);

        f.getContentPane().add(tbar, BorderLayout.NORTH);
        f.getContentPane().add(centerPanel, BorderLayout.CENTER);

        newItem.addActionListener(new ActionListener() {
                                      @Override public void actionPerformed(ActionEvent event) {
                                          OutPut.setText("");
                                          result = "";
                                          atBegin = true;
                                          MyParser.clearVariables();
                                      }
                                  }
        );

        openItem.addActionListener(new ActionListener() {
                                       @Override public void actionPerformed(ActionEvent event) {
                                           JFileChooser chooser;
                                           String filename = System.getProperty("user.home");
                                           File file = new File(pfad.trim());
                                           chooser = new JFileChooser(pfad);
                                           chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                                           FileNameExtensionFilter plainFilter = new FileNameExtensionFilter(
                                                   "Plaintext: txt", "txt");
                                           FileNameExtensionFilter markUpFilter = new FileNameExtensionFilter(
                                                   "Markup: html", "html");
                                           chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
                                           chooser.setFileFilter(plainFilter);
                                           chooser.setFileFilter(markUpFilter);
                                           chooser.setDialogTitle("Open...");
                                           chooser.setVisible(true);

                                           int iresult = chooser.showOpenDialog(f);

                                           if (iresult == JFileChooser.APPROVE_OPTION) {

                                               if (filename != null) {


                                                   filename = chooser.getSelectedFile().toString();
                                                   try {
                                                       BufferedReader reader =new BufferedReader(new FileReader(filename));
                                                       String line = null, txt="";
                                                       while ((line = reader.readLine()) != null) {
                                                           txt = txt +line;
                                                       }
                                                       txt = txt.substring(txt.indexOf("<body>")+6,txt.indexOf("</body>"));
                                                       performComputationsOnVariables(f,txt);
                                                       if (txt.length()>0) atBegin=false;
                                                       setResult(txt);
                                                       if (txt.indexOf("<br><br><br>")>-1) txt = txt.substring(0,txt.indexOf("<br><br><br>"));
                                                       OutPut.setText(txt);
                                                       inputIndex = txt.length();
                                                       int index = txt.lastIndexOf("<font color=\"#0000FF\">")+22;
                                                       int index2 = txt.lastIndexOf("</font>");
                                                       result = txt.substring(index,index2);
                                                   } catch (IOException x) {
                                                       showErrorMessage(f, "Can not open " + filename + ". ");
                                                       System.err.println(x);
                                                   }
                                               }
                                           }
                                           chooser.setVisible(false);
                                       }
                                   }
        );

        saveItem.addActionListener(new ActionListener() {
                                       @Override public void actionPerformed(ActionEvent event) {
                                           if (pfad.length() == 0)	saveOutPut(f,OutPut);
                                           else {
                                               File file = new File(pfad);
                                               FileNameExtensionFilter plainFilter = new FileNameExtensionFilter(
                                                       "Plaintext: txt, csv", "txt", "csv");
                                               FileNameExtensionFilter markUpFilter = new FileNameExtensionFilter(
                                                       "Markup: html", "html");
                                               if (plainFilter.accept(file) || markUpFilter.accept(file)) {
                                                   try {
                                                       PrintWriter writer = new PrintWriter(file);

                                                       String txt = OutPut.getText();
                                                       txt = txt.substring(txt.indexOf("<body>")+6,txt.indexOf("</body>"));
                                                       txt = "<head><title>" + pfad + "</title></head><body>" + txt + "</body>";

                                                       writer.printf(txt);
                                                       writer.close();
                                                   }
                                                   catch (IOException e) {
                                                       showErrorMessage(f," Can not save ." + pfad + ".");
                                                   }
                                               }
                                           }
                                       }
                                   }
        );

        saveAsItem.addActionListener(new ActionListener() {
                                         @Override public void actionPerformed(ActionEvent event) {
                                             saveOutPut(f,OutPut);
                                         }
                                     }
        );

        quitItem.addActionListener(new ActionListener() {
                                       @Override public void actionPerformed(ActionEvent event) {
                                           System.exit(0);
                                       }
                                   }
        );

        rowColumnItem.addActionListener(new ActionListener() {
                                            @Override public void actionPerformed(ActionEvent event) {
                                                String newText = settingDia.generate("matrix",shortProgramName,2,1,5);
                                                if (newText.length()>0) {
                                                    rows = Integer.parseInt(newText.substring(0,newText.indexOf(",")));
                                                    columns = Integer.parseInt(newText.substring(newText.indexOf(",")+1,newText.length()));
                                                }
                                            }
                                        }
        );

        precisionItem.addActionListener(new ActionListener() {
                                            @Override public void actionPerformed(ActionEvent event) {
                                                String newText = settingDia.generate("precision",shortProgramName,6,2,16);
                                                if (newText.length()>0) {
                                                    precision = Integer.parseInt(newText);
                                                }
                                            }
                                        }
        );

        plotItem.addActionListener(new ActionListener() {
                                       @Override public void actionPerformed(ActionEvent event) {
                                           plotGenerator.exec(MyParser, "", "", "","");
                                       }
                                   }
        );

        plotComplexItem.addActionListener(new ActionListener() {
                                              @Override public void actionPerformed(ActionEvent event) {
                                                  complexPlotGenerator.exec(MyParser,"x");
                                              }
                                          }
        );

        helpItem.addActionListener(new ActionListener() {
                                       @Override public void actionPerformed(ActionEvent event) {
                                           helpGenerator.exec(shortProgramName,licenseName);
                                       }
                                   }
        );

        licenseItem.addActionListener(new ActionListener() {
                                          @Override public void actionPerformed(ActionEvent event) {
                                              licenseGenerator.exec(shortProgramName,licenseName);
                                          }
                                      }
        );

        aboutItem.addActionListener(new ActionListener() {
                                        @Override public void actionPerformed(ActionEvent event) {
                                            aboutGenerator.exec(longProgramName,licenseName);
                                        }
                                    }
        );

        ZeroButton.addActionListener(new ActionListener() {
                                         @Override public void actionPerformed(ActionEvent event) {
                                             insert(InPut,"0");
                                         }
                                     }
        );

        OneButton.addActionListener(new ActionListener() {
                                        @Override public void actionPerformed(ActionEvent event) {
                                            insert(InPut,"1");
                                        }
                                    }
        );

        TwoButton.addActionListener(new ActionListener() {
                                        @Override public void actionPerformed(ActionEvent event) {
                                            insert(InPut,"2");
                                        }
                                    }
        );

        ThreeButton.addActionListener(new ActionListener() {
                                          @Override public void actionPerformed(ActionEvent event) {
                                              insert(InPut,"3");
                                          }
                                      }
        );

        FourButton.addActionListener(new ActionListener() {
                                         @Override public void actionPerformed(ActionEvent event) {
                                             insert(InPut,"4");
                                         }
                                     }
        );

        FiveButton.addActionListener(new ActionListener() {
                                         @Override public void actionPerformed(ActionEvent event) {
                                             insert(InPut,"5");
                                         }
                                     }
        );

        SixButton.addActionListener(new ActionListener() {
                                        @Override public void actionPerformed(ActionEvent event) {
                                            insert(InPut,"6");
                                        }
                                    }
        );

        SevenButton.addActionListener(new ActionListener() {
                                          @Override public void actionPerformed(ActionEvent event) {
                                              insert(InPut,"7");
                                          }
                                      }
        );

        EightButton.addActionListener(new ActionListener() {
                                          @Override public void actionPerformed(ActionEvent event) {
                                              insert(InPut,"8");
                                          }
                                      }
        );

        NineButton.addActionListener(new ActionListener() {
                                         @Override public void actionPerformed(ActionEvent event) {
                                             insert(InPut,"9");
                                         }
                                     }
        );

        PlusButton.addActionListener(new ActionListener() {
                                         @Override public void actionPerformed(ActionEvent event) {
                                             insert(InPut,"+");
                                         }
                                     }
        );

        MinusButton.addActionListener(new ActionListener() {
                                          @Override public void actionPerformed(ActionEvent event) {
                                              insert(InPut,"-");
                                          }
                                      }
        );

        MultButton.addActionListener(new ActionListener() {
                                         @Override public void actionPerformed(ActionEvent event) {
                                             insert(InPut,"*");
                                         }
                                     }
        );

        DivButton.addActionListener(new ActionListener() {
                                        @Override public void actionPerformed(ActionEvent event) {
                                            insert(InPut,"/");
                                        }
                                    }
        );

        PointButton.addActionListener(new ActionListener() {
                                          @Override public void actionPerformed(ActionEvent event) {
                                              insert(InPut,".");
                                          }
                                      }
        );

        AssignmentButton.addActionListener(new ActionListener() {
                                               @Override public void actionPerformed(ActionEvent event) {
                                                   insert(InPut,"=");
                                               }
                                           }
        );

        XButton.addActionListener(new ActionListener() {
                                      @Override public void actionPerformed(ActionEvent event) {
                                          insert(InPut,"x");
                                      }
                                  }
        );

        iButton.addActionListener(new ActionListener() {
                                      @Override public void actionPerformed(ActionEvent event) {insert(InPut,"i");}
                                  }
        );

        BrackOpButton.addActionListener(new ActionListener() {
                                            @Override public void actionPerformed(ActionEvent event) {
                                                insert(InPut,"(");
                                            }
                                        }
        );

        BrackClButton.addActionListener(new ActionListener() {
                                            @Override public void actionPerformed(ActionEvent event) {
                                                insert(InPut,")");
                                            }
                                        }
        );

        PowerButton.addActionListener(new ActionListener() {
                                          @Override public void actionPerformed(ActionEvent event) {
                                              insert(InPut,"^");
                                          }
                                      }
        );

        ModButton.addActionListener(new ActionListener() {
                                        @Override public void actionPerformed(ActionEvent event) {
                                            insert(InPut,"%");
                                        }
                                    }
        );

        facButton.addActionListener(new ActionListener() {
                                        @Override public void actionPerformed(ActionEvent event) {insert(InPut,"!");}
                                    }
        );

        CButton.addActionListener(new ActionListener() {
                                      @Override public void actionPerformed(ActionEvent event) {
                                          OutPut.setText("");
                                          result = "";
                                          atBegin = true;
                                          MyParser.clearVariables();}
                                  }
        );

        integralButton.addActionListener(new ActionListener() {
                                             @Override public void actionPerformed(ActionEvent event) {
                                                 String newText = scalarDia.exec("integral",shortProgramName);
                                                 if (newText.length()>0) InPut.setText(InPut.getText()+newText);
                                                 InPut.requestFocus();
                                             }
                                         }
        );

        derivativeButton.addActionListener(new ActionListener() {
                                               @Override public void actionPerformed(ActionEvent event) {
                                                   String newText = scalarDia.exec("derivative",shortProgramName);
                                                   if (newText.length()>0) InPut.setText(InPut.getText()+newText);
                                                   InPut.requestFocus();
                                               }
                                           }
        );

        conditionButton.addActionListener(new ActionListener() {
                                              @Override public void actionPerformed(ActionEvent event) {
                                                  String newText = scalarDia.exec("condition",shortProgramName);
                                                  if (newText.length()>0) InPut.setText(InPut.getText()+newText);
                                                  InPut.requestFocus();
                                              }
                                          }
        );

        gcdButton.addActionListener(new ActionListener() {
                                        @Override public void actionPerformed(ActionEvent event) {
                                            String newText = scalarDia.exec("gcd",shortProgramName);
                                            if (newText.length()>0) InPut.setText(InPut.getText()+newText);
                                            InPut.requestFocus();
                                        }
                                    }
        );

        matrixButton.addActionListener(new ActionListener() {
                                           @Override public void actionPerformed(ActionEvent event) {
                                               try {
                                                   matrix m = matDia.system(rows,columns,MyParser);
                                                   performSLE(m,OutPut);
                                               }
                                               catch (MyParserException e) {
                                                   showErrorMessage(f,e.message);
                                               }
                                           }
                                       }
        );



        InPut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (InPut.getText().length()>0) {
                    try {
                        printOut(InPut.getText(), MyParser.parse(InPut.getText(),precision));
                        InPut.setText("");
                        InPut.requestFocus();
                    }
                    catch (MyParserException Exception) {
                        showErrorMessage(f,Exception.message);
                    }
                    catch (Exception ex) {
                        showErrorMessage(f, "Syntax Error");
                    }
                }
                else {
                    InPut.setText(result);
                    InPut.requestFocus();
                }
            }
        });

        InPut.addKeyListener(new java.awt.event.KeyListener() {

            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int keyCode = e.getKeyCode();
                int end, beg;
                switch( keyCode ) {
                    case KeyEvent.VK_UP:
                        beg = OutPut.getText().lastIndexOf("input: ", inputIndex - 1) + 7;
                        if (beg < 7) {
                            beg = OutPut.getText().lastIndexOf("input: ") + 7;
                        }
                        end = OutPut.getText().indexOf("<br>", beg);
                        if (end > beg) {
                            inputIndex = beg - 7;
                            OutPut.getText().substring(beg, end);
                            InPut.setText(org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(removeEmptyBeg(OutPut.getText().substring(beg, end))));
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        beg = OutPut.getText().indexOf("input: ", inputIndex + 1) + 7;
                        if (beg < 7) {
                            beg = OutPut.getText().indexOf("input: ") + 7;
                        }
                        end = OutPut.getText().indexOf("<br>", beg);
                        if (end > beg) {
                            inputIndex = beg - 7;
                            OutPut.getText().substring(beg, end);
                            InPut.setText(org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(removeEmptyBeg(OutPut.getText().substring(beg, end))));
                        }
                        break;
                }
            }

            private String removeEmptyBeg(String str) {
                while (str.length() > 0 && (str.charAt(0) == ' ' || str.charAt(0) == '\n')) {
                    str = str.substring(1);
                }
                return str;
            }

            public void keyReleased(java.awt.event.KeyEvent e) {

            }

            public void keyTyped(java.awt.event.KeyEvent e) {

            }
        });


        ListSelectionListener Listener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    try {
                        String data = (String)functionList.getSelectedValue();
                        if (data != null) {
                            if (data.contains("matrix") && !data.contains("setEntry") && !data.contains("getEntry")) {
                                String matrixString = matDia.exec(rows,columns,MyParser);
                                if (matrixString != null) {
                                    String str = "";
                                    if (data.contains("rank")) {
                                        str = "rank(" + matrixString + ")";
                                    }
                                    else if (data.contains("minimal poly")) {
                                        str = "minipoly(" + matrixString + ")";
                                    }
                                    else if (data.contains("characteristic poly")) {
                                        str = "charpoly(" + matrixString + ")";
                                    }
                                    else if (data.contains("inverse")) {
                                        str = "invert(" + matrixString +")";
                                    }
                                    else if (data.contains("det")) {
                                        str = "det(" + matrixString + ")";
                                    }
                                    else if (data.contains("trace")) {
                                        str = "trace(" + matrixString + ")";
                                    }
                                    else if (data.contains("Gauss")) {
                                        str = "gauss(" + matrixString + ")";
                                    }
                                    else if (data.contains("declare")) {
                                        str = matrixString;
                                    }
                                    InPut.setText(InPut.getText()+str);
                                }
                            }
                            else if (data.contains("matrices")) {
                                String matrixString1 = matDia.exec(rows,columns,MyParser);
                                String matrixString2 = matDia.exec(rows,columns,MyParser);
                                String str = matrixString1 + data.charAt(0) + matrixString2;
                                InPut.setText(InPut.getText()+str);
                            }
                            else if (data.contains("system of linear equations")) {
                                matrix m = matDia.system(rows,columns,MyParser);
                                performSLE(m,OutPut);
                            }
                            else {
                                String newText = scalarDia.exec(data,shortProgramName);
                                if (newText.length()>0) InPut.setText(InPut.getText()+newText);
                            }
                        }
                    }
                    catch (MyParserException e) {
                        showErrorMessage(f,e.message);
                    }

                    InPut.requestFocus();
                    if (functionList.getSelectedIndex()>-1) functionList.clearSelection();
                }
            }
        };
        functionList.addListSelectionListener(Listener);

        InPut.requestFocus();
        f.pack();
        f.setVisible(true);
    }
}
