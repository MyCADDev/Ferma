package org.jglue.totorom;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bryn Cooke (http://jglue.org)
 */

public class TestFramedVertex {

	
	private FramedGraph fg;
	private Person p1;
	private Person p2;
	private Knows e1;
    
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		Graph g = new TinkerGraph();
        fg = new FramedGraph(g);
        p1 = fg.addVertex(Person.class);
        p2 = fg.addVertex(Person.class);
        p1.setName("Bryn");
        p2.setName("Julia");
        e1 = p1.addKnows(p2);
        e1.setYears(15);

	}
	
    @Test
    public void testOut() {
        
        Assert.assertEquals(p2, p1.out().next(Person.class));
        Assert.assertEquals(p2, p1.out(1).next(Person.class));
    }

    
    @Test
    public void testIn() {
        Assert.assertEquals(p1, p2.in().next(Person.class));
        Assert.assertEquals(p1, p2.in(1).next(Person.class));
        
    }
    
    @Test
    public void testOutE() {
        Assert.assertEquals(e1, p1.outE().next(Knows.class));
        Assert.assertEquals(e1, p1.outE(1).next(Knows.class));
    }
    
    @Test
    public void testInE() {
        Assert.assertEquals(e1, p2.inE().next(Knows.class));
        Assert.assertEquals(e1, p2.inE(1).next(Knows.class));
    }
    
    @Test
    public void testBoth() {
        Assert.assertEquals(p2, p1.both().next(Person.class));
        Assert.assertEquals(p2, p1.both(1).next(Person.class));
        Assert.assertEquals(p1, p2.both().next(Person.class));
        Assert.assertEquals(p1, p2.both(1).next(Person.class));
    }
    
    @Test
    public void testBothE() {
        Assert.assertEquals(e1, p2.bothE().next(Knows.class));
        Assert.assertEquals(e1, p2.bothE(1).next(Knows.class));
    }

    @Test
    public void testLinkOutSingleLabel(){
        Person p3 = fg.addVertex(Person.class);
        p3.setName("Tjad");

        String label = "knows";
        long expectedEdgeCount = p3.outE(label).count()+1;
        long expectedVerticesCount =  p3.out(label).retain(Lists.newArrayList(p1.element())).count()+1;

        p3.linkOut(p1, label);
        //Make sure a new edge was created
        Assert.assertEquals("knows edge was not created", expectedEdgeCount, p3.outE(label).count());

        //Make sure the edge was created to the correct vertex
        Assert.assertEquals("Incorrect vertex associated", expectedVerticesCount, p3.out(label).retain(Lists.newArrayList(p1.element())).count());
    }

    @Test
    public void testLinkOutMultiLabel(){

        String[] newLabels = new String[]{"knows", "friends_with"};

        Person p3 = fg.addVertex(Person.class);
        p3.setName("Tjad");

        Map expectedCounts = new HashMap();

        for(String label : newLabels){
            expectedCounts.put("expected_e_" + label, p3.outE(label).count() + 1);
            expectedCounts.put("expected_v_" + label, p3.out(label).retain(Lists.newArrayList(p1.element())).count() + 1);
        }

        p3.linkOut(p1, newLabels);


        for(String label : newLabels){
            //Make sure a new edge was created
            Assert.assertEquals("knows edge was not created", expectedCounts.get("expected_e_" + label), p3.outE(label).count());

            //Make sure the edge was created to the correct vertex
            Assert.assertEquals("Incorrect vertex associated", expectedCounts.get("expected_v_" + label), p3.out(label).retain(Lists.newArrayList(p1.element())).count());
        }
          
    }

    @Test
    public void testLinkInSingleLabel(){
        Person p3 = fg.addVertex(Person.class);
        p3.setName("Tjad");

        String label = "knows";
        long expectedEdgeCount = p3.inE(label).count()+1;
        long expectedVerticesCount =  p3.in(label).retain(Lists.newArrayList(p1.element())).count()+1;

        p3.linkIn(p1, label);
        //Make sure a new edge was created
        Assert.assertEquals("knows edge was not created", expectedEdgeCount, p3.inE(label).count());

        //Make sure the edge was created to the correct vertex
        Assert.assertEquals("Incorrect vertex associated", expectedVerticesCount, p3.in(label).retain(Lists.newArrayList(p1.element())).count());
    }

    @Test
    public void testLinkInMultiLabel(){

        String[] newLabels = new String[]{"knows", "friends_with"};

        Person p3 = fg.addVertex(Person.class);
        p3.setName("Tjad");

        Map expectedCounts = new HashMap();

        for(String label : newLabels){
            expectedCounts.put("expected_e_" + label, p3.inE(label).count() + 1);
            expectedCounts.put("expected_v_" + label, p3.in(label).retain(Lists.newArrayList(p1.element())).count() + 1);
        }

        p3.linkIn(p1, newLabels);


        for(String label : newLabels){
            //Make sure a new edge was created
            Assert.assertEquals("knows edge was not created", expectedCounts.get("expected_e_" + label), p3.inE(label).count());

            //Make sure the edge was created to the correct vertex
            Assert.assertEquals("Incorrect vertex associated", expectedCounts.get("expected_v_" + label), p3.in(label).retain(Lists.newArrayList(p1.element())).count());
        }

    }

    @Test
    public void testLinkBothSingleLabel(){
        Person p3 = fg.addVertex(Person.class);
        p3.setName("Tjad");

        String label = "knows";
        long expectedEdgeCount = p3.bothE(label).count()+2;
        long tailCount =  p3.in(label).retain(Lists.newArrayList(p1.element())).count()+1;
        long headCount =  p3.out(label).retain(Lists.newArrayList(p1.element())).count()+1;

        p3.linkBoth(p1, label);
        //Make sure a new edge was created
        Assert.assertEquals("knows edge was not created", expectedEdgeCount, p3.bothE(label).count());

        //Make sure the edge was created to the correct vertex
        Assert.assertEquals("Incorrect in vertex associated", tailCount, p3.in(label).retain(Lists.newArrayList(p1.element())).count());
        Assert.assertEquals("Incorrect out vertex associated", headCount, p3.out(label).retain(Lists.newArrayList(p1.element())).count());
    }

    @Test
    public void testLinkBothMultiLabel(){

        String[] newLabels = new String[]{"knows", "friends_with"};

        Person p3 = fg.addVertex(Person.class);
        p3.setName("Tjad");

        Map expectedCounts = new HashMap();

        for(String label : newLabels){
            expectedCounts.put("expected_e_" + label, p3.bothE(label).count() + 2);
            expectedCounts.put("expected_tail_" + label, p3.in(label).retain(Lists.newArrayList(p1.element())).count()+1);
            expectedCounts.put("expected_head_" + label, p3.out(label).retain(Lists.newArrayList(p1.element())).count()+1);
        }

        p3.linkBoth(p1, newLabels);


        for(String label : newLabels){
            //Make sure a new edge was created
            Assert.assertEquals("knows edge was not created", expectedCounts.get("expected_e_" + label), p3.bothE(label).count());

            //Make sure the edge was created to the correct vertex
            Assert.assertEquals("Incorrect vertex associated", expectedCounts.get("expected_tail_" + label), p3.in(label).retain(Lists.newArrayList(p1.element())).count());
            Assert.assertEquals("Incorrect vertex associated", expectedCounts.get("expected_head_" + label), p3.out(label).retain(Lists.newArrayList(p1.element())).count());
        }

    }

    @Test
    public void testUnlinkInWithNull(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);
        Person p5 = fg.addVertex(Person.class);

        p4.addEdge(label, p3, Knows.class);
        p5.addEdge(label, p3, Knows.class);

        Assert.assertTrue("Multiple edges(in) of type "+label+" must exist for vertice", p3.in(label).count() > 1);

        p3.unlinkIn(null, label);

        //Make all edges were removed
        Assert.assertEquals("All " + label + " edges(in) should have been removed", 0, p3.in(label).count());
    }

    @Test
    public void testUnlinkOutWithNull(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);
        Person p5 = fg.addVertex(Person.class);

        p3.addEdge(label, p4, Knows.class);
        p3.addEdge(label, p5, Knows.class);

        Assert.assertTrue("Multiple edges(out) of type "+label+" must exist for vertice", p3.out(label).count() > 1);

        p3.unlinkOut(null, label);

        //Make all edges were removed
        Assert.assertEquals("All " + label + " edges(out) should have been removed", 0, p3.out(label).count());

    }

    @Test
    public void testUnlinkBothWithNull(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);
        Person p5 = fg.addVertex(Person.class);

        p3.addEdge(label, p4, Knows.class);
        p3.addEdge(label, p5, Knows.class);

        Assert.assertTrue("Multiple edges(out) of type "+label+" must exist for vertice", p3.out(label).count() > 1);

        p4.addEdge(label, p3, Knows.class);
        p5.addEdge(label, p3, Knows.class);

        Assert.assertTrue("Multiple edges(in) of type "+label+" must exist for vertice", p3.in(label).count() > 1);

        p3.unlinkBoth(null, label);

        //Make all edges were removed
        Assert.assertEquals("All " + label + " edges should have been removed", 0, p3.both(label).count());

    }

    @Test
    public void testUnlinkIn(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);
        Person p5 = fg.addVertex(Person.class);

        p4.addEdge(label, p3, Knows.class);
        p5.addEdge(label, p3, Knows.class);
        long allInEdgesCount = p3.in(label).count();
        Long targetVertex_InEdgeCount = p3.in(label).as("e").retain(Lists.newArrayList(p4.element())).back("e").count();

        Assert.assertTrue("target vertex requires an in edge for " + label, targetVertex_InEdgeCount > 0);
        Assert.assertTrue("Multiple edges(in) of type "+label+" must exist for vertice", allInEdgesCount - targetVertex_InEdgeCount > 0);

        p3.unlinkIn(p4, label);

        //Make all edges were removed
        Assert.assertEquals("All " + label + " edges(in) should have been removed from given vertex", 0, p3.in(label).retain(Lists.newArrayList(p4.element())).count());
        Assert.assertTrue("All " + label + " edges(in) should have remained for other vertices", p3.inE(label).count() > 0);
    }

    @Test
    public void testUnlinkOut(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);
        Person p5 = fg.addVertex(Person.class);

        p3.addEdge(label, p4, Knows.class);
        p3.addEdge(label, p5, Knows.class);
        long allInEdgesCount = p3.out(label).count();
        Long targetVertex_OutEdgeCount = p3.out(label).as("e").retain(Lists.newArrayList(p4.element())).back("e").count();

        Assert.assertTrue("target vertex requires an in edge for "+label, targetVertex_OutEdgeCount > 0);
        Assert.assertTrue("Multiple edges(out) of type "+label+" must exist for vertice", allInEdgesCount - targetVertex_OutEdgeCount > 0);

        p3.unlinkOut(p4, label);

        //Make all edges were removed
        Assert.assertEquals("All " + label + " edges(in) should have been removed from given vertex", 0, p3.out(label).retain(Lists.newArrayList(p4.element())).count());
        Assert.assertTrue("All " + label + " edges(in) should have remained for other vertices", p3.outE(label).count() > 0);
    }

    @Test
    public void testUnlinkBoth(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);
        Person p5 = fg.addVertex(Person.class);

        p4.addEdge(label, p3, Knows.class);
        p5.addEdge(label, p3, Knows.class);
        p3.addEdge(label, p4, Knows.class);
        p3.addEdge(label, p5, Knows.class);

        long allInEdgesCount = p3.both(label).count();
        Long targetVertex_EdgeCount = p3.both(label).as("e").retain(Lists.newArrayList(p4.element())).back("e").count();

        Assert.assertTrue("target vertex requires an in/out edge for "+label, targetVertex_EdgeCount > 0);
        Assert.assertTrue("Multiple edges of type "+label+" must exist for vertice", allInEdgesCount - targetVertex_EdgeCount > 0);

        p3.unlinkBoth(p4, label);

        //Make all edges were removed
        Assert.assertEquals("All " + label + " edges(in/out) should have been removed from given vertex", 0, p3.both(label).retain(Lists.newArrayList(p4.element())).count());
        Assert.assertTrue("All " + label + " edges(in/out) should have remained for other vertices", p3.bothE(label).count() > 0);
    }

    @Test
    public void testSetLinkIn(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);
        Person p5 = fg.addVertex(Person.class);

        p4.addEdge(label, p3, Knows.class);

        Assert.assertTrue("An edge(in) of type " + label + " must exist between vertices", p3.in(label).retain(Lists.newArrayList(p4.element())).count() > 0);

        p3.setLinkIn(p5, label);

        //Make sure old edge was deleted
        Assert.assertEquals("old " + label + " edge was not removed", 0, p3.in(label).retain(Lists.newArrayList(p4.element())).count());


        //Make sure a new edge was created (and to correct vertex)
        Assert.assertEquals(label + " edge was not created", 1, p3.in(label).retain(Lists.newArrayList(p5.element())).count());
    }

    @Test
    public void testSetLinkOut(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);
        Person p5 = fg.addVertex(Person.class);

        p3.addEdge(label, p4, Knows.class);

        Assert.assertTrue("An out edge of type "+label+" must exist between vertices", p3.out(label).retain(Lists.newArrayList(p4.element())).count() > 0);

        p3.setLinkOut(p5, label);

        //Make sure old edge was deleted
        Assert.assertEquals("old " + label + " edge was not removed", 0, p3.out(label).retain(Lists.newArrayList(p4.element())).count());


        //Make sure a new edge was created (and to correct vertex)
        Assert.assertEquals(label + " edge was not created", 1, p3.out(label).retain(Lists.newArrayList(p5.element())).count());
    }

    @Test
    public void testSetLinkBoth(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);
        Person p5 = fg.addVertex(Person.class);

        p4.addEdge(label, p3, Knows.class);
        p3.addEdge(label, p4, Knows.class);

        Assert.assertTrue("An in edge of type " + label + " must exist between vertices", p3.in(label).retain(Lists.newArrayList(p4.element())).count() > 0);
        Assert.assertTrue("An out edge of type "+label+" must exist between vertices", p3.out(label).retain(Lists.newArrayList(p4.element())).count() > 0);

        p3.setLinkBoth(p5, label);

        //Make sure old edge was deleted
        Assert.assertEquals("old " + label + " edge in was not removed", 0, p3.in(label).retain(Lists.newArrayList(p4.element())).count());
        Assert.assertEquals("old " + label + " edge out was not removed", 0, p3.out(label).retain(Lists.newArrayList(p4.element())).count());


        //Make sure a new edges were created (and to correct vertex)
        Assert.assertEquals(label + " in edge was not created", 1, p3.in(label).retain(Lists.newArrayList(p5.element())).count());
        Assert.assertEquals(label + " out edge was not created", 1, p3.out(label).retain(Lists.newArrayList(p5.element())).count());

    }

    @Test
    public void testSetNewLinkIn(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);

        p4.addEdge(label, p3, Knows.class);

        Assert.assertTrue("An out edge of type "+label+" must exist between vertices", p3.in(label).retain(Lists.newArrayList(p4.element())).count() > 0);

        Person p5 = (Person)p3.setLinkIn(Person.class, label);

        //Make sure old edge was deleted
        Assert.assertEquals("old " + label + " edge was not removed", 0, p3.in(label).retain(Lists.newArrayList(p4.element())).count());


        //Make sure a new edge was created (and to correct vertex)
        Assert.assertEquals(label + " edge was not created", 1, p3.in(label).retain(Lists.newArrayList(p5.element())).count());

    }

    @Test
    public void testSetNewLinkOut(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);

        p3.addEdge(label, p4, Knows.class);

        Assert.assertTrue("An out edge of type "+label+" must exist between vertices", p3.out(label).retain(Lists.newArrayList(p4.element())).count() > 0);

        Person p5 = (Person)p3.setLinkOut(Person.class, label);

        //Make sure old edge was deleted
        Assert.assertEquals("old " + label + " edge was not removed", 0, p3.out(label).retain(Lists.newArrayList(p4.element())).count());


        //Make sure a new edge was created (and to correct vertex)
        Assert.assertEquals(label + " edge was not created", 1, p3.out(label).retain(Lists.newArrayList(p5.element())).count());

    }

    @Test
    public void testSetNewLinkBoth(){
        String label = "knows";
        Person p3 = fg.addVertex(Person.class);
        Person p4 = fg.addVertex(Person.class);

        p3.addEdge(label, p4, Knows.class);
        p4.addEdge(label, p3, Knows.class);

        Assert.assertTrue("An in edge of type "+label+" must exist between vertices", p3.in(label).retain(Lists.newArrayList(p4.element())).count() > 0);
        Assert.assertTrue("An out edge of type "+label+" must exist between vertices", p3.out(label).retain(Lists.newArrayList(p4.element())).count() > 0);

        Person p5 = (Person)p3.setLinkBoth(Person.class, label);

        //Make sure old edge was deleted
        Assert.assertEquals("old " + label + " edge was not removed", 0, p3.both(label).retain(Lists.newArrayList(p4.element())).count());


        //Make sure a new edge was created (and to correct vertex)
        Assert.assertEquals(label + " edge(out) was not created", 1, p3.out(label).retain(Lists.newArrayList(p5.element())).count());
        Assert.assertEquals(label + " edge(in) was not created", 1, p3.in(label).retain(Lists.newArrayList(p5.element())).count());
    }


}
