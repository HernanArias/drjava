/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is part of DrJava.  Download the current version of this project from http://www.drjava.org/
 * or http://sourceforge.net/projects/drjava/
 *
 * DrJava Open Source License
 * 
 * Copyright (C) 2001-2005 JavaPLT group at Rice University (javaplt@rice.edu).  All rights reserved.
 *
 * Developed by:   Java Programming Languages Team, Rice University, http://www.cs.rice.edu/~javaplt/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 * documentation files (the "Software"), to deal with the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 *     - Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 *       following disclaimers.
 *     - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the 
 *       following disclaimers in the documentation and/or other materials provided with the distribution.
 *     - Neither the names of DrJava, the JavaPLT, Rice University, nor the names of its contributors may be used to 
 *       endorse or promote products derived from this Software without specific prior written permission.
 *     - Products derived from this software may not be called "DrJava" nor use the term "DrJava" as part of their 
 *       names without prior written permission from the JavaPLT group.  For permission, write to javaplt@rice.edu.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * WITH THE SOFTWARE.
 * 
END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model.repl;

import java.util.List;
import java.io.StringReader;
import java.io.Reader;
import java.net.URL;
import edu.rice.cs.drjava.model.repl.newjvm.ClassPathManager;
import koala.dynamicjava.interpreter.*;
import koala.dynamicjava.interpreter.context.*;
import koala.dynamicjava.interpreter.error.*;
import koala.dynamicjava.interpreter.throwable.*;
import koala.dynamicjava.parser.wrapper.*;
import koala.dynamicjava.tree.*;

import edu.rice.cs.util.classloader.StickyClassLoader;
import edu.rice.cs.util.*;

// NOTE: Do NOT import/use the config framework in this class!
//  (This class runs in a different JVM, and will not share the config object)


/** An implementation of the interpreter for the repl pane.
 *
 *  This class is loaded in the Interpreter JVM, not the Main JVM.
 *  (Do not use DrJava's config framework here.)
 *
 * @version $Id$
 */
public class DynamicJavaAdapter implements JavaInterpreter {
  private InterpreterExtension _djInterpreter;

  ClassPathManager cpm;
  
   /** Constructor */
  public DynamicJavaAdapter(ClassPathManager c) {
    cpm = c;
    _djInterpreter = new InterpreterExtension(c);
  }

  /** Interprets a string as Java source.
   *  @param s the string to interpret
   *  @return the Object generated by the running of s
   */
  public Object interpret(String s) throws ExceptionReturnedException {
    boolean print = false;
    
    /* Trims the whitespace from beginning and end of string
     * Checks the end to see if it is a semicolon
     * Adds a semicolon if necessary
     */
    s = s.trim();
    if (!s.endsWith(";")) {
      //s += ";";
      print = true;
    }

    StringReader reader = new StringReader(s);
    try {
      Object result = _djInterpreter.interpret(reader, "DrJava");
      if (print) return result;
      else return JavaInterpreter.NO_RESULT;
    }
    catch (InterpreterException ie) {
      Throwable cause = ie.getException();
      if (cause instanceof ThrownException) cause = ((ThrownException) cause).getException();
      else if (cause instanceof CatchedExceptionError) cause = ((CatchedExceptionError) cause).getException();

      throw new ExceptionReturnedException(cause);
    }
    catch (CatchedExceptionError cee) {
      throw new ExceptionReturnedException(cee.getException());
    }
    catch (InterpreterInterruptedException iie) {
      return JavaInterpreter.NO_RESULT;
    }
    catch (ExitingNotAllowedException enae) {
      return JavaInterpreter.NO_RESULT;
    }
//    catch (Throwable ie) {
//      System.err.print(new Date() + ": ");
//      System.err.println(ie);
//      ie.printStackTrace();
//      System.err.println("\n");
//      throw new RuntimeException(ie.toString());
//
////      throw new ExceptionReturnedException(ie);
//    }
  }

  public List<Node> parse(String input) { return _djInterpreter.parse(input); }

  /** Adds a path to the current classpath.
   *  @param path the path to add
   */
//  public void addClassPath(String path) {
//    //DrJava.consoleErr().println("Added class path: " + path);
//    _djInterpreter.addClassPath(path);
//  }

  public void addProjectClassPath(URL path) { cpm.addProjectCP(path); }

  public void addBuildDirectoryClassPath(URL path) { cpm.addBuildDirectoryCP(path); }

  public void addProjectFilesClassPath(URL path) { cpm.addProjectFilesCP(path); }

  public void addExternalFilesClassPath(URL path) { cpm.addExternalFilesCP(path); }
  
  public void addExtraClassPath(URL path) { cpm.addExtraCP(path); }
  
  /** Set the scope for unqualified names to the given package.
   *  @param packageName Package to assume scope of.
   */
  public void setPackageScope(String packageName) {
    StringReader reader = new StringReader("package " + packageName + ";");
    _djInterpreter.interpret(reader, "DrJava");
  }

  /** Returns the value of the variable with the given name in the interpreter.
   *  @param name Name of the variable
   *  @return Value of the variable
   */
  public Object getVariable(String name) { return _djInterpreter.getVariable(name); }

  /** Returns the class of the variable with the given name in the interpreter.
   *  @param name Name of the variable
   *  @return class of the variable
   */
  public Class<?> getVariableClass(String name) { return _djInterpreter.getVariableClass(name); }

  /** Assigns the given value to the given name in the interpreter.  If type == null, we assume that the type of
   *  this variable has not been loaded so we set it to Object.
   *  @param name Name of the variable
   *  @param value Value to assign
   *  @param type the type of the variable
   */
  public void defineVariable(String name, Object value, Class<?> type) {
    if (type == null) type = java.lang.Object.class;
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value, type);
  }

  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value Value to assign
   */
  public void defineVariable(String name, Object value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value boolean to assign
   */
  public void defineVariable(String name, boolean value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value byte to assign
   */
  public void defineVariable(String name, byte value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value char to assign
   */
  public void defineVariable(String name, char value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value double to assign
   */
  public void defineVariable(String name, double value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value float to assign
   */
  public void defineVariable(String name, float value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }


  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value int to assign
   */
  public void defineVariable(String name, int value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value long to assign
   */
  public void defineVariable(String name, long value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value short to assign
   */
  public void defineVariable(String name, short value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  /** Assigns the given value to the given name in the interpreter.
   *  @param name Name of the variable
   *  @param value Value to assign
   */
  public void defineConstant(String name, Object value) {
    _djInterpreter.defineConstant(name, value);
  }

  /** Assigns the given value to the given name as a constant in the interpreter.
   *  @param name Name of the variable
   *  @param value boolean to assign
   */
  public void defineConstant(String name, boolean value) {
    _djInterpreter.defineConstant(name, value);
  }

  /** Assigns the given value to the given name as a constant in the interpreter.
   *  @param name Name of the variable
   *  @param value byte to assign
   */
  public void defineConstant(String name, byte value) {
    _djInterpreter.defineConstant(name, value);
  }

  /** Assigns the given value to the given name as a constant in the interpreter.
   *  @param name Name of the variable
   *  @param value char to assign
   */
  public void defineConstant(String name, char value) {
    _djInterpreter.defineConstant(name, value);
  }

  /** Assigns the given value to the given name as a constant in the interpreter.
   *  @param name Name of the variable
   *  @param value double to assign
   */
  public void defineConstant(String name, double value) {
    _djInterpreter.defineConstant(name, value);
  }

  /** Assigns the given value to the given name as a constant in the interpreter.
   *  @param name Name of the variable
   *  @param value float to assign
   */
  public void defineConstant(String name, float value) {
    _djInterpreter.defineConstant(name, value);
  }

  /** Assigns the given value to the given name as a constant in the interpreter.
   *  @param name Name of the variable
   *  @param value int to assign
   */
  public void defineConstant(String name, int value) {
    _djInterpreter.defineConstant(name, value);
  }

  /** Assigns the given value to the given name as a constant in the interpreter.
   *  @param name Name of the variable
   *  @param value long to assign
   */
  public void defineConstant(String name, long value) {
    _djInterpreter.defineConstant(name, value);
  }
  /** Assigns the given value to the given name as a constant in the interpreter.
   *  @param name Name of the variable
   *  @param value short to assign
   */
  public void defineConstant(String name, short value) {
    _djInterpreter.defineConstant(name, value);
  }

  /** Sets whether protected and private variables should be accessible in the interpreter.
   *  @param accessible Whether protected and private variable are accessible
   */
  public void setPrivateAccessible(boolean accessible) {
    _djInterpreter.setAccessible(accessible);
  }

  /** Factory method to make a new NameVisitor.
   *  @param nameContext the context
   *  @return visitor the visitor
   */
  public NameVisitor makeNameVisitor(Context nameContext) { return new NameVisitor(nameContext); }

  /** Factory method to make a new TypeChecker.
   *  @param nameContext Context for the NameVisitor
   *  @param typeContext Context being used for the TypeChecker.  This is necessary because we want to perform partial
   *         type checking for the right hand side of a VariableDeclaration.
   *  @return visitor the visitor
   */
//  public AbstractTypeChecker makeTypeChecker(Context context) {
//    // TO DO: move this into its own class if more methods need to be added
//    return AbstractTypeChecker.makeTypeChecker(context);
//  }
// Removed because AbstractTypeChecker contains a makeTypeChecker method

  /** Factory method to make a new EvaluationVisitor.
   *  @param context the context
   *  @return visitor the visitor
   */
  public EvaluationVisitor makeEvaluationVisitor(Context context) {
    return new EvaluationVisitorExtension(context);
  }

  /** Processes the tree before evaluating it, if necessary.
   *  @param node Tree to process
   */
  public Node processTree(Node node) { return node; }

  public GlobalContext makeGlobalContext(TreeInterpreter i) { return new GlobalContext(i); }

  /** An extension of DynamicJava's interpreter that makes sure classes are not loaded by the system class loader
   *  (when possible) so that future interpreters will be able to reload the classes.  This extension also ensures 
   *  that classes on "extra.classpath" will be loaded if referenced by user defined classes.  (Without this, classes
   *  on "extra.classpath" can only be referred to directly, and cannot be extended, etc.) <p>
   *  We also override the evaluation visitor to allow the interpreter to be interrupted and to return NO_RESULT if 
   *  there was no result.
   */
  public class InterpreterExtension extends TreeInterpreter {

    /** Only constructor. */
    public InterpreterExtension(ClassPathManager cpm) {
      super(new JavaCCParserFactory());

      classLoader = new ClassLoaderExtension(this, cpm);
      // We have to reinitialize these variables because they automatically
      // fetch pointers to classLoader in their constructors.
      nameVisitorContext = makeGlobalContext(this);
      ClassLoaderContainer clc = new ClassLoaderContainer() {
        public ClassLoader getClassLoader() { return classLoader; }
      };
      nameVisitorContext.setAdditionalClassLoaderContainer(clc);
      checkVisitorContext = makeGlobalContext(this);
      checkVisitorContext.setAdditionalClassLoaderContainer(clc);
      evalVisitorContext = makeGlobalContext(this);
      evalVisitorContext.setAdditionalClassLoaderContainer(clc);
      //System.err.println("set loader: " + classLoader);

    }

    /** Extends the interpret method to deal with possible interrupted exceptions.
     *  Unfortunately we have to copy all of this method to override it.
     *  @param r    the reader from which the statements are read
     *  @param fname the name of the parsed stream
     *  @return the result of the evaluation of the last statement
     */
    public Object interpret(Reader r, String fname) throws InterpreterException {
      List<Node> statements;
      try {
        SourceCodeParser p = parserFactory.createParser(r, fname);
        statements = p.parseStream();
//        Utilities.showDebug("Interpreting: " + statements);
      } 
      catch (ParseError e) {
        //throw new InteractionsException("There was a syntax error in the " +
        //                                "previous input.");
        throw new InterpreterException(e);
      }
      
      Object result = JavaInterpreter.NO_RESULT;
      
      nameVisitorContext.setRevertPoint();
      checkVisitorContext.setRevertPoint();
      evalVisitorContext.setRevertPoint();
      
      try {
        for (Node n : statements) {
          n = processTree(n);
          
          NameVisitor nv = makeNameVisitor(nameVisitorContext);
          Node o = n.acceptVisitor(nv);
          if (o != null) n = o;
          
          AbstractTypeChecker tc = AbstractTypeChecker.makeTypeChecker(checkVisitorContext);
          
          n.acceptVisitor(tc);
          
          evalVisitorContext.defineVariables(checkVisitorContext.getCurrentScopeVariables());
          
          EvaluationVisitor ev = makeEvaluationVisitor(evalVisitorContext);
//          Utilities.showDebug("Ready to interpret " + ev.toString());
          result = n.acceptVisitor(ev);
//          Utilities.showDebug("Interpreted result is: " + result.toString());
        }
      }
      catch (ExecutionError e) {
        // revert the contexts just in case a binding was made in
        // one context before this error was thrown.
        nameVisitorContext.revert();
        checkVisitorContext.revert();
        evalVisitorContext.revert();
        
        //e.printStackTrace(); // For Loop....
        throw new InterpreterException(e);
      }
      
      if (result instanceof String) return  "\"" + result + "\"";
      if (result instanceof Character) return "'" + result + "'";
      return result;
    }
    
    /**
     * Assigns the given value to the given name in the interpreter.
     * @param name Name of the variable
     * @param value Value to assign
     */
    public void defineConstant(String name, Object value) {
      Class<?> c = (value == null) ? null : value.getClass();
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, value);
    }

    /** Assigns the given value to the given name as a constant in the interpreter.
     *  @param name Name of the variable
     *  @param value boolean to assign
     */
    public void defineConstant(String name, boolean value) {
      Class<?> c = boolean.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, Boolean.valueOf(value));
    }

    /** Assigns the given value to the given name as a constant in the interpreter.
     *  @param name Name of the variable
     *  @param value byte to assign
     */
    public void defineConstant(String name, byte value) {
      Class<?> c = byte.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Byte(value));
    }

    /** Assigns the given value to the given name as a constant in the interpreter.
     *  @param name Name of the variable
     *  @param value char to assign
     */
    public void defineConstant(String name, char value) {
      Class<?> c = char.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Character(value));
    }

    /** Assigns the given value to the given name as a constant in the interpreter.
     *  @param name Name of the variable
     *  @param value double to assign
     */
    public void defineConstant(String name, double value) {
      Class<?> c = double.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Double(value));
    }

    /** Assigns the given value to the given name as a constant in the interpreter.
     *  @param name Name of the variable
     *  @param value float to assign
     */
    public void defineConstant(String name, float value) {
      Class<?> c = float.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Float(value));
    }

    /** Assigns the given value to the given name as a constant in the interpreter.
     *  @param name Name of the variable
     *  @param value int to assign
     */
    public void defineConstant(String name, int value) {
      Class<?> c = int.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Integer(value));
    }

    /** Assigns the given value to the given name as a constant in the interpreter.
     *  @param name Name of the variable
     *  @param value long to assign
     */
    public void defineConstant(String name, long value) {
      Class<?> c = long.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Long(value));
    }
    
    /** Assigns the given value to the given name as a constant in the interpreter.
     *  @param name Name of the variable
     *  @param value short to assign
     */
    public void defineConstant(String name, short value) {
      Class<?> c = short.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Short(value));
    }
  }

  /** A class loader for the interpreter. */
  public static class ClassLoaderExtension extends TreeClassLoader {
    
    // the classpath is augmented by calling addURL(URL url) on this class
    // this will update the classloader variable to contain the new classpath entry
    
  private static boolean classLoaderCreated = false;
  
  private static StickyClassLoader _stickyLoader;
  
  // manages the classpath for the interpreter
  ClassPathManager cpm;
  
  /** Constructor.
   *  @param i the object used to interpret the classes
   */
  public ClassLoaderExtension(koala.dynamicjava.interpreter.Interpreter i, ClassPathManager c) {
    super(i);
    cpm = c;
    // The protected variable classLoader contains the class loader to use
    // to find classes. When a new class path is added to the loader,
    // it adds on an auxilary classloader and chains the old classLoader
    // onto the end.
    // Here we initialize classLoader to be the system class loader, and wrap it to not load edu.rice.cs classes
    classLoader = new WrapperClassLoader(getClass().getClassLoader()); // classLoader is only used in getResource()
    // NOTE that the superclass of ClassLoaderExtension (TreeClassLoader) adds (appends)
    // URLs to the classpath of this classloader
    
    // don't load the dynamic java stuff using the sticky loader!
    // without this, interpreter-defined classes don't work.
    String[] excludes = {
      "edu.rice.cs.drjava.model.repl.DynamicJavaAdapter$InterpreterExtension",
      "edu.rice.cs.drjava.model.repl.DynamicJavaAdapter$ClassLoaderExtension"
    };
    
    if (!classLoaderCreated) {
      _stickyLoader = new StickyClassLoader(this, // Sticky's newLoader, indirectly points to the (dynamic) classLoader
                                            classLoader, // Sticky's oldLoader
                                            excludes);
      classLoaderCreated = true;
    }
    
    // we will use this to getResource classes
  }
  
  /** Delegates all resource requests to {@link #classLoader} (the system class loader by default).
   *  This method is called by the {@link StickyClassLoader}.
   */
  public URL getResource(String name) {
    // use the cpm to get the resource for the specified name
    return cpm.getClassLoader().getResource(name);
    //return classLoader.getResource(name);
  }
  
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException{
    Class<?> clazz;
    
    // check the cache
    if (classes.containsKey(name)) clazz = (Class<?>) classes.get(name);
    else {
      try {
        clazz = _stickyLoader.loadClass(name);
      }
      catch (ClassNotFoundException e) {
        // If it exceptions, just fall through to here to try the interpreter.
        // If all else fails, try loading the class through the interpreter.
        // That's used for classes defined in the interpreter.
        clazz = interpreter.loadClass(name);
      }
    }
    
    if (resolve) resolveClass(clazz);
    
    return clazz;
  }
  
  
//  /** Adds a URL to the class path.  DynamicJava's version of this creates a
//   *  new URLClassLoader with the given URL, using the old loader as a parent.
//   *  This seems to cause problems for us in certain cases, such as accessing
//   *  static fields or methods in a class that extends a superclass which is
//   *  loaded by "child" classloader...
//   *
//   *  Instead, we'll replace the old URLClassLoader with a new one containing
//   *  all the known URLs.
//   *
//   * (I don't know if this really works yet, so I'm not including it in
//   * the current release.  CSR, 3-13-2003)
//   */
//  public void addURL(URL url) {
//    if (classLoader == null) {
//      classLoader = new URLClassLoader(new URL[] { url });
//    }
//    else if (classLoader instanceof URLClassLoader) {
//      URL[] oldURLs = ((URLClassLoader)classLoader).getURLs();
//      URL[] newURLs = new URL[oldURLs.length + 1];
//      System.arraycopy(oldURLs, 0, newURLs, 0, oldURLs.length);
//      newURLs[oldURLs.length] = url;
//      
//      // Create a new class loader with all the URLs
//      classLoader = new URLClassLoader(newURLs);
//    }
//    else {
//      classLoader = new URLClassLoader(new URL[] { url }, classLoader);
//    }
//  }
  
//   public Class defineClass(String name, byte[] code)  {
//   File file = new File("debug-" + name + ".class");
//   
//   try {
//   FileOutputStream out = new FileOutputStream(file);
//   out.write(code);
//   out.close();
//   DrJava.consoleErr().println("debug class " + name + " to " + file.getAbsolutePath());
//   }
//   catch (Throwable t) { }
//   
//   Class c = super.defineClass(name, code);
//   return c;
//   }

  }
}
