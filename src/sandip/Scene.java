/*
*
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%    ___  ___  ____  ___  ______________
%   / _ \/ _ \/ __ \/ _ )/ __/  _/_  __/
%  / ___/ , _/ /_/ / _  / _/_/ /  / /   
% /_/  /_/|_|\____/____/_/ /___/ /_/    
%                                      
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
*
*/

package sandip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class Scene {
	Map<Integer, SceneNode> leaf_nodes = new HashMap<>();
	SceneNode tree_root;
	String name;
//	List<Node> curr_nodes = new ArrayList<>();
	Map<Integer, SceneNode>  curr_nodes = new HashMap<>();
	int max_index = -1;
	
	SceneTree sct;
	
	/**
	 * 
	 * @param hier_file_path
	 * @param dist_file_path
	 * @throws IOException
	 */
	public void parseScene(String hier_file_path, String dist_file_path) throws IOException{
		
		parseHierfile(hier_file_path);
		parseDistfile(dist_file_path);
		
	}
	
	/**
	 * 
	 * @param hier_file_path
	 * @throws IOException
	 */
	private void parseHierfile(String hier_file_path) throws IOException
	{
		String rd_str;
		BufferedReader br = new BufferedReader(new FileReader(new File(hier_file_path)));
		SceneNode curr_node = null;
		
		//skip root id
		br.readLine();
		
		name = br.readLine().split(" ")[1];
		
		while((rd_str=br.readLine())!=null)
		{
			if(rd_str.startsWith("newModel"))
			{
				//skip root = curr_node.parent !=-1
				if(curr_node!=null && curr_node.parent !=-1){
					max_index = (max_index<curr_node.node_index)?curr_node.node_index:max_index;
					curr_node.is_leaf = true;
					leaf_nodes.put(curr_node.node_index, curr_node);
//					curr_nodes.add(curr_node);
					curr_nodes.put(curr_node.node_index, curr_node);
				}
				
				curr_node = new SceneNode();
				curr_node.node_index = Integer.parseInt(rd_str.split(" ")[1]);
				continue;
			}
			
			else if(curr_node == null)	continue;
			
			if(rd_str.startsWith("label")){
				curr_node.ground_truth_label = Integer.parseInt(rd_str.split(" ")[1]);
				curr_node.ground_truth_label_name = rd_str.split(" ")[2];
			}
			
			else if(rd_str.startsWith("parent"))
				curr_node.parent = Integer.parseInt(rd_str.split(" ")[1]);
			
			else if(rd_str.startsWith("unary_descriptor")){
				
				String[] splt_str = rd_str.split(" ");
				
				curr_node.features = new ArrayList<Double>();
				
				for(int i=2; i<splt_str.length; i++){
					curr_node.features.add(Double.parseDouble(splt_str[i]));
				}
			}
			
			else if(rd_str.startsWith("proxy_box")){
				String[] splt_str = rd_str.split(" ");
				curr_node.node_bbox = new BBox(Double.parseDouble(splt_str[1]), Double.parseDouble(splt_str[2]), 
						Double.parseDouble(splt_str[3]), Double.parseDouble(splt_str[4]), 
						Double.parseDouble(splt_str[5]), Double.parseDouble(splt_str[6]));
			}
			
		}

		if(curr_node!=null){
			max_index = (max_index<curr_node.node_index)?curr_node.node_index:max_index;
			leaf_nodes.put(curr_node.node_index, curr_node);
//			curr_nodes.add(curr_node);
			curr_nodes.put(curr_node.node_index, curr_node);
		}
		
		br.close();
	}
	
	/**
	 * 
	 * @param dist_file_path
	 * @throws IOException
	 */
	private void parseDistfile(String dist_file_path) throws IOException
	{
		String[] rd_str;
		BufferedReader br = new BufferedReader(new FileReader(new File(dist_file_path)));
		
		int node_cnt = Integer.parseInt(br.readLine());
		List<Integer> node_idx_lst = new ArrayList<>();
		
		for(int i=0; i<node_cnt;i++){

			rd_str = br.readLine().split(" ");
			int node_idx = Integer.parseInt(rd_str[0]);
			node_idx_lst.add(node_idx);
			
			//we're reading it again as the hier file contains space
			//here the spaces are replaced with "_"
			leaf_nodes.get(node_idx).ground_truth_label_name = rd_str[3];
		}
		
		for(Integer i : node_idx_lst)
		{
			
			SceneNode curr_Node = leaf_nodes.get(i);
			rd_str = br.readLine().split(" ");
			int cnt = 0;
			
			for (Integer j : node_idx_lst)
				curr_Node.distances.put(j, Double.parseDouble(rd_str[cnt++]));
			
		}
		
		br.close();
	}
	
	public SceneTree buildSceneTree()
	{
		
		sct = new SceneTree();
		
		for (int idx : curr_nodes.keySet())		sct.node_map.put(idx, curr_nodes.get(idx));
		
		while(curr_nodes.size() > 1)
		{
			double min_dist = Double.MAX_VALUE;
			int min_node1 = -1;
			int min_node2 = -1;
			for (int id_i : curr_nodes.keySet()) {
				for (int id_j : curr_nodes.keySet()) {
					if(id_i!=id_j)
					{
						double dist = calculateNodeDist(id_i, id_j);
						if(min_dist>dist){
							min_dist = dist;
							min_node1 = id_i;
							min_node2 = id_j;
						}
					}
				}
			}
			SceneNode new_node = mergeNodes(min_node1,min_node2);
			sct.node_map.put(new_node.node_index, new_node);
		}
		
		tree_root = curr_nodes.get(max_index);
		sct.root = tree_root;
//		System.out.println("Root - " + tree_root.ground_truth_label_name);
		
		return sct;
	}
	
	private double calculateNodeDist(int nd1, int nd2)
	{
		SceneNode node1 = curr_nodes.get(nd1);
		SceneNode node2 = curr_nodes.get(nd2);
		
		double feature_dist = 0;
		double spatial_dist = 0;
		
		for(int i = 0; i<node1.features.size(); i++)	
			feature_dist+=(node1.features.get(i)-node2.features.get(i))*(node1.features.get(i)-node2.features.get(i));
		for(int i = 0; i<node1.node_bbox.getBBoxCenter().size(); i++)	
			spatial_dist+=(node1.node_bbox.getBBoxCenter().get(i)-node2.node_bbox.getBBoxCenter().get(i))*(node1.node_bbox.getBBoxCenter().get(i)-node2.node_bbox.getBBoxCenter().get(i));
		
//		System.out.println(node1.ground_truth_label_name + "-" + node2.ground_truth_label_name + "(" + 
//													+node1.node_index + "-" + node2.node_index + ")" +" = " + 
//													Math.sqrt(feature_dist) + "/" + Math.sqrt(spatial_dist));
		
		return Math.sqrt(feature_dist)+Math.sqrt(spatial_dist);
	}
	
	private SceneNode mergeNodes(int nd1, int nd2)
	{
		SceneNode node1 = curr_nodes.get(nd1);
		SceneNode node2 = curr_nodes.get(nd2);
		
		SceneNode curr_node = new SceneNode();
		curr_node.node_index = ++max_index;
		curr_node.ground_truth_label_name = node1.ground_truth_label_name + "+" + node2.ground_truth_label_name; 
		
		node1.parent = curr_node.node_index;
		node2.parent = curr_node.node_index;
		
		for(int i = 0; i<node1.features.size(); i++)	
			curr_node.features.add((node1.features.get(i)+node2.features.get(i))/2);
		
		curr_node.node_bbox = new BBox(Math.min(node1.node_bbox.x_min, node2.node_bbox.x_min)
										, Math.min(node1.node_bbox.y_min, node2.node_bbox.y_min)
										, Math.min(node1.node_bbox.z_min, node2.node_bbox.z_min)
										, Math.max(node1.node_bbox.x_max, node2.node_bbox.x_max)
										, Math.max(node1.node_bbox.y_max, node2.node_bbox.y_max)
										, Math.max(node1.node_bbox.z_max, node2.node_bbox.z_max));
		
		curr_node.children.add(node1);
		curr_node.children.add(node2);
		
		curr_nodes.remove(nd1);
		curr_nodes.remove(nd2);
		
		curr_nodes.put(curr_node.node_index, curr_node);
		
//		System.out.println(curr_node.node_index + ". " + curr_node.ground_truth_label_name + "(" + nd1 + "+" + nd2 + ")");
		
		return curr_node;
		
	}
	
	@Override
	public String toString() {
		printTreeQ(tree_root);
		return "";
	}
	
	private void printTreeQ(SceneNode tn) {
		
		Queue<SceneNode> q = new LinkedList<>();
		q.add(tree_root);
		
		while(!q.isEmpty()){
			SceneNode nd = q.poll();
			
			for (SceneNode ch : nd.children) {
				q.offer(ch);
			}
			
			System.out.println(nd.ground_truth_label_name);
			
		}

	}
	
}
