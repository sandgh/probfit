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


class TreeNode{
	
	int node_index;
	String node_label;
	int node_val;
	boolean is_leaf = false;
	int node_depth;
	List<TreeNode> children = new ArrayList<>();
	int parent;
	
}

public class Tree {

	TreeNode root = null;
	
	//format <node_id, node> - mapped node_index->node
	Map<Integer, TreeNode> node_map = new HashMap<>();
	
	//these two lists contains the mapped and unmapped nodes while matching 2 trees
	List<Integer> unmapped_list = new ArrayList<>();
	Map<Integer, Integer> mapped_map = new HashMap<>();
	
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
				tn.node_depth = p.node_depth+1;
				p.children.add(tn);
			}
			else{
				tn.node_depth=0;
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
	public Map<String, Integer> compareTree(Tree t){

		Map<String, String> match = new HashMap<>(); 
		
		addAllNodesToUnmappedList();
		t.addAllNodesToUnmappedList();
		
		//no need to match the roots (assumption both are bedroom scenes)
//		this.unmapped_list.remove((Object)root.node_index);
//		t.unmapped_list.remove((Object)t.root.node_index);
//		compareNodesv2(this, t, match);
		
		
//		this.unmapped_list.remove((Object)root.node_index);
//		t.unmapped_list.remove((Object)t.root.node_index);		
//		mapped_map.put(root.node_index, t.root.node_index);
//		t.mapped_map.put(t.root.node_index, this.root.node_index);
//		compareNodesTopDown(this, t, match);
		
		compareNodesBottomUp(this, t, match);
		
		for (String str : match.keySet()) {
			System.out.println(str + " --> " + match.get(str));
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
	private void compareNodesv2(Tree t1, Tree t2, Map<String, String> match_map){
		
		//base condition
		/** @todo - if one of the tree is completely mapped 
		 *  then the remaining nodes of the other will remain unmapped
		**/
		if(t1.unmapped_list.isEmpty() && t2.unmapped_list.isEmpty()){
			
			StringBuffer sb = new StringBuffer();
			
			for (int i : t1.mapped_map.keySet()) {
				sb.append(i+"->"+t1.mapped_map.get(i) + ", ");
			}
			match_map.put(sb.toString(), computeMatchScore());
		}
		
		
		for (int i=0;i<t1.unmapped_list.size(); i++) {
			for (int j=0;j<t2.unmapped_list.size(); j++) {
				
				int v1 = t1.unmapped_list.get(i);
				int v2 = t2.unmapped_list.get(j);
				boolean no_match = false;
				
				//matched only nodes that are in the same depth
				if((t1.node_map.get(v1).node_depth == t2.node_map.get(v2).node_depth) ){
					
					//test cond - if leaves are too far skip matching
					if(t1.node_map.get(v1).is_leaf && t2.node_map.get(v2).is_leaf 
							&& Math.abs(t1.node_map.get(v1).node_val-t2.node_map.get(v2).node_val) > 2)
						no_match = true;
						
					
					t1.addToMappedList(v1, no_match?-1:v2);
					t2.addToMappedList(v2, no_match?-1:v1);
					
					compareNodesv2(t1,t2,match_map);
					t1.removeFromMappedList(v1,i);
					t2.removeFromMappedList(v2,j);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @param match_map
	 */
	private void compareNodesTopDown(Tree t1, Tree t2, Map<String, String> match_map){
		
		//base condition
		/** @todo - if one of the tree is completely mapped 
		 *  then the remaining nodes of the other will remain unmapped
		**/
		if(t1.unmapped_list.isEmpty() && t2.unmapped_list.isEmpty()){
			
			StringBuffer sb = new StringBuffer();
			
			for (int i : t1.mapped_map.keySet()) {
				sb.append(i+"->"+t1.mapped_map.get(i) + ", ");
			}
			match_map.put(sb.toString(), computeMatchScore());
		}
		
		
		for (int i=0;i<t1.unmapped_list.size(); i++) {
			for (int j=0;j<t2.unmapped_list.size(); j++) {
				
				int v1 = t1.unmapped_list.get(i);
				int v2 = t2.unmapped_list.get(j);
				boolean no_match = false;
				
				//matched only nodes that are children of the mapped parents
				if( mapped_map.containsKey(t1.node_map.get(v1).parent)
						&& t2.mapped_map.containsKey(t2.node_map.get(v2).parent)) 
				{
						
					if(!(mapped_map.get(t1.node_map.get(v1).parent) == t2.node_map.get(v2).parent))
						no_match = true;

					t1.addToMappedList(v1, no_match?-1:v2);
					t2.addToMappedList(v2, no_match?-1:v1);
					
					compareNodesTopDown(t1,t2,match_map);
					
					t1.removeFromMappedList(v1,i);
					t2.removeFromMappedList(v2,j);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @param match_map
	 */
	private void compareNodesBottomUp(Tree t1, Tree t2, Map<String, String> match_map){
		
		//base condition
		/** @todo - if one of the tree is completely mapped 
		 *  then the remaining nodes of the other will remain unmapped
		**/
		if(t1.unmapped_list.isEmpty() && t2.unmapped_list.isEmpty()){
			
			StringBuffer sb = new StringBuffer();
			
			for (int i : t1.mapped_map.keySet()) {
				sb.append(i+"->"+t1.mapped_map.get(i) + ", ");
			}
			match_map.put(sb.toString(), computeMatchScore());
		}
		
		
		for (int i=0;i<t1.unmapped_list.size(); i++) {
			
			int v1 = t1.unmapped_list.get(i);
			boolean is_chld_mapped1 = false;
			
			//if no children mapped yet, continue
			for (TreeNode chld1 : t1.node_map.get(v1).children) {
				if(mapped_map.containsKey(chld1.node_index)){
					is_chld_mapped1 = true;
					break;
				}
			}
			
			if(!is_chld_mapped1 && !t1.node_map.get(v1).is_leaf)		continue;	
			
			
			for (int j=0;j<t2.unmapped_list.size(); j++) {
				
				
				int v2 = t2.unmapped_list.get(j);
				boolean is_chld_mapped2 = false;
				
				//if no children mapped yet, continue
				for (TreeNode chld1 : t1.node_map.get(v1).children) {
					if(mapped_map.containsKey(chld1.node_index)){
						is_chld_mapped2 = true;
						break;
					}
				}
				
				if(!is_chld_mapped2 && !t2.node_map.get(v2).is_leaf)		continue;
				
				
				boolean is_chld_match = false;
				
				if(t1.node_map.get(v1).is_leaf && t2.node_map.get(v2).is_leaf)
//						&& Math.abs(t1.node_map.get(v1).node_val-t2.node_map.get(v2).node_val) > 2)
				{
					t1.addToMappedList(v1, v2);
					t2.addToMappedList(v2, v1);
					
					compareNodesBottomUp(t1,t2,match_map);
					
					t1.removeFromMappedList(v1,i);
					t2.removeFromMappedList(v2,j);
				}
				
				//matched only nodes that are in the same depth
				else 
				{
						
					for (TreeNode chld1 : t1.node_map.get(v1).children) {
						for (TreeNode chld2 : t2.node_map.get(v2).children) {
							if(mapped_map.containsKey(chld1.node_index) 
									&& mapped_map.get(chld1.node_index) == chld2.node_index)
							{
								is_chld_match = true;
								break;
							}
						}
						if(is_chld_match)	break;
					}
					
					t1.addToMappedList(v1, is_chld_match?v2:-1);
					t2.addToMappedList(v2, is_chld_match?v2:-1);
					
					compareNodesBottomUp(t1,t2,match_map);
					
					t1.removeFromMappedList(v1,i);
					t2.removeFromMappedList(v2,j);
				}
			}
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
