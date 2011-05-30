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



package jing.chemUtil;


import jing.chem.*;
import java.util.*;
import jing.mathTool.*;
import jing.chem.FGAtom;
import jing.chemParser.*;
import jing.chem.FGElement;
import jing.chem.FreeElectron;
import jing.rxnSys.Logger;

//## package jing::chemUtil

//----------------------------------------------------------------------------
//jing\chemUtil\Node.java
//----------------------------------------------------------------------------

/**
Node is the key graph component.  A node stores user-defined content.  Each node can have non-negative number of neighbors of arcs.  Each node has a special ID (integer) indicating the position number of a node in a graph.
*/
//## class Node
public class Node extends GraphComponent {

  /**
  The position identification of this node.
  */
  protected Integer ID;		//## attribute ID

  //protected Integer centralID = new Integer(-1);		//## attribute centralID

  protected FreeElectron feElement = null;		//## attribute feElement

  protected Object fgElement = null;		//## attribute fgElement


  // Constructors

  /**
  Requires:
  Effects: constructor:
  (1) store the p_element in this Node
  (2) set the ID of this node as p_position if p_position >=0. otherwise, throw invalidPositionIDException.
  Modifies:
  */
  //## operation Node(int,Object)
  public  Node(int p_position, Object p_element) throws InvalidNodeIDException {
      //#[ operation Node(int,Object)
      super(p_element);

      if (p_position < 0) throw new InvalidNodeIDException();
      else ID = new Integer(p_position);




      //#]
  }
  //## operation Node(Integer,Object)
  public  Node(Integer p_position, Object p_element) throws InvalidNodeIDException {
      //#[ operation Node(Integer,Object)
      super(p_element);

      if (p_position.intValue() < 0) throw new InvalidNodeIDException();
      else ID = p_position;



      //#]
  }
  public  Node() {
  }

  /**
  Requires:
  Effects: if pass-in GraphComponent is not an instance of Arc, throw InvalidNeighborException; otherwise, super.addNeighbor()
  Modifies: this.neighbor
  */
  //## operation addNeighbor(GraphComponent)
  public void addNeighbor(GraphComponent p_graphComponent) throws InvalidNeighborException {
      //#[ operation addNeighbor(GraphComponent)
      if (!(p_graphComponent instanceof Arc)) {
        throw new InvalidNeighborException("node");
      }
      else {
        super.addNeighbor(p_graphComponent);
        return;
      }
      //#]
  }

  /**
  Requires:
  Effects: super.addNeighbor()
  Modifies: this.neighbor
  */
  //## operation addNeighbor(Arc)
  public void addNeighbor(Arc p_arc) throws InvalidNeighborException {
      //#[ operation addNeighbor(Arc)
      super.addNeighbor(p_arc);



      //#]
  }

  //## operation checkFgElement(FGAtom)
  public boolean checkFgElement(FGAtom p_fgAtom) {
      //#[ operation checkFgElement(FGAtom)
      // add fgAtom checking here!
      return true;


      //#]
  }

  public FreeElectron[] getVectorOfFreeElectrons(Object o) {
      Collection oc = (Collection)o;
      Iterator iter = oc.iterator();
      int numFE = oc.size();
      FreeElectron[] fe = new FreeElectron[numFE];
      for (int counter=0; counter<numFE; ++counter) {
          ChemNodeElement cne = (ChemNodeElement)iter.next();
          fe[counter] = cne.getFreeElectron();
      }
      return fe;
  }

  //## operation contentSub(GraphComponent)
  public boolean contentSub(GraphComponent p_graphComponent) throws InvalidChemNodeElementException {
      // "this" is the child
      // p_graphComponent is the parent

      if (this == p_graphComponent) return false;
      if (!(p_graphComponent instanceof Node)) return false;

      Node node = (Node)p_graphComponent;

      // compare radical number
      FreeElectron[] fe1 = null;
      if (getElement() instanceof Collection) {
          fe1 = new FreeElectron[((Collection)getElement()).size()];
          fe1 = getVectorOfFreeElectrons(getElement());
      }
      else {
          fe1 = new FreeElectron[1];
          fe1[0] = getFeElement();
      }

      FreeElectron[] fe2 = null;
      if (node.getElement() instanceof Collection) {
          fe2 = new FreeElectron[((Collection)node.getElement()).size()];
          fe2 = node.getVectorOfFreeElectrons(node.getElement());
      }
      else {
          fe2 = new FreeElectron[1];
          fe2[0] = node.getFeElement();
      }

      for (int i=0; i<fe1.length; i++) {
          boolean foundInParent = false;
          for (int j=0; j<fe2.length; j++) {
              if (fe1[i].getOrder() == fe2[j].getOrder()) {
                  foundInParent = true;
                  if (fe1[i].getOrder() == 2){// && fe1.length == 1 && fe2.length == 1) {
                      String spin1 = fe1[i].getSpin();
                      String spin2 = fe2[j].getSpin();
                      // If the "child" has no spin specified
                      if (spin1 == null) {
                          // If the parent has spin specified, then the "child" is not a child
                          if (spin2 != null) foundInParent = false;
                      }
                      // Else, the "child" has spin specified
                      else {
                          // If the parent also has spin specified, check to see if they are the same
                          if (spin2 != null) {
                              if (!spin1.equals(spin2)) foundInParent = false;
                          }
                      }
                  }
                  if (foundInParent) break;
              }
          }
          if (!foundInParent) return false;
      }

      /*
       * The question: Is node a child of this.node?
       */

      // Initialize the node objects
      Object fge_parent = node.getFgElement();
      Object fge_child = getFgElement();
      Atom atom_parent = null;
      FGAtom fgatom_parent = null;
      Atom atom_child = null;
      FGAtom fgatom_child = null;

      // Determine if the object is an Atom ("C", "H", "O", etc.)
      //    or FGAtom ("R_minXvalency", "R!H", "R_nondelocalized", etc.)
      if (fge_parent == null) {
          Object object_parent = node.element;
          if (object_parent instanceof Atom) atom_parent = (Atom)object_parent;
          else if (object_parent instanceof FGAtom) fgatom_parent = (FGAtom)object_parent;
          else if (object_parent instanceof Collection) {
              // If we have reached this point, we have already concluded the free electrons & spin are the same
              //    All we need to check at this point is the Atom(FGAtom) child/parent dependency
              //    Thus, MRH only grabs the first instance in the Collection to proceed
              Iterator iter = ((Collection)object_parent).iterator();
              Object firstInstance = (Object)iter.next();
              if (firstInstance instanceof Atom) atom_parent = (Atom)firstInstance;
              else if (firstInstance instanceof FGAtom) fgatom_parent = (FGAtom)firstInstance;
          }
      }
      if (fge_child == null) {
          Object object_child = this.getElement();
          if (object_child instanceof Atom) atom_child = (Atom)object_child;
          else if (object_child instanceof FGAtom) fgatom_child = (FGAtom)object_child;
          else if (object_child instanceof Collection) {
              // If we have reached this point, we have already concluded the free electrons & spin are the same
              //    All we need to check at this point is the Atom(FGAtom) child/parent dependency
              //    Thus, MRH only grabs the first instance in the Collection to proceed
              Iterator iter = ((Collection)object_child).iterator();
              Object firstInstance = (Object)iter.next();
              if (firstInstance instanceof Atom) atom_child = (Atom)firstInstance;
              else if (firstInstance instanceof FGAtom) fgatom_child = (FGAtom)firstInstance;
          }
      }

      // If either child or parent is the hard-coded "R_nondelocalized",
      //    check the neighbors for non "S" bonds
//      FGElement nondelocalized = FGElement.make("R_nondelocalized");
//      if (fgatom_child != null && fgatom_child.getFgElement().getName().equals("R_nondelocalized")) {
//          System.out.println("");
//      }
//      if (fgatom_parent != null && fgatom_parent.getFgElement().getName().equals("R_nondelocalized")) {
//          System.out.println("");
//      }

      // Else, check if the atom names are the same
      if (atom_child != null && atom_parent != null) {
          return atom_child.getChemElement().getSymbol().equals(atom_parent.getChemElement().getSymbol());
      }
      else if (atom_child != null && fgatom_parent != null) {
          if (fgatom_parent.getName().equals("R_nondelocalized")) {
              LinkedHashSet allNeighborsToCurrentChildNode = this.neighbor;
              // If all bonds to neighbors are "S", ...
              //    AND the neighbor is not Hydrogen, then return true (is a child)
              for (Iterator iter = allNeighborsToCurrentChildNode.iterator(); iter.hasNext();) {
                  Object currentBond = ((Arc)iter.next()).element;
                  if (currentBond instanceof Bond) {
                    if (!((Bond)currentBond).isSingle()) return false;
                  } else { // Object is a HashSet
                      HashSet currentBonds = (HashSet)currentBond;
                      boolean foundASingleBond = false;
                      for (Iterator iter2 = currentBonds.iterator(); iter2.hasNext();) {
                          if (((Bond)iter2.next()).isSingle()) {
                              foundASingleBond = true;
                              break;
                          }
                      }
                      if (!foundASingleBond) return false;
                  }
              }
              // Need to check if current child node is Hydrogen
              if (atom_child.getChemElement().getSymbol().equals("H")) return false;
              return true;
          }
          else {
              LinkedList atomsAssociatedWithParent = fgatom_parent.getFgElement().getAtomList();
              boolean foundAMatch = false;
              ChemElement ce_child = atom_child.getChemElement();
              if (atomsAssociatedWithParent == null)
                  System.out.println("stop");
              for (int j=0; j<atomsAssociatedWithParent.size(); j++) {
                  foundAMatch = (((ChemElement)atomsAssociatedWithParent.get(j)).getSymbol().equals(ce_child.getSymbol()));
                  if (foundAMatch) break;
              }
              return foundAMatch;
          }
      }
      else if (fgatom_child != null && atom_parent != null) {
          if (fgatom_child.getName().equals("R_nondelocalized")) {
              // A "R_nondelocalized" group is a general FGAtom
              //    Therefore, it cannot be a child of an Atom
              return false;
          }
          else {
              LinkedList atomsAssociatedWithChild = fgatom_child.getFgElement().getAtomList();
              for (int i=0; i<atomsAssociatedWithChild.size(); i++) {
                  boolean foundAMatch = false;
                  ChemElement ce_child = (ChemElement)atomsAssociatedWithChild.get(i);
                  foundAMatch = atom_parent.getChemElement().getSymbol().equals(ce_child.getSymbol());
                  if (foundAMatch) break;
                  if (!foundAMatch) return false;
              }
              return true;
          }
      }
      else if (fgatom_child != null && fgatom_parent != null) {
          // First check the names of the FGAtoms
          if (fgatom_child.getName().equals(fgatom_parent.getName())) return true;
          // Next, check if the parent is "R_nondelocalized"
          if (fgatom_parent.getName().equals("R_nondelocalized")) {
              LinkedHashSet allNeighborsToCurrentChildNode = this.neighbor;
              // If all bonds to neighbors are "S", ...
              //    AND the neighbor is not Hydrogen, then return true (is a child)
              for (Iterator iter = allNeighborsToCurrentChildNode.iterator(); iter.hasNext();) {
                  Object currentBond = ((Arc)iter.next()).element;
                  if (currentBond instanceof Bond) {
                    if (!((Bond)currentBond).isSingle()) return false;
                  } else { // Object is a HashSet
                      HashSet currentBonds = (HashSet)currentBond;
                      boolean foundASingleBond = false;
                      for (Iterator iter2 = currentBonds.iterator(); iter2.hasNext();) {
                          if (((Bond)iter2.next()).isSingle()) {
                              foundASingleBond = true;
                              break;
                          }
                      }
                      if (!foundASingleBond) return false;
                  }
              }
              LinkedList atomsAssociatedWithChild = fgatom_child.getFgElement().getAtomList();
              for (int i=0; i<atomsAssociatedWithChild.size(); i++) {
                  ChemElement ce_child = (ChemElement)atomsAssociatedWithChild.get(i);
                  if ("H".equals(ce_child.getSymbol())) return false;
              }
              return true;
          }
          // Next, check if the child is "R_nondelocalized"
          if (fgatom_child.getName().equals("R_nondelocalized")) {
              LinkedHashSet allNeighborsToCurrentParentNode = node.neighbor;
              // If all bonds to neighbors are "S", ...
              //    AND the neighbor is not Hydrogen, then return true (is a child)
              for (Iterator iter = allNeighborsToCurrentParentNode.iterator(); iter.hasNext();) {
                  Object currentBond = ((Arc)iter.next()).element;
                  if (currentBond instanceof Bond) {
                    if (!((Bond)currentBond).isSingle()) return false;
                  } else { // Object is a HashSet
                      HashSet currentBonds = (HashSet)currentBond;
                      boolean foundASingleBond = false;
                      for (Iterator iter2 = currentBonds.iterator(); iter2.hasNext();) {
                          if (((Bond)iter2.next()).isSingle()) {
                              foundASingleBond = true;
                              break;
                          }
                      }
                      if (!foundASingleBond) return false;
                  }
              }
              // Need to find at least one non-Hydrogen for "R_nondelocalized"
              //    to possibly be a child of parent
              LinkedList atomsAssociatedWithParent = fgatom_parent.getFgElement().getAtomList();
              boolean foundAnyNonHydrogen = false;
              for (int i=0; i<atomsAssociatedWithParent.size(); i++) {
                  ChemElement ce_parent = (ChemElement)atomsAssociatedWithParent.get(i);
                  if (!"H".equals(ce_parent.getSymbol())){
                      foundAnyNonHydrogen = true;
                      break;
                  }
              }
              return foundAnyNonHydrogen;
          }
          // Else, each element in the list of the child must be present in
          //    the list of the parent
          else {
              LinkedList atomsAssociatedWithChild = fgatom_child.getFgElement().getAtomList();
              LinkedList atomsAssociatedWithParent = fgatom_parent.getFgElement().getAtomList();
              for (int i=0; i<atomsAssociatedWithChild.size(); i++) {
                  boolean foundAMatch = false;
                  ChemElement ce_child = (ChemElement)atomsAssociatedWithChild.get(i);
                  for (int j=0; j<atomsAssociatedWithParent.size(); j++) {
                      foundAMatch = (((ChemElement)atomsAssociatedWithParent.get(j)).getSymbol().equals(ce_child.getSymbol()));
                      if (foundAMatch) break;
                  }
                  if (!foundAMatch) return false;
              }
              return true;
          }
      }

      // MRH believes we should never reach this step now ...
      //    unless something went horribly wrong
      return MathTool.isSub(fge_child,fge_parent);

  }

  //## operation getFeElement()
  public FreeElectron getFeElement() {
      //#[ operation getFeElement()
      identifyFeElement();
      return feElement;
      //#]
  }

  //## operation getFgElement()
  public Object getFgElement() {
      //#[ operation getFgElement()
      return fgElement;
      //#]
  }

  //## operation getID()
  public Integer getID() {
      //#[ operation getID()
      return ID;
      //#]
  }

  //## operation getOtherArcs(Arc)
  public HashSet getOtherArcs(Arc p_arc) {
      //#[ operation getOtherArcs(Arc)
      HashSet otherArcs = new HashSet();

      Iterator iter = getNeighbor();
      while (iter.hasNext()) {
      	Arc arc = (Arc)iter.next();
      	if (!arc.equals(p_arc)) otherArcs.add(arc);
      }

      return otherArcs;
      //#]
  }
  
  public HashSet getNeighboringNodes(){
	  HashSet neighboringNodes = new HashSet();
	  Iterator iter = getNeighbor();
	  while (iter.hasNext()){
		  Arc arc = (Arc)iter.next();
		  neighboringNodes.add(getOtherNode(arc));
	  }
	  return neighboringNodes;
  }

  /**
  Requires:
  Effects: return the other node connected by p_arc.   if p_arc is not connecting this node with any other node, return null.
  Modifies:
  */
  //## operation getOtherNode(Arc)
  public Node getOtherNode(Arc p_arc) {
      //#[ operation getOtherNode(Arc)
      return p_arc.getOtherNode(this);



      //#]
  }

  //## operation identifyFeElement()
  public void identifyFeElement() {
      //#[ operation identifyFeElement()
      if (feElement == null) {
      	updateFeElement();
      }


      //#]
  }

  //## operation includeFgElement(FGElement)
  public void includeFgElement(FGElement p_fgElement) {
      //#[ operation includeFgElement(FGElement)
      //#]
  }
  //## operation setID(int)
  public void setID(int p_ID) {
      //#[ operation setID(int)
      ID = new Integer(p_ID);
      //#]
  }

  /**
  Requires:
  Effects: super.isConnected()
  Modifies:
  */
  //## operation isConnected(Arc)
  public boolean isConnected(Arc p_arc) {
      //#[ operation isConnected(Arc)
      return super.isConnected(p_arc);
      //#]
  }

  /**
  Requires:
  Effects: if pass-in GraphComponent is not an instance of Arc, return false; otherwise, return super.isConnected().
  Modifies:
  */
  //## operation isConnected(GraphComponent)
  public boolean isConnected(GraphComponent p_GraphComponent) {
      //#[ operation isConnected(GraphComponent)
      // node can only connect to arc
      if (!(p_GraphComponent instanceof Arc)) return false;
      else return super.isConnected(p_GraphComponent);



      //#]
  }

  //## operation isConnectedBy(Node)
  public Arc isConnectedBy(Node p_node) {
      //#[ operation isConnectedBy(Node)
      if (this == p_node) return null;

      Iterator iter = getNeighbor();
      while (iter.hasNext()) {
      	Arc arc = (Arc)iter.next();
      	if (p_node.neighbor.contains(arc)) return arc;
      }
      return null;
      //#]
  }

  /**
  Requires:
  Effects: if this node only has one or zero neighbor, return true;  otherwise, return false.
  Modifies:
  */
  //## operation isLeaf()
  public boolean isLeaf() {
      //#[ operation isLeaf()
      return (neighbor.size() <= 1);



      //#]
  }

  /**
  Requires:
  Effects: if all the neighbors of this node are instances of Arc, return true; otherwise, return false.
  Modifies:
  */
  //## operation neighborOk()
  public boolean neighborOk() {
      //#[ operation neighborOk()
      // check if all the neighbors are node
      Iterator iter = getNeighbor();
      while (iter.hasNext()) {
      	if (!(iter.next() instanceof Arc)) return false;
      }

      return true;
      //#]
  }

  //## operation printFgElement()
  public String printFgElement() {
      //#[ operation printFgElement()
      if (fgElement == null) return "";

      if (fgElement instanceof Collection) {
      	String s = "";
      	for (Iterator iter = ((Collection)fgElement).iterator(); iter.hasNext();) {
      		FGElement fge = (FGElement)iter.next();
      		s += fge.getName() + ",";
      	}
      	s = s.substring(0,s.length()-1);
      	return s;
      }
      else {
      	String s = ((FGElement)fgElement).getName();
      	return s;
      }


      //#]
  }

  //## operation printLable()
  public String printLable() {
      //#[ operation printLable()
      String s = "N" + String.valueOf(getID());

      return s;

      //#]
  }

  /**
  Requires:
  Effects:check rep in the following aspects
  (1) neighborOk()?
  Modifies:
  */
  //## operation repOk()
  public boolean repOk() {
      //#[ operation repOk()
      return (neighborOk());
      //#]
  }

  

 
  //## operation toString()
  public String toString() {
      //#[ operation toString()
      String s = "";
      s = s + getID() + " ";
      if (isCentralNode()) s = s + "*" + getCentralID() + " ";
      Object o = getElement();
      s = s + " " + ChemParser.writeChemNodeElement(o);
      FreeElectron fee = getFeElement();
      String fe = null;
      if (fee == null) fe = "0";
      else fe = fee.getName();

      s = s + " " + fe;
      Iterator iter = getNeighbor();
      while (iter.hasNext()) {
      	s = s + " {";
      	Arc arc = (Arc)iter.next();
      	Node node = getOtherNode(arc);
      	s = s + node.getID() + ",";
      	s = s + arc.toString() + "}";
      }
      return s;


      //#]
  }

  //## operation toStringWithoutCentralID()
  public String toStringWithoutCentralID() {
      //#[ operation toStringWithoutCentralID()
      String s = "";
      s = s + getID() + " ";

      Object o = getElement();
      s = s + " " + ChemParser.writeChemNodeElement(o);
      FreeElectron fee = getFeElement();
      String fe = null;
      if (fee == null) fe = "0";
      else fe = fee.getName();

      s = s + " " + fe;
      Iterator iter = getNeighbor();
      while (iter.hasNext()) {
      	s = s + " {";
      	Arc arc = (Arc)iter.next();
      	Node node = getOtherNode(arc);
      	s = s + node.getID() + ",";
      	s = s + arc.toString() + "}";
      }
      return s;


      //#]
  }

  //## operation toStringWithoutCentralIDAndH()
  public String toStringWithoutCentralIDAndH() {
      //#[ operation toStringWithoutCentralIDAndH()
      String s = "";
      s = s + getID() + " ";

      Atom atom = (Atom)getElement();
      s = s + " " + ChemParser.writeChemNodeElement(atom);
      FreeElectron fee = getFeElement();
      String fe = null;
      if (fee == null) fe = "0";
      else fe = fee.getName();

      s = s + " " + fe;
      Iterator iter = getNeighbor();
      while (iter.hasNext()) {
      	Arc arc = (Arc)iter.next();
      	Node node = getOtherNode(arc);
      	Atom neighborAtom = (Atom)node.getElement();
      	if (!neighborAtom.isHydrogen()) {
      		s = s + " {";
      		s = s + node.getID() + ",";
      		s = s + arc.toString() + "}";
      	}
      }
      return s;


      //#]
  }

  //## operation updateFeElement()
  public void updateFeElement() throws InvalidFreeElectronException {
      //#[ operation updateFeElement()
      Object o = getElement();
      FreeElectron fe = null;
      if (o instanceof ChemNodeElement) {
      	fe = ((ChemNodeElement)o).getFreeElectron();
      }
      else if (o instanceof Collection) {
      	Collection oc = (Collection)o;
      	Iterator iter = oc.iterator();
      	FreeElectron old = null;
      	while (iter.hasNext()) {
      		ChemNodeElement cne = (ChemNodeElement)iter.next();
      		fe = cne.getFreeElectron();
      		if (old == null) old = fe;
      		else {
      			if (old != fe) throw new InvalidFreeElectronException();
      		}
      	}
      }
      if (fe == null) throw new InvalidFreeElectronException();
      feElement = fe;


      //#]
  }

  protected void setID(Integer p_ID) {
      ID = p_ID;
  }

  

  public void setFeElement(FreeElectron p_feElement) {
      feElement = p_feElement;
  }

  public void setFgElement(Object p_fgElement) {
      fgElement = p_fgElement;
  }

}
/*********************************************************************
	File Path	: RMG\RMG\jing\chemUtil\Node.java
*********************************************************************/

