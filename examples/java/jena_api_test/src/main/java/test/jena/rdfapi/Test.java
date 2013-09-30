package test.jena.rdfapi;

import java.net.URL;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
import com.hp.hpl.jena.sparql.core.assembler.DatasetAssemblerVocab;

public class Test {
    
	/*
	 * http://jena.apache.org/tutorials/rdf_api.html
	 */
	public static void main(String[] args) {
		
		URL storeURL = Test.class.getClassLoader().getResource("dataset.ttl");		
		
		if (storeURL == null) {
			System.out.println("Jena Assembler ttl (dataset.ttl) has not been found");
			return;
		}

		/*
		 * http://jena.apache.org/documentation/assembler/inside-assemblers.html
		 */
		Dataset ds = (Dataset) AssemblerUtils.build(storeURL.toString(), DatasetAssemblerVocab.tDataset);
		
		/*
		 * To create graph
		 */
		DatasetGraph dsg = ds.asDatasetGraph();
		
		Node graph = NodeFactory.createURI("http://test_graph");
		Node subject = NodeFactory.createURI("http://test_graph#subject");
		Node predicate = NodeFactory.createURI("http://test_graph#is");
		Node object = NodeFactory.createLiteral("15.5", XSDDatatype.XSDfloat);

		dsg.add(new Quad(graph, subject, predicate, object));		
		
		/*
		 *	To check if graph exists
		 */
		boolean contains = ds.containsNamedModel("http://test_graph");
		
		System.out.println("Contains graph " + contains);
		
		/*
		 *	To check if triple exists
		 */
		Model model = ds.getNamedModel("http://test_graph");

		contains = model.containsLiteral(model.createResource("http://test_graph#subject"), model.createProperty("http://test_graph#is"), 15.5f);

		System.out.println("Contains triple " + contains);
		
		/*
		 * To get all triples from graph
		 */
		
		StmtIterator iterator = model.listStatements(null, null, null, null); //все триплы

		while (iterator.hasNext()) {
			
			Statement st = iterator.next();
			System.out.println(st.getSubject() + " " + st.getPredicate() + " " + st.getObject());			
		}
		
		/*
		 * To delete triple from graph
		 */
		
		model.remove(model.createStatement(model.createResource("http://test_graph#subject"), model.createProperty("http://test_graph#is"), model.asRDFNode(object)));
		//model.removeAll(model.createResource("http://test_graph#subject"), model.createProperty("http://test_graph#is"), (RDFNode) null);

		contains = model.containsLiteral(model.createResource("http://test_graph#subject"), model.createProperty("http://test_graph#is"), 15.5f);

		System.out.println("Contains triple after deletion " + contains);		

		model.close();
		
		/*
		 * To delete graph
		 */
		
		ds.removeNamedModel("http://test_graph");
		
		contains = ds.containsNamedModel("http://test_graph");
		
		System.out.println("Contains graph after deletion " + contains);		
		
		ds.close();
	}
}
