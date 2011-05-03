////////////////////////////////////////////////////////////////////////////////
//
//	RMG - Reaction Mechanism Generator
//
//	Copyright (c) 2002-2011 Prof. William H. Green (whgreen@mit.edu) and the
//	RMG Team (rmg_dev@mit.edu)
//
//	Permission is hereby granted, free of charge, to any person obtaining a
//	copy of this software and associated documentation files (the "Software"),
//	to deal in the Software without restriction, including without limitation
//	the rights to use, copy, modify, merge, publish, distribute, sublicense,
//	and/or sell copies of the Software, and to permit persons to whom the
//	Software is furnished to do so, subject to the following conditions:
//
//	The above copyright notice and this permission notice shall be included in
//	all copies or substantial portions of the Software.
//
//	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
//	DEALINGS IN THE SOFTWARE.
//
////////////////////////////////////////////////////////////////////////////////



package jing.chem;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import jing.chemParser.ChemParser;

//## package jing::chem

//----------------------------------------------------------------------------
//jing\chem\FGElement.java
//----------------------------------------------------------------------------

//## class FGElement
public class FGElement {

  protected static FGElementDictionary fGElementDictionary = FGElementDictionary.getInstance();		//## attribute fGElementDictionary

  protected String name;		//## attribute name

  protected String type;		//## attribute type

  protected LinkedList atoms;


  // Constructors

  //## operation FGElement()
  protected  FGElement() {
      //#[ operation FGElement()
      //#]
  }
  //## operation FGElement(String)
  protected  FGElement(String p_name) throws UnknownSymbolException {
      //#[ operation FGElement(String)
      name = p_name;

      if (p_name.compareToIgnoreCase("R_nondelocalized") == 0) {
          type = "R_nondelocalized";
      }
      else {
      	throw new UnknownSymbolException("FGElement type: " + p_name);
      }




      //#]
  }

  protected FGElement(String p_name, LinkedList p_atoms) {
      name = p_name;
      atoms = p_atoms;
  }

  //## operation create(String)
  public static FGElement create(String p_name) {
      //#[ operation create(String)
      FGElement fge = null;
      if (p_name.equals("R_nondelocalized")) {
          fge = new FGElement("R_nondelocalized");
      }
      else throw new UnknownSymbolException("FGElement: " + p_name);

      return fge;
      //#]
  }

  //## operation make(String)
  public static FGElement make(String p_name) {
      //#[ operation make(String)
      try {
      	//String internalName = translateName(p_name);
      	//FGElement fge = fGElementDictionary.getFGElement(internalName);
		FGElement fge = fGElementDictionary.getFGElement(p_name);
      	
		if (fge == null) {
      		//fge = FGElement.create(internalName);
			fge = FGElement.create(p_name);
			fGElementDictionary.putFGElement(fge);
      	}
      	return fge;
      }
      catch (UnknownSymbolException e) {
      	throw new UnknownSymbolException("FG Element: " + p_name);
      }


      //#]
  }

  //## operation translateName(String)
  public static String translateName(String p_name) {
      //#[ operation translateName(String)
      if (p_name == null) throw new NullSymbolException("FGElement");

      if ((p_name.compareToIgnoreCase("R_nondelocalized")==0)) {
          return "R_nondelocalized";
      }
      else {
      	throw new UnknownSymbolException("FGElement");
      }
      //#]
  }

  public static FGElementDictionary getFGElementDictionary() {
      return fGElementDictionary;
  }

  public static void setFGElementDictionary(FGElementDictionary p_fGElementDictionary) {
      fGElementDictionary = p_fGElementDictionary;
  }

  public String getName() {
      return name;
  }

  public void setName(String p_name) {
      name = p_name;
  }

  public String getType() {
      return type;
  }

  public void setType(String p_type) {
      type = p_type;
  }

  public static void readFunctionalGroupElements() throws IOException {
      try {
          String fgElementsFile = System.getProperty("jing.chem.FGElement.fgElementsFile");
          if (fgElementsFile == null) {
              System.out.println("Undefined system property: jing.chem.FGElement.fgElementsFile!");
              System.out.println("No functional group elements file defined!");
              throw new IOException("Undefined system property: jing.chem.FGElement.fgElementsFile");
          }
          FileReader in = new FileReader(fgElementsFile);
          BufferedReader data = new BufferedReader(in);
          // step 1: read in structure
          String line = ChemParser.readMeaningfulLine(data, true);
          read: while (line != null) {
              StringTokenizer token = new StringTokenizer(line);
              String fgeName = token.nextToken();
              String listOfElements = ChemParser.removeBrace(token.nextToken());
              token = new StringTokenizer(listOfElements,",");
              LinkedList listOfAtoms = new LinkedList();
              while (token.hasMoreTokens()) {
                  listOfAtoms.add(ChemElement.make(token.nextToken()));
              }
              fGElementDictionary.putFGElement(new FGElement(fgeName,listOfAtoms));
              line = ChemParser.readMeaningfulLine(data, true);
          }
          in.close();
          return;
      }
      catch (Exception e) {
          throw new IOException(e.getMessage());
      }
  }

  public static void initializeHardCodedFunctionalGroupElements() {
      fGElementDictionary.putFGElement(new FGElement("R_nondelocalized"));
  }

}
/*********************************************************************
	File Path	: RMG\RMG\jing\chem\FGElement.java
*********************************************************************/

