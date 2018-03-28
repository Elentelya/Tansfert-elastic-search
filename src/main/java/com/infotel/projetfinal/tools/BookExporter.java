package com.infotel.projetfinal.tools;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookExporter {

	private RestHighLevelClient client;
	private ObjectMapper mapper;
	
	public void exportBook(List<Book> books) throws Exception{
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		try {
			client = new RestHighLevelClient(
			        RestClient.builder(
			                new HttpHost("localhost", 9200, "http"),
			                new HttpHost("localhost", 9201, "http")));
			
			deleteIndex("bibli");
			createIndex("bibli");
			for (Book book : books) {
				indexBook(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	private void deleteIndex(String index) throws IOException {
		try {
			DeleteIndexRequest request = new DeleteIndexRequest(index);
			DeleteIndexResponse response = client.indices().delete(request);
			
			System.out.println("Suppression index " + index  + " => " + response.isAcknowledged());
		} catch (ElasticsearchException e) {
			System.err.println("Failed deleting index : " + e.getMessage());
		}
	}
	
	private void createIndex(String index) throws IOException {
		CreateIndexRequest request = new CreateIndexRequest(index);
		
		request.mapping("doc", createMapping(index), XContentType.JSON);
		
		CreateIndexResponse response = 
				client.indices().create(request);
		
		System.out.println("Création index " + index + " => " + response.isAcknowledged());
	}
	
	private String createMapping(String index) throws IOException {
		switch (index) {
		case "bibli":
			return createEateryMapping().string();
		
		default:
			return null;
		}
	}
	
	private XContentBuilder createEateryMapping() throws IOException {
		XContentBuilder mapping = XContentFactory.jsonBuilder()
				.startObject()
					.startObject("doc")
						.startObject("properties")
							.startObject("title")
								.field("type", "text")
								.field("analyzer", "french")
								.startObject("fields")
									.startObject("keyword")
										.field("type","keyword")
										.field("ignore_above","256")
									.endObject()
								.endObject()
							.endObject()
							.startObject("description")
								.field("type", "text")
								.field("analyzer", "french")
							.endObject()
							.startObject("suggest")
								.field("type", "completion")
								.field("analyzer", "french")
							.endObject()
						.endObject()
					.endObject()
				.endObject();
				
		return mapping;
	}
	
	private void indexBook(Book b) throws Exception {
		
		String json = mapper.writeValueAsString(b);
		System.out.println(json);

		IndexRequest request = 
				new IndexRequest("bibli", "doc",  Integer.toString(b.getId()));
		request.source(json, XContentType.JSON);
		
		IndexResponse response = client.index(request);
		
		System.out.println("\t" + b.getTitle() + " : version = " + response.getVersion());
	}
}
