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
import java.util.StringTokenizer;
import jing.chemParser.ChemParser;


//----------------------------------------------------------------------------
//jing\chem\ChemElement.java
//----------------------------------------------------------------------------

/**
OVERVIEW: Properties which all atoms of the same type share.
*/
//## class ChemElement
public class ChemElement {

  protected static ChemElementDictionary chemElementDictionary = ChemElementDictionary.getInstance();		//## attribute chemElementDictionary

  /**
  Name of the atom
  */
  protected String symbol;		//## attribute name

  /**
  Valency of the atom
  */
  protected int valency;		//## attribute valency

  /**
  Atomic weight of the atom
  */
  protected double weight;		//## attribute weight

  protected int maxNeighbors;
  protected int maxHNeighbors;
  protected String elementName;


  // Constructors
  private  ChemElement() {
  }

  /**
  Requires:
  Effects: private constructor called by create() to create new object
  Modifies:
  */
  public ChemElement(String p_symbol, int p_neighbors, int p_Hneighbors, int p_valency, double p_weight, String p_name) {
        symbol = p_symbol;
        maxNeighbors = p_neighbors;
        maxHNeighbors = p_Hneighbors;
        valency = p_valency;
        weight = p_weight;
        elementName = p_name;
  }

  public String getElementName() {
      return elementName;
  }

  public int getValency() {
      return valency;
  }

  public double getWeight() {
      return weight;
  }

  public int getMaxNeighbors() {
      return maxNeighbors;
  }

  public int getMaxHNeighbors() {
      return maxHNeighbors;
  }

  public String getSymbol() {
      return symbol;
  }

  public static ChemElement make(String p_name) throws UnknownSymbolException {
      ChemElement ce = chemElementDictionary.getChemElement(p_name);
      if (ce == null) {
          throw new UnknownSymbolException("Chemical Element: " + p_name);
      }
      return ce;
  }

  protected static ChemElementDictionary getChemElementDictionary() {
      return chemElementDictionary;
  }

    public static void readListOfElements() throws IOException {
      try {
          String elementsFile = System.getProperty("jing.chem.ChemElement.elementsFile");
          if (elementsFile == null) {
              System.out.println("Undefined system property: jing.chem.ChemElement.elementsFile!");
              System.out.println("No elements file defined!");
              throw new IOException("Undefined system property: jing.chem.ChemElement.elementsFile");
          }
          FileReader in = new FileReader(elementsFile);
          BufferedReader data = new BufferedReader(in);
          String line = ChemParser.readMeaningfulLine(data, true);
          read: while (line != null) {
              StringTokenizer st = new StringTokenizer(line);
              chemElementDictionary.putChemElement(
                      new ChemElement(st.nextToken(),
                      Integer.parseInt(st.nextToken()),
                      Integer.parseInt(st.nextToken()),
                      Integer.parseInt(st.nextToken()),
                      Double.parseDouble(st.nextToken()),
                      st.nextToken()));
              line = ChemParser.readMeaningfulLine(data, true);
          }
          in.close();
          return;
      }
      catch (Exception e) {
          throw new IOException(e.getMessage());
      }
  }

}
/*********************************************************************
	File Path	: RMG\RMG\jing\chem\ChemElement.java
*********************************************************************/

