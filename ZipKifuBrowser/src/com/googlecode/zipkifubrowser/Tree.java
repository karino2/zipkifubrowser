package com.googlecode.zipkifubrowser;

import java.util.ArrayList;

import com.googlecode.zipkifubrowser.ForestNode.Edge;
import com.googlecode.zipkifubrowser.ForestNode.Traversable;

public class Tree<E> {
	Tree<E> parent;
	public Tree<E> getParent() { return parent; }
	public void setParent(Tree<E> par) { parent = par; }
	
	ArrayList<Tree<E>> children;
	
	E nodeTag;
	public E getTag() { return nodeTag; }
	
	public void addChild(Tree<E> child)
	{
		children.add(child);
		child.setParent(this);
	}
	
	public Tree<E> getChild(int i)
	{
		return children.get(i);
	}
	
	public int getIndex()
	{
		if(parent == null)
			return 0;
		return parent.children.indexOf(this);
	}
	
	public int getChildCount() { return children.size(); }
	
	public Tree(E tag){
		parent = null;
		children = new ArrayList<Tree<E>>();
		nodeTag = tag;
	}
	
	public Tree<String> getChild(Tree<String> elem, int i) {
		return elem.children.get(i);
	}
	
	// please call root node!
	public ForestIterater<Tree<E>> createForestIterater() {
		ForestNode<Tree<E>> rootNode = 
			new ForestNode<Tree<E>>(
					getTraversable(),
					Edge.Leading,
					this);
			
		return new ForestIterater<Tree<E>>(rootNode);		
	}

	Traversable<Tree<E>> getTraversable() { 
		return new Traversable<Tree<E>>() {
			@Override
			public Tree<E> getChild(Tree<E> elem, int i) {
				return elem.getChild(i);
			}
	
			@Override
			public Tree<E> getParent(Tree<E> elem) {
				return elem.getParent();
			}
	
			@Override
			public int getChildCount(Tree<E> elem) {
				return elem.getChildCount();
			}
	
			@Override
			public int getChildIndex(Tree<E> elem) {
				return elem.getIndex();
			}
		};
	}
		
}
