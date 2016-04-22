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
import java.util.List;
import java.util.Map;

class Node{
	int scene_index;
//	int global_index;
	int ground_truth_label;
    String ground_truth_label_name;
	List<Double> features = new ArrayList<>();
	Map<Integer,Double> distances = new HashMap<>();
}

public class Scene {
	Map<Integer, Node> leaf_nodes = new HashMap<>();
	Node tree_root;
	String name;

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
		Node curr_node = null;
		
		//skip root id
		br.readLine();
		
		name = br.readLine().split(" ")[1];
		
		while((rd_str=br.readLine())!=null)
		{
			if(rd_str.startsWith("newModel"))
			{
				if(curr_node!=null)	leaf_nodes.put(curr_node.scene_index, curr_node);
				
				curr_node = new Node();
				curr_node.scene_index = Integer.parseInt(rd_str.split(" ")[1]);
				continue;
			}
			
			else if(curr_node == null)	continue;
			
			if(rd_str.startsWith("label")){
				curr_node.ground_truth_label = Integer.parseInt(rd_str.split(" ")[1]);
				curr_node.ground_truth_label_name = rd_str.split(" ")[2];
			}
			else if(rd_str.startsWith("unary_descriptor")){
				
				String[] splt_str = rd_str.split(" ");
				
				curr_node.features = new ArrayList<Double>();
				
				for(int i=2; i<splt_str.length; i++){
					curr_node.features.add(Double.parseDouble(splt_str[i]));
				}
			}
			
		}

		if(curr_node!=null)	leaf_nodes.put(curr_node.scene_index, curr_node);
		
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
			
			Node curr_Node = leaf_nodes.get(i);
			rd_str = br.readLine().split(" ");
			int cnt = 0;
			
			for (Integer j : node_idx_lst)
				curr_Node.distances.put(j, Double.parseDouble(rd_str[cnt++]));
			
		}
		
		br.close();
	}
}
