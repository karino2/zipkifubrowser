package com.googlecode.zipkifubrowser.test;


import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;



import com.googlecode.zipkifubrowser.ForestIterater;
import com.googlecode.zipkifubrowser.ForestNode;
import com.googlecode.zipkifubrowser.ForestNode.Edge;
import com.googlecode.zipkifubrowser.ForestNode.Traversable;




public class ForestIteraterTest {
	class TreeForTest
	{
		TreeForTest parent;
		public String treeTag;
		public ArrayList<TreeForTest> children;
		TreeForTest(String tag)
		{
			parent = null;
			treeTag = tag;
			children = new ArrayList<TreeForTest>();
		}
		TreeForTest(TreeForTest par, String tag)
		{
			parent = par;
			treeTag = tag;
			children = new ArrayList<TreeForTest>();
		}
		
		void addChild(TreeForTest child)
		{
			children.add(child);
			child.parent = this;
		}
	}
		
	TreeForTest createTree(String tag)
	{
		return new TreeForTest(tag);
	}

	public ForestIterater<TreeForTest> createIterater(TreeForTest root)
	{
		ForestNode<TreeForTest> rootNode = 
			new ForestNode<TreeForTest>(new Traversable<TreeForTest>() {

				@Override
				public TreeForTest getChild(TreeForTest elem, int i) {
					return elem.children.get(i);
				}

				@Override
				public TreeForTest getParent(TreeForTest elem) {
					return elem.parent;
				}

				@Override
				public int getChildCount(TreeForTest elem) {
					return elem.children.size();
				}

				@Override
				public int getChildIndex(TreeForTest elem) {
					return elem.parent.children.indexOf(elem);
				}
			},
			Edge.Leading,
			root);
			
		return new ForestIterater<TreeForTest>(rootNode);
	}
	
	@Test
	public void test_iterater()
	{
		/*
		 * a--b-+-d
		 *    | |
		 *    | +-e
		 *    c
		 */
		TreeForTest a = createTree("a");
		TreeForTest b = createTree("b");
		TreeForTest c = createTree("c");
		TreeForTest d = createTree("d");
		TreeForTest e = createTree("e");
		a.addChild(b);
		a.addChild(c);
		b.addChild(d);
		b.addChild(e);
		
		ForestIterater<TreeForTest> iter = createIterater(a);
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Leading, a, iter.next());
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Leading, b, iter.next());
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Leading, d, iter.next());
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Trailing, d, iter.next());
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Leading, e, iter.next());
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Trailing, e, iter.next());
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Trailing, b, iter.next());
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Leading, c, iter.next());
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Trailing, c, iter.next());
		
		assertTrue(iter.hasNext());
		assertNode(Edge.Trailing, a, iter.next());
		
		assertFalse(iter.hasNext());
	}
	
	void assertNode(Edge expectE, TreeForTest expectNode, ForestNode<TreeForTest> actual)
	{
		assertEquals(expectE, actual.getEdge());
		assertEquals(expectNode, actual.getElement());
	}
}
