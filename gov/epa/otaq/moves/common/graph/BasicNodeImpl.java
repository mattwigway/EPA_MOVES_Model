/**************************************************************************************************
 * @(#)BasicNodeImpl.java
 *
 *************************************************************************************************/
package gov.epa.otaq.moves.common.graph;

import java.util.*;

/**
 * Provides a simple implementation of INode.
 * 
 * @author		Wesley Faler
 * @version		2010-01-16
**/
public class BasicNodeImpl implements INode {
	/** Resources needed by the node **/
	private ArrayList<IResource> inputs = null;
	/** Resources generated by the node **/
	private ArrayList<IResource> outputs = null;
	/** List of nodes that generate resources needed by this node **/
	private ArrayList<INode> predecessors = null;
	/** List of nodes that need the resources generated by this node **/
	private ArrayList<INode> successors = null;
	/** The node considered the linear predecessor of this node, may be null **/ 
	private INode linearPredecessor = null;
	/** Text associated with the node **/
	private String nodeText = "";

	/** Default constructor **/
	public BasicNodeImpl() {
		// Nothing to do here
	}

	/**
	 * Constructor
	 * @param linearPredecessorToUse The node considered the linear predecessor of this node, may be null
	**/
	public BasicNodeImpl(INode linearPredecessorToUse) {
		linearPredecessor = linearPredecessorToUse;
	}

	/**
	 * Constructor
	 * @param linearPredecessorToUse The node considered the linear predecessor of this node, may be null
	 * @param nodeTextToUse text associated with the node
	**/
	public BasicNodeImpl(INode linearPredecessorToUse, String nodeTextToUse) {
		linearPredecessor = linearPredecessorToUse;
		nodeText = nodeTextToUse;
	}

	/**
	 * Obtain the resources that are required by the node.
	 * @return an Iterator to the set of IResource objects that are required
	 * by the node.  Will be null if there are no inputs.
	**/
	public Iterator<IResource> getInputs() {
		if(inputs != null) {
			return inputs.iterator();
		}
		return null;
	}

	/**
	 * Add a resource to the set required by this node.
	 * @param r resource to add
	**/
	public void addInput(IResource r) {
		if(inputs == null) {
			inputs = new ArrayList<IResource>();
		}
		inputs.add(r);
	}

	/**
	 * Obtain the resources that are written to by the node.
	 * @return an Iterator to the set of IResource objects that are updated
	 * by the node.  Will be null if there are no outputs.
	**/
	public Iterator<IResource> getOutputs() {
		if(outputs != null) {
			return outputs.iterator();
		}
		return null;
	}

	/**
	 * Add a resource to the set generated by this node.
	 * @param r resource to add
	**/
	public void addOutput(IResource r) {
		if(outputs == null) {
			outputs = new ArrayList<IResource>();
		}
		outputs.add(r);
	}

	/**
	 * Obtain the set of nodes that should be executed immediately prior
	 * to this node.
	 * @return a list of INode objects that generate a resource needed by
	 * the node.
	**/
	public ArrayList<INode> getPredecessors() {
		if(predecessors == null) {
			predecessors = new ArrayList<INode>();
		}
		return predecessors;
	}

	/**
	 * Add a node to the set of nodes that should be executed immediately
	 * prior to this node.
	 * @param n node to be added
	**/
	public void addPredecessors(INode n) {
		if(predecessors == null) {
			predecessors = new ArrayList<INode>();
		}
		predecessors.add(n);
	}

	/**
	 * Obtain the set of nodes that should be executed immediately after
	 * to this node.
	 * @return a list of INode objects that consume a resource created by
	 * the node.
	**/
	public ArrayList<INode> getSuccessors() {
		if(successors == null) {
			successors = new ArrayList<INode>();
		}
		return successors;
	}

	/**
	 * Add a node to the set of nodes that should be executed immediately
	 * after this node.
	 * @param n node to be added
	**/
	public void addSuccessor(INode n) {
		if(successors == null) {
			successors = new ArrayList<INode>();
		}
		successors.add(n);
	}

	/**
	 * Obtain the node that is considered the linear predecessor of this
	 * node.
	 * @return the INode that is the linear predecessor of this node
	**/
	public INode getLinearPredecessor() {
		return linearPredecessor;
	}

	/**
	 * Set the node that is considered the linear predecessor of this node.
	 * @param n the INode that is the linear predecessor of this node,
	 * may be null
	**/
	public void setLinearPredecessor(INode n) {
		linearPredecessor = n;
	}

	/**
	 * Override the standard mechanism to obtain a textual version of this object.
	 * @return If set, the nodeText, otherwise the class name
	**/
	public String toString() {
		if(nodeText != null && nodeText.length() > 0) {
			return nodeText;
		}
		return "BasicNodeImpl";
	}
}
