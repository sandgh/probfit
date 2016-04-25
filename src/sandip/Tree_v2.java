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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Tree_v2 {

	TreeNode root = null;
	
	//format <node_id, node> - mapped node_index->node
	Map<Integer, TreeNode> node_map = new HashMap<>();
	
	//these two lists contains the mapped and unmapped nodes while matching 2 trees
	List<Integer> unmapped_list = new ArrayList<>();
	Map<Integer, Integer> mapped_map = new HashMap<>();
	
	Map<Integer, Integer> curr_map = new HashMap<>();
	Map<Integer, Integer> final_map = new HashMap<>();
	
	/**
	 * Loads the tree from a given string (this can be constructor also)
	 * @param treeStr - Tree i/p format - <node_index,node_val,parent_node_index>;<node_index,node_val,parent_node_index>; ... 
	 * @return - root node of the tree (not in use for now)
	 */
	public TreeNode loadTree(String treeStr)
	{
		//splits each node data
		String nodes[] = treeStr.split(";");
		int node_index = 0;
		
		//for each node
		for (String str : nodes){
			
			String[] node_str = str.trim().split(",");

			//create a new node
			TreeNode tn = new TreeNode();
			
			tn.node_index = node_index++;
			tn.node_val = Integer.parseInt(node_str[0]);
			tn.parent = Integer.parseInt(node_str[1]);
		
			//not the root
			if(tn.parent != -1){
				TreeNode p = node_map.get(tn.parent);
//				tn.node_depth = p.node_depth+1;
				p.children.add(tn);
			}
			else{
//				tn.node_depth=0;
				root = tn;
			}
			
			node_map.put(tn.node_index, tn);
		}
		
		for (int i : node_map.keySet()) {
			
			TreeNode t1 = node_map.get(i);
			
			if(t1.children.size() == 0 )	t1.is_leaf=true;
		}
		
		return root;
	}
	
	/**
	 * This will be called before we start matching two trees
	 */
	private void addAllNodesToUnmappedList(){
		
		unmapped_list.clear();
		mapped_map.clear();
		
		for (int idx : node_map.keySet())	unmapped_list.add(idx);
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public Map<String, Integer> compareTree(Tree_v2 t){

		Map<String, String> match = new HashMap<>(); 
		
		addAllNodesToUnmappedList();
		t.addAllNodesToUnmappedList();
		
		//no need to match the roots (assumption both are bedroom scenes)
//		this.unmapped_list.remove((Object)root.node_index);
//		t.unmapped_list.remove((Object)t.root.node_index);
//		compareNodesv2(this, t, match);
		
		
		this.curr_map.put(0, 0);
		compareNodesTopDown_v2(this, t, match);
		
//		compareNodesBottomUp(this, t, match);
		
		for (Integer str : final_map.keySet()) {
			System.out.println(str + " --> " + final_map.get(str));
		}
		
		return null;
	}
	
	/**
	 * Add a node to the mapped list
	 * @param i - Index of the node in this tree 
	 * @param j - Index of the node to the compared tree
	 */
	private void addToMappedList(int i, int j){
		this.unmapped_list.remove((Object)i);
		this.mapped_map.put(i, j);
	}
	
	/**
	 * Removes a node from mapped list and placed it to the unmapped list
	 * @param i
	 * @param posn
	 */
	private void removeFromMappedList(int i, int posn){
		this.mapped_map.remove(i);
		this.unmapped_list.add(posn, i);
	}
	
	/**
	 * Compute the score of matching
	 * @return
	 */
	private String computeMatchScore(){
		
		int m, r, u;
		m=r=u=0;
		
		for (int i : mapped_map.keySet()) {
			if(i==mapped_map.get(i))	m++;
			else if(mapped_map.get(i)==-1)	u++;
			else	r++;
		}
		
		
		return "m="+ m + ", r=" + r + ", u=" + u;
	}
	
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @param match_map
	 */
	private void compareNodesTopDown_v2(Tree_v2 t1, Tree_v2 t2, Map<String, String> match_map){
		
		while(!curr_map.isEmpty()){
			
			Map<Integer, Integer> next_curr_map = new HashMap<>();
			for (Integer i : curr_map.keySet()) {
				
				TreeNode v1 = t1.node_map.get(i);
				TreeNode v2 = t2.node_map.get(curr_map.get(i));
				
				//base
				if(v1.children.size()>0 && v2.children.size()>0)
				{
					double diff1 = v1.children.get(0).node_val-v2.children.get(0).node_val; 
					diff1*=diff1;
					double diff2 = v1.children.get(1).node_val-v2.children.get(1).node_val; 
					diff2*=diff2;
					
					double diff3 = v1.children.get(1).node_val-v2.children.get(0).node_val; 
					diff3*=diff3;
					double diff4 = v1.children.get(0).node_val-v2.children.get(1).node_val; 
					diff4*=diff4;
					
					if((diff1+diff2) > (diff3+diff4)){
						next_curr_map.put(v1.children.get(1).node_index, v2.children.get(0).node_index);
						next_curr_map.put(v1.children.get(0).node_index, v2.children.get(1).node_index);
					}
					else{
						next_curr_map.put(v1.children.get(1).node_index, v2.children.get(1).node_index);
						next_curr_map.put(v1.children.get(0).node_index, v2.children.get(0).node_index);
					}
				}
				final_map.put(i, curr_map.get(i));
			}
			curr_map.clear();
			for (Integer i : next_curr_map.keySet())	curr_map.put(i, next_curr_map.get(i));
		}
		
	}
	
	
	@Override
	public String toString() {
		printTree(root);
		return "";
	}
	
	
	private void printTree(TreeNode tn) {
		if (tn == null)		return;
		
		System.out.print(tn.node_val + "(" + tn.node_depth + ", " + tn.is_leaf + ")   ");

		if (tn.children.size() == 0)	return;
		
		for (TreeNode c : tn.children) {
			printTree(c);
		}

	}
	
	
}
