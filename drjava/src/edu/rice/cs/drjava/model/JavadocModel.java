/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is a part of DrJava. Current versions of this project are available
 * at http://sourceforge.net/projects/drjava
 *
 * Copyright (C) 2001-2002 JavaPLT group at Rice University (javaplt@rice.edu)
 *
 * DrJava is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * DrJava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * or see http://www.gnu.org/licenses/gpl.html
 *
 * In addition, as a special exception, the JavaPLT group at Rice University
 * (javaplt@rice.edu) gives permission to link the code of DrJava with
 * the classes in the gj.util package, even if they are provided in binary-only
 * form, and distribute linked combinations including the DrJava and the
 * gj.util package. You must obey the GNU General Public License in all
 * respects for all of the code used other than these classes in the gj.util
 * package: Dictionary, HashtableEntry, ValueEnumerator, Enumeration,
 * KeyEnumerator, Vector, Hashtable, Stack, VectorEnumerator.
 *
 * If you modify this file, you may extend this exception to your version of the
 * file, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version. (However, the
 * present version of DrJava depends on these classes, so you'd want to
 * remove the dependency first!)
 *
END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model;

import java.io.IOException;

import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import java.util.List;

public interface JavadocModel {
  /**
   * Accessor for the Javadoc error model.
   */
  public CompilerErrorModel getJavadocErrorModel();
  
  /**
   * Clears all current Javadoc errors.
   */
  public void resetJavadocErrors();
  
  /**
   * Javadocs all open documents, after ensuring that all are saved.
   * The user provides a destination, and the gm provides the package info.
   * @param select a command object for selecting a directory and warning a user
   *        about bad input
   * @param saver a command object for saving a document (if it moved/changed)
   * @param classpath a collection of classpath elements to be used by Javadoc
   * @param listener an object to be notified of start and end events, etc.
   * @throws IOException if there is a problem manipulating files
   * @throws InvalidPackageException if a document has a bad package statement
   */
  public void javadocAll(DirectorySelector select, FileSaveSelector saver,
                         List<String> classpath,
                         JavadocListener listener)
    throws IOException, InvalidPackageException;
}