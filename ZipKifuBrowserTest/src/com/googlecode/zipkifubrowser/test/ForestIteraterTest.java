package com.googlecode.zipkifubrowser.test;


import static org.junit.Assert.*;

import org.junit.Test;



import com.googlecode.zipkifubrowser.ForestIterater;
import com.googlecode.zipkifubrowser.ForestNode;
import com.googlecode.zipkifubrowser.ForestNode.Edge;
import com.googlecode.zipkifubrowser.Tree;




public class ForestIteraterTest {
	Tree<String> createTree(String tag)
	{
		return new Tree<String>(tag);
	}

	public ForestIterater<Tree<String>> createIterater(Tree<String> root)
	{
		return root.createForestIterater();
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
		Tree<String> a = createTree("a");
		Tree<String> b = createTree("b");
		Tree<String> c = createTree("c");
		Tree<String> d = createTree("d");
		Tree<String> e = createTree("e");
		a.addChild(b);
		a.addChild(c);
		b.addChild(d);
		b.addChild(e);
		
		ForestIterater<Tree<String>> iter = createIterater(a);
		
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
	
	void assertNode(Edge expectE, Tree<String> expectNode, ForestNode<Tree<String>> actual)
	{
		assertEquals(expectE, actual.getEdge());
		assertEquals(expectNode, actual.getElement());
	}
}
