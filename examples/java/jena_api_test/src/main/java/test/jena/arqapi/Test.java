package test.jena.arqapi;

import java.net.URL;
import java.util.Iterator;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
import com.hp.hpl.jena.sparql.core.assembler.DatasetAssemblerVocab;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class Test {
    
	/*
	 * http://jena.apache.org/documentation/query/index.html
	 * http://jena.apache.org/documentation/query/update.html
	 * http://jena.apache.org/documentation/query/app_api.html
	 */
	public static void main(String[] args) {
		
		URL storeURL = Test.class.getClassLoader().getResource("dataset.ttl");
		
		if (storeURL == null) {
			System.out.println("Jena Assembler ttl (dataset.ttl) has not been found");
			return;
		}

		Dataset ds = (Dataset) AssemblerUtils.build(storeURL.toString(), DatasetAssemblerVocab.tDataset);
		
		GraphStore graphStore = GraphStoreFactory.create(ds);
		
		String insertQuery = "INSERT DATA { GRAPH <http://test_graph> {<http://test_graph#subject> <http://test_graph#is> \"simple test\"} }";
		
		UpdateRequest updateRequest = UpdateFactory.create(insertQuery);
		
		UpdateProcessor updateProcessor = UpdateExecutionFactory.create(updateRequest, graphStore);

		updateProcessor.execute();
		
		
		String selectQuery = "SELECT * FROM <http://test_graph> WHERE {?s ?p ?o} LIMIT 10";
		
		Query query = QueryFactory.create(selectQuery);
		
		QueryExecution queryExecution = QueryExecutionFactory.create(query, ds);
		
		ResultSet rs = queryExecution.execSelect();
		
		while (rs.hasNext()) {
			
			QuerySolution soln = rs.nextSolution();
			Iterator<String> vars = soln.varNames();

			while (vars.hasNext()) {
				
				String var = vars.next();
				RDFNode node = soln.get(var);
				
				if (node.isLiteral()) {
					System.out.println("literal : " + ((Literal) node).getLexicalForm());
				}

				if (node.isResource()) {
					Resource r = (Resource) node;

					if (!r.isAnon()) {
						System.out.println("uri : " + r.getURI());						
					}
				}				
			}
		}
		
		
		
		String deleteQuery = "DELETE WHERE { GRAPH <http://test_graph> { ?s ?p ?o . } }";
		
		updateRequest = UpdateFactory.create(deleteQuery);
		
		updateProcessor = UpdateExecutionFactory.create(updateRequest, graphStore);

		updateProcessor.execute();
		
		
		
		selectQuery = "SELECT * FROM <http://test_graph> WHERE {?s ?p ?o} LIMIT 10";
		
		query = QueryFactory.create(selectQuery);
		
		queryExecution = QueryExecutionFactory.create(query, ds);
		
		rs = queryExecution.execSelect();
		
		if (!rs.hasNext()) {
			System.out.println("Graph <http://test_graph> has been deleted");			
		} else {
			System.out.println("Graph <http://test_graph> has not been deleted");			
		}
		
		queryExecution.close();
		ds.close();
	}
}
